package iskallia.vault.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

public class VaultBedrockBlock extends Block implements GameMasterBlock {
   public VaultBedrockBlock() {
      super(Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().isValidSpawn((a, b, c, d) -> false));
   }
}
