package com.qulture.draughts.websocket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qulture.draughts.GameManager;
import com.qulture.draughts.model.Spot;
import com.qulture.draughts.model.Table;
import com.qulture.draughts.model.Turn;
import com.qulture.draughts.util.ComunicationManager;

public class CustomPerConnectionWSH implements WebSocketHandler, BeanFactoryAware {

	private static final Log logger = LogFactory.getLog(PerConnectionWebSocketHandler.class);

	private final BeanCreatingHandlerProvider<WebSocketHandler> provider;
	
	/*
	 * Gerenciador do jogo, responsável por processar os movimentos do tabuleiro
	 * */
	GameManager gameManager = new GameManager();

	/*
	 * variável responsável por armazenar as sessões conectadas ao websocket
	 * */	
	private final Map<WebSocketSession, WebSocketHandler> handlers = new ConcurrentHashMap<>();
	
	private final boolean supportsPartialMessages;

	public CustomPerConnectionWSH(Class<? extends WebSocketHandler> handlerType) {
		this(handlerType, false);
	}

	public CustomPerConnectionWSH(Class<? extends WebSocketHandler> handlerType, boolean supportsPartialMessages) {
		this.provider = new BeanCreatingHandlerProvider<>(handlerType);
		this.supportsPartialMessages = supportsPartialMessages;
	}

	/*
	 * gerencia as sessões conectadas, definindo os jogadores 1 e 2 por ordem de conexão,
	 * impede a conexão de mais de 2 sessões no websocket
	 * serializa o json para comunicação com front-end
	 * */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("GERENCIADOR DE SESSÕES RECEBEU NOVA SESSÃO");
		WebSocketHandler handler = this.provider.getHandler();
		if (this.handlers.size() == 0) {
			this.handlers.put(session, handler);
			
			session.sendMessage(new TextMessage("{\r\n"
					+ "  \"code\": \"2\",\r\n"
					+ "  \"message\": \"Aguardando oponente\"\r\n"
					+ "}"));
			session.getAttributes().put("player", "j1");
			session.sendMessage(new TextMessage("{\r\n"
					+ "  \"code\": \"4\",\r\n"
					+ "  \"message\": \"j1\"\r\n"
					+ "}"));
			handler.afterConnectionEstablished(session);
			
		} else if (this.handlers.size() == 1) {
			this.handlers.put(session, handler);
			session.getAttributes().put("player", "j2");
			ComunicationManager.sendToAll(this.handlers, "{\r\n"
						+ "  \"code\": \"3\",\r\n"
						+ "  \"message\": \"Partida iniciando\"\r\n"
						+ "}");
			session.sendMessage(new TextMessage("{\r\n"
					+ "  \"code\": \"4\",\r\n"
					+ "  \"message\": \"j2\"\r\n"
					+ "}"));
			
			/*
			 * mapa para serializar 2 objetos em 1 único JSON
			 * */
			
			Map<String, Object> serialMap = new LinkedHashMap<>();
			serialMap.put("table", gameManager.getInitialTable());
			serialMap.put("turn", gameManager.getTurn());
			serialMap.put("code", "6");
			
			String serialized = new ObjectMapper().writeValueAsString(serialMap);
			
			ComunicationManager.sendToAll(this.handlers, serialized);
		
		} else {
			session.sendMessage(new TextMessage("{\r\n"
					+ "  \"code\": \"0\",\r\n"
					+ "  \"message\": \"Partida em andamento\"\r\n"
					+ "}"));
		}
		
		

	}
	
	/*
	 * Trata  ou delega o tratamento de mensagens recebidas do front-end
	 * */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		if(!session.getAttributes().containsKey("ign")) {
			session.getAttributes().put("ign", message.getPayload().toString());
			if(this.handlers.size() == 2) {
				ComunicationManager.sendAllNamesToAllSessions(this.handlers);
			}
		}
		
		String[] splittedMessage = message.getPayload().toString().split("@");
		System.out.println(message.getPayload().toString());
		if(splittedMessage[0].equals("MOVE")) {
			String[] splittedOldSpot = splittedMessage[1].split("/");
			String[] splittedNewSpot = splittedMessage[2].split("/");
			Table newTable = gameManager.getNewTable(new Spot(splittedOldSpot[0], splittedOldSpot[1]), 
										new Spot(splittedNewSpot[0], splittedNewSpot[1]));
			Turn nextTurn = gameManager.getTurn();
			
			if(GameManager.getPlayer1PiecesCounter()==0) {
				session.sendMessage(new TextMessage("{\r\n"
						+ "  \"code\": \"7\",\r\n"
						+ "  \"message\": \"Player 2 venceu\"\r\n"
						+ "}"));
			} else if (GameManager.getPlayer2PiecesCounter()==0) {
				session.sendMessage(new TextMessage("{\r\n"
						+ "  \"code\": \"7\",\r\n"
						+ "  \"message\": \"Player 1 venceu\"\r\n"
						+ "}"));
			}
			
			Map<String, Object> serialMap = new LinkedHashMap<>();
			serialMap.put("table", newTable);
			serialMap.put("turn", nextTurn);
			serialMap.put("code", "6");
			
			String serialized = new ObjectMapper().writeValueAsString(serialMap);
			
			ComunicationManager.sendToAll(this.handlers, serialized);
		}
		
		
	
		getHandler(session).handleMessage(session, message);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		System.out.println("Player desconectado");
		try {
			getHandler(session).afterConnectionClosed(session, closeStatus);
		} finally {
			destroyHandler(session);
			ComunicationManager.sendToAll(handlers, "{\r\n"
					+ "  \"code\": \"1\",\r\n"
					+ "  \"message\": \"Oponente desconectado\"\r\n"
					+ "}");
		}
		if (handlers.size()==0) {
			gameManager.finishedGame();
		}
	}

	private WebSocketHandler getHandler(WebSocketSession session) {
		WebSocketHandler handler = this.handlers.get(session);
		if (handler == null) {
			throw new IllegalStateException("WebSocketHandler not found for " + session);
		}
		return handler;
	}

	private void destroyHandler(WebSocketSession session) {
		WebSocketHandler handler = this.handlers.remove(session);
		try {
			if (handler != null) {
				this.provider.destroy(handler);
			}
		} catch (Throwable ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error while destroying " + handler, ex);
			}
		}
	}

	@Override
	public String toString() {
		return "PerConnectionWebSocketHandlerProxy[handlerType=" + this.provider.getHandlerType() + "]";
	}
	

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.provider.setBeanFactory(beanFactory);
	}


	@Override
	public boolean supportsPartialMessages() {
		return this.supportsPartialMessages;
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		getHandler(session).handleTransportError(session, exception);
	}

	

}
