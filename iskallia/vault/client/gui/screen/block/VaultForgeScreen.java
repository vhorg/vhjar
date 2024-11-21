package iskallia.vault.client.gui.screen.block;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.client.ClientProficiencyData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.CraftingSelectorElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.LimitedSliderElement;
import iskallia.vault.client.gui.framework.element.ScalableSliderElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.VerticalSegmentedBarElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.block.base.ForgeRecipeContainerScreen;
import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ProficiencyForgeMessage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class VaultForgeScreen extends ForgeRecipeContainerScreen<VaultForgeTileEntity, VaultForgeContainer> {
   private final ScalableSliderElement levelSlider;
   private final VerticalSegmentedBarElement<?, VaultForgeScreen.ProficiencySegment> proficiencyBar;
   private boolean updatingLevelSelection = false;

   public VaultForgeScreen(VaultForgeContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, 206, 206);
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(14, 24), new TextComponent("Level: ").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      int maxCraftable = Math.min(VaultBarOverlay.vaultLevel, ModConfigs.LEVELS_META.getMaxLevel());
      this.levelSlider = this.addElement(
         new LimitedSliderElement(Spatials.positionXY(13, 38).size(129, 6), 0.0F, maxCraftable / 100.0F, this::updateInput)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.setLevelInputVisible(true);
      this.updateInput(maxCraftable / 100.0F);
      this.updateSlider(String.valueOf(maxCraftable));
      this.levelInput.onTextChanged(this::updateSlider);
      ((ButtonElement)((ButtonElement)this.addElement(
               (ButtonElement)new ButtonElement(
                     Spatials.positionXY(145, 6),
                     ScreenTextures.BUTTON_PROFICIENCY__TEXTURES,
                     () -> ModNetwork.CHANNEL.sendToServer(ProficiencyForgeMessage.INSTANCE)
                  )
                  .layout((screen, gui, parent, world) -> world.translateXY(gui))
            ))
            .tooltip(() -> new TextComponent("Add Proficiency").withStyle(ChatFormatting.GRAY)))
         .setDisabled(() -> {
            VaultForgeTileEntity tile = ((VaultForgeContainer)this.getMenu()).getTile();
            return tile == null ? true : tile.getOtherInputInventory().getItem(0).isEmpty();
         });
      this.proficiencyBar = new VerticalSegmentedBarElement<>(
         Spatials.positionXY(169, 6).size(22, 50), () -> ScreenTextures.VAULT_FORGE_PROFICIENCY_BAR, () -> {
            int absProficiency = ClientProficiencyData.getProficiency();
            return VaultGearCraftingConfig.calculateRelativeProficiency(absProficiency, this.getCraftedLevel());
         }
      );
      this.addElement(this.proficiencyBar).layout((screen, gui, parent, world) -> world.translateXY(gui));
      ModConfigs.VAULT_GEAR_CRAFTING_CONFIG
         .getProficiencySteps()
         .forEach(step -> this.proficiencyBar.addSegment(step.getMinProficiency(), new VaultForgeScreen.ProficiencySegment(step)));
   }

   private void updateInput(float sliderValue) {
      if (!this.updatingLevelSelection) {
         this.updatingLevelSelection = true;
         int level = Math.round(sliderValue * 100.0F);
         this.levelInput.setInput(String.valueOf(level));
         this.updatingLevelSelection = false;
      }
   }

   private void updateSlider(String inputValue) {
      try {
         if (this.updatingLevelSelection) {
            return;
         }

         this.updatingLevelSelection = true;
         if (inputValue.isEmpty()) {
            this.levelSlider.setValue(0.0F);
            return;
         }

         try {
            int level = Integer.parseInt(inputValue);
            this.levelSlider.setValue(level / 100.0F);
         } catch (NumberFormatException var6) {
            this.levelSlider.setValue(0.0F);
         }
      } finally {
         this.updatingLevelSelection = false;
      }
   }

   @Override
   protected ISpatial getLevelInputOffset() {
      return Spatials.positionXY(46, 22);
   }

   @Override
   protected void onRecipeSelect(VaultForgeRecipe recipe, boolean canCraft) {
      this.craftButton.setDisabled(!canCraft);
      this.selectedRecipe = recipe;
      this.setLevelInputVisible(true);
   }

   @Override
   protected void addBackgroundElement() {
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(this.getGuiSpatial(), ScreenTextures.VAULT_FORGE_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
   }

   @Nonnull
   @Override
   protected CraftingSelectorElement<?> createCraftingSelector() {
      return this.makeCraftingSelector(ClientDiscoveredEntriesData.Trinkets.getObserverTrinkets());
   }

   public static class ProficiencySegment extends VerticalSegmentedBarElement.BarSegment {
      private final VaultGearCraftingConfig.ProficiencyStep step;

      public ProficiencySegment(VaultGearCraftingConfig.ProficiencyStep step) {
         super(step.getColor());
         this.step = step;
      }

      @Override
      protected List<Component> getTooltipComponents() {
         ChatFormatting potentialStyle = this.step.getCraftingPotentialMultiplier() >= 1.0F
            ? ChatFormatting.GREEN
            : (this.step.getCraftingPotentialMultiplier() >= 0.5F ? ChatFormatting.YELLOW : ChatFormatting.RED);
         Style poolStyle = ModConfigs.VAULT_GEAR_TYPE_CONFIG
            .getRollPool(this.step.getPool())
            .map(VaultGearTypeConfig.RollType::getColor)
            .<Style>map(Style.EMPTY::withColor)
            .orElse(Style.EMPTY.withColor(ChatFormatting.WHITE));
         List<Component> tooltip = new ArrayList<>();
         tooltip.add(
            new TextComponent("Proficiency: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(this.step.getProficiencyName()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.step.getColor()))))
         );
         tooltip.add(
            new TextComponent("Durability: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(this.step.getDurabilityOutcomeName()).withStyle(ChatFormatting.WHITE))
         );
         tooltip.add(
            new TextComponent("Crafting Potential: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(Math.round(this.step.getCraftingPotentialMultiplier() * 100.0F) + "%").withStyle(potentialStyle))
         );
         tooltip.add(new TextComponent("Rarity Pool: ").withStyle(ChatFormatting.GRAY).append(new TextComponent(this.step.getPool()).withStyle(poolStyle)));
         tooltip.add(
            new TextComponent("Maximum Repair Slots: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(String.valueOf(this.step.getMaximumRepairSlots())).withStyle(ChatFormatting.WHITE))
         );
         tooltip.add(
            new TextComponent("Soulbound Chance: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(Math.round(this.step.getSoulboundChance() * 100.0F) + "%").withStyle(ChatFormatting.WHITE))
         );
         if (this.step.getGreaterModifierChance() > 0.0F) {
            tooltip.add(
               new TextComponent("Greater Modifier Chance: ")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(Math.round(this.step.getGreaterModifierChance() * 100.0F) + "%").withStyle(Style.EMPTY.withColor(-10890730)))
            );
         }

         return tooltip;
      }
   }
}
