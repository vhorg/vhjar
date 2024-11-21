package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class PartialBlockProperties implements TilePlacement<PartialBlockProperties> {
   private final Map<String, String> properties;

   protected PartialBlockProperties(Map<String, String> properties) {
      this.properties = properties;
   }

   public static PartialBlockProperties empty() {
      return new PartialBlockProperties(new HashMap<>());
   }

   public static PartialBlockProperties of(Map<String, String> properties) {
      return new PartialBlockProperties(properties);
   }

   public static PartialBlockProperties of(BlockState state) {
      Map<String, String> properties = new HashMap<>();

      for (Property property : state.getProperties()) {
         properties.put(property.getName(), property.getName(state.getValue(property)));
      }

      return new PartialBlockProperties(properties);
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public <T extends Comparable<T>> T get(Property<T> property) {
      return (T)property.getValue(this.properties.get(property.getName())).orElse(null);
   }

   public <T extends Comparable<T>, V extends T> PartialBlockProperties set(Property<T> property, V value) {
      this.properties.put(property.getName(), property.getName(value));
      return this;
   }

   public boolean isSubsetOf(PartialBlockProperties other) {
      if (other == null) {
         return false;
      } else {
         for (Entry<String, String> entry : this.properties.entrySet()) {
            if (!entry.getValue().equals(other.properties.get(entry.getKey()))) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      return this.isSubsetOf(of(world.getBlockState(pos)));
   }

   public void fillInto(PartialBlockProperties other) {
      other.properties.putAll(this.properties);
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      BlockState oldState = world.getBlockState(pos);
      BlockState newState = this.apply(oldState);
      if (oldState != newState) {
         world.setBlock(pos, newState, flags);
      }
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.isSubsetOf(state.getProperties());
   }

   public BlockState apply(BlockState state) {
      StateDefinition<Block, BlockState> definition = state.getBlock().getStateDefinition();

      for (Entry<String, String> entry : this.properties.entrySet()) {
         Property property = definition.getProperty(entry.getKey());
         if (property != null && state.hasProperty(property)) {
            Optional<?> value = property.getValue(entry.getValue());
            if (!value.isEmpty()) {
               state = (BlockState)state.setValue(property, (Comparable)value.get());
            }
         }
      }

      return state;
   }

   public PartialBlockProperties copy() {
      return new PartialBlockProperties(new HashMap<>(this.properties));
   }

   @Override
   public String toString() {
      if (this.properties.isEmpty()) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('[');
         Iterator<Entry<String, String>> iterator = this.properties.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            if (iterator.hasNext()) {
               sb.append(',');
            }
         }

         return sb.append(']').toString();
      }
   }

   public static Optional<PartialBlockProperties> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialBlockProperties parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialBlockProperties parse(StringReader reader) throws CommandSyntaxException {
      Map<String, String> properties = new HashMap<>();
      if (reader.canRead() && reader.peek() == '[') {
         reader.skip();
         int cursor = -1;
         reader.skipWhitespace();

         while (reader.canRead() && reader.peek() != ']') {
            reader.skipWhitespace();
            String key = reader.readString();
            int prevCursor = reader.getCursor();
            reader.skipWhitespace();
            if (properties.containsKey(key)) {
               reader.setCursor(cursor);
               throw new IllegalArgumentException("Duplicate property <" + key + "> in tile '" + reader.getString() + "'");
            }

            if (!reader.canRead() || reader.peek() != '=') {
               reader.setCursor(prevCursor);
               throw new IllegalArgumentException("Empty property <" + key + "> in tile '" + reader.getString() + "'");
            }

            reader.skip();
            reader.skipWhitespace();
            cursor = reader.getCursor();
            String value = reader.readString();
            properties.put(key, value);
            reader.skipWhitespace();
            if (reader.canRead()) {
               cursor = -1;
               if (reader.peek() != ',') {
                  if (reader.peek() != ']') {
                     throw new IllegalArgumentException("Unclosed properties in tile '" + reader.getString() + "'");
                  }
                  break;
               }

               reader.skip();
            }
         }

         if (reader.canRead()) {
            reader.skip();
            return of(properties);
         } else {
            if (cursor >= 0) {
               reader.setCursor(cursor);
            }

            throw new IllegalArgumentException("Unclosed properties in tile '" + reader.getString() + "'");
         }
      } else {
         return of(properties);
      }
   }

   public static class Adapter implements ISimpleAdapter<PartialBlockProperties, Tag, JsonElement> {
      public void writeBits(PartialBlockProperties value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.properties.size()), buffer);
            value.properties.forEach((k, v) -> {
               Adapters.UTF_8.writeBits(k, buffer);
               Adapters.UTF_8.writeBits(v, buffer);
            });
         }
      }

      @Override
      public Optional<PartialBlockProperties> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            Map<String, String> properties = new HashMap<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for (int i = 0; i < size; i++) {
               properties.put(Adapters.UTF_8.readBits(buffer).orElseThrow(), Adapters.UTF_8.readBits(buffer).orElseThrow());
            }

            return Optional.of(PartialBlockProperties.of(properties));
         }
      }

      public Optional<Tag> writeNbt(@Nullable PartialBlockProperties value) {
         if (value != null && !value.properties.isEmpty()) {
            CompoundTag nbt = new CompoundTag();
            value.properties.forEach(nbt::putString);
            return Optional.of(nbt);
         } else {
            return Optional.empty();
         }
      }

      @Override
      public Optional<PartialBlockProperties> readNbt(@Nullable Tag nbt) {
         if (nbt instanceof CompoundTag compound) {
            Map<String, String> properties = new HashMap<>();

            for (String key : compound.getAllKeys()) {
               if (compound.get(key) instanceof StringTag string) {
                  properties.put(key, string.getAsString());
               }
            }

            return Optional.of(PartialBlockProperties.of(properties));
         } else {
            return nbt instanceof StringTag string ? PartialBlockProperties.parse(string.getAsString(), true) : Optional.empty();
         }
      }
   }
}
