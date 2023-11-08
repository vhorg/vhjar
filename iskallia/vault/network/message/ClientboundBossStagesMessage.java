package iskallia.vault.network.message;

import iskallia.vault.entity.boss.ArtifactBossEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundBossStagesMessage {
   private final int entityId;
   private final List<CompoundTag> stagesNbt;

   public ClientboundBossStagesMessage(int entityId, List<CompoundTag> stagesNbt) {
      this.entityId = entityId;
      this.stagesNbt = stagesNbt;
   }

   public static void encode(ClientboundBossStagesMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.entityId);
      buffer.writeCollection(message.stagesNbt, FriendlyByteBuf::writeNbt);
   }

   public static ClientboundBossStagesMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundBossStagesMessage(buffer.readInt(), (List<CompoundTag>)buffer.readCollection(ArrayList::new, FriendlyByteBuf::readNbt));
   }

   public static void handle(ClientboundBossStagesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updateStages(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateStages(ClientboundBossStagesMessage message) {
      Level level = Minecraft.getInstance().level;
      if (level != null && level.getEntity(message.entityId) instanceof ArtifactBossEntity artifactBossEntity) {
         artifactBossEntity.setStagesFromNbt(message.stagesNbt);
      }
   }
}
