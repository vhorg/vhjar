package iskallia.vault.task.source;

import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class TaskSource implements ISerializable<CompoundTag, JsonObject> {
   public abstract RandomSource getRandom();

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
