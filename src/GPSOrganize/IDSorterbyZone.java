package GPSOrganize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;

public class IDSorterbyZone {

	static File tokyo = new File("C:/Users/yabetaka/Desktop/Tokyo_shpFiles/tokyo");
	static GeometryChecker gtokyo = new GeometryChecker(tokyo);
	static File shinjuku = new File("C:/Users/yabetaka/Desktop/Tokyo_shpFiles/shinjuku");
	static GeometryChecker gshinjuku = new GeometryChecker(shinjuku);
	static File ikebukuro = new File("C:/Users/yabetaka/Desktop/Tokyo_shpFiles/ikebukuro");
	static GeometryChecker gikebukuro = new GeometryChecker(ikebukuro);
	static File shinagawa = new File("C:/Users/yabetaka/Desktop/Tokyo_shpFiles/shinagawa");
	static GeometryChecker gshinagawa = new GeometryChecker(shinagawa);
	static File shibuya = new File("C:/Users/yabetaka/Desktop/Tokyo_shpFiles/shibuya");
	static GeometryChecker gshibuya = new GeometryChecker(shibuya);

	public static void main (String args[]) throws IOException{
		File id_office = new File ("c:/users/yabetaka/desktop/TyphoonGPS/id_Office.csv");
		
		ExtractIDbyZone(id_office,gtokyo,"tokyo");
		ExtractIDbyZone(id_office,gshinjuku,"shinjuku");
		ExtractIDbyZone(id_office,gikebukuro,"ikebukuro");
		ExtractIDbyZone(id_office,gshinagawa,"shinagawa");
		ExtractIDbyZone(id_office,gshibuya, "shibuya");
		
	}

	public static File ExtractIDbyZone(File allidoffices, GeometryChecker gc, String place) throws IOException{
		File out = new File ("c:/users/yabetaka/desktop/TyphoonGPS/id_office_"+place+".csv");
		BufferedReader br = new BufferedReader(new FileReader(allidoffices));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		int count = 0;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Double lon = Double.parseDouble(tokens[1]);
			Double lat = Double.parseDouble(tokens[2]);
			List<String> zonecodeList = gc.listOverlaps("zonecode",lon,lat);
			if( zonecodeList == null || zonecodeList.isEmpty() ) 
			{continue;}
			else{
				bw.write(line);
				bw.newLine();
				count++;
			}
		}
		bw.close();
		br.close();
		System.out.println(place +"'s ids : " + count);
		return out;
	}

}
