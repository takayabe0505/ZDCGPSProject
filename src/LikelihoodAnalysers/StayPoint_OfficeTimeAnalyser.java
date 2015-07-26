package LikelihoodAnalysers;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import Tools.GyrationCalculator;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.STPoint;

public class StayPoint_OfficeTimeAnalyser {

	public static void main (String args[]) throws NumberFormatException, IOException, ParseException{

		for(int i=10; i<=22; i++){
			String day = String.format("%02d", i);
			File in = new File ("c:/users/yabetaka/desktop/TyphoonGPS/StayPoints/FrequentStaypointswithTime.csv");
			File offices = new File ("c:/users/yabetaka/desktop/TyphoonGPS/StayPoints/id_OfficeSchool.csv");
			File disLogs = new File ("c:/users/yabetaka/desktop/TyphoonGPS/DatabyDays/DataforExp_"+day+".csv");

			HashMap<Integer,ArrayList<Integer>> id_staytime = intoResMap(in,offices,day); // get staytimes of office data
			HashMap<Integer,ArrayList<Date>> id_DisData = sortDisTime(disLogs,offices);
			HashMap<Integer,Integer> id_DisStayTime = getDisStayTime(id_DisData);

			HashMap<Integer,Double> results = new HashMap<Integer, Double>();

			File out = new File("c:/users/yabetaka/desktop/TyphoonGPS/likelihood/StayTime/StayTimelikelihood_"+day+".csv");

			int count2 = 0;

			for(int id : id_staytime.keySet()){
				int count = 0;
				if(id_DisStayTime.containsKey(id)){
					int dissec = id_DisStayTime.get(id);
					double sum = 0d;
					double average = 0d;
					for(int time : id_staytime.get(id)){
						double likelihood = getLikelihood(time,dissec,3600);
						sum = sum + likelihood;
						count++;
					}
					average = sum/(double)count;
					Double logave = Math.log10(average);
					if(logave.isNaN()||logave.isInfinite()){
						continue;
					}
					else{
						results.put(id, logave);
						count2++;
					}
				}
				count++;
			}
			writeout(results,out);
			System.out.println(day+ ","+count2);
		}
	}

	public static File writeout(HashMap<Integer,Double> results, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for(int id:results.keySet()){
			Double likelihood = results.get(id);
			if(likelihood.isNaN()){
				likelihood = 0d;
			}
			bw.write(id + "," + likelihood);
			bw.newLine();
		}
		bw.close();
		return out;
	}

