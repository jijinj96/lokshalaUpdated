package com.beans;

import java.util.ArrayList;

public class GraphDetails {
	private int infra;
	private int sanitation;
	private int academics;
	private int midDay;
	private ArrayList<Events> events;

	public ArrayList<Events> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Events> events) {
		this.events = events;
	}

	public int getInfra() {
		return infra;
	}

	public void setInfra(int infra) {
		this.infra = infra;
	}

	public int getSanitation() {
		return sanitation;
	}

	public void setSanitation(int sanitation) {
		this.sanitation = sanitation;
	}

	public int getAcademics() {
		return academics;
	}

	public void setAcademics(int academics) {
		this.academics = academics;
	}

	public int getMidDay() {
		return midDay;
	}

	public void setMidDay(int midDay) {
		this.midDay = midDay;
	}

}
