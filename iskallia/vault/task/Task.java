package iskallia.vault.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.renderer.TaskRenderer;
import iskallia.vault.task.renderer.context.RendererContext;
import iskallia.vault.task.source.TaskSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public abstract class Task implements ISerializable<CompoundTag, JsonObject> {
   protected List<TaskRenderer<?, ?>> renderer = new ArrayList<>();

   public abstract boolean isCompleted(TaskSource var1);

   public void onAttach(TaskSource source) {
   }

   public void onStart(TaskSource source) {
      this.onAttach(source);
   }

   public void onTick(TaskSource source) {
   }

   public void onStop(TaskSource source) {
      this.onDetach();
   }

   public void onDetach() {
   }

   public <T extends Task> T add(TaskRenderer<?, ?> renderer) {
      this.renderer.add(renderer);
      return (T)this;
   }

   @OnlyIn(Dist.CLIENT)
   public <C extends RendererContext> void render(C context) {
      for (TaskRenderer<?, ?> instance : this.renderer) {
         ((TaskRenderer<?, C>)instance).render(this, context);
      }
   }

   public <T extends Task> T copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      Adapters.TASK.writeBits(this, buffer);
      buffer.setPosition(0);
      return (T)Adapters.TASK.readBits(buffer).orElseThrow();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.renderer.size()), buffer);

      for (TaskRenderer<?, ?> instance : this.renderer) {
         Adapters.TASK_RENDERER.get(this).writeBits(instance, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         this.renderer.add(Adapters.TASK_RENDERER.get(this).readBits(buffer).orElse(null));
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      if (this.renderer.size() == 1) {
         Adapters.TASK_RENDERER.get(this).writeNbt(this.renderer.get(0)).ifPresent(value -> nbt.put("renderer", value));
      } else if (this.renderer.size() > 1) {
         ListTag list = new ListTag();

         for (TaskRenderer<?, ?> instance : this.renderer) {
            Adapters.TASK_RENDERER.get(this).writeNbt(instance).ifPresent(list::add);
         }

         nbt.put("renderer", list);
      }

      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.renderer = new ArrayList<>();
      if (nbt.get("renderer") instanceof CompoundTag tag) {
         this.renderer.add(Adapters.TASK_RENDERER.get(this).readNbt(tag).orElse(null));
      } else if (nbt.get("renderer") instanceof ListTag tag && tag.getElementType() == 10) {
         for (int i = 0; i < tag.size(); i++) {
            this.renderer.add(Adapters.TASK_RENDERER.get(this).readNbt(tag.getCompound(i)).orElse(null));
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      if (this.renderer.size() == 1) {
         Adapters.TASK_RENDERER.get(this).writeJson(this.renderer.get(0)).ifPresent(value -> json.add("renderer", value));
      } else if (this.renderer.size() > 1) {
         JsonArray list = new JsonArray();

         for (TaskRenderer<?, ?> instance : this.renderer) {
            Adapters.TASK_RENDERER.get(this).writeJson(instance).ifPresent(list::add);
         }

         json.add("renderer", list);
      }

      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.renderer.clear();
      if (json.get("renderer") instanceof JsonObject object) {
         this.renderer.add(Adapters.TASK_RENDERER.get(this).readJson(object).orElse(null));
      } else if (json.get("renderer") instanceof JsonArray array) {
         for (int i = 0; i < array.size(); i++) {
            this.renderer.add(Adapters.TASK_RENDERER.get(this).readJson(array.get(i)).orElse(null));
         }
      }
   }

   public static class Adapter extends TypeSupplierAdapter<Task> {
      public Adapter() {
         super("type", false);
         this.register("node", NodeTask.class, NodeTask::new);
         this.register("timed", TimedTask.class, TimedTask::new);
         this.register("bounty", BountyTask.class, BountyTask::new);
         this.register("achievement", AchievementTask.class, AchievementTask::new);
         this.register("kill_entity", KillEntityTask.class, KillEntityTask::new);
         this.register("loot_chest", LootChestTask.class, LootChestTask::new);
         this.register("loot_chest_item", LootChestItemTask.class, LootChestItemTask::new);
         this.register("mine_block", MineBlockTask.class, MineBlockTask::new);
         this.register("interact_block", InteractBlockTask.class, InteractBlockTask::new);
         this.register("crafting", CraftingTask.class, CraftingTask::new);
         this.register("item_stat", ItemStatTask.class, ItemStatTask::new);
         this.register("complete_god_altar", CompleteGodAltarTask.class, CompleteGodAltarTask::new);
         this.register("fail_god_altar", FailGodAltarTask.class, FailGodAltarTask::new);
      }

      @Nullable
      protected Task readSuppliedJson(JsonElement json) {
         Task task = (Task)super.readSuppliedJson(json);
         if (json instanceof JsonObject object && !(task instanceof NodeTask) && object.has("children")) {
            NodeTask node = new NodeTask(task);
            node.readJson(object);
            return node;
         } else {
            return task;
         }
      }
   }
}
