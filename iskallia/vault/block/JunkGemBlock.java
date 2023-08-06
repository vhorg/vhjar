package iskallia.vault.block;

import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class JunkGemBlock extends AmethystBlock {
   public JunkGemBlock() {
      super(Properties.copy(Blocks.AMETHYST_BLOCK));
   }
}