	public static HashMap<Integer,ArrayList<Integer>> intoResMap(File in, File offices,String day) throws IOException, NumberFormatException, ParseException{
		HashMap<Integer,ArrayList<Integer>> res = new HashMap<Integer,ArrayList<Integer>>();

		HashMap<Integer,LonLat> officemap = getOfficeMap(offices);
		HashMap<Integer, ArrayList<STPoint>> datamap = sortintoMap(in,day);
		
		for(Integer id : datamap.keySet()){
			ArrayList<Date> officetimes = getOfficeTimes(datamap.get(id),officemap,id); //get timestamps of Office logs
			ArrayList<Integer> time = getOfficeStayTime(officetimes);
			if(time==null){continue;}
			else{
				res.put(id, time);
			}
		}
		//		System.out.println("#Got last log of office data of  "+count +" IDs");
		return res;
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

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format
	protected static final SimpleDateFormat SDF_MDS = new SimpleDateFormat("HH:mm:ss");//change time format

	public static HashMap<Integer, ArrayList<STPoint>> sortintoMap(File in, String day) throws NumberFormatException, IOException, ParseException{ // sort all data (exclude disaster day)
		HashMap<Integer, ArrayList<STPoint>> id_count = new HashMap<Integer, ArrayList<STPoint>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		Date startdate = SDF_TS.parse("2011-09-"+day+" 00:00:01");
		Date finishdate = SDF_TS.parse("2011-09-"+day+" 23:59:59");

		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			STPoint point = new STPoint(SDF_TS.parse(tokens[4]),Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			ArrayList<STPoint> list = id_count.containsKey(id) ? id_count.get(id):new ArrayList<STPoint>();
			if(point.getTimeStamp().after(finishdate)||point.getTimeStamp().before(startdate)){
				list.add(point);
				id_count.put(id, list);
			}
		}
		br.close();	
		return id_count;
	}

	public static ArrayList<Date> getOfficeTimes(ArrayList<STPoint> STPset, HashMap<Integer,LonLat> officemap, int id){
		ArrayList<Date> officetimes = new ArrayList<Date>();
		LonLat office = officemap.get(id);
		if(office == null){
			return null;
		}
		else{
			if(STPset.isEmpty()==false){
				for(STPoint p : STPset){
					if(p.getLon()==office.getLon()&&p.getLat()==office.getLat()){
						officetimes.add(p.getTimeStamp());
					}
					else{
						continue;
					}
				}
				return officetimes;
			}
			else{
				return null;
			}
		}
	}

	public static ArrayList<Integer> getOfficeStayTime(ArrayList<Date> alllogs) throws ParseException{
		ArrayList<Integer> staytimes = new ArrayList<Integer>();
		if(alllogs==null){
			return null;
		}
		else{
			for(int i=1;i<=20;i++){
				ArrayList<Date> temp = new ArrayList<Date>();
				String day = String.format("%02d",i);
				Date startdate = SDF_TS.parse("2011-09-"+day+" 00:00:01");
				Date finishdate = SDF_TS.parse("2011-09-"+day+" 23:59:59");
				String enter = null;
				String exit = null;
				int staytime = 0;
				for(Date d:alllogs){
					if(d.after(startdate)&&d.before(finishdate)){
						String dtemp = SDF_MDS.format(d);
						Date hms = SDF_MDS.parse(dtemp);
						temp.add(hms);
					}
				}
				Collections.sort(temp);
				//			System.out.println(temp);
				if(temp.isEmpty()){
					continue;
				}
				else{
					enter= STPoint.FORMAT_HMS.format(temp.get(0));
				}
				Collections.reverse(temp);
				if(temp.isEmpty()){
					continue;
				}
				else{
					exit= STPoint.FORMAT_HMS.format(temp.get(0));
				}
				int entersec = converttoSecs(enter);
				int exitsec = converttoSecs(exit);
				staytime = exitsec - entersec;
				staytimes.add(staytime);
			}
			return staytimes;
		}
	}
	
	public static int converttoSecs(String time){
		String[] tokens = time.split(":");
		int hour = Integer.parseInt(tokens[0]);
		int min  = Integer.parseInt(tokens[1]);
		int sec  = Integer.parseInt(tokens[2]);

		int totalsec = hour*3600+min*60+sec;
		return totalsec;
	}

	public static HashMap<Integer,ArrayList<Date>> sortDisTime(File dislogs, File offices) throws IOException, ParseException{
		HashMap<Integer,LonLat> id_office = getOfficeMap(offices);
		HashMap<Integer, ArrayList<Date>> temp = new HashMap<Integer, ArrayList<Date>>();

		HashSet<Integer> fortest = new HashSet<Integer>();

		BufferedReader br = new BufferedReader(new FileReader(dislogs));
		String line = null;
		while((line=br.readLine())!=null){
			String tokens[] = line.split(",");
			Integer id = Integer.parseInt(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
			if(id_office.containsKey(id)){
				if(GyrationCalculator.distancekm(point, id_office.get(id))<1){
					fortest.add(id);
					if(temp.get(id)==null){
						ArrayList<Date> dates = new ArrayList<Date>();
						dates.add(SDF_TS.parse(tokens[1]));
						temp.put(id,dates);
					}
					else{
						temp.get(id).add(SDF_TS.parse(tokens[1]));
					}
				}
			}
		}
		//		System.out.println(fortest.size());
		br.close();
		return temp;
	}

	public static HashMap<Integer,Integer> getDisStayTime(HashMap<Integer,ArrayList<Date>> map) throws ParseException{
		HashMap<Integer,Integer> res = new HashMap<Integer,Integer>();
		for(Integer id : map.keySet()){
			int entry = 0;
			int exit = 0;
			int staytime = 0;
			ArrayList<Date> alllogs = map.get(id);
			if(alllogs==null){
				return null;
			}
			else{
				ArrayList<Date> temp = new ArrayList<Date>();
				for(Date d:alllogs){
					String dtemp = SDF_MDS.format(d);
					Date hms = SDF_MDS.parse(dtemp);
					temp.add(hms);
				}
				Collections.sort(temp);
				//			System.out.println(temp);
				if(temp.isEmpty()){
					continue;
				}
				else{
					entry= converttoSecs(STPoint.FORMAT_HMS.format(temp.get(0)));
				}
				Collections.reverse(temp);
				if(temp.isEmpty()){
					continue;
				}
				else{
					exit= converttoSecs(STPoint.FORMAT_HMS.format(temp.get(0)));
				}
				staytime = exit - entry;
				res.put(id, staytime);
			}
		}
		return res;
	}

	public static double getLikelihood(int sec1, int sec2, double sigma){
		Double likelihood = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp( - ((sec1 - sec2) * (sec1 - sec2)) / (2 * sigma * sigma));
		if(likelihood.isInfinite()||(likelihood>=1)){
			return likelihood = 0d;
		}
		return likelihood;
	}

}
