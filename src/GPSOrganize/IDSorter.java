package GPSOrganize;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.ac.ut.csis.pflow.geom.STPoint;

public class IDSorter {

	public static void main(String args[]) throws NumberFormatException, ParseException, IOException{

		File in = new File ("c:/users/yabetaka/desktop/GPSfromYutsan/alldata.csv");
		File out= new File ("c:/users/yabetaka/desktop/DataofThousandIDs.csv");

//		for(int i=0; i<=11; i++){
			IdSorter(in,out, 100000, 0); //this exp. contains id<2,500,000
//		}
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static File IdSorter(File in, File out, int idBorder, int k){
		int counter = 0;
		int counter2= 0;
		HashMap<Integer, ArrayList<STPoint>> id_count = new HashMap<Integer, ArrayList<STPoint>>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out,true));
			String line = null;
			while ((line=br.readLine())!=null){
				String[] tokens = line.split(",");
				Integer id = Integer.valueOf(tokens[0]);
				int idup = idBorder*(k+1);
				if((id>=idBorder*k)&&(id<idup)){
					STPoint point = new STPoint(SDF_TS.parse(tokens[1]),Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
					ArrayList<STPoint> list = id_count.containsKey(id) ? id_count.get(id):new ArrayList<STPoint>();
					list.add(point);
					id_count.put(id, list);
					counter2++;
					if(counter2%1000==0){
						System.out.println(counter2);
					}
				}
			}
			System.out.println("done sorting out, will start putting them in order...");

			Date startdate = SDF_TS.parse("2013-01-14 00:00:01");
			Date finishdate = SDF_TS.parse("2013-01-14 23:59:00");
			
			for(Map.Entry<Integer, ArrayList<STPoint>> entry : id_count.entrySet()){
				ArrayList<STPoint> STPlist = entry.getValue();
				Collections.sort(STPlist);

				for (STPoint point : STPlist){
					bw.write(entry.getKey() + "," + STPoint.FORMAT_YMDHMS.format(point.getTimeStamp()) + "," + point.getLon() + "," + 
				point.getLat() +","+ mode_time_Deciders.modeDecider(point, startdate, finishdate));
					bw.newLine();
				}
				counter++;
				if(counter%1==0){
					System.out.println(counter);
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("done "+k+"th round!");
		return out;
	}

	//	public static File WriteoutMap(File in, File out) throws NumberFormatException, ParseException, IOException{
	//		try{
	//			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
	//			TreeMap<Integer,STPoint> map = sortbyID(in);
	//			for(Map.Entry<Integer, STPoint> entry : map.entrySet()){
	//				bw.write(entry.getKey() + "," + entry.getValue());
	//				bw.newLine();
	//			}
	//			bw.close();
	//		}
	//		catch(FileNotFoundException xx) {
	//			System.out.println("File not found 5");
	//		}
	//		catch(IOException xxx) {
	//			System.out.println(xxx);
	//		}
	//		return out;
	//	}

	//	public static TreeMap<Integer,STPoint> sortbyID(File in) throws NumberFormatException, ParseException{
	//		Set<Integer, STPoint> list = new Set<Integer, STPoint>();
	//		try{
	//			int counter2 = 0;
	//			BufferedReader br = new BufferedReader(new FileReader(in));
	//			String line = null;
	//			while ((line=br.readLine())!=null){
	//				String[] tokens = line.split(",");
	//				Integer id = Integer.valueOf(tokens[0]);
	//				STPoint point = new STPoint(SDF_TS.parse(tokens[1]),Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
	//				mapT.put(id,point);
	//				counter2++;
	//				if(counter2%1000000==0){
	//					System.out.println(counter2);
	//				}
	//			}
	//			br.close();
	//		}
	//		catch(FileNotFoundException xx) {
	//			System.out.println("File not found 5");
	//		}
	//		catch(IOException xxx) {
	//			System.out.println(xxx);
	//		}
	//		return mapT;
	//	}

}
