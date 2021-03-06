import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.JSONException;
import twitter4j.JSONObject;


public class TwitterBestTime extends HttpServlet {

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String twuid = request.getParameter("uid");
		String twun = request.getParameter("un");
		
		System.out.println(twuid);
		System.out.println(twun);
		
		Long uid = null;
		if(twuid != null && twuid != ""){
			uid = Long.valueOf(twuid);
		}
		
		String[] bestTimes = TwitterFollowers.fetchBestTimeToPost(uid, twun);
		response.setContentType("application/json");
		
		JSONObject json = new JSONObject();
		try {
			json.put("bestDay", bestTimes[0]);
			json.put("bestTime", bestTimes[1]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		out.print(json);
		out.close();
	}

}
