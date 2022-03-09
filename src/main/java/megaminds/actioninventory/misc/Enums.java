package megaminds.actioninventory.misc;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Enums/constants/whatever else I put here
 */
public class Enums {
	public static final String COMPLETE = "COMPLETE";
	public enum GuiType {PLAYER, ENDER_CHEST, NAMED_GUI, GENERATED}
	public enum MessagePlaceHolders {PLAYER, SERVER, BROADCAST}

	private Enums() {}

	public enum TagOption {
		ALL {
			@Override
			public <E> boolean matches(Set<Identifier> checkFor, Stream<TagKey<E>> checkIn) {
				return checkIn.map(TagKey::id).collect(HashSet::new, Set::add, Set::addAll).containsAll(checkFor);
			}
		},
		NONE {
			@Override
			public <E> boolean matches(Set<Identifier> checkFor, Stream<TagKey<E>> checkIn) {
				return checkIn.map(TagKey::id).noneMatch(checkFor::contains);
			}
		},
		EXACT {
			@Override
			public <E> boolean matches(Set<Identifier> checkFor, Stream<TagKey<E>> checkIn) {
				return checkIn.map(TagKey::id).collect(HashSet::new, Set::add, Set::addAll).equals(checkFor);
			}
		},
		ANY {
			@Override
			public <E> boolean matches(Set<Identifier> checkFor, Stream<TagKey<E>> checkIn) {
				return checkIn.map(TagKey::id).anyMatch(checkFor::contains);
			}
		};

		public abstract <E> boolean matches(Set<Identifier> checkFor, Stream<TagKey<E>> checkIn);
	}
}