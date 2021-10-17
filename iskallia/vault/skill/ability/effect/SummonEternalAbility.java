package iskallia.vault.skill.ability.effect;

import iskallia.vault.Vault;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalHelper;
import iskallia.vault.skill.ability.config.SummonEternalConfig;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.archetype.CommanderTalent;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SummonEternalAbility<C extends SummonEternalConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Summon Eternal";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      if (!player.func_130014_f_().func_201670_d() && player.func_130014_f_() instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)player.func_130014_f_();
         EternalsData.EternalGroup playerEternals = EternalsData.get(sWorld).getEternals(player);
         if (playerEternals.getEternals().isEmpty()) {
            player.func_145747_a(new StringTextComponent("You have no eternals to summon.").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
            return false;
         } else if (player.func_130014_f_().func_234923_W_() != Vault.VAULT_KEY && config.isVaultOnly()) {
            player.func_145747_a(new StringTextComponent("You can only summon eternals in the Vault!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
            return false;
         } else {
            List<EternalData> eternals = new ArrayList<>();
            int count = this.getEternalCount(playerEternals, config);

            for (int i = 0; i < count; i++) {
               EternalData eternal = null;
               if (rand.nextFloat() < config.getAncientChance()) {
                  eternal = playerEternals.getRandomAliveAncient(
                     rand, eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
                  );
               }

               if (eternal == null) {
                  eternal = playerEternals.getRandomAlive(
                     rand, eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
                  );
               }

               if (eternal != null) {
                  eternals.add(eternal);
               }
            }

            if (eternals.isEmpty()) {
               player.func_145747_a(new StringTextComponent("You have no (alive) eternals to summon.").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
               return false;
            } else {
               TalentTree talents = PlayerTalentsData.get(sWorld).getTalents(player);
               double damageMultiplier = talents.getLearnedNodes(CommanderTalent.class)
                  .stream()
                  .mapToDouble(node -> node.getTalent().getSummonEternalDamageDealtMultiplier())
                  .max()
                  .orElse(1.0);
               AttributeModifier modifier = new AttributeModifier(
                  CommanderTalent.ETERNAL_DAMAGE_INCREASE_MODIFIER, "CommanderTalent", damageMultiplier, Operation.MULTIPLY_TOTAL
               );

               for (EternalData eternalData : eternals) {
                  EternalEntity eternalx = EternalHelper.spawnEternal(sWorld, eternalData);
                  eternalx.func_70012_b(
                     player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), player.field_70177_z, player.field_70125_A
                  );
                  eternalx.setDespawnTime(sWorld.func_73046_m().func_71259_af() + config.getDespawnTime());
                  eternalx.setOwner(player.func_110124_au());
                  eternalx.setEternalId(eternalData.getId());
                  eternalx.func_110148_a(Attributes.field_233823_f_).func_233769_c_(modifier);
                  eternalx.func_195064_c(new EffectInstance(Effects.field_188423_x, Integer.MAX_VALUE, 0, true, false));
                  this.postProcessEternal(eternalx, config);
                  if (eternalData.getAura() != null) {
                     eternalx.setProvidedAura(eternalData.getAura().getAuraName());
                  }

                  sWorld.func_217376_c(eternalx);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   protected int getEternalCount(EternalsData.EternalGroup eternals, C config) {
      return config.getNumberOfEternals();
   }

   protected void postProcessEternal(EternalEntity eternalEntity, C config) {
   }

   @SubscribeEvent
   public void onDamage(LivingAttackEvent event) {
      LivingEntity damagedEntity = event.getEntityLiving();
      Entity dealerEntity = event.getSource().func_76346_g();
      if (damagedEntity instanceof EternalEntity && dealerEntity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)dealerEntity;
         if (!player.func_184812_l_()) {
            event.setCanceled(true);
         }
      }
   }
}
