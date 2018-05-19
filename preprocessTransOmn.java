import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is to preprocess the data exported from VIEW huiset in psql of
 * omniture 2014. Aggregate the same user's history in one day into one
 * transaction. Note: a user may appears in multiple transactions in one day.
 * date_time is converted to the number of days to the 1st date of the year.
 * 
 * @author Xing Zhao
 *
 */

public class preprocessTransOmn {

	public static void main(String[] args) {
		String input = "aggTimeSubscribedUser_L5.txt";
		String output = "trans_subscribedUsers.txt";
		try {
			preprocess(input, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Scan the raw file from query and aggregate the news ID for a same user in
	 * a day. Convert the date_time to days count up to the 1st date of the
	 * year.
	 * 
	 * @param input
	 *            , raw file from query
	 * @param output
	 *            , aggregated transactions file
	 * @throws IOException
	 *             , mismatched date format
	 */
	public static void preprocess(String input, String output)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		StringBuilder transBuffer = new StringBuilder();
		StringBuilder newsIDbuff = new StringBuilder();
		StringBuilder sptimeBuff = new StringBuilder();
		StringBuilder eventsBuff = new StringBuilder();
		// scan the input file to load it into memory
		BufferedReader newsReader = new BufferedReader(new FileReader(input));
		String thisLine;
		String preUser = "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date bufferDate = null;
		// 1st date of year 2014
		//Date dayOne = new Date();
		/*try {
			dayOne = df.parse("2013-12-31");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		//long days; // days between 1st date to the calculated date
		int transID = 1;

		String title = "# transId : userID : newsID set: sptime: events: date";
		writer.write(title);
		writer.newLine();

		// for each line (transactions) until the end of the file
		while (((thisLine = newsReader.readLine()) != null)) {
			if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
					|| thisLine.contains("post_prop9")
					|| thisLine.contains("+")) {
				continue;
			}
			// split the transaction according to the | separator
			String split[] = thisLine.split("\\|");
			// System.out.println(thisLine + "\t" + split.length);
			String user = split[0].trim();
			String sptime = split[2].trim();
			String events = split[3].trim();

			sptime = sptime.isEmpty() ? "0" : sptime;
			//events = events.isEmpty() ? "0" : events;
			events = events.isEmpty() ? "0" : "100"; // any events considered as 100
			
			Date currentDate = new Date();
			try {
				currentDate = df.parse(split[4].trim());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// startDate. 1st line
			if (bufferDate == null) {
				bufferDate = new Date(currentDate.getTime());
				transBuffer.append(transID + ":");
				preUser = user;
				transBuffer.append(user + ":"); // add user
				newsIDbuff.append(split[1].trim() + " ");
				sptimeBuff.append(sptime + " ");
				eventsBuff.append(events + " ");
				continue;
			}
			// normal iteration
			if (currentDate.equals(bufferDate) && preUser.equals(user)) {
				newsIDbuff.append(split[1].trim() + " ");
				sptimeBuff.append(sptime + " ");
				eventsBuff.append(events + " ");
			} else {
				// date and user are not equal, write out record
				transBuffer.append(newsIDbuff.toString().trim() + ":");
				transBuffer.append(sptimeBuff.toString().trim() + ":");
				transBuffer.append(eventsBuff.toString().trim() + ":");
				//days = daysBetween(bufferDate, dayOne);
				transBuffer.append(df.format(bufferDate));
				writer.write(transBuffer.toString());
				writer.newLine();
				transID++;
				// reset buffer
				transBuffer = new StringBuilder();
				newsIDbuff = new StringBuilder();
				sptimeBuff = new StringBuilder();
				eventsBuff = new StringBuilder();
				// startDate
				bufferDate = new Date(currentDate.getTime());
				transBuffer.append(transID + ":");
				preUser = user;
				transBuffer.append(user + ":"); // add user
				newsIDbuff.append(split[1].trim() + " ");
				sptimeBuff.append(sptime + " ");
				eventsBuff.append(events + " ");
			}

		}

		// write out last record
		transBuffer.append(newsIDbuff.toString().trim() + ":");
		transBuffer.append(sptimeBuff.toString().trim() + ":");
		transBuffer.append(eventsBuff.toString().trim() + ":");
		//days = daysBetween(bufferDate, dayOne);
		transBuffer.append(df.format(bufferDate));
		writer.write(transBuffer.toString());
		writer.newLine();
		// end of preprocessing
		newsReader.close();
		writer.close();
	}

	/**
	 * Calculate the days between two dates
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

}
