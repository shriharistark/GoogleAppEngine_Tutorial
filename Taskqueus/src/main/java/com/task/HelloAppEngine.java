package com.task;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueFetchQueuesResponse.Queue;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
      
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");

    //Accessing the default queue
    com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getDefaultQueue();
    //building  retries object
    RetryOptions retryops = RetryOptions.Builder.withTaskRetryLimit(4).maxBackoffSeconds(200).maxDoublings(3);
    //buiding queues with retry options and target url
    queue.add(TaskOptions.Builder.withRetryOptions(retryops));
    queue.add(TaskOptions.Builder.withUrl("/addentries").param("numbert", request.getParameter("number")).param("keyt", request.getParameter("key")));
    
    response.getWriter().println("Added to taskqueue ... Number will be updated");
    response.getWriter().println("<a href = \"http://localhost:8080/_ah/admin\">Go here then</a>");
  }
}