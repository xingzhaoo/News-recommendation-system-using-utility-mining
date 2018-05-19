package dataUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents an itemset (a set of items) implemented as an array of
 * integers with its associated UtilityArray of the itemset and a variable to
 * store the support count of the itemset.
 * 
 * @author Xing Zhao
 */

public class UItemset {
	/** the array of items **/
	protected int[] uItemset;
	/** the Utility array of items using map **/
	protected int[] utilityArray;
	/** the Utility of this UItemset */
	protected int utility;

	/**
	 * Constructor
	 */
	public UItemset() {
		uItemset = new int[] {};
		utilityArray = new int[] {};
		utility = 0;
	}

	/**
	 * Constructor - 1-itemsets
	 * 
	 * @param item
	 *            an item that should be added to the new itemset
	 * @param uv
	 *            the utility value of the item
	 */
	public UItemset(int item, int luv, int utilityItem) {

		uItemset = new int[] { item };
		utilityArray = new int[] { luv };
		utility = utilityItem;
	}

	/**
	 * Constructor - 2+ itemsets
	 * 
	 * @param items
	 *            an array of items that should be added to the new itemset
	 * @param luv
	 *            an array of local utility value of items to the Itemset
	 */
	public UItemset(int[] itemset, int[] luv, int itemsetUtility) {
		uItemset = new int[itemset.length];
		utilityArray = new int[uItemset.length];
		// copy array from items to uItemset
		for (int i = 0; i < itemset.length; i++) {
			uItemset[i] = itemset[i];
			utilityArray[i] = luv[i];
		}

		utility = itemsetUtility;
	}

	/**
	 * Constructor - 2+ itemsets, auto sum up utility based on luv set
	 * 
	 * @param items
	 *            an array of items that should be added to the new itemset
	 * @param luv
	 *            an array of local utility value of items to the Itemset
	 */
	public UItemset(int[] itemset, int[] luv) {
		uItemset = new int[itemset.length];
		utilityArray = new int[uItemset.length];
		utility = 0;
		// copy array from items to uItemset
		for (int i = 0; i < itemset.length; i++) {
			uItemset[i] = itemset[i];
			utilityArray[i] = luv[i];
			utility += luv[i];
		}
	}

	/**
	 * clone an exactly same UItemset
	 * 
	 * @return a deep copy of this UItemset
	 */
	public UItemset clone() {
		return new UItemset(uItemset, utilityArray, utility);
	}

	/**
	 * Sort the items in ascending order based on integer value of item
	 * 
	 * @return a sorted itemset based on integer value of an item
	 */
	public UItemset sort() {
		int greater;
		int[] ordered = new int[uItemset.length];
		int[] index = new int[ordered.length];
		// imperfect number ordering algorithm
		for (int indexL = 0; indexL < uItemset.length; indexL++) {
			greater = 0;
			for (int indexR = 0; indexR < uItemset.length; indexR++) {
				if (uItemset[indexL] > uItemset[indexR]) {
					greater++;
				}
			}
			while (greater < uItemset.length && ordered[greater] == uItemset[indexL]) {
				greater++;
			}
			if (greater < uItemset.length) {
				ordered[greater] = uItemset[indexL];
				index[greater] = indexL;
			}
		} // done sorting

		int[] sortUtil = new int[utilityArray.length];

		for (int i = 0; i < sortUtil.length; i++) {
			sortUtil[i] = utilityArray[index[i]];
		}

		return new UItemset(ordered, sortUtil, utility);
	}

