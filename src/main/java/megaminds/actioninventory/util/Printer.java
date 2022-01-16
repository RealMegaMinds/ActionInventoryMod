package megaminds.actioninventory.util;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eu.pb4.sgui.api.ScreenProperty;
import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.OpenGui;
import megaminds.actioninventory.actions.CloseAction;
import megaminds.actioninventory.actions.CommandAction;
import megaminds.actioninventory.actions.ConsumeAction;
import megaminds.actioninventory.actions.GiveAction;
import megaminds.actioninventory.actions.GroupAction;
import megaminds.actioninventory.actions.MessageAction;
import megaminds.actioninventory.actions.RequirementAction;
import megaminds.actioninventory.actions.SendPropertyAction;
import megaminds.actioninventory.actions.SoundAction;
import megaminds.actioninventory.consumables.BasicConsumable;
import megaminds.actioninventory.consumables.XpConsumable;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.elements.AccessableGuiElement;
import megaminds.actioninventory.gui.elements.SlotElement;
import megaminds.actioninventory.misc.Enums.GuiType;
import megaminds.actioninventory.misc.Enums.TagOption;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;
import megaminds.actioninventory.serialization.PolyAdapterFactory;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.serialization.wrappers.InstancedAdapterWrapper;
import megaminds.actioninventory.serialization.wrappers.TypeAdapterWrapper;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.serialization.wrappers.ValidatedAdapterWrapper;
import megaminds.actioninventory.serialization.wrappers.WrapperAdapterFactory;
import megaminds.actioninventory.util.annotations.Instanced;
import megaminds.actioninventory.util.annotations.Poly;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.MessageType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@SuppressWarnings("all")	//Testing only class
public class Printer {
	public static void dump(Path gameDir) {
		try {
			Files.writeString(nextFile(gameDir, "FurnaceBuilder"), Serializer.GSON.toJson(createFurnaceBuilder()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			Files.writeString(nextFile(gameDir, "9x9Builder"), Serializer.GSON.toJson(create9x6Builder()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			Files.writeString(nextFile(gameDir, "Bopener"), Serializer.GSON.toJson(createBlockOpener()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			Files.writeString(nextFile(gameDir, "Eopener"), Serializer.GSON.toJson(createEntityOpener()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			Files.writeString(nextFile(gameDir, "Iopener"), Serializer.GSON.toJson(createItemOpener()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Path nextFile(Path p, String name) {
		Path r = p.resolve(name+".json");
		int i = 1;
		while (Files.exists(r)) r = p.resolve(name+" ("+ i++ +").json");
		return r;
	}
	
	private static NamedGuiBuilder create9x6Builder() {
		ItemStack[] items = {
				new ItemStack(Items.ACACIA_BOAT, 1),
				new ItemStack(Items.DIAMOND_SWORD),
				new ItemStack(Items.AZALEA_LEAVES_FLOWERS, 5)
		};
		
		BasicConsumable[] consumables = {
				new XpConsumable(false, 2, 0)
		};

		BasicAction[] actions2 = {
				new CommandAction("/help", false, false),
				new GiveAction(items)
		};

		BasicAction[] actions3 = {
				new MessageAction(new LiteralText("Helooo"), null, null, MessageType.CHAT),
				new SendPropertyAction(ScreenProperty.CURRENT_PROGRESS, 100)
		};

		BasicAction[] actions4 = {
				new SoundAction(SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, null, null, null),
				new CommandAction("/kill @p", true, false)
		};

		ConsumeAction ca2 = new ConsumeAction(actions2, null, false, true);
		GroupAction ga = new GroupAction(actions3);
		RequirementAction ra = new RequirementAction(actions4, "[gamemode=spectator]");
		
		List<SlotElement> elements = new ArrayList<>(
				Arrays.asList(
						new AccessableGuiElement(3, ca2, new ItemStack(Items.AZURE_BLUET, 6)),
						new AccessableGuiElement(14, ga, new ItemStack(Items.AXOLOTL_SPAWN_EGG, 17)),
						new AccessableGuiElement(22, ra, new ItemStack(Items.ZOMBIE_HEAD, 51))
						));

		NamedGuiBuilder builder = new NamedGuiBuilder(ScreenHandlerType.GENERIC_9X6, new Identifier("9x6builder"), new LiteralText("9x6 Builder"), false, elements.toArray(SlotElement[]::new));
		return builder;
	}

	private static NamedGuiBuilder createFurnaceBuilder() {
		BasicConsumable[] consumables = {
				new XpConsumable(false, 2, 0)
		};
		
		BasicAction[] actions1 = {
				new CommandAction("/give @s minecraft:diamond 64", false, true),
				new CloseAction()
		};

		OpenGui cga = new OpenGui(GuiType.PLAYER, (UUID)null);
		OpenGui cga2 = new OpenGui(GuiType.NAMED_GUI, new Identifier("9x6builder"));
		ConsumeAction ca = new ConsumeAction(actions1, consumables, false, false);

		List<SlotElement> elements = new ArrayList<>(
				Arrays.asList(
						new AccessableGuiElement(0, cga, new ItemStack(Items.PORKCHOP, 12)),
						new AccessableGuiElement(1, cga2, new ItemStack(Items.COAL, 1)),
						new AccessableGuiElement(2, ca, new ItemStack(Items.ACACIA_PLANKS, 13))
						));

		NamedGuiBuilder builder = new NamedGuiBuilder(ScreenHandlerType.FURNACE, new Identifier("furnacegui"), new LiteralText("Furnace Builder"), false, elements.toArray(SlotElement[]::new));
		return builder;
	}
	
	private static BlockOpener createBlockOpener() {
		return new BlockOpener(new Identifier("furnacegui"), Blocks.GOLD_BLOCK, null, null, null, null);
	}
	private static EntityOpener createEntityOpener() {
		return new EntityOpener(new Identifier("furnacegui"), null);
	}
	private static ItemOpener createItemOpener() {
		return new ItemOpener(new Identifier("furnacegui"), null, Set.of(ItemTags.LOGS.getId(), ItemTags.WOOL.getId()), TagOption.ANY);
	}
}