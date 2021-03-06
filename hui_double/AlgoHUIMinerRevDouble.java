
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an implementation of the "HUI-MINER Algorithm" for High-Utility Itemsets Mining
 * as described in the conference paper : <br/><br/>
 * 
 *  Liu, M., Qu, J. (2012). Mining High Utility Itemsets without Candidate Generation. 
 *  Proc. of CIKM 2012. pp.55-64.
 *
 * @see UtilityListDouble
 * @see ElementDouble
 * @author Xing Zhao revised from Philippe Fournier-Viger
 */
public class AlgoHUIMinerRevDouble {

	// variable for statistics
	double maxMemory = 0;     // the maximum memory usage
	long startTimestamp = 0;  // the time the algorithm started
	long endTimestamp = 0;   // the time the algorithm terminated
	int huiCount =0;  // the number of HUI generated
	
	Map<Integer, Double> mapItemToTWU;
	Map<Integer, List<Pair>> mapTidToitsUtilityArray;
	
	BufferedWriter writer = null;  // writer to write the output file
	private int joinCount;
	
	// this class represent an item and its utility in a transaction
	class Pair{
		int item = 0;
		double utility = 0;
	}
	
	/**
	 * Default constructor
	 */
	public AlgoHUIMinerRevDouble() {
	}

	/**
	 * Run the algorithm
	 * @param input the input file path
	 * @param output the output file path
	 * @param minUtility the minimum utility threshold
	 * @throws IOException exception if error while writing the file
	 */
	public void runAlgorithm(String input, String output, double minUtility) throws IOException {
		// reset maximum
		maxMemory =0;
		
		startTimestamp = System.currentTimeMillis();
		
		writer = new BufferedWriter(new FileWriter(output));

		//  We create a  map to store the TWU of each item
		mapItemToTWU = new HashMap<Integer, Double>();
		//  We create a map to store the Utility Array of each item per transaction
		mapTidToitsUtilityArray = new HashMap<Integer, List<Pair>>();

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
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// split the transaction according to the : separator
				String split[] = thisLine.split(":"); 
				// the first part is the list of items
				String items[] = split[0].split(" "); 
				// the second part is the transaction utility
				double transactionUtility = Double.parseDouble(split[1]);  
				// for each item, we add the transaction utility to its TWU
				for(int i=0; i < items.length; i++){
					// convert item to integer
					Integer item = Integer.parseInt(items[i]);
					// get the current TWU of that item
					Double twu = mapItemToTWU.get(item);
					// add the utility of the item in the current transaction to its twu
					twu = (twu == null)? 
							transactionUtility : twu + transactionUtility;
					mapItemToTWU.put(item, twu);
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
		
		// CREATE A LIST TO STORE THE UTILITY LIST OF ITEMS WITH TWU  >= MIN_UTILITY.
		List<UtilityListDouble> listOfUtilityListDoubles = new ArrayList<UtilityListDouble>();
		// CREATE A MAP TO STORE THE UTILITY LIST FOR EACH ITEM.
		// Key : item    Value :  utility list associated to that item
		Map<Integer, UtilityListDouble> mapItemToUtilityListDouble = new HashMap<Integer, UtilityListDouble>();
		
		// For each item
		for(Integer item: mapItemToTWU.keySet()){
			// if the item is promising  (TWU >= minutility)
			if(mapItemToTWU.get(item) >= minUtility){
				// create an empty Utility List that   we will fill later.
				UtilityListDouble uList = new UtilityListDouble(item);
				mapItemToUtilityListDouble.put(item, uList);
				// add the item to the list of high TWU items
				listOfUtilityListDoubles.add(uList); 
				
			}
		}
		// SORT THE LIST OF HIGH TWU ITEMS IN ASCENDING ORDER
		Collections.sort(listOfUtilityListDoubles, new Comparator<UtilityListDouble>(){
			public int compare(UtilityListDouble o1, UtilityListDouble o2) {
				// compare the TWU of the items
				return compareItems(o1.item, o2.item);
			}
			} );
		
		// SECOND DATABASE PASS TO CONSTRUCT THE UTILITY LISTS 
		// OF 1-ITEMSETS  HAVING TWU  >= minutil (promising items)
		try {
			// prepare object for reading the file
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			// variable to count the number of transaction
			int tid =0;
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// split the line according to the separator
				String split[] = thisLine.split(":");
				// get the list of items
				String items[] = split[0].split(" ");
				// get the list of utility values corresponding to each item
				// for that transaction
				String utilityValues[] = split[2].split(" ");
				
				// Copy the transaction into lists but 
				// without items with TWU < minutility
				
				double remainingUtility =0;
				
				// Create a list to store items >= minUtility
				List<Pair> revisedTransaction = new ArrayList<Pair>();
				// create a list to store items
				List<Pair> tidUtilityArray = new ArrayList<Pair>();
				// for each item
				for(int i=0; i <items.length; i++){
					/// convert values to integers
					Pair pair = new Pair();
					pair.item = Integer.parseInt(items[i]);
					pair.utility = Double.parseDouble(utilityValues[i]);				
					// add pair of all items for each transaction
					tidUtilityArray.add(pair);
					// if the item has enough utility
					if(mapItemToTWU.get(pair.item) >= minUtility){
						// add it
						revisedTransaction.add(pair);
						remainingUtility += pair.utility;
					}
					
				}
				
				Collections.sort(revisedTransaction, new Comparator<Pair>(){
					public int compare(Pair o1, Pair o2) {
						return compareItems(o1.item, o2.item);
					}});
							
				Collections.sort(tidUtilityArray, new Comparator<Pair>(){
					public int compare(Pair o1, Pair o2) {
						return compareItems(o1.item, o2.item);
					}});
				
				// for each item left in the transaction
				for(Pair pair : revisedTransaction){
					// subtract the utility of this item from the remaining utility
					remainingUtility = remainingUtility - pair.utility;
					
					// get the utility list of this item
					UtilityListDouble UtilityListDoubleOfItem = mapItemToUtilityListDouble.get(pair.item);
					
					// Add a new ElementDouble to the utility list of this item corresponding to this transaction
					ElementDouble ElementDouble = new ElementDouble(tid, pair.utility, remainingUtility);
					
					UtilityListDoubleOfItem.addElementDouble(ElementDouble);
				}
				
				// load List of pairs to UtilityArray of each item per transaction
				mapTidToitsUtilityArray.put(tid, tidUtilityArray);
				
				tid++; // increase tid number for next transaction

			}
		} catch (Exception e) {
			// to catch error while reading the input file
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }
		
		// check the memory usage
		checkMemory();

		// Mine the database recursively
		huiMiner(new int[0], null, listOfUtilityListDoubles, minUtility);
		
		// check the memory usage again and close the file.
		checkMemory();
		// close output file
		writer.close();
		// record end time
		endTimestamp = System.currentTimeMillis();
	}
	
