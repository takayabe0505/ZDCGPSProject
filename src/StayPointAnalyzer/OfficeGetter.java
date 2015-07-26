package StayPointAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
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
import Tools.GetMap;

public class OfficeGetter {

	public static void main(String args[]) throws IOException, NumberFormatException, ParseException{

		//String mode = "TyphoonGPS";
		String mode = "SnowGPS";
		File result = new File ("c:/users/yabetaka/desktop/"+mode+"/id_office.csv");

		long startTime = System.currentTimeMillis();
		File in = new File("c:/users/yabetaka/desktop/"+mode+"/dataforexp.csv");

		HashMap<Integer,HashMap<LonLat,ArrayList<STPoint>>> SPmap = StayPointGetter.getSPs(in,"10:00:00", "18:00:00", 15);
		long Time1 = System.currentTimeMillis();
		System.out.println("#done calculating StayPoints; " + (Time1-startTime)/1000+"secs");

		File id_home = new File ("c:/users/yabetaka/desktop/"+mode+"/id_home.csv");
		HashMap<Integer,LonLat> idhome = GetMap.getHomeMap(id_home);

		HashMap<Integer,HashMap<LonLat,Integer>> id_SP_visitcount = ExcludeLowFrequentSPs(SPmap,idhome,7,0.7);

		HashMap<Integer,LonLat> resmap = getOfficePoints(id_SP_visitcount,idhome);
		long Time4 = System.currentTimeMillis();
		System.out.println("#got result map!; " + (Time4-startTime)/1000+"secs");

		writeOut(resmap, result);
		long Time5 = System.currentTimeMillis();
		System.out.println("#completed...:) " + (Time5-startTime)/1000+"secs");
	}

	public static HashMap<Integer,HashMap<LonLat,Integer>> ExcludeLowFrequentSPs(HashMap<Integer,HashMap<LonLat,ArrayList<STPoint>>> map, HashMap<Integer,LonLat> idhome, int totalweekdays, double minrate){
		HashMap<Integer,HashMap<LonLat,Integer>> res = new HashMap<Integer,HashMap<LonLat,Integer>>();
		for(Integer id : map.keySet()){
			HashMap<LonLat,Integer> tempmap = new HashMap<LonLat,Integer>();
			for(LonLat sp : map.get(id).keySet()){
				HashSet<String> temp = new HashSet<String>();
				for(STPoint stp : map.get(id).get(sp)){
					String date = (new SimpleDateFormat("yyyy-MM-dd")).format(stp.getTimeStamp());
					String[] youso = date.split("-");
					Integer d = Integer.valueOf(youso[2]);
					if(!((d==10)||(d==11)||(d==17)||(d==18)||(d==19)||(d==21))){
						temp.add(youso[2]);
					}
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

	public static HashMap<Integer,LonLat> getOfficePoints(HashMap<Integer,HashMap<LonLat,Integer>> map, HashMap<Integer,LonLat> idhome){
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		for(Integer id:map.keySet()){
			if(map.get(id).size()>0){
				if(idhome.containsKey(id)){
					LonLat office = getOffice(map.get(id),idhome,id);
					res.put(id, office);
				}
			}
		}
		return res;
	}

	public static LonLat getOffice(HashMap<LonLat,Integer> map, HashMap<Integer,LonLat> idhome, Integer id){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(LonLat p:map.keySet()){
			list.add(map.get(p));
		}
		Collections.sort(list);
		Collections.reverse(list);
		int count = list.get(0);
		LonLat point = null;
		LonLat home = idhome.get(id);
		if(home!=null){
			for(LonLat p :map.keySet()){
				if(map.get(p)==count){
					point = p;
					if((point.distance(home)<1000)){
						if(list.size()>=2){
							count = list.get(1);
							for(LonLat p2 :map.keySet()){
								if(map.get(p2)==count){
									point = p2;
									if((point.distance(home)<1000)){
										return null;
									}
								}
							}
						}
						else{
							return null;
						}
					}
				}
			}
		}
		return point;
	}

	public static File writeOut(HashMap<Integer,LonLat> map, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for(Integer id:map.keySet()){
			if(map.get(id)!=null){
				bw.write(id + "," + map.get(id).getLon() + "," + map.get(id).getLat());
				bw.newLine();
			}
		}
		bw.close();
		return out;
	}

}
