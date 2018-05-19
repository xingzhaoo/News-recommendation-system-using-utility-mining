import java.util.Comparator;

/**
* This class represents Rule Unit, an item of a Rule (a set of items) implemented as an array of integers with
* its associated utility of the item.
* 
* @author Xing Zhao
*/

public class RuleItem implements Comparable<RuleItem>{
	/****/
	/** Rule item */
	private String name;
	private int item;
	/** weight/utility */
	private int itemUtility;

	/**
	 * Constructor
	 * 
	 * @param item
	 *            the item of a rule
	 * @param utility
	 * 			  the utility of an item
	 */
	public RuleItem(int item, int utility) {
		this.name = Integer.toString(item);
		this.item = item;
		this.itemUtility = utility;
	}

	public RuleItem(String item, int utility){
		this.name = item;
		this.item = Integer.parseInt(item);
		this.itemUtility = utility;
	}
	
	public RuleItem(RuleItem old){
		this.name = old.getItemName();
		this.item = old.getItem();
		this.itemUtility = old.getItemUtility();
	}
	
	public int getItem(){
		return item;
	}
	
	public String getItemName(){
		return name;
	}
	
	public int getItemUtility(){
		return itemUtility;
	}
	
	public void setItemUtility(int value){
		this.itemUtility = value;
	}
	public boolean equal(RuleItem other){
		if(this.getItem() == other.getItem()){
			return true;
		}
		return false;
	}
	
	public int compareUtility(RuleItem other){
		if(this.getItemUtility() == other.getItemUtility() ){
			return 0;
		} else if(this.getItemUtility() - other.getItemUtility() > 0){
			return 1;
		} 
		
		return -1;
	}
	
	public RuleItem mergeSameItem(RuleItem other){
		if(this.getItem() != other.getItem()){
			return null;
		}
		
		return new RuleItem(this.getItem(), this.getItemUtility() + other.getItemUtility());
	}
	
	/* (non-Javadoc)
	 * allow Arrays.sort(RuleItem[]) on its utility property in ascending order
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RuleItem compareRuleItem){
		
		int compareUtility = ((RuleItem) compareRuleItem).getItemUtility();
		
		//ascending order
		return this.getItemUtility() - compareUtility;
		
		//descending order
		//return compareUtility - return this.getItemUtility()
	}
	
	public static Comparator<RuleItem> RuleItemNameComparator = new Comparator<RuleItem>() {
		/* (non-Javadoc)
		 * allow Arrays.sort(RuleItem[], RuleItem.RuleItemNameComparator) on its name in ascending order
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(RuleItem a, RuleItem b){
			String ItemName1 = a.getItemName().toUpperCase();
			String ItemName2 = b.getItemName().toUpperCase();
			
			//ascending order
			return ItemName1.compareTo(ItemName2);
			//descending order
			//return ItemName2.compareTo(ItemName1);
		}
	};
	
}
