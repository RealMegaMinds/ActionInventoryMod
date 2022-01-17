package megaminds.actioninventory.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import megaminds.actioninventory.misc.LevelSetter;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements LevelSetter {
	@Final
	@Shadow
    private CommandOutput output;
	@Final
	@Shadow
    private Vec3d position;
	@Final
	@Shadow
    private Vec2f rotation;
	@Final
	@Shadow
    private ServerWorld world;
	@Final
	@Shadow
	private int level;
	@Final
	@Shadow
    private String name;
	@Final
	@Shadow
    private Text displayName;
	@Final
	@Shadow
    private MinecraftServer server;
	@Final
	@Shadow
    private Entity entity;

	@Unique
	@Override
	public ServerCommandSource withHigherLevel(int level) {
		return new ServerCommandSource(output, position, rotation, world, Math.max(this.level, level), name, displayName, server, entity);
	}
}