package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadCutandWrite {

	public static void main(String args[]) throws IOException{
		File in = new File ("c:/users/yabetaka/desktop/SnowGPS/Exp0617/kitakutraveltimesCOEFF-distance.csv");
		File out = new File ("c:/users/yabetaka/desktop/SnowGPS/kitakutraveltimesCOEFF-distance_cut.csv");
		RCW(in,out);
	}
	
	public static File RCW(File in, File out) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
//			Integer id = Integer.parseInt(tokens[0]);
			Double distime = Double.parseDouble(tokens[1]);
			Double normaltime = Double.parseDouble(tokens[2]);
//			Double ratio = Double.parseDouble(tokens[3]);
//			Double lon = Double.parseDouble(tokens[4]);
//			Double lat = Double.parseDouble(tokens[5]);
//			Double distance = Double.parseDouble(tokens[6]);
			if((distime<20000)&&(normaltime<20000)){
				bw.write(line);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
		return out;
	}
	
}
