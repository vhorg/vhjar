package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class NodeTask extends Task {
   protected List<Task> children = new ArrayList<>();
   private static final ArrayAdapter<Task> CHILDREN = Adapters.ofArray(Task[]::new, Adapters.TASK);

   @Override
   public Iterable<Task> getChildren() {
      return this.children;
   }

   public <P extends Task, C extends Task> P setChildren(C... children) {
      this.children.clear();
      this.addChildren(children);
      return (P)this;
   }

   public <P extends Task, C extends Task> P setChildren(List<C> children) {
      this.children.clear();
      this.addChildren(children);
      return (P)this;
   }

   public <P extends Task, C extends Task> P addChildren(C... children) {
      this.addChildren(Arrays.asList(children));
      return (P)this;
   }

   public <P extends Task, C extends Task> P addChildren(List<C> children) {
      for (C child : children) {
         this.children.add(child);
         child.parent = this;
      }

      return (P)this;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      CHILDREN.writeBits(this.children.toArray(Task[]::new), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.setChildren(CHILDREN.readBits(buffer).orElseThrow());
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         CHILDREN.writeNbt(this.children.toArray(Task[]::new)).ifPresent(value -> nbt.put("children", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.setChildren(CHILDREN.readNbt(nbt.get("children")).orElse(new Task[0]));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         CHILDREN.writeJson(this.children.toArray(Task[]::new)).ifPresent(value -> json.add("children", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.setChildren(CHILDREN.readJson(json.get("children")).orElse(new Task[0]));
   }
}
