package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.task.ConfiguredTask;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import iskallia.vault.task.renderer.CardTaskRenderer;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.util.TaskProgress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TaskLootCardModifier extends CardModifier<TaskLootCardModifier.Config> {
   private UUID uuid;
   private Task task;
   private final Map<Integer, Integer> counts = new LinkedHashMap<>();

   public TaskLootCardModifier() {
      super(new TaskLootCardModifier.Config());
   }

   public TaskLootCardModifier(TaskLootCardModifier.Config config) {
      super(config);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setTask(Task task) {
      this.task = task;
   }

   public Task getTask() {
      return this.task;
   }

   @Override
   public boolean onPopulate() {
      if (!super.onPopulate()) {
         return false;
      } else {
         this.uuid = Mth.createInsecureUUID();
         JavaRandom random = JavaRandom.ofNanoTime();
         this.task = ModConfigs.CARD_TASKS.getRandom(this.getConfig().task, random).map(task -> task.copy()).orElse(null);
         if (this.task != null) {
            this.task.streamSelfAndDescendants(ConfiguredTask.class).forEach(other -> {
               other.onPopulate(TaskContext.of(EntityTaskSource.ofUuids(random), ServerLifecycleHooks.getCurrentServer()));
               other.setPopulated(true);
               if (other instanceof ProgressConfiguredTask<?, ?> progressTask) {
                  progressTask.getCounter().setPopulated(true);
               }
            });
         }

         this.counts.clear();
         this.getConfig().count.forEach((tier, roll) -> this.counts.put(tier, roll.get(random)));
         return true;
      }
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected, int tier) {
      super.onInventoryTick(world, entity, slot, selected, tier);
      this.task.streamSelfAndDescendants(ProgressConfiguredTask.class).forEach(task -> {
         if (task.getCounter() instanceof TargetTaskCounter<?, ?> counter && counter.isPopulated()) {
            counter.get("targetTierContribution", Adapters.DOUBLE).ifPresent(contribution -> {
               if (counter.getBaseTarget() instanceof Integer base) {
                  counter.setTarget((int)(base.intValue() + (tier - 1) * contribution * base.intValue()));
               }
            });
         }
      });
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getSnapshotAttributes(int tier) {
      return Collections.emptyList();
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int tier) {
      super.addText(tooltip, minIndex, flag, time, tier);
      Component[] text = new Component[]{this.getConfig().tooltip};
      if (this.task.getRenderer() instanceof CardTaskRenderer renderer && renderer.getTooltip() instanceof TextComponent taskTooltip) {
         text[0] = this.replace(text[0], "${task}", taskTooltip);
      }

      text[0] = this.replace(text[0], "${count}", new TextComponent(String.valueOf(this.counts.get(tier))));
      this.task.streamSelfAndDescendants().filter(t -> t instanceof ProgressConfiguredTask).findFirst().ifPresent(task -> {
         TaskProgress progress = ((ProgressConfiguredTask)task).getCounter().getProgress();
         text[0] = this.replace(text[0], "${current}", new TextComponent(progress.getCurrent().toString()));
         text[0] = this.replace(text[0], "${target}", new TextComponent(progress.getTarget().toString()));
      });
      tooltip.add(text[0]);
   }

   public Component replace(Component component, String target, TextComponent replacement) {
      if (!(component instanceof TextComponent base)) {
         return component;
      } else {
         List<Component> siblings = base.getSiblings();
         siblings.add(0, base.plainCopy().setStyle(base.getStyle()));

         for (int result = 0; result < siblings.size(); result++) {
            Component sibling = siblings.get(result);
            if (sibling instanceof TextComponent) {
               String text = ((TextComponent)sibling).getText();
               if (!text.isEmpty()) {
                  List<Component> parts = new ArrayList<>();
                  Style styledReplacement = replacement.getStyle() == Style.EMPTY ? sibling.getStyle() : Style.EMPTY;
                  if (text.equals(target)) {
                     parts.add(replacement.plainCopy().withStyle(styledReplacement));
                  } else {
                     for (String raw : text.split(Pattern.quote(target))) {
                        parts.add(new TextComponent(raw).setStyle(sibling.getStyle()));
                        parts.add(replacement.plainCopy().withStyle(styledReplacement));
                     }

                     parts.remove(parts.size() - 1);
                  }

                  siblings.remove(result);

                  for (int j = 0; j < parts.size(); j++) {
                     siblings.add(result, parts.get(parts.size() - j - 1));
                  }
               }
            }
         }

         TextComponent resultx = new TextComponent("");
         resultx.setStyle(base.getStyle());

         for (Component sibling : siblings) {
            resultx.append(sibling);
         }

         return resultx;
      }
   }

   @Override
   public int getHighlightColor() {
      return this.getConfig().highlightColor;
   }

   public List<ItemStack> generateLoot(int tier, RandomSource random) {
      List<ItemStack> items = new ArrayList<>();
      CardEntry.getForTier(this.counts, tier).ifPresent(count -> {
         for (int i = 0; i < count; i++) {
            this.getConfig().loot.getRandom(random).ifPresent(entry -> items.addAll(entry.getStack(random)));
         }
      });
      return items;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         Adapters.UUID.writeBits(this.uuid, buffer);
         Adapters.TASK.writeBits(this.task, buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.counts.size()), buffer);
         this.counts.forEach((tier, count) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(count, buffer);
         });
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
         this.task = Adapters.TASK.readBits(buffer).orElseThrow();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.counts.clear();

         for (int i = 0; i < size; i++) {
            this.counts.put(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(), Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
            Adapters.TASK.writeNbt(this.task).ifPresent(tag -> nbt.put("task", tag));
            ListTag counts = new ListTag();
            this.counts.forEach((tier, count) -> {
               CompoundTag entry = new CompoundTag();
               Adapters.INT_SEGMENTED_3.writeNbt(tier).ifPresent(tag -> entry.put("tier", tag));
               Adapters.INT_SEGMENTED_3.writeNbt(count).ifPresent(tag -> entry.put("count", tag));
               counts.add(entry);
            });
            nbt.put("counts", counts);
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();
         this.task = Adapters.TASK.readNbt(nbt.get("task")).orElseThrow();
         ListTag count = nbt.getList("counts", 10);
         this.counts.clear();

         for (int i = 0; i < count.size(); i++) {
            this.counts
               .put(
                  Adapters.INT_SEGMENTED_3.readNbt(count.getCompound(i).get("tier")).orElseThrow(),
                  Adapters.INT_SEGMENTED_3.readNbt(count.getCompound(i).get("count")).orElseThrow()
               );
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            Adapters.UUID.writeJson(this.uuid).ifPresent(tag -> json.add("uuid", tag));
            Adapters.TASK.writeJson(this.task).ifPresent(tag -> json.add("task", tag));
            JsonArray counts = new JsonArray();
            this.counts.forEach((tier, count) -> {
               JsonObject entry = new JsonObject();
               Adapters.INT_SEGMENTED_3.writeJson(tier).ifPresent(tag -> entry.add("tier", tag));
               Adapters.INT_SEGMENTED_3.writeJson(count).ifPresent(tag -> entry.add("count", tag));
               counts.add(entry);
            });
            json.add("counts", counts);
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.uuid = Adapters.UUID.readJson(json.get("uuid")).orElseThrow();
         this.task = Adapters.TASK.readJson(json.get("task")).orElseThrow();
         JsonArray counts = json.getAsJsonArray("counts");
         this.counts.clear();

         for (int i = 0; i < counts.size(); i++) {
            JsonObject entry = counts.get(i).getAsJsonObject();
            this.counts
               .put(Adapters.INT_SEGMENTED_3.readJson(entry.get("tier")).orElseThrow(), Adapters.INT_SEGMENTED_3.readJson(entry.get("count")).orElseThrow());
         }
      }
   }

   public static class Config extends CardModifier.Config {
      private LootPool loot;
      private String task;
      private final Map<Integer, IntRoll> count = new LinkedHashMap<>();
      private Component tooltip;
      private int highlightColor;

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.LOOT_POOL.writeBits(this.loot, buffer);
         Adapters.UTF_8.writeBits(this.task, buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.count.size()), buffer);
         this.count.forEach((tier, roll) -> {
            Adapters.INT_SEGMENTED_3.writeBits(tier, buffer);
            Adapters.INT_ROLL.writeBits(roll, buffer);
         });
         Adapters.COMPONENT.writeBits(this.tooltip, buffer);
         Adapters.INT.writeBits(Integer.valueOf(this.highlightColor), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.loot = Adapters.LOOT_POOL.readBits(buffer).orElseThrow();
         this.task = Adapters.UTF_8.readBits(buffer).orElseThrow();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.count.clear();

         for (int i = 0; i < size; i++) {
            this.count.put(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(), Adapters.INT_ROLL.readBits(buffer).orElseThrow());
         }

         this.tooltip = Adapters.COMPONENT.readBits(buffer).orElseThrow();
         this.highlightColor = Adapters.INT.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.LOOT_POOL.writeNbt(this.loot).ifPresent(tag -> nbt.put("loot", tag));
            Adapters.UTF_8.writeNbt(this.task).ifPresent(tag -> nbt.put("task", tag));
            ListTag count = new ListTag();
            this.count.forEach((tier, roll) -> {
               CompoundTag entry = new CompoundTag();
               Adapters.INT_SEGMENTED_3.writeNbt(tier).ifPresent(tag -> entry.put("tier", tag));
               Adapters.INT_ROLL.writeNbt(roll).ifPresent(tag -> entry.put("roll", tag));
               count.add(entry);
            });
            nbt.put("count", count);
            Adapters.COMPONENT.writeNbt(this.tooltip).ifPresent(tag -> nbt.put("tooltip", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.highlightColor)).ifPresent(tag -> nbt.put("highlightColor", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.loot = Adapters.LOOT_POOL.readNbt(nbt.getList("loot", 10)).orElseThrow();
         this.task = Adapters.UTF_8.readNbt(nbt.get("task")).orElseThrow();
         ListTag count = nbt.getList("count", 10);
         this.count.clear();

         for (int i = 0; i < count.size(); i++) {
            this.count
               .put(
                  Adapters.INT_SEGMENTED_3.readNbt(count.getCompound(i).get("tier")).orElseThrow(),
                  Adapters.INT_ROLL.readNbt(count.getCompound(i).get("roll")).orElseThrow()
               );
         }

         this.tooltip = Adapters.COMPONENT.readNbt(nbt.get("tooltip")).orElseThrow();
         this.highlightColor = Adapters.INT.readNbt(nbt.get("highlightColor")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.LOOT_POOL.writeJson(this.loot).ifPresent(tag -> json.add("loot", tag));
            Adapters.UTF_8.writeJson(this.task).ifPresent(tag -> json.add("task", tag));
            JsonArray count = new JsonArray();
            this.count.forEach((tier, roll) -> {
               JsonObject entry = new JsonObject();
               Adapters.INT_SEGMENTED_3.writeJson(tier).ifPresent(tag -> entry.add("tier", tag));
               Adapters.INT_ROLL.writeJson(roll).ifPresent(tag -> entry.add("roll", tag));
               count.add(entry);
            });
            json.add("count", count);
            Adapters.COMPONENT.writeJson(this.tooltip).ifPresent(tag -> json.add("tooltip", tag));
            Adapters.INT.writeJson(Integer.valueOf(this.highlightColor)).ifPresent(tag -> json.add("highlightColor", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.loot = Adapters.LOOT_POOL.readJson(json.getAsJsonArray("loot")).orElseThrow();
         this.task = Adapters.UTF_8.readJson(json.get("task")).orElseThrow();
         JsonArray count = json.getAsJsonArray("count");
         this.count.clear();

         for (int i = 0; i < count.size(); i++) {
            JsonObject entry = count.get(i).getAsJsonObject();
            this.count.put(Adapters.INT_SEGMENTED_3.readJson(entry.get("tier")).orElseThrow(), Adapters.INT_ROLL.readJson(entry.get("roll")).orElseThrow());
         }

         this.tooltip = Adapters.COMPONENT.readJson(json.get("tooltip")).orElseThrow();
         this.highlightColor = Adapters.INT.readJson(json.get("highlightColor")).orElseThrow();
      }
   }
}
