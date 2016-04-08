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
 * Servlet implementation class SettingsServlet
 */
@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {
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
			action = (String) jsonobj.get("action");
			String oldPass = (String) jsonobj.get("oldPass");
			String newPass = (String) jsonobj.get("newPass");
			HttpSession session = request.getSession(false);
			String username = "";
			if(session == null){
				obj.put("msg", "unauthorised login");
			}
			else{
				username = (String) session.getAttribute("username");
			}
			Account account = new Account();
			boolean status = account.updatePassword(username, oldPass, newPass);
			if(status){
				obj.put("msg", account.getMsg());
			}
			else{
				obj.put("msg", account.getMsg());
			}
			System.out.println(obj.toJSONString());
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
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		
	
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
