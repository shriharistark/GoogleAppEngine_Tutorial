package tutorials.memcache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.mail.Address;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

/**
 * Servlet implementation class Addtomemcache
 * 
 * Input - key
 * adds the entity corresponding to that key to the memcache
 * Any object that can be serialised can be added to memcache, using
 * the key-value pair 
 * key(Entity key) - value (Entity object)
 * 
 */
@WebServlet("/addvalues")
public class AddtoMemcache extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Cache cache;   
	
    public AddtoMemcache() {
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			/* Useless
			CacheFactory cachefac = CacheManager.getInstance().getCacheFactory();
			Map<Object,Object> results = new HashMap<>();
			results.put(GCacheFactory.EXPIRATION_DELTA,TimeUnit.SECONDS.toSeconds(30));
			cache = cachefac.createCache(results);
			*/
			MemcacheService syncache = MemcacheServiceFactory.getMemcacheService();
			Key key = KeyFactory.stringToKey(request.getParameter("keym"));
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			if(!syncache.contains(KeyFactory.keyToString(key))){
				syncache.put(KeyFactory.keyToString(key), datastore.get(key));
			}
			
			else{
				response.setContentType("text/html");
				response.getWriter().write("Memcache already has this entry<br>");
				response.getWriter().write("<a href = '/getvalues?keymg="+KeyFactory.keyToString(key)+"'>Get here</a>");
				System.out.println("already contains no need to add again");
			}
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
