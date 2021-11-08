package com.qulture.draughts.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/*
 * Classe responsável por lidar com as interações do WebSocket
 *  
 * */
@Component
public class GameWebSocketHandler extends AbstractWebSocketHandler  {

	/*
	 * método responsável por lidar com a mensagem recebida do client
	 */
	
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println(message.getPayload().toString());
		session.sendMessage(new TextMessage("MENSAGEM RECEBIDA PELO SERVIDOR"));
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("Conexão encerrada");
	}

}
