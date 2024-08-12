package iskallia.vault.integration;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IntegrationRefinedStorage {
   public static boolean shouldPreventImportingCapability(@Nullable Level level, BlockPos hostPos, @Nullable Direction side) {
      if (level != null && side != null) {
         BlockState state = level.getBlockState(hostPos.relative(side));
         ResourceLocation key = state.getBlock().getRegistryName();
         if (key != null && key.getNamespace().equals("refinedstorage")) {
            return key.getPath().equals("importer") || key.getPath().equals("external_storage");
         }
      }

      return false;
   }
}
