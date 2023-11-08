package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;

public class StructurePreset implements ISerializable<CompoundTag, JsonObject> {
   private Map<RegionPos, TemplatePreset> rooms = new HashMap<>();

   public Optional<TemplatePreset> get(RegionPos region) {
      return Optional.ofNullable(this.rooms.get(region));
   }

   public boolean contains(RegionPos region) {
      return this.rooms.containsKey(region);
   }

   public StructurePreset put(RegionPos region, TemplatePreset entry) {
      this.rooms.put(region, entry);
      return this;
   }

   public Map<RegionPos, TemplatePreset> getAll() {
      return this.rooms;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.rooms.size()), buffer);
      this.rooms.forEach((region, entry) -> {
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(region.getX()), buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(region.getZ()), buffer);
         Adapters.TEMPLATE_PRESET.writeBits(entry, buffer);
      });
   }

   @Override
   public void readBits(BitBuffer buffer) {
      int groupSize = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.rooms = new HashMap<>();

      for (int i = 0; i < groupSize; i++) {
         int x = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         int z = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         RegionPos region = RegionPos.of(x, z, 0, 0);
         TemplatePreset entry = Adapters.TEMPLATE_PRESET.readBits(buffer).orElseThrow();
         this.rooms.put(region, entry);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ListTag rooms = new ListTag();
      this.rooms.forEach((region, entry) -> {
         CompoundTag element = new CompoundTag();
         ListTag list = new ListTag();
         list.add(IntTag.valueOf(region.getX()));
         list.add(IntTag.valueOf(region.getZ()));
         element.put("region", list);
         Adapters.TEMPLATE_PRESET.writeNbt(entry).ifPresent(value -> {
            element.put("entry", value);
            rooms.add(element);
         });
      });
      nbt.put("rooms", rooms);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.rooms = new HashMap<>();
      ListTag rooms = nbt.getList("rooms", 10);

      for (int i = 0; i < rooms.size(); i++) {
         CompoundTag element = rooms.getCompound(i);
         ListTag list = element.getList("region", 3);
         int x = list.getInt(0);
         int z = list.getInt(1);
         RegionPos region = RegionPos.of(x, z, 0, 0);
         TemplatePreset entry = Adapters.TEMPLATE_PRESET.readNbt(element.get("entry")).orElseThrow();
         this.rooms.put(region, entry);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray rooms = new JsonArray();
      this.rooms.forEach((region, entry) -> {
         JsonObject element = new JsonObject();
         JsonArray list = new JsonArray();
         list.add(region.getX());
         list.add(region.getZ());
         element.add("region", list);
         Adapters.TEMPLATE_PRESET.writeJson(entry).ifPresent(value -> {
            element.add("entry", value);
            rooms.add(element);
         });
      });
      json.add("rooms", rooms);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.rooms = new HashMap<>();
      JsonArray rooms = json.getAsJsonArray("rooms");

      for (int i = 0; i < rooms.size(); i++) {
         JsonObject element = rooms.get(i).getAsJsonObject();
         JsonArray list = element.getAsJsonArray("region");
         int x = list.get(0).getAsInt();
         int z = list.get(1).getAsInt();
         RegionPos region = RegionPos.of(x, z, 0, 0);
         TemplatePreset entry = Adapters.TEMPLATE_PRESET.readJson(element.get("entry")).orElseThrow();
         this.rooms.put(region, entry);
      }
   }
}
