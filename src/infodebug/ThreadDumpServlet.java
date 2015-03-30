package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThreadDumpServlet extends HttpServlet {
	private static final long serialVersionUID = 7142307182007483639L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Thread Dump</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");
		out.println("<h1>Thread Dump</h1>");

		try {
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			try {
				long[] deadlocked = bean.findDeadlockedThreads();
				if (deadlocked == null || deadlocked.length == 0) {
					out.println("No deadlocked threads were found.<br/>");
				} else {
					out.print("Found deadlocked Threads: " + deadlocked[0]);
					for(int i=1; i<deadlocked.length; i++) {
						out.print(", " + deadlocked[i]);
					}
					out.println(".<br/>");
				}
			} catch (Exception ex) {
				out.println("<p>Sorry, deadlocked threads check via java.lang.management failed due to: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
				ex.printStackTrace();
			}
			try {
				long[] deadlocked = bean.findMonitorDeadlockedThreads();
				if (deadlocked == null || deadlocked.length == 0) {
					out.println("No monitor deadlocked threads were found.<br/>");
				} else {
					out.print("Found monitor deadlocked Threads: " + deadlocked[0]);
					for(int i=1; i<deadlocked.length; i++) {
						out.print(", " + deadlocked[i]);
					}
					out.println(".<br/>");
				}
			} catch (Exception ex) {
				out.println("<p>Sorry, deadlocked threads check via java.lang.management failed due to: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
				ex.printStackTrace();
			}
	
			try {
				ThreadInfo[] ti = bean.dumpAllThreads(true, true);
				
				for(ThreadInfo thread : ti) {
					MonitorInfo[] mi = thread.getLockedMonitors();
					LockInfo[] li = thread.getLockedSynchronizers();
					StackTraceElement[] st = thread.getStackTrace();
					LockInfo lock = thread.getLockInfo();
					Thread.State state = thread.getThreadState();
					out.println("<h3>" + thread.getThreadName() + " id=" + thread.getThreadId() + " " + state + "</h3>");
					out.println("<table>");
					if(li != null && li.length > 0) {
						out.print("<tr><td><em>holding synchronizer &lt;0x" + Integer.toHexString(li[0].getIdentityHashCode()) + "&gt; ");
						for(int i=1; i<li.length; i++) {
							out.print(", &lt;0x" + Integer.toHexString(li[i].getIdentityHashCode()) +  "&gt;");
						}
						out.println("</em></td></tr>");
					}
					if(lock != null) {
						switch(state) {
							case WAITING:
							case TIMED_WAITING:
								out.println("<tr><td><em>waiting on &lt;0x" + Integer.toHexString(lock.getIdentityHashCode()) + "&gt; " + text2html(lock.toString()) + "</em></td></tr>");
								break;
							case BLOCKED:
								out.println("waiting to lock <0x" + Integer.toHexString(lock.getIdentityHashCode()) + "> " + text2html(lock.toString()) + " which is held by " + text2html(thread.getLockOwnerName()));
								break;
						}
					}
					for(int i=0; i<st.length; i++) {
						StackTraceElement ste = st[i];
						out.print("<tr><td nowrap=\"nowrap\" valign=\"top\">&nbsp;at " + text2html(ste.toString()));
						for (MonitorInfo moni : mi) {
							if(moni.getLockedStackDepth() == i)
							out.print("<br/><em>&nbsp;&nbsp;- locked &lt;0x" + moni.getIdentityHashCode() + "&gt; " + text2html(moni.toString()) + "</em>");
						}
						out.println("</td></tr>");
					}
					out.println("</table>");
					out.println("<br />");
				}
			} catch (Exception ex) {
				out.println("<p>Sorry, thread dump via java.lang.management failed due to: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			out.println("<p>Sorry, attempt to query the java.lang.management ThreadMXBean failed due to: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
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
