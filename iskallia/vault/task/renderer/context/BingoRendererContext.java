package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BingoRendererContext extends RendererContext {
   private UUID uuid;
   private boolean expandedView;
   private boolean completed;

   public BingoRendererContext(PoseStack matrices, float tickDelta, BufferSource bufferSource, Font font) {
      super(matrices, tickDelta, bufferSource, font);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setUuid(UUID uuid) {
      this.uuid = uuid;
   }

   public boolean isExpandedView() {
      return this.expandedView;
   }

   public void setExpandedView(boolean expandedView) {
      this.expandedView = expandedView;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }
}
