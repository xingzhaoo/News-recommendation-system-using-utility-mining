import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Association Rules applied on HUI mined patterns, this algorithm considers Y and the consequence as high utility itemsets.
 * It considers X as a high utility itemset to prune the not important association rules
 * For an association rule R: X->Y, X, Y are nonempty, Y is 1-itemset and X^Y = empty,
 * we define uconf(R) = luv(Y, XUY)/ u(XUY)) where Y is HUI
 * Revised version 1: Only top k rules per pattern is output to the file
 * @author Xing Zhao
 *
 */
public class AssocRulesHUIminerZ {

	    // the frequent itemsets that will be used to generate the rules
	    protected List<UItemset> patterns;

	    // the set stores all high utility itemsets
	    protected HashSet<String> HUIset;
		// variable used to store the result if the user choose to save
		// the result in memory rather than to an output file
		protected Rules rules;
			
		// object to write the output file if the user wish to write to a file
		protected BufferedWriter writer = null;
		
		// define the top K number of rules per pattern
		protected int topK = 3;
		
		// for statistics
		protected final static double EPSILON = 0.00001; // precision for double comparison 
		protected long startTimestamp = 0; // last execution start time
		protected long endTimestamp = 0;   // last execution end time
		double maxMemory = 0;     		// the maximum memory usage
		protected int rulesCount = 0;  // number of rules generated
		protected int totalpatterns = 0; // number of transactions in database
		
		// parameters
		protected double minconf;

		
		/**
		 * Default constructor
		 */
		public AssocRulesHUIminerZ(){
			
		}
		
		/**
		 * Run the algorithm
		 * @param patterns  a set of frequent itemsets
		 * @param output an output file path for writing the result or null if the user want this method to return the result
		 * @param databaseSize  the number of transactions in the database
		 * @param minconf  the minconf threshold
		 * @return  the set of association rules if the user wished to save them into memory
		 * @throws IOException exception if error writing to the output file
		 */
		public Rules runAlgorithm(String input, String output, double minconf) throws IOException {
			maxMemory =0;
			// save the parameters
			this.minconf = minconf;
			//totalpatterns = 0;
			//patterns = new ArrayList<UItemset>();// can use map to sort the patterns
			HUIset = new HashSet<String>();
			// load the patterns to List of UItemset from input file
			// We scan the database a first time to calculate the TWU of each item.
			BufferedReader myInput = null;
			String thisLine;
			try {
				// prepare the object for reading the file
				myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
				// for each line (transaction) until the end of file
				while ((thisLine = myInput.readLine()) != null) {
					// if the line is  a comment, is  empty or is a
					// kind of metadata
					if (thisLine.isEmpty() == true) {
						continue;
					}
					
					// split the transaction according to the : separator
					String split[] = thisLine.trim().split(" #UTIL: "); 
					// the first part is the list of items
					String items[] = split[0].split(" "); 
					// load HUI string to HUI set : stores sorted HUIs in ascending order for later verification
					HUIset.add(sortStrArray(items));
/*					//int index[] = sortArrayIndex(items);
					// the second part is the total utility and utility array
					String utilityANDuarray[] = split[1].split("#UARY: ");
					// the utility of the itemset
					int totalUtility = Integer.parseInt(utilityANDuarray[0]);
					// utility array
					String utilityArray[] = utilityANDuarray[1].split(" ");
					// for each item, we add them to itemset array along with its local utility to utility array
					int itemsArray[] = new int[items.length];
					int itemsetUtilityArray[] = new int[utilityArray.length];
					
					for(int i = 0; i <items.length; i++){
						// convert item to integer
						itemsArray[i] = Integer.parseInt(items[i]);
						// convert utility to integer
						itemsetUtilityArray[i] = Integer.parseInt(utilityArray[i]);
					}
					
					UItemset uitemset = new UItemset(itemsArray, itemsetUtilityArray, totalUtility);
					if(uitemset != null){
					   patterns.add(uitemset);
					   totalpatterns++;
					}*/
				}
			} catch (Exception e) {
				// catches exception if error while reading the input file
				e.printStackTrace();
			}finally {
				if(myInput != null){
					myInput.close();
				}
		    }
			
			
			// check the memory usage
			checkMemory();
			// start the algorithm
			return runAlgorithm(HUIset, input, output);

		}
		

