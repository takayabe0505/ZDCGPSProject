package Tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class HomeOfficeDistance {

	public static void main(String args[]) throws IOException{

		File offices = new File ("c:/users/yabetaka/desktop/SnowGPS/id_Office.csv");
		File homes = new File("c:/users/yabetaka/desktop/SnowGPS/id_home.csv");


		getDistance(homes, offices);

	}

	public static HashMap<Integer,Double> getDistance(File homes, File offices) throws IOException{
		HashMap<Integer,LonLat> id_offices = GetMap.getOfficeMap(offices);
		HashMap<Integer,LonLat> id_home = GetMap.getHomeMap(homes);
		
		HashMap<Integer,Double> res =  new HashMap<Integer,Double>();
		for(Integer id : id_home.keySet()){
			double dis = 0d;
			if(id_offices.containsKey(id)){
				dis = distancekm(id_home.get(id),id_offices.get(id));
			}
			res.put(id, dis);
		}
		return res;
	}

	public static Double distancekm(LonLat p2, LonLat b){
		Double tempx = p2.getLon()-b.getLon();
		Double tempy = p2.getLat()-b.getLat();
		Double x = tempx*90.1639;
		Double y = tempy*111;
		Double dis = Math.pow(Math.pow(x,2)+Math.pow(y,2), 0.5);
		return dis;
	}

}
