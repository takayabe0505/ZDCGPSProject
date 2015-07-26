package GPSOrganize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import jp.ac.ut.csis.pflow.geom.STPoint;

public class IDExtractor { //methods to extract specific IDs from BIG DATA

	public static void main(String args[]) throws ParseException, IOException{
		//		int count = 1;
		//File in = new File ("c:/users/yabetaka/desktop/Exp0529/FrequentStayPoints.csv");
		//getDataofIDwithoutInterval(in,",",65551);
		File in = new File ("c:/users/yabetaka/desktop/TyphoonGPS/Data/allrawdata.csv");
		//File out = new File ("c:/users/yabetaka/desktop/TyphoonGPS/id-count.csv");
		File idfile = new File ("c:/users/yabetaka/desktop/TyphoonGPS/Data/idsforExp.csv");
		File out = new File ("c:/users/yabetaka/desktop/TyphoonGPS/dataforExp.csv");
		//NumberofPoints(in,",", out);
		IDextract(in,idfile,out);
	}

	public static File IDextract(File in, File idFile, File out) throws IOException{ 
//		ArrayList<Integer> ids = new ArrayList<Integer>();
//		BufferedReader br = new BufferedReader(new FileReader(idFile));
//		String line = null;
//		while((line=br.readLine())!=null){
//			String[] tokens = line.split(",");
//			Integer id = Integer.parseInt(tokens[0]);
//			ids.add(id);
//		}
//		br.close();
		
//		System.out.println("#done getting 30000 IDs");

		String line2 = null;
		BufferedReader br2 = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		int count = 0;
		int count2 = 0;
		while((line2=br2.readLine())!=null){
			String[] tokens = line2.split(",");
			Integer id = Integer.parseInt(tokens[0]);
			if(id<45000){
				bw.write(line2);
				bw.newLine();
				count2++;
				if(count2%100000==0){
					System.out.println("#written "+count2);
				}
			}
			count++;
			if(count%1000000==0){
				System.out.println("#in total "+count);
			}
		}
		br2.close();
		bw.close();
		return out;
	}

