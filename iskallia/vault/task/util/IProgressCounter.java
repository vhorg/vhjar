package iskallia.vault.task.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface IProgressCounter<N extends Number> extends ISerializable<Tag, JsonElement> {
   void addCount(N var1);

   N getCount();

   void setCount(N var1);

   void onTick();

   public abstract static class Config<N extends Number> implements ISerializable<Tag, JsonObject> {
      private final String type;

      private static <N extends Number> Optional<IProgressCounter.Config<N>> initialize(String type) {
         if (type.equals("simple")) {
            return Optional.of(new SimpleIntProgressCounter.Config());
         } else {
            return type.equals("sliding_time") ? Optional.of(new SlidingTimeIntProgressCounter.Config()) : Optional.empty();
         }
      }

      public static <N extends Number> Optional<IProgressCounter.Config<N>> fromBits(BitBuffer buffer) {
         return initialize(buffer.readString());
      }

      public static <N extends Number> Optional<IProgressCounter.Config<N>> fromNbt(Tag nbt) {
         return initialize(nbt instanceof CompoundTag compoundTag ? compoundTag.getString("type") : nbt.getAsString());
      }

      public static <N extends Number> Optional<IProgressCounter.Config<N>> fromJson(JsonObject json) {
         return initialize(json.get("type").getAsString());
      }

      public Config(String type) {
         this.type = type;
      }

      public String getType() {
         return this.type;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         buffer.writeString(this.type);
      }

      @Override
      public void readBits(BitBuffer buffer) {
      }

      @Override
      public Optional<Tag> writeNbt() {
         CompoundTag tag = new CompoundTag();
         tag.putString("type", this.type);
         return Optional.of(tag);
      }

      @Override
      public void readNbt(Tag nbt) {
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         json.addProperty("type", this.type);
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
      }

      public abstract IProgressCounter<N> initCounter();
   }
}
