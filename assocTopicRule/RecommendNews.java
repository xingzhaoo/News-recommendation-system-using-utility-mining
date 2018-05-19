import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import dataUnit.TRule;
import dataUnit.TRules;
import dataUnit.Rule;
import dataUnit.Rules;

/**
 * Recommend component, last stage for News Recommender
 * @author Xing Zhao
 *
 */

public class RecommendNews {
	
	protected int TopK = 5;   // specify only keep TopK largest probability topics to a news
	protected int precisionR = 5; // specify precison@r
	protected int recNum = 0;  // total recommended news
	protected int recNews = 0; // news has recommendation 
	protected double avgPrecision = 0; // average Precision
	protected int recSession = 0; // num of session has recommendation 

	protected int minNew = 0; // min for min_max normalize
	protected int maxNew = 1; // max for min_max normalize
	
	protected Map<String, Map<String, Double>> newsTopics; // news to topics distribution
	protected Map<String, Set<String>> topicToNews;        // topic to news distribution
	protected TRules topicRuleSet;				// topic Rules mined from AssocRulesTopic
	protected Map<String, Map<String, Double>> mergedTRuleSet;     // topic rules with same antecedent are merged
	protected Map<String, Long> newsPublishTime; 		  // news Published time 
	
	protected double maxMemory = 0;    // the maximum memory usage
	protected long startTimestamp = 0; // last execution start time
	protected long endTimestamp = 0;   // last execution end time
		
	/**
	 * Default constructor
	 */
	public RecommendNews() {
	}

