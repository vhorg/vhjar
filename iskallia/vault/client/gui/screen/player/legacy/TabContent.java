package iskallia.vault.client.gui.screen.player.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nonnull;

public interface TabContent {
   void init();

   boolean mouseClicked(double var1, double var3, int var5);

   boolean mouseReleased(double var1, double var3, int var5);

   void mouseMoved(double var1, double var3);

   boolean mouseScrolled(double var1, double var3, double var5);

   void update();

   List<Runnable> render(@Nonnull PoseStack var1, int var2, int var3, float var4);

   void removed();
}
