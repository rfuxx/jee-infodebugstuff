package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SystemPropertyServlet extends HttpServlet {
	private static final long serialVersionUID = -9121067131137697255L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>System Properties</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");

		out.println("<h1>System Properties</h1>");
		Properties sysprops = System.getProperties();
		Enumeration<?> e = sysprops.propertyNames();
		out.println("<table>");
		out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Name</th><th>Value</th></tr>");
		while(e.hasMoreElements()) {
			String key = (String) e.nextElement();
			out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(sysprops.getProperty(key)) + "</td></tr>");
		}
		out.println("</table>");

		out.println("<h1>Java Runtime</h1>");
		Runtime runtime = Runtime.getRuntime();
		out.println("<table>");
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">availableProcessors()</td><td>" + runtime.availableProcessors() + "</td></tr>");
		out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">maxMemory()</td><td>" + runtime.maxMemory() + "</td></tr>");
		out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">totalMemory()</td><td>" + totalMemory + "</td></tr>");
		out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">freeMemory()</td><td>" + freeMemory + "</td></tr>");
		out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">= used memory</td><td>" + (totalMemory - freeMemory) + "</td></tr>");
		out.println("</table>");

		out.println("</body></html>");
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static String text2html(String text) {
		if(text != null ) {
			return text.replace("&","&amp;").replace(">","&gt;").replace("<","&lt;").replace("'","&#039;").replace("\"","&#034;").replace("\n", "<br/>");
		} else {
			return "";
		}
	}	// probably inefficient implementation but local here, so it does not create additional dependencies
}
