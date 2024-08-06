package iskallia.vault.task;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.renderer.TaskRenderer;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Task implements ISerializable<CompoundTag, JsonObject> {
   protected String id;
   protected Task parent;
   protected TaskRenderer<?, ?> renderer;

   @Nullable
   public String getId() {
      return this.id;
   }

   public <T extends Task> T setId(String id) {
      this.id = id;
      return (T)this;
   }

   public Task getParent() {
      return this.parent;
   }

   public void setParent(Task parent) {
      this.parent = parent;
   }

   public abstract Iterable<Task> getChildren();

   public Iterable<Task> getSelfAndChildren() {
      return Iterables.concat(Collections.singleton(this), this.getChildren());
   }

   public <T> Iterable<T> getChildren(Class<T> type) {
      return Iterables.filter(this.getChildren(), type);
   }

   public <T> Iterable<T> getSelfAndChildren(Class<T> type) {
      return Iterables.filter(this.getSelfAndChildren(), type);
   }

   public Stream<Task> streamChildren() {
      return Streams.stream(this.getChildren());
   }

   public Stream<Task> streamSelfAndChildren() {
      return Streams.stream(this.getSelfAndChildren());
   }

   public <T> Stream<T> streamChildren(Class<T> type) {
      return Streams.stream(this.getChildren(type));
   }

   public <T> Stream<T> streamSelfAndChildren(Class<T> type) {
      return Streams.stream(this.getSelfAndChildren(type));
   }

   public Iterable<Task> getDescendants() {
      List<Task> flattened = new ArrayList<>();

      for (Task child : this.getChildren()) {
         flattened.add(child);
         child.getDescendants().forEach(flattened::add);
      }

      return flattened;
   }

   public Iterable<Task> getSelfAndDescendants() {
      return Iterables.concat(Collections.singleton(this), this.getDescendants());
   }

   public <T> Iterable<T> getDescendants(Class<T> type) {
      return Iterables.filter(this.getDescendants(), type);
   }

   public <T> Iterable<T> getSelfAndDescendants(Class<T> type) {
      return Iterables.filter(this.getSelfAndDescendants(), type);
   }

   public Stream<Task> streamDescendants() {
      return Streams.stream(this.getDescendants());
   }

   public Stream<Task> streamSelfAndDescendants() {
      return Streams.stream(this.getSelfAndDescendants());
   }

   public <T> Stream<T> streamDescendants(Class<T> type) {
      return Streams.stream(this.getDescendants(type));
   }

   public <T> Stream<T> streamSelfAndDescendants(Class<T> type) {
      return Streams.stream(this.getSelfAndDescendants(type));
   }

   public <T extends Task, C extends RendererContext> TaskRenderer<T, C> getRenderer() {
      return (TaskRenderer<T, C>)(this.renderer != null ? this.renderer : TaskRenderer.PASS);
   }

   public <T extends Task, C extends RendererContext> T setRenderer(TaskRenderer<T, C> renderer) {
      this.renderer = renderer;
      return (T)this;
   }

   public abstract boolean isCompleted();

   public void onAttach(TaskContext context) {
      this.getChildren().forEach(task -> task.onAttach(context));
   }

   public void onDetach() {
      CommonEvents.release(this);
      this.getChildren().forEach(Task::onDetach);
   }

   @OnlyIn(Dist.CLIENT)
   public void onRender(RendererContext context) {
      this.<Task, RendererContext>getRenderer().onRender(this, context);
   }

   @OnlyIn(Dist.CLIENT)
   public void onMouseMoved(RendererContext context) {
      this.<Task, RendererContext>getRenderer().onMouseMoved(this, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseClicked(int button, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onMouseClicked(this, button, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseReleased(int button, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onMouseReleased(this, button, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseDragged(int button, double dragX, double dragY, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onMouseDragged(this, button, dragX, dragY, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseScrolled(double delta, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onMouseScrolled(this, delta, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onKeyPressed(this, keyCode, scanCode, modifiers, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onKeyReleased(int keyCode, int scanCode, int modifiers, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onKeyReleased(this, keyCode, scanCode, modifiers, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onCharTyped(char codePoint, int modifiers, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onCharTyped(this, codePoint, modifiers, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onChangeFocus(boolean focus, RendererContext context) {
      return this.<Task, RendererContext>getRenderer().onChangeFocus(this, focus, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isMouseOver(RendererContext context) {
      return this.<Task, RendererContext>getRenderer().isMouseOver(this, context);
   }

   @OnlyIn(Dist.CLIENT)
   public void onUpdateNarration(NarrationElementOutput output, RendererContext context) {
      this.<Task, RendererContext>getRenderer().onUpdateNarration(this, output, context);
   }

   @OnlyIn(Dist.CLIENT)
   public NarrationPriority getNarrationPriority(RendererContext context) {
      return this.<Task, RendererContext>getRenderer().getNarrationPriority(this, context);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isActive(RendererContext context) {
      return this.<Task, RendererContext>getRenderer().isActive(this, context);
   }

   public <T extends Task> T copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      Adapters.TASK.writeBits(this, buffer);
      buffer.setPosition(0);
      return (T)Adapters.TASK.readBits(buffer).orElseThrow();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.UTF_8.asNullable().writeBits(this.id, buffer);
      Adapters.TASK_RENDERER.get(this).writeBits(this.renderer, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.id = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
      this.renderer = Adapters.TASK_RENDERER.get(this).readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UTF_8.asNullable().writeNbt(this.id).ifPresent(value -> nbt.put("id", value));
      Adapters.TASK_RENDERER.get(this).writeNbt(this.renderer).ifPresent(value -> nbt.put("renderer", value));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.id = Adapters.UTF_8.asNullable().readNbt(nbt.get("id")).orElse(null);
      this.renderer = Adapters.TASK_RENDERER.get(this).readNbt(nbt.get("renderer")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.UTF_8.asNullable().writeJson(this.id).ifPresent(value -> json.add("id", value));
      Adapters.TASK_RENDERER.get(this).writeJson(this.renderer).ifPresent(value -> json.add("renderer", value));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.id = Adapters.UTF_8.asNullable().readJson(json.get("id")).orElse(null);
      this.renderer = Adapters.TASK_RENDERER.get(this).readJson(json.get("renderer")).orElse(null);
   }

   public static class Adapter extends TypeSupplierAdapter<Task> {
      public Adapter() {
         super("type", false);
         this.register("timed", TimedTask.class, TimedTask::new);
         this.register("achievement", AchievementTask.class, AchievementTask::new);
         this.register("in_vault", InVaultTask.class, InVaultTask::new);
         this.register("bingo", BingoTask.class, BingoTask::new);
         this.register("god_altar", GodAltarTask.class, GodAltarTask::new);
         this.register("kill_entity", KillEntityTask.class, KillEntityTask::new);
         this.register("loot_chest", LootChestTask.class, LootChestTask::new);
         this.register("loot_chest_item", LootChestItemTask.class, LootChestItemTask::new);
         this.register("mine_block", MineBlockTask.class, MineBlockTask::new);
         this.register("interact_block", InteractBlockTask.class, InteractBlockTask::new);
         this.register("crafting", CraftingTask.class, CraftingTask::new);
         this.register("item_stat", ItemStatTask.class, ItemStatTask::new);
         this.register("transmog_reward", DiscoverTransmogTask.class, DiscoverTransmogTask::new);
         this.register("item_reward", ItemRewardTask.class, ItemRewardTask::new);
         this.register("achievement_complete", AchievementCompleteTask.class, AchievementCompleteTask::new);
         this.register("player_vault_level", PlayerVaultLevelTask.class, PlayerVaultLevelTask::new);
         this.register("add_vault_modifier", AddVaultModifierTask.class, AddVaultModifierTask::new);
         this.register("take_no_damage", TakeNoDamageTask.class, TakeNoDamageTask::new);
         this.register("deal_no_damage", DealNoDamageTask.class, DealNoDamageTask::new);
         this.register("use_no_mana", UseNoManaTask.class, UseNoManaTask::new);
         this.register("find_vault_room", FindVaultRoomTask.class, FindVaultRoomTask::new);
      }

      @Nullable
      protected Task readSuppliedJson(JsonElement json) {
         if (json instanceof JsonObject object) {
            String type = object.get(this.key).getAsString();
            String[] types = type.split(Pattern.quote("/"));
            if (types.length > 1) {
               Task root = null;
               Task leaf = null;
               TaskRenderer<?, ?> renderer = null;

               for (int i = 0; i < types.length; i++) {
                  String t = types[i];
                  Task other = this.getValue(t);
                  if (other != null) {
                     other.readJson(object);
                     renderer = other.getRenderer();
                     other.setId(null);
                     other.setRenderer(null);
                     if (leaf != null) {
                        if (leaf instanceof NodeTask node) {
                           node.addChildren(other);
                        }
                     } else {
                        root = other;
                     }

                     leaf = other;
                  }
               }

               leaf.setRenderer(renderer);
               return root;
            }
         }

         return (Task)super.readSuppliedJson(json);
      }
   }

   public static class NbtAdapter implements ISimpleAdapter<Task, Tag, JsonElement> {
      public void writeBits(@Nullable Task task, BitBuffer buffer) {
         Adapters.GENERIC_NBT.asNullable().writeBits(this.writeNbt(task).orElse(null), buffer);
      }

      @Override
      public Optional<Task> readBits(BitBuffer buffer) {
         return Adapters.GENERIC_NBT.asNullable().readBits(buffer).flatMap(this::readNbt);
      }

      public Optional<Tag> writeNbt(@Nullable Task task) {
         return Adapters.TASK.writeNbt(task);
      }

      @Override
      public Optional<Task> readNbt(@Nullable Tag nbt) {
         return Adapters.TASK.readNbt(nbt);
      }

      public Optional<JsonElement> writeJson(@Nullable Task task) {
         return Adapters.GENERIC_NBT.asNullable().writeJson(this.writeNbt(task).orElse(null));
      }

      @Override
      public Optional<Task> readJson(@Nullable JsonElement json) {
         return Adapters.GENERIC_NBT.asNullable().readJson(json).flatMap(this::readNbt);
      }
   }
}