		/**
		 * Run the algorithm for generating association rules from a set of itemsets.
		 * @param patterns the set of itemsets
		 * @param output the output file path. If null the result is saved in memory and returned by the method.
		 * @param databaseSize  the number of transactions in the original database
		 * @return the set of rules found if the user chose to save the result to memory
		 * @throws IOException exception if error while writting to file
		 */
		private Rules runAlgorithm(HashSet<String> HUIset, String input, String output)
				throws IOException {

			
			startTimestamp = System.currentTimeMillis();
			// if the user want to keep the result into memory
			/*
			if(output == null){
				writer = null;
				rules =  new Rules("HUI MINED ASSOCIATION RULES", minconf);
		    }else{ 
		    	// if the user want to save the result to a file
		    	rules = null;
				writer = new BufferedWriter(new FileWriter(output)); 
			}
			*/
			writer = new BufferedWriter(new FileWriter(output)); 
			rules =  new Rules("HUI MINED ASSOCIATION RULES", minconf); // ignored above code
			
			// initialize variable to count the number of rules found
			rulesCount = 0;
			
			BufferedReader myInput = null;
			String thisLine;
			try {
				// prepare the object for reading the file
				myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
				// for each line (transaction) until the end of file
				while ((thisLine = myInput.readLine()) != null) {
					// if the line is  a comment, is  empty or is a
					// kind of metadata
					if (thisLine.isEmpty() == true) {
						continue;
					}
					
					// split the transaction according to the : separator
					String split[] = thisLine.trim().split(" #UTIL: "); 
					// the first part is the list of items
					String items[] = split[0].split(" "); 	
					//int index[] = sortArrayIndex(items);
					// the second part is the total utility and utility array
					String utilityANDuarray[] = split[1].split("#UARY: ");
					// the utility of the itemset
					int totalUtility = Integer.parseInt(utilityANDuarray[0]);
					// utility array
					String utilityArray[] = utilityANDuarray[1].split(" ");
					// for each item, we add them to itemset array along with its local utility to utility array
					int itemsArray[] = new int[items.length];
					int itemsetUtilityArray[] = new int[utilityArray.length];
					
					for(int i = 0; i <items.length; i++){
						// convert item to integer
						itemsArray[i] = Integer.parseInt(items[i]);
						// convert utility to integer
						itemsetUtilityArray[i] = Integer.parseInt(utilityArray[i]);
					}
					
					UItemset eachItemset = new UItemset(itemsArray, itemsetUtilityArray, totalUtility);
					if(eachItemset != null){
					   //patterns.add(uitemset);
						if(eachItemset.getUItemset().length < 2){
							continue;
						}
						
						eachItemset = eachItemset.sort();
						
						// split each item start from first to last 2nd X->Y
						// finding unordered Association rule 
						// get a list of items from an itemset
						for(UItemset consequentY: eachItemset.getUItemsetList()){
							//System.out.println("=>" + HUIset.contains(consequentY.getUItemsetString()) + consequentY.getUItemsetString()); 
							// consequentY must be HUI 
							if(HUIset.contains(consequentY.getUItemsetString())){
								// generate the unique subsets of superset except consequentY
								List<UItemset> subsetListX = eachItemset.cloneItemSetMinusAnItemset(consequentY).getSubset();
		/*						System.out.println("=>" + eachItemset.getUItemsetString());
								for(UItemset e: subsetListX){
									System.out.println("* *" + e.getUItemsetString());
								}
								System.out.println("-------");*/
								
								for(UItemset antecedentX: subsetListX){
									// antecedentX must be HUI
									if(HUIset.contains(antecedentX.getUItemsetString())){
										//System.out.println(antecedentX.getUItemsetString());
										double uconf =  consequentY.getUtility() / (double) (antecedentX.getUtility() + consequentY.getUtility());
										if(uconf - minconf >= EPSILON){
											Rule aRule = new Rule(antecedentX, consequentY, consequentY.getUtility(),  consequentY.getUtility() + antecedentX.getUtility(), uconf);
											aRule.sortItemset();
		/*									System.out.println(aRule.toString());
											writer.write(aRule.toString());
											writer.newLine();*/
											rules.addRule(aRule);
											if(!rules.contains(aRule)){
												//System.out.println(aRule.toString());
												rules.addRule(aRule);
											}

										}
									}
								}
								
							}
						}
						
						
						
					   totalpatterns++;
					}
				}
			} catch (Exception e) {
				// catches exception if error while reading the input file
				e.printStackTrace();
			}finally {
				if(myInput != null){
					myInput.close();
				}
		    }

			   /* original version, in order Association Rule: in order check
			   for(int j = 0 ; j < eachItemset.size() - 1; j++){
				     UItemset consequentY = eachItemset.cloneItemSetMinusItemsToIndex(j);
				     UItemset antecedentX = eachItemset.cloneItemSetKeepItemsTillIndex(j); // X is HUI
					 if(consequentY != null){
						 if(HUIset.contains(consequentY.getUItemsetString()) && HUIset.contains(antecedentX.getUItemsetString())){ // revised for X is HUI
							double uconf =  consequentY.getUtility() / (double) eachItemset.getUtility();
						 	if(uconf - minconf >= EPSILON){
						 		//UItemset antecedentX = eachItemset.cloneItemSetKeepItemsTillIndex(j); // X not HUI
						 		rulesInPattern.addRule(new Rule(antecedentX, consequentY, consequentY.getUtility(), eachItemset.getUtility(), uconf));
						 		/*rulesInPattern.addRule(new Rule(antecedentX.getUItemset(), consequentY.getUItemset(), UnionUtilitySet(antecedentX, consequentY), consequentY.getUtility(),
									eachItemset.getUtility(), uconf));// revised for topK
									*/
							//saveRule(antecedentX.getUItemset(), consequentY.getUItemset(), consequentY.getUtility(),
									//eachItemset.getUtility(), uconf);
				/*		 	}
						 }
					 }else{
						 System.out.println("error on pattern:"+ eachItemset.getUItemset());
					 }
				}
				*/
			
				//rulesInPattern.sortByConfidence();
				// load rules to a list
				//List<Rule> rulesPatternList = rulesInPattern.getRules();
				/* added for topK */
				// sort rules by confidence
				//int range = rulesPatternList.size() < topK ? rulesPatternList.size() : topK ;
				/*int range = rulesPatternList.size(); // cleared TopK
				for(int j = 0; j < range; j++){
					Rule newRule = rulesPatternList.get(j);
					if(!rules.contains(newRule)){
						rules.addRule(newRule);
					}
				}*/
				
			
			// sort rules
			rules.sortByConfidence();
	
			// save to file
			//saveSortedRule(rules); // numbers
			saveSortedRule(rules); // text presentation
			// check the memory usage again and close the file.
			checkMemory();
			// close the file if we saved the result to a file
			if(writer != null){
				writer.close();
			}
			
		   endTimestamp = System.currentTimeMillis();
		   return rules;
		}
		

