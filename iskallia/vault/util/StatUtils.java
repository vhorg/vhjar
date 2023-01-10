package iskallia.vault.util;

import iskallia.vault.client.ClientDamageData;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.util.damage.PlayerDamageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class StatUtils {
   public static double getAverageDps(Player player) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
      double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
      if (player.getLevel().isClientSide()) {
         MobEffectInstance inst = player.getEffect(MobEffects.DAMAGE_BOOST);
         if (inst != null) {
            attackDamage += 3 * (inst.getAmplifier() + 1);
         }
      }

      float chance = snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      double damage = attackDamage * snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_DAMAGE, VaultGearAttributeTypeMerger.floatSum()).floatValue();
      attackDamage = attackDamage * (1.0F - chance) + damage * chance;
      float dmgIncrease = snapshot.getAttributeValue(ModGearAttributes.DAMAGE_INCREASE, VaultGearAttributeTypeMerger.floatSum());
      attackDamage *= 1.0F + dmgIncrease;
      float dynamicDmgMultiplier;
      if (player instanceof ServerPlayer) {
         dynamicDmgMultiplier = PlayerDamageHelper.getDamageMultiplier(player, true);
      } else {
         dynamicDmgMultiplier = ClientDamageData.getCurrentDamageMultiplier();
      }

      attackDamage *= dynamicDmgMultiplier;
      return attackDamage * attackSpeed;
   }

   public static double getDefence(Player player) {
      int armor = player.getArmorValue();
      float resistance = ResistanceHelper.getResistance(player);
      float blockChance = BlockChanceHelper.getBlockChance(player);
      double dmgReduction = getArmorMultiplier(armor);
      dmgReduction *= 1.0F - resistance;
      dmgReduction *= 1.0F - blockChance;
      return 1.0 - dmgReduction;
   }

   public static float getArmorMultiplier(float armor) {
      return 1.0F - 1.0F / ((float)Math.pow(40.0F / armor, 2.0) + 1.0F);
   }
}
