import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * This class is to sort the news id for same user before aggregating the time spend and the events 
 * or consecutive records (for Omni2014) with same user and news id due to the events and time spend 
 * are logged as separated records
 * Expensive time complexity due to use of map
 * @author Xing Zhao
 *
 */
public class aggreTimeEventPX {

	public static void main(String[] args) {
		String input = "subscribedUser.txt";
		String output = "pre_aggSubscribedUser.txt";
		
		try {
			aggregatorPX(input, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.print("Pre-Aggregation,group news ids for same user Completed!");
	}
	
	public static void aggregatorPX(String input, String output) throws IOException{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		StringBuilder buffer;
		//scan the input file to load it into memory
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		/*user map to <news, newsInfo> */
		HashMap<String, HashMap<String, ArrayList<String>>> userPerDay = new HashMap<String, HashMap<String, ArrayList<String>>>();
		/*news to newInfo per user*/
		HashMap<String, ArrayList<String>> newsId;
		ArrayList<String> newsList; // hold news info per news id
		ArrayList<String> userAsIs = new ArrayList<String>(); // keep user in reading order
		/* user to news order list */
		HashMap<String, ArrayList<String>> userNewsAsIs = new HashMap<String, ArrayList<String>>();
		ArrayList<String> getNewsAsIs; // keep news id in reading order per user
		
		String thisLine;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date bufferDate = null;
		// for each line until the end of the file
		while (((thisLine = newsReader.readLine()) != null)) { 
			
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#' || thisLine.contains("+") || thisLine.contains("post_prop9")) {
				continue;
			}
			
			// split the transaction according to the | separator
			String thisSplit[] = thisLine.split("\\|");
			Date current = new Date();
			try {
				 current = df.parse(thisSplit[4]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(bufferDate == null){
				bufferDate = new Date(current.getTime());
			}

			if(bufferDate.compareTo(current) != 0){// another date
				/* write out all stored values */
				buffer = new StringBuilder();
				for(String user: userAsIs){
					HashMap<String, ArrayList<String>> news = userPerDay.get(user);
					getNewsAsIs = userNewsAsIs.get(user);
					for(String news_id: getNewsAsIs){
						ArrayList<String> set = news.get(news_id);	
						for(String rest: set){
							buffer.append(user + "|");
							buffer.append(news_id + "|");
							buffer.append(rest + "\n");
						}
					}
				}
				writer.write(buffer.toString());
				// reset map, arrays
				userPerDay.clear();
				userNewsAsIs.clear();
				userAsIs.clear();
				bufferDate = new Date(current.getTime());
/*				userPerDay = new HashMap<String, HashMap<String, ArrayList<String>>>();
				userNewsAsIs = new HashMap<String, ArrayList<String>>();
				userAsIs = new ArrayList<String>();*/

			}
			
			/* aggregate when userid and newsid are same */
			//System.out.println(userPerDay.containsKey(thisSplit[0])+thisSplit[0]+"\t"+thisSplit[1]);
			if(userPerDay.containsKey(thisSplit[0])){// existing user ID
				
				newsId = userPerDay.get(thisSplit[0]);
				String newsSet = thisSplit[2] + "|" + thisSplit[3] + "|" + thisSplit[4];
				
				if(newsId.containsKey(thisSplit[1])){// existing news ID
					newsList = newsId.get(thisSplit[1]);
					newsList.add(newsSet);
							
				}else{// new news ID
					// update news for user
					newsList = new ArrayList<String>();
					newsList.add(newsSet);
					newsId.put(thisSplit[1], newsList);
					// add news in reading order
					getNewsAsIs = userNewsAsIs.get(thisSplit[0]);
					getNewsAsIs.add(thisSplit[1]);
				}
				
			}else{// new user ID
				newsId = new HashMap<String, ArrayList<String>>();
				newsList = new ArrayList<String>(); 
				String newsSet = thisSplit[2] + "|" + thisSplit[3] + "|" + thisSplit[4];
				// add user
				newsList.add(newsSet);
				newsId.put(thisSplit[1], newsList);
				userPerDay.put(thisSplit[0], newsId);
				userAsIs.add(thisSplit[0]);
				// add news in reading order per user
				getNewsAsIs = new ArrayList<String>(); 
				getNewsAsIs.add(thisSplit[1]);
				userNewsAsIs.put(thisSplit[0], getNewsAsIs);
			}
			
		}
		

		
		if(!userPerDay.isEmpty()){
			/* write out rest of sorted values */
			buffer = new StringBuilder();
			for(String user: userAsIs){
				HashMap<String, ArrayList<String>> news = userPerDay.get(user);
				if(news == null){
					System.out.println("news null");
				}
				getNewsAsIs = userNewsAsIs.get(user);
				for(String news_id: getNewsAsIs){
					
					ArrayList<String> set = news.get(news_id);
					
					if(set == null){
						System.out.println("set null");
						System.out.println(news_id);
					}
					for(String rest: set){
						buffer.append(user + "|");
						buffer.append(news_id + "|");
						buffer.append(rest + "\n");
					}
				}
			}
			writer.write(buffer.toString());
		}
		newsReader.close();
		writer.close();
	}
}
