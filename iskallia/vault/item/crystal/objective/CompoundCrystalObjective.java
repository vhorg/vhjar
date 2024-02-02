package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.CrystalProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CompoundCrystalObjective extends CrystalObjective {
   protected List<CrystalObjective> children;

   public CompoundCrystalObjective() {
      this.children = new ArrayList<>();
   }

   public CompoundCrystalObjective(List<CrystalObjective> children) {
      this.children = children;
   }

   @Override
   public Collection<CrystalProperty> getChildren() {
      return this.children.stream().map(child -> (CrystalProperty)child).toList();
   }

   @Override
   public Optional<Integer> getColor(float time) {
      for (CrystalObjective child : this.children) {
         Optional<Integer> color = child.getColor(time);
         if (color.isPresent()) {
            return color;
         }
      }

      return Optional.empty();
   }

   public static CompoundCrystalObjective flatten(CrystalObjective... objectives) {
      List<CrystalObjective> children = new ArrayList<>();

      for (CrystalObjective objective : objectives) {
         if (objective instanceof CompoundCrystalObjective) {
            children.addAll(((CompoundCrystalObjective)objective).children);
         } else if (objective != null) {
            children.add(objective);
         }
      }

      return new CompoundCrystalObjective(children);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();

      for (CrystalObjective child : this.children) {
         if (child == NullCrystalObjective.INSTANCE) {
            CompoundTag element = new CompoundTag();
            element.putString("type", "null");
            list.add(element);
         } else {
            CrystalData.OBJECTIVE.writeNbt(child).ifPresent(list::add);
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
            this.children.add(NullCrystalObjective.INSTANCE);
         } else {
            this.children.add(CrystalData.OBJECTIVE.readNbt(element).orElseThrow());
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();

      for (CrystalObjective child : this.children) {
         if (child == NullCrystalObjective.INSTANCE) {
            JsonObject element = new JsonObject();
            element.addProperty("type", "null");
            array.add(element);
         } else {
            CrystalData.OBJECTIVE.writeJson(child).ifPresent(array::add);
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
            this.children.add(NullCrystalObjective.INSTANCE);
         } else {
            this.children.add(CrystalData.OBJECTIVE.readJson(element).orElseThrow());
         }
      }
   }
}
