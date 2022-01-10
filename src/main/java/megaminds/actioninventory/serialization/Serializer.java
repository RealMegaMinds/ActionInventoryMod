package megaminds.actioninventory.serialization;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import megaminds.actioninventory.gui.AccessableAnimatedGuiElement;
import megaminds.actioninventory.gui.AccessableGuiElement;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.SlotFunction;
import megaminds.actioninventory.openers.BasicOpener;
import megaminds.actioninventory.util.Helper;
import megaminds.actioninventory.util.ItemStackish;
import megaminds.actioninventory.util.JsonHelper;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

public class Serializer {
	private static JsonSerializer<Object> CANT_SERIALIZE = (s, t, c)->new JsonPrimitive("Can't serialize");
	public static final Gson GSON;
	
	public static NamedGuiBuilder builderFromJson(Reader json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static NamedGuiBuilder builderFromJson(String json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static NamedGuiBuilder builderFromJson(JsonObject json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static BasicOpener openerFromJson(Reader json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	public static BasicOpener openerFromJson(JsonObject json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	public static BasicOpener openerFromJson(String json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	static {
		GSON = new GsonBuilder()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.registerTypeAdapter(AccessableGuiElement.class, new AccessableGuiElementSerializer())
				.registerTypeAdapter(AccessableAnimatedGuiElement.class, new AccessableAnimatedGuiElementSerializer())
				.registerTypeAdapter(ItemStack.class, delegateD(ItemStackish.class, ItemStackish::toStack))
				.registerTypeAdapter(ItemStack.class, CANT_SERIALIZE)
				.registerTypeAdapter(NamedGuiBuilder.class, new NamedGuiBuilderSerializer())
				.registerTypeHierarchyAdapter(SlotFunction.class, new SlotFunctionSerializer())
				.registerTypeAdapter(Identifier.class, delegate(String.class, Identifier::new, Identifier::toString))
				.registerTypeHierarchyAdapter(Text.class, basic(Text.Serializer::fromJson, Text.Serializer::toJsonTree))
				.registerTypeAdapter(Item.class, registryDelegate(Registry.ITEM))
				.registerTypeAdapter(Enchantment.class, registryDelegate(Registry.ENCHANTMENT))
				.registerTypeAdapter(EntityAttribute.class, registryDelegate(Registry.ATTRIBUTE))
				.registerTypeAdapter(Block.class, registryDelegate(Registry.BLOCK))
				.registerTypeAdapter(BlockEntity.class, registryDelegate(Registry.BLOCK_ENTITY_TYPE))
				.registerTypeAdapter(EntityType.class, registryDelegate(Registry.ENTITY_TYPE))
				.registerTypeAdapter(SoundEvent.class, registryDelegate(Registry.SOUND_EVENT))
				.registerTypeAdapter(ScreenHandler.class, registryDelegate(Registry.SCREEN_HANDLER))
				.registerTypeAdapter(StatusEffect.class, registryDelegate(Registry.STATUS_EFFECT))
				.registerTypeAdapter(ParticleType.class, registryDelegate(Registry.PARTICLE_TYPE))
				.registerTypeAdapter(Optional.class, new OptionalAdapterFactory())
				.create();
	}
	
	private static <T> Both<T> basic(Function<JsonElement, T> from, Function<T, JsonElement> to) {
		return new Both<T>() {
			@Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {return from.apply(json);}
			@Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {return to.apply(src);}
		};
	}
	
	private static <T, D> Both<T> delegate(Class<D> delegate, Function<D, T> from, Function<T, D> to) {
		return new Both<T>(){
			@Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {return JsonHelper.notNull(json) ? Helper.apply(context.deserialize(json, delegate), from) : null;}
			@Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {return Helper.apply(src, e2->Helper.apply(to.apply(e2), context::serialize, JsonNull.INSTANCE), JsonNull.INSTANCE);}
		};
	}
	
	private static <T> Both<T> registryDelegate(Registry<T> registry) {
		return delegate(Identifier.class, registry::get, registry::getId);
	}
	
	private static <T, D> Both<T> delegateD(Class<D> delegate, Function<D, T> from) {
		return new Both<T>() {
			@Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {return JsonHelper.notNull(json) ? Helper.apply(context.deserialize(json, delegate), from) : null;}
			@Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {return new JsonPrimitive("Cannot Serialize. No delegate serializer for type: "+typeOfSrc);}
		};
	}
	
//	private static <T> Both<T> combine(JsonDeserializer<T> from, JsonSerializer<T> to) {
//		return new Both<T>(){
//			@Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {return from.deserialize(json, typeOfT, context);}
//			@Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {return to.serialize(src, typeOfSrc, context);}
//		};
//	}
//	
	private static interface Both<T> extends JsonDeserializer<T>, JsonSerializer<T> {}
}