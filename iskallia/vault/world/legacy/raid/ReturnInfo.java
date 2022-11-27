package iskallia.vault.world.legacy.raid;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

public class ReturnInfo implements INBTSerializable<CompoundTag> {
   private Vec3 position;
   private float yaw;
   private float pitch;
   private GameType gamemode;
   private ResourceKey<Level> dimension;

   public ReturnInfo() {
      this(Vec3.ZERO, 0.0F, 0.0F, GameType.DEFAULT_MODE, ServerLevel.OVERWORLD);
   }

   public ReturnInfo(ServerPlayer player) {
      this(player.position(), player.getYRot(), player.getXRot(), player.gameMode.getGameModeForPlayer(), player.level.dimension());
   }

   public ReturnInfo(Vec3 position, float yaw, float pitch, GameType gamemode, ResourceKey<Level> dimension) {
      this.position = position;
      this.yaw = yaw;
      this.pitch = pitch;
      this.gamemode = gamemode;
      this.dimension = dimension;
   }

   public void apply(MinecraftServer server, ServerPlayer player) {
      ServerLevel world = server.getLevel(this.dimension);
      player.teleportTo(world, this.position.x, this.position.y, this.position.z, this.yaw, this.pitch);
      player.setGameMode(this.gamemode);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putDouble("PosX", this.position.x);
      nbt.putDouble("PosY", this.position.y);
      nbt.putDouble("PosZ", this.position.z);
      nbt.putFloat("Yaw", this.yaw);
      nbt.putFloat("Pitch", this.pitch);
      nbt.putInt("Gamemode", this.gamemode.ordinal());
      nbt.putString("Dimension", this.dimension.location().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.position = new Vec3(nbt.getDouble("PosX"), nbt.getDouble("PosY"), nbt.getDouble("PosZ"));
      this.yaw = nbt.getFloat("Yaw");
      this.pitch = nbt.getFloat("Pitch");
      this.gamemode = GameType.byId(nbt.getInt("Gamemode"));
      this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("Dimension")));
   }
}
