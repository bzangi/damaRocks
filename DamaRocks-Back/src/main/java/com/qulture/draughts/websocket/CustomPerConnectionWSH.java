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
import com.qulture.draughts.util.ComunicationManager;

public class CustomPerConnectionWSH implements WebSocketHandler, BeanFactoryAware {

	private static final Log logger = LogFactory.getLog(PerConnectionWebSocketHandler.class);

	private final BeanCreatingHandlerProvider<WebSocketHandler> provider;

	private final Map<WebSocketSession, WebSocketHandler> handlers = new ConcurrentHashMap<>();
	
	private final boolean supportsPartialMessages;

	public CustomPerConnectionWSH(Class<? extends WebSocketHandler> handlerType) {
		this(handlerType, false);
	}

	public CustomPerConnectionWSH(Class<? extends WebSocketHandler> handlerType, boolean supportsPartialMessages) {
		this.provider = new BeanCreatingHandlerProvider<>(handlerType);
		this.supportsPartialMessages = supportsPartialMessages;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.provider.setBeanFactory(beanFactory);
	}

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
			handler.afterConnectionEstablished(session);
		} else if (this.handlers.size() == 1) {
			this.handlers.put(session, handler);
			ComunicationManager.sendToAll(this.handlers, "{\r\n"
						+ "  \"code\": \"3\",\r\n"
						+ "  \"message\": \"Partida iniciando\"\r\n"
						+ "}");
			
			/*
			 * mapa para serializar 2 objetos em 1 único JSON
			 * */
			Map<String, Object> serialMap = new LinkedHashMap<>();
			GameManager gameManager = new GameManager();
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

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		getHandler(session).handleMessage(session, message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		getHandler(session).handleTransportError(session, exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		try {
			getHandler(session).afterConnectionClosed(session, closeStatus);
		} finally {
			destroyHandler(session);
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return this.supportsPartialMessages;
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

}
