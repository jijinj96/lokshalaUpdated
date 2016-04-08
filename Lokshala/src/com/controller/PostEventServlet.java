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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.model.Account;

/**
 * Servlet implementation class PostEventServlet
 */
@WebServlet("/PostEventServlet")
public class PostEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonString = "";
		if (br != null) 
		{
			jsonString = br.readLine();
		}
		System.out.println("Received JSON " + jsonString);
		JSONParser parser = new JSONParser();
		String action="";
		
		try 
		{
			JSONObject jsonobj = (JSONObject) parser.parse(jsonString);
			JSONObject obj = new JSONObject();
			HttpSession session = request.getSession();
			if( session == null )
			{
				obj.put("msg", "unauthorised");
			}
			else
			{
				String email = (String) session.getAttribute("username");
				Account account = new Account();
				String title = (String)jsonobj.get("title");
				String date = (String) jsonobj.get("date");
				String desc = (String) jsonobj.get("desc");
				account.postEvent(title, date, desc, email);
				obj.put("msg", account.getMsg());
				
				StringWriter out = new StringWriter();
				obj.writeJSONString(out);

				String jsonText = out.toString();
				System.out.print(jsonText);

				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setContentType("application/json");
				OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());

				// writer.write(URLDecoder.decode(new String(decryptedData),
				// "UTF-8"));
				writer.write(jsonText);
				writer.flush();
				writer.close();
				System.out.println("Done Sending Data");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
