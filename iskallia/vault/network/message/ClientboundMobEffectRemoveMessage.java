package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundMobEffectRemoveMessage(int entityId, MobEffect effect) {
   public static void encode(ClientboundMobEffectRemoveMessage message, FriendlyByteBuf buffer) {
      buffer.writeVarInt(message.entityId);
      buffer.writeVarInt(MobEffect.getId(message.effect));
   }

   public static ClientboundMobEffectRemoveMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundMobEffectRemoveMessage(buffer.readVarInt(), MobEffect.byId(buffer.readVarInt()));
   }

   public static void handle(ClientboundMobEffectRemoveMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ClientLevel level = Minecraft.getInstance().level;
         if (level != null) {
            if (level.getEntity(message.entityId) instanceof LivingEntity livingEntity) {
               livingEntity.removeEffectNoUpdate(message.effect);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
