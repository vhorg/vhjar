package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.CrystalEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CompoundCrystalLayout extends CrystalLayout {
   protected List<CrystalLayout> children;

   public CompoundCrystalLayout() {
      this.children = new ArrayList<>();
   }

   public CompoundCrystalLayout(List<CrystalLayout> children) {
      this.children = children;
   }

   @Override
   public Collection<CrystalEntry> getChildren() {
      return this.children.stream().map(child -> (CrystalEntry)child).toList();
   }

   public static <T> T get(CrystalLayout layout, Class<T> type) {
      for (CrystalLayout child : flatten(layout).children) {
         if (type.isAssignableFrom(child.getClass())) {
            return (T)child;
         }
      }

      return null;
   }

   public static CompoundCrystalLayout flatten(CrystalLayout... layouts) {
      List<CrystalLayout> children = new ArrayList<>();

      for (CrystalLayout layout : layouts) {
         if (layout instanceof CompoundCrystalLayout) {
            children.addAll(((CompoundCrystalLayout)layout).children);
         } else if (layout != null) {
            children.add(layout);
         }
      }

      return new CompoundCrystalLayout(children);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();

      for (CrystalLayout child : this.children) {
         if (child == NullCrystalLayout.INSTANCE) {
            CompoundTag element = new CompoundTag();
            element.putString("type", "null");
            list.add(element);
         } else {
            CrystalData.LAYOUT.writeNbt(child).ifPresent(list::add);
         }
      }

      nbt.put("children", list);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.children = new ArrayList<>();
      ListTag list = nbt.getList("children", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag element = list.getCompound(i);
         if (element.contains("type") && element.getString("type").equals("null")) {
            this.children.add(NullCrystalLayout.INSTANCE);
         } else {
            this.children.add(CrystalData.LAYOUT.readNbt(element).orElseThrow());
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();

      for (CrystalLayout child : this.children) {
         if (child == NullCrystalLayout.INSTANCE) {
            JsonObject element = new JsonObject();
            element.addProperty("type", "null");
            array.add(element);
         } else {
            CrystalData.LAYOUT.writeJson(child).ifPresent(array::add);
         }
      }

      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.children = new ArrayList<>();
      JsonArray array = json.getAsJsonArray("children");

      for (int i = 0; i < array.size(); i++) {
         JsonObject element = array.get(i).getAsJsonObject();
         if (element.has("type") && element.get("type").getAsString().equals("null")) {
            this.children.add(NullCrystalLayout.INSTANCE);
         } else {
            this.children.add(CrystalData.LAYOUT.readJson(element).orElseThrow());
         }
      }
   }
}