		/**
		 * Method to check the memory usage and keep the maximum memory usage.
		 */
		private void checkMemory() {
			// get the current memory usage
			double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
					/ 1024d / 1024d;
			// if higher than the maximum until now
			if (currentMemory > maxMemory) {
				// replace the maximum with the current memory usage
				maxMemory = currentMemory;
			}
		}
		
		/**
		 * Print statistics about the latest execution to System.out.
		 */
		public void printStats() {
			System.out.println("=============  Association Rule ALGORITHM - STATS =============");
			System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
			System.out.println(" Memory ~ " + maxMemory+ " MB");
			System.out.println(" High-utility itemsets count : " + totalpatterns); 
			System.out.println(" Number of Rules generated : " + rulesCount); 
			System.out.println("===================================================");
		}
		
		/**
		 * Save a rule to the output file or in memory depending
		 * if the user has provided an output file path or not
		 * @param itemset1  left itemset of the rule
		 * @param supportItemset1 the support of itemset1 if known
		 * @param itemset2  right itemset of the rule
		 * @param supportItemset2 the support of itemset2 if known
		 * @param absoluteSupport support of the rule
		 * @param conf confidence of the rule
		 * @param lift lift of the rule
		 * @throws IOException exception if error writing the output file
		 */
		protected void saveRule(int[] itemset1, int[] itemset2, int[] utilityset,
				int potentialUtility, int patternUtility, double uconf) throws IOException {
			
			rulesCount++;
			// if the result should be saved to a file
			if(writer != null){
				StringBuilder buffer = new StringBuilder();
				// write itemset 1
				for (int i = 0; i < itemset1.length; i++) {
					buffer.append(itemset1[i]);
					if (i != itemset1.length - 1) {
						buffer.append(" ");
					}
				}
				// write separator
				buffer.append(" ==> ");
				// write itemset 2
				for (int i = 0; i < itemset2.length; i++) {
					buffer.append(itemset2[i]);
					if (i != itemset2.length - 1) {
						buffer.append(" ");
					}
				}
				
				buffer.append("#Rule local Utility ");
				for (int i = 0; i < itemset2.length; i++) {
					buffer.append(itemset2[i]);
					if (i != itemset2.length - 1) {
						buffer.append(" ");
					}
				}
				buffer.append("%");
				// write separator
				buffer.append(" #PotentialUtility ");
				// write support
				buffer.append(potentialUtility);
				// write separator
				buffer.append(" #PatternUtility ");
				// write support
				buffer.append(patternUtility);
				// write separator
				buffer.append(" #UCONF: ");
				// write confidence
				buffer.append(doubleToString(uconf));
				
				writer.write(buffer.toString());
				writer.newLine();
			}// otherwise the result is kept into memory
			else{
				rules.addRule(new Rule(itemset1, itemset2, utilityset, potentialUtility,
						patternUtility, uconf));
			}
		}