	/**
	 * Sort the items in ascending order based on their local utility
	 * 
	 * @return a sorted UItemset in ascending order based on the local utility
	 */
	public UItemset uSort() {
		int greater;
		int[] ordered = new int[uItemset.length]; // sorted array
		// memo array stores the sorted order of array index
		int[] index = new int[ordered.length]; 
		/* number ordering algorithm */
		for (int indexL = 0; indexL < uItemset.length; indexL++) {
			greater = 0;
			for (int indexR = 0; indexR < uItemset.length; indexR++) {
				if (utilityArray[indexL] > utilityArray[indexR]) {
					greater++;
				}
			}
			while (greater < uItemset.length && ordered[greater] == utilityArray[indexL]) {
				greater++;
			}
			if (greater < uItemset.length) {
				ordered[greater] = utilityArray[indexL];
				index[greater] = indexL;
			}
		} // done sorting

		int[] sortItem = new int[utilityArray.length];

		for (int i = 0; i < sortItem.length; i++) {
			sortItem[i] = uItemset[index[i]];
		}

		return new UItemset(sortItem, ordered, utility);
	}

	/**
	 * Get the items as array
	 * 
	 * @return the items
	 */
	public int[] getUItemset() {
		return uItemset;
	}

	/**
	 * return the string format of the itemset
	 * 
	 * @return String
	 */
	public String getUItemsetString() {
		StringBuilder itemset = new StringBuilder();
		for (int e : this.getUItemset()) {
			itemset.append(e);
			itemset.append(" ");
		}

		return itemset.toString().trim();
	}

	/**
	 * Get the utility of this itemset
	 * 
	 * @return the utility of the itemset
	 */
	public int getUtility() {
		return utility;
	}

