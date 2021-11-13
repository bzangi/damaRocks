package com.qulture.draughts.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Classe responsável pela comunicação com as sessões
 * */

public class ComunicationManager {

	/*
	 * Envia uma mensagem para todas as sessões conectadas no websocket
	 * */
	public static void sendToAll(Map<WebSocketSession, WebSocketHandler> map, String message) {
		map.entrySet().stream().forEach(player -> {
			try {
				player.getKey().sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/*
	 * Envia o nome inserido em uma sessão para a outra sessão conectada no websocket
	 * */	
	public static void sendAllNamesToAllSessions(Map<WebSocketSession, WebSocketHandler> map) throws JsonProcessingException{
		Map<String, String> playersMap = new LinkedHashMap<>();
		map.entrySet().stream().forEach(player -> {
			playersMap.put(player.getKey().getAttributes().get("player").toString(), player.getKey().getAttributes().get("ign").toString());
		});
		playersMap.put("code", "5");
		String serialized = new ObjectMapper().writeValueAsString(playersMap);
		sendToAll(map, serialized);
	}
	
}
