package WorkingAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jp.ac.ut.csis.pflow.geom.LonLat;
import GPSAnalyzer.KitakuTravelTimeGetter;

public class ShukkinIDAnalyser {

	public static void main(String args[]) throws IOException{
		String mode = "TyphoonGPS";
		File id_offices = new File("c:/users/yabetaka/desktop/"+mode+"/id_Office.csv");	
		HashMap<Integer,ArrayList<Integer>> map = GetTsuukinID(10,22,id_offices,mode); //day,shukkinids
		
		//出勤していないIDを抽出
		ArrayList<Integer> TargetDayIDs = map.get(21);
		getNonWorkingIDs(TargetDayIDs,id_offices);
		
		//出勤率計算
		//CalculateShukkinRatio(map, id_offices);
	}

	public static void CalculateShukkinRatio(HashMap<Integer,ArrayList<Integer>> in, File id_office) throws IOException{
		HashMap<Integer,LonLat> id_offices = KitakuTravelTimeGetter.getOfficeMap(id_office);
		for(Integer day : in.keySet()){
			double ratio = (double)in.get(day).size()/(double)id_offices.size();
			System.out.println(day + "," + ratio);
		}
	}

	public static HashMap<Integer,ArrayList<Integer>> GetTsuukinID(int startday, int endday, File id_office,String mode) throws IOException{
		HashMap<Integer,LonLat> id_offices = KitakuTravelTimeGetter.getOfficeMap(id_office);

		// return map with IDs who went to work
		HashMap<Integer,ArrayList<Integer>> res = new HashMap<Integer,ArrayList<Integer>>();

		//for each day, put all logs into map
		for(int i=startday;i<=endday;i++){
//			if(!((i==10)||(i==11)||(i==17)||(i==18)||(i==19))){
				String day = String.format("%02d", i);
				File logs = new File("c:/users/yabetaka/desktop/"+mode+"/DatabyDays/DataforExp_"+day+".csv");
				ArrayList<Integer> id_list = TsuukinDecider(logs,id_offices);
				res.put(i, id_list);
//			}
		}
		return res;
	}

	public static ArrayList<Integer> TsuukinDecider(File in, HashMap<Integer,LonLat> id_offices) throws IOException{
		ArrayList<Integer> list = new ArrayList<Integer>();
		HashSet<Integer> temp = new HashSet<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.parseInt(tokens[0]);
			if(id_offices.containsKey(id)){
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				LonLat point = new LonLat(lon,lat);
				if(point.distance(id_offices.get(id))<1000){
					temp.add(id);
				}
			}
		}
		for(Integer id : temp){
			list.add(id);
		}
		br.close();
		return list;
	}

	public static ArrayList<Integer> getNonWorkingIDs(ArrayList<Integer> list, File id_office) throws IOException{
		ArrayList<Integer> res = new ArrayList<Integer>();
		HashMap<Integer,LonLat> id_offices = KitakuTravelTimeGetter.getOfficeMap(id_office);
		for(Integer id: id_offices.keySet()){
			if(!list.contains(id)){
				res.add(id);
				System.out.println(id);
			}
		}
		return res;
	}
	
}
