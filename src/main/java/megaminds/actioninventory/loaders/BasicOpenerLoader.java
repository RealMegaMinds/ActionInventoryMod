package megaminds.actioninventory.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;
import megaminds.actioninventory.serialization.Serializer;
import megaminds.actioninventory.util.ValidationException;

public class BasicOpenerLoader {
	private BasicOpenerLoader() {}

	public static void load(Path[] paths) {
		for (Path p : paths) {
			load(p);
		}
	}

	public static int[] load(Path path) {
		int[] count = new int[2];
		try (Stream<Path> files = Files.list(path)) {
			files.filter(p->p.toString().endsWith(".json"))
			.forEach(p->{
				try (BufferedReader br = Files.newBufferedReader(p)) {
					count[loadOpener(br) ? 0 : 1]++;
				} catch (IOException e) {
					ActionInventoryMod.warn("Failed to read Opener from: "+p);
					count[1]++;
				}
			});
		} catch (IOException e) {
			ActionInventoryMod.warn("Cannot read files from: "+path);
		}
		ActionInventoryMod.info("Loaded "+count[0]+" Openers. Failed to load "+count[1]+". ("+path+")");
		return count;
	}

	private static boolean loadOpener(BufferedReader br) {
		try {
			Serializer.openerFromJson(br);
			return true;
		} catch (ValidationException e) {
			ActionInventoryMod.warn("Opener Validation Exception: "+e.getMessage());
			return false;
		}
	}

	public static void clear() {
		BlockOpener.clearOpeners();
		ItemOpener.clearOpeners();
		EntityOpener.clearOpeners();
	}
}