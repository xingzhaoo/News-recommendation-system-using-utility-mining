package dataUnit;

import java.text.DecimalFormat;

/**
 * This class represent an association rule, where itemsets are arrays of
 * integers.
 * 
 * @see UItemset
 * @see Rules
 * @author Xing Zhao revised from Philippe Fournier-Viger
 */
public class Rule {
	/** antecedent */
	private int[] itemset1;
	/** consequent */
	private int[] itemset2;
	/** rule utility array */
	private int[] utilityset;
	/** potentialUtility (utility of the consequent) */
	private int potentialUtility;
	/** utility of the itemset */
	private int itemsetUtility;
	/** confidence of the rule */
	private double confidence;

	/**
	 * Constructor
	 * 
	 * @param itemset1
	 *            the antecedent of the rule (an itemset)
	 * @param itemset2
	 *            the consequent of the rule (an itemset)
	 * @param potentialUtility
	 *            the support of the consequent as a number of transactions
	 * @param patternUtility
	 *            the Utility of the itemset1 AND itemset2
	 * @param confidence
	 *            the confidence of the rule
	 */
	public Rule(int[] itemset1, int[] itemset2, int[] utilityset,
			int potentialUtility, int patternUtility, double confidence) {
		this.itemset1 = itemset1;
		this.itemset2 = itemset2;
		this.utilityset = utilityset;
		this.potentialUtility = potentialUtility;
		this.itemsetUtility = patternUtility;
		this.confidence = confidence;
	}

	/**
	 * Constructor compatible for UItemset
	 * 
	 * @param X
	 *            the antecedent of the rule
	 * @param Y
	 *            the consequent of the rule
	 * @param potentialUtility
	 *            the support of the consequent as a number of transactions
	 * @param patternUtility
	 *             the Utility of the itemset1 AND itemset2
	 * @param confidence
	 *            the confidence of the rule
	 */
	public Rule(UItemset X, UItemset Y, int potentialUtility,
			int patternUtility, double confidence) {
		this.itemset1 = X.getUItemset();
		this.itemset2 = Y.getUItemset();
		this.utilityset = UnionUtilitySet(X, Y);
		this.potentialUtility = potentialUtility;
		this.itemsetUtility = patternUtility;
		this.confidence = confidence;
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
	private int[] UnionUtilitySet(UItemset x, UItemset y) {

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

	/**
	 * Get the left itemset of this rule (antecedent).
	 * 
	 * @return an itemset.
	 */
	public int[] getItemset1() {
		return itemset1;
	}

	/**
	 * Get the right itemset of this rule (consequent).
	 * 
	 * @return an itemset.
	 */
	public int[] getItemset2() {
		return itemset2;
	}

	/**
	 * Get the Rule Utility Array
	 * 
	 * @return the utility array
	 */
	public int[] getRuleUtility() {
		return utilityset;
	}

	/**
	 * Get the total utility of this rule (integer).
	 * 
	 * @return the absolute support.
	 */
	public int getTotalUtility() {
		return itemsetUtility;
	}

	/**
	 * Get the confidence of this rule.
	 * 
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Get the potentialUtility of the rule (the utility of the rule consequent)
	 * 
	 * @return the potentialUtility (int)
	 */
	public int getpotentialUtility() {
		return potentialUtility;
	}

	public void sortItemset(){
		int set1[] = this.itemset1;
		int set2[] = this.itemset2;
		int utility[] = this.utilityset;
		int numSet1 = set1.length - 1;
		// sort itemset1
		for (int i = 0; i < set1.length; i++){
		   for(int j = 0; j < set1.length - 1; j++){
		       if(set1[i] < set1[j+1]){
		          int temp = set1[j + 1];
		          int util1 = utility[j + 1];
		          
		          set1[j + 1] = set1[i];
		          set1[i] = temp;
		          
		          utility[j + 1] = utility[i];
		          utility[i] = util1;
		       }
		   }
	    }
		// sort itemset2
		for (int i = 0; i < set2.length; i++){
		    for(int j = 0; j < set2.length - 1; j++){
			    if(set2[i] > set2[j + 1]){
			       int temp = set2[j + 1];
			       int util2 = utility[numSet1 + j + 1];
			          
			       set2[j + 1] = set2[i];
			       set2[i] = temp;
			          
			       utility[numSet1 + j + 1] = utility[numSet1 + i];
			       utility[numSet1 + i] = util2;
			   }
		    }
		}
	}
	
	/**
	 * Compare two rules if they are equal
	 * @param other other rule
	 * @return true if both are equal
	 */
	public boolean equals(Rule other){
		if(this.itemset1.length != other.itemset1.length){
			return false;
		}
		
		if(this.itemset2.length != other.itemset2.length){
			return false;
		}

		for(int i = 0; i < this.itemset1.length; i++){
			if(this.itemset1[i] != other.itemset1[i]){
				return false;
			}
		}
		
		for(int i = 0; i < this.itemset2.length; i++){
			if(this.itemset2[i] != other.itemset2[i]){
				return false;
			}
		}
		
		for(int i = 0; i < this.utilityset.length; i++){
			if(this.utilityset[i] != other.utilityset[i]){
				return false;
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * Print this rule to System.out.
	 */
	public void print() {
		System.out.println(toString());
	}

	/**
	 * Return a String representation of this rule
	 * 
	 * @return a String
	 */
	public String toString() {
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

		buffer.append("#Rule Utility: ");
		for (int i = 0; i < utilityset.length; i++) {
			buffer.append(utilityset[i]);
			if (i != utilityset.length - 1) {
				buffer.append(" ");
			}
		}

		buffer.append("%");

		// write separator
		buffer.append(" #PotentialUtility: ");
		// write support
		buffer.append(potentialUtility);
		// write separator
		buffer.append(" #PatternUtility: ");
		// write support
		buffer.append(itemsetUtility);
		// write separator
		buffer.append(" #UCONF: ");
		// write confidence
		buffer.append(doubleToString(confidence));
		return buffer.toString();
	}

	/**
	 * Convert a double value to a string with only five decimal
	 * 
	 * @param value
	 *            a double value
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
