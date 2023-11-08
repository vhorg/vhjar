package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class TemplatePreset implements ISerializable<CompoundTag, JsonObject> {
   @Override
   public void writeBits(BitBuffer buffer) {
   }

   @Override
   public void readBits(BitBuffer buffer) {
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag());
   }

   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject());
   }

   public void readJson(JsonObject json) {
   }
}
