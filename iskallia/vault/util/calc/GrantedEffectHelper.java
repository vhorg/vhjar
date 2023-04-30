package iskallia.vault.util.calc;

import com.google.common.collect.Sets;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.type.EffectAuraConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.ability.special.EmpowerImmunityModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.NoOpConfig;
import iskallia.vault.gear.attribute.type.EffectAvoidanceSingleMerger;
import iskallia.vault.init.ModEtchings;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class GrantedEffectHelper {
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.START) {
         Player player = event.player;
         if (player instanceof ServerPlayer sPlayer) {
            applyEffects(player, getEffectData(player, sPlayer.getLevel(), (Predicate<MobEffect>)(effect -> !hasImmunity(player, effect))));
         }
      }
   }

   public static void applyEffects(LivingEntity entity, Map<MobEffect, Integer> effects) {
      effects.forEach((effect, amplifier) -> {
         if (amplifier >= 0) {
            MobEffectInstance activeEffect = entity.getEffect(effect);
            MobEffectInstance newEffect = new MobEffectInstance(effect, 339, amplifier, false, false, true);
            if (activeEffect == null || activeEffect.getAmplifier() < amplifier || activeEffect.getDuration() <= 259) {
               entity.addEffect(newEffect);
            }
         }
      });
   }

   public static GrantedEffectHelper.GrantedEffects getEffectData(Player player, ServerLevel world) {
      return getEffectData(player, world, (Predicate<MobEffect>)(effect -> true));
   }

   public static Integer getEffectData(Player player, ServerLevel world, MobEffect effect) {
      Map<MobEffect, Integer> effectData = getEffectData(player, world, (Predicate<MobEffect>)(otherEffect -> otherEffect == effect));
      return effectData.getOrDefault(effect, -1);
   }

   public static GrantedEffectHelper.GrantedEffects getEffectData(Player player, ServerLevel world, MobEffect... effects) {
      Set<MobEffect> filter = Sets.newHashSet(effects);
      return getEffectData(player, world, filter::contains);
   }

   public static GrantedEffectHelper.GrantedEffects getEffectData(Player player, ServerLevel world, Predicate<MobEffect> effectFilter) {
      GrantedEffectHelper.GrantedEffects grantedEffects = new GrantedEffectHelper.GrantedEffects();
      getSnapshotEffectData(player, effectFilter).forEach((effect, realAmplifier) -> grantedEffects.addAmplifier(effect, realAmplifier + 1));
      AuraManager.getInstance()
         .getAurasAffecting(player)
         .stream()
         .filter(aura -> aura.getAura() instanceof EffectAuraConfig)
         .map(aura -> (EffectAuraConfig)aura.getAura())
         .forEach(effectAura -> {
            EffectTalent auraTalent = effectAura.getEffect();
            if (effectFilter.test(auraTalent.getEffect())) {
               grantedEffects.addAmplifier(auraTalent);
            }
         });
      CommonEvents.GRANTED_EFFECT.invoke(grantedEffects, world, player, effectFilter);
      return grantedEffects;
   }

   public static GrantedEffectHelper.GrantedEffects getSnapshotEffectData(LivingEntity entity) {
      return getSnapshotEffectData(entity, effect -> true);
   }

   public static GrantedEffectHelper.GrantedEffects getSnapshotEffectData(LivingEntity entity, Predicate<MobEffect> effectFilter) {
      GrantedEffectHelper.GrantedEffects grantedEffects = new GrantedEffectHelper.GrantedEffects();
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      snapshot.getGrantedPotions().getEffects().forEach((effect, amplifier) -> {
         if (effectFilter.test(effect)) {
            grantedEffects.addAmplifier(effect, amplifier);
         }
      });
      return grantedEffects;
   }

   public static boolean hasImmunity(LivingEntity entity, MobEffect effect) {
      if (!AttributeSnapshotHelper.canHaveSnapshot(entity)) {
         return false;
      } else {
         AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
         if (snapshot.hasEtching(ModEtchings.DIVINITY) && effect.getCategory() == MobEffectCategory.HARMFUL) {
            return true;
         } else {
            return EmpowerAbility.hasEmpowerEffectActive(entity)
                  && !SpecialAbilityModification.<NoOpConfig, EmpowerImmunityModification>getModifications(entity, EmpowerImmunityModification.class).isEmpty()
               ? true
               : snapshot.getImmunities().contains(effect);
         }
      }
   }

   public static Collection<MobEffect> getImmunities(LivingEntity entity) {
      if (!AttributeSnapshotHelper.canHaveSnapshot(entity)) {
         return Collections.emptyList();
      } else {
         AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
         Set<MobEffect> immunities = new HashSet<>(snapshot.getImmunities());
         if (snapshot.hasEtching(ModEtchings.DIVINITY)
            || EmpowerAbility.hasEmpowerEffectActive(entity)
               && !SpecialAbilityModification.<NoOpConfig, EmpowerImmunityModification>getModifications(entity, EmpowerImmunityModification.class).isEmpty()) {
            ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL).forEach(immunities::add);
         }

         return immunities;
      }
   }

   public static boolean canAvoidEffect(MobEffect effect, LivingEntity entity, Random random) {
      if (!AttributeSnapshotHelper.canHaveSnapshot(entity)) {
         return false;
      } else if (hasImmunity(entity, effect)) {
         return true;
      } else {
         AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
         float chance = snapshot.getAttributeValue(ModGearAttributes.EFFECT_AVOIDANCE, EffectAvoidanceSingleMerger.of(effect));
         return chance > random.nextFloat();
      }
   }

   public static class GrantedEffects extends HashMap<MobEffect, Integer> {
      public void addAmplifier(EffectTalent talent) {
         this.addAmplifier(talent.getEffect(), talent.getAmplifier());
      }

      public void addAmplifier(MobEffect effect, int amplifier) {
         int ampl = this.getOrDefault(effect, Integer.valueOf(-1));
         this.put(effect, Integer.valueOf(ampl + amplifier));
      }
   }
}
