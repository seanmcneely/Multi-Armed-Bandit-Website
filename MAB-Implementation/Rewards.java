import java.util.HashMap;
import java.util.Map;

public class Rewards {
	
	private static Map<String, Double> rewards;
	private static Map<String, String> redirects;
	private static Integer numVersions;

	static {
		rewards = new HashMap< String, Double>(); 
		rewards.put("FILTER_PROJECTS", 2.0);
		rewards.put("MORE_PROJECTS", 5.0);
		rewards.put("CONTACT", 3.0);
		redirects = new HashMap<String, String>();
		redirects.put("CONTACT", "contact");
		numVersions = new Integer(2);
	}
	
	public static Double getReward(String s) {
		return rewards.get(s);
	}
	
	public static String getRedirect(String s) {
		return redirects.get(s);
	}
	
	public static Integer getNumVersions() {
		return numVersions;
	}
}
