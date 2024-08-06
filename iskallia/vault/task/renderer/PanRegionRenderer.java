package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.Optional;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;

public abstract class PanRegionRenderer<T extends Task, C extends RendererContext> extends TranslatableRenderer<T, C> {
   private static final double SCROLL_SENSITIVITY = 12.0;
   protected boolean dragging;
   protected Vec2d clickedPos;

   protected abstract Vec2d getSize(T var1, C var2);

   protected abstract PanRegionRenderer.ScrollType getScrollType(T var1, C var2);

   protected Vec2d getCenterButtonPos(T task, C context) {
      int centerButtonX = (int)(this.getSize(task, context).getX() - 18.0);
      int centerButtonY = (int)(this.getSize(task, context).getY() - 18.0);
      return new Vec2d(centerButtonX, centerButtonY);
   }

   protected NineSlice.TextureRegion getBackground() {
      return ScreenTextures.INSET_GREY_BACKGROUND;
   }

   protected void onRenderBackground(T task, C context) {
      Vec2d size = this.getSize(task, context);
      GuiComponent.fill(context.getMatrices(), 0, 0, (int)size.getX(), (int)size.getY(), 33554431);
   }

   protected abstract void onRenderForeground(T var1, C var2);

   @Override
   public void onRender(T task, C context) {
      Vec2d size = this.getSize(task, context);
      this.getBackground().blit(context.getMatrices(), -1, -1, 0, (int)size.getX() + 2, (int)size.getY() + 2);
      UIHelper.renderOverflowHidden(context.getMatrices(), matrices -> this.onRenderBackground(task, context), matrices -> {
         context.push();
         context.translate(this.getTranslation(task, context).getX(), this.getTranslation(task, context).getY(), 0.0);
         this.onRenderForeground(task, context);
         context.pop();
      });
   }

   public boolean isInBounds(Vec2d mouse, T task, C context) {
      Vec2d size = this.getSize(task, context);
      return mouse.getX() >= 0.0 && mouse.getX() < size.getX() && mouse.getY() >= 0.0 && mouse.getY() <= size.getY();
   }

   @Override
   public boolean onMouseClicked(T task, int button, C context) {
      if (!this.isInBounds(context.getMouse(), task, context)) {
         return false;
      } else {
         this.clickedPos = context.getMouse();
         context.pushMouse(context.getMouse().subtract(this.getTranslation(task, context)));
         this.dragging = true;

         for (Task child : task.getChildren()) {
            if (child.onMouseClicked(button, context)) {
               this.dragging = false;
               break;
            }
         }

         context.popMouse();
         return true;
      }
   }

   @Override
   public boolean onMouseReleased(T task, int button, C context) {
      this.dragging = false;
      context.pushMouse(context.getMouse().subtract(this.getTranslation(task, context)));

      for (Task child : task.getChildren()) {
         child.onMouseReleased(button, context);
      }

      context.popMouse();
      return button == 0;
   }

   @Override
   public boolean onMouseDragged(T task, int button, double dragX, double dragY, C context) {
      context.pushMouse(context.getMouse().subtract(this.getTranslation(task, context)));

      for (Task child : task.getChildren()) {
         if (child.onMouseDragged(button, dragX, dragY, context)) {
            context.popMouse();
            return true;
         }
      }

      context.popMouse();
      if (!this.dragging) {
         return false;
      } else {
         Vec2d clicked = context.getMouse();
         if (this.clickedPos != null) {
            Vec2d delta = clicked.subtract(this.clickedPos);
            if (this.getScrollType(task, context) == PanRegionRenderer.ScrollType.VERTICAL) {
               double value = delta.getY();
               Vec2d translation = this.getTranslation(task, context);
               if (translation.getY() + value > 0.0) {
                  value = 0.0;
               }

               this.addTranslation(task, context, new Vec2d(0.0, value));
            } else if (this.getScrollType(task, context) == PanRegionRenderer.ScrollType.HORIZONTAL) {
               this.addTranslation(task, context, new Vec2d(delta.getX(), 0.0));
            } else {
               this.addTranslation(task, context, delta);
            }
         }

         this.clickedPos = clicked;
         return true;
      }
   }

   @Override
   public boolean onMouseScrolled(T task, double delta, C context) {
      double value = delta * 12.0;
      if (this.getScrollType(task, context) == PanRegionRenderer.ScrollType.HORIZONTAL) {
         this.addTranslation(task, context, new Vec2d(value, 0.0));
      } else if (this.getScrollType(task, context) == PanRegionRenderer.ScrollType.VERTICAL) {
         Vec2d translation = this.getTranslation(task, context);
         if (translation.getY() + value > 0.0) {
            value = translation.getY() * -1.0;
         }

         this.addTranslation(task, context, new Vec2d(0.0, value));
      }

      return super.onMouseScrolled(task, delta, context);
   }

   @Override
   public boolean isMouseOver(T task, C context) {
      if (this.dragging) {
         return true;
      } else {
         Vec2d size = this.getSize(task, context);
         Vec2d mouse = context.getMouse();
         return mouse.getX() >= 0.0 && mouse.getX() < size.getX() && mouse.getY() >= 0.0 && mouse.getY() <= size.getY();
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.dragging, buffer);
      Adapters.VEC_2D.asNullable().writeBits(this.clickedPos, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.dragging = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
      this.clickedPos = Adapters.VEC_2D.asNullable().readBits(buffer).orElse(new Vec2d());
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BOOLEAN.writeNbt(this.dragging).ifPresent(value -> nbt.put("dragging", value));
         Adapters.VEC_2D.writeNbt(this.clickedPos).ifPresent(value -> nbt.put("clickedPos", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.dragging = Adapters.BOOLEAN.readNbt(nbt.get("dragging")).orElse(false);
      this.clickedPos = Adapters.VEC_2D.readNbt(nbt.get("clickedPos")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.BOOLEAN.writeJson(this.dragging).ifPresent(value -> json.add("dragging", value));
         Adapters.VEC_2D.writeJson(this.clickedPos).ifPresent(value -> json.add("clickedPos", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.dragging = Adapters.BOOLEAN.readJson(json.get("dragging")).orElse(false);
      this.clickedPos = Adapters.VEC_2D.readJson(json.get("clickedPos")).orElse(null);
   }

   public static enum ScrollType {
      ZOOM,
      VERTICAL,
      HORIZONTAL;
   }
}
