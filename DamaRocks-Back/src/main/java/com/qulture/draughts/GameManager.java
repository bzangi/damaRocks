package com.qulture.draughts;

import java.util.ArrayList;

import com.qulture.draughts.model.Spot;
import com.qulture.draughts.model.Table;
import com.qulture.draughts.model.Turn;

public class GameManager {

	private Table table;
	private Turn turn;

	public GameManager() {
		this.table = new Table(new ArrayList<Spot>());
		this.turn = new Turn("p1");
	}

	/*
	 * método responsável por montar o tabuleiro inicial com as peças em seus
	 * devidos lugares.
	 */

	public Table getInitialTable() {
		for (int i = 1; i <= 10; i++) {
			for (int j = 1; j <= 10; j++) {
				if (i <= 4) {
					if ((i % 2 != 0 && j % 2 == 0) || (i % 2 == 0 && j % 2 != 0)) {
						table.getSpots().add(new Spot(i + "," + j, "pj1"));
					}
				} else if (i >= 7) {
					if (i % 2 == 0 && j % 2 != 0 || i % 2 != 0 && j % 2 == 0)
						table.getSpots().add(new Spot(i + "," + j, "pj2"));
				} else {
					if (i % 2 == 0 && j % 2 != 0 || i % 2 != 0 && j % 2 == 0) {
						table.getSpots().add(new Spot(i + "," + j, "pj0"));
					}
				}
			}
			
		}
		return table;
	}

	public Turn getTurn() {
		return turn;
	}

}
