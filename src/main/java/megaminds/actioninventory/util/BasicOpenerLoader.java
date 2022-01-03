package megaminds.actioninventory.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.serialization.Serializer;

public class BasicOpenerLoader {
	private static final List<BasicOpener> loadedOpeners = new ArrayList<>();

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
					BasicOpener opener = Serializer.openerFromJson(br);
					if (opener.addToMap()) {
						loadedOpeners.add(opener);
						count[0]++;
					} else {
						ActionInventoryMod.warn("An Opener with name: '"+opener.getName()+"' already exists.");
						count[1]++;
					}
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
	
	public static void clear() {
		loadedOpeners.forEach(o->o.removeFromMap());
		loadedOpeners.clear();
	}
}