package com.demo.customer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class ContentFeedController extends HttpServlet
{
  private static final Logger log = Logger.getLogger(ContentFeedController.class.getName());
  
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    String gsaURL = "http://gsa5.enterprisedemo-google.com:19900/xmlfeed";
    String feedXML = req.getParameter("data");
    String feedDataSource = req.getParameter("datasource");
    String feedType = req.getParameter("feedtype");
    
    //Socket socket = null;
    String payload = "datasource=" + feedDataSource + "&feedtype=" + feedType + "&data=" + feedXML;
    try
    {
      /* Using URL Fetch */
      URL url = new URL(gsaURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(payload);
      writer.close();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    	 System.out.println(conn.getResponseMessage());
      }
      /* Using URL Fetch */
      
      /* Using socket	
      log.warning("Before new Socket");
      socket = new Socket("gsa5.enterprisedemo-google.com", 19900);
      log.warning("Before new BufferedWriter");
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
      out.write("POST /xmlfeed HTTP/1.0\r\n");
      out.write("Content-Length: " + payload.length() + "\r\n");
      out.write("Content-Type: application/x-www-form-urlencodedrn");
      out.write("\r\n");
      log.warning("Before write payload");
      out.write(payload);
      log.warning("Before flush");
      out.flush();

      log.warning("Before new BufferedReader");
      BufferedReader recv = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
      String line;
      while ((line = recv.readLine()) != null) {  
        log.warning(line);
      } 
      log.warning("Before close");
      out.close();
      recv.close();
      Using socket */
    	
    }
    catch (IOException e)
    {
      log.warning(e.getMessage());
      e.printStackTrace();
    } 
  } 
}