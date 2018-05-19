import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;

import dataUnit.Rule;
import dataUnit.Rules;
import utilityAssocRule.AssocRulesHUIminerBK;
import utilityAssocRule.AssocRulesHUIminerES;

/**
 * This class uses AssocRulesHUIminerY to implement the Utility Based Association Rules which have been defined in LURA project
 * @see AssocRulesHUIminerY.java
 * @author Xing Zhao
 *
 */
public class AssocTest {

	public static void main(String[] args) throws IOException {
		
			
			    String input = "../evaluation/trans_HUImined_UARY.txt";//"../trans_HUImined_UARY.txt";
			    String output = "../trans_assoc_rule_HUImined_cf60_es.txt";

			    /*load map database for id translate to headline*/
				//String mapdb = ".//postprocessDB.txt";
				//String textput = ".//evalsrt_training_5dev2_trans_Assoc_HUImined_headlines.txt";
	
				//String input = fileToPath("output.txt");
								
				double min_utility_conf = 0.30;
				//AssocRulesHUIminerES algoShane = new AssocRulesHUIminerES();
				AssocRulesHUIminerBK algoShane = new AssocRulesHUIminerBK();
				Rules pattern =	algoShane.runAlgorithm(input, output, min_utility_conf);
				
				algoShane.printStats();
				
				/*load map database for id translate to headline*/
				//HashMap<Integer, String> mappingdb = new HashMap<Integer, String>();
				//mappingdb = loadMapDB(mapdb);
				//System.out.println("loadMap successful!");
				//saveSortedRuleToText(pattern,mappingdb, textput);
				
	}

	
	/**
	 * Load mappingDB to the search Map "idToheadline"
	 */
	public static HashMap<Integer, String> loadMapDB(String mapfile) throws IOException{
		// map contains all newsID to headlines for translation from newsID to headline
		HashMap<Integer, String> idToheadline = new HashMap<Integer, String>();
		BufferedReader myMappingDB = null;
		
		String entryLine;
		
		try{
			myMappingDB = new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapfile))));
			// for each line (mapping entry) until the end of the file
			while ((entryLine = myMappingDB.readLine()) != null) {
				// if the line is empty
				if (entryLine.isEmpty() == true) {
					continue;
				}
				//split the transaction according to the " # " separator
				String split[] = entryLine.split(" # ");
				//count++;System.out.println(count + " " + split[0] + " " + split[1]);
				idToheadline.put(Integer.parseInt(split[0]), split[1]);				
			}
			
		}catch (Exception e) {
        	// catches exception if error while reading the input file
        	e.printStackTrace();
        }finally {
        	if(myMappingDB != null){
        		myMappingDB.close();
        	}
        }
		return idToheadline;
	}
	
    /**
     * Save translated text headline results 
     * @param rules , rules generated from AssocRulesHUIminer
     * @param mapping , Map with newsID and headline
     * @param textOutput , output file with final text presentation
     * @throws IOException
     */
    protected static void saveSortedRuleToText(Rules rules, HashMap<Integer, String> mapping, String textOutput) throws IOException {
			
			// if the result should be saved to a file
    	    BufferedWriter writer = new BufferedWriter(new FileWriter(textOutput)); 
			if(writer != null){				

				// create a string buffer
				StringBuilder buffer = new StringBuilder(" ----------------------------------------------------------- ");
				buffer.append(rules.getName());
				buffer.append(" (TOTAL NUMBER ");
				buffer.append(rules.size());
				buffer.append(", MIN-UCONF ");
				buffer.append(rules.getMinUconfString());
				buffer.append(") -----------------------------------------------------------\n");
				buffer.append(" Rule No. \t\t Rule \t\t\t\t Potential utility value \t\t Pattern utility value \t\t Utility-confidence \n"); 
				buffer.append(" -------------------------------------------------------------------------------------------");
				buffer.append("-------------------------------------------------------------------------------------------\n");
				
				int k = 0;
				// for each rule
				for(Rule rule : rules.getRules()){
					// append the rule, its support and confidence.
					buffer.append(" ");//buffer.append(" rule ");
					buffer.append(k);
					buffer.append("\t  ");

					// write itemset 1
					int itemset1 [] = rule.getItemset1();
					for (int i = 0; i < itemset1.length; i++) {
						String headline;
						
						if(mapping.containsKey(itemset1[i])){
				    	   headline = mapping.get(itemset1[i]);
				    	}else{
				    	   headline = "nil-" + itemset1[i];
				    	}	
						buffer.append("[" + headline + "]");
						if (i != itemset1.length - 1) {
							buffer.append(" ");
						}
					}
								
					// write separator
					buffer.append(" ==> ");
					
					// write itemset 2
					int itemset2 [] = rule.getItemset2();
					for (int i = 0; i < itemset2.length; i++) {
						String headline;
						if(mapping.containsKey(itemset2[i])){
					    	   headline = mapping.get(itemset2[i]);
					    	}else{
					    	   headline = "nil-" + itemset2[i];
					    }
						buffer.append("[" + headline + "]");
						buffer.append(" ");
					}	
					// write separator
					buffer.append("\t "); //buffer.append(" #PotentialUtility ");
					// write Utility Value of Y
					buffer.append(rule.getpotentialUtility());
					// write separator
					buffer.append("\t ");//buffer.append(" #PatternUtility ");
					// write pattern utility XY
					buffer.append(rule.getTotalUtility());
					// write separator
					buffer.append("\t ");//buffer.append(" #UCONF: ");
					// write confidence
					buffer.append(doubleToString(rule.getConfidence()));
					buffer.append("\n");
					k++;
				}
				writer.write(buffer.toString());		
			}		
			writer.close();
	}
    
	/**
	 * Convert a double value to a string with only five decimal
	 * @param value  a double value
	 * @return a string
	 */
	static String doubleToString(double value) {
		// convert it to a string with two decimals
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(0); 
		format.setMaximumFractionDigits(5); 
		return format.format(value);
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
			    URL url = AssocTest.class.getResource(filename);
	        	return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
