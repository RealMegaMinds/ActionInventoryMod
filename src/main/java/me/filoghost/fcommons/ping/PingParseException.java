/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.ping;

public class PingParseException extends Exception {

    private final String jsonString;

    public PingParseException(String message, String jsonString) {
        super(message);
        this.jsonString = jsonString;
    }

    public String getJsonString() {
        return jsonString;
    }

}
