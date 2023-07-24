package iskallia.vault.skill.ability.effect.spi.core;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class Cooldown implements ISerializable<CompoundTag, JsonObject> {
   public static final SerializableAdapter<Cooldown, CompoundTag, JsonObject> ADAPTER = new SerializableAdapter<>(Cooldown::new, true);
   public int maxTicks;
   public int remainingTicks;
   public int remainingDelayTicks;

   private Cooldown() {
   }

   public Cooldown(int maxTicks, int remainingTicks, int remainingDelayTicks) {
      this.maxTicks = maxTicks;
      this.remainingTicks = remainingTicks;
      this.remainingDelayTicks = remainingDelayTicks;
   }

   public int getMaxTicks() {
      return this.maxTicks;
   }

   public int getRemainingTicks() {
      return this.remainingTicks;
   }

   public int getRemainingDelayTicks() {
      return this.remainingDelayTicks;
   }

   public void decrement() {
      if (this.remainingDelayTicks > 0) {
         this.remainingDelayTicks--;
      } else {
         this.remainingTicks--;
      }
   }

   public boolean isLargerThan(Cooldown other) {
      return this.remainingTicks + this.remainingDelayTicks > other.remainingTicks + other.remainingDelayTicks;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.maxTicks), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.remainingTicks), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.remainingDelayTicks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.maxTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.remainingTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.remainingDelayTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT.writeNbt(Integer.valueOf(this.maxTicks)).ifPresent(tag -> nbt.put("maxTicks", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.remainingTicks)).ifPresent(tag -> nbt.put("remainingTicks", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.remainingDelayTicks)).ifPresent(tag -> nbt.put("remainingDelayTicks", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.maxTicks = Adapters.INT.readNbt(nbt.get("maxTicks")).orElse(0);
      this.remainingTicks = Adapters.INT.readNbt(nbt.get("remainingTicks")).orElse(0);
      this.remainingDelayTicks = Adapters.INT.readNbt(nbt.get("remainingDelayTicks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT.writeJson(Integer.valueOf(this.maxTicks)).ifPresent(element -> json.add("maxTicks", element));
      Adapters.INT.writeJson(Integer.valueOf(this.remainingTicks)).ifPresent(element -> json.add("remainingTicks", element));
      Adapters.INT.writeJson(Integer.valueOf(this.remainingDelayTicks)).ifPresent(element -> json.add("remainingDelayTicks", element));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.maxTicks = Adapters.INT.readJson(json.get("maxTicks")).orElse(0);
      this.remainingTicks = Adapters.INT.readJson(json.get("remainingTicks")).orElse(0);
      this.remainingDelayTicks = Adapters.INT.readJson(json.get("remainingDelayTicks")).orElse(0);
   }
}
