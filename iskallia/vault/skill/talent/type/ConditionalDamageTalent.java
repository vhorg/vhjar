package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class ConditionalDamageTalent extends PlayerTalent {
   @Expose
   private final ResourceLocation targetEffect;
   @Expose
   private final double damageIncrease;

   public ConditionalDamageTalent(int cost, MobEffect targetEffect, double damageIncrease) {
      super(cost);
      this.targetEffect = targetEffect.getRegistryName();
      this.damageIncrease = damageIncrease;
   }

   public MobEffect getTargetEffect() {
      return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.targetEffect);
   }

   public double getDamageIncrease() {
      return this.damageIncrease;
   }

   @SubscribeEvent
   public static void onAttack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer sPlayer) {
         LivingEntity attacked = event.getEntityLiving();
         float addedMultiplier = 0.0F;
         TalentTree talents = PlayerTalentsData.get(sPlayer.getLevel()).getTalents(sPlayer);

         for (TalentNode<ConditionalDamageTalent> talentNode : talents.getLearnedNodes(ConditionalDamageTalent.class)) {
            ConditionalDamageTalent talent = talentNode.getTalent();
            if (talent.getTargetEffect() != null && attacked.hasEffect(talent.getTargetEffect())) {
               addedMultiplier = (float)(addedMultiplier + talent.getDamageIncrease());
            }
         }

         event.setAmount(event.getAmount() * (1.0F + addedMultiplier));
      }
   }
}
