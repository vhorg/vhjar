package iskallia.vault.task.renderer;

import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.RendererContext;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;

public abstract class DelegatedTaskRenderer<T extends Task, C extends RendererContext> extends TaskRenderer<T, C> {
   public abstract TaskRenderer<T, C> getDelegate(T var1, C var2);

   @Override
   public void onRender(T task, C context) {
      this.getDelegate(task, context).onRender(task, context);
   }

   @Override
   public void onMouseMoved(T task, C context) {
      this.getDelegate(task, context).onMouseMoved(task, context);
   }

   @Override
   public boolean onMouseClicked(T task, int button, C context) {
      return this.getDelegate(task, context).onMouseClicked(task, button, context);
   }

   @Override
   public boolean onMouseReleased(T task, int button, C context) {
      return this.getDelegate(task, context).onMouseReleased(task, button, context);
   }

   @Override
   public boolean onMouseDragged(T task, int button, double dragX, double dragY, C context) {
      return this.getDelegate(task, context).onMouseDragged(task, button, dragX, dragY, context);
   }

   @Override
   public boolean onMouseScrolled(T task, double delta, C context) {
      return this.getDelegate(task, context).onMouseScrolled(task, delta, context);
   }

   @Override
   public boolean onKeyPressed(T task, int keyCode, int scanCode, int modifiers, C context) {
      return this.getDelegate(task, context).onKeyPressed(task, keyCode, scanCode, modifiers, context);
   }

   @Override
   public boolean onKeyReleased(T task, int keyCode, int scanCode, int modifiers, C context) {
      return this.getDelegate(task, context).onKeyReleased(task, keyCode, scanCode, modifiers, context);
   }

   @Override
   public boolean onCharTyped(T task, char codePoint, int modifiers, C context) {
      return this.getDelegate(task, context).onCharTyped(task, codePoint, modifiers, context);
   }

   @Override
   public boolean onChangeFocus(T task, boolean focus, C context) {
      return this.getDelegate(task, context).onChangeFocus(task, focus, context);
   }

   @Override
   public boolean isMouseOver(T task, C context) {
      return this.getDelegate(task, context).isMouseOver(task, context);
   }

   @Override
   public void onUpdateNarration(T task, NarrationElementOutput output, C context) {
      this.getDelegate(task, context).onUpdateNarration(task, output, context);
   }

   @Override
   public NarrationPriority getNarrationPriority(T task, C context) {
      return this.getDelegate(task, context).getNarrationPriority(task, context);
   }

   @Override
   public boolean isActive(T task, C context) {
      return this.getDelegate(task, context).isActive(task, context);
   }
}
