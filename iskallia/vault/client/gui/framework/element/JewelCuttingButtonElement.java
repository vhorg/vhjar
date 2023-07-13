package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
                  float modifiedChance = 0.0F;

                  for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
                     if (learnedTalentNode.getChild() instanceof JewelExpertise jewelExpertise) {
                        modifiedChance = jewelExpertise.getModifierChanceReduction();
                     }
                  }

                  ItemStack scrap = container.getScrapSlot().getItem();
                  ItemStack bronze = container.getBronzeSlot().getItem();
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
                  tooltip.add(new TextComponent("Removes"));
                  tooltip.add(
                     new TextComponent("- ").append("Size: ").append(range.getMin() + " - ").append(range.getMax() + "").withStyle(ChatFormatting.GRAY)
                  );
                  if (shiftDown && modifiedChance != 0.0F) {
                     tooltip.add(
                        new TextComponent("- ")
                           .append("Modifier Chance: ")
                           .append("%.0f%%".formatted(chance * 100.0F))
                           .append(" (-" + "%.0f%%".formatted(modifiedChance * 100.0F) + ")")
                           .withStyle(ChatFormatting.GRAY)
                     );
                  } else {
                     tooltip.add(
                        new TextComponent("- ")
                           .append("Failure Chance: ")
                           .append("%.0f%%".formatted((chance - modifiedChance) * 100.0F))
                           .withStyle(ChatFormatting.GRAY)
                     );
                  }

                  if (hasInput) {
                     tooltip.add(new TextComponent(""));
                     if (shiftDown) {
                        tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED));
                     } else {
                        tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL));
                     }

                     VaultGearData data = VaultGearData.read(inputItem);

                     for (VaultGearAttributeInstance<Integer> sizeAttribute : data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
                        if (sizeAttribute.getValue() <= 10) {
                           tooltip.add(new TextComponent(""));
                           tooltip.add(new TextComponent("Cannot cut size to lower than 10").withStyle(ChatFormatting.RED));
                        }
                     }
                  } else {
                     tooltip.add(new TextComponent(""));
                     tooltip.add(new TextComponent("Requires Jewel").withStyle(ChatFormatting.RED));
                  }

                  return tooltip;
               }
            }
         )
      );
   }
}
