package iskallia.vault.block;

import java.util.function.Supplier;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class VaultLogBlock extends RotatedPillarBlock {
   private final Supplier<Block> stripped;

   public static VaultLogBlock log(Supplier<Block> stripped, MaterialColor topColor, MaterialColor barkColor) {
      return new VaultLogBlock(stripped, topColor, barkColor);
   }

   public static VaultLogBlock stripped(MaterialColor topColor, MaterialColor barkColor) {
      return new VaultLogBlock(null, topColor, barkColor);
   }

   private VaultLogBlock(Supplier<Block> stripped, MaterialColor topColor, MaterialColor barkColor) {
      super(
         Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Axis.Y ? topColor : barkColor).strength(2.0F).sound(SoundType.WOOD)
      );
      this.stripped = stripped;
   }

   public BlockState getStripped(BlockState existing) {
      return this.stripped.get().withPropertiesOf(existing);
   }

   public boolean isStrippable() {
      return this.stripped != null;
   }
}