	/**
	 * Test functions
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String newsTopicDB = "BB_187_sample1k_test.txt";// load topic dist table
		int TopK = 5; // specify only keep TopK largest probability topics to a news
		
		Map<String, Set<String>> topicToNews;
		
		topicToNews = topicsToNews(loadMapNewsTopics(newsTopicDB, TopK));
		
		for(Entry<String, Set<String>> e: topicToNews.entrySet()){
			System.out.println(e.toString());
		}
		
	}

	/**
	 * Recommend a list of news per user visited news
	 * @param input, user news reading session
	 * @param output, output a list of recommend news per user visited news
	 * @param newsRules, many-1 newsRules mined via user news reading history 
	 * @param topicRules, topicRules mined via user news reading history 
	 * @param newsTopicDB, news to topics probability distribution table
	 * @param newsStamps, news published time stamp
	 * @throws IOException 
	 * 
	 */
	public void newsRecOnTopics(String input, String output, Rules newsRules,TRules topicRules, String newsTopicDB, String newsStamps) throws IOException{
		maxMemory =0;
		startTimestamp = System.currentTimeMillis();
		
		topicRuleSet = topicRules;		// removed if never used
		mergedTRuleSet = topicRules.mergeRulesConf(); 		// get a map of merged rules with same antec
		
		newsTopics = loadMapNewsTopics(newsTopicDB, TopK); // news to topics distribution
		topicToNews = topicsToNews(newsTopics); // topic to news distribution
		
		newsPublishTime = loadNewsPublishTime(newsStamps); // get a map of latest news
		
		// test merged topic rules
		//for(Entry<String, Set<String>> e: mergedTRuleSet.entrySet()){
		//	System.out.println(e.toString());
		//}
		
		checkMemory();
		BufferedReader myInput = null;
		BufferedWriter writer = null; 
		//StringBuilder buffer = new StringBuilder(); moved to loop due to write to disk per session
		String thisLine;
		try {
			// prepare the object for reading the file
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			// save output object
			writer = new BufferedWriter(new FileWriter(output));
			// for each line (transaction) until the end of file
			int count = 0; // session scanned count
			while ((thisLine = myInput.readLine()) != null) {
				
				if (thisLine.isEmpty() == true) {
					continue;
				}
				StringBuilder buffer = new StringBuilder();
				// split the transaction according to the : separator
				String split[] = thisLine.trim().split(":");
				// the first part is the list of news items
				String items[] = split[0].split(" ");
				List<String> nwReading = new ArrayList<String>();
				int localNewsRec = 0; // num of news has rec in this session
				double avglocalP = 0; // avg local precision per session
				// scan each user visited news session
				count++;
				
				for(int i = 0; i < items.length - 1; i++){
					// for each news
					nwReading.add(items[i]);
					Map <String, Double> rankAR = new HashMap<String, Double>();//<news:AR>
					Map <String, Double> rankTR;//<news:TR>
					double normalMaxAR = 0; // max for min_max normalize
					double normalMinAR = Double.MAX_VALUE; // min for min_max normalize
					/* computing AR */
					for(Rule r: newsRules.getRules()){
						int antec[]= r.getItemset1();
						//map size of visited news and size of rule antec
						if(antec.length != nwReading.size())
							continue;
						// match rule's antec and visited news
						int numNw = nwReading.size();
						for(int j = 0; j < antec.length; j++){
							// news can be unordered using list
							if(nwReading.contains(antec[j]+"")){															
								numNw--;
							}
						}
						// fully matched news and antec
						if(numNw == 0){
							// compute AR for conseq(1-item)
							int conseq[] = r.getItemset2(); // news to recommend
							double AR = r.getpotentialUtility() * r.getConfidence();
							rankAR.put(conseq[0]+"", AR);
							normalMaxAR = AR > normalMaxAR ? AR:normalMaxAR;
							normalMinAR = AR < normalMinAR ? AR:normalMinAR;
						}			
					}
					// sort AR in descending order
					// normalize AR to [0,1] to match range of TR
					for(String e : rankAR.keySet()){
						double newValue = rankAR.get(e);
						newValue = minMaxNormal(newValue, normalMinAR, normalMaxAR, minNew, maxNew);
						rankAR.put(e, newValue);
					}
					
/*					if(rankAR.size() > 0){// have AR rank
						buffer.append(nwReading.toString() +" AR: "+ entriesSortedByValues(rankAR) + "\n");
					}*/
					
					/* computing TR */					
					rankTR = getRankTR(items[i].trim());
					
					double normalMaxTR = 0; // max for min_max normalize
					double normalMinTR = Double.MAX_VALUE; // min for min_max normalize
					
					for(String e : rankTR.keySet()){
						double value = rankTR.get(e);
						normalMaxTR = value > normalMaxTR ? value:normalMaxTR;
						normalMinTR = value < normalMinTR ? value:normalMinTR;
					}
					
					// normalize TR to [0,1] to match range of TR
					for(String e : rankTR.keySet()){
						double newValue = rankTR.get(e);
						newValue = minMaxNormal(newValue, normalMinTR, normalMaxTR, minNew, maxNew);
						rankTR.put(e, newValue);
					}
					
					
/*					if(rankTR.size() > 0){// have AR rank
						buffer.append(" TR: "+ entriesSortedByValues(rankTR) + "\n");
					}*/
					
					// Calculate REC, use rankTR to merge rankAR
					//rankTR = new HashMap<String, Double>();disable TR values
					for(String eTR: rankTR.keySet()){
						if(!rankAR.containsKey(eTR)){ // REC = TR * minAR
							rankTR.put(eTR, rankTR.get(eTR) * normalMinAR);
						}
					}
										
					Set<Map.Entry<String, Double>> ARentries = rankAR.entrySet();
					
					for(Map.Entry<String, Double> entry : ARentries){
						if(rankTR.containsKey(entry.getKey())){ // REC = AR * TR
							Double RECscore = entry.getValue() * rankTR.get(entry.getKey());
							rankTR.put( entry.getKey(), RECscore);
						}
						else {	// REC = AR * minTR
							rankTR.put(entry.getKey(), entry.getValue() * normalMinTR);
						}
					}
					
/*					if(rankTR.size() > 0){// have AR rank
						buffer.append(" REC: "+ entriesSortedByValues(rankTR) + "\n");
					}*/
					
					if(rankTR.size() == 0)
						continue;
					
					/* computing precision@r */
					List <Entry<String, Double>> precisionRank = entriesSortedByValues(rankTR);
					List <String> recommendNews = new ArrayList<String>();
					
					buffer.append(count + "#"); // start recording, session
					buffer.append(items[i] + "->"); // visited news
					
					precisionR = precisionR > rankTR.size() ? rankTR.size():precisionR;
					recNum += precisionR; // add recommend news
					recNews++;		// increase total recNews count
					localNewsRec++; // increase localNews count
					
					for(int r = 0; r < precisionR; r++){
						recommendNews.add(precisionRank.get(r).getKey());
						buffer.append(precisionRank.get(r).getKey() + ", ");
						//System.out.print(precisionRank.get(r).getKey() + "@" + precisionRank.get(r).getValue()+"#");
					}
					//System.out.println();
					
					// evaluate future news
					int precision = 0;
					for(int f = i + 1; f < items.length ; f++){
						if(recommendNews.contains(items[f].trim())){
							precision++;
						}
					}
					
					double precision_r = precision / (double) precisionR;
					// avgPrecision += precision_r;
					avglocalP += precision_r;
					buffer.append("SUM p@r=" + precision_r + "\n");
					
				}
				
				if(localNewsRec > 0){
					avgPrecision += avglocalP / localNewsRec; //avg p per session
					recSession++;
				}
				
				writer.write(buffer.toString());
				//writer.newLine();
			}
		} catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }
		
