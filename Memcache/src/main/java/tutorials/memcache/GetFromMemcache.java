package tutorials.memcache;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Servlet implementation class GetFromMemcache
 * 
 * Input - key
 * Retrieves entities from memcache by default
 * if not found on memcache, returns from the datastore
 * 
 * Beware of stale results : even if an entity is modified or is deleted,
 * the memcache will return stale results. Everytime an update is done, make
 * sure it is updated on memcache.
 * 
 * ** memcache is preferably used to store values that are static
 */
@WebServlet("/getvalues")
public class GetFromMemcache extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore;
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		PrintWriter out = response.getWriter();
		
		if(memcache.contains(request.getParameter("keymg"))){
			//Entity entity = (Entity)memcache.get(request.getParameter("keymg"));
			Entity en = (Entity)memcache.get(request.getParameter("keymg"));
			System.out.println(en.toString());
			System.out.println(request.getParameter("keymg"));
			out.write("<b>This was obtained from memcache</b>");
			
			for(Map.Entry<String, Object> s : en.getProperties().entrySet()){
				out.println("<br>"+s.getKey()+" "+s.getValue().toString());
			}
		}
		
		else{
			out.write("<b>This is not part of memcache!</b>");
			out.write("<br>This is returned from the datastore."+datastore.toString());
			out.write("<br>May be you want to add : <a href = '/addvalues?keym="+request.getParameter("keymg")+"'>Add here</a>");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
