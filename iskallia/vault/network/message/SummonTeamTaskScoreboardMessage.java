package iskallia.vault.network.message;

import iskallia.vault.entity.entity.TeamTaskScoreboardEntity;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class SummonTeamTaskScoreboardMessage {
   private final int id;
   private final UUID uuid;
   private final BlockPos pos;
   private final Direction direction;
   private final int width;
   private final int height;
   private final Vec3 position;

   public SummonTeamTaskScoreboardMessage(TeamTaskScoreboardEntity entity) {
      this.id = entity.getId();
      this.uuid = entity.getUUID();
      this.pos = entity.getPos();
      this.direction = entity.getDirection();
      this.width = entity.getWidth();
      this.height = entity.getHeight();
      this.position = entity.position();
   }

   public SummonTeamTaskScoreboardMessage(FriendlyByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.uuid = buffer.readUUID();
      this.pos = buffer.readBlockPos();
      this.direction = (Direction)buffer.readEnum(Direction.class);
      this.width = buffer.readVarInt();
      this.height = buffer.readVarInt();
      this.position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
   }

   public static void encode(SummonTeamTaskScoreboardMessage message, FriendlyByteBuf buffer) {
      buffer.writeVarInt(message.id);
      buffer.writeUUID(message.uuid);
      buffer.writeBlockPos(message.pos);
      buffer.writeEnum(message.direction);
      buffer.writeVarInt(message.width);
      buffer.writeVarInt(message.height);
      buffer.writeDouble(message.position.x);
      buffer.writeDouble(message.position.y);
      buffer.writeDouble(message.position.z);
   }

   public static SummonTeamTaskScoreboardMessage decode(FriendlyByteBuf buffer) {
      return new SummonTeamTaskScoreboardMessage(buffer);
   }

   public static void handle(SummonTeamTaskScoreboardMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> spawnTeamTaskScoreboard(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void spawnTeamTaskScoreboard(SummonTeamTaskScoreboardMessage message) {
      ClientLevel world = Minecraft.getInstance().level;
      if (world != null) {
         Entity entity = new TeamTaskScoreboardEntity(world, message.pos, message.position, message.direction, message.width, message.height);
         entity.setPacketCoordinates(message.pos.getX(), message.pos.getY(), message.pos.getZ());
         entity.setId(message.id);
         entity.setUUID(message.uuid);
         world.putNonPlayerEntity(message.id, entity);
      }
   }
}
