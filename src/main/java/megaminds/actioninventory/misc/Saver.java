package megaminds.actioninventory.misc;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Saver {
	private static Path savesDir;
	private static final List<Saver> SAVERS = new ArrayList<>();
	
	protected Saver() {
		SAVERS.add(this);
	}
	
	public static void setSavesDir(Path dir) {
		savesDir = dir;
	}
	
	public static void remove(Saver s) {
		SAVERS.remove(s);
	}
	
	public static void saveAll() {
		SAVERS.forEach(s->s.save(savesDir));
	}
	
	public static void load(Saver s) {
		s.load(savesDir);
	}
	
	public static void clear() {
		SAVERS.clear();
		savesDir = null;
	}

	public abstract void load(Path saveDir);
	public abstract void save(Path saveDir);
}