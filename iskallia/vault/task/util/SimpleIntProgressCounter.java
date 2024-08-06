package iskallia.vault.task.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class SimpleIntProgressCounter implements IProgressCounter<Integer> {
   public static final String TYPE = "simple";
   private int count;

   public void addCount(Integer count) {
      this.count = this.count + count;
   }

   public Integer getCount() {
      return this.count;
   }

   public void setCount(Integer count) {
      this.count = count;
   }

   @Override
   public void onTick() {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      buffer.writeInt(this.count);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.count = buffer.readInt();
   }

   @Override
   public Optional<Tag> writeNbt() {
      return Optional.of(IntTag.valueOf(this.count));
   }

   @Override
   public void readNbt(Tag nbt) {
      this.count = nbt instanceof IntTag intTag ? intTag.getAsInt() : 0;
   }

   @Override
   public Optional<JsonElement> writeJson() {
      return Optional.of(new JsonPrimitive(this.count));
   }

   @Override
   public void readJson(JsonElement json) {
      this.count = json instanceof JsonPrimitive jsonPrimitive ? jsonPrimitive.getAsInt() : 0;
   }

   public static class Config extends IProgressCounter.Config<Integer> {
      public Config() {
         super("simple");
      }

      @Override
      public Optional<Tag> writeNbt() {
         return Optional.of(StringTag.valueOf("simple"));
      }

      @Override
      public IProgressCounter<Integer> initCounter() {
         return new SimpleIntProgressCounter();
      }
   }
}
