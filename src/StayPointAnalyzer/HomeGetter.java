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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.STPoint;

public class HomeGetter {

	public static void main(String args[]) throws IOException, NumberFormatException, ParseException{

				String mode = "SnowGPS";
		//String mode = "TyphoonGPS";

		File in = new File("c:/users/yabetaka/desktop/"+mode+"/DataforExp.csv");

		HashMap<Integer, HashMap<LonLat, ArrayList<STPoint>>> SPmap = StayPointGetter.getSPs(in, "00:00:00", "07:00:00", 15);

		File res = new File ("c:/users/yabetaka/desktop/"+mode+"/id_home.csv");

		HashMap<Integer,HashMap<LonLat,Integer>> id_SP_visitcount = ExcludeLowFrequentSPs(SPmap,10,0.8);

		HashMap<Integer,LonLat> resmap = getHomePoints(id_SP_visitcount);
		System.out.println("#got result map!");

		writeOut(resmap, res);
	}

	public static HashMap<Integer,HashMap<LonLat,Integer>> ExcludeLowFrequentSPs(HashMap<Integer,HashMap<LonLat,ArrayList<STPoint>>> map, int totalweekdays, double minrate){
		HashMap<Integer,HashMap<LonLat,Integer>> res = new HashMap<Integer,HashMap<LonLat,Integer>>();
		for(Integer id : map.keySet()){
			HashMap<LonLat,Integer> tempmap = new HashMap<LonLat,Integer>();
			for(LonLat sp : map.get(id).keySet()){
				HashSet<String> temp = new HashSet<String>();
				for(STPoint stp : map.get(id).get(sp)){
					String date = (new SimpleDateFormat("yyyy-MM-dd")).format(stp.getTimeStamp());
					String[] youso = date.split("-");
					temp.add(youso[2]);
				}
				double rate = (double)temp.size()/(double)totalweekdays;
				if(rate>minrate){
					tempmap.put(sp, map.get(id).get(sp).size());
				}
			}
			res.put(id, tempmap);
		}
		return res;
	}

	public static HashMap<Integer,HashMap<LonLat,Integer>> FrequentStayPointsintoMap(File in) throws IOException{
		HashMap<Integer,HashMap<LonLat,Integer>> res = new HashMap<Integer,HashMap<LonLat,Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			int id = Integer.parseInt(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			int count = Integer.parseInt(tokens[3]);
			if(res.containsKey(id)){
				res.get(id).put(point, count);
			}
			else{
				HashMap<LonLat,Integer> map = new HashMap<LonLat,Integer>();
				map.put(point, count);
				res.put(id, map);
			}
		}
		br.close();
		return res;
	}

	public static HashMap<Integer,LonLat> getHomePoints(HashMap<Integer,HashMap<LonLat,Integer>> map){
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		for(Integer id : map.keySet()){
			if(map.get(id).size()>0){
				HashMap<LonLat,Integer> mapofID = map.get(id);
				LonLat point = getHome(mapofID);
				if(point!=null){
					res.put(id, point);
				}
			}
		}
		return res;
	}

	public static LonLat sortbyOrder(HashMap<LonLat,Integer> map, int rank){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(LonLat p:map.keySet()){
			list.add(map.get(p));
		}
		Collections.sort(list);
		Collections.reverse(list);
		if(list.size()>=rank){
			int count = list.get(rank-1);
			LonLat point = null;
			for(LonLat p :map.keySet()){
				if(map.get(p)==count){
					point = p;
				}
			}
			return point;
		}
		else{
			return null;
		}
	}

	public static LonLat getHome(HashMap<LonLat,Integer> map){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(LonLat p:map.keySet()){
			list.add(map.get(p));
		}
		Collections.sort(list);
		Collections.reverse(list);
		int count = list.get(0);
		LonLat point = null;
		for(LonLat p :map.keySet()){
			if(map.get(p)==count){
				point = p;
			}
		}
		return point;
	}

	public static File writeOut(HashMap<Integer,LonLat> map, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		int count = 0;
		for(Integer id:map.keySet()){
			bw.write(id + "," + map.get(id).getLon() + "," + map.get(id).getLat());
			bw.newLine();
			count++;
		}
		bw.close();
		System.out.println("IDs with Home: " + count);
		return out;
	}

}
