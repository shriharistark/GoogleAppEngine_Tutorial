package com.task;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreFailureException;
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
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Servlet implementation class PushTasks
 */
@WebServlet("/pushtask")
public class PushTasks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore;

	public void init(ServletConfig config) throws ServletException {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Query query = new Query("Task");
		Filter numberfilter = new FilterPredicate("Number", FilterOperator.EQUAL, null);
		query.setFilter(numberfilter);
		PreparedQuery pq = datastore.prepare(query);

		List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
		Queue queue = QueueFactory.getDefaultQueue();

		for (Entity n : results) {

			Integer num = 0;
			String key = KeyFactory.keyToString(n.getKey());
			queue.add(TaskOptions.Builder.withUrl("/pushtaskworker")
					.param("numbert", (++num).toString())
					.param("keyt", key)
					.retryOptions(RetryOptions.Builder.withTaskRetryLimit(2)));

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
