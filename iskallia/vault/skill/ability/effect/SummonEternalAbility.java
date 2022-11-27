package iskallia.vault.skill.ability.effect;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalHelper;
import iskallia.vault.skill.ability.config.SummonEternalConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import iskallia.vault.skill.archetype.archetype.CommanderArchetype;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SummonEternalAbility<C extends SummonEternalConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Summon Eternal";
   }

   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      if (!player.getCommandSenderWorld().isClientSide() && player.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
         EternalsData.EternalGroup playerEternals = EternalsData.get(serverLevel).getEternals(player);
         if (playerEternals.getEternals().isEmpty()) {
            player.sendMessage(new TextComponent("You have no eternals to summon.").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            return false;
         } else if (!ServerVaults.isInVault(player) && config.isVaultOnly()) {
            player.sendMessage(new TextComponent("You can only summon eternals in the Vault!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            return false;
         } else {
            return super.canDoAction(config, player, active);
         }
      } else {
         return false;
      }
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      if (player.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
         EternalsData.EternalGroup var11 = EternalsData.get(serverLevel).getEternals(player);
         ArrayList eternals = new ArrayList();
         int count = config.getNumberOfEternals();

         for (int i = 0; i < count; i++) {
            EternalData eternal = null;
            if (RANDOM.nextFloat() < config.getAncientChance()) {
               eternal = var11.getRandomAliveAncient(
                  RANDOM, eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
               );
            }

            if (eternal == null) {
               eternal = var11.getRandomAlive(
                  RANDOM, eternalDatax -> !eternals.contains(eternalDatax) && !ActiveEternalData.getInstance().isEternalActive(eternalDatax.getId())
               );
            }

            if (eternal != null) {
               eternals.add(eternal);
            }
         }

         if (eternals.isEmpty()) {
            player.sendMessage(new TextComponent("You have no (alive) eternals to summon.").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            return AbilityActionResult.FAIL;
         } else {
            for (EternalData eternalData : eternals) {
               EternalEntity eternalx = EternalHelper.spawnEternal(serverLevel, eternalData);
               eternalx.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
               eternalx.setDespawnTime(serverLevel.getServer().getTickCount() + config.getDespawnTime());
               eternalx.setOwner(player.getUUID());
               eternalx.setEternalId(eternalData.getId());
               eternalx.addEffect(new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0, true, false));
               PlayerArchetypeData.get(serverLevel)
                  .getArchetypeContainer(player)
                  .ifCurrentArchetype(CommanderArchetype.class, archetype -> archetype.applyToEternal(eternal));
               if (eternalData.getAura() != null) {
                  eternalx.setProvidedAura(eternalData.getAura().getAuraName());
               }

               serverLevel.addFreshEntity(eternalx);
            }

            return AbilityActionResult.SUCCESS_COOLDOWN;
         }
      } else {
         return AbilityActionResult.FAIL;
      }
   }

   protected void doParticles(C config, ServerPlayer player) {
   }

   protected void doSound(C config, ServerPlayer player) {
   }

   @SubscribeEvent
   public void onDamage(LivingAttackEvent event) {
      LivingEntity damagedEntity = event.getEntityLiving();
      if (damagedEntity instanceof EternalEntity && event.getSource().getEntity() instanceof Player player && !player.isCreative()) {
         event.setCanceled(true);
      }
   }
}
