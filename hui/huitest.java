import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;


/**
 * This class takes the input file with the required format of algorithm HUIminer and 
 * output the patterns mined by HUIminer
 * 
 * @see ca.pfv.spmf.algorithms.frequentpatterns.hui_miner.AlgoHUIMiner.java
 * @author Xing Zhao
 */

public class huitest {

	public static void main(String [] arg) throws IOException{
		
		String input = "../trans_users_HUI.txt";
		String output = "../trans_HUImined_UARY.txt";
		       
		double min_utility_prestage = 0.000287;
		
		long [] totalUtilTrans = totalUtilityTransNum(input);
		long TU = totalUtilTrans[0];
		long numTrans = totalUtilTrans[1];
		long mean = TU / numTrans;
		System.out.println("Total Utility: " + TU + "\t numTrans: " + numTrans);
		System.out.println("Mean = " + mean);
		int threshold = (int) (TU * min_utility_prestage);
		
		System.out.println("Threshold in seconds: " + threshold);
		// Applying the HUIMiner algorithm		
		AlgoHUIMinerRev1 huiminer = new AlgoHUIMinerRev1();
		huiminer.runAlgorithm(input, output, threshold);
		huiminer.printStats();
	}

	/**
	 * This class scan through all the transactions of the file and sum up the total utility of each transaction
	 * @param inputfile, the file has the required format of the HUIminer
	 * @return the total utility of the transaction of the inputfile
	 * @throws IOException
	 */
	public static long[] totalUtilityTransNum(String inputfile) throws IOException {
		long totalUtil = 0;
		long numTrans = 0;
		BufferedReader myInput = null;
		String entryLine;
		try {
			// prepare the object for reading the file
			myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(inputfile))));
			// for each line (transaction) until the end of file
			while ((entryLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (entryLine.isEmpty() == true) {
					continue;
				}
				// split the transaction according to the : separator
				String split[] = entryLine.trim().split(":"); 
				// the second part is the total utility of the transaction of items
				totalUtil += Long.parseLong(split[1]); 
				numTrans++;
			}
		} catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }	
		return new long[] {totalUtil, numTrans};
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = huitest.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