	public static HashSet<Integer> getIDSet(File in, String divider){ // return the set of all id's
		HashSet<Integer> ids = new HashSet<Integer>();
		//		int count=0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			String line = null;
			while((line=br.readLine()) != null){
				String[] tokens = line.split(divider);
				Integer id = Integer.valueOf(tokens[0]);
				ids.add(id);
				//			count++;
				//if(count%1000000==0){System.out.println(count);}
			}
			br.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return ids;
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static File NumberofPoints(File in, String divider, File out) throws ParseException{ // return the File of id & count of logs

//		Date startdate = SDF_TS.parse("2013-01-14 00:00:01");
//		Date finishdate = SDF_TS.parse("2013-01-14 23:59:00");
//		int counter = 0;
		int counter2= 0;
		HashMap<Integer, Integer> id_count = new HashMap<Integer, Integer>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out,true));
			String line2 = null;
			while((line2=br.readLine()) != null){
				String[] tokens = line2.split(divider);
				Integer id = Integer.valueOf(tokens[0]);
//				Date date = SDF_TS.parse(tokens[1]);
//				if(Tools.modeDeciderbyTime(date, startdate, finishdate)==1){
					if(id_count.containsKey(id)){
						int count = id_count.get(id);
						id_count.put(id, count+1);
					}
					else{
						id_count.put(id, 1);
					}
//					counter++;
//					if(counter%100000==0){
//						System.out.println("dis log: "+ counter);
//					}
//				}
				counter2++;
				if(counter2%100000==0){
					System.out.println("total log: "+ counter2);
				}
			}
			System.out.println("Done going through them!");
			for(int i:id_count.keySet()){
				bw.write(i + "," + id_count.get(i));
				bw.newLine();
			}
			bw.close();
			br.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return out;
	}

	public static File getDataofIDwithInterval(File in, String divider, int ID) throws NumberFormatException, ParseException{
//		Date startdate = SDF_TS.parse("2013-01-14 00:00:01");
//		Date finishdate = SDF_TS.parse("2013-01-14 23:59:00");
		File out = new File ("c:/users/yabetaka/desktop/"+ID+"_points.csv");
		ArrayList<STPoint> list = new ArrayList<STPoint>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line2 = null;
//			int counter = 0;
			Date prevTime = null;
			while((line2=br.readLine()) != null){
				String[] tokens = line2.split(divider);
				Integer id = Integer.valueOf(tokens[0]);
				if(id==ID){
					Date date = SDF_TS.parse(tokens[1]);
					if((prevTime==null)||((prevTime!=null)&&(intervalChecker(prevTime,date)==true))){
						STPoint point = new STPoint(SDF_TS.parse(tokens[1]),Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
						//LonLat point = new LonLat(Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
						list.add(point);
						bw.write(tokens[1] + "," + tokens[2] + "," + tokens[3]);
						bw.newLine();
						//						System.out.println("yep");
						prevTime = date;
					}
				}
				else{
					continue;
				}
//				counter++;
				//				if(counter%1==0){
				//					System.out.println(counter);
				//				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}	
		return out;
	}

	public static File getDataofIDwithoutInterval(File in, String divider, int ID) throws NumberFormatException, ParseException{
		Date startdate = SDF_TS.parse("2013-01-14 00:00:01");
		Date finishdate = SDF_TS.parse("2013-01-14 23:59:00");
		File out = new File ("c:/users/yabetaka/desktop/"+ID+"_points.csv");
		ArrayList<STPoint> list = new ArrayList<STPoint>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line2 = null;
			//			int counter = 0;
			while((line2=br.readLine()) != null){
				String[] tokens = line2.split(divider);
				Integer id = Integer.valueOf(tokens[0]);
				if(id==ID){
					//					Date date = SDF_TS.parse(tokens[1]);
					STPoint point = new STPoint(SDF_TS.parse(tokens[4]),Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
					//LonLat point = new LonLat(Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
					list.add(point);
					bw.write(tokens[1] + "," + tokens[2] + "," + tokens[3]+","+mode_time_Deciders.modeDecider(point, startdate, finishdate));
					bw.newLine();
				}
				else{
					continue;
				}
				//				counter++;
				//				if(counter%1==0){
				//					System.out.println(counter);
				//				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}	
		return out;
	}

	public static ArrayList<STPoint> getDataofIDListwithIntervalfromFile(File in, String divider, int ID) throws NumberFormatException, ParseException{
		//		File out = new File ("c:/users/yabetaka/desktop/"+ID+"_points.csv");
		ArrayList<STPoint> list = new ArrayList<STPoint>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			//			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line2 = null;
			//			int counter = 0;
			Date prevTime = null;
			while((line2=br.readLine()) != null){
				String[] tokens = line2.split(divider);
				Integer id = Integer.valueOf(tokens[0]);
				if(id==ID){
					Date date = SDF_TS.parse(tokens[1]);
					if((prevTime==null)||((prevTime!=null)&&(intervalChecker(prevTime,date)==true))){
						STPoint point = new STPoint(SDF_TS.parse(tokens[1]),Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
						//LonLat point = new LonLat(Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
						list.add(point);
						//						bw.write(tokens[1] + "," + tokens[2] + "," + tokens[3]);
						//						bw.newLine();
						//				System.out.println("yep");
						prevTime = date;
					}
				}
				else{
					continue;
				}
				//				counter++;
				//		if(counter%1==0){
				//			System.out.println(counter);
				//		}
			}
			br.close();
			//			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}	
		return list;
	}

	public static ArrayList<STPoint> getDataofIDListwithIntervalfromList(ArrayList<STPoint> in, String divider, int ID) throws NumberFormatException, ParseException{
		ArrayList<STPoint> list = new ArrayList<STPoint>();
		Date prevTime = null;
		if(in!=null){
			for(STPoint p:in){
				Date date = p.getTimeStamp();
				if((prevTime==null)||((prevTime!=null)&&(intervalChecker(prevTime,date)==true))){
					list.add(p);
					prevTime = date;
				}
			}
		}
		return list;
	}

	public static boolean intervalChecker(Date date1, Date date2){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		cal1.add(Calendar.MINUTE, 10);
		Date newDate = cal1.getTime();
		if(date2.after(newDate)){ //interval is over 10 minutes
			return true;
		}
		else{
			return false;
		}
	}


}
