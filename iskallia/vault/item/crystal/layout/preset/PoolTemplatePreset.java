package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.template.data.TemplatePool;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class PoolTemplatePreset extends TemplatePreset {
   private TemplatePool pool;

   public PoolTemplatePreset() {
   }

   public PoolTemplatePreset(TemplatePool pool) {
      this.pool = pool;
   }

   public TemplatePool getPool() {
      return this.pool;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.TEMPLATE_POOL.writeBits(this.pool, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.pool = Adapters.TEMPLATE_POOL.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.TEMPLATE_POOL.writeNbt(this.pool).ifPresent(value -> nbt.put("pool", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.pool = Adapters.TEMPLATE_POOL.readNbt((ListTag)nbt.get("pool")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.TEMPLATE_POOL.writeJson(this.pool).ifPresent(value -> json.add("pool", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.pool = Adapters.TEMPLATE_POOL.readJson(json.getAsJsonArray("pool")).orElseThrow();
   }
}
