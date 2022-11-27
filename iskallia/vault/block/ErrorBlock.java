package iskallia.vault.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class ErrorBlock extends Block {
   public ErrorBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).strength(0.0F, 0.0F).noDrops());
   }
}