	/**
	 * Get the utility array of this itemset
	 */
	public int[] getUtilityArray() {
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
	 * Compare two itemsets if they are exact the same. Local utility of itemset
	 * is not involved. (Require all items are sorted in the same order)
	 */
	public boolean equalTo(UItemset Y) {
		boolean equal = false;

		if (this.size() == Y.size()) {
			int itemsetX[] = this.getUItemset();
			int itemsetY[] = Y.getUItemset();
			for (int i = 0; i < this.size(); i++) {
				if (itemsetX[i] != itemsetY[i]) {
					return equal;
				}
			}
			equal = true;
		}
		return equal;
	}

	/**
	 * Return a list of every item as 1-itemset from the UItemset
	 * 
	 * @return a list of 1-UItemset
	 */
	public List<UItemset> getUItemsetList() {
		List<UItemset> uList = new ArrayList<UItemset>();
		for (int i = 0; i < this.size(); i++) {
			uList.add(new UItemset(uItemset[i], utilityArray[i], utilityArray[i]));
		}
		return uList;
	}

	/**
	 * Make a copy of this itemset but exclude a given index from the
	 * beginning(index[0]) keep the left side of index
	 * 
	 * @param itemsIndexToRemove
	 *            the given index (range 0 to itemset.length - 2)
	 * @return the copy
	 */
	public UItemset cloneItemSetMinusItemsToIndex(int itemsIndexToRemove) {
		if (itemsIndexToRemove > uItemset.length - 2)
			return null;
		// create the new itemset
		int[] newItemset = new int[uItemset.length - itemsIndexToRemove - 1];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;

		int j = 0;
		for (int i = itemsIndexToRemove + 1; i < uItemset.length; i++) {
			newItemset[j] = uItemset[i];
			newUtilityArray[j] = utilityArray[i];
			newUtility += utilityArray[i];
			j++;
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); 
	}

	/**
	 * Make a copy of this itemset but keep a given index from the
	 * beginning(index[0]) till the specified index
	 * (a.k.a keep the right side of the index)
	 * 
	 * @param itemsIndexToKeep
	 *             index of the itemset (range from 0 to size of the itemset - 2)
	 * @return the copy
	 */
	public UItemset cloneItemSetKeepItemsTillIndex(int itemsIndexToKeep) {
		if (itemsIndexToKeep > uItemset.length - 2)
			return null;
		// create the new itemset
		int[] newItemset = new int[itemsIndexToKeep + 1];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;

		int j = 0;
		for (int i = 0; i < itemsIndexToKeep + 1; i++) {
			newItemset[j] = uItemset[i];
			newUtilityArray[j] = utilityArray[i];
			newUtility += utilityArray[i];
			j++;
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); 
	}

	/**
	 * Make a copy of this itemset but exclude a given item
	 * 
	 * @param itemToRemove
	 *             item name
	 * @return a copy of this itemset but excluding the given item 
	 */
	public UItemset cloneItemSetMinusOneItem(Integer itemToRemove) {

		// create the new itemset
		int[] newItemset = new int[uItemset.length - 1];
		int[] newUtilityArray = new int[uItemset.length - 1];
		int newUtility = 0;
		int i = 0;
		// for each item in this itemset
		for (int j = 0; j < uItemset.length; j++) {
			// copy the item except if it is the item that should be excluded
			if (uItemset[j] != itemToRemove) {
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				i++;
			}
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); 
	}

	/**
	 * Make a copy of this itemset but exclude a set of items
	 * 
	 * @param itemsetToNotKeep
	 *            the set of items to be excluded
	 * @return the copy of this itemset but excluding the given set of items
	 */
	public UItemset cloneItemSetMinusAnItemset(UItemset itemsetToNotKeep) {
		// create a new itemset
		int[] newItemset = new int[uItemset.length - itemsetToNotKeep.size()];
		int[] newUtilityArray = new int[newItemset.length];
		int newUtility = 0;
		int i = 0;
		// for each item of this itemset
		for (int j = 0; j < uItemset.length; j++) {
			// copy the item except if it is not an item that should be excluded
			if (itemsetToNotKeep.contains(uItemset[j]) == false) {
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				i++;
			}
		}

		return new UItemset(newItemset, newUtilityArray, newUtility); 
	}

	/**
	 * check if this itemset contains a given item
	 * 
	 * @param item in integer format
	 * @return true if item is in this itemset
	 */
	public boolean contains(Integer item) {
		for (int i = 0; i < uItemset.length; i++) {
			if (uItemset[i] == item) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method return an itemset containing items that are included in this
	 * itemset and in a given itemset(#works only if items can be arbitrarily
	 * ordered in each transaction)
	 * 
	 * @param itemset2
	 *            the given itemset
	 * @return a new itemset
	 */
	public UItemset intersection(UItemset itemset2) {
		int[] array1 = this.getUItemset();
		int[] array2 = itemset2.getUItemset();

		int[] array1utilary = this.getUtilityArray();
		int newUtility = 0;
		// create a new array having the smallest size between the two arrays
		final int newArraySize = (array1.length < array2.length) ? array1.length : array2.length;
		int[] newArray = new int[newArraySize];
		int[] newUtilityArray = new int[newArraySize];
		int pos1 = 0;
		int pos2 = 0;
		int posNewArray = 0;
		while (pos1 < array1.length && pos2 < array2.length) {
			if (array1[pos1] < array2[pos2]) {
				pos1++;
			} else if (array2[pos2] < array1[pos1]) {
				pos2++;
			} else { // if they are the same
				newArray[posNewArray] = array1[pos1];
				newUtilityArray[posNewArray] = array1utilary[pos1];
				posNewArray++;
				pos1++;
				pos2++;
			}
		}
		// return the subrange of the new array that is full.
		int[] intersection = Arrays.copyOfRange(newArray, 0, posNewArray);
		int[] intersectionUtilityArray = Arrays.copyOfRange(array1utilary, 0, posNewArray);
		for (int i : intersectionUtilityArray) {
			newUtility += i;
		}

		return new UItemset(intersection, intersectionUtilityArray, newUtility);
	}

	/**
	 * Given the item name of this itemset, return a UItemset which is a subset
	 * of this itemset
	 * 
	 * @param itemset
	 *            name of the Subset in String
	 * @return a UItemset
	 */
	public UItemset getSubsetByName(String itemset) {
		int[] uItems = uItemset;
		int[] utilityAry = utilityArray;
		String[] items = itemset.trim().split(" ");

		int[] itemComb = new int[items.length];
		int[] utilComb = new int[items.length];
		int TU = 0;
		// validation to be implemented
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < uItems.length; j++) {
				if (Integer.parseInt(items[i]) == uItems[j]) {
					itemComb[i] = uItems[j];
					utilComb[i] = utilityAry[j];
					TU += utilityAry[j];
				}
			}
		}

		return new UItemset(itemComb, utilComb, TU);
	}

	/**
	 * Add 1-itemset or item to the UItemset and return a new UItemset The
	 * insertion order is from smallest to largest based on items
	 * 
	 * @param oneitemToAdd
	 *            1-itemset
	 * @return a new UItemset with added item 
	 *         (a union of this itemset and oneitemToAdd)
	 */
	public UItemset addItem(UItemset oneitemToAdd) {
		// create the new itemset
		int[] newItemset = new int[uItemset.length + 1];
		int[] newUtilityArray = new int[uItemset.length + 1];
		int newUtility = 0;
		int i = 0;
		int isAdd = 0;
		// for each item in this itemset (insertion sort)
		for (int j = 0; j < uItemset.length; j++) {
			// copy the item except if it is the item that should be excluded
			if (uItemset[j] > oneitemToAdd.uItemset[0] && isAdd == 0) {
				// only insert once
				newItemset[i] = oneitemToAdd.uItemset[0];
				newUtilityArray[i] = oneitemToAdd.utilityArray[0];
				newUtility += oneitemToAdd.utility;
				++i; // move to next position in new array
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				isAdd++;
			} else {
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
			}
			i++; // move to next position in new array
		}
		/* post process for added or not added cases */
		if (isAdd > 0) { // new item added
			i--;
			newItemset[i] = uItemset[i - 1];
			newUtilityArray[i] = utilityArray[i - 1];
			newUtility += utilityArray[i - 1];
		} else { // new item not added
			newItemset[i] = oneitemToAdd.uItemset[0];
			newUtilityArray[i] = oneitemToAdd.utilityArray[0];
			newUtility += oneitemToAdd.utility;
		}

		return new UItemset(newItemset, newUtilityArray, newUtility);
	}

	/**
	 * Add 1-itemset or item to the UItemset and return a new UItemset The
	 * insertion order is from smallest to largest based on utility of items
	 * 
	 * @param oneitemToAdd
	 *            1-itemset
	 * @return a new UItemset with added item
	 *         (a union of this itemset and oneitemToAdd)
	 */
	public UItemset addUItem(UItemset oneitemToAdd) {
		// create the new itemset
		int[] newItemset = new int[uItemset.length + 1];
		int[] newUtilityArray = new int[uItemset.length + 1];
		int newUtility = 0;
		int i = 0;
		int isAdd = 0;
		// for each item in this itemset (insertion sort)
		for (int j = 0; j < uItemset.length; j++) {
			/*
			 * copy the item except if it is the item that should be excluded
			 * new item is added if its utility is less than this item
			 */
			if (utilityArray[j] > oneitemToAdd.utilityArray[0] && isAdd == 0) {
				// only insert once	
				newItemset[i] = oneitemToAdd.uItemset[0];
				newUtilityArray[i] = oneitemToAdd.utilityArray[0];
				newUtility += oneitemToAdd.utility;
				++i; // move to next position in new array
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
				isAdd++;
			} else {
				newItemset[i] = uItemset[j];
				newUtilityArray[i] = utilityArray[j];
				newUtility += utilityArray[j];
			}
			i++; // move to next position in new array
		}
		/* post process for added or not added cases */
		if (isAdd > 0) { // new item added
			i--;
			newItemset[i] = uItemset[i - 1];
			newUtilityArray[i] = utilityArray[i - 1];
			newUtility += utilityArray[i - 1];
		} else { // new item not added
			newItemset[i] = oneitemToAdd.uItemset[0];
			newUtilityArray[i] = oneitemToAdd.utilityArray[0];
			newUtility += oneitemToAdd.utility;
		}

		return new UItemset(newItemset, newUtilityArray, newUtility);
	}
}
