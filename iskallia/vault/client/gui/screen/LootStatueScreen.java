package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.StatueOptionSlot;
import iskallia.vault.container.LootStatueContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.OmegaStatueUIMessage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class LootStatueScreen extends AbstractContainerScreen<LootStatueContainer> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/omega_statue_options.png");
   private final List<StatueOptionSlot> slots = new ArrayList<>();
   List<ItemStack> items = new ArrayList<>();
   BlockPos statuePos;
   private int selected = 0;

   public LootStatueScreen(LootStatueContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.font = Minecraft.getInstance().font;
      this.imageWidth = 176;
      this.imageHeight = 84;
      this.titleLabelX = 88;
      this.titleLabelY = 7;

      for (Tag nbt : screenContainer.getItemsCompound()) {
         CompoundTag itemNbt = (CompoundTag)nbt;
         this.items.add(ItemStack.of(itemNbt));
      }

      this.statuePos = NbtUtils.readBlockPos(screenContainer.getBlockPos());
      int x = 0;
      int y = 29;

      for (int i = 0; i < 5; i++) {
         if (i == 0) {
            x += 44;
         } else {
            x += 18;
         }

         this.slots.add(new StatueOptionSlot(x, y, 16, 16, this.items.get(i)));
      }
   }

   protected void init() {
      super.init();
      this.initFields();
   }

   protected void initFields() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      float midX = this.width / 2.0F;
      float midY = this.height / 2.0F;
      RenderSystem.setShaderTexture(0, TEXTURE);
      blit(matrixStack, (int)(midX - this.imageWidth / 2), (int)(midY - this.imageHeight / 2), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.renderTitle(matrixStack);
      this.renderText(matrixStack);
      int startX = this.width / 2 - this.imageWidth / 2;
      int startY = this.height / 2 - this.imageHeight / 2;
      StatueOptionSlot hovered = null;

      for (StatueOptionSlot slot : this.slots) {
         this.renderItem(slot.getStack(), startX + slot.getPosX(), startY + slot.getPosY());
         if (this.isHovering(slot.getPosX(), slot.getPosY(), 16, 16, mouseX, mouseY)) {
            int l = slot.getPosX();
            int i1 = slot.getPosY();
            this.selected = this.slots.indexOf(slot);
            renderSlotHighlight(matrixStack, startX + l, startY + i1, this.getBlitOffset(), this.getSlotColor(0));
            hovered = slot;
         }
      }

      StatueOptionSlot selected = this.getSelected();
      RenderSystem.setShaderTexture(0, TEXTURE);
      blit(matrixStack, startX + selected.getPosX() - 3, startY + selected.getPosY() - 3, 0.0F, 84.0F, 22, 22, 256, 256);
      if (hovered != null) {
         this.renderTooltip(matrixStack, hovered.getStack(), mouseX, mouseY);
      }
   }

   private void renderItem(ItemStack stack, int x, int y) {
      this.itemRenderer.renderGuiItem(stack, x, y);
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
   }

   private void renderTitle(PoseStack matrixStack) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      float startX = i + this.titleLabelX - this.font.width(this.title.getString()) / 2.0F;
      float startY = (float)j + this.titleLabelY;
      this.font.draw(matrixStack, this.title, startX, startY, 4210752);
   }

   private void renderText(PoseStack matrixStack) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      TextComponent text = new TextComponent("Select an option for");
      TextComponent text1 = new TextComponent("the statue to generate.");
      float startTextX = i + this.imageWidth / 2.0F - this.font.width(text.getString()) / 2.0F;
      float startTextY = j + 59.0F;
      this.font.draw(matrixStack, text, startTextX, startTextY, 4210752);
      float startText1X = i + this.titleLabelX - this.font.width(text1.getString()) / 2.0F;
      float startText1Y = j + 56.0F + 13.0F;
      this.font.draw(matrixStack, text1, startText1X, startText1Y, 4210752);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      int translatedX = (int)Math.max(0.0, mouseX - this.getGuiLeft());
      int translatedY = (int)Math.max(0.0, mouseY - this.getGuiTop());
      StatueOptionSlot slot = this.getSelected();
      if (slot != null) {
         ModNetwork.CHANNEL.sendToServer(OmegaStatueUIMessage.selectItem(slot.getStack(), this.statuePos));
         this.onClose();
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private StatueOptionSlot getSelected() {
      return this.slots.get(this.selected);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if ((keyCode == 256 || keyCode == 69) && this.minecraft != null && this.minecraft.player != null) {
         ModNetwork.CHANNEL.sendToServer(OmegaStatueUIMessage.selectItem(this.getSelected().getStack(), this.statuePos));
         this.minecraft.player.closeContainer();
         return true;
      } else if (keyCode == 263) {
         this.keyDown();
         return true;
      } else {
         if (keyCode == 262) {
            this.keyUp();
         }

         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
      if (amount >= 1.0) {
         this.keyUp();
         return true;
      } else if (amount <= -1.0) {
         this.keyDown();
         return true;
      } else {
         return false;
      }
   }

   private void keyDown() {
      this.selected = (this.slots.size() + this.selected - 1) % this.slots.size();
   }

   private void keyUp() {
      this.selected = (this.selected + 1) % this.slots.size();
   }
}
