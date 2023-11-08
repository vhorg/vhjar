package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.renderer.context.RendererContext;
import iskallia.vault.task.source.TaskSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class NodeTask extends OperableTask {
   private NodeTask parent;
   private List<NodeTask> children;
   private static final ArrayAdapter<NodeTask> CHILDREN = Adapters.ofArray(NodeTask[]::new, Adapters.TASK);

   public NodeTask() {
   }

   public NodeTask(Task delegate, NodeTask... children) {
      super(delegate);
      this.children = new ArrayList<>(Arrays.asList(children));
      this.children.forEach(child -> child.parent = this);
   }

   public NodeTask getParent() {
      return this.parent;
   }

   public List<NodeTask> getChildren() {
      return this.children;
   }

   @Override
   public <C extends RendererContext> void render(C context) {
      if (this.renderer.isEmpty()) {
         this.delegate.render(context);

         for (NodeTask child : this.children) {
            child.render(context);
         }
      } else {
         super.render(context);
      }
   }

   @Override
   public boolean shouldBeOperating(TaskSource source) {
      return super.shouldBeOperating(source) && (this.parent == null || this.parent.isCompleted(source));
   }

   @Override
   public void onAttach(TaskSource source) {
      super.onAttach(source);

      for (NodeTask child : this.children) {
         child.onAttach(source);
      }
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);

      for (NodeTask child : this.children) {
         child.onTick(source);
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();

      for (NodeTask child : this.children) {
         child.onDetach();
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      CHILDREN.writeBits(this.children.toArray(NodeTask[]::new), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.children = Arrays.stream(CHILDREN.readBits(buffer).orElseThrow()).toList();
      this.children.forEach(child -> child.parent = this);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         CHILDREN.writeNbt(this.children.toArray(NodeTask[]::new)).ifPresent(value -> nbt.put("children", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.children = Arrays.stream(CHILDREN.readNbt(nbt.get("children")).orElse(new NodeTask[0])).toList();
      this.children.forEach(child -> child.parent = this);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         CHILDREN.writeJson(this.children.toArray(NodeTask[]::new)).ifPresent(value -> json.add("children", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.children = Arrays.stream(CHILDREN.readJson(json.get("children")).orElse(new NodeTask[0])).toList();
      this.children.forEach(child -> child.parent = this);
   }
}
