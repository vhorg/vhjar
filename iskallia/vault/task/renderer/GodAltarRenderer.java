package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.GodAltarTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.context.GodAltarRendererContext;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GodAltarRenderer {
   public static class Child extends TaskRenderer<Task, GodAltarRendererContext> {
      private String title;
      private String hint;

      public Child() {
      }

      public Child(String title, String hint) {
         this.title = title;
         this.hint = hint;
      }

      @OnlyIn(Dist.CLIENT)
      public void onRender(Task task, GodAltarRendererContext context) {
         if (task instanceof IProgressTask progressTask) {
            TaskProgress progress = progressTask.getProgress();
            String current = String.valueOf(progress.getCurrent().intValue());
            String target = String.valueOf(progress.getTarget().intValue());
            context.renderProgressBar(this.title, this.hint.replace("${current}", current).replace("${target}", target));
         } else {
            context.renderHeader(this.title, false);
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.asNullable().writeBits(this.title, buffer);
         Adapters.UTF_8.asNullable().writeBits(this.hint, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.title = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
         this.hint = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.title).ifPresent(value -> nbt.put("title", value));
            Adapters.UTF_8.writeNbt(this.hint).ifPresent(value -> nbt.put("hint", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.title = Adapters.UTF_8.readNbt(nbt.get("title")).orElse(null);
         this.hint = Adapters.UTF_8.readNbt(nbt.get("hint")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.title).ifPresent(value -> json.add("title", value));
            Adapters.UTF_8.writeJson(this.hint).ifPresent(value -> json.add("hint", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.title = Adapters.UTF_8.readJson(json.get("title")).orElse(null);
         this.hint = Adapters.UTF_8.readJson(json.get("hint")).orElse(null);
      }
   }

   public static class Root extends TaskRenderer<GodAltarTask, GodAltarRendererContext> {
      public static final GodAltarRenderer.Root INSTANCE = new GodAltarRenderer.Root();

      @OnlyIn(Dist.CLIENT)
      public void onRender(GodAltarTask root, GodAltarRendererContext context) {
         TimedTask timed = root.getChild();

         for (Task child : timed.getChildren()) {
            child.onRender(context);
         }

         context.renderTimerBar(root.getGod(), UIHelper.formatTimeString(timed.getDuration() - timed.getElapsed()));
      }
   }
}
