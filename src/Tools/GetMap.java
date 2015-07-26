package Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class GetMap {

	public static HashMap<Integer,LonLat> getOfficeMap(File offices) throws IOException{
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(offices));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}
	
	public static HashMap<Integer,LonLat> getHomeMap(File in) throws IOException{
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			Integer id = Integer.valueOf(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}
	
	public static HashMap<String,LonLat> getHomeMap_String(File in) throws IOException{
		HashMap<String,LonLat> res = new HashMap<String,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			String id = tokens[0];
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}
	
	public static HashMap<String,LonLat> getOfficeMap_String(File offices) throws IOException{
		HashMap<String,LonLat> res = new HashMap<String,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(offices));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			String id = tokens[0];
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}
	
}
