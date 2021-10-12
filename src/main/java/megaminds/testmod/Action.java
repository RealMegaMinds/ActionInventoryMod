package megaminds.testmod;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * Each itemstack in an inventory has actions.
 * When an itemstack is clicked, each action executed with the argument of the player that clicked it.
 */
public enum Action {
	MESSAGE((player, args)->{
		MutableText msg = Text.Serializer.fromJson(args.getString("message"));	//a Text in json format
		if (args.getBoolean("log")) {	//logs to server only, checked first
			Helper.logMessage(player, msg, true);
			return;
		}

		boolean toP = args.getBoolean("toPlayer");	//to the player only
		boolean fromP = toP ? false : args.getBoolean("fromPlayer");	//as opposed to from server
		boolean broadcast = args.getBoolean("broadcast");	//if true, doesn't need receivers
		
		if (broadcast) {
			Helper.sendMessage(fromP?player:null, msg, player, !fromP, true);
		} else {
			NbtList receivers = args.getList("receivers", NbtElement.STRING_TYPE);	//the people to send the message, formed by uuid.toString
			List<UUID> list = receivers.stream().map(s->UUID.fromString(((NbtString)s).asString())).collect(Collectors.toList());
			list.forEach(n->Helper.sendMessage(player, msg, n, !fromP));
		}
	}),
	COMMAND((player, args)->{
		
	}),
	GIVE((player, args)->{
		
	}),
	OP((player, args)->{
		
	}),
	OPEN_MENU((player, args)->{
		
	}),
	SOUND((player, args)->{
		
	}),
	DISABLED((player, args)->{
		
	});
	
	private NbtCompound arguments;
	private BiConsumer<ServerPlayerEntity, NbtCompound> onExecute;
	
	private Action(BiConsumer<ServerPlayerEntity, NbtCompound> onExecute) {
		arguments = new NbtCompound();
		this.onExecute = onExecute;
	}
	
	public NbtCompound getArguments() {
		return arguments;
	}
	
	public void execute(ServerPlayerEntity entity) {
		onExecute.accept(entity, arguments);
	}
}