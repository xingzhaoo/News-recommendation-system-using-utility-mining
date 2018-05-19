import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
* This class represents an itemset (a set of items) implemented as an array of integers with
* its associated UtilityArray of the itemset and a variable to store the support count of the itemset.
* 
* @author Xing Zhao
*/

public class UItemset {
	/** the array of items **/
	protected int[] uItemset; 
	/** the Utility array of items using map**/
	protected int[] utilityArray;
	/**  the Utility of this UItemset */
	protected int utility; 
	
	/**
	 * Constructor
	 */
	public UItemset(){
		uItemset = new int[]{};
		utilityArray = new int[]{};
		utility = 0;		
	}
	
	/**
	 * Constructor - 1-itemsets
	 * @param item an item that should be added to the new itemset
	 * @param uv the utility value of the item
	 */
	public UItemset(int item, int luv, int utilityItem){
		
		uItemset = new int[]{item};
		utilityArray = new int[]{luv};
		utility = utilityItem;
	}

	/**
	 * Constructor - 2+ itemsets
	 * @param items an array of items that should be added to the new itemset
	 * @param luv an array of local utility value of items to the Itemset
	 */
	public UItemset(int [] itemset, int [] luv, int itemsetUtility){
		uItemset = new int[itemset.length];
		utilityArray = new int[uItemset.length];
		// copy array from items to uItemset 
		for(int i = 0; i < itemset.length; i++){
			uItemset[i] = itemset[i];
			utilityArray[i] = luv[i];
		}
		
        utility = itemsetUtility;
	}
	
	/**
	 * clone an exactly same UItemset
	 * @param other
	 * @return a copy of UItemset other
	 */
	public UItemset clone(UItemset other){
		return new UItemset(other.uItemset, other.utilityArray, other.utility);
	}
	
	/**
	 * Sort the items in ascending order
	 * @return a sorted UItemset
	 */
	public UItemset sort(){
		int greater;
        int[] ordered = new int[uItemset.length];
        int[] index = new int[ordered.length]; 
        //imperfect number ordering algorithm
        for(int indexL=0;indexL<uItemset.length;indexL++)
        {
            greater=0;
            for(int indexR=0;indexR<uItemset.length;indexR++)
            {
                if(uItemset[indexL] > uItemset[indexR])
                {
                    greater++;
                }
            }
            while (ordered[greater] == uItemset[indexL]) {
                greater++;
            }
           ordered[greater] = uItemset[indexL];
           index[greater] = indexL;
        }// done sorting
        
        int [] sortUtil = new int[utilityArray.length];
        
        for(int i = 0; i < sortUtil.length; i++){
        	sortUtil[i] = utilityArray[index[i]];
        }
        
        return new UItemset(ordered, sortUtil, utility);
	}
	
	/**
	 * Get the items as array
	 * @return the items
	 */
	public int[] getUItemset() {
		return uItemset;
	}
	
	/**
	 * return the string format of the itemset
	 * @return String
	 */
	public String getUItemsetString(){
		StringBuilder itemset = new StringBuilder();
		for(int e : this.getUItemset()){
			itemset.append(e);
			itemset.append(" ");
		}
		
		return itemset.toString().trim();
		
	}
	/**
	 * Get the utility of this itemset 
	 * @return the utility of the itemset
	 */
	public int getUtility(){
		return utility;
	}
	/**
	 * Get the utility array of this itemset
	 */
	public int[] getUtilityArray(){
		return utilityArray;		
	}
	

	/**
	 * Get the size of this itemset 
	 */
	public int size() {
		return uItemset.length;
	}

	/**
	 * Get the item at a given position in this itemset
	 */
	public Integer get(int position) {
		return uItemset[position];
	}

	/**
	 * Get the local value of the item at a given position in this itemset
	 */
	public Integer getluv(int position) {
		return utilityArray[position];
	}
	
	/**
	 * compare two itemsets if they are the same (Note all items are in the same order)
	 */
	public boolean equalTo(UItemset Y){
		boolean equal = false;
		
		if(this.size() == Y.size()){
		   int itemsetX[] = this.getUItemset();
		   int itemsetY[] = Y.getUItemset();
		   for(int i = 0; i < this.size(); i++){
			   if(itemsetX[i] != itemsetY[i]){
				  return equal; 
			   }				   
		   }
		   equal = true;
		
		}
		return equal;
		
	}
	
