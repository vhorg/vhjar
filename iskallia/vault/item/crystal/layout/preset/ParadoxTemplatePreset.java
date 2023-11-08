package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.world.template.data.TemplatePool;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class ParadoxTemplatePreset extends PoolTemplatePreset {
   private VaultGod god;

   public ParadoxTemplatePreset() {
   }

   public ParadoxTemplatePreset(TemplatePool pool, VaultGod god) {
      super(pool);
      this.god = god;
   }

   public VaultGod getGod() {
      return this.god;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.GOD_ORDINAL.writeBits(this.god, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.god = Adapters.GOD_ORDINAL.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.GOD_ORDINAL.writeNbt(this.god).ifPresent(value -> nbt.put("god", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.god = Adapters.GOD_ORDINAL.readNbt(nbt.get("god")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.GOD_ORDINAL.writeJson(this.god).ifPresent(value -> json.add("god", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.god = Adapters.GOD_ORDINAL.readJson(json.get("god")).orElseThrow();
   }
}
