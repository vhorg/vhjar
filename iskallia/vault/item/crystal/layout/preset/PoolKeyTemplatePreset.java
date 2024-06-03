package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PoolKeyTemplatePreset extends TemplatePreset {
   private ResourceLocation pool;

   public PoolKeyTemplatePreset() {
   }

   public PoolKeyTemplatePreset(TemplatePoolKey pool) {
      this.pool = pool.getId();
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.IDENTIFIER.writeBits(this.pool, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.pool = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.IDENTIFIER.writeNbt(this.pool).ifPresent(value -> nbt.put("pool", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.pool = Adapters.IDENTIFIER.readNbt(nbt.get("pool")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.IDENTIFIER.writeJson(this.pool).ifPresent(value -> json.add("pool", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.pool = Adapters.IDENTIFIER.readJson(json.get("pool")).orElseThrow();
   }
}