		/**
		 * Save a rule to the output file or in memory depending
		 * if the user has provided an output file path or not
		 * @param itemset1  left itemset of the rule
		 * @param supportItemset1 the support of itemset1 if known
		 * @param itemset2  right itemset of the rule
		 * @param supportItemset2 the support of itemset2 if known
		 * @param absoluteSupport support of the rule
		 * @param conf confidence of the rule
		 * @param lift lift of the rule
		 * @throws IOException exception if error writing the output file
		 */
		protected void saveSortedRule(Rules rules) throws IOException {
			
			// if the result should be saved to a file
			rulesCount = rules.size();
			if(writer != null){				
				// create a string buffer
				StringBuilder buffer = new StringBuilder();
				buffer.append(rules.toString());
				writer.write(buffer.toString());

				/*
				StringBuilder buffer = new StringBuilder(" ------- ");
				buffer.append(rules.getName());
				buffer.append(" -------\n");
				writer.write(buffer.toString());
				int i=0;
				// for each rule
				for(Rule rule : rules.getRules()){
					StringBuilder bufferR = new StringBuilder();
					// append the rule, its support and confidence.
					bufferR.append("  rule ");
					bufferR.append(i);
					bufferR.append(":  ");
					bufferR.append(rule.toString());
					bufferR.append("\n");
					writer.write(bufferR.toString());
					i++;
				} */
			
					
			}
			
		}