	/**
	 * return a list of every item as 1-itemset from the UItemset
	 * @return a list of 1-UItemset
	 */
	public List<UItemset> getUItemsetList(){
		List<UItemset> uList = new ArrayList<UItemset>();
		for(int i = 0; i < this.size(); i++){
			uList.add(new UItemset(uItemset[i], utilityArray[i], utilityArray[i]));
		}
		return uList;
	}
	
	/**
	 * Make a copy of this itemset but exclude a given index from the beginning(index[0])
	 * @param itemsIndexToRemove the given index (range 0 to itemset.length - 2)
	 * @return the copy
	 */
	public UItemset cloneItemSetMinusItemsToIndex(int itemsIndexToRemove) {
		if(itemsIndexToRemove > uItemset.length - 2)
			return null;
		// create the new itemset
		int[] newItemset = new int[uItemset.length - itemsIndexToRemove - 1];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;
		
		int j = 0;
		for(int i = itemsIndexToRemove + 1; i < uItemset.length; i++){
			newItemset[j]= uItemset[i];
			newUtilityArray[j] = utilityArray[i];
			newUtility += utilityArray[i];
			j++;			
		}
		
		return new UItemset(newItemset, newUtilityArray, newUtility); // return the copy
	}
	
	/**
	 * Make a copy of this itemset but keep a given index from the beginning(index[0]) till
	 * @param itemsIndexToRemove the given index (range 0 to itemset.length - 2)
	 * @return the copy
	 */
	public UItemset cloneItemSetKeepItemsTillIndex(int itemsIndexToKeep) {
		if(itemsIndexToKeep > uItemset.length - 2)
			return null;
		// create the new itemset
		int[] newItemset = new int[itemsIndexToKeep + 1];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;
		
		int j = 0;
		for(int i = 0; i < itemsIndexToKeep + 1; i++){
			newItemset[j]= uItemset[i];
			newUtilityArray[j] = utilityArray[i];
			newUtility += utilityArray[i];
			j++;			
		}
		
		return new UItemset(newItemset, newUtilityArray, newUtility); // return the copy
	}
	/**
	 * Make a copy of this itemset but exclude a given item
	 * @param itemToRemove the given item
	 * @return the copy
	 */
	public UItemset cloneItemSetMinusOneItem(Integer itemToRemove) {

		// create the new itemset
		int[] newItemset = new int[uItemset.length -1];
		int[] newUtilityArray = new int[uItemset.length -1];
		int newUtility = 0;
		int i=0;
		// for each item in this itemset
		for(int j = 0; j < uItemset.length; j++){
			// copy the item except if it is the item that should be excluded
			if(uItemset[j] != itemToRemove){
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				i++;
			}
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); // return the copy
	}
		
	/**
	 * Make a copy of this itemset but exclude a set of items
	 * @param itemsetToNotKeep the set of items to be excluded
	 * @return the copy
	 */
	public UItemset cloneItemSetMinusAnItemset(UItemset itemsetToNotKeep) {
		// create a new itemset
		int[] newItemset = new int[uItemset.length - itemsetToNotKeep.size()];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;
		int i=0;
		// for each item of this itemset
		for(int j = 0; j < uItemset.length; j++){
			// copy the item except if it is not an item that should be excluded
			if(itemsetToNotKeep.contains(uItemset[j]) == false){
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				i++;
			}
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); // return the copy
	}
	
	/**
	 * check if this uitemset contains the item
	 * @param item
	 * @return
	 */
	public boolean contains(Integer item){
		for(int i = 0; i < uItemset.length; i++){
			if(uItemset[i] == item){
				return true;
			}
		}
		
		return false;		
	}
	
	/**
	 * This method return an itemset containing items that are included
	 * in this itemset and in a given itemset(#works only if items can be arbitrarily ordered in each transaction)
	 * @param itemset2 the given itemset
	 * @return the new itemset
	 */
	public UItemset intersection(UItemset itemset2) {
		int[] array1 = this.getUItemset();
		int[] array2 = itemset2.getUItemset();
		
		int[] array1utilary = this.getUtilityArray();
		int newUtility = 0;
		// create a new array having the smallest size between the two arrays
	    final int newArraySize = (array1.length < array2.length) ? array1.length : array2.length;
	    int[] newArray = new int[newArraySize];
        //ArrayList<Integer> unitedArray = new ArrayList<Integer>();
	    int[] newUtilityArray = new int[newArraySize];
	    int pos1 = 0;
	    int pos2 = 0;
	    int posNewArray = 0;
	    while(pos1 < array1.length && pos2 < array2.length) {
	    	if(array1[pos1] < array2[pos2]) {
	    		pos1++;
	    	}else if(array2[pos2] < array1[pos1]) {
	    		pos2++;
	    	}else { // if they are the same
	    		newArray[posNewArray] = array1[pos1];
	    		newUtilityArray[posNewArray] = array1utilary[pos1];
	    		posNewArray++;
	    		pos1++;
	    		pos2++;
	    	}
	    }
	    // return the subrange of the new array that is full.
	    int [] intersection = Arrays.copyOfRange(newArray, 0, posNewArray); 
		int [] intersectionUtilityArray = Arrays.copyOfRange(array1utilary, 0, posNewArray); 
		for(int i: intersectionUtilityArray){
			newUtility += i;
		}
		
		return new UItemset(intersection, intersectionUtilityArray, newUtility);
	}
	
