import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * This class calculate the external utility for each news item
 * beased on the user access date and news publish date
 * @author Xing Zhao
 *
 */
public class preprocessTransExternal {

	public static void main(String[] args) throws Exception {
		
		String input ="trans_subscribedUsers.txt";
		String output ="trans_subscribedUsers_ext.txt";
		String newsList ="newsPublishDate.txt";
		HashMap<String, String> newsMap = loadMap(newsList);
		getExternalUtil(input, output, newsMap);
		
	}
	
	/**
	 * Calculate the external utility for each news item and 
	 * append/replace the external utility set to the end of each transaction 
	 *  
	 * @param input
	 * @param output
	 * @param newsDB , newsID to publish date
	 * @throws Exception
	 */
	public static void getExternalUtil(String input, String output, HashMap<String, String> newsDB) throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		// scan the input file to load it into memory
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String thisLine;
		StringBuilder buffer = new StringBuilder();
		// for each line (transactions) until the end of the file
		while ((thisLine = newsReader.readLine()) != null) {
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#') {
				continue;	
			}
			
			// split the transaction according to the : separator
			String split[] = thisLine.split(":");
			
			String newsIds [] = split[2].split(" ");
			long[] externalUtil = new long[newsIds.length];
			
			for(int i = 0; i < externalUtil.length; i++){
				if(newsDB.containsKey(newsIds[i])){
					Date publishDay = df.parse(newsDB.get(newsIds[i]));
					Date userAccessDay = df.parse(split[5].trim());
					externalUtil[i] = daysBetween(userAccessDay, publishDay);
				}else{
					newsReader.close();
					writer.close();
					throw new Exception(newsIds[i] + "\tNot in newsDB, verify newsFilterL5_Omn.java");
				}
			}
			// reassemble the data, replace date with external utility array
			for(int j = 0; j < split.length - 1; j++){
				buffer.append(split[j] + ":");
			}
			
			for(int j = 0; j < externalUtil.length; j++){
				buffer.append(externalUtil[j]);
				if(j < externalUtil.length - 1){
					buffer.append(" ");
				}
			}
			
			buffer.append("\n"); // for large memory, otherwise write out
			
		}
		
		newsReader.close();
		
		writer.write(buffer.toString());
		
		writer.close();
		
	}
	

	
	/**
	 * load the newsID and publushed date from L5
	 * @param newsMap , L5 news ID file
	 * @return array of news ID in L5
	 * @throws IOException
	 */
	public static HashMap<String, String> loadMap(String newsMap) throws IOException{
		HashMap<String, String> newsDB = new HashMap<String, String>();
		
		BufferedReader myMappingDB = null;
				
		String entryLine;
		
		try{
			myMappingDB = new BufferedReader(new InputStreamReader(new FileInputStream(new File(newsMap))));
			// for each line (mapping entry) until the end of the file
			while ((entryLine = myMappingDB.readLine()) != null) {
				// if the line is empty
				if (entryLine.isEmpty() == true || entryLine.charAt(0) == '#') {
					continue;
				}
				//split the transaction according to the " # " separator
				String split[] = entryLine.trim().split("#");
				//count++;System.out.println(count + " " + split[0] + " " + split[1]);
				newsDB.put(split[0], split[1]);				
			}
			
		}catch (Exception e) {
        	// catches exception if error while reading the input file
        	e.printStackTrace();
        }finally {
        	if(myMappingDB != null){
        		myMappingDB.close();
        	}
        }
		return newsDB;
	}
	
	/**
	 * Calculate the days between two dates
	 * 
	 * @param one
	 *            , end date
	 * @param two
	 *            , start date
	 * @return days
	 * 
	 *         source from:
	 *         http://javarevisited.blogspot.ca/2015/07/how-to-find-
	 *         number-of-days-between-two-dates-in-java.html
	 */
	private static long daysBetween(Date one, Date two) {
		long difference = (one.getTime() - two.getTime()) / 86400000;
		return Math.abs(difference);
	}
}
