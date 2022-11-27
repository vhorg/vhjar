package iskallia.vault.core.world.data;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class PartialState {
   private Block block;
   private final Map<Property<?>, Comparable<?>> properties = new HashMap<>();

   protected PartialState(Block block) {
      this.block = block;
   }

   protected PartialState(Block block, Map<Property<?>, Comparable<?>> properties) {
      this.block = block;
      this.properties.putAll(properties);
   }

   public static PartialState of(Block block) {
      return new PartialState(block);
   }

   public static PartialState of(BlockState delegate) {
      PartialState state = new PartialState(delegate.getBlock());
      state.properties.putAll(delegate.getValues());
      return state;
   }

   public static PartialState parse(String target) {
      return new TileParser(new StringReader(target), ModBlocks.ERROR_BLOCK, false).parse().getPartialState();
   }

   public Block getBlock() {
      return this.block;
   }

   public Map<Property<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   public <T extends Comparable<T>> T get(Property<T> property) {
      return (T)this.properties.get(property);
   }

   public <T extends Comparable<T>, V extends T> PartialState with(Property<T> property, V value) {
      this.properties.put(property, value);
      return this;
   }

   public boolean matches(BlockState state) {
      if (state.getBlock() == this.block && this.properties.size() == state.getProperties().size()) {
         for (Property<?> property : state.getProperties()) {
            if (!this.properties.containsKey(property)) {
               return false;
            }

            if (!this.properties.get(property).equals(state.getValue(property))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isSubsetOf(PartialState state) {
      if (state.getBlock() != this.block) {
         return false;
      } else {
         for (Entry<Property<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
            Property<?> property = entry.getKey();
            Comparable<?> value = entry.getValue();
            if (this.properties.containsKey(property) && !this.properties.get(property).equals(value)) {
               return false;
            }
         }

         return true;
      }
   }

   public void fillMissing(PartialState state) {
      for (Entry<Property<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
         if (!this.properties.containsKey(entry.getKey())) {
            this.properties.put(entry.getKey(), entry.getValue());
         }
      }
   }

   public void copyInto(PartialState target) {
      if (this.block != null) {
         target.block = this.block;
      }

      target.properties.putAll(this.properties);
   }

   public BlockState asBlockState() {
      BlockState state = this.block.defaultBlockState();

      for (Entry<Property<?>, Comparable<?>> entry : this.properties.entrySet()) {
         if (state.getProperties().contains(entry.getKey())) {
            state = (BlockState)state.setValue(entry.getKey(), entry.getValue());
         }
      }

      return state;
   }

   public void mirror(Mirror mirror) {
      BlockState mirrored = this.asBlockState().mirror(mirror);
      UnmodifiableIterator var3 = mirrored.getValues().entrySet().iterator();

      while (var3.hasNext()) {
         Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
         if (this.properties.containsKey(entry.getKey())) {
            this.properties.put(entry.getKey(), entry.getValue());
         }
      }
   }

   public void rotate(Rotation rotation) {
      BlockState rotated = this.asBlockState().rotate(rotation);
      UnmodifiableIterator var3 = rotated.getValues().entrySet().iterator();

      while (var3.hasNext()) {
         Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
         if (this.properties.containsKey(entry.getKey())) {
            this.properties.put(entry.getKey(), entry.getValue());
         }
      }
   }

   public PartialState copy() {
      PartialState state = of(this.block);
      state.properties.putAll(this.properties);
      return state;
   }

   public Tag toNBT(CompoundTag nbt) {
      nbt.putString("Name", ForgeRegistries.BLOCKS.getKey(this.block).toString());
      if (!this.properties.isEmpty()) {
         CompoundTag compoundtag1 = new CompoundTag();

         for (Entry<Property<?>, Comparable<?>> entry : this.properties.entrySet()) {
            Property<?> property = entry.getKey();
            compoundtag1.putString(property.getName(), property.getName(entry.getValue()));
         }

         nbt.put("Properties", compoundtag1);
      }

      return nbt;
   }

   public static PartialState fromNBT(CompoundTag nbt) {
      if (!nbt.contains("Name", 8)) {
         return of(ModBlocks.ERROR_BLOCK);
      } else {
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("Name")));
         Map<Property<?>, Comparable<?>> properties = new HashMap<>();
         if (nbt.contains("Properties", 10)) {
            CompoundTag propertiesNBT = nbt.getCompound("Properties");

            for (String s : propertiesNBT.getAllKeys()) {
               Property<?> property = block.getStateDefinition().getProperty(s);
               if (property != null) {
                  Optional<?> valueOpt = property.getValue(propertiesNBT.getString(s));
                  if (valueOpt.isPresent()) {
                     properties.put(property, (Comparable<?>)valueOpt.get());
                  } else {
                     VaultMod.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", s, propertiesNBT.getString(s), nbt.toString());
                  }
               }
            }
         }

         return new PartialState(block, properties);
      }
   }
}
