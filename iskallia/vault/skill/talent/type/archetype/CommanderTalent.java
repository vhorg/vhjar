package iskallia.vault.skill.talent.type.archetype;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CommanderTalent extends ArchetypeTalent {
   public static final UUID ETERNAL_DAMAGE_INCREASE_MODIFIER = UUID.fromString("f231b599-0de3-42d6-aeb1-775b0be4fae8");
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
         Entity attacker = event.getSource().func_76346_g();
         if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)attacker;
            TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);

            for (TalentNode<CommanderTalent> node : talents.getLearnedNodes(CommanderTalent.class)) {
               CommanderTalent talent = node.getTalent();
               event.setAmount(event.getAmount() * talent.getDamageDealtMultiplier());
            }
         }

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
}
