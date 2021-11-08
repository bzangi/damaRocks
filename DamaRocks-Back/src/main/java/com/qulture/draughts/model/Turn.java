package com.qulture.draughts.model;

public class Turn {
	
	private String player;

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public Turn(String player) {
		super();
		this.player = player;
	}

}
