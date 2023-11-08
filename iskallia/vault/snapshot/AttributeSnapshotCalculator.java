package iskallia.vault.snapshot;

import iskallia.vault.etching.EtchingSet;
import iskallia.vault.etching.set.EffectSet;
import iskallia.vault.etching.set.GearAttributeSet;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.attribute.type.EffectAvoidanceCombinedMerger;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.charm.CharmHelper;
import iskallia.vault.gear.charm.GearAttributeCharm;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.CuriosGearItem;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.gear.CharmItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.talent.GearAttributeSkill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerEtchingData;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AttributeSnapshotCalculator {
   public static void computeSnapshot(ServerPlayer player, AttributeSnapshot snapshot) {
      addEtchingInformationToSnapshot(PlayerEtchingData.get(player.server).getEtchingSets(player), snapshot);
      addTalentInformationToSnapshot(player, snapshot);
      addExpertiseInformationToSnapshot(player, snapshot);
      computeCuriosSnapshot(player, snapshot);
      int playerLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
      computeGearSnapshot(player::getItemBySlot, player.getCooldowns()::isOnCooldown, playerLevel, snapshot);
   }

   private static void computeCuriosSnapshot(ServerPlayer player, AttributeSnapshot snapshot) {
      TrinketHelper.getTrinkets(IntegrationCurios.getCuriosItemStacks(player), GearAttributeTrinket.class)
         .forEach(
            gearTrinket -> {
               if (gearTrinket.isUsable(player)) {
                  ((GearAttributeTrinket)gearTrinket.trinket())
                     .getAttributes()
                     .forEach(
                        attributeInstance -> snapshot.gearAttributeValues
                           .computeIfAbsent(attributeInstance.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                           .addCachedValue(attributeInstance.getValue())
                     );
               }
            }
         );
      CharmHelper.getCharms(IntegrationCurios.getCuriosItemStacks(player), GearAttributeCharm.class)
         .forEach(
            gearCharm -> {
               if (gearCharm.isUsable(player)) {
                  if (CharmItem.hasValue(gearCharm.stack())) {
                     ((GearAttributeCharm)gearCharm.charm())
                        .getAttributes(CharmItem.getValue(gearCharm.stack()))
                        .forEach(
                           attributeInstance -> snapshot.gearAttributeValues
                              .computeIfAbsent(attributeInstance.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                              .addCachedValue(attributeInstance.getValue())
                        );
                  } else {
                     ((GearAttributeCharm)gearCharm.charm())
                        .getAttributes()
                        .forEach(
                           attributeInstance -> snapshot.gearAttributeValues
                              .computeIfAbsent(attributeInstance.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                              .addCachedValue(attributeInstance.getValue())
                        );
                  }
               }
            }
         );
      IntegrationCurios.getCuriosItemStacks(player)
         .forEach(
            (slot, stacks) -> stacks.forEach(
               stackTpl -> {
                  ItemStack stack = (ItemStack)stackTpl.getA();
                  if (AttributeGearData.hasData(stack)) {
                     if (!(stack.getItem() instanceof CuriosGearItem curiosGearItem && !curiosGearItem.isIntendedSlot(stack, slot))) {
                        if (!stack.is(ModItems.MAGNET) || !MagnetItem.isLegacy(stack)) {
                           AttributeGearData data = AttributeGearData.read(stack);

                           for (VaultGearAttribute<?> attribute : VaultGearAttributeRegistry.getRegistry()) {
                              data.get(attribute, VaultGearAttributeTypeMerger.asList())
                                 .forEach(
                                    value -> snapshot.gearAttributeValues
                                       .computeIfAbsent(attribute, v -> new AttributeSnapshot.AttributeValue())
                                       .addCachedValue(value)
                                 );
                           }
                        }
                     }
                  }
               }
            )
         );
   }

   public static void computeGearSnapshot(
      Function<EquipmentSlot, ItemStack> equipmentFn, Predicate<Item> isItemOnCooldown, int playerLevel, AttributeSnapshot snapshot
   ) {
      List<ItemStack> gear = new ArrayList<>();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = equipmentFn.apply(slot);
         if (!stack.isEmpty() && !isItemOnCooldown.test(stack.getItem())) {
            Item var11 = stack.getItem();
            if (var11 instanceof VaultGearItem) {
               VaultGearItem gearItem = (VaultGearItem)var11;
               if (!gearItem.isIntendedForSlot(stack, slot) || gearItem.isBroken(stack)) {
                  continue;
               }
            }

            var11 = stack.getItem();
            if (var11 instanceof CuriosGearItem) {
               CuriosGearItem gearItem = (CuriosGearItem)var11;
               if (!gearItem.isIntendedSlot(stack, slot)) {
                  continue;
               }
            }

            gear.add(stack);
         }
      }

      gear.forEach(
         stackx -> {
            AttributeGearData data = AttributeGearData.read(stackx);
            if (!(data instanceof VaultGearData vData && vData.getItemLevel() > playerLevel)) {
               for (VaultGearAttribute<?> attribute : VaultGearAttributeRegistry.getRegistry()) {
                  data.get(attribute, VaultGearAttributeTypeMerger.asList())
                     .forEach(
                        value -> snapshot.gearAttributeValues.computeIfAbsent(attribute, v -> new AttributeSnapshot.AttributeValue()).addCachedValue(value)
                     );
               }
            }
         }
      );
      computeAvoidances(snapshot);
   }

   private static void computeAvoidances(AttributeSnapshot snapshot) {
      snapshot.getAttributeValue(ModGearAttributes.EFFECT_AVOIDANCE, EffectAvoidanceCombinedMerger.getInstance())
         .getAvoidanceChances()
         .entrySet()
         .stream()
         .filter(entry -> entry.getValue() >= 1.0F)
         .forEach(
            entry -> snapshot.gearAttributeValues
               .computeIfAbsent(ModGearAttributes.EFFECT_IMMUNITY, v -> new AttributeSnapshot.AttributeValue())
               .addCachedValue(entry.getKey())
         );
   }

   private static void addEtchingInformationToSnapshot(Collection<EtchingSet<?>> etchingSets, AttributeSnapshot snapshot) {
      snapshot.etchings = new ArrayList<>(etchingSets);
      etchingSets.forEach(
         etchingSet -> {
            if (etchingSet instanceof GearAttributeSet attributeSet) {
               attributeSet.getAttributes()
                  .forEach(
                     value -> snapshot.gearAttributeValues
                        .computeIfAbsent(value.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                        .addCachedValue(value.getValue())
                  );
            }

            if (etchingSet instanceof EffectSet effectSet) {
               List<EffectGearAttribute> effectAttributes = effectSet.getGrantedEffects().stream().map(EffectSet.GrantedEffect::asGearAttribute).toList();
               snapshot.gearAttributeValues
                  .computeIfAbsent(ModGearAttributes.EFFECT, v -> new AttributeSnapshot.AttributeValue())
                  .addCachedValues(effectAttributes);
            }
         }
      );
   }

   private static void addTalentInformationToSnapshot(ServerPlayer player, AttributeSnapshot snapshot) {
      TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);
      talents.iterate(
         GearAttributeSkill.class,
         attributeSkill -> {
            if (attributeSkill instanceof Skill skill && skill.isUnlocked()) {
               attributeSkill.getGearAttributes(SkillContext.of(player))
                  .forEach(
                     attributeValue -> snapshot.gearAttributeValues
                        .computeIfAbsent(attributeValue.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                        .addCachedValue(attributeValue.getValue())
                  );
            }
         }
      );
   }

   private static void addExpertiseInformationToSnapshot(ServerPlayer player, AttributeSnapshot snapshot) {
      ExpertiseTree expertise = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
      expertise.iterate(
         GearAttributeSkill.class,
         attributeSkill -> {
            if (attributeSkill instanceof Skill skill && skill.isUnlocked()) {
               attributeSkill.getGearAttributes(SkillContext.ofExpertise(player))
                  .forEach(
                     attributeValue -> snapshot.gearAttributeValues
                        .computeIfAbsent(attributeValue.getAttribute(), v -> new AttributeSnapshot.AttributeValue())
                        .addCachedValue(attributeValue.getValue())
                  );
            }
         }
      );
   }
}