	/**Return a subset of a UItemset based on the name
	 * @param itemset name of the Subset in String
	 * @return a UItemset
	 */
	public UItemset getSubset(String itemset){
		int [] uItems = uItemset;
		int [] utilityAry = utilityArray;
		String[] items = itemset.trim().split(" ");
		//items = sortStrArray(items).split(" ");
		
		int [] itemComb = new int[items.length];
		int [] utilComb = new int[items.length];
		int TU = 0;
		// validation to be implemented
		for(int i = 0; i < items.length; i++){
			for(int j = 0; j < uItems.length; j++){
				if(Integer.parseInt(items[i]) == uItems[j]){
					itemComb[i] = uItems[j];
					utilComb[i]	= utilityAry[j];
					TU += utilityAry[j];
				}
			}
	    }
		
		return new UItemset(itemComb, utilComb, TU);
	}
	
	/**
	 * generate a list of half subset of the given itemset
	 * @param itemset
	 * @return a list of half subset
	 */
	public List<UItemset> getHalfSubset(){
		List<UItemset> halfSubset = new LinkedList<UItemset>();
		
		Integer[] items = new Integer[uItemset.length];
		int [] uItems = uItemset;
		int [] utilityAry = utilityArray;
		
		for(int i = 0; i < items.length; i++){
			items[i] = new Integer(uItems[i]);
		}

		Combination setSort = Combination.getInstance();
		
		List<List<Object[]>> sumlist = setSort.halfSubset(items);
		
		for(List<Object[]> list:sumlist)
		for(Object[] each: list){
			int [] itemComb = new int[each.length];
			int [] utilComb = new int[each.length];
			int TU = 0;
			for(int i = 0; i < each.length; i++){
				for(int j = 0; j < uItems.length; j++){
					if(((Integer) each[i]).intValue() == uItems[j]){
						itemComb[i] = uItems[j];
						utilComb[i]	= utilityAry[j];
						TU += utilityAry[j];
					}

				}
		    }
			halfSubset.add(new UItemset(itemComb, utilComb, TU));
		}
		
		setSort.clearSet();
		return halfSubset;
	}
	
	/**
	 * generate a list of full subset of the given itemset
	 * @param itemset
	 * @return a list of full subset
	 */
	public List<UItemset> getSubset(){
		List<UItemset> sublist = new LinkedList<UItemset>();
		
		Integer[] items = new Integer[uItemset.length];
		int [] uItems = uItemset;
		int [] utilityAry = utilityArray;
		
		for(int i = 0; i < items.length; i++){
			items[i] = new Integer(uItems[i]);
		}
		
		//Combination setSort = Combination.getInstance();	
		
		List<Object[]> list = Combination.subsetList(items); //.subset(items);
		
		for(Object[] each: list){
			int [] itemComb = new int[each.length];
			int [] utilComb = new int[each.length];
			int TU = 0;
			for(int i = 0; i < each.length; i++){
				for(int j = 0; j < uItems.length; j++){
					if(((Integer) each[i]).intValue() == uItems[j]){
						itemComb[i] = uItems[j];
						utilComb[i]	= utilityAry[j];
						TU += utilityAry[j];
					}

				}
		    }
			sublist.add(new UItemset(itemComb, utilComb, TU));
		}
		
		//setSort.clearSet();
		
		return sublist;
	}
	
