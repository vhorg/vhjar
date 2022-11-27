package iskallia.vault.util;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModEffects;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.util.damage.PlayerDamageHelper;
import iskallia.vault.world.data.ServerVaults;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public final class PlayerRageHelper {
   public static final int MAX_RAGE = 100;
   public static final int RAGE_DURATION_TICKS = 10;
   private static final Map<UUID, Integer> LAST_ATTACK_TICK_MAP = new HashMap<>();

   private PlayerRageHelper() {
   }

   public static int getRagePerHit(LivingEntity entity) {
      float result = 0.0F;
      result = CommonEvents.PLAYER_STAT.invoke(PlayerStat.RAGE_PER_HIT, entity, result).getValue();
      return Mth.clamp(Math.round(result), 0, 100);
   }

   public static float getRageDamagePerPoint(LivingEntity entity) {
      float result = 0.0F;
      result = CommonEvents.PLAYER_STAT.invoke(PlayerStat.RAGE_DAMAGE, entity, result).getValue();
      return Math.max(0.0F, result);
   }

   public static int getCurrentRage(LivingEntity entity) {
      MobEffectInstance effect = entity.getEffect(ModEffects.RAGE);
      return effect == null ? 0 : effect.getAmplifier() + 1;
   }

   @SubscribeEvent
   public static void on(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && ServerVaults.isVaultWorld(event.getDimension())) {
         player.removeEffect(ModEffects.RAGE);
      }
   }

   @SubscribeEvent
   public static void on(LivingHurtEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (!entityLiving.getLevel().isClientSide()
         && event.getSource().getEntity() instanceof ServerPlayer player
         && LAST_ATTACK_TICK_MAP.getOrDefault(player.getUUID(), 0) <= player.tickCount - 10) {
         int ragePerHit = getRagePerHit(player);
         if (ragePerHit != 0) {
            MobEffectInstance effect = player.getEffect(ModEffects.RAGE);
            if (effect == null) {
               player.addEffect(new MobEffectInstance(ModEffects.RAGE, ragePerHit * 10, 0, false, false, true));
            } else {
               int amplifier = effect.getAmplifier();
               int newAmplifier = Math.min(99, amplifier + ragePerHit);
               player.addEffect(new MobEffectInstance(ModEffects.RAGE, (newAmplifier + 1) * 10, newAmplifier, false, false, true));
            }

            LAST_ATTACK_TICK_MAP.put(player.getUUID(), player.tickCount);
         }
      }
   }

   @SubscribeEvent
   public static void on(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player) {
         LAST_ATTACK_TICK_MAP.remove(player.getUUID());
      }
   }

   public static class RageEffect extends MobEffect {
      private final UUID uuid;

      public RageEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
         this.uuid = UUID.nameUUIDFromBytes(resourceLocation.toString().getBytes(StandardCharsets.UTF_8));
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return amplifier * 10 >= duration;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         livingEntity.removeEffect(this);
         if (amplifier > 0) {
            livingEntity.addEffect(new MobEffectInstance(this, 10 * amplifier, amplifier - 1, false, false, true));
         }
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            float multiplier = PlayerRageHelper.getRageDamagePerPoint(livingEntity);
            PlayerDamageHelper.removeMultiplier(player, this.uuid);
            PlayerDamageHelper.applyMultiplier(this.uuid, player, (amplifier + 1) * multiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY);
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            PlayerDamageHelper.removeMultiplier(player, this.uuid);
         }
      }
   }
}
