
package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.beans.ApplicantDetails;
import com.beans.Events;
import com.beans.Facilities;
import com.beans.GraphDetails;
import com.beans.School;

public class Account {
	private final String dbName = "lokshala";
	private final String driver = "com.mysql.jdbc.Driver";
	private final String url = "jdbc:mysql://localhost:3306/";
	private final String user="root";
	private final String password = "";
	private final String TABLE_EVENT = "event";
	private final String TABLE_USERS = "user";
	private final String TABLE_facility = "facility";
	private final String TABLE_facilityfeedback = "facility_feedback";
	private final String TABLE_School = "school";
	private final String TABLE_EventFeedback = "event_feedback";
	private final String TABLE_SmcRegister = "smc";
	private final String TABLE_SmcUsers = "smcusers";
	
	
	Connection con;
	private String msg = "";
	public String getMsg() {
		return msg;
	}
	
	public void postEvent( String title , String date , String desc,String username) throws ClassNotFoundException, SQLException{
		dbConnect();
		
		String sql = "select school_id from smcusers where email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int school_id = -1;
		while( rs.next() ){
			school_id = rs.getInt("school_id");
		}
		
		if(school_id > 0){
			sql = "insert into event (school_id,event_title,event_description,image) values(?,?,?,?)";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, school_id);
			stmt.setString(2, title);
			stmt.setString(3, desc);
			stmt.setString(4, "images/annual.jpg");
			int i = stmt.executeUpdate();
			if(i > 0){
				setMsg("successsfully registered");
			}
			else{
				setMsg("cannot post");
			}
		}
		else{
			setMsg("cannot post");
		}
		
