package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LightThreadDumpServlet extends HttpServlet {
	private static final long serialVersionUID = 7142307182007483639L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Light Thread Dump</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");
		out.println("<h1>Light Thread Dump</h1>");
		out.println("<em>Warning: This Thread dump is only based on java.lang.Thread.getAllStackTraces(). It does therefore not contain any Java locking information, that's why we call it \"light\". If your JVM is capable of java.lang.management-based thread dumping, please try if <a href=\"threaddump\">Thread Dump</a> works for you.</em>");

		try {
			Map<Thread, StackTraceElement[]> alldumpsMap = Thread.getAllStackTraces();
			for(Thread thread : alldumpsMap.keySet()) {
				out.println("<h3>" + thread.getName() + (thread.isInterrupted() ? "interrupted" : "") + (thread.isDaemon() ? " daemon" : "") + " prio=" + thread.getPriority() + " id=" + thread.getId() + " " + thread.getState() + "</h3>");
				out.println("<table>");
				for(StackTraceElement ste : alldumpsMap.get(thread)) {
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">at " + text2html(ste.toString()) + "</td></tr>");
				}
				out.println("</table>");
				out.println("<br />");
			}
		} catch (Exception ex) {
			out.println("<p>Sorry, thread dump via Thread.getAllStackTraces() failed due to: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

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