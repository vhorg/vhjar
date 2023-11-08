package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.CompleteGodAltarTask;
import iskallia.vault.task.NodeTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TaskRenderer<T extends Task, C extends RendererContext> implements ISerializable<CompoundTag, JsonObject> {
   @OnlyIn(Dist.CLIENT)
   public abstract void render(T var1, C var2);

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
         this.register("god_altar", GodAltarRenderer.Base.class, GodAltarRenderer.Base::new);
         this.register("god_altar", GodAltarRenderer.Timed.class, GodAltarRenderer.Timed::new, TimedTask.class);
         this.register("god_altar", GodAltarRenderer.Node.class, GodAltarRenderer.Node::new, NodeTask.class);
         this.register("god_altar", GodAltarRenderer.Complete.class, GodAltarRenderer.Complete::new, CompleteGodAltarTask.class);
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
