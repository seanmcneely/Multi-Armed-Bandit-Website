import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Bandit
 */
@WebServlet("/Bandit")
public class Bandit extends HttpServlet {
	private static final long serialVersionUID = 1L;    
    /**
     * @see HttpServlet#HttpServlet()
     */
	private static final Integer epsilon = 20;
	private static final boolean greedy = false;
	
    public Bandit() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer numVersions = Rewards.getNumVersions();
		Double[] siteRewards = new Double[numVersions];
		Integer[] numVisits = new Integer[numVersions];
		ServletContext application = getServletConfig().getServletContext();
		
		List<Double> sitePerformances = calculateSiteAverageRewards(numVersions, siteRewards, numVisits, application);
		servePageBandit(numVersions, siteRewards, numVisits, application, sitePerformances, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void servePageBandit(Integer numVersions, Double[] siteRewards, Integer[] numVisits, ServletContext application, List<Double> sitePerformances, HttpServletResponse response) {
		if(greedy) {
			servePageEpsilonGreedy(numVersions, siteRewards, numVisits, application, sitePerformances, response);
		}
		else {
			servePageUCB(numVersions, siteRewards, numVisits, application, sitePerformances, response);
		}
	}

	private void servePageUCB(Integer numVersions, Double[] siteRewards, Integer[] numVisits, ServletContext application, List<Double> sitePerformances, HttpServletResponse response) {
		List<Double> upperConfidenceBounds = calculateUCB(sitePerformances, numVisits);
		List<Integer> maxElementIndices = findMaxElementIndices(upperConfidenceBounds);
		serveFromBestPages(maxElementIndices, numVisits, sitePerformances, application, response);
	}

	private List<Double> calculateUCB(List<Double> sitePerformances, Integer[] numVisits) {
		List<Double> UCB = new ArrayList<Double>();
		Integer timeStep = sumSiteVisits(numVisits);
		
		for(int i = 0; i < sitePerformances.size(); i++) {
			Double siteUCB = sitePerformances.get(i) + Math.sqrt( 2 * Math.log(timeStep) / numVisits[i] );
			System.out.println("UCB " + i + ": " + siteUCB);
			UCB.add(siteUCB);
		}
		
		return UCB;
	}

	private void servePageEpsilonGreedy(Integer numVersions, Double[] siteRewards, Integer[] numVisits, ServletContext application, List<Double> sitePerformances, HttpServletResponse response) {
		Random rand = new Random();
		Integer servedPageIndex;
		Integer exploreOrExploit = rand.nextInt(100);
		if(exploreOrExploit < epsilon) {
			servedPageIndex = rand.nextInt(numVersions);
			System.out.println("serving random page... ");
			serve(servedPageIndex, numVisits, application, response);
		}
		else {
			List<Integer> maxElementIndices = findMaxElementIndices(sitePerformances);
			System.out.println("serving BEST page...");
			serveFromBestPages(maxElementIndices, numVisits, sitePerformances, application, response);
		}
	}
	
	private void serveFromBestPages(List<Integer> maxElementIndices, Integer[] numVisits, List<Double> sitePerformances, ServletContext application, HttpServletResponse response) {
		Random rand = new Random();
		Integer servedPageIndex;
		if(maxElementIndices.size() == 1) {
			servedPageIndex = maxElementIndices.get(0);
		}
		else {
			Integer randomBestPage = rand.nextInt(maxElementIndices.size());
			servedPageIndex = randomBestPage;
		}
		serve(servedPageIndex, numVisits, application, response);
	}
	
	private void serve(Integer pageIndex, Integer[] numVisits, ServletContext application, HttpServletResponse response) {
		numVisits[pageIndex]++;
		Integer versionNumber = pageIndex + 1;
		String appNumVisitsAttributeString = "n" + versionNumber.toString();
		application.setAttribute(appNumVisitsAttributeString, numVisits[pageIndex]);
		String indexHtmlString = "index" + versionNumber.toString() + ".html";
		try {
			response.sendRedirect(indexHtmlString);
		} catch (IOException e) {
		}
	}
	
	//Returns a list of the indices of the maximum elements in a list
	private List<Integer> findMaxElementIndices(List<Double> myList) {
		Double maxValue = myList.get(0);
		List<Integer> maxIndices = new ArrayList<Integer>();
		
		for(int i = 0; i < myList.size(); i++) {
			if(myList.get(i) > maxValue) {
				maxIndices.clear();
				maxIndices.add(i);
				maxValue = myList.get(i);
			}
			else if(myList.get(i).equals(maxValue)) {
				maxIndices.add(i);
			}
		}
		return maxIndices;
	}
	
	private List<Double> calculateSiteAverageRewards(Integer numVersions, Double[] siteRewards, Integer[] numVisits, ServletContext application){
		List<Double> sitePerformances = new ArrayList<Double>();
		for(int i = 0; i < numVersions; i++) {
			Integer versionNumber = i + 1;
			String appRewardAttributeString = "r" + versionNumber.toString();
			String appNumVisitsAttributeString = "n" + versionNumber.toString();
			siteRewards[i] = (Double) application.getAttribute(appRewardAttributeString);
			numVisits[i] = (Integer) application.getAttribute(appNumVisitsAttributeString);
			
			if(siteRewards[i] == null) {
				siteRewards[i] = 0.0;
				application.setAttribute(appRewardAttributeString, siteRewards[i]);
			}
			if(numVisits[i] == null) {
				numVisits[i] = 1;
				application.setAttribute(appNumVisitsAttributeString, numVisits[i]);
			}
			Double performance = (Double) siteRewards[i] / numVisits[i];
			sitePerformances.add(performance);
		}
		return sitePerformances;
	}
	
	private Integer sumSiteVisits(Integer[] numVisits) {
		Integer sum = 0;
		for(int i = 0; i < numVisits.length; i++) {
			sum += numVisits[i];
		}
		return sum;
	}

}
