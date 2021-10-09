/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import me.filoghost.fcommons.logging.Log;

public class FCommons {

	private static Plugin pluginInstance;

	public static void setPluginInstance(Plugin pluginInstance) {
		FCommons.pluginInstance = pluginInstance;
	}

	public static Plugin getPluginInstance() {
		if (pluginInstance == null) {
			throw new IllegalStateException("no plugin instance has been set");
		}
		return pluginInstance;
	}

	public static Logger getLogger() {
		return null;
	}
}
