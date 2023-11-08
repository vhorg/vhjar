package iskallia.vault.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

public class BossProtectionPylonBlock extends Block {
   public BossProtectionPylonBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).noDrops());
   }
}
