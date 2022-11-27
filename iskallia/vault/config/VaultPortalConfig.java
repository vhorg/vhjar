package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
         Blocks.BLACKSTONE.getRegistryName().toString(),
         Blocks.POLISHED_BLACKSTONE.getRegistryName().toString(),
         Blocks.POLISHED_BLACKSTONE_BRICKS.getRegistryName().toString(),
         Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getRegistryName().toString(),
         ModBlocks.FINAL_VAULT_FRAME.getRegistryName().toString()
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
