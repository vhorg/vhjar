package iskallia.vault.world.vault.logic.objective.architect.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.logic.objective.architect.processor.BlockPlacementPostProcessor;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

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
      return Registry.field_212618_g.func_241873_b(new ResourceLocation(this.block)).orElse(Blocks.field_150350_a).func_176223_P();
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
