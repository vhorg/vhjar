package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class PartialBlock implements TilePlacement<PartialBlock> {
   protected ResourceLocation id;

   protected PartialBlock(ResourceLocation id) {
      this.id = id;
   }

   public static PartialBlock empty() {
      return new PartialBlock(null);
   }

   public static PartialBlock of(ResourceLocation id) {
      return new PartialBlock(id);
   }

   public static PartialBlock of(Block block) {
      return new PartialBlock(block.getRegistryName());
   }

   public static PartialBlock of(BlockState state) {
      return new PartialBlock(state.getBlock().getRegistryName());
   }

   public boolean isSubsetOf(PartialBlock other) {
      return this.id == null || this.id.equals(other.id);
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      return this.isSubsetOf(of(world.getBlockState(pos).getBlock()));
   }

   public void fillInto(PartialBlock other) {
      if (this.id != null) {
         other.id = this.id;
      }
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      this.asWhole().ifPresent(block -> {
         BlockState oldState = world.getBlockState(pos);
         BlockState newState = block.defaultBlockState();

         for (Property property : oldState.getProperties()) {
            if (newState.hasProperty(property)) {
               newState = (BlockState)newState.setValue(property, oldState.getValue(property));
            }
         }

         world.setBlock(pos, newState, flags);
      });
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.isSubsetOf(state.getBlock());
   }

   public Optional<Block> asWhole() {
      return !ForgeRegistries.BLOCKS.containsKey(this.id) ? Optional.empty() : Optional.ofNullable((Block)ForgeRegistries.BLOCKS.getValue(this.id));
   }

   public PartialBlock copy() {
      return new PartialBlock(this.id);
   }

   @Override
   public String toString() {
      return this.id == null ? "" : this.id.toString();
   }

   public static Optional<PartialBlock> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialBlock parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialBlock parse(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && isCharValid(reader.peek())) {
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(new ResourceLocation(string));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid block identifier '" + string + "' in tile '" + reader.getString() + "'");
         }
      } else {
         return empty();
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   public static class Adapter implements ISimpleAdapter<PartialBlock, Tag, JsonElement> {
      public Optional<Tag> writeNbt(@Nullable PartialBlock value) {
         return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.id);
      }

      @Override
      public Optional<PartialBlock> readNbt(@Nullable Tag nbt) {
         return nbt == null ? Optional.empty() : Adapters.IDENTIFIER.readNbt(nbt).map(PartialBlock::of);
      }
   }
}
