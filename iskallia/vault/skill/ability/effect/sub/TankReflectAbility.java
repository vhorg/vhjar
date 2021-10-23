package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.TankReflectConfig;
import iskallia.vault.skill.ability.effect.TankAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankReflectAbility extends TankAbility<TankReflectConfig> {
   @SubscribeEvent
   @Override
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
                  TankReflectConfig config = (TankReflectConfig)tankNode.getAbilityConfig();
                  Entity attacker = event.getSource().func_76346_g();
                  if (attacker instanceof LivingEntity
                     && ((LivingEntity)attacker).func_110143_aJ() > 0.0F
                     && rand.nextFloat() < config.getDamageReflectChance()) {
                     float damage = event.getAmount() * config.getDamageReflectPercent();
                     attacker.func_70097_a(DamageSource.func_92087_a(player), damage);
                  }
               }
            }
         }
      }
   }
}
