package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

public class JewelCuttingButtonElement<E extends JewelCuttingButtonElement<E>> extends ButtonElement<E> {
   private static final Random rand = new Random();

   public JewelCuttingButtonElement(IPosition position, Runnable onClick, VaultJewelCuttingStationContainer container) {
      super(position, ScreenTextures.BUTTON_CRAFT_TEXTURES, onClick);
      this.tooltip(
         Tooltips.multi(
            () -> {
               Player player = Minecraft.getInstance().player;
               if (player == null) {
                  return List.of();
               } else {
                  long window = Minecraft.getInstance().getWindow().getWindow();
                  boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
                  ItemStack inputItem = ItemStack.EMPTY;
                  Slot inputSlot = container.getJewelInputSlot();
                  if (inputSlot != null && !inputSlot.getItem().isEmpty()) {
                     inputItem = inputSlot.getItem();
                  }

                  boolean hasInput = !inputItem.isEmpty();
                  List<Component> tooltip = new ArrayList<>();
                  VaultJewelCuttingConfig.JewelCuttingInput input = container.getTileEntity().getRecipeInput();
                  VaultJewelCuttingConfig.JewelCuttingRange range = container.getTileEntity().getJewelCuttingRange();
                  float chance = container.getTileEntity().getJewelCuttingModifierRemovalChance();
                  int numberOfFreeCuts = 0;

                  for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
                     if (learnedTalentNode.getChild() instanceof JewelExpertise jewelExpertise) {
                        numberOfFreeCuts = jewelExpertise.getNumberOfFreeCuts();
                     }
                  }

                  ItemStack scrap = container.getScrapSlot().getItem();
                  ItemStack bronze = container.getBronzeSlot().getItem();
                  if (hasInput) {
                     VaultGearData data = VaultGearData.read(inputItem);
                     List<VaultGearModifier<?>> prefix = new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
                     List<VaultGearModifier<?>> suffix = new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
                     int affixSize = prefix.size() + suffix.size();
                     VaultGearRarity lowerRarity = VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1);
                     String jewelLowerRarity = "item.the_vault.jewel." + lowerRarity.name().toLowerCase(Locale.ROOT);
                     MutableComponent lowerRarityComponent = new TranslatableComponent(jewelLowerRarity).withStyle(ChatFormatting.YELLOW);
                     VaultGearRarity rarity = VaultJewelCuttingStationTileEntity.getNewRarity(affixSize);
                     String jewelRarity = "item.the_vault.jewel." + rarity.name().toLowerCase(Locale.ROOT);
                     MutableComponent rarityComponent = new TranslatableComponent(jewelRarity).withStyle(ChatFormatting.YELLOW);
                     if (affixSize < 2) {
                        tooltip.add(new TextComponent("Cut the Jewel into a Gemstone"));
                     } else {
                        tooltip.add(
                           new TextComponent("Cut the jewel down in size (" + range.getMin() + "-" + range.getMax() + "), making it ")
                              .append(lowerRarityComponent)
                              .append(new TextComponent("."))
                        );
                        tooltip.add(new TextComponent("This will make it lose a random affix."));
                     }

                     if (numberOfFreeCuts > 0) {
                        tooltip.add(TextComponent.EMPTY);
                        tooltip.add(
                           new TextComponent("")
                              .append(new TextComponent("* ").withStyle(ChatFormatting.GOLD))
                              .append(new TextComponent("Your "))
                              .append(new TextComponent("Jeweler Expertise").withStyle(ChatFormatting.LIGHT_PURPLE))
                              .append(new TextComponent(" gives you "))
                              .append(new TextComponent(String.valueOf(numberOfFreeCuts)).withStyle(ChatFormatting.YELLOW))
                              .append(new TextComponent(" free cut" + (numberOfFreeCuts == 1 ? "" : "s")))
                        );
                        tooltip.add(new TextComponent("retaining its current grade."));
                        int usedFreeCuts = !inputItem.getOrCreateTag().contains("freeCuts") ? 0 : inputItem.getOrCreateTag().getInt("freeCuts");
                        int remaining = numberOfFreeCuts - usedFreeCuts;
                        tooltip.add(
                           new TextComponent("Expertise Cuts: ")
                              .append(this.addTooltipDots(usedFreeCuts, ChatFormatting.YELLOW))
                              .append(this.addTooltipDots(remaining, ChatFormatting.GRAY))
                        );
                     }

                     tooltip.add(TextComponent.EMPTY);
                     tooltip.add(new TextComponent("Cost"));
                     tooltip.add(
                        new TextComponent("- ")
                           .append(input.getMainInput().getHoverName())
                           .append(" x" + input.getMainInput().getCount())
                           .append(" [%s]".formatted(scrap.getCount()))
                           .withStyle(input.getMainInput().getCount() > scrap.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN)
                     );
                     tooltip.add(
                        new TextComponent("- ")
                           .append(input.getSecondInput().getHoverName())
                           .append(" x" + input.getSecondInput().getCount())
                           .append(" [%s]".formatted(bronze.getCount()))
                           .withStyle(input.getSecondInput().getCount() > bronze.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN)
                     );
                     tooltip.add(new TextComponent(""));
                     if (shiftDown) {
                        tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED));
                     } else {
                        tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL));
                     }

                     for (VaultGearAttributeInstance<Integer> sizeAttribute : data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
                        if (sizeAttribute.getValue() <= 10) {
                           tooltip.add(new TextComponent(""));
                           tooltip.add(new TextComponent("Cannot cut size to lower than 10").withStyle(ChatFormatting.RED));
                        }
                     }
                  } else {
                     tooltip.add(new TextComponent("Requires Jewel").withStyle(ChatFormatting.RED));
                  }

                  return tooltip;
               }
            }
         )
      );
   }

   private Component addTooltipDots(int amount, ChatFormatting formatting) {
      return new TextComponent("⬢ ".repeat(Math.max(0, amount))).withStyle(formatting);
   }
}
