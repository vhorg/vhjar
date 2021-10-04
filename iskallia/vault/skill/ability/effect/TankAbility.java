package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.TankConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankAbility<C extends TankConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Tank";
   }

   public boolean onAction(C config, PlayerEntity player, boolean active) {
      if (player.func_70644_a(ModEffects.TANK)) {
         return false;
      } else {
         EffectInstance newEffect = new EffectInstance(
            ModEffects.TANK, config.getDurationTicks(), config.getAmplifier(), false, config.getType().showParticles, config.getType().showIcon
         );
         player.field_70170_p
            .func_184148_a(
               null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.TANK_SFX, SoundCategory.MASTER, 0.175F, 1.0F
            );
         player.func_213823_a(ModSounds.TANK_SFX, SoundCategory.MASTER, 0.175F, 1.0F);
         player.func_195064_c(newEffect);
         return false;
      }
   }

   @SubscribeEvent
   public void onDamage(LivingDamageEvent event) {
      EffectInstance tank = event.getEntityLiving().func_70660_b(ModEffects.TANK);
      if (tank != null) {
         if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getEntityLiving();
            if (!player.func_130014_f_().func_201670_d() && player.func_130014_f_() instanceof ServerWorld) {
               ServerWorld world = (ServerWorld)player.func_130014_f_();
               AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(player);
               AbilityNode<?, ?> tankNode = abilities.getNodeByName("Tank");
               if (tankNode.getAbility() == this && tankNode.isLearned()) {
                  TankConfig cfg = (TankConfig)tankNode.getAbilityConfig();
                  if (cfg != null) {
                     event.setAmount(event.getAmount() * (1.0F - cfg.getDamageReductionPercent()));
                  }
               }
            }
         }
      }
   }
}
