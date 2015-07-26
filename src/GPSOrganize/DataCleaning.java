package GPSOrganize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;

public class DataCleaning {

	public static void main (String args[]){
		File in = new File ("c:/users/yabetaka/desktop/TyphoonGPS/dataforExp_uncleaned.csv"); //FUNDAMENTAL File
		File out = new File ("c:/users/yabetaka/desktop/TyphoonGPS/dataforExp.csv"); //File we get
	
		excludePointsbyLoc(in,out);
	}

	static File shapedir = new File("C:/Users/yabetaka/Documents/SekimotoLab/DocumentsIMade/FundamentalData/pt08tky.zoneshape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static boolean areaDetector(double lon, double lat){
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",lon,lat);
		if( zonecodeList == null || zonecodeList.isEmpty() ){
			return false;
		}
		else{
			return true;
		}
	}
	
	public static File excludePointsbyLoc(File in, File out){
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			int count = 0;
			int allcount = 0;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				if(areaDetector(lon,lat)==true){
					bw.write(line);
					bw.newLine();
					count++;
				}
				allcount++;
				if(allcount%100000==0){
					System.out.println("all count: "+allcount);
				}
			}
			System.out.println(count + " out of " + allcount);
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		} 
		return out;
	}
	
	public static File excludeFewPointUsers(File in, File out){ //TODO under construction...
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			int count = 0;
			int allcount = 0;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
//				String id = tokens[0];
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				if(areaDetector(lon,lat)==true){
					bw.write(line);
					bw.newLine();
					count++;
				}
				allcount++;
				if(allcount%1000==0){
					System.out.println(allcount);
				}
			}
			System.out.println(count + " out of " + allcount);
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		} 
		return out;
	}
	
}
