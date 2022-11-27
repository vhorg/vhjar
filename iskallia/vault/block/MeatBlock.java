package iskallia.vault.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

public class MeatBlock extends Block {
   public MeatBlock() {
      super(Properties.of(Material.GRASS).sound(SoundType.SLIME_BLOCK));
   }
}
