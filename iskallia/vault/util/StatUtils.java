package iskallia.vault.util;

import iskallia.vault.client.ClientDamageData;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StatUtils {
   public static double getAverageDps(Player player) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
      double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
      if (player.getLevel().isClientSide()) {
         attackDamage = modifyWeaponDamage(player, attackDamage);
         MobEffectInstance inst = player.getEffect(MobEffects.DAMAGE_BOOST);
         if (inst != null) {
            attackDamage += 3 * (inst.getAmplifier() + 1);
         }
      }

      float chance = snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      double damage = attackDamage * snapshot.getAttributeValue(ModGearAttributes.FATAL_STRIKE_DAMAGE, VaultGearAttributeTypeMerger.floatSum()).floatValue();
      attackDamage += attackDamage * chance * damage;
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

   public static double modifyWeaponDamage(LivingEntity entity, double damage) {
      ItemStack stack = entity.getMainHandItem();
      if (stack.getItem() instanceof VaultGearItem && entity instanceof Player player) {
         int playerLevel = SidedHelper.getVaultLevel(player);
         if (VaultGearData.read(stack).getItemLevel() > playerLevel) {
            return damage;
         }
      }

      Collection<AttributeModifier> modifiers = entity.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE);
      if (modifiers.isEmpty()) {
         return damage;
      } else {
         Map<Operation, List<AttributeModifier>> grouped = new HashMap<>();

         for (AttributeModifier modifier : modifiers) {
            grouped.computeIfAbsent(modifier.getOperation(), op -> new ArrayList<>()).add(modifier);
         }

         for (AttributeModifier mod : grouped.getOrDefault(Operation.ADDITION, Collections.emptyList())) {
            damage += mod.getAmount();
         }

         double d1 = damage;

         for (AttributeModifier mod : grouped.getOrDefault(Operation.MULTIPLY_BASE, Collections.emptyList())) {
            d1 += damage * mod.getAmount();
         }

         for (AttributeModifier mod : grouped.getOrDefault(Operation.MULTIPLY_TOTAL, Collections.emptyList())) {
            d1 *= 1.0 + mod.getAmount();
         }

         return d1;
      }
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
