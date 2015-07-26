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
import java.util.Date;

public class SortLogsbyDays {

	public static void main(String args[]) throws IOException, ParseException{
		File in = new File("c:/users/yabetaka/desktop/SnowGPS/DataforExp.csv");

		for(int i=1 ; i<=20; i++){
			String day = String.format("%02d", i);
			String next = String.format("%02d", i+1);
			System.out.println(day + "," + next);

			File out = new File("c:/users/yabetaka/desktop/SnowGPS/DatabyDays/DataforExp_"+day+".csv");

			Date startdate = SDF_TS.parse("2013-01-"+day+" 04:00:00");
			Date finishdate = SDF_TS.parse("2013-01-"+next+" 04:00:00");
//			System.out.println(startdate);
//			System.out.println(finishdate);
			
			DayExtractor(in, out, startdate, finishdate);
//			if(i==1){break;}
		}
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format	
//	protected static final SimpleDateFormat SDF_HMS = new SimpleDateFormat("HH:mm:ss");//change time format

//	public static File sortbyTime(File in, File out) throws IOException, ParseException{
//		BufferedReader br = new BufferedReader(new FileReader(in));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
//		String line = null;
//		int count = 0;
//		while((line=br.readLine())!= null){
//			String[] tokens = line.split(",");
//			Date date = SDF_TS.parse(tokens[1]);
//			String hms = SDF_HMS.format(date);
//			int time = converttoSecs(hms);
//			bw.write(String.valueOf(time));
//			bw.newLine();
//			count++;
//			if(count%10000000==0){
//				System.out.println("#count : " + count +","+ time);
//			}
//		}
//		br.close();
//		bw.close();
//		return out;
//	}

	public static int converttoSecs(String time){
		String[] tokens = time.split(":");
		int hour = Integer.parseInt(tokens[0]);
		int min  = Integer.parseInt(tokens[1]);
		int sec  = Integer.parseInt(tokens[2]);

		int totalsec = hour*3600+min*60+sec;
		return totalsec;
	}

	public static File disasterTagger(File in, Date startdate, Date finishdate, String divider) throws ParseException{
		File out = new File ("c:/users/yabetaka/desktop/GPSfromYutsan/IndividualData/"+getNameofFile(in) + "_Tagged.csv");
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while((line=br.readLine()) != null){
				String[] tokens = line.split(divider);
				Date date = SDF_TS.parse(tokens[0]);
				if(date.before(startdate)||date.after(finishdate)){
					bw.write(line + ",0");
					bw.newLine();		
				}
				else{
					bw.write(line + ",1");
					bw.newLine();
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
		}
		return out;
	}

	public static String getNameofFile(File in){
		int lastdotpos = in.getName().lastIndexOf(".");
		return (in.getName().substring(0,lastdotpos));
	}

	public static File DayExtractor(File in, File out, Date start, Date finish) throws ParseException{
		try{
			int counter = 0;
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while((line=br.readLine()) != null){
				String[] tokens = line.split(",");
				Date date = SDF_TS.parse(tokens[1]);
				if(date.before(finish)&&date.after(start)){
					bw.write(line);
					bw.newLine();	
				}
				counter++;
				if(counter%1000000==0){System.out.println(counter);}
			}
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
