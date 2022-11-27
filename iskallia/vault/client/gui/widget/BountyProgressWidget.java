package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Rectangle;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BountyProgressWidget extends AbstractWidget {
   public BountyProgressWidget(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   public Rectangle getBounds() {
      return new Rectangle(this.x - 12, this.y - 12, this.width, this.height);
   }

   public void updateNarration(@NotNull NarrationElementOutput ignored) {
   }
}
