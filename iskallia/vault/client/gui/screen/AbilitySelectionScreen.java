package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientAbilityData;
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
import net.minecraft.util.text.TextFormatting;

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
      List<AbilityNode<?, ?>> learnedAbilities = ClientAbilityData.getLearnedAbilityNodes();
      double clickableAngle = (Math.PI * 2) / learnedAbilities.size();

      for (int i = 0; i < learnedAbilities.size(); i++) {
         AbilityNode<?, ?> ability = learnedAbilities.get(i);
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

   public void requestSwap(AbilityNode<?, ?> abilityNode) {
      if (!abilityNode.getGroup().equals(ClientAbilityData.getSelectedAbility())) {
         ModNetwork.CHANNEL.sendToServer(new AbilityKeyMessage(abilityNode.getGroup()));
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

      for (AbilitySelectionWidget widget : abilitiesAsWidgets) {
         widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
         if (!focusRendered && widget.func_231047_b_(mouseX, mouseY)) {
            int yOffset = 35;
            if (widget.getAbilityNode().getSpecialization() != null) {
               yOffset += 10;
            }

            String abilityName = widget.getAbilityNode().getName();
            int abilityNameWidth = minecraft.field_71466_p.func_78256_a(abilityName);
            minecraft.field_71466_p.func_238405_a_(matrixStack, abilityName, midX - abilityNameWidth / 2.0F, midY - (radius + yOffset), 16777215);
            if (widget.getAbilityNode().getSpecialization() != null) {
               String specName = widget.getAbilityNode().getSpecializationName();
               int specNameWidth = minecraft.field_71466_p.func_78256_a(specName);
               minecraft.field_71466_p
                  .func_238405_a_(matrixStack, specName, midX - specNameWidth / 2.0F, midY - (radius + yOffset - 10.0F), TextFormatting.GOLD.func_211163_e());
            }

            if (widget.getAbilityNode().getGroup().equals(ClientAbilityData.getSelectedAbility())) {
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
