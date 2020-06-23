

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Rewarder
 */
@WebServlet("/Rewarder")
public class Rewarder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Rewarder() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String rewardType = request.getParameter("rewardType");
		String versionString = request.getParameter("version");
		if(rewardType == null || versionString == null) {
			showRewardErrorPage(out);
			return;
		}
		Integer siteVersion;
		try {
			siteVersion = Integer.parseInt(versionString);
		}
		catch (NumberFormatException e) {
			showRewardErrorPage(out);
			return;
		}
		reward(rewardType, siteVersion, out);
		redirect(rewardType, siteVersion, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void redirect(String rewardType, Integer siteVersion, HttpServletResponse response) {
		String pagename = Rewards.getRedirect(rewardType);
		if(pagename != null) {
			String redirectHtml = pagename + siteVersion.toString() + ".html";
			try {
				response.sendRedirect(redirectHtml);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void reward(String rewardType, Integer siteVersion, PrintWriter out) {
		Double reward = Rewards.getReward(rewardType);
		Integer numVersions = Rewards.getNumVersions();
		
		if(reward == null || siteVersion < 1 || siteVersion > numVersions) {
			showRewardErrorPage(out);
			return;
		}
		rewardVerified(rewardType, reward, siteVersion);
	}
	
	private void rewardVerified(String rewardType, Double reward, Integer siteVersion) {
		String appRewardAttributeString = "r" + siteVersion.toString();
		ServletContext application = getServletConfig().getServletContext();
		Double totalReward = (Double) application.getAttribute(appRewardAttributeString);
		if(totalReward == null) {
			totalReward = 0.0;
		}
		totalReward += reward;
		application.setAttribute(appRewardAttributeString, totalReward);
		
		System.out.println("Reward type: " + rewardType + siteVersion.toString() + " Value: " + reward.toString());
		System.out.println("Total reward: " + totalReward.toString());
	}
	
	private void showRewardErrorPage(PrintWriter out) {
		out.println("An error occured, please press the back button or exit the page");
	}
}
