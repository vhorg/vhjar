package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class GameControlsModifier extends VaultModifier<GameControlsModifier.Properties> {
   public static final GameControlsModifier DEFAULT = new GameControlsModifier(
      VaultMod.id("default_controls"),
      new GameControlsModifier.Properties(true, true, true, false),
      new VaultModifier.Display("Default Controls", TextColor.parseColor("#000000"), "", null, null)
   );

   public GameControlsModifier(ResourceLocation id, GameControlsModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   public static class Properties {
      @Expose
      private boolean forward;
      @Expose
      private boolean backward;
      @Expose
      private boolean jump;
      @Expose
      private boolean swapLeftAndRight;

      public Properties(boolean forward, boolean backward, boolean jump, boolean swapLeftAndRight) {
         this.forward = forward;
         this.backward = backward;
         this.jump = jump;
         this.swapLeftAndRight = swapLeftAndRight;
      }

      public boolean canMoveForward() {
         return this.forward;
      }

      public boolean canMoveBackward() {
         return this.backward;
      }

      public boolean canJump() {
         return this.jump;
      }

      public boolean isLeftAndRightSwapped() {
         return this.swapLeftAndRight;
      }

      public void setForward(boolean forward) {
         this.forward = forward;
      }

      public void setBackward(boolean backward) {
         this.backward = backward;
      }

      public void setJump(boolean jump) {
         this.jump = jump;
      }

      public void setSwapLeftAndRight(boolean swapLeftAndRight) {
         this.swapLeftAndRight = swapLeftAndRight;
      }
   }
}
