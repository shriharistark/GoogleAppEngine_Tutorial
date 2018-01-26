package com.task;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

/**
 * Servlet implementation class PullQueueWorker
 */
@WebServlet("/pullqueueworker")
public class PullQueueWorker extends HttpServlet {
	private static final long serialVersionUID = 1L;
    DatastoreService datastore;
    
    public PullQueueWorker() {
        datastore = DatastoreServiceFactory.getDatastoreService();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Queue pullqueue = QueueFactory.getQueue("pull-queue");
		List<TaskHandle> tasks = pullqueue.leaseTasks(4,TimeUnit.SECONDS,4);
		
		Key key;
		Entity entity;
		for(TaskHandle th : tasks){
			
			if(th.getTag().equals("marksupdate")){
				System.out.println("marksupdate found .. ");
				String message = new String(th.getPayload());
				System.out.println(message.split(",")[0]);
				key = KeyFactory.stringToKey(message.split(",")[0]);
				//String key = results.get(0).getValue();
				String mark = message.split(",")[1];
				
				try {
					entity = datastore.get(key);
					entity.setProperty("Marks", mark);
					datastore.put(entity);
					pullqueue.deleteTask(th);
				} catch (EntityNotFoundException e) {
					response.getWriter().println("Key is not found!");
					e.printStackTrace();
				}
			}
		}
		
		response.getWriter().println("Pulled and completed 4 tasks");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
