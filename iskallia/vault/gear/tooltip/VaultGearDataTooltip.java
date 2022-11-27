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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VaultGearDataTooltip {
   public static List<Component> createTooltip(ItemStack stack, GearTooltip flag) {
      if (!ModConfigs.isInitialized()) {
         return Collections.emptyList();
      } else {
         VaultGearData data = VaultGearData.read(stack);
         List<Component> tooltip = new ArrayList<>();
         VaultGearState state = data.getState();
         tooltip.add(new TextComponent("Level: ").append(new TextComponent(data.getItemLevel() + "").setStyle(Style.EMPTY.withColor(11583738))));
         if (flag.displayCraftingDetail()) {
            addPotentialTooltip(data, stack, tooltip, state);
         }

         if (flag.displayBase()) {
            data.getFirstValue(ModGearAttributes.CRAFTED_BY)
               .ifPresent(
                  crafter -> tooltip.add(new TextComponent("Crafted by: ").append(new TextComponent(crafter).setStyle(Style.EMPTY.withColor(16770048))))
               );
         }

         addRarityTooltip(data, stack, tooltip, state);
         if (state == VaultGearState.IDENTIFIED) {
            if (flag.displayBase()) {
               addModelTooltip(data, stack, tooltip);
            }

            if (flag.displayBase()) {
               addEtchingTooltip(tooltip, data);
            }

            int usedRepairs = data.getUsedRepairSlots();
            int totalRepairs = data.getRepairSlots();
            addRepairTooltip(tooltip, usedRepairs, totalRepairs);
            List<VaultGearModifier<?>> implicits = data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
            if (!implicits.isEmpty()) {
               addAffixTooltip(data, VaultGearModifier.AffixType.IMPLICIT, stack, tooltip, flag.displayModifierDetail());
               tooltip.add(TextComponent.EMPTY);
            }

            int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
            if (prefixes > 0) {
               addAffixTooltip(data, VaultGearModifier.AffixType.PREFIX, stack, tooltip, flag.displayModifierDetail());
               if (flag.displayModifierDetail()) {
                  tooltip.add(TextComponent.EMPTY);
               }
            }

            int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
            if (suffixes > 0) {
               addAffixTooltip(data, VaultGearModifier.AffixType.SUFFIX, stack, tooltip, flag.displayModifierDetail());
            }
         }

         return tooltip;
      }
   }

   private static void addEtchingTooltip(List<Component> tooltip, VaultGearData data) {
      data.getFirstValue(ModGearAttributes.ETCHING).ifPresent(etchingSet -> {
         EtchingConfig.Etching etchingConfig = ModConfigs.ETCHING.getEtchingConfig((EtchingSet<?>)etchingSet);
         if (etchingConfig != null) {
            MutableComponent etchingCmp = new TextComponent(etchingConfig.getName()).withStyle(Style.EMPTY.withColor(etchingConfig.getComponentColor()));
            tooltip.add(new TextComponent("Etching: ").withStyle(ChatFormatting.RED).append(etchingCmp));
         }
      });
   }

   public static void addRepairTooltip(List<Component> tooltip, int usedRepairs, int totalRepairs) {
      int remaining = totalRepairs - usedRepairs;
      tooltip.add(new TextComponent("Repairs: ").append(tooltipDots(usedRepairs, ChatFormatting.YELLOW)).append(tooltipDots(remaining, ChatFormatting.GRAY)));
   }

   private static void addAffixTooltip(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, List<Component> tooltip, boolean displayDetails) {
      List<VaultGearModifier<?>> affixes = data.getModifiers(type);
      VaultGearAttribute<Integer> affixAttribute = type == VaultGearModifier.AffixType.PREFIX ? ModGearAttributes.PREFIXES : ModGearAttributes.SUFFIXES;
      int emptyAffixes = data.getFirstValue(affixAttribute).orElse(0);
      if (displayDetails) {
         tooltip.add(new TextComponent(type.getDisplayName() + ":").withStyle(ChatFormatting.GRAY));
      }

      affixes.forEach(modifier -> modifier.getDisplay(data, type, stack, displayDetails).ifPresent(tooltip::add));
      if (displayDetails && type != VaultGearModifier.AffixType.IMPLICIT) {
         for (int i = 0; i < emptyAffixes - affixes.size(); i++) {
            tooltip.add(emptyAffix(type));
         }
      }
   }

   private static void addRarityTooltip(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
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

   private static void addModelTooltip(VaultGearData data, ItemStack stack, List<Component> tooltip) {
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

   private static void addPotentialTooltip(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      if (state == VaultGearState.IDENTIFIED) {
         int craftingPotential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(0);
         MutableComponent potential = new TextComponent(String.valueOf(craftingPotential));
         if (craftingPotential > 0) {
            potential.withStyle(Style.EMPTY.withColor(16757593));
         } else {
            potential.withStyle(ChatFormatting.RED);
         }

         tooltip.add(new TextComponent("Crafting Potential: ").append(potential));
      }
   }

   private static Component emptyAffix(VaultGearModifier.AffixType type) {
      return new TextComponent("■ empty %s".formatted(type.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY);
   }

   public static Component tooltipDots(int amount, ChatFormatting formatting) {
      return new TextComponent("⬢ ".repeat(Math.max(0, amount))).withStyle(formatting);
   }
}
