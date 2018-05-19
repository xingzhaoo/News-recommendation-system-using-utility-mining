package dataUnit;

import java.text.DecimalFormat;

/**
 * Topic Rule
 * @author Xing Zhao
 *
 */
public class TRule {
	private String topic1; // antecedent
	private String topic2; // consequent
	private double probConf; // P(t2|t1)
	
	/**
	 * Constructor
	 * @param topic1	antecedent
	 * @param topic2	consequent
	 * @param probConf	P(t2|t1)
	 */
	public TRule(String topic1, String topic2, double probConf) {
		super();
		this.topic1 = topic1;
		this.topic2 = topic2;
		this.probConf = probConf;
	}

	/**
	 * @return the topic1
	 */
	public String getTopic1() {
		return topic1;
	}

	/**
	 * @param topic1 the topic1 to set
	 */
	public void setTopic1(String topic1) {
		this.topic1 = topic1;
	}

	/**
	 * @return the topic2
	 */
	public String getTopic2() {
		return topic2;
	}

	/**
	 * @param topic2 the topic2 to set
	 */
	public void setTopic2(String topic2) {
		this.topic2 = topic2;
	}

	/**
	 * @return the probConf
	 */
	public double getProbConf() {
		return probConf;
	}

	/**
	 * @param probConf the probConf to set
	 */
	public void setProbConf(double probConf) {
		this.probConf = probConf;
	}
	
	/**
	 * Return a String representation of this rule
	 * 
	 * @return a String
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		// write topic 1
		buffer.append(topic1);
		// write separator
		buffer.append("==>");
		// write topic 2
		buffer.append(topic2);
		// write topic confidence
		buffer.append("#TCONF:");
		buffer.append(doubleToString(probConf));	
		return buffer.toString();
	}

	/**
	 * Convert a double value to a string with only five decimal
	 * 
	 * @param value
	 *            a double value
	 * @return a string
	 */
	String doubleToString(double value) {
		// convert it to a string with two decimals
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(5);
		return format.format(value);
	}
	
}
