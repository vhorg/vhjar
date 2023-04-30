package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.gui.widget.AbilitySelectionWidget;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.network.message.ServerboundAbilitySelectMessage;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class AbilitySelectionScreen extends Screen {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");

   public AbilitySelectionScreen() {
      super(new TextComponent(""));
   }

   public List<AbilitySelectionWidget> getAbilitiesAsWidgets() {
      List<AbilitySelectionWidget> abilityWidgets = new LinkedList<>();
      Minecraft minecraft = Minecraft.getInstance();
      float midX = minecraft.getWindow().getGuiScaledWidth() / 2.0F;
      float midY = minecraft.getWindow().getGuiScaledHeight() / 2.0F;
      float radius = 60.0F;
      List<TieredSkill> learnedAbilities = ClientAbilityData.getLearnedAbilities();
      double clickableAngle = (Math.PI * 2) / learnedAbilities.size();

      for (int i = 0; i < learnedAbilities.size(); i++) {
         TieredSkill ability = learnedAbilities.get(i);
         double angle = i * ((Math.PI * 2) / learnedAbilities.size()) - (Math.PI / 2);
         double x = radius * Math.cos(angle) + midX;
         double y = radius * Math.sin(angle) + midY;
         AbilitySelectionWidget widget = new AbilitySelectionWidget((int)x, (int)y, (SpecializedSkill)ability.getParent(), clickableAngle / 2.0);
         abilityWidgets.add(widget);
      }

      return abilityWidgets;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      for (AbilitySelectionWidget widget : this.getAbilitiesAsWidgets()) {
         if (widget.isMouseOver(mouseX, mouseY)) {
            this.requestSwap(widget.getAbility());
            this.onClose();
            return true;
         }
      }

      this.onClose();
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      if (keyCode != ModKeybinds.abilityWheelKey.getKey().getValue()) {
         return super.keyReleased(keyCode, scanCode, modifiers);
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         double guiScaleFactor = minecraft.getWindow().getGuiScale();
         double mouseX = minecraft.mouseHandler.xpos() / guiScaleFactor;
         double mouseY = minecraft.mouseHandler.ypos() / guiScaleFactor;

         for (AbilitySelectionWidget widget : this.getAbilitiesAsWidgets()) {
            if (widget.isMouseOver(mouseX, mouseY)) {
               this.requestSwap(widget.getAbility());
               break;
            }
         }

         this.onClose();
         return true;
      }
   }

   public void requestSwap(SpecializedSkill ability) {
      if (!ClientAbilityData.isSelectedAbility(ability)) {
         ServerboundAbilitySelectMessage.send(ability.getId());
      }
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      Minecraft minecraft = Minecraft.getInstance();
      float midX = minecraft.getWindow().getGuiScaledWidth() / 2.0F;
      float midY = minecraft.getWindow().getGuiScaledHeight() / 2.0F;
      float radius = 60.0F;
      List<AbilitySelectionWidget> abilitiesAsWidgets = this.getAbilitiesAsWidgets();
      boolean focusRendered = false;

      for (AbilitySelectionWidget widget : abilitiesAsWidgets) {
         widget.render(matrixStack, mouseX, mouseY, partialTicks);
         if (!focusRendered && widget.isMouseOver(mouseX, mouseY)) {
            int yOffset = 35;
            if (widget.getSelectedAbility() != null) {
               yOffset += 10;
            }

            String abilityName = widget.getSelectedAbility().getId();
            int abilityNameWidth = minecraft.font.width(abilityName);
            minecraft.font.drawShadow(matrixStack, abilityName, midX - abilityNameWidth / 2.0F, midY - (radius + yOffset), 16777215);
            String specName = widget.getSelectedAbility().getId();
            int specNameWidth = minecraft.font.width(specName);
            minecraft.font.drawShadow(matrixStack, specName, midX - specNameWidth / 2.0F, midY - (radius + yOffset - 10.0F), ChatFormatting.GOLD.getColor());
            if (ClientAbilityData.isSelectedAbility(widget.getAbility())) {
               String text = "Currently Focused Ability";
               int textWidth = minecraft.font.width(text);
               minecraft.font.drawShadow(matrixStack, text, midX - textWidth / 2.0F, midY + radius + 15.0F, 11266750);
            }

            focusRendered = true;
         }
      }

      super.render(matrixStack, mouseX, mouseY, partialTicks);
   }
}
