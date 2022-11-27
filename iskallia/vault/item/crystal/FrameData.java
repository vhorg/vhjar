package iskallia.vault.item.crystal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

public class FrameData implements INBTSerializable<CompoundTag> {
   public List<FrameData.Tile> tiles = new ArrayList<>();

   public static FrameData fromNBT(CompoundTag nbt) {
      FrameData frame = new FrameData();
      frame.deserializeNBT(nbt);
      return frame;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag tilesList = new ListTag();
      this.tiles.forEach(tile -> tilesList.add(tile.serializeNBT()));
      nbt.put("Tiles", tilesList);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag tilesList = nbt.getList("Tiles", 10);
      this.tiles.clear();

      for (int i = 0; i < tilesList.size(); i++) {
         FrameData.Tile tile = new FrameData.Tile();
         tile.deserializeNBT(tilesList.getCompound(i));
         this.tiles.add(tile);
      }
   }

   public static class Tile implements INBTSerializable<CompoundTag> {
      public Block block;
      public CompoundTag data;
      public BlockPos pos;

      public Tile() {
      }

      public Tile(Block block, CompoundTag data, BlockPos pos) {
         this.block = block;
         this.data = data;
         this.pos = pos;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("Block", this.block.getRegistryName().toString());
         nbt.put("Data", this.data.copy());
         nbt.putInt("PosX", this.pos.getX());
         nbt.putInt("PosY", this.pos.getY());
         nbt.putInt("PosZ", this.pos.getZ());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("Block")));
         this.data = nbt.getCompound("Data").copy();
         this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
      }
   }
}
