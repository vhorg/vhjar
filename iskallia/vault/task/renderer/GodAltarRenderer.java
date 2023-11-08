package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.CompleteGodAltarTask;
import iskallia.vault.task.NodeTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.context.GodAltarRendererContext;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class GodAltarRenderer {
   public static class Base extends TaskRenderer<Task, GodAltarRendererContext> {
      private String title;
      private String bar;

      public Base() {
      }

      public Base(String title, String bar) {
         this.title = title;
         this.bar = bar;
      }

      public void render(Task task, GodAltarRendererContext context) {
         context.setCompleted(task.isCompleted(null));
         if (!context.isCompleted()) {
            if (task instanceof IProgressTask progressTask) {
               TaskProgress progress = progressTask.getProgress();
               String current = String.valueOf(progress.getCurrent().intValue());
               String max = String.valueOf(progress.getTarget().intValue());
               context.renderProgressBar(this.title, progress.getProgress(), this.bar.replace("${current}", current).replace("${target}", max));
            } else {
               context.renderHeader(this.title, false);
            }
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.writeBits(this.title, buffer);
         Adapters.UTF_8.writeBits(this.bar, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.title = Adapters.UTF_8.readBits(buffer).orElse(null);
         this.bar = Adapters.UTF_8.readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.title).ifPresent(value -> nbt.put("title", value));
            Adapters.UTF_8.writeNbt(this.bar).ifPresent(value -> nbt.put("progress", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.title = Adapters.UTF_8.readNbt(nbt.get("title")).orElse(null);
         this.bar = Adapters.UTF_8.readNbt(nbt.get("progress")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.title).ifPresent(value -> json.add("title", value));
            Adapters.UTF_8.writeJson(this.bar).ifPresent(value -> json.add("progress", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.title = Adapters.UTF_8.readJson(json.get("title")).orElse(null);
         this.bar = Adapters.UTF_8.readJson(json.get("progress")).orElse(null);
      }
   }

   public static class Complete extends TaskRenderer<CompleteGodAltarTask, GodAltarRendererContext> {
      public void render(CompleteGodAltarTask task, GodAltarRendererContext context) {
         if (context.isCompleted()) {
            context.renderHeader("Drain the Altar", context.isWorld());
         }
      }
   }

   public static class Node extends TaskRenderer<NodeTask, GodAltarRendererContext> {
      public void render(NodeTask task, GodAltarRendererContext context) {
         for (NodeTask child : task.getChildren()) {
            child.render(context);
         }

         task.getDelegate().render(context);
      }
   }

   public static class Timed extends TaskRenderer<TimedTask, GodAltarRendererContext> {
      public void render(TimedTask task, GodAltarRendererContext context) {
         long elapsed = task.getElapsed();
         long duration = task.getDuration();
         context.renderTimerBar((double)(duration - elapsed) / duration, UIHelper.formatTimeString(duration - elapsed));
      }
   }
}
