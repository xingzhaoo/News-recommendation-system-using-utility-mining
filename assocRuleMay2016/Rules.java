import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a list of association rules, where itemsets are array of integers.
* 
*  @see UItemset
*  @see Rule
 * @author Xing Zhao revised from Philippe Fournier-Viger
 */

public class Rules {
	/** a list of association rules */
	private final List<Rule> rules = new ArrayList<Rule>();  // rules
	/** a name that an algorithm can give to this list of association rules */
	private final String name;
	/** the minimum utility confidence user defined */
	private final double uconf;
	/**
	 * Sort the rules by confidence
	 */
	public void sortByConfidence(){
		Collections.sort(rules, new Comparator<Rule>() {
			public int compare(Rule r1, Rule r2) {
				return (int)((r2.getConfidence() - r1.getConfidence() ) * Integer.MAX_VALUE);
			}
		});
	}
	
	/**
	 * Constructor
	 * @param name  a name for this list of association rules (string)
	 */
	public Rules(String name, double minUconf){
		this.name = name;
		this.uconf = minUconf;
	}
	
	public int size(){
	    return rules.size();	
	}
	/**
	 * Print all the rules in this list to System.out.
	 * @param databaseSize the number of transactions in the transaction database where the rules were found
	 */
	public void printRules(){
		System.out.println("           ------- " + name + " -------             ");
		int i=0;
		for(Rule rule : rules){
			System.out.print("  rule " + i + ":  " + rule.toString());
			/*System.out.print(" potential value / total Utility of the rule :  " + 
					" (" + rule.getpotentialUtility() + "/" + rule.getTotalUtility() + ") ");
			System.out.print("confidence :  " + rule.getConfidence());*/
			System.out.println("");
			i++;
		}
		System.out.println(" --------------------------------");
	}
	
	/**
	 * Check if the rules list has rule already
	 * @param rule , rule to compare
	 * @return true of rule is in the rules list
	 */
	public boolean contains(Rule other){
		
		for(Rule e: this.rules){
			if(e.equals(other)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a string representation of this list of rules
	 * @param databaseSize the number of transactions in the database where the rules were found.
	 * @return a string
	 */
	public String toString(){
		// create a string buffer
		StringBuilder buffer = new StringBuilder(); //(" ------------------ ");
		//buffer.append(name);
		//buffer.append(" ------------------\n");
		//int i=0;
		// for each rule
		for(Rule rule : rules){
			// append the rule, its support and confidence.
			//buffer.append(" rule ");
			//buffer.append(i);
			//buffer.append(":  ");
			buffer.append(rule.toString());
			
			buffer.append("\n");
			//i++;
		}
		
		return buffer.toString(); // return the string
	}
	
	/**
	 * Add a rule to this list of rules
	 * @param rule the rule to be added
	 */
	public void addRule(Rule rule){
		rules.add(rule);
	}
	
	/**
	 * Get the number of rules in this list of rules
	 * @return the number of rules
	 */
	public int getRulesCount(){
		return rules.size();
	}

	/**
	 * Get the list of rules.
	 * @return a list of rules.
	 */
	public List<Rule> getRules() {
		return rules;
	}
	
	/**
	 * Get the name of rules.
	 * @return a name of rules.
	 */
	public String getName() {
		return name;
	}
	
	public String getMinUconfString(){
		return doubleToString(uconf);
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

