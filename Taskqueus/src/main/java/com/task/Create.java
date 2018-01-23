package com.task;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/**
 * Servlet implementation class Create
 */
@WebServlet("/create")
public class Create extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore;
	
	@Override
	public void init() throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();

	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = request.getParameter("name");
		Entity ent = new Entity("Task");
		ent.setProperty("Name", request.getParameter("name"));
		datastore.put(ent);
		
		response.getWriter().println("To update numbers .. go to ");
		response.getWriter().println("<a href = \"index.html\">Home</a><br>");
		response.getWriter().println("To view admin go to <a href = \"http://localhost:8080/_ah/admin\">Admin</a>");
	}

}
