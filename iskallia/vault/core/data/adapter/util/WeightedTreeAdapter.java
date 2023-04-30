package iskallia.vault.core.data.adapter.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.item.crystal.data.adapter.IAdapter;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.HashSet;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;

public abstract class WeightedTreeAdapter<T, W extends WeightedTree<T>> implements ISimpleAdapter<W, ListTag, JsonArray> {
   public abstract W create();

   public abstract String getName(T var1);

   public abstract IAdapter getAdapter(String var1);

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
               this.getAdapter(name).writeJson(branch, null).ifPresent(element -> object.add("name", (JsonElement)element));
            }
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
            int weight = element.get("weight").getAsInt();
            HashSet<String> keys = new HashSet<>(element.keySet());
            keys.remove("weight");
            String name = keys.iterator().next();
            if (name.equals("pool")) {
               this.readJson(element.get(name).getAsJsonArray()).ifPresent(w -> tree.addTree(w, (double)weight));
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
