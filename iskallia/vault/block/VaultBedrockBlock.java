package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;

public class VaultBedrockBlock extends Block {
   public VaultBedrockBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200948_a(-1.0F, 3600000.0F).func_222380_e().func_235827_a_((a, b, c, d) -> false));
   }
}