		/**
		 * Load mappingDB to the search Map "idToheadline"
		 */
		public HashMap<Integer, String> loadMapDB(String mapfile) throws IOException{
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
		 *  Save translated text headline results 
		 * 
		 */
	    protected void saveSortedRuleToText(Rules rules, HashMap<Integer, String> mapping, String textOutput) throws IOException {
				
				// if the result should be saved to a file
	    	    BufferedWriter writer = new BufferedWriter(new FileWriter(textOutput)); 
				if(writer != null){				

					// create a string buffer
					StringBuilder buffer = new StringBuilder(" ------------------ ");
					buffer.append(rules.getName());
					buffer.append(" (TOTAL NUMBER ");
					buffer.append(rules.size());
					buffer.append(", MINUCONF ");
					buffer.append(rules.getMinUconfString());
					buffer.append(") ------------------\n");

					int k = 0;
					// for each rule
					for(Rule rule : rules.getRules()){
						// append the rule, its support and confidence.
						buffer.append(" rule ");
						buffer.append(k);
						buffer.append(":  ");
						
						/*buffer.append("potential value / total Utility of the rule :  ");
						buffer.append(" (");
						buffer.append(rule.getpotentialUtility());
						buffer.append("/");
						buffer.append(rule.getTotalUtility());
						buffer.append(") ");
						buffer.append("confidence :  " );
						buffer.append(rule.getConfidence()); */
						// write itemset 1
						int itemset1 [] = rule.getItemset1();
						for (int i = 0; i < itemset1.length; i++) {
							String headline;
							
							if(mapping.containsKey(itemset1[i])){
					    	   headline = mapping.get(itemset1[i]);
					    	}else{
					    	   headline = "nil-" + itemset1[i];
					    	}	
							buffer.append(headline);
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
							buffer.append(headline);
							buffer.append(" ");
						}
						
						// write separator
						buffer.append(" #PotentialUtility ");
						// write Utility Value of Y
						buffer.append(rule.getpotentialUtility());
						// write separator
						buffer.append(" #PatternUtility ");
						// write pattern utility XY
						buffer.append(rule.getTotalUtility());
						// write separator
						buffer.append(" #UCONF: ");
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
		 * Union up the two utility arrays of UItemset
		 * 
		 * @param x
		 *            UItemset
		 * @param y
		 *            UItemset
		 * @return the utility
		 */
	    /*
		private static int[] UnionUtilitySet(UItemset x, UItemset y) {

			int[] xSet = x.utilityArray;
			int[] ySet = y.utilityArray;
			int[] ruleUtility = new int[xSet.length + ySet.length];

			int j = 0;
			for (int i = 0; i < ruleUtility.length; i++) {
				if (i < xSet.length) {
					ruleUtility[i] = xSet[i];
				} else {
					ruleUtility[i] = ySet[j++];
				}

			}
			return ruleUtility;
		}
	     */
	    /**
	     * String array sort
	     * @param elements
	     * @return a index of a raw array in sorted format
	     */
	    
	    public static int[] sortArrayIndex(String[] elements){
	    	 int greater;
	    	 String [] ordered = new String[elements.length];
	         int[] index = new int[ordered.length]; 
	         //imperfect number ordering algorithm
	         for(int indexL=0;indexL< elements.length;indexL++)
	         {
	             greater=0;
	             for(int indexR=0;indexR<elements.length;indexR++)
	             {
	                 if(Integer.parseInt(elements[indexL]) > Integer.parseInt(elements[indexR])) //(elements[indexL].compareTo(elements[indexR]) > 0)
	                 {
	                     greater++;
	                 }
	             }
	             while (ordered[greater] == elements[indexL]) {
	                 greater++;
	             }
	            ordered[greater] = elements[indexL];
	            index[greater] = indexL;
	         }
	         
	         
	         return index;
	    }
	    
	    /**
	     * String array sort
	     * @param elements
	     * @return a String format of a sorted array
	     */
	    public static String sortStrArray(String[]  elements){
	    	 int greater;
	    	 StringBuilder result = new StringBuilder();
	         String [] ordered = new String[elements.length];
	         int[] index = new int[ordered.length]; 
	         //imperfect number ordering algorithm
	         for(int indexL=0;indexL< elements.length;indexL++)
	         {
	             greater=0;
	             for(int indexR=0;indexR<elements.length;indexR++)
	             {
	                 if(Integer.parseInt(elements[indexL]) > Integer.parseInt(elements[indexR])) //(elements[indexL].compareTo(elements[indexR]) > 0)
	                 {
	                     greater++;
	                 }
	             }
	             while (ordered[greater] == elements[indexL]) {
	                 greater++;
	             }
	            ordered[greater] = elements[indexL];
	            index[greater] = indexL;
	         }
	         
	         for(String e: ordered){
	        	 result.append(e + " ");
	         }
	         
	         return result.toString().trim();
	    }
		
		/**
		 * Convert a double value to a string with only five decimal
		 * @param value  a double value
		 * @return a string
		 */
		String doubleToString(double value) {
			// convert it to a string with two decimals
			DecimalFormat format = new DecimalFormat();
			format.setMinimumFractionDigits(0); 
			format.setMaximumFractionDigits(5); 
			return format.format(value);
		}


	
}
