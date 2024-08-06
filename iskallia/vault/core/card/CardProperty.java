package iskallia.vault.core.card;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class CardProperty<C extends CardProperty.Config> implements ISerializable<CompoundTag, JsonObject> {
   private C config;
   private boolean populated;

   public CardProperty(C config) {
      this.config = config;
   }

   public C getConfig() {
      return this.config;
   }

   public void setConfig(C config) {
      this.config = config;
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public void setPopulated(boolean populated) {
      this.populated = populated;
   }

   public boolean onPopulate() {
      boolean result = !this.isPopulated();
      this.setPopulated(true);
      return result;
   }

   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int tier) {
   }

   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected, int tier) {
      if (!this.populated) {
         this.onPopulate();
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      this.config.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.populated, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.config.readBits(buffer);
      this.populated = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         if (!this.populated) {
            CompoundTag other = this.config.writeNbt().orElseThrow();
            other.getAllKeys().forEach(key -> nbt.put(key, Objects.requireNonNull(other.get(key))));
         } else {
            nbt.put("config", (Tag)this.config.writeNbt().orElseThrow());
         }

         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      if (!nbt.contains("config")) {
         this.config.readNbt(nbt);
         this.populated = false;
      } else {
         this.config.readNbt(nbt.getCompound("config"));
         this.populated = true;
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(json -> {
         if (!this.populated) {
            this.config.writeJson().orElseThrow().entrySet().forEach(entry -> json.add((String)entry.getKey(), (JsonElement)entry.getValue()));
         } else {
            json.add("config", (JsonElement)this.config.writeJson().orElseThrow());
         }

         return (JsonObject)json;
      });
   }

   public void readJson(JsonObject json) {
      if (!json.has("config")) {
         this.config.readJson(json);
         this.populated = false;
      } else {
         this.config.readJson(json.getAsJsonObject("config"));
         this.populated = true;
      }
   }

   public static class Config implements ISerializable<CompoundTag, JsonObject> {
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
}
