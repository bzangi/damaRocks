package com.qulture.draughts;

import java.util.ArrayList;

import javax.naming.directory.InvalidAttributesException;

import com.qulture.draughts.model.Spot;
import com.qulture.draughts.model.Table;
import com.qulture.draughts.model.Turn;

public class GameManager {

	private Table table;
	private Turn turn;
	private static int player1PiecesCounter = 20;
	private static int player2PiecesCounter = 20;

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



	/*
	 * Gera nova tabela baseado no movimento do jogador apenas quando o movimento
	 * for válido
	 */

	public Table getNewTable(Spot oldSpot, Spot newSpot) throws InvalidAttributesException {
		String[] oldSplitted = oldSpot.getLocation().split(",");
		String[] newSplitted = newSpot.getLocation().split(",");

		int newLine = Integer.parseInt(newSplitted[0]);
		int oldLine = Integer.parseInt(oldSplitted[0]);
		int newColumn = Integer.parseInt(newSplitted[1]);
		int oldColumn = Integer.parseInt(oldSplitted[1]);


		if (oldSpot.getState().equals("qpj1")) {
			this.queenMovimentationRules(oldSpot, newSpot, "qpj1", "pj2");;
			
		} else if (oldSpot.getState().equals("qpj2")) {
			this.queenMovimentationRules(oldSpot, newSpot, "qpj2", "pj1");
			
			
			/*
			 * movimento simples jogador 1
			 */
		} else if (oldSpot.getState().equals("pj1")) {
			if (newLine > oldLine && newLine - oldLine == 1
					&& (newColumn - oldColumn == 1 || newColumn - oldColumn == -1)) {
				System.out.println("MOVIMENTO VALIDO J1");

				this.simpleMove(table, oldSpot, newSpot, "pj1", "j2");

				/*
				 * movimento para comer peças jogador 1
				 */
			} else if (newLine > oldLine && newLine - oldLine == 2) {
				if (newColumn - oldColumn == 2 || newColumn - oldColumn == -2) {
					int enemyLine = (newLine + oldLine) / 2;
					int enemyColumn = (newColumn + oldColumn) / 2;
					String enemyLocation = enemyLine + "," + enemyColumn;
					Spot enemySpot = new Spot(enemyLocation, "pj2");

					this.jumpMove(enemySpot, oldSpot, newSpot, "pj1");

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
			if (oldSpot.getState().equals("qpj2")) {
				// IMPLEMENTAR MOVIMENTAÇÃO DA DAMA
			} else {
				if (newLine < oldLine && newLine - oldLine == -1
						&& (newColumn - oldColumn == 1 || newColumn - oldColumn == -1)) {
					System.out.println("MOVIMENTO VALIDO J2");

					this.simpleMove(table, oldSpot, newSpot, "pj2", "j1");

					/*
					 * movimento para comer peças jogador 2
					 */
				} else if (newLine < oldLine && newLine - oldLine == -2) {
					if (newColumn - oldColumn == 2 || newColumn - oldColumn == -2) {
						int enemyLine = (newLine + oldLine) / 2;
						int enemyColumn = (newColumn + oldColumn) / 2;
						String enemyLocation = enemyLine + "," + enemyColumn;
						Spot enemySpot = new Spot(enemyLocation, "pj1");

						this.jumpMove(enemySpot, oldSpot, newSpot, "pj2");

					} else {
						System.out.println("MOVIMENTO INVÁLIDO J2 - COLUNAS");
					}
				} else {
					System.out.println("MOVIMENTO INVÁLIDO J2");
				}
			}
		}
		return table;
	}


	/*
	 * Remove a peça da casa antiga e posiciona na nova, passando o turno para o
	 * oponente. Caso a peça alcance o outro lado do tabuleiro, ela irá se tornar Dama
	 */
	private void simpleMove(Table table, Spot oldSpot, Spot newSpot, String state, String turn) {
		int length = table.getSpots().size();
		
		/*
		 * Iteração para encontrar o antigo spot na ArrayList e alterar seu estado
		 */
		
		
		for (int i = 0; i < length; i++) {
			Spot spot = table.getSpots().get(i);
			if (spot.getLocation().equals(oldSpot.getLocation())) {
				spot.setState("pj0");
			}
			if (spot.getLocation().equals(newSpot.getLocation()) && newSpot.getLocation().split(",")[0].equals("10")){ 
				spot.setState("qpj1");
				
			} else if(spot.getLocation().equals(newSpot.getLocation()) && newSpot.getLocation().split(",")[0].equals("1")) {
				spot.setState("qpj2");
				
			}else if(spot.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("1") || 
					spot.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("10")) {
				spot.setState(state);
			}
		}
		this.turn.setPlayer(turn);
	}
	

	/*
	 * Remove a peça da casa antiga e posiciona na nova, removendo a peça inimiga e
	 * mantendo o turno
	 */
	private void jumpMove(Spot enemySpot, Spot oldSpot, Spot newSpot, String state) {
		int length = table.getSpots().size();

		if (enemySpot.getState().equals("pj2") || enemySpot.getState().equals("qpj2")) {

			for (int i = 0; i < length; i++) {
				Spot spot = table.getSpots().get(i);
				if (spot.getLocation().equals(enemySpot.getLocation())) {
					if (spot.getState().equals(enemySpot.getState()) || spot.getState().equals("qpj2")) {
						spot.setState("pj0");
						GameManager.player2PiecesCounter = GameManager.player2PiecesCounter - 1;
						System.out.println("COMIDA VÁLIDA");

						for (int j = 0; j < length; j++) {
							Spot spot2 = table.getSpots().get(j);
							if (spot2.getLocation().equals(oldSpot.getLocation())) {
								spot2.setState("pj0");
							}
							if (spot2.getLocation().equals(newSpot.getLocation()) && newSpot.getLocation().split(",")[0].equals("10")){ //SE NOVO SPOT FOR LINHA 10 OU 1 SETAR DAMA
								spot2.setState("qpj1");
								
							}else if(spot2.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("10")) {
								spot2.setState(state);
							}

						}

					} else {
						System.out.println("MOVIMENTO INVÁLIDO J1 - 2 casas sem oponente");
					}
					this.turn.setPlayer("j1");
				}
			}

		} else if (enemySpot.getState().equals("pj1") || enemySpot.getState().equals("qpj1")) {
			for (int i = 0; i < length; i++) {
				Spot spot = table.getSpots().get(i);
				if (spot.getLocation().equals(enemySpot.getLocation())) {
					if (spot.getState().equals(enemySpot.getState()) || spot.getState().equals("qpj1")) {
						spot.setState("pj0");
						GameManager.player1PiecesCounter = GameManager.player1PiecesCounter - 1;
						System.out.println("COMIDA VÁLIDA");

						for (int j = 0; j < length; j++) {
							Spot spot2 = table.getSpots().get(j);
							if (spot2.getLocation().equals(oldSpot.getLocation())) {
								spot2.setState("pj0");
							}
							if (spot2.getLocation().equals(newSpot.getLocation()) && newSpot.getLocation().split(",")[0].equals("1")){ //SE NOVO SPOT FOR LINHA 10 OU 1 SETAR DAMA
								spot2.setState("qpj2");
								
							}else if(spot2.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("1")) {
								spot2.setState(state);
							}

						}
						
					} else {
						System.out.println("MOVIMENTO INVÁLIDO J2 - 2 casas sem oponente");
					}
					this.turn.setPlayer("j2");
				}
			}
		}
	}
	/*
	 * Regras de movimentação da dama, impede ela de pular por mais de 1 peça inimiga, 
	 * obriga a dama a ficar 1 casa depois da peça que ela "comeu"
	 * */
	public void queenMovimentationRules(Spot oldSpot, Spot newSpot, String thisState, String enemyState) {
		String[] oldSplitted = oldSpot.getLocation().split(",");
		String[] newSplitted = newSpot.getLocation().split(",");

		int newLine = Integer.parseInt(newSplitted[0]);
		int oldLine = Integer.parseInt(oldSplitted[0]);
		int newColumn = Integer.parseInt(newSplitted[1]);
		int oldColumn = Integer.parseInt(oldSplitted[1]);

		int deltaLine = newLine - oldLine;
		int deltaColumn = newColumn - oldColumn;
		
		String nextTurn;
		if(thisState == "qpj1") {
			nextTurn = "j2";
		} else {
			nextTurn = "j1";
		}
		
		if (deltaLine == deltaColumn || deltaLine == -(deltaColumn)) {
			
			// direita embaixo
			if (deltaLine > 0 && deltaColumn > 0) {
				for (int i = oldLine, j = oldColumn; i <= newLine && j <= newColumn; i++, j++) {
					String location = i + "," + j;
					String nextLocation = (i + 1) + "," + (j + 1);
					Spot spotFinder = this.spotsIterator(table.getSpots(), location);
					Spot nextSpotFinder = this.spotsIterator(table.getSpots(), nextLocation);
					if (spotFinder.getState().equals("pj0") && location.equals(newSpot.getLocation())) {
						// ANDAR
						this.queenMove(table, oldSpot, newSpot, thisState, nextTurn);
					} else if (spotFinder.getState().equals(enemyState) || spotFinder.getState().equals("q" + enemyState)) {
						// COMER
						if(nextSpotFinder.getState().equals("pj0")) {
							this.jumpMove(spotFinder, oldSpot, nextSpotFinder, thisState);
						    break;
						}else{
							System.out.println("MOVIMENTO INVÁLIDO");
							break;
						}
					}
				}
				
				// esquerda embaixo
			} else if (deltaLine > 0 && deltaColumn < 0) {
				for (int i = oldLine, j = oldColumn ; i <= newLine && j >= newColumn; i++, j--) {
					String location = i + "," + j;
					String nextLocation = (i + 1) + "," + (j - 1);
					Spot spotFinder = this.spotsIterator(table.getSpots(), location);
					Spot nextSpotFinder = this.spotsIterator(table.getSpots(), nextLocation);
					if (spotFinder.getState().equals("pj0") && location.equals(newSpot.getLocation())) {
						// ANDAR
						this.queenMove(table, oldSpot, newSpot, thisState, nextTurn);
					} else if (spotFinder.getState().equals(enemyState) || spotFinder.getState().equals("q" + enemyState)) {
						// COMER
						if(nextSpotFinder.getState().equals("pj0")) {
							this.jumpMove(spotFinder, oldSpot, nextSpotFinder, thisState);
							break;
						}else{
							break;
						}
					}
				}

			} else if (deltaLine < 0 && deltaColumn > 0) {
				for (int i = oldLine, j = oldColumn; i >= newLine && j <= newColumn; i--, j++) {
					String location = i + "," + j;
					String nextLocation = (i - 1) + "," + (j + 1);
					Spot spotFinder = this.spotsIterator(table.getSpots(), location);
					Spot nextSpotFinder = this.spotsIterator(table.getSpots(), nextLocation);
					if (spotFinder.getState().equals("pj0") && location.equals(newSpot.getLocation())) {
						// ANDAR
						this.queenMove(table, oldSpot, newSpot, thisState, nextTurn);
					} else if (spotFinder.getState().equals(enemyState) || spotFinder.getState().equals("q" + enemyState)) {
						// COMER
						if(nextSpotFinder.getState().equals("pj0")) {
							this.jumpMove(spotFinder, oldSpot, nextSpotFinder, thisState);
							break;
						}else{
							break;
						}
					}
				}
			} else {
				for (int i = oldLine, j = oldColumn; i >= newLine && j >= newColumn; i--, j--) {
					String location = i + "," + j;
					String nextLocation = (i - 1) + "," + (j - 1);
					Spot spotFinder = this.spotsIterator(table.getSpots(), location);
					Spot nextSpotFinder = this.spotsIterator(table.getSpots(), nextLocation);
					if (spotFinder.getState().equals("pj0") && location.equals(newSpot.getLocation())) {
						// ANDAR
						this.queenMove(table, oldSpot, newSpot, thisState, nextTurn);
					} else if (spotFinder.getState().equals(enemyState) || spotFinder.getState().equals("q" + enemyState)) {
						// COMER
						if(nextSpotFinder.getState().equals("pj0")) {
							this.jumpMove(spotFinder, oldSpot, nextSpotFinder, thisState);
							break;
						}else{
							break;
						}
					}
				}
			}

		} else {
			System.out.println("MOVIMENTO INVÁLIDO - dama fora da diagonal");
		}
		
	}
	
	
	/*
	 * Move a dama livremente pelas diagonais vazias
	 * */
	private void queenMove(Table table, Spot oldSpot, Spot newSpot, String state, String turn) {
		int length = table.getSpots().size();
		
		for (int i = 0; i < length; i++) {
			Spot spot = table.getSpots().get(i);
			if (spot.getLocation().equals(oldSpot.getLocation())) {
				spot.setState("pj0");
			}
				
			if(spot.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("1") || 
					spot.getLocation().equals(newSpot.getLocation()) && !newSpot.getLocation().split(",")[0].equals("10")) {
				spot.setState(state);
			}
		}
		this.turn.setPlayer(turn);
	}
	
	private Spot spotsIterator(ArrayList<Spot> Spots, String location) {
		for (Spot spot : Spots) {
			if (spot.getLocation().equals(location)) {
				return spot;
			}
		}
		return null;
	}

	public void finishedGame() {
		this.table = new Table(new ArrayList<Spot>());
		this.turn = new Turn("j1");
		GameManager.player1PiecesCounter = 20;
		GameManager.player2PiecesCounter = 20;

	}
	
	public Turn getTurn() {
		return turn;
	}

	public static int getPlayer1PiecesCounter() {
		return player1PiecesCounter;
	}

	public static void setPlayer1PiecesCounter(int player1PiecesCounter) {
		GameManager.player1PiecesCounter = player1PiecesCounter;
	}

	public static int getPlayer2PiecesCounter() {
		return player2PiecesCounter;
	}

	public static void setPlayer2PiecesCounter(int player2PiecesCounter) {
		GameManager.player2PiecesCounter = player2PiecesCounter;
	}
}
