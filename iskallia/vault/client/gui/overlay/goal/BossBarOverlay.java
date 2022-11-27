package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class BossBarOverlay {
   public abstract boolean shouldDisplay();

   public abstract int drawOverlay(PoseStack var1, float var2);
}
