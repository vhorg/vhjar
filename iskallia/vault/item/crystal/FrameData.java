package iskallia.vault.item.crystal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

public class FrameData implements INBTSerializable<CompoundNBT> {
   public List<FrameData.Tile> tiles = new ArrayList<>();

   public static FrameData fromNBT(CompoundNBT nbt) {
      FrameData frame = new FrameData();
      frame.deserializeNBT(nbt);
      return frame;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT tilesList = new ListNBT();
      this.tiles.forEach(tile -> tilesList.add(tile.serializeNBT()));
      nbt.func_218657_a("Tiles", tilesList);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      ListNBT tilesList = nbt.func_150295_c("Tiles", 10);
      this.tiles.clear();

      for (int i = 0; i < tilesList.size(); i++) {
         FrameData.Tile tile = new FrameData.Tile();
         tile.deserializeNBT(tilesList.func_150305_b(i));
         this.tiles.add(tile);
      }
   }

   public static class Tile implements INBTSerializable<CompoundNBT> {
      public Block block;
      public CompoundNBT data;
      public BlockPos pos;

      public Tile() {
      }

      public Tile(Block block, CompoundNBT data, BlockPos pos) {
         this.block = block;
         this.data = data;
         this.pos = pos;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Block", this.block.getRegistryName().toString());
         nbt.func_218657_a("Data", this.data.func_74737_b());
         nbt.func_74768_a("PosX", this.pos.func_177958_n());
         nbt.func_74768_a("PosY", this.pos.func_177956_o());
         nbt.func_74768_a("PosZ", this.pos.func_177952_p());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.func_74779_i("Block")));
         this.data = nbt.func_74775_l("Data").func_74737_b();
         this.pos = new BlockPos(nbt.func_74762_e("PosX"), nbt.func_74762_e("PosY"), nbt.func_74762_e("PosZ"));
      }
   }
}
