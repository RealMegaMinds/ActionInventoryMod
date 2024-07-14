package megaminds.actioninventory.serialization;

import java.lang.reflect.Type;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.serialization.wrappers.InstancedAdapterWrapper;
import megaminds.actioninventory.serialization.wrappers.ValidatedAdapterWrapper;
import megaminds.actioninventory.serialization.wrappers.WrapperAdapterFactory;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleType;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public class Serializer {
	private Serializer() {}

	public static Gson constructGson(RegistryWrapper.WrapperLookup lookup) {
		return new GsonBuilder()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.enableComplexMapKeySerialization()
				.setExclusionStrategies(new ExcludeStrategy())

				.registerTypeHierarchyAdapter(NbtElement.class, new NbtElementAdapter().nullSafe())
				.registerTypeHierarchyAdapter(Text.class, codecDelegate(TextCodecs.CODEC, lookup))

				.registerTypeAdapter(ClickCallback.class, delegate(BasicAction.class, ClickCallback.class::cast, BasicAction.class::cast))
				.registerTypeAdapter(ItemStack.class, codecDelegate(ItemStack.CODEC, lookup))
				.registerTypeAdapter(ItemPredicate.class, codecDelegate(ItemPredicate.CODEC, lookup))
				.registerTypeAdapter(Identifier.class, codecDelegate(Identifier.CODEC, lookup))
				.registerTypeAdapter(TriState.class, new TriStateAdapter())

				.registerTypeAdapter(Item.class, registryDelegate(Registries.ITEM))
				.registerTypeAdapter(Enchantment.class, registryDelegate(Registries.ENCHANTMENT))
				.registerTypeAdapter(EntityAttribute.class, registryDelegate(Registries.ATTRIBUTE))
				.registerTypeAdapter(Block.class, registryDelegate(Registries.BLOCK))
				.registerTypeAdapter(BlockEntity.class, registryDelegate(Registries.BLOCK_ENTITY_TYPE))
				.registerTypeAdapter(EntityType.class, registryDelegate(Registries.ENTITY_TYPE))
				.registerTypeAdapter(SoundEvent.class, registryDelegate(Registries.SOUND_EVENT))
				.registerTypeAdapter(ScreenHandlerType.class, registryDelegate(Registries.SCREEN_HANDLER))
				.registerTypeAdapter(StatusEffect.class, registryDelegate(Registries.STATUS_EFFECT))
				.registerTypeAdapter(ParticleType.class, registryDelegate(Registries.PARTICLE_TYPE))
				.registerTypeAdapter(EntityPredicate.class, codecDelegate(EntityPredicate.CODEC, lookup))
				.registerTypeAdapterFactory(new WrapperAdapterFactory(new InstancedAdapterWrapper(), new ValidatedAdapterWrapper()))
				.registerTypeAdapterFactory(new PolyAdapterFactory())
				.registerTypeAdapterFactory(new OptionalAdapterFactory())

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
			@Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				D d = context.deserialize(json, delegate);
				return d==null ? null : from.apply(d);
			}
			@Override public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
				return src==null ? JsonNull.INSTANCE : context.serialize(to.apply(src), delegate);
			}
		};
	}

	private static <T> Both<T> registryDelegate(Registry<T> registry) {
		return delegate(Identifier.class, registry::get, registry::getId);
	}

	private static <T> Both<T> codecDelegate(Codec<T> codec, RegistryWrapper.WrapperLookup lookup) {
		return basic(j -> codec.parse(lookup.getOps(JsonOps.INSTANCE), j).result().orElse(null), p -> codec.encodeStart(lookup.getOps(JsonOps.INSTANCE), p).result().orElse(JsonNull.INSTANCE));
	}

	private static interface Both<T> extends JsonDeserializer<T>, JsonSerializer<T> {}
}