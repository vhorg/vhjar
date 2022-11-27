package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class VaultRockBlock extends OreBlock {
   public VaultRockBlock() {
      super(
         Properties.of(Material.STONE, MaterialColor.DIAMOND)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 9)
            .strength(3.0F, 5.1F)
            .sound(ModSounds.VAULT_GET_SOUND_TYPE)
      );
   }

   public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
      return Mth.nextInt(this.RANDOM, 3, 7);
   }
}
