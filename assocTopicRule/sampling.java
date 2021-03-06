import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class sampling {
	static Random rng = new Random();
	static double splitR = 0.7;

	public static void main(String[] args) throws IOException {
                String input ="../trans_users_HUI.txt";
                String output ="../evaluation/unit5/sampling.txt";
                int sampleSize = 50000;
                int maxLineFile = 2735004;
                int minLineFile = 0;
                getSample(input, output, sampleSize, maxLineFile, minLineFile);

	}

	/**
	 * generate a random sample from DB input by setting size of the sample, min line and max line of the input file
	 * @param input	- DB file
	 * @param output	- sampling file
	 * @param size	- sample size
	 * @param max	- max line(entry) of DB
	 * @param min	- min line(entry) of DB
	 * @throws IOException
	 */
	public static void getSample(String input, String output,  int size, int max, int min) throws IOException{
		BufferedReader myInput = null;
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		StringBuilder bufferTrain = new StringBuilder();
		StringBuilder bufferTest = new StringBuilder();
		Set<Integer> rng = rndSet(size, min, max);
		Set<Integer> train = new TreeSet<Integer>();
		Set<Integer> test = new TreeSet<Integer>();
		int splitL = (int) (rng.size() * splitR); // the line where 70% of training data is up to
		for(int e : rng){
			if(splitL > 0){
				train.add(e);
			}else{
				test.add(e);
			}
			splitL--;
		}
		System.out.println("Train: " + train.toString());
		System.out.println("Test: "+ test.toString());
		String thisLine;
		try{
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
		    // for each line (transaction) until the end of file
			int i = 0;
		    while ((thisLine = myInput.readLine()) != null) {
		    	// if the line is  empty
		    	if (thisLine.isEmpty() == true) {
		    		continue;
		    	}
		    	
		    	if(train.contains(i)){
		    		bufferTrain.append(thisLine+"\n");
		    	}else if(test.contains(i)){
		    		bufferTest.append(thisLine+"\n");
		    	}
		    	i++;
		    }
		    writer.write(bufferTrain.toString());
		    writer.newLine();
		    writer.write(bufferTest.toString());
		}catch (Exception e) {
        	// catches exception if error while reading the input file
        	e.printStackTrace();
        }finally {
        	if(myInput != null){
        		myInput.close();
        	}
        }
		
        	if(writer != null){
        		writer.close();
        	}
	}
	
	/**
	 * Get a set of random numbers
	 * @param size	- specify the size of the set
	 * @param min	- specify the minimum number of the set
	 * @param max	- specify the maximum number of the set
	 * @return a set of random numbers
	 */
	public static Set<Integer> rndSet(int size, int min, int max){

		if (max < size)
		{
		    throw new IllegalArgumentException("Can't ask for more numbers than are available");
		}

		Set<Integer> generated = new LinkedHashSet<Integer>();
		while (generated.size() < size)
		{
		    Integer next = rng.nextInt(max - min + 1) + min;
		    // As we're adding to a set, this will automatically do a containment check
		    generated.add(next);
		}
		System.out.println("line sampled: "+generated.toString());
		return generated;
	}
}
