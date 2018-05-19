import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class filter out the records with newsID from aggregated data grouped by users 
 * which are not in L5 newsID
 * @author Xing Zhao
 *
 */
public class newsFilterL5_Omn {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input ="aggTimeSubscribedUser.txt";
		String output ="aggTimeSubscribedUser_L5.txt";
		
		String newsPublishFile ="newsPublishDate.txt";
		
		try {
			ArrayList<String> newsDB = loadMap(newsPublishFile);
			newsFilterL5(input, output, newsDB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Filter out the record with news ID which is not in newsDB 
	 * @param input
	 * @param output
	 * @param newsDB , array with news ID from L5
	 * @throws IOException
	 */
	public static void newsFilterL5(String input, String output, ArrayList<String> newsDB) throws IOException{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		// scan the input file to load it into memory
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		String thisLine;
		StringBuilder buffer = new StringBuilder();
		// for each line (transactions) until the end of the file
		while (((thisLine = newsReader.readLine()) != null)) {
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
				|| thisLine.contains("post_prop9")
				|| thisLine.contains("+")) {
		    	continue;
			}
			
			String split[] = thisLine.split("\\|");
			if(newsDB.contains(split[1].trim())){
				buffer.append(thisLine + "\n");
			}	
		}
		
		writer.write(buffer.toString());
		newsReader.close();
		writer.close();
		
	}
	
	/**
	 * load the newsID from L5
	 * @param newsMap , L5 news ID file
	 * @return array of news ID in L5
	 * @throws IOException
	 */
	public static ArrayList<String> loadMap(String newsMap) throws IOException{
		ArrayList<String> newsDBarray = new ArrayList<String>();
		
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
				String split[] = entryLine.split("#");
				//count++;System.out.println(count + " " + split[0] + " " + split[1]);
				newsDBarray.add(split[0]);				
			}
			
		}catch (Exception e) {
        	// catches exception if error while reading the input file
        	e.printStackTrace();
        }finally {
        	if(myMappingDB != null){
        		myMappingDB.close();
        	}
        }
		return newsDBarray;
	}
}
