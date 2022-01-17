package megaminds.actioninventory.commands;

import static net.minecraft.server.command.CommandManager.literal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.loaders.NamedGuiLoader;
import megaminds.actioninventory.misc.Saver;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.MessageHelper;
import megaminds.actioninventory.util.Printer;

import static net.minecraft.command.argument.IdentifierArgumentType.identifier;
import static net.minecraft.command.argument.IdentifierArgumentType.getIdentifier;

import net.minecraft.command.CommandSource;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.minecraft.server.command.CommandManager.argument;

@SuppressWarnings("all")
public class CreateCommand {
	private static final DynamicCommandExceptionType UNKNOWN_TYPE = new DynamicCommandExceptionType(i->new LiteralText("Unknown screen handler type: "+i));

	private static final SuggestionProvider<ServerCommandSource> SCREEN_SUGGESTIONS = (context, builder) -> {
		return CommandSource.suggestIdentifiers(Registry.SCREEN_HANDLER.getIds(), builder);
	};

	private static final SuggestionProvider<ServerCommandSource> COPY_SUGGESTIONS = (context, builder) -> {
		return CommandSource.suggestIdentifiers(NamedGuiLoader.builderNames(), builder);
	};

	private static final BuilderList BUILDERS = new BuilderList();

	public static void register() {
		//		LiteralArgumentBuilder<ServerCommandSource> edit = literal("edit");
		//		LiteralArgumentBuilder<ServerCommandSource> opener = literal("opener");

		LiteralCommandNode<ServerCommandSource> create = 
				literal("create")
				.then(literal("builder")
						.then(argument("type", identifier())
								.suggests(SCREEN_SUGGESTIONS)
								.then(argument("name", identifier())
										.executes(c->createBuilder(getIdentifier(c, "type"), getIdentifier(c, "name"), false, c))
										.then(argument("includePlayerInventorySlots", bool())
												.executes(c->createBuilder(getIdentifier(c, "type"), getIdentifier(c, "name"), getBool(c, "includePlayerInventorySlots"), c))
												)
										)
								)
						)
				.build();
		LiteralCommandNode<ServerCommandSource> copy = 
				literal("copy")
				.then(argument("name", identifier())
						.suggests(SCREEN_SUGGESTIONS)
						.executes(CreateCommand::copyBuilder)
						)
				.build();
	}

	private static int copyBuilder(CommandContext<ServerCommandSource> context) {
		Identifier name = getIdentifier(context, "name");
		if (Helper.containsAny(BUILDERS.builders, b->b.getName().equals(name))) {
			context.getSource().sendError(new LiteralText("An in-progress builder of this name already exists: "+name));
			return 0;
		}
		BUILDERS.builders.add(NamedGuiLoader.getBuilder(name).copy());
		context.getSource().sendFeedback(MessageHelper.toSuccess(name+" copied."), false);
		return 1;
	}

	private static int createBuilder(Identifier type, Identifier name, boolean includePlayer, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ScreenHandlerType<?> sType = Registry.SCREEN_HANDLER.get(type);
		if (sType==null) UNKNOWN_TYPE.create(sType);
		if (Helper.containsAny(BUILDERS.builders, b->b.getName().equals(name))) {
			context.getSource().sendError(new LiteralText("An in-progress builder of this name already exists: "+name));
			return 0;
		}
		BUILDERS.builders.add(new NamedGuiBuilder(sType, name, includePlayer));
		context.getSource().sendFeedback(MessageHelper.toSuccess(name+" created."), false);
		return 1;
	}

	private static class BuilderList extends Saver {
		public final List<NamedGuiBuilder> builders = new ArrayList<>();
		@Override
		public void load(Path saveDir) {}
		@Override
		public void save(Path saveDir) {
			saveDir = saveDir.resolve("builders");
			for (NamedGuiBuilder b : builders) {
				Printer.print(b, saveDir);
			}
			builders.clear();
		}
	}
}









