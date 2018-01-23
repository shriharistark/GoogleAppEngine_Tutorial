package com.task;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;


/**
 * Servlet implementation class ReadEntries
 */
@WebServlet("/readentries")
public class ReadEntries extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore;
	Query query;

	public void init(ServletConfig config) throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity;
		query = new Query("Task");
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(3);

	    // If this servlet is passed a cursor parameter, let's use it.
	    String startCursor = request.getParameter("cursor");
	    if (startCursor != null) {
	      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
	    }
	    
	    Query q = new Query("Task");
	    PreparedQuery pq = datastore.prepare(q);

	    QueryResultList<Entity> results;
	    try {
	      results = pq.asQueryResultList(fetchOptions);
	    } catch (IllegalArgumentException e) {
	      // IllegalArgumentException happens when an invalid cursor is used.
	      // A user could have manually entered a bad cursor in the URL or there
	      // may have been an internal implementation detail change in App Engine.
	      // Redirect to the page without the cursor parameter to show something
	      // rather than an error.
	      response.sendRedirect("/people");
	      return;
	    }

	    response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");
	    PrintWriter w = response.getWriter();
	    w.println("<!DOCTYPE html>");
	    w.println("<meta charset=\"utf-8\">");
	    w.println("<title>Cloud Datastore Cursor Resutls</title>");
	    w.println("<ul>");
	    for (Entity entity : results) {
	      w.println("<li>" + entity.getProperty("Name") +"|" +entity.getProperty("Number")+"</li>");
	    }
	    w.println("</ul>");

	    String cursorString = results.getCursor().toWebSafeString();

	    // This servlet lives at '/people'.
	    w.println("<a href='/readentries?cursor=" + cursorString + "'>Next page</a>");

	  }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
