import java.io.IOException;
import dataUnit.TRules;
import dataUnit.Rules;
import utilityAssocRule.*;
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
		String HUIpattern = "../evaluation/unit5/trans_HUImined_UARY500.txt";
		String newsTopicDB = "BB_187_topic_dist.txt";
		String output = "../evaluation/unit5/topicRules_es_5.txt";
		double minPconf = 0.5;
		int DButility = 656235; // Total Utility of HUIpattern
		
		/* mining the news rules */
		double min_utility_conf = 0.6;
		//AssocRulesHUIminerBK algoNWRules = new AssocRulesHUIminerBK();
		AssocRulesHUIminerES algoNWRules = new AssocRulesHUIminerES();	
		Rules newsRule = algoNWRules.runAlgorithm(HUIpattern, output, min_utility_conf);
		Rules newsRecRules = algoNWRules.getRecNewsRule();
		algoNWRules.printStats();
		
		/* mining the topic rules */
		AssocRulesTopics algTopicRules = new AssocRulesTopics();
		algTopicRules.loadNewsRules(newsRule);
		TRules tRules = algTopicRules.runAlgorithm(HUIpattern, newsTopicDB, output, DButility, minPconf);
		
		algTopicRules.printStats();
		
		/* recommendation starts*/
		String userSession = "../evaluation/unit5/test.txt";
		String recommendOutput = "../evaluation/unit5/recommendNews_ES_p5.txt";
		String newsStamps = "../newsPublishDate.txt";
		
		RecommendNews recommender = new RecommendNews();
		recommender.newsRecOnTopics(userSession, recommendOutput,newsRecRules, tRules, newsTopicDB, newsStamps);
		recommender.printStats();

	}
}
