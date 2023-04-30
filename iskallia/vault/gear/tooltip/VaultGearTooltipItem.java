package iskallia.vault.gear.tooltip;

import iskallia.vault.config.EtchingConfig;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.BottleItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public interface VaultGearTooltipItem {
   default List<Component> createTooltip(ItemStack stack, GearTooltip flag) {
      if (!ModConfigs.isInitialized()) {
         return Collections.emptyList();
      } else {
         VaultGearData data = VaultGearData.read(stack);
         List<Component> tooltip = new ArrayList<>();
         VaultGearState state = data.getState();
         this.addTooltipItemLevel(data, stack, tooltip, state);
         if (flag.displayCraftingDetail()) {
            this.addTooltipCraftingPotential(data, stack, tooltip, state);
            this.addTooltipToolCapacity(data, stack, tooltip, state);
         }

         if (flag.displayBase()) {
            data.getFirstValue(ModGearAttributes.CRAFTED_BY)
               .ifPresent(
                  crafter -> tooltip.add(new TextComponent("Crafted by: ").append(new TextComponent(crafter).setStyle(Style.EMPTY.withColor(16770048))))
               );
         }

         this.addTooltipRarity(data, stack, tooltip, state);
         if (state == VaultGearState.IDENTIFIED) {
            if (flag.displayBase()) {
               this.addTooltipGearModel(data, stack, tooltip);
            }

            if (flag.displayBase()) {
               this.addTooltipEtchingSet(tooltip, data);
            }

            int usedRepairs = data.getUsedRepairSlots();
            int totalRepairs = data.getRepairSlots();
            this.addRepairTooltip(tooltip, usedRepairs, totalRepairs);
            this.addTooltipDurability(tooltip, stack);
            List<VaultGearModifier<?>> implicits = data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
            if (!implicits.isEmpty()) {
               this.addTooltipAffixGroup(data, VaultGearModifier.AffixType.IMPLICIT, stack, tooltip, flag.displayModifierDetail());
               tooltip.add(TextComponent.EMPTY);
            }

            int maxPrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
            List<VaultGearModifier<?>> prefixes = data.getModifiers(VaultGearModifier.AffixType.PREFIX);
            int maxSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
            List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);
            if (maxPrefixes > 0 || !prefixes.isEmpty()) {
               this.addTooltipAffixGroup(data, VaultGearModifier.AffixType.PREFIX, stack, tooltip, flag.displayModifierDetail());
               if (flag.displayModifierDetail() && (maxSuffixes > 0 || !suffixes.isEmpty())) {
                  tooltip.add(TextComponent.EMPTY);
               }
            }

            if (maxSuffixes > 0 || !suffixes.isEmpty() || this instanceof BottleItem) {
               this.addTooltipAffixGroup(data, VaultGearModifier.AffixType.SUFFIX, stack, tooltip, flag.displayModifierDetail());
            }
         }

         return tooltip;
      }
   }

   default void addTooltipAffixGroup(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, List<Component> tooltip, boolean displayDetails) {
      List<VaultGearModifier<?>> affixes = data.getModifiers(type);
      VaultGearAttribute<Integer> affixAttribute = type == VaultGearModifier.AffixType.PREFIX ? ModGearAttributes.PREFIXES : ModGearAttributes.SUFFIXES;
      int emptyAffixes = data.getFirstValue(affixAttribute).orElse(0);
      if (displayDetails) {
         tooltip.add(new TextComponent(type.getPlural() + ":").withStyle(ChatFormatting.GRAY));
      }

      affixes.forEach(modifier -> modifier.getDisplay(data, type, stack, displayDetails).ifPresent(tooltip::add));
      if (displayDetails && type != VaultGearModifier.AffixType.IMPLICIT) {
         for (int i = 0; i < emptyAffixes - affixes.size(); i++) {
            tooltip.add(this.addTooltipEmptyAffixes(type));
         }
      }
   }

   default void addRepairTooltip(List<Component> tooltip, int usedRepairs, int totalRepairs) {
      if (totalRepairs > 0) {
         int remaining = totalRepairs - usedRepairs;
         tooltip.add(
            new TextComponent("Repairs: ")
               .append(this.addTooltipDots(usedRepairs, ChatFormatting.YELLOW))
               .append(this.addTooltipDots(remaining, ChatFormatting.GRAY))
         );
      }
   }

   default void addTooltipEtchingSet(List<Component> tooltip, VaultGearData data) {
      data.getFirstValue(ModGearAttributes.ETCHING).ifPresent(etchingSet -> {
         EtchingConfig.Etching etchingConfig = ModConfigs.ETCHING.getEtchingConfig((EtchingSet<?>)etchingSet);
         if (etchingConfig != null) {
            MutableComponent etchingCmp = new TextComponent(etchingConfig.getName()).withStyle(Style.EMPTY.withColor(etchingConfig.getComponentColor()));
            tooltip.add(new TextComponent("Etching: ").withStyle(ChatFormatting.RED).append(etchingCmp));
         }
      });
   }

   default void addTooltipGearModel(VaultGearData data, ItemStack stack, List<Component> tooltip) {
      data.getFirstValue(ModGearAttributes.GEAR_MODEL)
         .flatMap(modelId -> ModDynamicModels.REGISTRIES.getModel(stack.getItem(), modelId))
         .ifPresent(
            gearModel -> {
               if (stack.getItem() instanceof VaultGearItem gearItem) {
                  String name = gearModel.getDisplayName();
                  if (gearModel instanceof ArmorPieceModel modelPiece) {
                     name = modelPiece.getArmorModel().getDisplayName();
                  }

                  VaultGearRarity rollRarity = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(gearItem, gearModel.getId());
                  MutableComponent modelTooltip = new TextComponent("Model: ")
                     .withStyle(new ChatFormatting[0])
                     .append(new TextComponent(name).withStyle(Style.EMPTY.withColor(rollRarity.getColor().getValue())));
                  tooltip.add(modelTooltip);
               }
            }
         );
   }

   default void addTooltipRarity(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      switch (state) {
         case UNIDENTIFIED:
            data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE)
               .flatMap(ModConfigs.VAULT_GEAR_TYPE_CONFIG::getRollPool)
               .ifPresent(
                  pool -> tooltip.add(
                     new TextComponent("Roll: ").append(new TextComponent(pool.getName()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(pool.getColor()))))
                  )
               );
            break;
         case IDENTIFIED:
            MutableComponent rarityText = new TextComponent("Rarity: ")
               .append(data.getRarity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(data.getRarity().getColor())));
            tooltip.add(rarityText);
      }
   }

   default void addTooltipItemLevel(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      tooltip.add(new TextComponent("Level: ").append(new TextComponent(data.getItemLevel() + "").setStyle(Style.EMPTY.withColor(11583738))));
   }

   default void addTooltipCraftingPotential(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      if (state == VaultGearState.IDENTIFIED) {
         data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).ifPresent(craftingPotential -> {
            MutableComponent potential = new TextComponent(String.valueOf(craftingPotential));
            if (craftingPotential > 0) {
               potential.withStyle(Style.EMPTY.withColor(16757593));
            } else {
               potential.withStyle(ChatFormatting.RED);
            }

            tooltip.add(new TextComponent("Crafting Potential: ").append(potential));
         });
      }
   }

   default void addTooltipToolCapacity(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      if (state == VaultGearState.IDENTIFIED) {
         data.getFirstValue(ModGearAttributes.TOOL_CAPACITY).ifPresent(craftingPotential -> {
            MutableComponent potential = new TextComponent(String.valueOf(craftingPotential));
            if (craftingPotential > 0) {
               potential.withStyle(Style.EMPTY.withColor(16757593));
            } else {
               potential.withStyle(ChatFormatting.RED);
            }

            tooltip.add(new TextComponent("Capacity: ").append(potential));
         });
      }
   }

   default Component addTooltipEmptyAffixes(VaultGearModifier.AffixType type) {
      return new TextComponent("■ empty %s".formatted(type.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY);
   }

   default Component addTooltipDots(int amount, ChatFormatting formatting) {
      return new TextComponent("⬢ ".repeat(Math.max(0, amount))).withStyle(formatting);
   }

   default void addTooltipDurability(List<Component> tooltip, ItemStack stack) {
      if (stack.isDamageableItem() && stack.getMaxDamage() > 0) {
         tooltip.add(
            new TextComponent("Durability: ")
               .append(new TextComponent("%d/%d".formatted(stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage())).withStyle(ChatFormatting.GRAY))
         );
      }
   }
}