		StringBuilder resultBuff = new StringBuilder();
		double aveP = avgPrecision / recSession; // avg precision
		resultBuff.append("Recommeded News :" + recNum + "\n");
		resultBuff.append("Session has recommedation:" + recSession + "\n");
		resultBuff.append("News has recommedation :" + recNews + "\n");
		resultBuff.append("Sum avg precsion per session :" + avgPrecision + "\n");
		resultBuff.append("Average Precision in Total :" + aveP + "\n");
		writer.write(resultBuff.toString());
		//writer.newLine();
		
		// check the memory usage
		checkMemory();
		writer.close();
		endTimestamp = System.currentTimeMillis();				
	}
	
	/**
	 * Calculate TR and save results in a Map
	 * @param news given a news_id
	 * @return Map with recommend news and TR score 
	 */
	public Map<String, Double> getRankTR(String news){
		Map<String, Double> rank = new HashMap<String, Double>();
		String topic_i = getTopic(news);
		// bypass topics not exist in news topic dist list
		if(topic_i.isEmpty())
			return rank;
		
		// TRules must be aggregated by now and load to a map
		// use Map to get topic_i
		Map<String, Double> conseqTopics = mergedTRuleSet.get(topic_i);
		
		// bypass topics not exist in mined Topic RuleSet
		if(conseqTopics == null)
			return rank;
		
		// loop each topic find news in topic dist and pick the latest news and Tconf
		for(String topicRec: conseqTopics.keySet()){
			//newsRecommend = newsRecommend + topicRec + "@" + getRecommendNews(topicRec) + " ";
			rank.put(getRecommendNews(topicRec), conseqTopics.get(topicRec));
		}
		
		return rank;
	}
	
	
	/**
	 * Given a topic, recommend a latest news based on a topic news distribution
	 * @param topic specify a topic 
	 * @return latest published news
	 */
	public String getRecommendNews(String topic){
		Set<String> newsSet = topicToNews.get(topic);
		String recommend = new String();
		long min = Long.MAX_VALUE;
		for(String e: newsSet){
			if(!newsPublishTime.containsKey(e)){
				System.err.println("news " + e + "is not found in publish Date");
			}else if(min > newsPublishTime.get(e)){
						min = newsPublishTime.get(e);
						recommend = e;
			}
		}		
		return recommend;
	}

	/**
	 * find top1 probability topic associated to the news
	 * @param news a visited news
	 * @return
	 */
	public String getTopic(String news){
		String topic = new String();
		if(newsTopics == null){
			System.err.println("news topics distribution is not initialised yet!");
			return topic;
		}
		//System.out.println(newsTopics.toString());
		/* find top1 topic based on visited news */
		if(newsTopics.containsKey(news)){
			Map<String, Double> topicsProbDist = newsTopics.get(news);
			double max = 0;
			for(String eTopic: topicsProbDist.keySet()){
				if(max < topicsProbDist.get(eTopic)){
					max = topicsProbDist.get(eTopic);
					topic = eTopic;
				}
			}
		}else{
			//System.err.println("this news "+ news +" is not registered in news topics distribution.");
		}
		
		return topic;
	}
	
	/**
	 * Generate a Map of <news, publish date>, the publish date is expressed in long format
	 * the more recent the news is, its value is smaller than others. 
	 * @param newsPublishDB , file stores the news publish time stamp
	 * @return a Map <News, days between published date and today>
	 * @throws IOException
	 */
	public static Map<String, Long> loadNewsPublishTime(String newsPublishDB) throws IOException{
		BufferedReader myMappingDB = null;
		Map<String, Long> newsPublish = new HashMap<String, Long>();
		String entryLine;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try{
			myMappingDB = new BufferedReader(new InputStreamReader(new FileInputStream(new File(newsPublishDB))));
			// for each line (mapping entry) until the end of the file
			while ((entryLine = myMappingDB.readLine()) != null) {
				// if the line is empty
				if (entryLine.isEmpty() == true || entryLine.charAt(0) == '#') {
					continue;
				}
				//split the transaction according to the " # " separator
				String split[] = entryLine.split("#");
				Date record = df.parse(split[1]);
				Date now = new Date();
				Long time = daysBetween(now, record);
				newsPublish.put(split[0], time);
			}	
		}catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		}finally {
			if(myMappingDB != null){
				myMappingDB.close();
			}
		}
		return newsPublish;
	}
	
	/**
	 * Load mappingDB to the search Map "newsIDtoTopicID_prob"
	 * @param mapfile - the mapping database with sets <newsID, topicID_i, probability_i,...>, i = N
	 * @param topK - specify only keep the topK largest probability topics associated with 
	 * 					this news (i = topK).
	 * @return a Map with <newsID, <topicID,probability>>
	 * @throws IOException
	 */
	public static Map<String, Map<String, Double>> loadMapNewsTopics(String mapfile, int topK) throws IOException{
		// a nested map contains all newsID to topicID for translation from newsID to topicID
		Map<String, Map<String, Double>> newsMapTopic = new HashMap<String, Map<String, Double>>();
		BufferedReader myMappingDB = null;
		
		String entryLine;
		
		try{
			myMappingDB = new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapfile))));
			// for each line (mapping entry) until the end of the file
			while ((entryLine = myMappingDB.readLine()) != null) {
				// if the line is empty
				if (entryLine.isEmpty() == true || entryLine.charAt(0) == '#') {
					continue;
				}
				//split the transaction according to the " # " separator
				String split[] = entryLine.split("\t");
				
				Map<String, Double> probDist = new HashMap<String, Double>();
				
				if(split.length < topK || topK < 1){ // handle illegal operation
					topK = split.length;
				}
				
				// get <topic, prob> from split[1...n]
				for(int i = 1; i < topK - 1; i = i + 2){
					//if(i % 2 == 1){ // odd index
					probDist.put(split[i].trim(), Double.parseDouble(split[i+1].trim()));
					//} // skip even index
				}			
				newsMapTopic.put(split[0].trim(), probDist);
			}
			
		}catch (Exception e) {
        	// catches exception if error while reading the input file
        	e.printStackTrace();
        }finally {
        	if(myMappingDB != null){
        		myMappingDB.close();
        	}
        }
		return newsMapTopic;
	}
	
	/**
	 * generate a topic to news set map by reversing the <news to topics> probability distribution
	 * @param newsTopics
	 * @return a map of topics to news
	 */
	public static Map<String, Set<String>> topicsToNews(Map<String, Map<String, Double>> newsTopics){
		Map<String, Set<String>> topicsMapNews = new TreeMap<String, Set<String>>();
		for(String news : newsTopics.keySet()){
			for(String topics: newsTopics.get(news).keySet()){
				if(topicsMapNews.containsKey(topics)){
					topicsMapNews.get(topics).add(news);
				}else{
					Set<String> newsSet = new TreeSet<String>();
					newsSet.add(news);
					topicsMapNews.put(topics, newsSet);
				}
			}
		}
		return topicsMapNews;
	}
	
	/**
	 * Calculate the days between two dates (a.k.a freshness of a news article)
	 * 
	 * @param one
	 *            , end date
	 * @param two
	 *            , start date
	 * @return days
	 * 
	 *         source from:
	 *         http://javarevisited.blogspot.ca/2015/07/how-to-find-
	 *         number-of-days-between-two-dates-in-java.html
	 */
	private static long daysBetween(Date one, Date two) {
		long difference = (one.getTime() - two.getTime()) / 86400000;
		return Math.abs(difference);
	}
	
	/**
	 * Sorted the map based on its value in descending order
	 * source from http://stackoverflow.com/questions/11647889/sorting-the-mapkey-value-in-descending-order-based-on-the-value
	 * @param map
	 * @return sorted map
	 */
	public static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K,V>>() {
				@Override
				public int compare(Entry<K,V> e1, Entry<K,V> e2) {
					return e2.getValue().compareTo(e1.getValue());
				}
			}
		);
		return sortedEntries;
	}
	
	/**
	 * Min-Max normalization
	 * @param value
	 * @param min_v
	 * @param max_v
	 * @param new_min
	 * @param new_max
	 * @return normalized value
	 */
	public static double minMaxNormal(double value, double min_v, double max_v, double new_min, double new_max){
		return (value - min_v) * (new_max - new_min) / (max_v - min_v) + new_min;
	}
	
	/**
	 * Method to check the memory usage and keep the maximum memory usage.
	 */
	private void checkMemory() {
		// get the current memory usage
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
				/ 1024d / 1024d;
		// if higher than the maximum until now
		if (currentMemory > maxMemory) {
			// replace the maximum with the current memory usage
			maxMemory = currentMemory;
		}
	}
	
	/**
	 * Print statistics about the latest execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  Recommend News ALGORITHM - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Memory ~ " + maxMemory+ " MB");
		//System.out.println(" High-utility itemsets count : " + totalpatterns); 
		System.out.println(" Number of news recommended : " + recNum); 
		System.out.println("===================================================");
	}
	
}
