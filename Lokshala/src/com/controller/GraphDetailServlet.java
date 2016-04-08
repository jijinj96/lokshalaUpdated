package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beans.Events;
import com.beans.GraphDetails;
import com.model.Account;

@WebServlet("/GraphDetailServlet")
public class GraphDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonString = "";

		if (br != null) {
			jsonString = br.readLine();
		}

		System.out.println("Received JSON " + jsonString);
		JSONParser parser = new JSONParser();
		String action = "";
		try 
		{
			JSONObject jsonobj = (JSONObject) parser.parse(jsonString);
			JSONObject obj = new JSONObject();
			String name = "";
			action = (String) jsonobj.get("action");
			if (action.equals("details")) {
				HttpSession session = request.getSession(false);
				if (session == null) {
					obj.put("msg", "unauthorised");
				} else {
					obj.put("msg", "authorised");
					name = (String) session.getAttribute("username");
					System.out.println(name);
				}
			}
			
			
			Account account = new Account();
			String schoolname = account.getSchoolName(name);
			GraphDetails details = account.getSmcData(schoolname);
			obj.put("infra",details.getInfra() );
			obj.put("acad",details.getAcademics() );
			obj.put("midday",details.getMidDay() );
			obj.put("sanitation",details.getSanitation() );
			JSONArray eventsArray = new JSONArray();
			for(Events e : details.getEvents()){
				JSONObject event = new JSONObject();
				event.put("title", e.getEvent_title());
				event.put("feedback", e.getFeedback());
				eventsArray.add(event);
			}
			obj.put("events", eventsArray);
			System.out.println(obj.toJSONString());
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			String jsonText = out.toString();
			System.out.print(jsonText);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("application/json");
			OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
			writer.write(jsonText);
			writer.flush();
			writer.close();
			System.out.println("Done Sending Data");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		
		
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
