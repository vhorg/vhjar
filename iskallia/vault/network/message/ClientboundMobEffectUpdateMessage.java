package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundMobEffectUpdateMessage {
   private final int entityId;
   private final MobEffect effect;
   private final byte effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;

   public ClientboundMobEffectUpdateMessage(int entityId, MobEffectInstance effectInstance) {
      this.entityId = entityId;
      this.effect = effectInstance.getEffect();
      this.effectAmplifier = (byte)(effectInstance.getAmplifier() & 0xFF);
      if (effectInstance.getDuration() > 32767) {
         this.effectDurationTicks = 32767;
      } else {
         this.effectDurationTicks = effectInstance.getDuration();
      }

      byte flags = 0;
      if (effectInstance.isAmbient()) {
         flags = (byte)(flags | 1);
      }

      if (effectInstance.isVisible()) {
         flags = (byte)(flags | 2);
      }

      if (effectInstance.showIcon()) {
         flags = (byte)(flags | 4);
      }

      this.flags = flags;
   }

   public ClientboundMobEffectUpdateMessage(int entityId, MobEffect effect, byte effectAmplifier, int effectDurationTicks, byte flags) {
      this.entityId = entityId;
      this.effect = effect;
      this.effectAmplifier = effectAmplifier;
      this.effectDurationTicks = effectDurationTicks;
      this.flags = flags;
   }

   public static void encode(ClientboundMobEffectUpdateMessage message, FriendlyByteBuf buffer) {
      buffer.writeVarInt(message.entityId);
      buffer.writeVarInt(MobEffect.getId(message.effect));
      buffer.writeByte(message.effectAmplifier);
      buffer.writeVarInt(message.effectDurationTicks);
      buffer.writeByte(message.flags);
   }

   public static ClientboundMobEffectUpdateMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundMobEffectUpdateMessage(
         buffer.readVarInt(), MobEffect.byId(buffer.readVarInt()), buffer.readByte(), buffer.readVarInt(), buffer.readByte()
      );
   }

   public boolean isEffectVisible() {
      return (this.flags & 2) == 2;
   }

   public boolean isEffectAmbient() {
      return (this.flags & 1) == 1;
   }

   public boolean effectShowsIcon() {
      return (this.flags & 4) == 4;
   }

   public static void handle(ClientboundMobEffectUpdateMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            if (message.effect != null) {
               ClientLevel level = Minecraft.getInstance().level;
               if (level != null) {
                  if (level.getEntity(message.entityId) instanceof LivingEntity livingEntity) {
                     MobEffectInstance mobeffectinstance = new MobEffectInstance(
                        message.effect,
                        message.effectDurationTicks,
                        message.effectAmplifier,
                        message.isEffectAmbient(),
                        message.isEffectVisible(),
                        message.effectShowsIcon()
                     );
                     mobeffectinstance.setNoCounter(message.effectDurationTicks == 32767);
                     livingEntity.forceAddEffect(mobeffectinstance, null);
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
