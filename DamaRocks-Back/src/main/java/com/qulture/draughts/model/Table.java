package com.qulture.draughts.model;

import java.util.ArrayList;

public class Table {

	private ArrayList<Spot> spots;

	public Table(ArrayList<Spot> spots) {
		super();
		this.spots = spots;
	}
	
	public ArrayList<Spot> getSpots() {
		return spots;
	}

	public void setSpots(ArrayList<Spot> spots) {
		this.spots = spots;
	}

	
	
	
	
}
