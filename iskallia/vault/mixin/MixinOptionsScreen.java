package iskallia.vault.mixin;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundChangeDifficultyMessage;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({OptionsScreen.class})
public class MixinOptionsScreen extends Screen {
   private LockIconButton vaultLockButton;
   private CycleButton<VaultDifficulty> vaultDifficultyButton;

   protected MixinOptionsScreen(Component pTitle) {
      super(pTitle);
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   protected void init(CallbackInfo ci) {
      if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
         this.vaultDifficultyButton = (CycleButton<VaultDifficulty>)this.addRenderableWidget(
            this.createDifficultyButton(this.width, this.height, this.minecraft)
         );
         if (!this.minecraft.level.getLevelData().isHardcore()) {
            this.vaultDifficultyButton.setWidth(this.vaultDifficultyButton.getWidth() - 20);
            this.vaultLockButton = (LockIconButton)this.addRenderableWidget(
               new LockIconButton(
                  this.vaultDifficultyButton.x + this.vaultDifficultyButton.getWidth(),
                  this.vaultDifficultyButton.y,
                  button -> this.minecraft
                     .setScreen(
                        new ConfirmScreen(
                           this::lockVaultDifficulty,
                           new TranslatableComponent("the_vault.difficulty.lock.title"),
                           new TranslatableComponent(
                              "the_vault.difficulty.lock.question", new Object[]{WorldSettings.get(this.minecraft.level).getVaultDifficulty().getDisplayName()}
                           )
                        )
                     )
               )
            );
            this.vaultLockButton.setLocked(WorldSettings.get(this.minecraft.level).isVaultDifficultyLocked());
            this.vaultLockButton.active = !this.vaultLockButton.isLocked();
            this.vaultDifficultyButton.active = !this.vaultLockButton.isLocked();
         } else {
            this.vaultDifficultyButton.active = false;
         }
      }
   }

   private void lockVaultDifficulty(boolean vaultDifficultyLocked) {
      this.minecraft.setScreen(this);
      if (vaultDifficultyLocked && this.minecraft.level != null) {
         ModNetwork.CHANNEL
            .sendToServer(new ServerboundChangeDifficultyMessage(WorldSettings.get(this.minecraft.level).getVaultDifficulty(), vaultDifficultyLocked));
         this.vaultLockButton.setLocked(true);
         this.vaultLockButton.active = false;
         this.vaultDifficultyButton.active = false;
      }
   }

   private CycleButton<VaultDifficulty> createDifficultyButton(int screenWidth, int screenHeight, Minecraft mc) {
      return CycleButton.builder(VaultDifficulty::getDisplayName)
         .withValues(VaultDifficulty.values())
         .withInitialValue(WorldSettings.get(mc.level).getVaultDifficulty())
         .create(
            screenWidth / 2 - 155 + 160,
            screenHeight / 6 - 12 + 20,
            150,
            20,
            new TranslatableComponent("the_vault.options.difficulty"),
            (button, value) -> ModNetwork.CHANNEL
               .sendToServer(new ServerboundChangeDifficultyMessage(value, WorldSettings.get(mc.level).isVaultDifficultyLocked()))
         );
   }
}
