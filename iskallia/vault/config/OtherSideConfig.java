package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class OtherSideConfig extends Config {
   @Expose
   public String[] VALID_BLOCKS;

   @Override
   public String getName() {
      return "other_side";
   }

   @Override
   protected void reset() {
      this.VALID_BLOCKS = new String[]{
         Blocks.field_150371_ca.getRegistryName().toString(),
         Blocks.field_235395_nI_.getRegistryName().toString(),
         Blocks.field_196770_fj.getRegistryName().toString(),
         Blocks.field_196581_bI.getRegistryName().toString(),
         Blocks.field_196772_fk.getRegistryName().toString()
      };
   }

   public Block[] getValidFrameBlocks() {
      Block[] blocks = new Block[this.VALID_BLOCKS.length];
      int i = 0;

      for (String s : this.VALID_BLOCKS) {
         ResourceLocation res = new ResourceLocation(s);
         blocks[i++] = (Block)ForgeRegistries.BLOCKS.getValue(res);
      }

      return blocks;
   }
}
