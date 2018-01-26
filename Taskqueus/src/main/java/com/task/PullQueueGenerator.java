package com.task;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Servlet implementation class PullQueueGenerator
 */
@WebServlet("/pullqueuescheduler")
public class PullQueueGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Task");
		Filter marksfilter = new FilterPredicate("Marks",FilterOperator.EQUAL,null);
		
		query.setFilter(marksfilter);
		PreparedQuery pq = datastore.prepare(query);
		Queue pullqueue = QueueFactory.getQueue("pull-queue");
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(15));
		String marks[] = {"A","B","C","D","E","F"};
		
		for(Entity n : results){
			StringBuilder message = new StringBuilder();
			
			int rand = (int)(0 + (Math.random() * (5 - 0)));
			message.append(KeyFactory.keyToString(n.getKey())).append(",");
			message.append(marks[rand]);
			
			pullqueue.add(TaskOptions.Builder
					.withMethod(TaskOptions.Method.PULL)
					.payload(message.toString())
					.tag("marksupdate".getBytes()));
			
			response.getWriter().write("Successful generation.");
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
