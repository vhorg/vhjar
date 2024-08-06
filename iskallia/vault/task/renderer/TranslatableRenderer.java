package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class TranslatableRenderer<T extends Task, C extends RendererContext> extends TaskRenderer<T, C> {
   private Vec2d defaultTranslation;
   private Vec2d translation;

   public void setTranslation(Vec2d position) {
      this.translation = position;
   }

   protected void addTranslation(T task, C context, Vec2d additional) {
      this.setTranslation(this.getTranslation(task, context).add(additional));
   }

   public void resetTranslation() {
      this.translation = this.defaultTranslation;
   }

   protected Vec2d getTranslation(T task, C context) {
      return this.translation;
   }

   @Override
   public void onRender(T task, C context) {
      context.push();
      context.translate(this.translation.getX(), this.translation.getY(), 0.0);
      super.onRender(task, context);
      context.pop();
   }

   @Override
   public void onMouseMoved(T task, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      super.onMouseMoved(task, context);
      context.popMouse();
   }

   @Override
   public boolean onMouseClicked(T task, int button, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      boolean result = super.onMouseClicked(task, button, context);
      context.popMouse();
      return result;
   }

   @Override
   public boolean onMouseReleased(T task, int button, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      boolean result = super.onMouseReleased(task, button, context);
      context.popMouse();
      return result;
   }

   @Override
   public boolean onMouseDragged(T task, int button, double dragX, double dragY, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      boolean result = super.onMouseDragged(task, button, dragX, dragY, context);
      context.popMouse();
      return result;
   }

   @Override
   public boolean onMouseScrolled(T task, double delta, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      boolean result = super.onMouseScrolled(task, delta, context);
      context.popMouse();
      return result;
   }

   @Override
   public boolean isMouseOver(T task, C context) {
      context.pushMouse(context.getMouse().subtract(this.translation));
      boolean result = super.isMouseOver(task, context);
      context.popMouse();
      return result;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.VEC_2D.asNullable().writeBits(this.translation, buffer);
      Adapters.VEC_2D.asNullable().writeBits(this.defaultTranslation, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.translation = Adapters.VEC_2D.asNullable().readBits(buffer).orElse(Vec2d.ZERO);
      this.defaultTranslation = Adapters.VEC_2D.asNullable().readBits(buffer).orElse(this.translation);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.VEC_2D.asNullable().writeNbt(this.translation).ifPresent(tag -> nbt.put("translation", tag));
         Adapters.VEC_2D.asNullable().writeNbt(this.defaultTranslation).ifPresent(tag -> nbt.put("default_translation", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.translation = Adapters.VEC_2D.asNullable().readNbt(nbt.get("translation")).orElseGet(Vec2d::new);
      this.defaultTranslation = Adapters.VEC_2D.asNullable().readNbt(nbt.get("default_translation")).orElse(this.translation);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.VEC_2D.asNullable().writeJson(this.translation).ifPresent(tag -> json.add("translation", tag));
         Adapters.VEC_2D.asNullable().writeJson(this.defaultTranslation).ifPresent(tag -> json.add("default_translation", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.translation = Adapters.VEC_2D.asNullable().readJson(json.get("translation")).orElseGet(Vec2d::new);
      this.defaultTranslation = Adapters.VEC_2D.asNullable().readJson(json.get("default_translation")).orElse(this.translation);
   }
}
