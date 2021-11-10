package com.qulture.draughts;

import java.util.ArrayList;

import javax.naming.directory.InvalidAttributesException;

import com.qulture.draughts.model.Spot;
import com.qulture.draughts.model.Table;
import com.qulture.draughts.model.Turn;

public class GameManager {

	private Table table;
	private Turn turn;

	public GameManager() {
		this.table = new Table(new ArrayList<Spot>());
		this.turn = new Turn("j1");
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
	
	public Table getNewTable(Spot oldSpot, Spot newSpot) throws InvalidAttributesException  {
		String[] oldSplitted = oldSpot.getLocation().split(",");
		String[] newSplitted = newSpot.getLocation().split(",");
		System.out.println(oldSpot.getState());
		if (oldSpot.getState().equals("pj1")){
			if(Integer.parseInt(newSplitted[0]) > Integer.parseInt(oldSplitted[0])) {
				System.out.println("MOVIMENTO VALIDO J1");				
				
				/*
				 * Iteração para encontrar o antigo spot na ArrayList e alterar seu estado
				 * */
				int length=table.getSpots().size();
				for(int i=0; i<length; i++) {
				    Spot spot = table.getSpots().get(i);
					if (spot.getLocation().equals(oldSpot.getLocation())) {
				    	spot.setState("pj0");
				    }
					if (spot.getLocation().equals(newSpot.getLocation())) {
						spot.setState("pj1");
					}
				}
				this.turn.setPlayer("j2");
			} else {
				System.out.println("MOVIMENTO INVÁLIDO J1");
				
			}
		} else {
			if(Integer.parseInt(newSplitted[0]) < Integer.parseInt(oldSplitted[0])) {
				System.out.println("MOVIMENTO VALIDO J2");
				
				int length=table.getSpots().size();
				for(int i=0; i<length; i++) {
				    Spot spot = table.getSpots().get(i);
					if (spot.getLocation().equals(oldSpot.getLocation())) {
				    	spot.setState("pj0");
				    }
					if (spot.getLocation().equals(newSpot.getLocation())) {
						spot.setState("pj2");
					}
				}
				this.turn.setPlayer("j1");
			} else {
				System.out.println("MOVIMENTO INVÁLIDO J2");
			}
		}
		return table;
	}

}
