package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.AchievementTask;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.CraftingTask;
import iskallia.vault.task.DiscoverTransmogTask;
import iskallia.vault.task.GodAltarTask;
import iskallia.vault.task.ItemRewardTask;
import iskallia.vault.task.KillEntityTask;
import iskallia.vault.task.MineBlockTask;
import iskallia.vault.task.MultiVaultTask;
import iskallia.vault.task.PlayerVaultLevelTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TaskRenderer<T extends Task, C extends RendererContext> implements ISerializable<CompoundTag, JsonObject> {
   public static TaskRenderer<?, ?> PASS = new TaskRenderer();

   @OnlyIn(Dist.CLIENT)
   public void onRender(T task, C context) {
      for (Task child : task.getChildren()) {
         child.onRender(context);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void onMouseMoved(T task, C context) {
      for (Task child : task.getChildren()) {
         child.onMouseMoved(context);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseClicked(T task, int button, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onMouseClicked(button, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseReleased(T task, int button, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onMouseReleased(button, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseDragged(T task, int button, double dragX, double dragY, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onMouseDragged(button, dragX, dragY, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onMouseScrolled(T task, double delta, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onMouseScrolled(delta, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onKeyPressed(T task, int keyCode, int scanCode, int modifiers, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onKeyPressed(keyCode, scanCode, modifiers, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onKeyReleased(T task, int keyCode, int scanCode, int modifiers, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onKeyReleased(keyCode, scanCode, modifiers, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onCharTyped(T task, char codePoint, int modifiers, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onCharTyped(codePoint, modifiers, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onChangeFocus(T task, boolean focus, C context) {
      boolean result = false;

      for (Task child : task.getChildren()) {
         result |= child.onChangeFocus(focus, context);
      }

      return result;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isMouseOver(T task, C context) {
      for (Task child : task.getChildren()) {
         if (child.isMouseOver(context)) {
            return true;
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void onUpdateNarration(T task, NarrationElementOutput output, C context) {
   }

   @OnlyIn(Dist.CLIENT)
   public NarrationPriority getNarrationPriority(T task, C context) {
      return NarrationPriority.NONE;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isActive(T task, C context) {
      return true;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
   }

   @Override
   public void readBits(BitBuffer buffer) {
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag());
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject());
   }

   public void readJson(JsonObject json) {
   }

   public static class Adapter {
      private Map<Class<? extends Task>, TypeSupplierAdapter<TaskRenderer<?, ?>>> children;

      public Adapter() {
         this.initialize();
         this.register("god_altar", GodAltarRenderer.Child.class, GodAltarRenderer.Child::new);
         this.register("god_altar", GodAltarRenderer.Root.class, GodAltarRenderer.Root::new, GodAltarTask.class);
         this.register("achievement", AchievementRenderer.Base.class, AchievementRenderer.Base::new);
         this.register("achievement", AchievementRenderer.Root.class, AchievementRenderer.Root::new, AchievementTask.class);
         this.register("achievement", KillEntityTaskRenderer.Achievement.class, KillEntityTaskRenderer.Achievement::new, KillEntityTask.class);
         this.register("achievement", CraftingTaskRenderer.Achievement.class, CraftingTaskRenderer.Achievement::new, CraftingTask.class);
         this.register("achievement", MineBlockTaskRenderer.Achievement.class, MineBlockTaskRenderer.Achievement::new, MineBlockTask.class);
         this.register("achievement", TransmogRewardRenderer.Achievement.class, TransmogRewardRenderer.Achievement::new, DiscoverTransmogTask.class);
         this.register("achievement", ItemRewardRenderer.Achievement.class, ItemRewardRenderer.Achievement::new, ItemRewardTask.class);
         this.register("achievement", InVaultTaskRenderer.Achievement.class, InVaultTaskRenderer.Achievement::new, MultiVaultTask.class);
         this.register("achievement", TimedTaskRenderer.Achievement.class, TimedTaskRenderer.Achievement::new, TimedTask.class);
         this.register("achievement", PlayerVaultLevelTaskRenderer.Achievement.class, PlayerVaultLevelTaskRenderer.Achievement::new, PlayerVaultLevelTask.class);
         this.register("bingo", BingoRenderer.Leaf.class, BingoRenderer.Leaf::new);
         this.register("bingo", BingoRenderer.Root.class, BingoRenderer.Root::new, BingoTask.class);
         this.register("card", CardTaskRenderer.class, CardTaskRenderer::new);
         this.register("team", TeamRenderer.class, TeamRenderer::new);
      }

      private void initialize() {
         this.children = new HashMap<>();
         Adapters.TASK.getClasses().forEach(task -> this.children.put((Class<? extends Task>)task, new TypeSupplierAdapter<>("type", true)));
      }

      public TypeSupplierAdapter<TaskRenderer<?, ?>> get(Task task) {
         return this.children.get(task.getClass());
      }

      public <T extends TaskRenderer<?, ?>> void register(String id, Class<? extends T> type, Supplier<? extends T> supplier) {
         this.children.forEach((task, adapter) -> adapter.register(id, type, supplier));
      }

      public <T extends TaskRenderer<?, ?>> void register(String id, Class<? extends T> type, Supplier<? extends T> supplier, Class<?>... tasks) {
         for (Class<?> task : tasks) {
            this.children.get(task).register(id, type, supplier);
         }
      }
   }
}
