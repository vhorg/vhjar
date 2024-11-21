package iskallia.vault.gear.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.config.EtchingConfig;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface VaultGearTooltipItem {
   default List<Component> createTooltip(ItemStack stack, GearTooltip flag) {
      if (!ModConfigs.isInitialized()) {
         return Collections.emptyList();
      } else {
         long window = Minecraft.getInstance().getWindow().getWindow();
         boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
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

         if (shiftDown || state == VaultGearState.UNIDENTIFIED) {
            this.addTooltipRarity(data, stack, tooltip, state);
         }

         if (state == VaultGearState.IDENTIFIED) {
            if (flag.displayBase()) {
               this.addTooltipEtchingSet(tooltip, data);
            }

            if (stack.getItem() instanceof VaultGearItem gearItem) {
               EquipmentSlot slot = gearItem.getGearType(stack).getEquipmentSlot();
               if (slot != null) {
                  this.addSlotTooltip(tooltip, slot);
               }
            }

            int usedRepairs = data.getUsedRepairSlots();
            int totalRepairs = data.getRepairSlots();
            this.addRepairTooltip(tooltip, usedRepairs, totalRepairs);
            this.addTooltipDurability(tooltip, stack);
            if (shiftDown && flag.displayBase()) {
               this.addTooltipGearModel(data, stack, tooltip);
            }

            this.addTooltipImportantBaseAttributes(data, stack, tooltip, flag.displayModifierDetail());
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

            if (maxSuffixes > 0 || !suffixes.isEmpty()) {
               this.addTooltipAffixGroup(data, VaultGearModifier.AffixType.SUFFIX, stack, tooltip, flag.displayModifierDetail());
               if (!data.isModifiable()) {
                  tooltip.add(TextComponent.EMPTY);
               }
            }
         }

         if (!data.isModifiable()) {
            MutableComponent ct = new TextComponent("").append(new TextComponent("Corrupted").setStyle(Style.EMPTY.withColor(11337728)));
            if (flag.displayModifierDetail()) {
               ct.append(new TextComponent(" (Unmodifiable Item)").withStyle(ChatFormatting.GRAY));
            }

            tooltip.add(ct);
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

   default void addSlotTooltip(List<Component> tooltip, EquipmentSlot slot) {
      tooltip.add(
         new TextComponent("Slot: ").append(new TranslatableComponent("the_vault.equipment." + slot.getName()).withStyle(Style.EMPTY.withColor(14869130)))
      );
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

   default void addTooltipImportantBaseAttributes(VaultGearData data, ItemStack stack, List<Component> tooltip, boolean displayDetail) {
      int txtLength = tooltip.size();
      data.getAttributes(ModGearAttributes.DURABILITY)
         .filter(inst -> inst.getValue() > 0)
         .forEach(attr -> attr.getDisplay(data, VaultGearModifier.AffixType.IMPLICIT, stack, displayDetail).ifPresent(tooltip::add));
      data.getAttributes(ModGearAttributes.SOULBOUND)
         .filter(VaultGearAttributeInstance::getValue)
         .forEach(attr -> attr.getDisplay(data, VaultGearModifier.AffixType.IMPLICIT, stack, displayDetail).ifPresent(tooltip::add));
      if (tooltip.size() > txtLength && displayDetail) {
         tooltip.add(TextComponent.EMPTY);
      }
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

                  VaultGearRarity rollRarity = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(stack, gearModel.getId());
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
      if (VaultBarOverlay.vaultLevel >= data.getItemLevel()) {
         tooltip.add(new TextComponent("Level: ").append(new TextComponent(String.valueOf(data.getItemLevel())).setStyle(Style.EMPTY.withColor(11583738))));
      } else {
         tooltip.add(
            new TextComponent("Requires Level: ")
               .withStyle(ChatFormatting.RED)
               .append(new TextComponent(String.valueOf(data.getItemLevel())).withStyle(ChatFormatting.RED))
         );
      }
   }

   default void addTooltipCraftingPotential(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
      if (state == VaultGearState.IDENTIFIED) {
         data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL)
            .ifPresent(
               craftingPotential -> {
                  MutableComponent potential = new TextComponent(String.valueOf(craftingPotential));
                  int maxPotential = 0;
                  int color = Color.HSBtoRGB(0.0F, 1.0F, 0.66F);
                  if (data.hasAttribute(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
                     maxPotential = data.getFirstValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL).orElse(0);
                     if (maxPotential > 0) {
                        float percentage = Math.max((float)craftingPotential.intValue() / maxPotential, 0.0F);
                        color = Color.HSBtoRGB(0.33F * percentage, 1.0F, 0.66F);
                     }
                  }

                  if (craftingPotential > 0) {
                     potential.withStyle(Style.EMPTY.withColor(color));
                  } else {
                     potential.withStyle(Style.EMPTY.withColor(color));
                  }

                  MutableComponent txt = new TextComponent("")
                     .withStyle(ChatFormatting.GRAY)
                     .append(new TextComponent("Crafting Potential: ").withStyle(ChatFormatting.WHITE))
                     .append(potential);
                  if (maxPotential > 0) {
                     txt.append("/").append(String.valueOf(maxPotential));
                  }

                  tooltip.add(txt);
               }
            );
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
      if (VaultGearItem.<VaultGearItem>of(stack).isBroken(stack)) {
         tooltip.add(new TextComponent("Durability: ").append(new TextComponent("BROKEN").withStyle(ChatFormatting.RED)));
      } else if (stack.isDamageableItem() && stack.getMaxDamage() > 0) {
         tooltip.add(
            new TextComponent("Durability: ")
               .append(new TextComponent("%d/%d".formatted(stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage())).withStyle(ChatFormatting.GRAY))
         );
      }
   }
}