		dbClose();
	}
	
	public ArrayList<ApplicantDetails> getApplicants(String username) throws ClassNotFoundException, SQLException{
		dbConnect();
		
		String sql = "select school_id from smcusers where email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int school_id = -1;
		while( rs.next() ){
			school_id = rs.getInt("school_id");
		}
		if(school_id > 0)
		{
			sql = "select name,child_name,phone_no,std from user,smc where user.user_id in ( select user_id from smc where school_id = ?)";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, school_id);
			rs = stmt.executeQuery();
			ArrayList<ApplicantDetails> applicant = new ArrayList<ApplicantDetails>();
			while(rs.next())
			{
				ApplicantDetails details = new ApplicantDetails();
				details.setChild_name(rs.getString("child_name"));
				details.setName(rs.getString("name"));
				details.setContact(rs.getString("phone_no"));
				details.setId(rs.getString("std"));
				applicant.add(details);
			}
				
			return applicant;
		}
		else
		{
			setMsg("Error in school id");
			return null;
		}
		
	}
	
	
	public School getSchoolDetails(String schoolname) throws ClassNotFoundException, SQLException{
		dbConnect();
		
		String sql = "select * from school where school_name = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, schoolname);
		ResultSet rs = stmt.executeQuery();
		School sc = new School();
		while( rs.next() ){
			sc.setAddress(rs.getString("school_address"));
			sc.setSchool_Name(rs.getString("school_name"));
			//sc.setImages(rs.getString("im"));
			sc.setTimings(rs.getString("school_time"));
		}
		dbClose();
		return sc;
	}
	
	public ArrayList<School> getSchoolName() throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "Select * from school ";
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<School> school = new ArrayList<School>();
		while(rs.next()){
			School s = new School();
			s.setSchool_Name(rs.getString("school_name"));
			s.setAddress(rs.getString("school_address"));
			s.setTimings(rs.getString("school_time"));
			school.add(s);
		}
		dbClose();
		return school;
	}
	public School getSchoolInfo(String schoolName) throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "Select * from school where school_name = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, schoolName);
		ResultSet rs = stmt.executeQuery();
		School s = new School();
		while(rs.next()){
			s.setSchool_Name(rs.getString("school_name"));
			s.setAddress(rs.getString("school_address"));
			s.setTimings(rs.getString("school_time"));
		}
		dbClose();
		return s;
	}
	
	public boolean updatePassword(String username , String old , String newPass) throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "select count(*) as count from "+TABLE_USERS+" where email = ? and password = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		stmt.setString(2, old);
		ResultSet rs = stmt.executeQuery();
		int count = -1 ;
		while(rs.next()){
			count = rs.getInt("count");
		}
		if( count > 0){
			sql = "UPDATE "+TABLE_USERS+" SET password= ? where email= ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, newPass);
			stmt.setString(2, username);
			System.out.println(stmt);
			int i = stmt.executeUpdate();
			if(i > 0){
				setMsg("updated successfully");
				return true;
			}
			else{
				setMsg("update unsuccessfull");
				return false;
			}
		}
		else{
			setMsg("old password incorrect");
			return false;
		}
	}
	
	public String getSchoolName(String username) throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql= "select school_name from school,smcusers where smcusers.school_id=school.school_id and email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		String schoolName="";
		System.out.println(stmt);
		while(rs.next()){
			schoolName = rs.getString("school_name");
		}
		System.out.println(schoolName);
		dbClose();
		return schoolName;
	}
	
	public boolean doSmcLogin(String email , String password) throws ClassNotFoundException, SQLException{
		dbConnect();
		String query = "Select count(*) as count from "+TABLE_SmcUsers+" where email = ? and password = ?";
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, email);
		pstmt.setString(2, password);
		
		ResultSet rs = pstmt.executeQuery();
		
		int count = 0;
		while(rs.next()){
			count = rs.getInt("count");
		}
		rs.close();
		dbClose();
		if(count > 0){
			return true;
		}
		return false;
	}
	
	public boolean smcRegister(String username , String schoolName , String childName , String mobile , String div) throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "Select user_id from " + TABLE_USERS + " where email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int user_id = -1;
		while( rs.next()){
			user_id = rs.getInt("user_id");
		}
		if(user_id > 0 ){
			sql = "select count(*) as count from "+TABLE_SmcRegister+" where user_id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, user_id);
			rs = stmt.executeQuery();
			int count = -1;
			while( rs.next() ){
				count = rs.getInt("count");
			}
			if(count <= 0){
				int schoolId = getSchoolId(schoolName);
				sql = "insert into "+TABLE_SmcRegister+" (school_id,user_id,phone_no,child_name,std) values(?,?,?,?,?)";
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, schoolId);
				stmt.setInt(2, user_id);
				stmt.setString(3, mobile);
				stmt.setString(4, childName);
				stmt.setString(5, div);
				int i = stmt.executeUpdate();
				dbClose();
				if( i > 0 ){
					setMsg("Successfull Registration");
					return true;
				}
				else{
					setMsg("Cannot register");
					return false;
				}
			}
			else{
				setMsg("User Already registered");
				return false;
			}
		}
		else{
			dbClose();
			setMsg("User not valid");
			return false;
		}
	}
	
	public GraphDetails getSmcData(String schoolName) throws ClassNotFoundException, SQLException{
		dbConnect();
		int school_id = getSchoolId(schoolName);
		String sql = "select sum(f_feedback) as sum,facility_name from facility_feedback,facility where"
				+ " facility_feedback.facility_id = facility.facility_id and school_id = ? group by facility.facility_id";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, school_id);
		ResultSet rs = pstmt.executeQuery();
		GraphDetails details = new GraphDetails();
		while(rs.next()){
			String fname = rs.getString("facility_name");
			if(fname.equals("Infrastucture")){
				details.setInfra(rs.getInt("sum"));
			}
			else if(fname.equals("Midday meal")){
				details.setMidDay(rs.getInt("sum"));
			}
			else if(fname.equals("Academics")){
				details.setAcademics(rs.getInt("sum"));
			}
			else if(fname.equals("Sanitation")){
				details.setSanitation(rs.getInt("sum"));
			}
		}
		sql = "select event_title,sum(e_feedback) as sum from event,event_feedback where event.event_id = event_feedback.event_id and event.school_id = ? group by event_title";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, school_id);
		System.out.println(pstmt);
		rs = pstmt.executeQuery();
		
		ArrayList<Events> event = new ArrayList<Events>();
		while( rs.next() ){
			Events events = new Events();
			events.setEvent_title(rs.getString("event_title"));
			events.setFeedback(rs.getInt("sum"));
			event.add(events);
		}
		details.setEvents(event);
		dbClose();
		return details;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	private void dbConnect() throws ClassNotFoundException, SQLException{
		Class.forName(driver);
		
		con = DriverManager.getConnection(url+dbName, user, password);
	}
	
	
	
	public boolean doLogin(String email , String password) throws ClassNotFoundException, SQLException{
		dbConnect();
		String query = "Select count(*) as count from "+TABLE_USERS+" where email = ? and password = ?";
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, email);
		pstmt.setString(2, password);
		
		ResultSet rs = pstmt.executeQuery();
		
		int count = 0;
		while(rs.next()){
			count = rs.getInt("count");
		}
		rs.close();
		dbClose();
		if(count > 0){
			return true;
		}
		return false;
	}
	
	public boolean doRegister(String email,String password ,String name,String type) throws ClassNotFoundException, SQLException{
		dbConnect();
		
		String sql = "select count(*) as count from "+TABLE_USERS+" where email = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, email);
		ResultSet rsCount = pstmt.executeQuery();
		int count = 0;
		while(rsCount.next()){
			count = rsCount.getInt("count");
		}
		System.out.println(count);
		if(count > 0){
			dbClose();
			pstmt.close();
			rsCount.close();
			setMsg("Email id already exists!!");
			return false;
		}
		String query = "insert into "+TABLE_USERS+" (email,password,name,type) values (?,?,?,?)";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, email);
		ps.setString(2, password);
		ps.setString(3, name);
		ps.setString(4, type);
		System.out.println(ps);
		int i = ps.executeUpdate();
		dbClose();
		if(i > 0){
			setMsg("Sucessfully Registered");
			return true;
		}
		setMsg("Error Registring");
		return false;
	}
	
	
	public ArrayList<Events> getEvents() throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "select * from "+TABLE_EVENT;
		PreparedStatement stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		ArrayList<Events> events = new ArrayList<Events>();
 		while( rs.next() ){
			Events event = new Events();
			event.setEvent_id(rs.getInt("event_id"));
			event.setEvent_title(rs.getString("event_title"));
			event.setEvent_description(rs.getString("event_description"));
			event.setEvent_image(rs.getString("image"));
			event.setSchool_id(rs.getInt("school_id"));
			events.add(event);
		}	
		dbClose();
		return events;
	}
	
	
	public ArrayList<Facilities> getFacility() throws ClassNotFoundException, SQLException{
		dbConnect();
		String sql = "select * from "+TABLE_facility;
		PreparedStatement stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		ArrayList<Facilities> facilities = new ArrayList<Facilities>();
 		while( rs.next() ){
			Facilities f = new Facilities();
			f.setFacility_id(rs.getInt("facility_id"));
			f.setFacility_name(rs.getString("facility_name"));
			f.setImage(rs.getString("image"));
			facilities.add(f);
		}	
		dbClose();
		return facilities;
	}
	
	
