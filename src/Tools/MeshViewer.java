package Tools;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.Mesh;

public class MeshViewer {

	public static void main (String args[]) throws IOException{
		
		File in = new File ("c:/users/yabetaka/desktop/SnowGPS/Exp0617/kitakutraveltimesCOEFF-distance-home.csv");
		/* this file contains "id","kitakutime in disaster","kitakutime normal","2/3",
		 * "officelon","officelat","homeofficeDistance"     
		 */
		
		HashMap<String, ArrayList<Double>> mc_coeff = SortintoMap(in);
		HashMap<String,Double> mc_coeffave = getAverage(mc_coeff);
		
		File out = new File ("c:/users/yabetaka/desktop/SnowGPS/kitakutraveltimesCOEFF-MESH.csv");
		writeoutwithGeom(mc_coeffave,out);
	}

	public static HashMap<String, ArrayList<Double>> SortintoMap(File in) throws IOException{
		HashMap<String, ArrayList<Double>> res = new HashMap<String, ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Double coeff = Double.parseDouble(tokens[3]);
			Double lon = Double.parseDouble(tokens[4]);
			Double lat = Double.parseDouble(tokens[5]);
			Mesh mesh = new Mesh(3,lon,lat);
			String meshcode = mesh.getCode();
			if(res.containsKey(meshcode)){
				res.get(meshcode).add(coeff);
			}
			else{
				ArrayList<Double> list = new ArrayList<Double>();
				list.add(coeff);
				res.put(meshcode,list);
			}
		}
		br.close();
		return res;
	}
	
	public static HashMap<String,Double> getAverage(HashMap<String,ArrayList<Double>> map){
		HashMap<String,Double> res = new HashMap<String,Double>();
		for(String meshcode : map.keySet()){
			Double ave = ave(map.get(meshcode));
			res.put(meshcode, ave);
		}
		return res;
	}
	
	public static Double ave(ArrayList<Double> list){
		Double sum = 0d;
		Double res = 0d;
		for(Double d:list){
			sum = sum + d;
		}
		res = sum/(double)list.size();
		return res;
	}
	
	public static File writeoutwithGeom(HashMap<String,Double> map, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for(String mc : map.keySet()){
			Mesh mesh = new Mesh(mc);
			Rectangle2D.Double rect = mesh.getRect();
			String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
					rect.getMinX(),rect.getMaxY(),
					rect.getMaxX(),rect.getMaxY(),
					rect.getMaxX(),rect.getMinY(),
					rect.getMinX(),rect.getMinY());
			bw.write(mc + "\t" + map.get(mc) + "\t" + wkt);
			bw.newLine();
		}
		bw.close();
		return out;
	}
}
