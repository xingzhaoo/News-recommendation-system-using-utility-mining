package utilityAssocRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import dataUnit.Rule;
import dataUnit.Rules;
import dataUnit.UItemset;

/**
 * This class evaluation a sorted UItemset in ascending order on item utility 
 * in backwards fashion. It is used for 1-Itemset recommendation. 
 * 
 * @author Xing Zhao
 *
 */
public class BackwardAnalysis {

	protected final static double EPSILON = 0.00001; // precision for double comparison 
    // the set stores all high utility itemsets
    protected Set<String> HUIset;
    // minimum confidence specified by user
    protected double minConfidence;
	// list of Rule
    protected List<Rule> rules;
    
	public BackwardAnalysis(Set<String> HUI, double minconf){
		HUIset = HUI;
		minConfidence = minconf;
		rules = new ArrayList<Rule>();
	}
	/**
	 * @param itemset, a sorted itemset in ascending order based on local utility of an item
	 * @return a List of Rule
	 */
	public void findRules(UItemset itemset){
		itemset = itemset.uSort(); // sort the items in ascending order on utility
		int [] integerSet = itemset.getUItemset();
		UItemset antec = itemset.clone();
		/*start itemset backwards by last item*/
		for(int i = itemset.size() - 1; i >= 0; i--){
			antec = antec.cloneItemSetMinusOneItem(integerSet[i]);
			UItemset conseq = new UItemset(itemset.get(i), itemset.getluv(i), itemset.getluv(i));
			/*check if conseq is HUI*/
			if (!HUIset.contains(conseq.getUItemsetString())){
				return;
			}
			
			backwards(antec, conseq);
			
		}
				
	}
	
	/**
	 * return a list of rules from this itemset
	 * @return
	 */
	public List<Rule> getRules(){
		return rules;
	}
	
	/**
	 * remove all Rules
	 */
	public void clearRules(){
		rules.clear();
	}
	
	/**
	 * Recursively finding the rule by evaluating uconf and 
	 * removing 1 item backwards in each turn
	 * @param X itemset
	 * @param Y 1-itemset
	 */
	protected void backwards(UItemset X, UItemset Y){
		/* revised: added if condition for one to one rule */
		UItemset XUY = X.addUItem(Y); // add uses insertion sort
		/*base case, XUY is HUI*/
		if (X.size() == 0 || !HUIset.contains(XUY.getUItemsetString()))
		   return;
		/*evaluate X -> Y*/
		if(X.size() == 1){ // added if condition for one to one rule
			double uconf =  Y.getUtility() / (double) XUY.getUtility();
			if(uconf - minConfidence >= EPSILON){
				Rule aRule = new Rule(X, Y, Y.getUtility(), XUY.getUtility(), uconf);
				aRule.sortItemset(); // may remove after test as X is sorted
				rules.add(aRule);
			}
		} else// added if else condition for one to one topic rule 
		{	// compare last item(to be removed) in X to Y in descending order
			int index = X.size() - 1;
			UItemset antec_1 = new UItemset(X.get(index), X.getluv(index), X.getluv(index));
			/* check last item in X before it is removed */
			backwards(antec_1, Y);
		}
		// the following is many to one topic rule
		/*copy items from index 0 to size of X - 2*/
		UItemset antec_many = X.cloneItemSetKeepItemsTillIndex(X.size() - 2);
		// start backwards search 
		backwards(antec_many, Y);				
	}
	
}
