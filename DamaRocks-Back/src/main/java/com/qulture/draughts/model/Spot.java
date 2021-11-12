package com.qulture.draughts.model;

public class Spot {
	
	private String location;
	/*
	 * pj0 para casas vazias
	 * pj1 para casas c peças do jogador 1
	 * pj2 para casas c peças do jogador 2
	 * */
	private String state;
	
	public Spot(String location, String state) {
		super();
		this.location = location;
		this.state = state;
	}	

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	

}
