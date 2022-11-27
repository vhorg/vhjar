package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlockPlacementModifier extends RaidModifier {
   @Expose
   private final String block;
   @Expose
   private final int blocksToSpawn;
   @Expose
   private final String blockDescription;

   public BlockPlacementModifier(String name, Block block, int blocksToSpawn, String blockDescription) {
      this(name, block.getRegistryName().toString(), blocksToSpawn, blockDescription);
   }

   public BlockPlacementModifier(String name, String block, int blocksToSpawn, String blockDescription) {
      super(false, true, name);
      this.block = block;
      this.blocksToSpawn = blocksToSpawn;
      this.blockDescription = blockDescription;
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
      BlockState placementState = Registry.BLOCK.getOptional(new ResourceLocation(this.block)).orElse(Blocks.AIR).defaultBlockState();
      int toPlace = this.blocksToSpawn * Math.round(value);
      AABB placementBox = raid.getRaidBoundingBox();

      for (int i = 0; i < toPlace; i++) {
         BlockPos at;
         do {
            at = MiscUtils.getRandomPos(placementBox, rand);
         } while (!world.isEmptyBlock(at) || !world.getBlockState(at.below()).isFaceSturdy(world, at, Direction.UP));

         world.setBlock(at, placementState, 2);
      }
   }

   @Override
   public Component getDisplay(float value) {
      int sets = Math.round(value);
      String set = sets > 1 ? "sets" : "set";
      return new TextComponent("+" + sets + " " + set + " of " + this.blockDescription).withStyle(ChatFormatting.GREEN);
   }
}
