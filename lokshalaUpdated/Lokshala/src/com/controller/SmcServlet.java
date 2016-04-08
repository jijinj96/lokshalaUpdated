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

@WebServlet("/SmcServlet")
public class SmcServlet extends HttpServlet {
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
		try 
		{
			JSONObject jsonobj = (JSONObject) parser.parse(jsonString);
			JSONObject obj = new JSONObject();
			String schoolName = (String) jsonobj.get("sname");
			String childName = (String) jsonobj.get("cname");
			String mobileNo = (String) jsonobj.get("mno");
			String div = (String) jsonobj.get("pno");
			HttpSession session = request.getSession(false);
			if( session == null )
			{
				obj.put("msg", "Unauthorised Entry");
			}
			else{
				Account account = new Account();
				String username = (String)session.getAttribute("username");
				boolean status = account.smcRegister(username, schoolName, childName, mobileNo, div);
				if( status == true){
					obj.put("msg", "Registered Successfully");
				}
				else{
					obj.put("msg", account.getMsg());
				}
			}
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
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
