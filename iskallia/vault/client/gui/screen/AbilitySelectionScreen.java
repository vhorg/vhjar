package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.widget.AbilitySelectionWidget;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityKeyMessage;
import iskallia.vault.skill.ability.AbilityNode;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class AbilitySelectionScreen extends Screen {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/abilities.png");

   public AbilitySelectionScreen() {
      super(new StringTextComponent(""));
   }

   public List<AbilitySelectionWidget> getAbilitiesAsWidgets() {
      List<AbilitySelectionWidget> abilityWidgets = new LinkedList<>();
      Minecraft minecraft = Minecraft.func_71410_x();
      float midX = minecraft.func_228018_at_().func_198107_o() / 2.0F;
      float midY = minecraft.func_228018_at_().func_198087_p() / 2.0F;
      float radius = 60.0F;
      List<AbilityNode<?>> learnedAbilities = AbilitiesOverlay.learnedAbilities;
      double clickableAngle = (Math.PI * 2) / learnedAbilities.size();

      for (int i = 0; i < learnedAbilities.size(); i++) {
         AbilityNode<?> ability = learnedAbilities.get(i);
         double angle = i * ((Math.PI * 2) / learnedAbilities.size()) - (Math.PI / 2);
         double x = radius * Math.cos(angle) + midX;
         double y = radius * Math.sin(angle) + midY;
         AbilitySelectionWidget widget = new AbilitySelectionWidget((int)x, (int)y, ability, clickableAngle / 2.0);
         abilityWidgets.add(widget);
      }

      return abilityWidgets;
   }

   public boolean func_231178_ax__() {
      return false;
   }

   public boolean func_231177_au__() {
      return false;
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      for (AbilitySelectionWidget widget : this.getAbilitiesAsWidgets()) {
         if (widget.func_231047_b_(mouseX, mouseY)) {
            this.requestSwap(widget.getAbilityNode());
            this.func_231175_as__();
            return true;
         }
      }

      this.func_231175_as__();
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public boolean func_223281_a_(int keyCode, int scanCode, int modifiers) {
      if (keyCode != ModKeybinds.abilityWheelKey.getKey().func_197937_c()) {
         return super.func_223281_a_(keyCode, scanCode, modifiers);
      } else {
         Minecraft minecraft = Minecraft.func_71410_x();
         double guiScaleFactor = minecraft.func_228018_at_().func_198100_s();
         double mouseX = minecraft.field_71417_B.func_198024_e() / guiScaleFactor;
         double mouseY = minecraft.field_71417_B.func_198026_f() / guiScaleFactor;

         for (AbilitySelectionWidget widget : this.getAbilitiesAsWidgets()) {
            if (widget.func_231047_b_(mouseX, mouseY)) {
               this.requestSwap(widget.getAbilityNode());
               break;
            }
         }

         this.func_231175_as__();
         return true;
      }
   }

   public void requestSwap(AbilityNode<?> abilityNode) {
      int abilityIndex = AbilitiesOverlay.learnedAbilities.indexOf(abilityNode);
      if (abilityIndex != AbilitiesOverlay.focusedIndex) {
         ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(abilityIndex));
      }
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      Minecraft minecraft = Minecraft.func_71410_x();
      float midX = minecraft.func_228018_at_().func_198107_o() / 2.0F;
      float midY = minecraft.func_228018_at_().func_198087_p() / 2.0F;
      float radius = 60.0F;
      List<AbilitySelectionWidget> abilitiesAsWidgets = this.getAbilitiesAsWidgets();
      boolean focusRendered = false;

      for (int i = 0; i < abilitiesAsWidgets.size(); i++) {
         AbilitySelectionWidget widget = abilitiesAsWidgets.get(i);
         widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
         if (!focusRendered && widget.func_231047_b_(mouseX, mouseY)) {
            String abilityName = widget.getAbilityNode().getName();
            int abilityNameWidth = minecraft.field_71466_p.func_78256_a(abilityName);
            minecraft.field_71466_p.func_238405_a_(matrixStack, abilityName, midX - abilityNameWidth / 2.0F, midY - (radius + 35.0F), 16777215);
            if (i == AbilitiesOverlay.focusedIndex) {
               String text = "Currently Focused Ability";
               int textWidth = minecraft.field_71466_p.func_78256_a(text);
               minecraft.field_71466_p.func_238405_a_(matrixStack, text, midX - textWidth / 2.0F, midY + radius + 15.0F, 11266750);
            }

            focusRendered = true;
         }
      }

      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
   }
}
