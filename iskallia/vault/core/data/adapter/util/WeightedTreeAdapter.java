package iskallia.vault.core.data.adapter.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.item.crystal.data.adapter.IAdapter;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.HashSet;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public abstract class WeightedTreeAdapter<T, W extends WeightedTree<T>> implements ISimpleAdapter<W, ListTag, JsonArray> {
   public abstract W create();

   public abstract String getName(T var1);

   public abstract IAdapter getAdapter(String var1);

   public void writeBits(@Nullable W value, BitBuffer buffer) {
      if (value == null) {
         buffer.writeBoolean(false);
      } else {
         buffer.writeBoolean(true);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.getChildren().size()), buffer);
         value.getChildren().forEach((branch, weight) -> {
            buffer.writeBoolean(branch instanceof WeightedTree);
            buffer.writeDouble(weight);
            if (branch instanceof WeightedTree) {
               this.writeBits((W)branch, buffer);
            } else {
               String name = this.getName((T)branch);
               buffer.writeString(name);
               this.getAdapter(name).writeBits(branch, buffer, null);
            }
         });
      }
   }

   @Override
   public Optional<W> readBits(BitBuffer buffer) {
      if (!buffer.readBoolean()) {
         return Optional.empty();
      } else {
         int groupSize = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         W tree = this.create();

         for (int i = 0; i < groupSize; i++) {
            boolean isTree = buffer.readBoolean();
            double weight = buffer.readDouble();
            if (isTree) {
               this.readBits(buffer).ifPresent(w -> tree.addTree(w, weight));
            } else {
               String name = buffer.readString();
               IAdapter adapter = this.getAdapter(name);
               if (adapter != null) {
                  adapter.readBits(buffer, null).ifPresent(o -> tree.addLeaf((T)o, weight));
               }
            }
         }

         return Optional.of(tree);
      }
   }

   public Optional<ListTag> writeNbt(@Nullable W value) {
      if (value == null) {
         return Optional.empty();
      } else {
         ListTag list = new ListTag();
         value.getChildren().forEach((branch, weight) -> {
            CompoundTag compound = new CompoundTag();
            compound.putDouble("weight", weight);
            if (branch instanceof WeightedTree) {
               this.writeNbt((W)branch).ifPresent(element -> compound.put("pool", element));
            } else {
               String name = this.getName((T)branch);
               this.getAdapter(name).writeNbt(branch, null).ifPresent(element -> compound.put(name, (Tag)element));
            }

            list.add(compound);
         });
         return Optional.of(list);
      }
   }

   public Optional<W> readNbt(@Nullable ListTag nbt) {
      if (nbt == null) {
         return Optional.empty();
      } else {
         W tree = this.create();

         for (int i = 0; i < nbt.size(); i++) {
            CompoundTag element = nbt.getCompound(i);
            double weight = element.getDouble("weight");
            HashSet<String> keys = new HashSet<>(element.getAllKeys());
            keys.remove("weight");
            String name = keys.iterator().next();
            if (name.equals("pool")) {
               this.readNbt(element.getList(name, 10)).ifPresent(w -> tree.addTree(w, weight));
            } else {
               IAdapter adapter = this.getAdapter(name);
               if (adapter != null) {
                  adapter.readNbt(element.get(name), null).ifPresent(o -> tree.addLeaf((T)o, weight));
               }
            }
         }

         return Optional.of(tree);
      }
   }

   public Optional<JsonArray> writeJson(@Nullable W value) {
      if (value == null) {
         return Optional.empty();
      } else {
         JsonArray array = new JsonArray();
         value.getChildren().forEach((branch, weight) -> {
            JsonObject object = new JsonObject();
            object.addProperty("weight", weight);
            if (branch instanceof WeightedTree) {
               this.writeJson((W)branch).ifPresent(element -> object.add("pool", element));
            } else {
               String name = this.getName((T)branch);
               this.getAdapter(name).writeJson(branch, null).ifPresent(element -> object.add(name, (JsonElement)element));
            }

            array.add(object);
         });
         return Optional.of(array);
      }
   }

   public Optional<W> readJson(@Nullable JsonArray json) {
      if (json == null) {
         return Optional.empty();
      } else {
         W tree = this.create();

         for (int i = 0; i < json.size(); i++) {
            JsonObject element = json.get(i).getAsJsonObject();
            double weight = element.get("weight").getAsDouble();
            HashSet<String> keys = new HashSet<>(element.keySet());
            keys.remove("weight");
            String name = keys.iterator().next();
            if (name.equals("pool")) {
               this.readJson(element.get(name).getAsJsonArray()).ifPresent(w -> tree.addTree(w, weight));
            } else {
               IAdapter adapter = this.getAdapter(name);
               if (adapter != null) {
                  adapter.readJson(element.get(name), null).ifPresent(o -> tree.addLeaf((T)o, weight));
               }
            }
         }

         return Optional.of(tree);
      }
   }
}
