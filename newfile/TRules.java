package dataUnit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a list of topic association rules
 * 
 * @author Xing Zhao revised from Philippe Fournier-Viger
 */

public class TRules {
	/** a list of association rules */
	private final List<TRule> rules = new ArrayList<TRule>();  // rules
	/** a name that an algorithm can give to this list of associationjava rules */
	private final String name;
	/** the minimum utility confidence user defined */
	private final double tconf;
	
	/**
	 * Constructor
	 * @param name  a name for this list of association rules (string)
	 */
	public TRules(String name, double minTconf){
		this.name = name;
		this.tconf = minTconf;
	}
	
	public int size(){
	    return rules.size();	
	}
	
	/**
	 * Sort the rules by confidence
	 */
	public void sortByConfidence(){
		Collections.sort(rules, new Comparator<TRule>() {
			public int compare(TRule r1, TRule r2) {
				return (int)((r2.getProbConf() - r1.getProbConf()) * Integer.MAX_VALUE);
			}
		});
	}
	
	/**
	 * Merge rules for the same antecedent topic
	 * e.g. topic1->topic2 and topic1->topic3 are merged to
	 * 		topic1->[topic2, topic3]
	 * @return a map of merged rules for a same antecedent topic 
	 */
	public Map<String, Set<String>> mergeRules(){
		Map<String, Set<String>> rulesMerge = new HashMap<String, Set<String>>();
		for(TRule e: rules){
			if(rulesMerge.containsKey(e.getTopic1())){
				rulesMerge.get(e.getTopic1()).add(e.getTopic2());
			}else{
				Set<String> topics = new HashSet<String>();
				topics.add(e.getTopic2());
				rulesMerge.put(e.getTopic1(), topics);
			}
		}
		return rulesMerge;
	}
	
	/**
	 * Merge rules for the same antecedent topic
	 * same as mergeRules but it comes with Topic Confidence
	 * @return a map of merged rules for a same antecedent topic and associate tConf
	 */
	public Map<String, Map<String,Double>> mergeRulesConf(){
		
		Map<String, Map<String,Double>> rulesMerge = new HashMap<String, Map<String,Double>>();
		for(TRule e: rules){
			if(rulesMerge.containsKey(e.getTopic1())){
				rulesMerge.get(e.getTopic1()).put(e.getTopic2(),e.getProbConf());
			}else{
				Map<String,Double> topics = new HashMap<String,Double>();
				topics.put(e.getTopic2(),e.getProbConf());
				rulesMerge.put(e.getTopic1(), topics);
			}
		}
		return rulesMerge;
	}
	
	/**
	 * Print all the rules in this list to System.out.
	 * @param databaseSize the number of transactions in the transaction database where the rules were found
	 */
	public void printRules(){
		System.out.println("           ------- " + name + " -------             ");
		int i=0;
		for(TRule rule : rules){
			System.out.print("  rule " + i + ":  " + rule.toString());
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
	public boolean contains(TRule other){
		
		for(TRule e: this.rules){
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
		StringBuilder buffer = new StringBuilder();
		// for each rule
		for(TRule rule : rules){
			buffer.append(rule.toString());
			buffer.append("\n");
		}
		
		return buffer.toString(); // return the string
	}
	
	/**
	 * Add a rule to this list of rules
	 * @param rule the rule to be added
	 */
	public void addRule(TRule rule){
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
	public List<TRule> getRules() {
		return rules;
	}
	
	/**
	 * Get the name of rules.
	 * @return a name of rules.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the minimum topic confidence
	 * @return minimum topic confidence
	 */
	public String getMinTconfString(){
		return doubleToString(tconf);
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

