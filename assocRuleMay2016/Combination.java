import java.util.ArrayList;
import java.util.List;


/**
 * This class generate unique subset of a provided set which is used in UItemset for
 * HUI association rule mining
 * @author Xing Zhao
 *
 */
public class Combination {
    
	/**
	 * set to store all subset
	 */
	private static final List<List<Object[]>> set = new ArrayList<List<Object[]>>();
	private List<Object[]> list = new ArrayList<Object[]>();
 	
	/**
	 * Constructor
	 */
	private Combination(){

	}
	
	private static class SingletonHelper{
		private static final Combination INSTANCE = new Combination();
	}
	
	public static Combination getInstance(){
		return SingletonHelper.INSTANCE;
	}
	
    /**
     * For test purpose
     * @param args
     */
    public static void main(String[] args){
    	/* Test combination method */
        //Object[] elements = new Object[] {'C','B','A','D','F', 'E'};
        int [] elements = {14659864, 8609460, 19873440, 19822996};
        /*Combination.combination(elements,2);
        Combination.halfSubset(elements);        
        for(Object[] e: list){
        	//System.out.print(e);
        	for(Object k: e){
        		System.out.print(k);
        	}
        	System.out.println();
        }
        */
        int greater;
        int[] ordered = new int[elements.length];
        int[] index = new int[ordered.length]; 
        //imperfect number ordering algorithm
        for(int indexL=0;indexL<elements.length;indexL++)
        {
            greater=0;
            for(int indexR=0;indexR<elements.length;indexR++)
            {
                if(elements[indexL] > elements[indexR])
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
       
        for(Object e: ordered)
        System.out.print(e + " ");
        System.out.println();
        for(int e: index)
        System.out.print(elements[e] + " ");
        System.out.println();
        /* test this Combination Class */
        Object [] element = {"A", "C", "B", "D", "E", "F"};
        Combination singlton = Combination.getInstance();
        singlton.subset(element);
        System.out.println("num of subset: " + singlton.getSet().size());
/*        for(Object[] e: singlton.getSet()){
        	for(Object k : e)
        	System.out.print(k.toString() + " ");
        	System.out.println();
        }*/
        
    }
    
    /**
     * sort the provided array in ascending order
     * Note: sort Integer element in this case, 
     * Object can be casted to other type
     * @param elements array
     * @return sorted array
     */
    public static Object[] sort(Object[]  elements){
    	 int greater;
         Object [] ordered = new Object[elements.length];
         int[] index = new int[ordered.length]; 
         //imperfect number ordering algorithm
         for(int indexL=0;indexL<elements.length;indexL++)
         {
             greater=0;
             for(int indexR=0;indexR<elements.length;indexR++)
             {
                 if((int)elements[indexL] > (int)elements[indexR])
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
         return ordered;
    }
    
    /**
     * Generate unique subsets which are half size of the provided set
     * @param elements set
     * @return a set of unique subsets
     */
    public List<List<Object[]>> halfSubset(Object[]  elements){
    	clearSet();
    	
    	int halfN = elements.length / 2;
    	    	
    	for(int size = 1; size <= halfN; size++){
    		combination(elements, size);
    	}
    	addLastSet();
    	return getSet();
    }
    
    /**
     * Generate unique subsets of the provided set
     * @param elements set
     * @return a set of unique subsets
     */
    public List<List<Object[]>> subset(Object[]  elements){
    	clearSet();
    	
    	int N = elements.length;
    	    	
    	for(int size = 1; size < N; size++){
    		combination(elements, size);
    	}
    	
    	addLastSet();
    	
    	return getSet();
    }
    
    private void addLastSet(){
    	if(list.size() > 0){
    		set.add(list);
    	}
    }
    
    /**
     * Generate unique subsets of the provided set
     * @param elements set
     * @return a set of unique subsets
     */
    public static List<Object[]> subsetList(Object[]  elements){
    	List<Object[]> sum = new ArrayList<Object[]>();
    	
    	int N = elements.length;
    	    	
    	for(int size = 1; size < N; size++){
    		
    		List<Object[]> subInSize = combinationSubsets(elements, size);
    		
    		for(Object[] each: subInSize){
    			sum.add(each);
    		}
    		
    	}
    	
    	return sum;
    }
    /**
     * Generate unique subsets of the provided set
     * @param elements set
     * @return a set of unique subsets
     */
    public static String subsetListPrint(Object[]  elements){
    	StringBuffer sumBuffer = new StringBuffer();
    	int N = elements.length;   	
    	for(int size = 1; size < N; size++){
    		
    		sumBuffer.append(combinationPrint(elements, size));
    		
    	}
    	
    	return sumBuffer.toString();
    }
    
    
    /**
     * Generate unique subsets of the subset
     * @param elements set
     * @param K size of the set
     */
    public void combination(Object[]  elements, int K){
        // get the length of the array
        // e.g. for {'A','B','C','D'} => N = 4
        int N = elements.length;
         
        if(K > N){
            System.out.println("Invalid input, K > N");
            return;
        }

        // calculate the possible combinations
        // e.g. c(4,2)
        //c(N,K);
         
        // get the combination by index
        // e.g. 01 --> AB , 23 --> CD
        int combination[] = new int[K];
         
        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;      
        int index = 0;
         
        while(r >= 0){
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"
             
            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if(index <= (N + (r - K))){
                    combination[r] = index;
                     
                // if we are at the last position print and increase the index
                if(r == K-1){
 
                    //do something with the combination e.g. add to list or print
                    //print(combination, elements);
                	addSet(combination, elements);
                    index++;                
                }
                else{
                    // select index for next position
                    index = combination[r]+1;
                    r++;                                        
                }
            }
            else{
                r--;
                if(r > 0)
                    index = combination[r]+1;
                else
                    index = combination[0]+1;   
            }           
        }
    }

    /**
     * Generate unique subsets of the subset
     * @param elements set
     * @param K size of the set
     */
    public static List<Object[]> combinationSubsets(Object[]  elements, int K){
    	int max = (int) Math.pow(2, 20);
    	List<List<Object[]>> sumList = new ArrayList<List<Object[]>>();
    	List<Object[]> list = new ArrayList<Object[]>();
        // get the length of the array
        // e.g. for {'A','B','C','D'} => N = 4
        int N = elements.length;
         
        if(K > N){
            System.out.println("Invalid input, K > N, return null");
            return null;
        }

        // calculate the possible combinations
        // e.g. c(4,2)
        //c(N,K);
         
        // get the combination by index
        // e.g. 01 --> AB , 23 --> CD
        int combination[] = new int[K];
         
        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;      
        int index = 0;
         
        while(r >= 0){
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"
             
            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if(index <= (N + (r - K))){
                    combination[r] = index;
                     
                // if we are at the last position print and increase the index
                if(r == K-1){
 
                    //do something with the combination e.g. add to list or print
                    //print(combination, elements);
                	//addSet(combination, elements);
                	// prevent max length exceeded
                	if(list.size() > max){
                		sumList.add(list);
                		list = new ArrayList<Object[]>();
                	}
                	
                	list.add(getSubset(combination, elements));
                    index++;                
                }
                else{
                    // select index for next position
                    index = combination[r]+1;
                    r++;                                        
                }
            }
            else{
                r--;
                if(r > 0)
                    index = combination[r]+1;
                else
                    index = combination[0]+1;   
            }           
        }
        
        return list;
    }
    
    /**
     * Generate unique subsets of the subset
     * @param elements set
     * @param K size of the set
     */
    public static String combinationPrint(Object[]  elements, int K){
    	StringBuffer buffer = new StringBuffer();
        // get the length of the array
        // e.g. for {'A','B','C','D'} => N = 4
        int N = elements.length;
         
        if(K > N){
            System.out.println("Invalid input, K > N, return null");
            return null;
        }

        // calculate the possible combinations
        // e.g. c(4,2)
        //c(N,K);
         
        // get the combination by index
        // e.g. 01 --> AB , 23 --> CD
        int combination[] = new int[K];
         
        // position of current index
        //  if (r = 1)              r*
        //  index ==>        0   |   1   |   2
        //  element ==>      A   |   B   |   C
        int r = 0;      
        int index = 0;
         
        while(r >= 0){
            // possible indexes for 1st position "r=0" are "0,1,2" --> "A,B,C"
            // possible indexes for 2nd position "r=1" are "1,2,3" --> "B,C,D"
             
            // for r = 0 ==> index < (4+ (0 - 2)) = 2
            if(index <= (N + (r - K))){
                    combination[r] = index;
                     
                // if we are at the last position print and increase the index
                if(r == K-1){
 
                    //do something with the combination e.g. add to list or print
                    //print(combination, elements);
                	//addSet(combination, elements);
                	Object[] temp = getSubset(combination, elements);
                	for(Object e: temp){
                	    buffer.append(((Integer)e).toString() + " ");
                	}
                    index++;                
                }
                else{
                    // select index for next position
                    index = combination[r]+1;
                    r++;                                        
                }
            }
            else{
                r--;
                if(r > 0)
                    index = combination[r]+1;
                else
                    index = combination[0]+1;   
            }           
        }
        
        buffer.append("\n");
        
        return buffer.toString();
    }
    /**
     * Print out the array based on provided sequence of index
     * @param combination sequence of index
     * @param elements array
     */
    public static void print(int [] combination, Object[]  elements){
        for(int e : combination){
            System.out.print(elements[e]);
        }
        System.out.println();
    }
    
    /**
     * Add a set to global set based on the provided sequence of index 
     * @param combination sequence of index
     * @param elements array
     */
    public void addSet(int [] combination, Object[]  elements){
    	int max = (int) Math.pow(2, 20);
    	Object[] eComb = new Object[combination.length];
    	
    	for(int i = 0; i < combination.length; i++){
    		eComb[i] = elements[combination[i]];
    	}
    	
    	if(list.size() > max){
    		set.add(list);
    		list = new ArrayList<Object[]>();
    	}
    	
    	list.add(eComb);
    }
    
    /**
     * Return a set to global set based on the provided sequence of index 
     * @param combination sequence of index
     * @param elements array
     */
    public static Object[] getSubset(int [] combination, Object[]  elements){
    	Object[] eComb = new Object[combination.length];
    	
    	for(int i = 0; i < combination.length; i++){
    		eComb[i] = elements[combination[i]];
    	}
    	
    	return eComb;
    }
    
    /**
     * Return the current global set
     * @return a set of Object array
     */
    public List<List<Object[]>> getSet(){
    	return set;
    }
    
    /**
     * Clear all elements in the global set
     */
    public void clearSet(){
    	set.clear();
    	list.clear();
    }
}
