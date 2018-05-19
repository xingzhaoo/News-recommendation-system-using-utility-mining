package utilityAssocRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dataUnit.Rule;
import dataUnit.UItemset;

/**
 * This class evaluation a sorted UItemset in ascending order on item utility in
 * backwards fashion. It is used for 1-Itemset recommendation.
 * 
 * @author Xing Zhao
 *
 */
public class ExhaustAnalysis2NR {

	protected final static double EPSILON = 0.00001; // precision for double
														// comparison
	// the set stores all high utility itemsets
	protected Set<String> HUIset;
	// minimum confidence specified by user
	protected double minConfidence;
	// list of Rule
	protected List<Rule> rules;

	/**
	 * empty constructor for testing only
	 */
	public ExhaustAnalysis2NR() {

	}

	public ExhaustAnalysis2NR(Set<String> HUI, double minconf) {
		HUIset = HUI;
		minConfidence = minconf;
		rules = new ArrayList<Rule>();
	}

	/**
	 * @param itemset,
	 *            a sorted itemset in ascending order based on local utility of
	 *            an item
	 * @return a List of Rule
	 */
	public void findRules(UItemset itemset) {
		/* sort the items in ascending order on utility */
		itemset = itemset.uSort(); 
		
		int[] integerSet = itemset.getUItemset();
		/* loop through itemset backwards, from large to small utility */
		for (int i = itemset.size() - 1; i >= 0; i--) {
			UItemset antec = itemset.cloneItemSetMinusOneItem(integerSet[i]);
			UItemset conseq = new UItemset(itemset.get(i), itemset.getluv(i), itemset.getluv(i));
			/* check if conseq is HUI, else terminate search since items are in ascending order */
			if (!HUIset.contains(conseq.getUItemsetString())) {
				return;
			}
			/* exhaustive search up to 2^|antec| in worst case */
			for (int size = antec.size(); size > 0; size--) {
				combination(antec, conseq, size);
			}

		}

	}

	/**
	 * return a list of rules from this itemset
	 * 
	 * @return a list of rules
	 */
	public List<Rule> getRules() {
		return rules;
	}

	/**
	 * remove all Rules
	 */
	public void clearRules(){
		rules.clear();
	}
	
	/**
	 * Generate unique subsets of the subset
	 * 
	 * @param antec
	 *            UItemset antecedent
	 * @param antec
	 *            UItemset consequent
	 * @param K
	 *            size of the combination set among antecedent
	 */
	public void combination(UItemset antec, UItemset conseq, int K) {
		int[] elements = antec.getUItemset();
		// get the length of the array
		// e.g. for {'A','B','C','D'} => N = 4

		int N = elements.length;

		if (K > N || K < 1) {
			System.out.println("Invalid input, K > N or K < 1");
			return;
		}

		// calculate the possible combinations
		// e.g. c(4,2)
		// c(N,K);

		// get the combination by index
		// e.g. 01 --> AB , 23 --> CD
		int combination[] = new int[K];

		// position of current index
		// if (r = 1) r*
		// index ==> 0 | 1 | 2
		// element ==> A | B | C
		int r = 0;
		int index = 0;

		while (r >= 0) {
			// possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
			// possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"

			// for r = 0 ==> index < (4+ (0 - 2)) = 2
			if (index <= (N + (r - K))) {
				combination[r] = index;

				// if we are at the last position print and increase the index
				if (r == K - 1) {

					// do something with the combination e.g. add to list or
					// print
					// print(combination, elements);
					addSet(combination, antec, conseq);
					index++;
				} else {
					// select index for next position
					index = combination[r] + 1;
					r++;
				}
			} else {
				r--;
				if (r > 0)
					index = combination[r] + 1;
				else
					index = combination[0] + 1;
			}
		}
	}

	/**
	 * Analyze the generated combination(subset of X) Add a satisfying rule to
	 * global rules based on the provided sequence of index
	 * 
	 * @param combinationIndex
	 *            combination sequence of index
	 * @param X
	 *            original UItemset antecedent
	 * @param Y
	 *            original 1-UItemset consequent, Y must contains item
	 */
	public void addSet(int[] combinationIndex, UItemset X, UItemset Y) {
		/* mining many to one news rules */
		/* generated combination set*/
		int[] ItemCombination = new int[combinationIndex.length]; 
		/* utility of combination */
		int[] ItemUtility = new int[combinationIndex.length];
		// int[] itemsX = X.uItemset;
		// get combination set from X
		for (int i = 0; i < combinationIndex.length; i++) {
			ItemCombination[i] = X.getUItemset()[combinationIndex[i]];
			ItemUtility[i] = X.getUtilityArray()[combinationIndex[i]];
		}

		UItemset subsetX = new UItemset(ItemCombination, ItemUtility);

		UItemset subsetXUY = subsetX.addUItem(Y); // an ascending itemset
		// subsetXUY = subsetXUY.uSort();
/*		System.out.println("X:" + subsetX.getUItemsetString());
		System.out.println("XUY:" + subsetXUY.getUItemsetString());*/
		/* subsetXUY must be HUI */
		if (HUIset.contains(subsetXUY.getUItemsetString())) {
			/* evaluate subsetX -> Y */
			double uconf = Y.getUtility() / (double) subsetXUY.getUtility();
			if (uconf - minConfidence >= EPSILON) {
				Rule aRule = new Rule(subsetX, Y, Y.getUtility(), subsetXUY.getUtility(), uconf);
				aRule.sortItemset(); // may remove after test as X is sorted
				rules.add(aRule);
			}
			
		}

	}

}
