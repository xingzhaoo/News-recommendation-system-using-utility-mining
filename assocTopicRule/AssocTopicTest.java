
import java.io.IOException;

/**
 * Test class for Topic Association Rule 
 * @author Xing Zhao
 *
 */
public class AssocTopicTest {

	public static void main(String[] args) throws IOException {
		
		String assocNewsRule = "../trans_assoc_rule_HUImined_cf60_es.txt";
		String HUIpattern = "../trans_HUImined_UARY.txt";
		String newsTopicDB = "BB_187_topic_dist.txt";
		String output = "../topicRules.txt";
		double minPconf = 0.0;
		int DButility = 51462641;
		AssocRulesTopics alg = new AssocRulesTopics();
		alg.loadNewsRules(assocNewsRule);
		alg.runAlgorithm(HUIpattern, newsTopicDB, output, DButility, minPconf);
		alg.printStats();
/*		int utilityDB = 51462641;
		Map<String, Double> oneItemsProb = probDistList(input, utilityDB);
		for(String e: oneItemsProb.keySet())
			System.out.println(e + " : " + oneItemsProb.get(e));*/
		
/*		Map<String, TMelement> newsToTopicProb = loadMapNewsTopics(newsTopicDB);
		for(String e: newsToTopicProb.keySet())
			System.out.println(e + " : " + newsToTopicProb.get(e).getTopic() + ":" + newsToTopicProb.get(e).getProbability());*/
		
	}
}
