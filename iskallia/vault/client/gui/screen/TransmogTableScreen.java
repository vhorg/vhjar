package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.item.gear.VaultArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TransmogTableScreen extends ContainerScreen<TransmogTableContainer> {
   private static final ResourceLocation GUI_RESOURCE = Vault.id("textures/gui/transmog-table.png");

   public TransmogTableScreen(TransmogTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(""), this.field_238742_p_, this.field_238743_q_, 4210752);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      float midX = this.field_230708_k_ / 2.0F;
      float midY = this.field_230709_l_ / 2.0F;
      Minecraft minecraft = this.getMinecraft();
      int containerWidth = 176;
      int containerHeight = 166;
      minecraft.func_110434_K().func_110577_a(GUI_RESOURCE);
      this.func_238474_b_(matrixStack, (int)(midX - containerWidth / 2), (int)(midY - containerHeight / 2), 0, 0, containerWidth, containerHeight);
      TransmogTableInventory transmogInventory = ((TransmogTableContainer)this.field_147002_h).getInternalInventory();
      ItemStack armorStack = transmogInventory.func_70301_a(0);
      if (transmogInventory.isIngredientSlotsFilled() && !transmogInventory.recipeFulfilled()) {
         this.func_238474_b_(matrixStack, (int)(midX + 15.0F), (int)(midY - 33.0F), 176, 0, 28, 21);
      }

      FontRenderer fontRenderer = minecraft.field_71466_p;
      String title = "Transmogrification";
      fontRenderer.func_238421_b_(matrixStack, title, midX - 35.0F, midY - 70.0F, 4144959);
      String inventoryTitle = "Inventory";
      fontRenderer.func_238421_b_(matrixStack, inventoryTitle, midX - 80.0F, midY - 11.0F, 4144959);
      if (!armorStack.func_190926_b() && armorStack.func_77973_b() instanceof VaultArmorItem) {
         int requiredVaultBronze = transmogInventory.requiredVaultBronze();
         if (requiredVaultBronze != -1) {
            fontRenderer.func_238421_b_(matrixStack, "x", midX - 9.0F, midY - 45.0F, 9145227);
            fontRenderer.func_238421_b_(matrixStack, String.valueOf(requiredVaultBronze), midX - 2.0F, midY - 45.0F, 9145227);
         }
      }

      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }
}
