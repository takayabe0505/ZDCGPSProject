package Tools;

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
import java.util.HashMap;
import java.util.HashSet;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.STPoint;
import GPSOrganize.IDExtractor;

public class GyrationCalculator {

	public static void main(String args[]) throws ParseException, NumberFormatException, IOException{
		
		String mode = "TyphoonGPS";
		File ids = new File("c:/users/yabetaka/desktop/"+mode+"/NonWorkingIDSet.csv");

		File in = new File ("c:/users/yabetaka/desktop/"+mode+"/DataforExp.csv");
		File dis = new File ("c:/users/yabetaka/desktop/"+mode+"/DatabyDays/DataforExp_21.csv");

		File out = new File ("c:/users/yabetaka/desktop/GyrationResults.csv");

		HashMap<Integer, ArrayList<STPoint>> mapNormal = sortintoMap(in,",");
		System.out.println("done getting normal Map");

		HashMap<Integer, ArrayList<STPoint>> mapDisaster = sortintoMap(dis,",");
		System.out.println("done getting disaster Map");

		HashSet<Integer> set = IDExtractor.getIDSet(ids,",");
		System.out.println("done getting IDs");

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(out,true));
			int counter = 0;
			int hikikomori = 0;
			for(int id:set){
				double NorGyr = getGyration(mapNormal,id,10); //何点以上ある人を計算対象とするか？
				double DisGyr = getGyration(mapDisaster,id,5);
				//				if((DisGyr > 1)&&(NorGyr > 1)){
				bw.write(id+","+ NorGyr +","+DisGyr);
				bw.newLine();
				if(counter % 1000 == 0){
					System.out.println("#"+counter);
				}
				counter++;
				if(DisGyr<1000){
					hikikomori++;
				}
				//				}
			}
			bw.close();
			System.out.println(counter + "," + set.size());
			System.out.println("hikki : " + hikikomori);
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		} 
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static HashMap<Integer, ArrayList<STPoint>> sortintoMap(File in, String divider) throws ParseException, NumberFormatException, IOException{
		HashMap<Integer, ArrayList<STPoint>> id_count = new HashMap<Integer, ArrayList<STPoint>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;

		while ((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			STPoint point = new STPoint(SDF_TS.parse(tokens[1]),Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
			if(id_count.containsKey(id)){
				id_count.get(id).add(point);
			}
			else{
				ArrayList<STPoint> list = new ArrayList<STPoint>();
				list.add(point);
				id_count.put(id, list);
			}
		}
		br.close();	

		return id_count;
	}

	public static double getGyration(HashMap<Integer, ArrayList<STPoint>> id_count, int ID, int minimumpoints){
		Double radius = 0d;
		ArrayList<STPoint> STPlist = id_count.get(ID);
		if(STPlist==null){
			radius = 0d;
		}
		else if (STPlist.size()>=minimumpoints){
			radius = gyration(STPlist)*1000; //in meters!!
		}
		else{
			radius = 0d;
		}
		return radius;
	}

	public static Double gyration(ArrayList<STPoint> list){
		Double radius = 0d;
		Double tempsum = 0d;
		LonLat ave = average(list);
		for(STPoint p:list){
			tempsum = tempsum + Math.pow(distancekm(p,ave), 2);
		}
		radius = Math.pow(tempsum/list.size(), 0.5);
		return radius;
	}

	public static LonLat average(ArrayList<STPoint> list){
		Double lonave = 0d;
		Double latave = 0d;
		Double lonsum = 0d;
		Double latsum = 0d;
		for(STPoint p:list){
			lonsum = lonsum + p.getLon();
			latsum = latsum + p.getLat();
		}
		lonave = lonsum/list.size();
		latave = latsum/list.size();
		LonLat avepoint = new LonLat(lonave,latave);
		return avepoint;
	}

	public static Double distancekm(LonLat p2, LonLat b){
		Double tempx = p2.getLon()-b.getLon();
		Double tempy = p2.getLat()-b.getLat();
		Double x = tempx*90.1639;
		Double y = tempy*111;
		Double dis = Math.pow(Math.pow(x,2)+Math.pow(y,2), 0.5);
		return dis;
	}

	public static Double distanceLonLat(LonLat a, LonLat b){
		Double tempx = a.getLon()-b.getLon();
		Double tempy = a.getLat()-b.getLat();
		Double x = tempx*90.1639;
		Double y = tempy*111;
		Double dis = Math.pow(Math.pow(x,2)+Math.pow(y,2), 0.5);
		return dis;
	}



}
