package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class RemovedSkill extends LearnableSkill {
   private String type;

   public RemovedSkill(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UTF_8.writeBits(this.type, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.type = Adapters.UTF_8.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UTF_8.writeNbt(this.type).ifPresent(tag -> nbt.put("type", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.type = Adapters.UTF_8.readNbt(nbt.get("type")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UTF_8.writeJson(this.type).ifPresent(element -> json.add("type", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.type = Adapters.UTF_8.readJson(json.get("type")).orElseThrow();
   }
}
