package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
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
		if(request.getMethod() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getMethod()</td><td>" + text2html(request.getMethod()) + "</td></tr>");
		if(request.getRequestURI() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRequestURI()</td><td>" + text2html(request.getRequestURI()) + "</td></tr>");
		if(request.getProtocol() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getProtocol()</td><td>" + text2html(request.getProtocol()) + "</td></tr>");
		if(request.getRemoteAddr() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRemoteAddr()</td><td>" + text2html(request.getRemoteAddr()) + "</td></tr>");
		if(request.getRemoteHost() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRemoteHost()</td><td>" + text2html(request.getRemoteHost()) + "</td></tr>");
		if(request.getRemotePort() != 0) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRemotePort()</td><td>" + request.getRemotePort() + "</td></tr>");
		if(request.getLocalAddr() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getLocalAddr()</td><td>" + text2html(request.getLocalAddr()) + "</td></tr>");
		if(request.getLocalName() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getLocalName()</td><td>" + text2html(request.getLocalName()) + "</td></tr>");
		if(request.getLocalPort() != 0) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getLocalPort()</td><td>" + request.getLocalPort() + "</td></tr>");
		if(request.getAuthType() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getAuthType()</td><td>" + text2html(request.getAuthType()) + "</td></tr>");
		if(request.getRemoteUser() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRemoteUser()</td><td>" + text2html(request.getRemoteUser()) + "</td></tr>");
		if(request.getRequestedSessionId() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getRequestedSessionId()</td><td>" + text2html(request.getRequestedSessionId()) + "</td></tr>");
		if(request.getScheme() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getScheme()</td><td>" + text2html(request.getScheme()) + "</td></tr>");
		if(request.getServerName() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getServerName()</td><td>" + text2html(request.getServerName()) + "</td></tr>");
		if(request.getServerPort() != 0) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getServerPort()</td><td>" + request.getServerPort() + "</td></tr>");
		if(request.getContextPath() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getContextPath()</td><td>" + text2html(request.getContextPath()) + "</td></tr>");
		if(request.getServletPath() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getServletPath()</td><td>" + text2html(request.getServletPath()) + "</td></tr>");
		if(request.getPathInfo() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getPathInfo()</td><td>" + text2html(request.getPathInfo()) + "</td></tr>");
		if(request.getQueryString() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getQueryString()</td><td>" + text2html(request.getQueryString()) + "</td></tr>");
		if(request.getPathTranslated() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getPathTranslated()</td><td>" + text2html(request.getPathTranslated()) + "</td></tr>");
		if(request.getContentType() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getContentType()</td><td>" + text2html(request.getContentType()) + "</td></tr>");
		if(request.getCharacterEncoding() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getCharacterEncoding()</td><td>" + text2html(request.getCharacterEncoding()) + "</td></tr>");
		if(request.getLocale() != null) out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">getLocale()</td><td>" + text2html(request.getLocale().toString()) + "</td></tr>");
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

		ServletContext servletcontext = this.getServletContext();
		if(servletcontext != null) {
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
