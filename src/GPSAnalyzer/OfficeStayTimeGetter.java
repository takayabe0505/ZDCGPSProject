package GPSAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Tools.HomeOfficeDistance;
import jp.ac.ut.csis.pflow.geom.LonLat;

public class OfficeStayTimeGetter {

	public static void main (String args[]) throws NumberFormatException, IOException, ParseException{

		//String mode = "SnowGPS";
		String mode = "TyphoonGPS";
		//TODO change int i (days of exp)

		File homes = new File("c:/users/yabetaka/desktop/"+mode+"/id_home.csv");
		File offices = new File ("c:/users/yabetaka/desktop/"+mode+"/id_Office.csv");
		String filepath = "c:/users/yabetaka/desktop/"+mode+"/DatabyDays/DataforExp_";
		HashMap<Integer, HashMap<Integer, Integer>> map = createMap(offices,filepath); //this is the MAIN Map for Kitaku times

		HashMap<Integer,Double> HODistance = HomeOfficeDistance.getDistance(homes, offices);

		File out = new File ("c:/users/yabetaka/desktop/"+mode+"/officestaytimes.csv");
		//writeoutDistance(map,21,out,HODistance);
		writeout(map,21,out);
		//writeoutALL(map,out);
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format
	protected static final SimpleDateFormat SDF_MDS = new SimpleDateFormat("HH:mm:ss");//change time format

	public static HashMap<Integer, HashMap<Integer, Integer>> createMap(File offices,String path) throws IOException, NumberFormatException, ParseException{
		HashMap<Integer, HashMap<Integer, Integer>> result = new HashMap<Integer, HashMap<Integer, Integer>>();

		HashMap<Integer,LonLat> id_offices = getOfficeMap(offices);
		//		System.out.println("id_offices : "+id_offices.size());

		for(int i=10; i<=22; i++){
			String day = String.format("%02d", i);
			File TargetDayLogs = new File (path+day+".csv");

			HashMap<Integer,ArrayList<Integer>> id_logs = getLogsnearOffice(TargetDayLogs,id_offices,day); //get id_logsnearOffice from TargetDayLogs
			//			System.out.println("id_logs : "+id_logs.size());

			HashMap<Integer, Integer> id_firstlogofday = getFirstLogofDay(id_logs); //loop for all ids, and get last log of day
			//			System.out.println("id_lastlog : "+id_lastlogofday.size());
			HashMap<Integer, Integer> id_lastlogofday = getLastLogofDay(id_logs); //loop for all ids, and get last log of day

			for(Integer id: id_firstlogofday.keySet()){
				if(id_lastlogofday.containsKey(id)){
					int staytime = id_lastlogofday.get(id)-id_firstlogofday.get(id);
					//				System.out.println(staytime);
					if(result.containsKey(id)){
						result.get(id).put(Integer.parseInt(day), staytime);
					}
					else{
						HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
						map.put(Integer.parseInt(day), staytime);
						result.put(id, map);
					}
				}
			}
		}
		System.out.println("result size : " + result.size());
		return result;
	}

	public static HashMap<Integer,LonLat> getOfficeMap(File offices) throws IOException{
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(offices));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			int id = Integer.parseInt(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}

	public static HashMap<Integer,ArrayList<Integer>> getLogsnearOffice(File in, HashMap<Integer,LonLat> id_offices, String day) throws IOException, NumberFormatException, ParseException{
		HashMap<Integer,ArrayList<Integer>> res = new HashMap<Integer,ArrayList<Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.parseInt(tokens[0]);
			if(id_offices.containsKey(id)){
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				LonLat point = new LonLat(lon,lat);
				String date = tokens[1];
				String[] youso = date.split(" ");
				String ymd = youso[0];
				String[] youso2 = ymd.split("-");
				String hiniti = youso2[2];

				if(point.distance(id_offices.get(id))<1000){
					Integer time = converttoSecs(SDF_MDS.format(SDF_TS.parse(tokens[1])));
					if(!(hiniti.equals(day))){
						time = time + 86400;
					}

					if(res.containsKey(id)){
						res.get(id).add(time);
					}
					else{
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(time);
						res.put(id, list);
					}
				}
			}
		}		
		br.close();
		return res;
	}

