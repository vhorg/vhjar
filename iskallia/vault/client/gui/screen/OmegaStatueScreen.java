package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.component.StatueOptionSlot;
import iskallia.vault.container.OmegaStatueContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.OmegaStatueUIMessage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class OmegaStatueScreen extends ContainerScreen<OmegaStatueContainer> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/omega_statue_options.png");
   private List<StatueOptionSlot> slots = new ArrayList<>();
   List<ItemStack> items = new ArrayList<>();
   BlockPos statuePos;

   public OmegaStatueScreen(OmegaStatueContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
      this.field_230712_o_ = Minecraft.func_71410_x().field_71466_p;
      this.field_146999_f = 176;
      this.field_147000_g = 84;
      this.field_238742_p_ = 88;
      this.field_238743_q_ = 7;

      for (INBT nbt : screenContainer.getItemsCompound()) {
         CompoundNBT itemNbt = (CompoundNBT)nbt;
         this.items.add(ItemStack.func_199557_a(itemNbt));
      }

      this.statuePos = NBTUtil.func_186861_c(screenContainer.getBlockPos());
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

   protected void func_231160_c_() {
      super.func_231160_c_();
      this.initFields();
   }

   protected void initFields() {
      this.field_230706_i_.field_195559_v.func_197967_a(true);
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
   }

   public boolean func_231046_a_(int keyCode, int scanCode, int modifiers) {
      if ((keyCode == 256 || keyCode == 69) && this.field_230706_i_ != null && this.field_230706_i_.field_71439_g != null) {
         ModNetwork.CHANNEL.sendToServer(OmegaStatueUIMessage.selectItem(this.items.get(0), this.statuePos));
         this.field_230706_i_.field_71439_g.func_71053_j();
         return true;
      } else {
         return false;
      }
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
      this.renderText(matrixStack);
      int startX = this.field_230708_k_ / 2 - this.field_146999_f / 2;
      int startY = this.field_230709_l_ / 2 - this.field_147000_g / 2;

      for (StatueOptionSlot slot : this.slots) {
         this.renderItem(slot.getStack(), startX + slot.getPosX(), startY + slot.getPosY());
      }

      for (StatueOptionSlot slot : this.slots) {
         if (slot.contains(mouseX - startX, mouseY - startY) && !slot.getStack().func_190926_b()) {
            this.func_230457_a_(matrixStack, slot.getStack(), startX + slot.getPosX(), startY + slot.getPosY());
            break;
         }
      }
   }

   private void renderItem(ItemStack stack, int x, int y) {
      this.field_230707_j_.func_175042_a(stack, x, y);
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

   private void renderText(MatrixStack matrixStack) {
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
      StringTextComponent text = new StringTextComponent("Select an option for");
      StringTextComponent text1 = new StringTextComponent("the statue to generate.");
      float startTextX = i + this.field_146999_f / 2.0F - this.field_230712_o_.func_78256_a(text.getString()) / 2.0F;
      float startTextY = j + 59.0F;
      this.field_230712_o_.func_243248_b(matrixStack, text, startTextX, startTextY, 4210752);
      float startText1X = i + this.field_238742_p_ - this.field_230712_o_.func_78256_a(text1.getString()) / 2.0F;
      float startText1Y = j + 56.0F + 13.0F;
      this.field_230712_o_.func_243248_b(matrixStack, text1, startText1X, startText1Y, 4210752);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      int translatedX = (int)Math.max(0.0, mouseX - this.getGuiLeft());
      int translatedY = (int)Math.max(0.0, mouseY - this.getGuiTop());
      StatueOptionSlot slot = this.getClickedSlot(translatedX, translatedY);
      if (slot != null) {
         ModNetwork.CHANNEL.sendToServer(OmegaStatueUIMessage.selectItem(slot.getStack(), this.statuePos));
         this.func_231175_as__();
      }

      return super.func_231044_a_(mouseX, mouseY, button);
   }

   private StatueOptionSlot getClickedSlot(int x, int y) {
      if (y >= 29 && y <= 45) {
         for (StatueOptionSlot slot : this.slots) {
            if (x >= slot.getPosX() && x <= slot.getPosX() + 16) {
               return slot;
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
