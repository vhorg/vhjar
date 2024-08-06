package iskallia.vault.task.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public class TaskWidget implements Widget, GuiEventListener, NarratableEntry {
   private Vec2d offset;
   private Task task;
   private Supplier<RendererContext> context;

   public TaskWidget(Vec2d offset, Task task, Supplier<RendererContext> context) {
      this.offset = offset;
      this.task = task;
      this.context = context;
   }

   private RendererContext getContext(Consumer<RendererContext> configurator) {
      RendererContext context = this.context.get();
      configurator.accept(context);
      return context;
   }

   private RendererContext getContext() {
      return this.getContext(context -> {});
   }

   public void render(PoseStack matrices, int mouseX, int mouseY, float tickDelta) {
      matrices.pushPose();
      matrices.translate(this.offset.getX(), this.offset.getY(), 0.0);
      this.task.onRender(this.getContext(context -> {
         context.setMatrices(matrices);
         context.setTickDelta(tickDelta);
         context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset));
      }));
      matrices.popPose();
   }

   public void mouseMoved(double mouseX, double mouseY) {
      this.task.onMouseMoved(this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.task.onMouseClicked(button, this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.task.onMouseReleased(button, this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      return this.task.onMouseDragged(button, dragX, dragY, this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      return this.task.onMouseScrolled(delta, this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.task.onKeyPressed(keyCode, scanCode, modifiers, this.getContext());
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.task.onKeyReleased(keyCode, scanCode, modifiers, this.getContext());
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.task.onCharTyped(codePoint, modifiers, this.getContext());
   }

   public boolean changeFocus(boolean focus) {
      return this.task.onChangeFocus(focus, this.getContext());
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.task.isMouseOver(this.getContext(context -> context.setMouse(new Vec2d(mouseX, mouseY).subtract(this.offset))));
   }

   public void updateNarration(NarrationElementOutput output) {
      this.task.onUpdateNarration(output, this.getContext());
   }

   public NarrationPriority narrationPriority() {
      return this.task.getNarrationPriority(this.getContext());
   }

   public boolean isActive() {
      return this.task.isActive(this.getContext());
   }
}
