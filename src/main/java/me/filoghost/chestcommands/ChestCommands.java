/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands;

import me.filoghost.chestcommands.api.internal.BackendAPI;
import me.filoghost.chestcommands.command.CommandHandler;
import me.filoghost.chestcommands.config.ConfigManager;
import me.filoghost.chestcommands.config.CustomPlaceholders;
import me.filoghost.chestcommands.config.Settings;
import me.filoghost.chestcommands.legacy.UpgradeExecutorException;
import me.filoghost.chestcommands.legacy.UpgradesExecutor;
import me.filoghost.chestcommands.listener.CommandListener;
import me.filoghost.chestcommands.listener.InventoryListener;
import me.filoghost.chestcommands.listener.JoinListener;
import me.filoghost.chestcommands.listener.SignListener;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.logging.PrintableErrorCollector;
import me.filoghost.chestcommands.menu.MenuManager;
import me.filoghost.chestcommands.parsing.menu.LoadedMenu;
import me.filoghost.chestcommands.placeholder.PlaceholderManager;
import me.filoghost.chestcommands.task.TickingTask;
import me.filoghost.fcommons.config.ConfigLoader;
import me.filoghost.fcommons.logging.ErrorCollector;
import me.filoghost.fcommons.logging.Log;
import me.filoghost.fcommons.reflection.ReflectUtils;
import megaminds.testmod.callbacks.InventoryEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;

import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ChestCommands implements ModInitializer {
	public static final String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "ChestCommands" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;

	private static Path dataFolderPath;

	private static ConfigManager configManager;
	private static CustomPlaceholders placeholders;

	private static ErrorCollector lastLoadErrors;
	private static String newVersion;

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world)->{
			dataFolderPath = server.getSavePath(WorldSavePath.ROOT).resolve("Chest Commands");
			configManager = new ConfigManager(getDataFolderPath());
			placeholders = new CustomPlaceholders();

			BackendAPI.setImplementation(new DefaultBackendAPI());
		});
		
		CommandRegistrationCallback.EVENT.register(CommandListener::register);
		UseItemCallback.EVENT.register(InventoryListener::onItemUse);
		UseBlockCallback.EVENT.register(InventoryListener::onBlockUse);
		AttackBlockCallback.EVENT.register(InventoryListener::onBlockAttack);
		InventoryEvents.BEFORE_SLOT_CLICK.register(InventoryListener::onInventoryClick);
	}

	protected void onCheckedEnable() {



		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);

		new CommandHandler("chestcommands").register(this);

		ErrorCollector errorCollector = load();

		if (errorCollector.hasErrors()) {
			errorCollector.logToConsole();
			Bukkit.getScheduler().runTaskLater(this, () -> {
				Bukkit.getConsoleSender().sendMessage(
						ChestCommands.CHAT_PREFIX + ChatColor.RED + "Encountered " + errorCollector.getErrorsCount() + " error(s) on load. "
								+ "Check previous console logs or run \"/chestcommands errors\" to see them again.");
			}, 10L);
		}

		if (Settings.get().update_notifications) {
			UpdateChecker.run(this, 56919, (String newVersion) -> {
				ChestCommands.newVersion = newVersion;

				Log.info("Found a new version: " + newVersion + " (yours: v" + getDescription().getVersion() + ")");
				Log.info("Download the update on Bukkit Dev:");
				Log.info("https://dev.bukkit.org/projects/chest-commands");
			});
		}

		// Start bStats metrics
		int pluginID = 3658;
		new MetricsLite(this, pluginID);

		Bukkit.getScheduler().runTaskTimer(this, new TickingTask(), 1L, 1L);
	}

	public void onDisable() {
		MenuManager.closeAllOpenMenuViews();
	}

	public static ErrorCollector load() {
		ErrorCollector errorCollector = new PrintableErrorCollector();
		MenuManager.reset();
		boolean isFreshInstall = !Files.isDirectory(configManager.getRootDataFolder());
		try {
			Files.createDirectories(configManager.getRootDataFolder());
		} catch (IOException e) {
			errorCollector.add(e, Errors.Config.createDataFolderIOException);
			return errorCollector;
		}

		try {
			UpgradesExecutor upgradeExecutor = new UpgradesExecutor(configManager);
			boolean allUpgradesSuccessful = upgradeExecutor.run(isFreshInstall, errorCollector);
			if (!allUpgradesSuccessful) {
				errorCollector.add(Errors.Upgrade.failedSomeUpgrades);
			}
		} catch (UpgradeExecutorException e) {
			errorCollector.add(e, Errors.Upgrade.genericExecutorError);
			errorCollector.add(Errors.Upgrade.failedSomeUpgrades);
		}

		configManager.tryLoadSettings(errorCollector);
		configManager.tryLoadLang(errorCollector);
		placeholders = configManager.tryLoadCustomPlaceholders(errorCollector);
		PlaceholderManager.setStaticPlaceholders(placeholders.getPlaceholders());

		// Create the menu folder with the example menu
		if (!Files.isDirectory(configManager.getMenusFolder())) {
			ConfigLoader exampleMenuLoader = configManager.getConfigLoader(configManager.getMenusFolder().resolve("example.yml"));
			configManager.tryCreateDefault(errorCollector, exampleMenuLoader);
		}

		List<LoadedMenu> loadedMenus = configManager.tryLoadMenus(errorCollector);
		for (LoadedMenu loadedMenu : loadedMenus) {
			MenuManager.registerMenu(loadedMenu, errorCollector);
		}

		ChestCommands.lastLoadErrors = errorCollector;
		return errorCollector;
	}

	public static Path getDataFolderPath() {
		return dataFolderPath;
	}

	public static boolean hasNewVersion() {
		return newVersion != null;
	}

	public static String getNewVersion() {
		return newVersion;
	}

	public static ErrorCollector getLastLoadErrors() {
		return lastLoadErrors;
	}
}