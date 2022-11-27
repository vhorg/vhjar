package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.BlockPlacementPostProcessor;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPlacementModifier extends VoteModifier {
   @Expose
   private final String block;
   @Expose
   private final int blocksPerSpawn;

   public BlockPlacementModifier(String name, String description, int voteLockDurationChangeSeconds, Block block, int blocksPerSpawn) {
      super(name, description, voteLockDurationChangeSeconds);
      this.block = block.getRegistryName().toString();
      this.blocksPerSpawn = blocksPerSpawn;
   }

   public BlockState getBlock() {
      return Registry.BLOCK.getOptional(new ResourceLocation(this.block)).orElse(Blocks.AIR).defaultBlockState();
   }

   public int getBlocksPerSpawn() {
      return this.blocksPerSpawn;
   }

   @Nullable
   @Override
   public VaultPieceProcessor getPostProcessor(ArchitectObjective objective, VaultRaid vault) {
      return new BlockPlacementPostProcessor(this.getBlock(), this.getBlocksPerSpawn());
   }
}
