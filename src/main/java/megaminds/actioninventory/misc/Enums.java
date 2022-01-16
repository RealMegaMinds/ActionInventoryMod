package megaminds.actioninventory.misc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.Identifier;

public class Enums {
	public enum GuiType {PLAYER, ENDER_CHEST, NAMED_GUI, GENERATED}
	public enum MessagePlaceHolders {PLAYER, SERVER, BROADCAST}
	
	private Enums() {}
	
	public enum TagOption {
		ALL {@Override public boolean matches(Set<Identifier> checkFor, Collection<Identifier> checkIn) {return checkIn.containsAll(checkFor);}},
		NONE {
			@Override
			public boolean matches(Set<Identifier> checkFor, Collection<Identifier> checkIn) {
				for (Identifier id : checkFor) {
					if (checkIn.contains(id)) return false;
				}
				return true;
			}
		},
		EXACT {@Override public boolean matches(Set<Identifier> checkFor, Collection<Identifier> checkIn) {return new HashSet<>(checkIn).equals(checkFor);}},
		ANY {
			@Override
			public boolean matches(Set<Identifier> checkFor, Collection<Identifier> checkIn) {
				for (Identifier id : checkFor) {
					if (checkIn.contains(id)) return true;
				}
				return false;
			}
		};
		public abstract boolean matches(Set<Identifier> checkFor, Collection<Identifier> checkIn);
	}
}