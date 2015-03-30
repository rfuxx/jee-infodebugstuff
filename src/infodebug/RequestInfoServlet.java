package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestInfoServlet extends HttpServlet {
	private static final long serialVersionUID = -9121067131137697255L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Request Information</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");

		out.println("<h1>Request Information</h1>");

		out.println("<h2>General</h2>");
		out.println("<table>");
		printGeneralRequestInfo(request, "getMethod", out);
		printGeneralRequestInfo(request, "getRequestURI", out);
		printGeneralRequestInfo(request, "getProtocol", out);
		printGeneralRequestInfo(request, "getRemoteAddr", out);
		printGeneralRequestInfo(request, "getRemoteHost", out);
		printGeneralRequestInfo(request, "getRemotePort", out);
		printGeneralRequestInfo(request, "getLocalAddr", out);
		printGeneralRequestInfo(request, "getLocalName", out);
		printGeneralRequestInfo(request, "getLocalPort", out);
		printGeneralRequestInfo(request, "getAuthType", out);
		printGeneralRequestInfo(request, "getRemoteUser", out);
		printGeneralRequestInfo(request, "getRequestedSessionId", out);
		printGeneralRequestInfo(request, "getScheme", out);
		printGeneralRequestInfo(request, "getServerName", out);
		printGeneralRequestInfo(request, "getServerPort", out);
		printGeneralRequestInfo(request, "getContextPath", out);
		printGeneralRequestInfo(request, "getServletPath", out);
		printGeneralRequestInfo(request, "getPathInfo", out);
		printGeneralRequestInfo(request, "getQueryString", out);
		printGeneralRequestInfo(request, "getPathTranslated", out);
		printGeneralRequestInfo(request, "getContentType", out);
		printGeneralRequestInfo(request, "getCharacterEncoding", out);
		printGeneralRequestInfo(request, "getLocale", out);
		out.println("</table>");

		Enumeration<String> e = request.getHeaderNames();
		out.println("<h2>HTTP Headers</h2>");
		out.println("<table>");
		out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Header</th><th>Value</th></tr>");
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			Enumeration<String> headers = request.getHeaders(key);
			while(headers.hasMoreElements()) {
				out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(headers.nextElement()) + "</td></tr>");
			}
		}
		out.println("</table>");

		e = request.getParameterNames();
		if(e.hasMoreElements()) {
			out.println("<h2>Parameters</h2>");
			out.println("<table>");
			out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Parameter</th><th>Value</th></tr>");
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				String[] values = request.getParameterValues(key);
				for(String value : values) {
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + ": </td><td>" + text2html(value) + "</td></tr>");
				}
			}
			out.println("</table>");
		}

		try {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				out.println("<h2>Cookies</h2>");
				out.println("<table>");
				out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Cookie</th><th>Value</th></tr>");
				for(Cookie c : cookies) {
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(c.getName()) + "</td><td>" + text2html(c.getValue()) + "</td></tr>");
				}
				out.println("</table>");
			}
		} catch (Exception ex) {
			out.println("<p>get cookies: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

		try {
			HttpSession session = request.getSession();
			e = session.getAttributeNames();
			if(e.hasMoreElements()) {
				out.println("<h2>HttpSession Data</h2>");
				out.println("<table>");
				out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Session Key</th><th>Value</th></tr>");
				while(e.hasMoreElements()) {
					String key = e.nextElement();
					Object attr = session.getAttribute(key);
					String attrval = attr != null ? attr.toString() : null;
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(attrval) + "</td></tr>");
				}
				out.println("</table>");
			}
		} catch (Exception ex) {
			out.println("<p>get session: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

		try {
			e = request.getAttributeNames();
			if(e.hasMoreElements()) {
				out.println("<h2>Request Attributes</h2>");
				out.println("<table>");
				out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Attribute</th><th>Value</th></tr>");
				while(e.hasMoreElements()) {
					String key = e.nextElement();
					Object attr = request.getAttribute(key);
					String attrval = attr != null ? attr.toString() : null;
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(attrval) + "</td></tr>");
				}
				out.println("</table>");
			}
		} catch (Exception ex) {
			out.println("<p>get attributes: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

		try {
			e = getInitParameterNames();
			if(e.hasMoreElements()) {
				out.println("<h2>Servlet Init Parameters</h2>");
				out.println("<table>");
				out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Servlet Init Parameter</th><th>Value</th></tr>");
				while(e.hasMoreElements()) {
					String key = e.nextElement();
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(this.getInitParameter(key)) + "</td></tr>");
				}
				out.println("</table>");
			}
		} catch (Exception ex) {
			out.println("<p>get init parameter names: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

		try {
			ServletContext servletcontext = this.getServletContext();
			if(servletcontext != null) {
				try {
					e = servletcontext.getAttributeNames();
					if(e.hasMoreElements()) {
						out.println("<h2>Context Attributes</h2>");
						out.println("<table>");
						out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Context Attribute</th><th>Value</th></tr>");
						while(e.hasMoreElements()) {
							String key = e.nextElement();
							Object attr = servletcontext.getAttribute(key);
							String attrval = attr != null ? attr.toString() : null;
							out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(attrval) + "</td></tr>");
						}
						out.println("</table>");
					}
				} catch (Exception ex) {
					out.println("<p>get servlet context attributes: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
					ex.printStackTrace();
				}

				try {
					e = servletcontext.getInitParameterNames();
					if(e.hasMoreElements()) {
						out.println("<h2>Servlet Context Init Parameters</h2>");
						out.println("<table>");
						out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Servlet Context Init Parameter</th><th>Value</th></tr>");
						while(e.hasMoreElements()) {
							String key = e.nextElement();
							out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(key) + "</td><td>" + text2html(servletcontext.getInitParameter(key)) + "</td></tr>");
						}
						out.println("</table>");
					}
				} catch (Exception ex) {
					out.println("<p>get servlet context init parameters: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			out.println("<p>get servlet context: <em>" + text2html(ex.getClass().getName() + ": " + ex.getMessage()) + "</em></p>");
			ex.printStackTrace();
		}

		out.println("</body></html>");
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static void printGeneralRequestInfo(HttpServletRequest request, String what, PrintWriter out) {
		try {
			Method m = HttpServletRequest.class.getMethod(what);
			Object result = m.invoke(request);
			if(result != null) {
				out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + what + "()</td><td>" + text2html(result.toString()) + "</td></tr>");
			}
			// print nothing if null in order to keep output short
		} catch (Exception e) {
			out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + what + "()</td><td><em>" + text2html(e.getClass().getName() + ": " + e.getMessage()) + "</em></td></tr>");
			e.printStackTrace();
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
