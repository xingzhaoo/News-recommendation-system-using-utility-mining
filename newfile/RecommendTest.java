import java.io.IOException;
import dataUnit.TRules;

/**
 * Test class RecommendNews 
 * @author Xing Zhao
 *
 */
public class RecommendTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//String assocNewsRule = "../trans_assoc_rule_HUImined_cf60_es.txt";
		String HUIpattern = "../trans_HUImined_UARY.txt";
		String newsTopicDB = "BB_187_topic_dist.txt";
		String output = "../topicRules.txt";
		double minPconf = 0.006;
		int DButility = 51462641;
		
		/* mining the news rules */
		double min_utility_conf = 0.40;
		AssocRulesHUIminerES algoNWRules = new AssocRulesHUIminerES();	
		Rules newsRule = algoNWRules.runAlgorithm(HUIpattern, output, min_utility_conf);
		Rules newsRecRules = algoNWRules.getRecNewsRule();
		algoNWRules.printStats();
		
		/* mining the topic rules */
		AssocRulesTopics algTopicRules = new AssocRulesTopics();
		//algTopicRules.loadNewsRules(assocNewsRule);
		algTopicRules.loadNewsRules(newsRule);
		TRules tRules = algTopicRules.runAlgorithm(HUIpattern, newsTopicDB, output, DButility, minPconf);
		
		algTopicRules.printStats();
		
		/* recommendation starts*/
		String userSession = "../trans_users_HUIt5k.txt";
		String recommendOutput = "../recommendNews.txt";
		String newsStamps = "../newsPublishDate.txt";
		
		RecommendNews recommender = new RecommendNews();
		recommender.newsRecOnTopics(userSession, recommendOutput,newsRecRules, tRules, newsTopicDB, newsStamps);
		recommender.printStats();

	}
}
