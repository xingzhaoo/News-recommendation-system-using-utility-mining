import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/*transform transactions into HUIminer(modified from SPMF) required format and normalize the dataset*/
public class preprocessTransHUI {

	public static void main(String[] args) throws IOException {
		String input = "trans_subscribedUsers_ext.txt";
		String output = "trans_users_HUI_dub.txt";
		
		int newMinRange = 0;
		int newMaxRange = 100;
		
		int minMax[] = getMinMax(input);
		System.out.println("sptime range: " + minMax[0] + "\t"+ minMax[1]);
		System.out.println("external range: "+ minMax[2] + "\t"+ minMax[3]);
		transToHUI(input, output, minMax, newMinRange, newMaxRange);
		System.out.println("transations in HUI format are ready.");
	}
	
	
	/**
	 * Transform the transactions into HUI required format
	 * newsIDs are kept, news item utility is calculated based on normalized values
	 * including internal utility(sptime and events), external utility(days)
	 * 
	 * @param input
	 * @param output
	 * @param minMaxValue
	 * @param newMin
	 * @param newMax
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void transToHUI(String input, String output, int[] minMaxValue, int newMin, int newMax) throws NumberFormatException, IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		StringBuilder buffer = new StringBuilder();
		String thisLine;
		while (((thisLine = newsReader.readLine()) != null)) {
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#') {
				continue;	
			}
			
			// split the transaction according to the : separator
			String split[] = thisLine.split(":");
			String sptime [] = split[3].split(" ");
			String events [] = split[4].split(" ");
			String external [] = split[5].split(" ");
			
			double utility [] = new double[sptime.length];
			double TU = 0;
			
			for(int i = 0; i < sptime.length; i++){
				int time = Integer.parseInt(sptime[i].trim());
				double ntime = minMaxNormal(time, minMaxValue[0], minMaxValue[1], newMin, newMax);
				int days = Integer.parseInt(external[i].trim());
				
				// add 1 for 0 values
				double ndays = minMaxNormal(days, minMaxValue[2], minMaxValue[3], newMin+1, newMax);
			
				int event = Integer.parseInt(events[i].trim());
				
				utility[i] =  (ntime + event) / ndays;
				TU += utility[i];
			}
					
			buffer.append(split[2] + ":");
			// buffer.append((int) (TU * 100));
			buffer.append(TU + ":");
			
			for(int j = 0; j < utility.length; j++){
				buffer.append(utility[j]);
				if(j < utility.length - 1){
					buffer.append(" ");
				}
			}
			
			buffer.append("\n");
			
		}
		
		newsReader.close();	
		writer.write(buffer.toString());
		writer.close();
		
	}
	
	/**
	 * Min-Max normalization
	 * @param value
	 * @param min_v
	 * @param max_v
	 * @param new_min
	 * @param new_max
	 * @return normalized value
	 */
	public static double minMaxNormal(int value, int min_v, int max_v, int new_min, int new_max){
		return (value - min_v) * (new_max - new_min) / (double) (max_v - min_v) + new_min;
	}
	
	public static int[] getMinMax(String input) throws IOException{
		int[] result = new int[4];
		
		int maxTime = 0;
		int minTime = Integer.MAX_VALUE;
		int maxExternal = 0;
		int minExternal = Integer.MAX_VALUE;
		
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		String thisLine;
		while (((thisLine = newsReader.readLine()) != null)) {
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#') {
				continue;	
			}
			
			// split the transaction according to the : separator
			String split[] = thisLine.split(":");
			String sptime [] = split[3].split(" ");
			String external [] = split[5].split(" ");
			
			for(int i = 0; i < sptime.length; i++){
				int time = Integer.parseInt(sptime[i].trim());
				if(time > maxTime){
					maxTime = time;
				}
				if(time < minTime){
					minTime = time;
				}
				
				int days = Integer.parseInt(external[i].trim());
				if(days > maxExternal){
					maxExternal = days;
				}
				if(days < minExternal){
					minExternal = days;
				}
				
			}
					
		}
		newsReader.close();
		
		result[0] = minTime;
		result[1] = maxTime;
		result[2] = minExternal;
		result[3] = maxExternal;
		
		return result;		
	}
	
	
	/**
	 * Phrase the array of String into array of Integer
	 * @param sptime array of String
	 * @return array of Integer
	 */
	public static int[] getInteger(String [] strValue){
		int[] value = new int[strValue.length];
		for(int i = 0; i < value.length; i++){
			value[i] = Integer.parseInt(strValue[i].trim());
		}
		return value;
	}

}
