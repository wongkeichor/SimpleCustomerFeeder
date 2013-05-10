package com.demo.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SampleCustomerLocatorServlet extends HttpServlet
{
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    PrintWriter out = resp.getWriter();
    Connection c = null;
    CustomerView view = new CustomerView();
    String valueViewKey = null;
    try {
      DriverManager.registerDriver(new com.google.appengine.api.rdbms.AppEngineDriver());
      c = DriverManager.getConnection("jdbc:google:rdbms://kenwong-cloud-sql:cloudsql2/GEO");
      valueViewKey = req.getParameter("viewkey");
      if (valueViewKey == "") {
        out.println("<html><head></head><body>You are missing the customer id.  Try again! Redirecting in 3 seconds...</body></html>");
      } else {
        String statement = "SELECT * FROM CustomerView WHERE ViewKey = ?";
        PreparedStatement stmt = c.prepareStatement(statement);
        stmt.setString(1, valueViewKey);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
          view = new CustomerView();
          view.setViewKey(rs.getString("ViewKey"));
          view.setCountry(rs.getString("Country"));
          view.setCustomerName(rs.getString("CustomerName"));
          view.setCompany(rs.getString("Company"));
          view.setOrderProduct(rs.getString("OrderProduct"));
          view.setProblems(rs.getString("Problems"));
          view.setLevel(rs.getInt("Level"));
        } 
      } 
    } catch (SQLException e) {
      e.printStackTrace();
      
      if (c != null)
        try {
          c.close(); } catch (SQLException localSQLException1) {}  } finally { if (c != null) {
        try {
          c.close();
        }
        catch (SQLException localSQLException2) {}
      } 
    } 
    resp.setContentType("text/html");
    out.println("<title>Simple Customer Record Locator</title>");
    out.println("<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html\"/>");
    out.println("<meta name=\"Number\" content=\"" + view.getViewKey() + "\"/>");
    out.println("<meta name=\"Name\" content=\"" + view.getCustomerName() + "\"/>");
    out.println("<meta name=\"Company\" content=\"" + view.getCompany() + "\"/>");
    out.println("<meta name=\"Level\" content=\"" + view.getLevel() + "\"/>");
    out.println("<meta name=\"Order Product\" content=\"" + view.getOrderProduct() + "\"/>");
    out.println("<meta name=\"Country\" content=\"" + view.getCountry() + "\"/>");
    out.println("<meta name=\"Ask question\" content=\"" + view.getProblems() + "\"/>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>Customer Record Locator</h1>");
    out.println("<p>viewkey: " + valueViewKey + "</p>");
    out.println("<p>ID: " + view.getViewKey() + "</p>");
    out.println("<p>Name: " + view.getCustomerName() + "</p>");
    out.println("<p>Company: " + view.getCompany() + "</p>");
    out.println("<p>Level: " + view.getLevel() + "</p>");
    out.println("<p>Order Product: " + view.getOrderProduct() + "</p>");
    out.println("<p>Country: " + view.getCountry() + "</p>");
    out.println("<p>Ask question:" + view.getProblems() + "</p>");
    out.println("</body>");
    out.println("</html>");
  } 
}