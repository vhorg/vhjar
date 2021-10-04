package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.container.inventory.CatalystDecryptionContainer;
import iskallia.vault.item.VaultCatalystItem;
import iskallia.vault.item.catalyst.ModifierRollResult;
import iskallia.vault.item.crystal.VaultCrystalItem;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CatalystDecryptionScreen extends ContainerScreen<CatalystDecryptionContainer> {
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/catalyst-decryption-table.png");

   public CatalystDecryptionScreen(CatalystDecryptionContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_146999_f = 176;
      this.field_147000_g = 234;
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int offsetX = (this.field_230708_k_ - this.field_146999_f) / 2;
      int offsetY = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.func_238474_b_(matrixStack, offsetX, offsetY, 0, 0, this.field_146999_f, this.field_147000_g);
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, 5.0F, 5.0F, 4210752);
      Slot crystalSlot = ((CatalystDecryptionContainer)this.field_147002_h)
         .func_75139_a(((CatalystDecryptionContainer)this.field_147002_h).field_75151_b.size() - 1);
      if (crystalSlot.func_75216_d()) {
         ItemStack crystal = crystalSlot.func_75211_c();
         if (!crystal.func_190926_b() && crystal.func_77973_b() instanceof VaultCrystalItem) {
            for (Slot catalystSlot : ((CatalystDecryptionContainer)this.field_147002_h).getCatalystSlots()) {
               if (catalystSlot.func_75216_d()) {
                  ItemStack catalyst = catalystSlot.func_75211_c();
                  if (!catalyst.func_190926_b() && catalyst.func_77973_b() instanceof VaultCatalystItem) {
                     List<String> modifierOutcomes = VaultCatalystItem.getCrystalCombinationModifiers(catalyst, crystal);
                     if (modifierOutcomes != null && !modifierOutcomes.isEmpty()) {
                        boolean isLeft = catalystSlot.field_75222_d % 2 == 0;
                        List<ITextComponent> results = modifierOutcomes.stream()
                           .map(ModifierRollResult::ofModifier)
                           .map(result -> result.getTooltipDescription("Adds ", false))
                           .flatMap(Collection::stream)
                           .collect(Collectors.toList());
                        RenderSystem.pushMatrix();
                        if (!isLeft) {
                           RenderSystem.translatef(catalystSlot.field_75223_e + 14, catalystSlot.field_75221_f, 0.0F);
                           RenderSystem.scalef(0.65F, 0.65F, 0.65F);
                           this.renderWrappedToolTip(matrixStack, results, 0, 0, this.field_230712_o_);
                        } else {
                           int maxLength = results.stream().mapToInt(txt -> this.field_230712_o_.func_243245_a(txt.func_241878_f())).max().orElse(0);
                           RenderSystem.translatef(catalystSlot.field_75223_e - 14 - maxLength * 0.65F, catalystSlot.field_75221_f, 0.0F);
                           RenderSystem.scalef(0.65F, 0.65F, 0.65F);
                           this.renderWrappedToolTip(matrixStack, results, 0, 0, this.field_230712_o_);
                        }

                        RenderSystem.popMatrix();
                     }
                  }
               }
            }
         }
      }
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public boolean func_231177_au__() {
      return false;
   }
}
