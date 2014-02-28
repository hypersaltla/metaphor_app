package metaphorDemo;

import java.io.*;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.simple.*;

public class MetaphorServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
		{
			String type = request.getParameter("type");
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("utf8");
			
			if(type == null) {
				response.setContentType("text/html");
				String path = getServletContext().getRealPath("/index.html");
				BufferedReader fr = new BufferedReader(new FileReader(path));
				StringBuffer stb = new StringBuffer();
				String line = null;
				while((line = fr.readLine()) != null) {
					stb.append(line);
				}
				//out.println(path);
				out.println(stb); //it works!
				fr.close();
			}
			else if(type.equals("fetch")) {
				response.setContentType("application/json");
				String method = request.getParameter("method");
				String lang = request.getParameter("lang");
				
				int start = request.getParameter("start") != null? Integer.parseInt(request.getParameter("start")) : -1;
				int end = request.getParameter("end") != null ? Integer.parseInt(request.getParameter("end")) : 1;
				int size;
				if(method.equals("metaphor")) {
					size = MetaphorCorpus.getSizeOfMetaphor(lang);
				}
				else if(method.equals("source")) {
					size = MetaphorCorpus.getSizeOfSources(lang);
				}
				else {
					out.print("[]");
					return;
				}
				assert(end > 0);
				if(start >= size || end <= 0 || !(lang.equals("EN") || lang.equals("SP") || lang.equals("RU") || lang.equals("FA"))) { //in the future, needs to clean the param filter code
					out.print("[]");
					return;
				}
				
				end = end <= size ? end : size; //update the end parameter if near the end
				if (method.equals("source")) {
					JSONArray source_list = MetaphorCorpus.getSourcesAtRange(start, end, lang);
					JSONObject st_mappings = MetaphorCorpus.getSourceTargetMappings(lang, source_list);
					JSONObject result = new JSONObject();
					result.put("total_size", size);
					result.put("source_list", source_list);
					result.put("st_mappings", st_mappings);
					out.print(result);
				}
				else {
					String ids = request.getParameter("ids");
					JSONArray id_list = new JSONArray();
					JSONArray items = null;
					if(ids != null) {
						String[] id_arr = ids.split(",");
						for(String id : id_arr) {
							id_list.add(id);
						}
						items = MetaphorCorpus.getItemsByIndexes(lang, id_list);
					}
					else {
						items = MetaphorCorpus.getItemsAtRange(start, end, lang);
						for(int i = start; i < end; i++) {
							id_list.add(i);
						}
					}
					JSONObject result = new JSONObject();
					result.put("total_size", size);
					result.put("id_list", id_list);
					result.put("items", items);
					out.print(result);
					
				}
			}
			else if(type.equals("domain")) {
				response.setContentType("application/json");
				String query = request.getParameter("domain");
				String lang = request.getParameter("lang");
				DomainStat inst = DomainStat.getInstance();
				try {
					out.print(inst.get(query, lang));
				}
				catch(NullPointerException e) {
					out.print("[]");
				}
			}
			else if(type.equals("sim")) {
				String sentence = request.getParameter("metaphor");
				String lang = request.getParameter("lang");
				response.setContentType("application/json");
				if(sentence != null) {
					JSONArray searchResults = SimVecBean.getSearchResults(sentence, lang);
					out.print(searchResults);
				}
				else {
					out.print("[]");
				}
			}
			/*
			else if(type.equals("testcnt")) {
				response.setContentType("text/html");
				MetaphorCorpus.clickCnt += 1;
				out.print(MetaphorCorpus.clickCnt);
			}*/
			else {
				out.println("<html><body><p>Wrong Request!</p></body></html>");
			}
		}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
		{
			doGet(request, response);
		}
		
}
