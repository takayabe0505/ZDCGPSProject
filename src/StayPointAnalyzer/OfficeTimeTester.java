package StayPointAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class OfficeTimeTester {

	public static void main (String args[]) throws NumberFormatException, IOException, ParseException{
		File offices = new File ("c:/users/yabetaka/desktop/TyphoonGPS/StayPoints/id_Office.csv");
		File alldata = new File ("c:/users/yabetaka/desktop/TyphoonGPS/DataforExp.csv");
		HashMap<Integer,LonLat> id_offices = getOfficeMap(offices);
		
		File out = new File ("c:/users/yabetaka/desktop/TyphoonGPS/office_times_all.csv");

		getLogsnearOffice(alldata,id_offices,out);
		
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format
	protected static final SimpleDateFormat SDF_MDS = new SimpleDateFormat("HH:mm:ss");//change time format

	public static HashMap<Integer,LonLat> getOfficeMap(File offices) throws IOException{
		HashMap<Integer,LonLat> res = new HashMap<Integer,LonLat>();
		BufferedReader br = new BufferedReader(new FileReader(offices));
		String line = null;
		while ((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			int id = Integer.parseInt(tokens[0]);
			LonLat point = new LonLat(Double.parseDouble(tokens[1]),Double.parseDouble(tokens[2]));
			res.put(id, point);
		}
		br.close();
		return res;
	}

	public static File getLogsnearOffice(File in, HashMap<Integer,LonLat> id_offices, File out) throws IOException, NumberFormatException, ParseException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		int count = 0;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			Integer id = Integer.parseInt(tokens[0]);
			if(id_offices.containsKey(id)){
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				LonLat point = new LonLat(lon,lat);

				if(point.distance(id_offices.get(id))<1000){
					Integer time = converttoSecs(SDF_MDS.format(SDF_TS.parse(tokens[1])));
					bw.write(id + "," + time);
					bw.newLine();
				}
			}
			count++;
			if(count%100000==0){System.out.println(count);}
		}		
		br.close();
		bw.close();
		return out;
	}

	public static int converttoSecs(String time){
		String[] tokens = time.split(":");
		int hour = Integer.parseInt(tokens[0]);
		int min  = Integer.parseInt(tokens[1]);
		int sec  = Integer.parseInt(tokens[2]);

		int totalsec = hour*3600+min*60+sec;		
		return totalsec;
	}

}