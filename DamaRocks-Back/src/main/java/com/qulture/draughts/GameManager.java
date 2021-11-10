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

	public Table getNewTable(Spot oldSpot, Spot newSpot) throws InvalidAttributesException {
		String[] oldSplitted = oldSpot.getLocation().split(",");
		String[] newSplitted = newSpot.getLocation().split(",");

		int newLine = Integer.parseInt(newSplitted[0]);
		int oldLine = Integer.parseInt(oldSplitted[0]);
		int newColumn = Integer.parseInt(newSplitted[1]);
		int oldColumn = Integer.parseInt(oldSplitted[1]);

		/*
		 * movimento simples jogador 1
		 */
		if (oldSpot.getState().equals("pj1")) {
			if (newLine > oldLine && newLine - oldLine == 1
					&& (newColumn - oldColumn == 1 || newColumn - oldColumn == -1)) {
				System.out.println("MOVIMENTO VALIDO J1");

				/*
				 * Iteração para encontrar o antigo spot na ArrayList e alterar seu estado
				 */
				int length = table.getSpots().size();
				for (int i = 0; i < length; i++) {
					Spot spot = table.getSpots().get(i);
					if (spot.getLocation().equals(oldSpot.getLocation())) {
						spot.setState("pj0");
					}
					if (spot.getLocation().equals(newSpot.getLocation())) {
						spot.setState("pj1");
					}
				}
				this.turn.setPlayer("j2");

				/*
				 * movimento para comer peças jogador 1
				 */
			} else if (newLine > oldLine && newLine - oldLine == 2) {
				if (newColumn - oldColumn == 2 || newColumn - oldColumn == -2) {
					int enemyLine = (newLine + oldLine) / 2;
					int enemyColumn = (newColumn + oldColumn) / 2;
					String enemyLocation = enemyLine + "," + enemyColumn;
					Spot enemySpot = new Spot(enemyLocation, "pj2");

					int length = table.getSpots().size();
					for (int i = 0; i < length; i++) {
						Spot spot = table.getSpots().get(i);
						if (spot.getLocation().equals(enemySpot.getLocation())) {
							if (spot.getState().equals(enemySpot.getState())) {
								spot.setState("pj0");
								System.out.println("COMIDA VÁLIDA");

								for (int j = 0; j < length; j++) {
									Spot spot2 = table.getSpots().get(j);
									if (spot2.getLocation().equals(oldSpot.getLocation())) {
										spot2.setState("pj0");
									}
									if (spot2.getLocation().equals(newSpot.getLocation())) {
										spot2.setState("pj1");
									}

								}

							} else {
								System.out.println("MOVIMENTO INVÁLIDO J1 - 2 casas sem oponente");
							}
							this.turn.setPlayer("j1");
						}
					}

				} else {
					System.out.println("MOVIMENTO INVÁLIDO J1 - COLUNAS");
				}

			} else {
				System.out.println("MOVIMENTO INVÁLIDO J1 - RECUO");
			}

			/*
			 * movimento simples jogador 2
			 */
		} else {
			if (newLine < oldLine && newLine - oldLine == -1
					&& (newColumn - oldColumn == 1 || newColumn - oldColumn == -1)) {
				System.out.println("MOVIMENTO VALIDO J2");

				int length = table.getSpots().size();
				for (int i = 0; i < length; i++) {
					Spot spot = table.getSpots().get(i);
					if (spot.getLocation().equals(oldSpot.getLocation())) {
						spot.setState("pj0");
					}
					if (spot.getLocation().equals(newSpot.getLocation())) {
						spot.setState("pj2");
					}
				}
				this.turn.setPlayer("j1");
				
			} else if (newLine < oldLine && newLine - oldLine == -2) {
				if (newColumn - oldColumn == 2 || newColumn - oldColumn == -2) {
					int enemyLine = (newLine + oldLine) / 2;
					int enemyColumn = (newColumn + oldColumn) / 2;
					String enemyLocation = enemyLine + "," + enemyColumn;
					Spot enemySpot = new Spot(enemyLocation, "pj1");

					int length = table.getSpots().size();
					for (int i = 0; i < length; i++) {
						Spot spot = table.getSpots().get(i);
						if (spot.getLocation().equals(enemySpot.getLocation())) {
							if (spot.getState().equals(enemySpot.getState())) {
								spot.setState("pj0");
								System.out.println("COMIDA VÁLIDA");

								for (int j = 0; j < length; j++) {
									Spot spot2 = table.getSpots().get(j);
									if (spot2.getLocation().equals(oldSpot.getLocation())) {
										spot2.setState("pj0");
									}
									if (spot2.getLocation().equals(newSpot.getLocation())) {
										spot2.setState("pj2");
									}

								}

							} else {
								System.out.println("MOVIMENTO INVÁLIDO J2 - 2 casas sem oponente");
							}
							this.turn.setPlayer("j2");
						}
					}

				} else {
					System.out.println("MOVIMENTO INVÁLIDO J2 - COLUNAS");
				}
			} else {
				System.out.println("MOVIMENTO INVÁLIDO J2");
			}
		}
		return table;
	}

	public void finishedGame() {
		this.table = new Table(new ArrayList<Spot>());
		this.turn = new Turn("j1");
		
	}
}

	