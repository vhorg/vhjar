package iskallia.vault.skill.talent.type.archetype;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CommanderTalent extends ArchetypeTalent {
   public static final UUID ETERNAL_DAMAGE_INCREASE_MODIFIER = UUID.fromString("f231b599-0de3-42d6-aeb1-775b0be4fae8");
   private static final Map<UUID, PlayerDamageHelper.DamageMultiplier> multiplierMap = new HashMap<>();
   @Expose
   protected float damageTakenMultiplier;
   @Expose
   protected float damageDealtMultiplier;
   @Expose
   protected float summonEternalAdditionalCooldownReduction;
   @Expose
   protected float summonEternalDamageDealtMultiplier;

   public CommanderTalent(
      int cost,
      float damageTakenMultiplier,
      float damageDealtMultiplier,
      float summonEternalAdditionalCooldownReduction,
      float summonEternalDamageDealtMultiplier
   ) {
      super(cost);
      this.damageTakenMultiplier = damageTakenMultiplier;
      this.damageDealtMultiplier = damageDealtMultiplier;
      this.summonEternalAdditionalCooldownReduction = summonEternalAdditionalCooldownReduction;
      this.summonEternalDamageDealtMultiplier = summonEternalDamageDealtMultiplier;
   }

   public float getDamageTakenMultiplier() {
      return this.damageTakenMultiplier;
   }

   public float getDamageDealtMultiplier() {
      return this.damageDealtMultiplier;
   }

   public float getSummonEternalAdditionalCooldownReduction() {
      return this.summonEternalAdditionalCooldownReduction;
   }

   public float getSummonEternalDamageDealtMultiplier() {
      return this.summonEternalDamageDealtMultiplier;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d()) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)entity;
            TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);

            for (TalentNode<CommanderTalent> node : talents.getLearnedNodes(CommanderTalent.class)) {
               CommanderTalent talent = node.getTalent();
               event.setAmount(event.getAmount() * talent.getDamageTakenMultiplier());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.END && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
         TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
         if (talents.hasLearnedNode(ModConfigs.TALENTS.COMMANDER)) {
            float damageMultiplier = talents.getNodeOf(ModConfigs.TALENTS.COMMANDER).getTalent().getDamageDealtMultiplier();
            PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(sPlayer.func_110124_au());
            if (existing != null) {
               if (existing.getMultiplier() == damageMultiplier) {
                  existing.refreshDuration(sPlayer.func_184102_h());
               } else {
                  PlayerDamageHelper.removeMultiplier(sPlayer, existing);
                  existing = null;
               }
            }

            if (existing == null) {
               existing = PlayerDamageHelper.applyMultiplier(sPlayer, damageMultiplier, PlayerDamageHelper.Operation.STACKING_MULTIPLY, false);
               multiplierMap.put(sPlayer.func_110124_au(), existing);
            }
         } else {
            removeExistingDamageBuff(sPlayer);
         }
      }
   }

   private static void removeExistingDamageBuff(ServerPlayerEntity player) {
      PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(player.func_110124_au());
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }
}
