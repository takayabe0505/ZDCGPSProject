package StayPointAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Tools.GyrationCalculator;
import jp.ac.ut.csis.pflow.geom.LonLat;

public class StayPointVisitCheck {

	/*
	 * For each ID, 
	 * For each staypoint log in disaster day, 
	 * the probability density function of the staypoint will be calculated. 
	 * then, the likelihood of each disaster log will be given back 
	 * given the likelihood, we will determine the "irregularity" of the behavior on disaster day. 
	 * 
	 */

	public static void main(String args[]) throws ParseException, NumberFormatException, IOException{
//		File in = new File ("c:/users/yabetaka/desktop/Exp0529/FrequentStaypoints.csv");
		File out = new File ("c:/users/yabetaka/desktop/Exp0529/StayPoints/StayPointVisitRate.csv");

//		int count = 0;
//		double average = 0;
		for(int i=1; i<=20; i++){
//			String day = String.format("%02d",i);
//			File disin = new File ("c:/users/yabetaka/desktop/Exp0529/DatabyDays/DataforExp_loc_cleaned_01_"+day+".csv");
//			OverlapCheck(in,disin,out,day);
//			count++;
//			System.out.println("done date : " + count);
			double zerorate = zerorate(out,i);
			System.out.println(i + "," + zerorate);
//			System.out.println(i + "," + getAverage(out,i));

		}

	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static File VisitRateCheck(File NorSP, File DisSP, File out, String day) throws NumberFormatException, ParseException, IOException{

		HashMap<Integer, ArrayList<LonLat>> NormalMap = sortintoMap12(NorSP,",");
		System.out.println("done sorting normal : " + NormalMap.size());
		HashMap<Integer, ArrayList<LonLat>> DisasterMap = sortintoMap23(DisSP,",");
		System.out.println("done sorting disaster : " + DisasterMap.size());

		BufferedWriter bw = new BufferedWriter(new FileWriter(out,true));

//		int counter = 0;

		for(Integer id : NormalMap.keySet()){ // loop for ID
			int nearpoints = 0;
			for(LonLat p1 : NormalMap.get(id)){ //loop for Disaster Point
				if(DisasterMap.get(id)!=null){
					if(checker(p1,DisasterMap.get(id))==true){
						nearpoints++;
					}
				}
			}

			//TODO change what to do/see for each ID
			double rateofVisits =  (double)nearpoints/(double)NormalMap.get(id).size();
			bw.write(id + "," + day + "," + NormalMap.get(id).size() + "," + nearpoints+ "," + rateofVisits);
			bw.newLine();
//			counter++;
			//			if(counter%100==0){System.out.println(counter);}
		}
		bw.close();
		return out;
	}

	public static boolean checker(LonLat p1, ArrayList<LonLat> list){
		int counter = 0;
		for(LonLat p2 : list){ // loop for normal point
			double dis = GyrationCalculator.distancekm(p2, p1);
			if(dis <= 1){
				counter++;
			}
		}

		if(counter==0){
			return false;
		}
		else{
			return true;
		}
	}
	//for both Normal & Disaster cases
	public static HashMap<Integer, ArrayList<LonLat>> sortintoMap23(File in, String divider) throws ParseException, NumberFormatException, IOException{
		HashMap<Integer, ArrayList<LonLat>> map = new HashMap<Integer, ArrayList<LonLat>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
			if(map.containsKey(id)){
				ArrayList<LonLat> set = map.get(id);
				set.add(point);
			}
			else{
				ArrayList<LonLat> set = new ArrayList<LonLat>();
				set.add(point);
				map.put(id, set);
			}

		}
		br.close();	
		return map;
	}

	public static HashMap<Integer, ArrayList<LonLat>> sortintoMap12(File in, String divider) throws ParseException, NumberFormatException, IOException{
		HashMap<Integer, ArrayList<LonLat>> map = new HashMap<Integer, ArrayList<LonLat>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			if(map.containsKey(id)){
				ArrayList<LonLat> set = map.get(id);
				set.add(point);
			}
			else{
				ArrayList<LonLat> set = new ArrayList<LonLat>();
				set.add(point);
				map.put(id, set);
			}

		}
		br.close();	
		return map;
	}

	public static HashSet<LonLat> removeOverlap(HashSet<LonLat> hashSet){
		if(hashSet==null){
			return null;
		}
		else{
			LonLat prev = null;
			HashSet<LonLat> set = new HashSet<LonLat>();
			for (LonLat p:hashSet){
				if((prev==null)||(prev!=p)){
					LonLat point = new LonLat(p.getLon(),p.getLat());
					set.add(point);
					prev = point;
				}
			}
			return set;
		}
	}

	public static double getAverage(File in, int day) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
//		double sum = 0;
//		int counter = 0;
		int allSPs = 0;
		int wentSPs = 0;
		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer date = Integer.parseInt(tokens[1]);
			if(day==date){
				allSPs += Integer.parseInt(tokens[2]);
				wentSPs += Integer.parseInt(tokens[3]);
				//sum += Double.parseDouble(tokens[4]);
//				counter++;
			}
		}
		br.close();
		double average = (double)wentSPs/(double)allSPs;
		return average;
	}

	public static double zerorate(File in, int day) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		int counter = 0;
		int zeros = 0;
		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer date = Integer.parseInt(tokens[1]);
			if(day==date){
//				int SPonthatday = Integer.parseInt(tokens[3]);
				double rate = Double.parseDouble(tokens[4]);
				if(rate == 1){
					zeros++;
				}
				counter++;
			}
		}
		br.close();
		return (double)zeros/(double)counter;
	}

}
