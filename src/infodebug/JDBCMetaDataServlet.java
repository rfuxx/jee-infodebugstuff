package infodebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

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
import javax.sql.DataSource;

public class JDBCMetaDataServlet extends HttpServlet {
	private static final long serialVersionUID = 6234929181718167085L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>JDBC Meta Data</title>");
		out.println("<style type=\"text/css\">table tr:nth-child(odd) {color: #000; background: #FFF} table tr:nth-child(even) {color: #000; background: #E0E0E0}</style>");
		out.println("</head><body>");

		out.println("<h1>Driver Manager Information</h1>");
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		out.println("<table>");
		out.println("<tr><th nowrap=\"nowrap\" valign=\"top\">Driver Name</th><th>Major.Minor Version</th></tr>");
		while(drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			//out.println("<h2>" + text2html(driver.getClass().getName()) + "</h2>");
			out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">" + text2html(driver.getClass().getName()) + "</td><td>" + driver.getMajorVersion() +"." + driver.getMinorVersion() + "</td></tr>");
		}
		out.println("</table>");

		Vector<String> dataSourceNames = new Vector<String>();
		InitialContext ic;
		try {
			ic = new InitialContext();
			scanForDataSources(ic.listBindings(""), dataSourceNames, "");
			if(!dataSourceNames.isEmpty()) {
				out.println("<h1>Data Source Information</h1>");
			}
			for(String dsName : dataSourceNames) {
				try {
					DataSource ds = (DataSource) ic.lookup(dsName);
					out.println("<h2>" + text2html(dsName) + "</h2>");
					Connection connection = ds.getConnection();
					DatabaseMetaData dbmd = connection.getMetaData();
					out.println("<table>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Database Product Name</td><td>" + text2html(dbmd.getDatabaseProductName()) + "</td></tr>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Database Product Version</td><td>" + text2html(dbmd.getDatabaseProductVersion()) + "</td></tr>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Database Major.Minor Version</td><td>" + dbmd.getDatabaseMajorVersion() + "." + dbmd.getDatabaseMinorVersion() + "</td></tr>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Driver Name</td><td>" + text2html(dbmd.getDriverName()) + "</td></tr>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Driver Version</td><td>" + text2html(dbmd.getDriverVersion()) + "</td></tr>");
					out.println("<tr><td nowrap=\"nowrap\" valign=\"top\">Driver Major.Minor Version</td><td>" + dbmd.getDriverMajorVersion() +"." + dbmd.getDriverMinorVersion() + "</td></tr>");
					out.println("</table>");
				} catch (SQLException e) {
					out.println("<pre>");
					out.println(text2html(safePrintStackTrace(e)));
					out.println("</pre>");
				}
			}
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		out.println("</body></html>");
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void scanForDataSources(NamingEnumeration ne, Vector<String> out, String contextname) throws NamingException {
		while(ne.hasMore()) {
			Object o = ne.next();
			if (o instanceof Binding) {
				Binding b  = (Binding) o;
				String name = b.getName();
				Object bo = b.getObject();
				if (bo instanceof DataSource) {
					out.add(contextname+"/"+name);
				} else if(bo instanceof Context) {
					try {
						scanForDataSources(((Context) bo).listBindings(""), out, contextname+"/"+name);
					} catch (NoPermissionException e) {
						//e.printStackTrace();
					}
				}
			}
		}
	}

	private static String safePrintStackTrace(Throwable e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.getBuffer().toString();
	}
	private static String text2html(String text) {
		if(text != null ) {
			return text.replace("&","&amp;").replace(">","&gt;").replace("<","&lt;").replace("'","&#039;").replace("\"","&#034;").replace("\n", "<br/>");
		} else {
			return "";
		}
	}	// probably inefficient implementation but local here, so it does not create additional dependencies
}
