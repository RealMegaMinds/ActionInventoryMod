/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands;

import me.filoghost.chestcommands.api.internal.BackendAPI;
import me.filoghost.chestcommands.config.ConfigManager;
import me.filoghost.chestcommands.config.CustomPlaceholders;
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
import megaminds.testmod.Helper;
import megaminds.testmod.callbacks.InventoryEvents;
import megaminds.testmod.callbacks.SignFinishCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ChestCommands implements ModInitializer {
	private static final Text CHAT_PREFIX = new LiteralText("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)).append(new LiteralText("ChestCommands").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(new LiteralText("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))));

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
			
			ErrorCollector errorCollector = load();
			if (errorCollector.hasErrors()) {
				errorCollector.logToConsole(server);
				Helper.nullToServerMessage(getChatPrefix().append(new LiteralText("Encountered " + errorCollector.getErrorsCount() + " error(s) on load. "
						+ "Check previous console logs or run \"/chestcommands errors\" to see them again.").setStyle(Style.EMPTY.withColor(Formatting.RED))), server);
			}
			
			
			
			server.addServerGuiTickable(()->new TickingTask(server));
		});
		
		CommandRegistrationCallback.EVENT.register(CommandListener::register);
		UseItemCallback.EVENT.register(InventoryListener::onItemUse);
		UseBlockCallback.EVENT.register(InventoryListener::onBlockUse);
		AttackBlockCallback.EVENT.register(InventoryListener::onBlockAttack);
		InventoryEvents.BEFORE_SLOT_CLICK.register(InventoryListener::onInventoryClick);
		ServerPlayConnectionEvents.JOIN.register(JoinListener::onJoin);
		UseBlockCallback.EVENT.register(SignListener::onSignClick);
		SignFinishCallback.EVENT.register(SignListener::onSignChange);
	}

	protected void onCheckedEnable() {
//		new CommandHandler("chestcommands").register(this);
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
	
	public static MutableText getChatPrefix() {
		return CHAT_PREFIX.shallowCopy();
	}
}