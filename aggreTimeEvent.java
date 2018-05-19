import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * This class is to aggregate the time spend and the events for consecutive records (for Omni2014)
 * with same user and news id due to the events and time spend are logged as separated records
 *  
 * @author Xing Zhao
 *
 */
public class aggreTimeEvent {

	public static void main(String[] args) {
		String input = "pre_aggSubscribedUser.txt";
		String output = "aggSubscribedUser.txt";
		
		try {
			aggregatorTE(input, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.print("Aggregation Completed!");
	}
	
	public static void aggregatorTE(String input, String output) throws IOException{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		StringBuilder buffer;
		int num = 0;
		//scan the input file to load it into memory
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		String thisLine, nextLine;
		thisLine = new String();
		// for each line (transactions) until the end of the file
		while (((nextLine = newsReader.readLine()) != null)) { 
			
			if (nextLine.isEmpty() == true || nextLine.charAt(0) == '#' || nextLine.contains("+") || nextLine.contains("post_prop9")) {
				continue;
			}
			
			if(thisLine.isEmpty()){
				thisLine = nextLine;
				continue;
			}
			
			buffer = new StringBuilder();
			// split the transaction according to the | separator
			String thisSplit[] = thisLine.split("\\|");
			String nextSplit[] = nextLine.split("\\|");
			/* aggregate when userid and newsid are same as well as 
			 * this sptime and next events are not null 
			 * this events and next sptime is null can be added
			 * */
			if (thisSplit[0].equals(nextSplit[0]) && thisSplit[1].equals(nextSplit[1]) 
					&& !thisSplit[2].trim().isEmpty() && !nextSplit[3].trim().isEmpty()
					&& thisSplit[3].trim().isEmpty() && nextSplit[2].trim().isEmpty()){
				buffer.append(thisSplit[0]+"|"+thisSplit[1]+"|"+ thisSplit[2]);
				buffer.append("|" + nextSplit[3] + "|" + thisSplit[4]);
				//System.out.println("#"+ thisSplit[3].trim().isEmpty()+"#");
				//System.out.println(thisLine + "\n" + nextLine + "\n");
				thisLine = new String();
				num++;
				
			}else{
				buffer.append(thisSplit[0]+"|"+thisSplit[1]+"|"+ thisSplit[2]);
				buffer.append("|" + thisSplit[3] + "|" + thisSplit[4]);
				thisLine = nextLine;
			}
			
			writer.write(buffer.toString());
			writer.newLine();
		}
		
		newsReader.close();
		if(!thisLine.isEmpty()){
			writer.write(thisLine);
			writer.newLine();
		}
		writer.close();
		System.out.println("aggregate happens: "+ num);
	}
}
