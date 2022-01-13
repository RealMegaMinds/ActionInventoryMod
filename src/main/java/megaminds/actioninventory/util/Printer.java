package megaminds.actioninventory.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.serialization.Serializer;
import net.minecraft.item.ItemStack;

public class Printer {
	private Printer() {}
	
	public static void print() {
		System.out.println(Serializer.GSON.toJson(createBuilder()));	//NOSONAR Testing only.
	}

	public static void dump(Path gameDir) {
		try {
			Files.writeString(nextFile(gameDir), Serializer.GSON.toJson(createBuilder()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Path nextFile(Path p) {
		Path r = p.resolve("PrinterDump.txt");
		int i = 1;
		while (Files.exists(r)) r = p.resolve("PrinterDump ("+ i++ +").txt");
		return r;
	}
	
	private static NamedGuiBuilder createBuilder() {
		NamedGuiBuilder builder = new NamedGuiBuilder(null, false);
		builder.addSlot((ItemStack)null);
		//TODO finish
		return builder;
	}
}