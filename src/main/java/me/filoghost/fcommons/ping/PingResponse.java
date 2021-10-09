/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.ping;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PingResponse {

	private final String motd;
	private final int onlinePlayers;
	private final int maxPlayers;

	private PingResponse(String motd, int onlinePlayers, int maxPlayers) {
		this.motd = motd;
		this.onlinePlayers = onlinePlayers;
		this.maxPlayers = maxPlayers;
	}

	static PingResponse fromJson(String jsonString) throws PingParseException {
		if (jsonString.isEmpty()) {
			throw new PingParseException("empty", jsonString);
		}

		Object jsonObject = JSONValue.parse(jsonString);

		if (!(jsonObject instanceof JSONObject)) {
			throw new PingParseException("wrong format", jsonString);
		}

		JSONObject json = (JSONObject) jsonObject;

		Object descriptionObject = json.get("description");

		String motd;
		int onlinePlayers = 0;
		int maxPlayers = 0;

		if (descriptionObject == null) {
			throw new PingParseException("description not found", jsonString);
		}

		motd = descriptionObject.toString();

		Object playersObject = json.get("players");

		if (playersObject instanceof JSONObject) {
			JSONObject playersJson = (JSONObject) playersObject;

			Object onlineObject = playersJson.get("online");
			if (onlineObject instanceof Number) {
				onlinePlayers = ((Number) onlineObject).intValue();
			}

			Object maxObject = playersJson.get("max");
			if (maxObject instanceof Number) {
				maxPlayers = ((Number) maxObject).intValue();
			}
		}

		return new PingResponse(motd, onlinePlayers, maxPlayers);
	}

	public String getMotd() {
		return motd;
	}

	public int getOnlinePlayers() {
		return onlinePlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

}