	private int compareItems(int item1, int item2) {
		double compare = mapItemToTWU.get(item1) - mapItemToTWU.get(item2);
		// if the same, use the lexical order otherwise use the TWU
		return (compare == 0)? item1 - item2 : (int) compare;
	}
	
	/**
	 * This is the recursive method to find all high utility itemsets. It writes
	 * the itemsets to the output file.
	 * @param prefix  This is the current prefix. Initially, it is empty.
	 * @param pUL This is the Utility List of the prefix. Initially, it is empty.
	 * @param ULs The utility lists corresponding to each extension of the prefix.
	 * @param minUtility The minUtility threshold.
	 * @throws IOException
	 */
	private void huiMiner(int [] prefix, UtilityListDouble pUL, List<UtilityListDouble> ULs, double minUtility)
			throws IOException {
		
		// For each extension X of prefix P
		for(int i=0; i< ULs.size(); i++){
			UtilityListDouble X = ULs.get(i);
			double[] utilityArray = new double[prefix.length + 1];
			int[] tidArray = new int[X.elements.size()];
			for(int k = 0; k < X.elements.size(); k++){
				tidArray[k] = X.elements.get(k).tid;				
			}
			
			// If pX is a high utility itemset.
			// we save the itemset:  pX 
			if(X.sumIutils >= minUtility){
				// save to file
				//writeOut(prefix, X.item, X.sumIutils);
				
				int [] huiItemset = new int[prefix.length + 1];
				System.arraycopy(prefix, 0, huiItemset, 0, prefix.length);
				huiItemset[prefix.length] = X.item;
				// load UL of X for utility array ---- extract transID from pUL, find each item in prefix and X.item
				// go back to trans dataset to find the value of item, sum them up.
				for(int j = 0; j < huiItemset.length; j++){ // loop items through the prefix array
					int luvItem = 0;
					for(int e: tidArray){ // go back to UL tid list
						for(Pair p: mapTidToitsUtilityArray.get(e)){ // find value of the item in each tid
							if(p.item == huiItemset[j])
								luvItem += p.utility; 				//  local value
						}
					}
					utilityArray[j] = luvItem;
				}
				//save to file
				writeOutAll(huiItemset, X.sumIutils, utilityArray);
				
 			}
			
			// If the sum of the remaining utilities for pX
			// is higher than minUtility, we explore extensions of pX.
			// (this is the pruning condition)
			if(X.sumIutils + X.sumRutils >= minUtility){
				// This list will contain the utility lists of pX extensions.
				List<UtilityListDouble> exULs = new ArrayList<UtilityListDouble>();
				// For each extension of p appearing
				// after X according to the ascending order
				for(int j=i+1; j < ULs.size(); j++){
					UtilityListDouble Y = ULs.get(j);
					// we construct the extension pXY 
					// and add it to the list of extensions of pX
					exULs.add(construct(pUL, X, Y));
					joinCount++;
				}
				// We create new prefix pX
				int [] newPrefix = new int[prefix.length+1];
				System.arraycopy(prefix, 0, newPrefix, 0, prefix.length);
				newPrefix[prefix.length] = X.item;
				
				// We make a recursive call to discover all itemsets with the prefix pXY
				huiMiner(newPrefix, X, exULs, minUtility); 
			}
		}
	}
	
