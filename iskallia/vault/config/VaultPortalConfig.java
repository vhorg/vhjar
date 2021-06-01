package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultPortalConfig extends Config {
   @Expose
   public String[] VALID_BLOCKS;

   @Override
   public String getName() {
      return "vault_portal";
   }

   @Override
   protected void reset() {
      this.VALID_BLOCKS = new String[]{
         Blocks.field_235406_np_.getRegistryName().toString(),
         Blocks.field_235410_nt_.getRegistryName().toString(),
         Blocks.field_235411_nu_.getRegistryName().toString(),
         Blocks.field_235412_nv_.getRegistryName().toString()
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
