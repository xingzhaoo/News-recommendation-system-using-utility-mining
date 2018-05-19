import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * This class is to aggregate the time spend for consecutive records (for Omni2014)
 * with same user and news id  
 * @author Xing Zhao
 *
 */
public class aggreTimeEventST {

	public static void main(String[] args) {
		String input = "aggSubscribedUser.txt";
		String output = "aggTimeSubscribedUser.txt";
		
		try {
			aggregatorTE(input, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.print("Time Aggregation Completed!");
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
			/* aggregate time when userid and newsid are same
			 * this and next sptime are not null
			 * this and next events are null
			 * */
			if (thisSplit[0].equals(nextSplit[0]) && thisSplit[1].equals(nextSplit[1]) 
					&& !thisSplit[2].trim().isEmpty() && !nextSplit[2].trim().isEmpty()
					&& thisSplit[3].trim().isEmpty() && nextSplit[3].trim().isEmpty()){
				int thisTime = Integer.parseInt(thisSplit[2].trim());
				int nextTime = Integer.parseInt(nextSplit[2].trim());
				int sum = thisTime + nextTime;
				buffer.append(thisSplit[0]+"|"+thisSplit[1]+"|"+ sum);
				buffer.append("|" + thisSplit[3] + "|" + thisSplit[4]);
				thisLine = new String();
				num++;
			}else{
				buffer.append(thisSplit[0]+"|"+thisSplit[1]+"|"+ thisSplit[2].trim());
				buffer.append("|" + thisSplit[3] + "|" + thisSplit[4]);
				thisLine = nextLine;
			}
			
			writer.write(buffer.toString());
			writer.newLine();
		}
		
		newsReader.close();
		if(!thisLine.isEmpty()){
			String thisSplit[] = thisLine.split("\\|");
			buffer = new StringBuilder();
			buffer.append(thisSplit[0]+"|"+thisSplit[1]+"|"+ thisSplit[2].trim());
			buffer.append("|" + thisSplit[3] + "|" + thisSplit[4]);
			writer.write(buffer.toString());
			writer.newLine();
		}
		writer.close();
		System.out.println("Time Summation aggregate happens: "+ num);
	}
}
