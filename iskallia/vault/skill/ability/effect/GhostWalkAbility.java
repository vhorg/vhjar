package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.GhostWalkConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GhostWalkAbility<C extends GhostWalkConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Ghost Walk";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      if (player.func_70644_a(ModEffects.GHOST_WALK)) {
         return false;
      } else {
         EffectInstance newEffect = new EffectInstance(
            config.getEffect(), config.getDurationTicks(), config.getAmplifier(), false, config.getType().showParticles, config.getType().showIcon
         );
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.GHOST_WALK_SFX,
               SoundCategory.PLAYERS,
               0.2F,
               1.0F
            );
         player.func_213823_a(ModSounds.GHOST_WALK_SFX, SoundCategory.PLAYERS, 0.2F, 1.0F);
         player.func_195064_c(newEffect);
         return false;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onDamage(LivingDamageEvent event) {
      Entity attacker = event.getSource().func_76346_g();
      if (attacker instanceof ServerPlayerEntity && this.doRemoveWhenDealingDamage()) {
         ServerPlayerEntity player = (ServerPlayerEntity)attacker;
         EffectInstance ghostWalk = player.func_70660_b(ModEffects.GHOST_WALK);
         if (ghostWalk != null) {
            ServerWorld world = (ServerWorld)player.func_130014_f_();
            PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
            AbilityTree abilities = data.getAbilities(player);
            AbilityNode<?, ?> node = abilities.getNodeOf(this);
            if (node.getAbility() == this && node.isLearned()) {
               player.func_195063_d(ModEffects.GHOST_WALK);
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
      if (event.getEntityLiving() instanceof MobEntity && this.isInvulnerable(event.getTarget(), null)) {
         ((MobEntity)event.getEntityLiving()).func_70624_b(null);
      }
   }

   private boolean isInvulnerable(@Nullable LivingEntity entity, @Nullable DamageSource source) {
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         EffectInstance ghostWalk = player.func_70660_b(ModEffects.GHOST_WALK);
         if (ghostWalk != null && this.preventsDamage() && (source == null || !source.func_76357_e())) {
            ServerWorld world = (ServerWorld)player.func_130014_f_();
            PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
            AbilityTree abilities = data.getAbilities(player);
            AbilityNode<?, ?> node = abilities.getNodeOf(this);
            if (node.getAbility() == this && node.isLearned()) {
               return true;
            }
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
}
