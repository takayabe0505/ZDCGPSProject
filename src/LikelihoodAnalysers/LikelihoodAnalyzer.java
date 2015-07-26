package LikelihoodAnalysers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LikelihoodAnalyzer {

	public static void main(String args[]) throws IOException{
		File out = new File ("c:/users/yabetaka/desktop/Exp0529/likelihood/likelihood_bunsan.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));

		for(int i = 1; i<=20; i++){
			String day = String.format("%02d", i);
			File in = new File ("c:/users/yabetaka/desktop/Exp0529/likelihood/likelihood_"+day+".csv");
			Double average = getAverage(in);
			Double bunsan = getBunsan(in,average);
			bw.write(day + "," + average + "," + bunsan);
			bw.newLine();
		}
		bw.close();
	}

	public static double getAverage(File in) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		Double sum = 0d;
		int count = 0;
		while ((line = br.readLine()) != null){
			String[] tokens = line.split(",");
			Double likelihood = Double.parseDouble(tokens[1]);
			sum = sum + likelihood;
			count++;
		}
		br.close();
		Double average = sum/(double)count;
		return average;
	}

	public static Double getBunsan(File in, double average) throws IOException{
		BufferedReader br2 = new BufferedReader(new FileReader(in));
		Double tempsum = 0d;
		Double temp = 0d;
		String line2 = null;
		int count = 0;
		while ((line2 = br2.readLine()) != null){
			String[] tokens = line2.split(",");
			Double like = Double.parseDouble(tokens[1]);
			temp = Math.pow((like-average), 2);
			tempsum = tempsum + temp;
			count++;
		}
		Double res = tempsum/count;
		br2.close();
		return res;
	}
}
