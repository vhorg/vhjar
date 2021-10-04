package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.widget.DifficultyButton;
import iskallia.vault.container.GlobalDifficultyContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.GlobalDifficultyMessage;
import iskallia.vault.world.data.GlobalDifficultyData;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GlobalDifficultyScreen extends ContainerScreen<GlobalDifficultyContainer> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("the_vault", "textures/gui/global_difficulty_screen.png");
   private DifficultyButton vaultDifficultyButton;
   private DifficultyButton crystalCostButton;
   private Button confirmButton;
   private final int buttonWidth = 168;
   private final int buttonHeight = 20;
   private final int buttonPadding = 5;
   private int buttonStartX;
   private int buttonStartY;

   public GlobalDifficultyScreen(GlobalDifficultyContainer container, PlayerInventory inventory, ITextComponent title) {
      super(container, inventory, title);
      this.field_230712_o_ = Minecraft.func_71410_x().field_71466_p;
      this.field_146999_f = 190;
      this.field_147000_g = 256;
      this.field_238742_p_ = this.field_146999_f / 2;
      this.field_238743_q_ = 7;
   }

   protected void func_231160_c_() {
      super.func_231160_c_();
      int centerX = this.field_147003_i + this.field_146999_f / 2;
      this.buttonStartX = centerX - 84;
      int guiBottom = this.field_147009_r + this.field_147000_g;
      this.buttonStartY = guiBottom - 75 - 8;
      this.initializeFields();
   }

   public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(TEXTURE);
      float midX = this.field_230708_k_ / 2.0F;
      float midY = this.field_230709_l_ / 2.0F;
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
      this.vaultDifficultyButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.crystalCostButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.confirmButton.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.field_147003_i + 5, this.field_147009_r + 27, 0.0);
      UIHelper.renderWrappedText(matrixStack, ModConfigs.DIFFICULTY_DESCRIPTION.getDescription(), this.field_146999_f - 10, 5);
      matrixStack.func_227865_b_();
      this.renderTitle(matrixStack);
   }

   protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
      this.field_230710_m_.forEach(button -> {
         if (button.func_230449_g_()) {
            button.func_230443_a_(matrixStack, mouseX, mouseY);
         }
      });
   }

   public boolean func_231046_a_(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   public boolean func_231177_au__() {
      return true;
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
      return this.func_195361_a(mouseX, mouseY, this.field_147003_i, this.field_147009_r, mouseButton)
         ? false
         : super.func_231044_a_(mouseX, mouseY, mouseButton);
   }

   private void initializeFields() {
      GlobalDifficultyData.Difficulty vaultDifficulty = GlobalDifficultyData.Difficulty.values()[((GlobalDifficultyContainer)this.field_147002_h)
         .getData()
         .func_74762_e("VaultDifficulty")];
      this.vaultDifficultyButton = new DifficultyButton(
         "Vault Mob Difficulty",
         "VaultDifficulty",
         this.buttonStartX,
         this.buttonStartY,
         168,
         20,
         new StringTextComponent("Vault Mob Difficulty: " + vaultDifficulty.toString()),
         this::buttonPressed
      );
      GlobalDifficultyData.Difficulty crystalCost = GlobalDifficultyData.Difficulty.values()[((GlobalDifficultyContainer)this.field_147002_h)
         .getData()
         .func_74762_e("CrystalCost")];
      this.crystalCostButton = new DifficultyButton(
         "Crystal Cost Multiplier",
         "CrystalCost",
         this.buttonStartX,
         this.vaultDifficultyButton.field_230691_m_ + 20 + 5,
         168,
         20,
         new StringTextComponent("Crystal Cost Multiplier: " + crystalCost.toString()),
         this::buttonPressed
      );
      this.confirmButton = new Button(
         this.buttonStartX, this.crystalCostButton.field_230691_m_ + 20 + 5, 168, 20, new StringTextComponent("Confirm"), this::buttonPressed
      );
      this.func_230480_a_(this.vaultDifficultyButton);
      this.func_230480_a_(this.crystalCostButton);
      this.func_230480_a_(this.confirmButton);
   }

   private void buttonPressed(Button button) {
      if (button instanceof DifficultyButton) {
         DifficultyButton difficultyButton = (DifficultyButton)button;
         difficultyButton.getNextOption();
         this.selectDifficulty(difficultyButton.getKey(), difficultyButton.getCurrentOption());
      } else {
         ModNetwork.CHANNEL.sendToServer(GlobalDifficultyMessage.setGlobalDifficultyOptions(((GlobalDifficultyContainer)this.field_147002_h).getData()));
         this.func_231175_as__();
      }
   }

   public void selectDifficulty(String key, GlobalDifficultyData.Difficulty selected) {
      ((GlobalDifficultyContainer)this.field_147002_h).getData().func_74768_a(key, selected.ordinal());
   }

   private void renderTitle(MatrixStack matrixStack) {
      int i = (this.field_230708_k_ - this.field_146999_f) / 2;
      int j = (this.field_230709_l_ - this.field_147000_g) / 2;
      float startX = i + this.field_238742_p_ - this.field_230712_o_.func_78256_a(this.field_230704_d_.getString()) / 2.0F;
      float startY = (float)j + this.field_238743_q_;
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, startX, startY, 4210752);
   }
}
