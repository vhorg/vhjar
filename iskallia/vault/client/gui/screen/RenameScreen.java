package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.util.RenameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RenameScreen extends AbstractContainerScreen<RenamingContainer> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/rename_screen.png");
   private String name;
   private CompoundTag data;
   private RenameType renameType;
   private Button renameButton;
   private ItemStack itemStack;
   private BlockPos chamberPos;
   private EditBox nameField;

   public RenameScreen(RenamingContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.font = Minecraft.getInstance().font;
      this.imageWidth = 172;
      this.imageHeight = 66;
      this.titleLabelX = this.imageWidth / 2;
      this.titleLabelY = 7;
      this.renameType = screenContainer.getRenameType();
      this.data = screenContainer.getNbt();
      switch (this.renameType) {
         case PLAYER_STATUE:
            this.name = this.data.getString("PlayerNickname");
            break;
         case CRYO_CHAMBER:
            this.chamberPos = NbtUtils.readBlockPos(this.data.getCompound("BlockPos"));
            this.name = this.data.getString("EternalName");
            break;
         case VAULT_CRYSTAL:
            this.itemStack = ItemStack.of(this.data);
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
      this.nameField = new EditBox(this.font, i + 34, j + 26, 103, 12, new TextComponent(this.name));
      this.nameField.setCanLoseFocus(false);
      this.nameField.setTextColor(-1);
      this.nameField.setTextColorUneditable(-1);
      this.nameField.setBordered(false);
      this.nameField.setMaxLength(16);
      this.nameField.setResponder(this::rename);
      this.addWidget(this.nameField);
      this.setInitialFocus(this.nameField);
      this.nameField.setValue(this.name);
      this.renameButton = new Button(i + 31, j + 40, 110, 20, new TextComponent("Confirm"), this::confirmPressed);
      this.addRenderableWidget(this.renameButton);
   }

   private void confirmPressed(Button button) {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("RenameType", this.renameType.ordinal());
      switch (this.renameType) {
         case PLAYER_STATUE:
            this.data.putString("PlayerNickname", this.name);
            nbt.put("Data", this.data);
            break;
         case CRYO_CHAMBER:
            CompoundTag data = new CompoundTag();
            data.put("BlockPos", NbtUtils.writeBlockPos(this.chamberPos));
            data.putString("EternalName", this.name);
            nbt.put("Data", data);
            break;
         case VAULT_CRYSTAL:
            nbt.put("Data", this.itemStack.serializeNBT());
      }

      ModNetwork.CHANNEL.sendToServer(RenameUIMessage.updateName(this.renameType, nbt));
      this.onClose();
   }

   private void rename(String name) {
      this.name = name;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.closeContainer();
         }
      } else if (keyCode == 257) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.confirmPressed(this.renameButton);
      } else if (keyCode == 69) {
         return true;
      }

      return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      float midX = this.width / 2.0F;
      float midY = this.height / 2.0F;
      RenderSystem.setShaderTexture(0, TEXTURE);
      blit(matrixStack, (int)(midX - this.imageWidth / 2), (int)(midY - this.imageHeight / 2), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.renderTitle(matrixStack);
      this.renderNameField(matrixStack, mouseX, mouseY, partialTicks);
      this.renameButton.render(matrixStack, mouseX, mouseY, partialTicks);
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

   public void renderNameField(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
   }
}
