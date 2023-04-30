package iskallia.vault.event;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundMobEffectRemoveMessage;
import iskallia.vault.network.message.ClientboundMobEffectUpdateMessage;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public final class MobEffectClientSyncEvent {
   @SubscribeEvent
   public static void on(PotionAddedEvent event) {
      LivingEntity livingEntity = event.getEntityLiving();
      if (!(livingEntity instanceof Player)) {
         MobEffectInstance effectInstance = event.getPotionEffect();
         if (ModEffects.SYNC_TO_CLIENT_ON_MOB.contains(effectInstance.getEffect())) {
            int entityId = livingEntity.getId();
            ModNetwork.CHANNEL
               .send(PacketDistributor.TRACKING_ENTITY.with(event::getEntityLiving), new ClientboundMobEffectUpdateMessage(entityId, effectInstance));
         }
      }
   }

   @SubscribeEvent
   public static void on(PotionRemoveEvent event) {
      syncRemoveEvent(event.getEntityLiving(), event.getPotionEffect());
   }

   @SubscribeEvent
   public static void on(PotionExpiryEvent event) {
      syncRemoveEvent(event.getEntityLiving(), event.getPotionEffect());
   }

   private static void syncRemoveEvent(LivingEntity livingEntity, MobEffectInstance effectInstance) {
      if (!(livingEntity instanceof Player)) {
         if (effectInstance != null && ModEffects.SYNC_TO_CLIENT_ON_MOB.contains(effectInstance.getEffect())) {
            ModNetwork.CHANNEL
               .send(
                  PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity),
                  new ClientboundMobEffectRemoveMessage(livingEntity.getId(), effectInstance.getEffect())
               );
         }
      }
   }

   private MobEffectClientSyncEvent() {
   }
}
