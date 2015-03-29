package infodebug;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JNDIDumpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Request Information</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");

		out.println("<h1>JNDI Dump</h1>");
		try {
			InitialContext ic = new InitialContext();
			out.println("<h2>Global</h2>");
			out.println("<table>");
			out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">JNDI Name</th><th>Class</th><th>toString()</th></tr>");
			list(ic.listBindings(""), out, "");
			out.println("</table>");
			out.println("<h2>Comp Env</h2>");
			out.println("<table>");
			out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">JNDI Name</th><th>Class</th><th>toString()</th></tr>");
			list(ic.listBindings("java:comp/env"), out, "java:comp/env");
			out.println("</table>");
		} catch (NamingException e) {
			e.printStackTrace(out);
		}
		out.println("</body></html>");
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void list(NamingEnumeration ne, PrintWriter out, String contextname) throws NamingException {
		while(ne.hasMore()) {
			Object o = ne.next();
			if (o instanceof Binding) {
				Binding b  = (Binding) o;
				String name = b.getName();
				Object bo = b.getObject();
				if(bo instanceof Context) {
					try {
						list(((Context) bo).listBindings(""), out, contextname+"/"+name);
					} catch (NoPermissionException e) {
						out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(contextname+"/"+name+"/*") + "</td><td colspan=\"2\"><em>&lt;no permission&gt;</em></td></tr>");
					}
				} else {
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(contextname+"/"+name) + "</td><td nowrap=\"nowrap\" valign=\"top\">" + text2html(bo.getClass().getName()) + "</td><td>" + text2html(bo.toString()) + "</td></tr>");
				}
			}
		}
	}

	private static String text2html(String text) {
		if(text != null ) {
			return text.replace("&","&amp;").replace(">","&gt;").replace("<","&lt;").replace("'","&#039;").replace("\"","&#034;").replace("\n", "<br/>");
		} else {
			return "";
		}
	}	// probably inefficient implementation but local here, so it does not create additional dependencies
}
