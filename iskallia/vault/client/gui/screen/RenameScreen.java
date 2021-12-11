package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.util.RenameType;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RenameScreen extends ContainerScreen<RenamingContainer> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/rename_screen.png");
   private String name;
   private CompoundNBT data;
   private RenameType renameType;
   private Button renameButton;
   private ItemStack itemStack;
   private TraderCore core;
   private BlockPos chamberPos;
   private TextFieldWidget nameField;

   public RenameScreen(RenamingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_230712_o_ = Minecraft.func_71410_x().field_71466_p;
      this.field_146999_f = 172;
      this.field_147000_g = 66;
      this.field_238742_p_ = this.field_146999_f / 2;
      this.field_238743_q_ = 7;
      this.renameType = screenContainer.getRenameType();
      this.data = screenContainer.getNbt();
      switch (this.renameType) {
         case PLAYER_STATUE:
            this.name = this.data.func_74779_i("PlayerNickname");
            break;
         case TRADER_CORE:
            this.itemStack = ItemStack.func_199557_a(this.data);
            CompoundNBT stackNbt = this.itemStack.func_196082_o();

            try {
               this.core = NBTSerializer.deserialize(TraderCore.class, stackNbt.func_74775_l("core"));
               this.name = this.core.getName();
            } catch (Exception var6) {
               var6.printStackTrace();
            }
            break;
         case CRYO_CHAMBER:
            this.chamberPos = NBTUtil.func_186861_c(this.data.func_74775_l("BlockPos"));
            this.name = this.data.func_74779_i("EternalName");
            break;
         case VAULT_CRYSTAL:
            this.itemStack = ItemStack.func_199557_a(this.data);
            this.name = VaultCrystalItem.getData(this.itemStack).getPlayerBossName();
      }
   }

   protected void func_231160_c_() {
      super.func_231160_c_();
      this.initFields();
   }

   protected void initFields() {
      this.field_230706_i_.field_195559_v.func_197967_a(true);
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.nameField = new TextFieldWidget(this.field_230712_o_, i + 34, j + 26, 103, 12, new StringTextComponent(this.name));
      this.nameField.func_146205_d(false);
      this.nameField.func_146193_g(-1);
      this.nameField.func_146204_h(-1);
      this.nameField.func_146185_a(false);
      this.nameField.func_146203_f(16);
      this.nameField.func_212954_a(this::rename);
      this.field_230705_e_.add(this.nameField);
      this.func_212928_a(this.nameField);
      this.nameField.func_146180_a(this.name);
      this.renameButton = new Button(i + 31, j + 40, 110, 20, new StringTextComponent("Confirm"), this::confirmPressed);
      this.func_230480_a_(this.renameButton);
   }

   private void confirmPressed(Button button) {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74768_a("RenameType", this.renameType.ordinal());
      switch (this.renameType) {
         case PLAYER_STATUE:
            this.data.func_74778_a("PlayerNickname", this.name);
            nbt.func_218657_a("Data", this.data);
            break;
         case TRADER_CORE:
            try {
               CompoundNBT stackNbt = this.itemStack.func_196082_o();
               this.core.setName(this.name);
               CompoundNBT coreNbt = NBTSerializer.serialize(this.core);
               stackNbt.func_218657_a("core", coreNbt);
               this.itemStack.func_77982_d(stackNbt);
               nbt.func_218657_a("Data", this.itemStack.serializeNBT());
            } catch (Exception var5) {
               var5.printStackTrace();
            }
            break;
         case CRYO_CHAMBER:
            CompoundNBT data = new CompoundNBT();
            data.func_218657_a("BlockPos", NBTUtil.func_186859_a(this.chamberPos));
            data.func_74778_a("EternalName", this.name);
            nbt.func_218657_a("Data", data);
            break;
         case VAULT_CRYSTAL:
            VaultCrystalItem.getData(this.itemStack).setPlayerBossName(this.name);
            nbt.func_218657_a("Data", this.itemStack.serializeNBT());
      }

      if (this.renameType != RenameType.PLAYER_STATUE && this.renameType != RenameType.TRADER_CORE && this.renameType == RenameType.CRYO_CHAMBER) {
      }

      ModNetwork.CHANNEL.sendToServer(RenameUIMessage.updateName(this.renameType, nbt));
      this.func_231175_as__();
   }

   private void rename(String name) {
      if (!name.isEmpty()) {
         this.name = name;
      }
   }

   public boolean func_231046_a_(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         if (this.field_230706_i_ != null && this.field_230706_i_.field_71439_g != null) {
            this.field_230706_i_.field_71439_g.func_71053_j();
         }
      } else if (keyCode == 257) {
         Minecraft.func_71410_x().func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
         this.confirmPressed(this.renameButton);
      } else if (keyCode == 69) {
         return true;
      }

      return this.nameField.func_231046_a_(keyCode, scanCode, modifiers)
         || this.nameField.func_212955_f()
         || super.func_231046_a_(keyCode, scanCode, modifiers);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      float midX = this.field_230708_k_ / 2.0F;
      float midY = this.field_230709_l_ / 2.0F;
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      func_238463_a_(
         matrixStack,
         (int)(midX - this.field_146999_f / 2),
         (int)(midY - this.field_147000_g / 2),
         0.0F,
         0.0F,
         this.field_146999_f,
         this.field_147000_g,
         256,
         256
      );
      this.renderTitle(matrixStack);
      this.renderNameField(matrixStack, mouseX, mouseY, partialTicks);
      this.renameButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
   }

   private void renderTitle(MatrixStack matrixStack) {
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
      float startX = i + this.field_238742_p_ - this.field_230712_o_.func_78256_a(this.field_230704_d_.getString()) / 2.0F;
      float startY = (float)j + this.field_238743_q_;
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, startX, startY, 4210752);
   }

   public void renderNameField(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.nameField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
   }
}