public void putEventFeedback(int event_id , String event_feedback , int School_id, String username) throws ClassNotFoundException, SQLException{
		
		dbConnect();
		
		String sql = "Select user_id from " + TABLE_USERS + " where email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int user_id = -1;
		while( rs.next()){
			user_id = rs.getInt("user_id");
		}
		
		if(user_id > 0){
			sql = "select count(*) as count from "+TABLE_EventFeedback +" where user_id = ? and event_id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, user_id);
			stmt.setInt(2, event_id);
			rs = stmt.executeQuery();
			int noOfFeed = -1;
			while(rs.next()){
				noOfFeed = rs.getInt("count");
			}
			if(noOfFeed <= 0 ){
				sql = "insert into "+TABLE_EventFeedback+" (user_id,event_id,e_feedback,school_id) values(?,?,?,?)";
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, user_id);
				stmt.setInt(2, event_id);
				stmt.setInt(4, School_id);
				stmt.setString(3, event_feedback);
				int i = stmt.executeUpdate();
				if(i > 0){
					setMsg("Feedback inserted");
				}
				else{
					setMsg("Failed to insert feedback");
				}
			}
			else{
				setMsg("Feedback already given");
			}
		}
		
		else{
			setMsg("Error putting feedback");
			dbClose();
			
		}
	}
	
	
	public boolean putFacilityFeedback(int facility_id , String facility_feedback , int School_id, String username) throws ClassNotFoundException, SQLException{
		
		dbConnect();
		
		String sql = "Select user_id from " + TABLE_USERS + " where email = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int user_id = -1;
		while( rs.next()){
			user_id = rs.getInt("user_id");
		}
		
		if(user_id > 0){
			sql = "select count(*) as count from "+TABLE_facilityfeedback + " where user_id = ? and facility_id = ? and school_id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, user_id);
			ps.setInt(2, facility_id);
			ps.setInt(3, School_id);
			ResultSet rs2 = ps.executeQuery();
			int noOfFeed = -1;
			while( rs2.next() ){
				noOfFeed = rs2.getInt("count");
			}
			if( noOfFeed <= 0){
				String sql1 = "insert into "+ TABLE_facilityfeedback +" (school_id,user_id,facility_id,f_feedback) values(?,?,?,?)";
				stmt = con.prepareStatement(sql1);
				stmt.setInt(1, School_id);
				stmt.setInt(2, user_id);
				stmt.setInt(3, facility_id);
				stmt.setString(4, facility_feedback);
				int i = stmt.executeUpdate();
				dbClose();
				if(i > 0){
					setMsg("Succeesss");
					return true;
				}
				else{
					setMsg("Error inserting");
					return true;
				}
			}
			else{
				setMsg("Already Given Feedback");
				return false;
			}
		}
		
		else{
			setMsg("Error putting feedback");
			dbClose();
			return false;
		}
	}
	
	public int getSchoolId(String schoolName) throws ClassNotFoundException, SQLException{
		dbConnect();
		
		String sql = "Select school_id from "+TABLE_School+" where school_name = ?";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, schoolName);
		ResultSet rs = stmt.executeQuery();
		int school_id = -1;
		while(rs.next()){
			school_id = rs.getInt("school_id");
		}
		
		return school_id;
	}
	
	
	
	
	
	private void dbClose() throws SQLException{
		con.close();
	}
}