package tutorials.memcache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyContainer;
/*
 * Support servlet for creating entities at the start
 * 
 * won't add to memcache by default
 * do it from /addvalues url
 */
@WebServlet(
    name = "Create",
    urlPatterns = {"/create"}
)
public class Create extends HttpServlet {
	
	DatastoreService datastore;
	
	@Override
	public void init() throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
      
	  Map<String,String> userdetails = new HashMap<>();
	  userdetails.put("Name", request.getParameter("uname"));
	  userdetails.put("Number", request.getParameter("unum"));
	  userdetails.put("Address", request.getParameter("uaddress"));
	  	  
	  Entity ent = new Entity("Memcache");
	  
	  for(Map.Entry<String, String> m : userdetails.entrySet()){
		  ent.setProperty(m.getKey(), m.getValue());
	  }
	  
	  try{
	  datastore.put(ent);
	  response.sendRedirect("index.html");
	  } catch(Exception e){
		  System.out.println("Exception ");
	  }
	  

  }
}