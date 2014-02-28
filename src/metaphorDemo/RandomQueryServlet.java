package metaphorDemo;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.*;

public class RandomQueryServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
		{
			String query = request.getParameter("q");
			String lang = request.getParameter("lang");
			JSONArray queryResults = new JSONArray();
			JSONArray details = new JSONArray();
			details.add(query);
			queryResults.add(details);
			//get valence score
			//get domains
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			if(query != null) {
				queryResults.add(SimVecBean.getSearchResults(query, lang));
				out.print(queryResults);
			}
			else {
				out.write("[]");
			}
		}
}
