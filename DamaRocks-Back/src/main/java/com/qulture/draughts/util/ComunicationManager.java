package com.qulture.draughts.util;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

public class ComunicationManager {

	
	public static void sendToAll(Map<WebSocketSession, WebSocketHandler> map, String message) {
		map.entrySet().stream().forEach(player -> {
			try {
				player.getKey().sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