	/**
	 * generate a list of full subset of the given itemset
	 * @param itemset
	 * @return a list of full subset
	 */
	public String getSubsetPrint(){
		
		Integer[] items = new Integer[uItemset.length];
		int [] uItems = uItemset;
		
		
		for(int i = 0; i < items.length; i++){
			items[i] = new Integer(uItems[i]);
		}
		
		//Combination setSort = Combination.getInstance();	
		
		String list = Combination.subsetListPrint(items); //.subset(items);
		
     	//setSort.clearSet();
		
		return list;
	}
	/**
	 * generate a list of full subset of the given itemset
	 * @param itemset
	 * @return a list of full subset
	 */
	public List<List<UItemset>> getSubset(Combination set){
		List<List<UItemset>> sublist = new ArrayList<List<UItemset>>();
		
		Integer[] items = new Integer[uItemset.length];
		int [] uItems = uItemset;
		int [] utilityAry = utilityArray;
		
		for(int i = 0; i < items.length; i++){
			items[i] = new Integer(uItems[i]);
		}
		
		Combination setSort = set;	
		
		List<List<Object[]>> sumlist = setSort.subset(items);
		
		for(List<Object[]> list:sumlist){
			List<UItemset> alist = new ArrayList<UItemset>();
			for(Object[] each: list){
				int [] itemComb = new int[each.length];
				int [] utilComb = new int[each.length];
				int TU = 0;
				for(int i = 0; i < each.length; i++){
					for(int j = 0; j < uItems.length; j++){
						if(((Integer) each[i]).intValue() == uItems[j]){
							itemComb[i] = uItems[j];
							utilComb[i]	= utilityAry[j];
							TU += utilityAry[j];
						}

					}
				}
				alist.add(new UItemset(itemComb, utilComb, TU));
			}
			sublist.add(alist);
		}
		
		return sublist;
	}
	/*
	@SuppressWarnings("unchecked")
	private void generateAllNonEmptySubsets(Vector itemsVector, int level,
			Set allNonEmptySubsets, UItemset currentItemset) {
		// make a copy of the currentItemset
		currentItemset = clone(currentItemset);
		// this loop always has two iterations only
		// in first iteration the item at itemsVector is not inlcuded in
		// currentItemset, in the second it is
		boolean itemAdded = false;
		while (true) {
			if (level == itemsVector.size() - 1) {
				// check if it's a proper subset before adding
				if (currentItemset.size() != 0
						&& currentItemset.size() != itemsVector.size()) {
					allNonEmptySubsets.add(currentItemset);
				}
			} else {
				generateAllNonEmptySubsets(itemsVector, level + 1,
						allNonEmptySubsets, currentItemset);
			}
			if (itemAdded) {
				break;
			} else {
				// have to copy before adding to avoid modifying itemsets
				// already added to allNonEmptySubsets
				currentItemset = clone(currentItemset);
				currentItemset = currentItemset.addUItem((UItemset) itemsVector.elementAt(level));
				itemAdded = true;
			}
		}
	}
	*/
	/**
	 * 
	 * @return a set of all subsets of the current UItemset, except empty subset
	 *         and the current itemset itself
	 */
	/*
	public Set generateAllNonEmptySubsets() {
		@SuppressWarnings("rawtypes")
		HashSet allNonEmptySubsets = new HashSet();
		generateAllNonEmptySubsets(new Vector(uItemset), 0, allNonEmptySubsets,
				new UItemset());
		return allNonEmptySubsets;
	}
	*/
	/**
	 * Add 1-itemset or item to the UItemset and return a new UItemset
	 * The insertion order is based on smallest to largest
	 * @param itemToAdd 1-itemset
	 * @return a new UItemset with added item
	 */
	public UItemset addUItem(UItemset itemToAdd){
		// create the new itemset
				int[] newItemset = new int[uItemset.length +1];
				int[] newUtilityArray = new int[uItemset.length +1];
				int newUtility = 0;
				int i = 0;
				int isAdd = 0;
				// for each item in this itemset
				for(int j = 0; j < uItemset.length; j++){
					// copy the item except if it is the item that should be excluded
					if(uItemset[j] < itemToAdd.uItemset[0]){
						newItemset[i] = uItemset[j];
						newUtilityArray[i] = utilityArray[j];
						newUtility += utilityArray[j];
					}else{
						newItemset[i] = itemToAdd.uItemset[0];
						newUtilityArray[i] = itemToAdd.utilityArray[0];
						newUtility += itemToAdd.utility;
						isAdd++;
					}
					i++;
				}
				
				if(isAdd > 0){	
					newItemset[i] = uItemset[i-1];
					newUtilityArray[i] = utilityArray[i-1];
					newUtility += utilityArray[i-1];
				}else{
					newItemset[i] = itemToAdd.uItemset[0];
					newUtilityArray[i] = itemToAdd.utilityArray[0];
					newUtility += itemToAdd.utility;
				}
		   return new UItemset(newItemset, newUtilityArray, newUtility);
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
	
	
}
