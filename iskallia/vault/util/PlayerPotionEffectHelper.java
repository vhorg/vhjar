package iskallia.vault.util;

import iskallia.vault.init.ModEffects;
import iskallia.vault.world.data.ServerVaults;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerPotionEffectHelper {
   private static final int IMMUNITY_DURATION = 80;
   private static final Map<UUID, Map<MobEffect, Integer>> PLAYER_IMMUNITIES = new HashMap<>();

   @SubscribeEvent
   public static void onTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         PLAYER_IMMUNITIES.forEach((uuid, immunities) -> {
            for (MobEffect key : new HashSet<>(immunities.keySet())) {
               int timeout = immunities.get(key);
               if (--timeout <= 0) {
                  immunities.remove(key);
               } else {
                  immunities.put(key, timeout);
               }
            }
         });
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onApplyPotionEffect(PotionApplicableEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer sPlayer) {
         if (!sPlayer.level.isClientSide) {
            if (!ServerVaults.get(sPlayer.level).isEmpty()) {
               MobEffect effect = event.getPotionEffect().getEffect();
               if (effect.getCategory() == MobEffectCategory.HARMFUL && effect != ModEffects.BLEED) {
                  UUID uuid = sPlayer.getUUID();
                  Map<MobEffect, Integer> immunities = PLAYER_IMMUNITIES.computeIfAbsent(uuid, k -> new HashMap<>());
                  if (immunities.containsKey(event.getPotionEffect().getEffect())) {
                     event.setResult(Result.DENY);
                  } else {
                     immunities.put(event.getPotionEffect().getEffect(), 80);
                  }
               }
            }
         }
      }
   }
}
