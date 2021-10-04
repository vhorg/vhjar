package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class BossBarOverlay {
   public abstract boolean shouldDisplay();

   public abstract int drawOverlay(MatrixStack var1, float var2);
}
