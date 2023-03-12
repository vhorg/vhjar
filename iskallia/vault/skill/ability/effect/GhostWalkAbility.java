package iskallia.vault.skill.ability.effect;

import iskallia.vault.gear.attribute.ability.special.GhostWalkDurationModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.GhostWalkConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import iskallia.vault.util.damage.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GhostWalkAbility<C extends GhostWalkConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Ghost Walk";
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      if (player.hasEffect(ModEffects.GHOST_WALK)) {
         return AbilityActionResult.FAIL;
      } else {
         int duration = config.getDurationTicks();

         for (ConfiguredModification<IntValueConfig, GhostWalkDurationModification> mod : SpecialAbilityModification.getModifications(
            player, GhostWalkDurationModification.class
         )) {
            duration = mod.modification().addDuration(mod.config(), duration);
         }

         MobEffectInstance newEffect = new MobEffectInstance(ModEffects.GHOST_WALK, duration, 0, false, false, true);
         player.addEffect(newEffect);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      }
   }

   protected void doSound(C config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.GHOST_WALK_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(ModSounds.GHOST_WALK_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onDamage(LivingDamageEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer && this.doRemoveWhenDealingDamage()) {
         MobEffectInstance ghostWalk = serverPlayer.getEffect(ModEffects.GHOST_WALK);
         if (ghostWalk != null) {
            ServerLevel world = (ServerLevel)serverPlayer.getCommandSenderWorld();
            PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
            AbilityTree abilities = data.getAbilities(serverPlayer);
            AbilityNode<?, ?> node = abilities.getNodeOf(this);
            if (node.getAbility() == this && node.isLearned()) {
               serverPlayer.removeEffect(ModEffects.GHOST_WALK);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onHurt(LivingHurtEvent event) {
      if (this.isInvulnerable(event.getEntityLiving(), event.getSource())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onAttack(LivingAttackEvent event) {
      if (this.isInvulnerable(event.getEntityLiving(), event.getSource())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onTarget(LivingSetAttackTargetEvent event) {
      if (event.getEntityLiving() instanceof Mob mob && this.isInvulnerable(event.getTarget(), null)) {
         mob.setTarget(null);
      }
   }

   private boolean isInvulnerable(@Nullable LivingEntity entity, @Nullable DamageSource source) {
      if (entity instanceof ServerPlayer serverPlayer) {
         MobEffectInstance ghostWalk = serverPlayer.getEffect(ModEffects.GHOST_WALK);
         if (ghostWalk != null && this.preventsDamage() && (source == null || !source.isBypassInvul())) {
            ServerLevel world = (ServerLevel)serverPlayer.getCommandSenderWorld();
            PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
            AbilityTree abilities = data.getAbilities(serverPlayer);
            AbilityNode<?, ?> node = abilities.getNodeOf(this);
            return node.getAbility() == this && node.isLearned();
         }
      }

      return false;
   }

   protected boolean preventsDamage() {
      return true;
   }

   protected boolean doRemoveWhenDealingDamage() {
      return true;
   }

   public static class GhostWalkEffect extends MobEffect {
      private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("d0476bd8-d306-4a40-9229-a0933b830617");

      public GhostWalkEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
         super(typeIn, liquidColorIn);
         this.setRegistryName(id);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return true;
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
         }

         super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
            PlayerAbilitiesData.setAbilityOnCooldown(player, "Ghost Walk");
         }

         super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
      }

      private void removeExistingDamageBuff(ServerPlayer player) {
         PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
         if (existing != null) {
            PlayerDamageHelper.removeMultiplier(player, existing);
         }
      }
   }
}