	/**
	 * This method constructs the utility list of pXY
	 * @param P :  the utility list of prefix P.
	 * @param px : the utility list of pX
	 * @param py : the utility list of pY
	 * @return the utility list of pXY
	 */
	private UtilityListDouble construct(UtilityListDouble P, UtilityListDouble px, UtilityListDouble py) {
		// create an empy utility list for pXY
		UtilityListDouble pxyUL = new UtilityListDouble(py.item);
		// for each ElementDouble in the utility list of pX
		for(ElementDouble ex : px.elements){
			// do a binary search to find ElementDouble ey in py with tid = ex.tid
			ElementDouble ey = findElementDoubleWithTID(py, ex.tid);
			if(ey == null){
				continue;
			}
			// if the prefix p is null
			if(P == null){
				// Create the new ElementDouble
				ElementDouble eXY = new ElementDouble(ex.tid, ex.iutils + ey.iutils, ey.rutils);
				// add the new ElementDouble to the utility list of pXY
				pxyUL.addElementDouble(eXY);
				
			}else{
				// find the ElementDouble in the utility list of p wih the same tid
				ElementDouble e = findElementDoubleWithTID(P, ex.tid);
				if(e != null){
					// Create new ElementDouble
					ElementDouble eXY = new ElementDouble(ex.tid, ex.iutils + ey.iutils - e.iutils,
								ey.rutils);
					// add the new ElementDouble to the utility list of pXY
					pxyUL.addElementDouble(eXY);
				}
			}	
		}
		// return the utility list of pXY.
		return pxyUL;
	}
	
	/**
	 * Do a binary search to find the ElementDouble with a given tid in a utility list
	 * @param ulist the utility list
	 * @param tid  the tid
	 * @return  the ElementDouble or null if none has the tid.
	 */
	private ElementDouble findElementDoubleWithTID(UtilityListDouble ulist, int tid){
		List<ElementDouble> list = ulist.elements;
		
		// perform a binary search to check if  the subset appears in  level k-1.
        int first = 0;
        int last = list.size() - 1;
       
        // the binary search
        while( first <= last )
        {
        	int middle = ( first + last ) >>> 1; // divide by 2

            if(list.get(middle).tid < tid){
            	first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
            }
            else if(list.get(middle).tid > tid){
            	last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
            }
            else{
            	return list.get(middle);
            }
        }
		return null;
	}

	/**
	 * Method to write a high utility itemset to the output file.
	 * @param the prefix to be writent o the output file
	 * @param an item to be appended to the prefix
	 * @param utility the utility of the prefix concatenated with the item
	 */
	private void writeOut(int[] prefix, int item, double utility) throws IOException {
		huiCount++; // increase the number of high utility itemsets found
		
		//Create a string buffer
		StringBuilder buffer = new StringBuilder();
		// append the prefix
		for (int i = 0; i < prefix.length; i++) {
			buffer.append(prefix[i]);
			buffer.append(' ');
		}
		// append the last item
		buffer.append(item);
		// append the utility value
		buffer.append("#UTIL: ");
		buffer.append(utility);
		// write to file
		writer.write(buffer.toString());
		writer.newLine();
	}
	/**
	 * Method to write a high utility itemset to the output file.
	 * @param the HUI pattern to be written to the output file
	 * @param utility the utility of the prefix concatenated with the item
	 * @param utility array of that pattern
	 */
	private void writeOutAll(int[] huiItemset, double utility, double[] utilityArray) throws IOException {
		huiCount++; // increase the number of high utility itemsets found
		
		//Create a string buffer
		StringBuilder buffer = new StringBuilder();
		// append the prefix
		for (int i = 0; i < huiItemset.length; i++) {
			buffer.append(huiItemset[i]);
			buffer.append(' ');
		}
         
		// append the utility value
		buffer.append("#UTIL: ");
		buffer.append(utility);
		//buffer.append("#UTIL: ");
		buffer.append("#UARY: ");
		for (int i = 0; i < utilityArray.length; i++) {
			buffer.append(utilityArray[i]);
			buffer.append(' ');
		}
		
		// write to file
		writer.write(buffer.toString());
		writer.newLine();
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
		System.out.println("=============  HUI-MINER ALGORITHM - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Memory ~ " + maxMemory+ " MB");
		System.out.println(" High-utility itemsets count : " + huiCount); 
		System.out.println(" Join count : " + joinCount); 
		System.out.println("===================================================");
	}
}
