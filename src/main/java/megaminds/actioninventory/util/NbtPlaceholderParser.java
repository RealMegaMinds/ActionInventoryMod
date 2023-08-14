package megaminds.actioninventory.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

public class NbtPlaceholderParser {
	private NbtPlaceholderParser() {}

	public static void replaceCompound(PlaceholderContext context, NbtCompound compound) {
		for (var key : compound.getKeys()) {
			var type = compound.getType(key);
			if (type == NbtElement.STRING_TYPE) {
				replaceString(context, compound, key);
			} else if (type == NbtElement.LIST_TYPE) {
				replaceList(context, compound.getList(key, NbtElement.STRING_TYPE));
			} else if (type == NbtElement.COMPOUND_TYPE) {
				replaceCompound(context, compound.getCompound(key));
			}
		}
	}

	public static void replaceString(PlaceholderContext context, NbtCompound compound, String key) {
		compound.put(key, parseString(context, compound.getString(key)));
	}

	public static void replaceList(PlaceholderContext context, NbtList list) {
		for (var i = 0; i < list.size(); i++) {
			list.set(i, parseString(context, list.getString(i)));
		}
	}

	public static NbtString parseString(PlaceholderContext context, String string) {
		return NbtString.of(Placeholders.parseText(Text.of(string), context).getString());
	}
}
