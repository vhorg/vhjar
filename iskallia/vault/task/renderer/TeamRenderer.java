package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.TeamRendererContext;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TeamRenderer extends TaskRenderer<Task, TeamRendererContext> {
   public String name;
   public ResourceLocation icon;
   public ItemStack stack;

   public TeamRenderer() {
   }

   public TeamRenderer(String name, ResourceLocation icon, ItemStack stack) {
      this.name = name;
      this.icon = icon;
      this.stack = stack;
   }

   @OnlyIn(Dist.CLIENT)
   public void onRender(Task task, TeamRendererContext context) {
      for (Task t : task.getSelfAndChildren()) {
         if (t instanceof IProgressTask progressTask) {
            TaskProgress progress = progressTask.getProgress();
            String current = String.valueOf(progress.getCurrent().intValue());
            String target = String.valueOf(progress.getTarget().intValue());
            if (this.icon != null) {
               context.renderIcon(this.icon);
            }

            if (this.stack != null) {
               context.renderStack(this.stack);
            }

            context.renderNameAndProgress(this.name, current + "/" + target);
            break;
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UTF_8.writeBits(this.name, buffer);
      Adapters.IDENTIFIER.asNullable().writeBits(this.icon, buffer);
      Adapters.ITEM_STACK.asNullable().writeBits(this.stack, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.name = Adapters.UTF_8.readBits(buffer).orElse(null);
      this.icon = Adapters.IDENTIFIER.asNullable().readBits(buffer).orElse(null);
      this.stack = Adapters.ITEM_STACK.asNullable().readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UTF_8.writeNbt(this.name).ifPresent(value -> nbt.put("name", value));
         Adapters.IDENTIFIER.asNullable().writeNbt(this.icon).ifPresent(value -> nbt.put("icon", value));
         Adapters.ITEM_STACK.asNullable().writeNbt(this.stack).ifPresent(value -> nbt.put("stack", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
      this.icon = Adapters.IDENTIFIER.asNullable().readNbt(nbt.get("icon")).orElse(null);
      this.stack = Adapters.ITEM_STACK.asNullable().readNbt(nbt.get("stack")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UTF_8.writeJson(this.name).ifPresent(value -> json.add("name", value));
         Adapters.IDENTIFIER.asNullable().writeJson(this.icon).ifPresent(value -> json.add("icon", value));
         Adapters.ITEM_STACK.asNullable().writeJson(this.stack).ifPresent(value -> json.add("stack", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
      this.icon = Adapters.IDENTIFIER.asNullable().readJson(json.get("icon")).orElse(null);
      this.stack = Adapters.ITEM_STACK.asNullable().readJson(json.get("stack")).orElse(null);
   }
}
