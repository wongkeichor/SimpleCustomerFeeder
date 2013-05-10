package com.demo.customer;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CustomerFeedServlet extends HttpServlet
{
  private static final Logger log = Logger.getLogger(CustomerFeedServlet.class.getName());
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException
  {
    PrintWriter out = resp.getWriter();
    Connection c = null;
    CustomerView view = new CustomerView();
    String valueViewKey = null;
    List<CustomerView> viewList = new ArrayList<CustomerView>();
    String gsaURL = "http://gsa5.enterprisedemo-google.com:19900/xmlfeed";
    String targetUrl = "http://kenwong-feed.appspot.com/customerlocator?viewkey=";
    ResultSet rs;
    try {
      DriverManager.registerDriver(new com.google.appengine.api.rdbms.AppEngineDriver());
      c = DriverManager.getConnection("jdbc:google:rdbms://kenwong-cloud-sql:cloudsql2/GEO");
      valueViewKey = req.getParameter("viewkey");
      
      if (valueViewKey == "") {
        out.println("<html><head></head><body>You are missing the customer id.  Try again! Redirecting in 3 seconds...</body></html>");
      } else if (valueViewKey.equalsIgnoreCase("all")) {
        String sql = "SELECT * FROM CustomerView WHERE Updated = 'Y'";
        Statement stmt = c.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          view = new CustomerView();
          view.setViewKey(rs.getString("ViewKey"));
          view.setCountry(rs.getString("Country"));
          view.setCustomerName(rs.getString("CustomerName"));
          view.setCompany(rs.getString("Company"));
          view.setOrderProduct(rs.getString("OrderProduct"));
          view.setProblems(rs.getString("Problems"));
          view.setLevel(rs.getInt("Level"));
          viewList.add(view);
        } 
      } else {
        String statement = "SELECT * FROM CustomerView WHERE Updated = 'Y' AND ViewKey = ?";
        PreparedStatement stmt = c.prepareStatement(statement);
        stmt.setString(1, valueViewKey);
        rs = stmt.executeQuery();
        while (rs.next()) {
          view = new CustomerView();
          view.setViewKey(rs.getString("ViewKey"));
          view.setCountry(rs.getString("Country"));
          view.setCustomerName(rs.getString("CustomerName"));
          view.setCompany(rs.getString("Company"));
          view.setOrderProduct(rs.getString("OrderProduct"));
          view.setProblems(rs.getString("Problems"));
          view.setLevel(rs.getInt("Level"));
          viewList.add(view);
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
    String feedXML = new String();
    if (!viewList.isEmpty()) {
      feedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
      feedXML = feedXML + "<!DOCTYPE gsafeed PUBLIC \"-//Google//DTD GSA Feeds//EN\" \"\">\n";
      feedXML = feedXML + "<gsafeed>\n";
      feedXML = feedXML + " <header>\n";
      feedXML = feedXML + "  <datasource>CRMDataFeed</datasource>\n";
      feedXML = feedXML + "  <feedtype>incremental</feedtype>\n";
      feedXML = feedXML + " </header>\n";
      feedXML = feedXML + " <group>\n";
      for (CustomerView cView : viewList)
      {
        feedXML = feedXML + "  <record url=\"" + targetUrl + cView.getViewKey() + "\" action=\"add\" mimetype=\"text/html\">\n";
        feedXML = feedXML + "   <metadata>\n";
        feedXML = feedXML + "\t\t<meta name=\"Level\" content=\"" + cView.getLevel() + "\"/>\n";
        feedXML = feedXML + "\t\t<meta name=\"Company\" content=\"" + cView.getCompany() + "\"/>\n";
        feedXML = feedXML + "\t\t<meta name=\"Product\" content=\"" + cView.getOrderProduct() + "\"/>\n";
        feedXML = feedXML + "\t\t<meta name=\"Country\" content=\"" + cView.getCountry() + "\"/>\n";
        feedXML = feedXML + "   </metadata>\n";
        feedXML = feedXML + "   <content>\n";
        feedXML = feedXML + "      <![CDATA[<html>\n";
        feedXML = feedXML + "                 <body>\n";
        feedXML = feedXML + "                 <H1>" + cView.getViewKey() + "</H1>\n";
        feedXML = feedXML + "                 <p>" + cView.getCustomerName() + "</p>\n";
        feedXML = feedXML + "                 <p>" + cView.getCompany() + "</p>\n";
        feedXML = feedXML + "                 <p>" + cView.getOrderProduct() + "</p>\n";
        feedXML = feedXML + "                 <p>" + cView.getCountry() + "</p>\n";
        feedXML = feedXML + "                 <p>" + cView.getProblems() + "</p>\n";
        feedXML = feedXML + "                 <p>" + cView.getLevel() + "</p>\n";
        feedXML = feedXML + "                 </body>\n";
        feedXML = feedXML + "      </html>]]>\n";
        feedXML = feedXML + "   </content>\n";
        if (cView.getCountry().equalsIgnoreCase("Taiwan")) {
          feedXML = feedXML + "   <acl>\n";
          if (!cView.getViewKey().equalsIgnoreCase("1005")) {
            feedXML = feedXML + "\t\t<principal namespace=\"DemoGroup\" case-sensitivity-type=\"everything-case-insensitive\" scope=\"user\" access=\"permit\">team-c1</principal>\n";
          } 
          feedXML = feedXML + "\t\t<principal namespace=\"DemoGroup\" case-sensitivity-type=\"everything-case-insensitive\" scope=\"group\" access=\"permit\">Administrators</principal>\n";
          feedXML = feedXML + "   </acl>\n";
        } 
        feedXML = feedXML + "  </record>\n";
      } 
      feedXML = feedXML + " </group>\n";
      feedXML = feedXML + "</gsafeed>";
      
      Queue q = QueueFactory.getDefaultQueue();
      TaskOptions option = TaskOptions.Builder.withUrl("/contentfeed");
      option.param("datasource", "CustomerDataFeed");
      option.param("feedtype", "incremental");
      option.param("data", feedXML);
      option.method(TaskOptions.Method.POST);
      q.add(option);
    } 
    
    resp.setContentType("text/xml");
    out.println(feedXML);
  } 
}