	public static HashMap<Integer, Integer> getFirstLogofDay(HashMap<Integer,ArrayList<Integer>> alllogs) throws ParseException{
		HashMap<Integer, Integer> id_lastlog = new HashMap<Integer, Integer>();
		for(Integer id : alllogs.keySet()){
			ArrayList<Integer> list = alllogs.get(id);
			Collections.sort(list);
			if(list.size()>=3){
				id_lastlog.put(id,list.get(0));
			}
		}
		return id_lastlog;
	}

	public static HashMap<Integer, Integer> getLastLogofDay(HashMap<Integer,ArrayList<Integer>> alllogs) throws ParseException{
		HashMap<Integer, Integer> id_lastlog = new HashMap<Integer, Integer>();
		for(Integer id : alllogs.keySet()){
			ArrayList<Integer> list = alllogs.get(id);
			Collections.sort(list);
			Collections.reverse(list);
			if(list.size()>=3){
				id_lastlog.put(id,list.get(0));
			}
		}
		return id_lastlog;
	}

	public static int converttoSecs(String time){
		String[] tokens = time.split(":");
		int hour = Integer.parseInt(tokens[0]);
		int min  = Integer.parseInt(tokens[1]);
		int sec  = Integer.parseInt(tokens[2]);

		int totalsec = hour*3600+min*60+sec;		
		return totalsec;
	}

	public static File writeout(HashMap<Integer, HashMap<Integer, Integer>> map, int day, File out) throws IOException{
		int lines = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for (Integer id:map.keySet()){
			//			if(map.get(id).size()>=5){
			for (Integer d : map.get(id).keySet()){
				if(d==day){
					bw.write(String.valueOf(map.get(id).get(d)) + "," + 1);
					bw.newLine();
				}
				//				else if(((d==13)||(d==12)||(d==5)||(d==6)||(d==19)||(d==20))){
				//					bw.write(String.valueOf(map.get(id).get(d)) + "," + 2);
				//					bw.newLine();
				//				}
				else{
					bw.write(String.valueOf(map.get(id).get(d)) + "," + 0);
					bw.newLine();
				}
			}
			lines++;
		}
		//		}
		bw.close();
		System.out.println("number of lines : " + lines);
		return out;
	}

	public static File writeoutALL(HashMap<Integer, HashMap<Integer, Integer>> map, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		System.out.println("number of IDs: " + map.size());
		for(Integer id : map.keySet()){
			int count = 0;
			for(Integer d : map.get(id).keySet()){
				if((map.get(id).get(d)>85800)&&(map.get(id).get(d)<90000)){
					count++;
				}
			}
			if(count>=5){
				bw.write(String.valueOf(id)); bw.newLine();
			}
		}
		bw.close();
		return out;
	}

	public static File writeoutavg(HashMap<Integer, HashMap<Integer, Integer>> map, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		System.out.println("number of IDs: " + map.size());
		for(Integer id : map.keySet()){
			ArrayList<Integer> list = new ArrayList<Integer>();
			for(Integer d : map.get(id).keySet()){
				if(!((d==13)||(d==12)||(d==5)||(d==6)||(d==19)||(d==20)||(d==14))){
					list.add(map.get(id).get(d));
				}
			}
			if(!list.isEmpty()){
				bw.write(id + "," + getavg(list));	bw.newLine();
			}
		}
		bw.close();
		return out;
	}

	public static File writeoutDistance(HashMap<Integer, HashMap<Integer, Integer>> map, int day, File out, HashMap<Integer,Double> id_dis) throws IOException{
		int lines = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		int count = 0;
		for (Integer id:map.keySet()){
			//			if(map.get(id).size()>=5){
			for (Integer d : map.get(id).keySet()){
				if(d==day){
					bw.write(map.get(id).get(d) + "," + 1 + "," + id_dis.get(id));
					bw.newLine();
				}
				else if(((d==13)||(d==12)||(d==5)||(d==6)||(d==19)||(d==20))){
					bw.write(map.get(id).get(d) + "," + 2);
					bw.newLine();
				}
				else{
					count++;
					if(count%6==0){
						bw.write(map.get(id).get(d) + "," + 0 + "," + id_dis.get(id));
						bw.newLine();
					}
				}
			}
			lines++;
		}
		//		}
		bw.close();
		System.out.println("number of lines : " + lines);
		return out;
	}

	public static double getavg(ArrayList<Integer> list){
		double sum = 0;
		for(Integer t : list){
			sum = t + sum;
		}
		double avg = sum/(int)list.size();
		return avg;
	}

}