package megaminds.actioninventory.openers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.util.Helper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemOpener extends BasicOpener {
	private static final String ITEM = "item", NAME = "customName", TEXT_ONLY = "textOnly", NBT = "nbt", TAGS = "tags";
	private static final List<ItemOpener> OPENERS = new ArrayList<>();

	private Item item;
	private Text customName;
	private boolean textOnly;
	private NbtCompound nbt;
	private List<Identifier> tags;

	@Override
	public BasicOpener fromJson(JsonObject obj, JsonDeserializationContext context) {
		item = obj.has(ITEM) ? Registry.ITEM.get(new Identifier(obj.get(ITEM).getAsString())) : null;
		customName = obj.has(NAME) ? Text.Serializer.fromJson(obj.get(NAME)) : null;
		textOnly = obj.has(TEXT_ONLY) ? obj.get(TEXT_ONLY).getAsBoolean() : true;
		try {
			nbt = obj.has(NBT) ? new StringNbtReader(new StringReader(obj.get(NBT).getAsString())).parseCompound()  : null;
		} catch (CommandSyntaxException e) {
			ActionInventoryMod.warn("Failed to read nbt data for an ItemOpener.");
		}
		tags = obj.has(TAGS) ? Helper.toList(obj.get(TAGS).getAsJsonArray(), e->new Identifier(e.getAsString())) : null;
		return this;
	}
	
	@Override
	public boolean open(ServerPlayerEntity player, Object... context) {
		if (context[0] instanceof ItemStack s 
				&& Helper.nullOrEquals(item, s.getItem())
				&& Helper.nullOr(customName, c->s.hasCustomName() && (textOnly ? c.toString() : c).equals(textOnly ? s.getName().toString() : s.getName()))
				&& Helper.compareNbt(nbt, s.writeNbt(new NbtCompound()))
				&& Helper.nullOr(tags, t->ItemTags.getTagGroup().getTagsFor(s.getItem()).containsAll(t))) {
			return super.open(player, context);
		}
		return false;
	}
	
	@Override
	public Opener getType() {
		return Opener.ITEM;
	}
	
	public boolean addToMap() {
		return OPENERS.contains(this) || OPENERS.add(this);
	}
	
	public void removeFromMap() {
		OPENERS.remove(this);
	}
	
	public static boolean tryOpen(ServerPlayerEntity p, ItemStack s) {
		return Helper.getFirst(OPENERS, o->o.open(p, s))!=null;
	}
}