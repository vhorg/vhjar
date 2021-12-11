package iskallia.vault.block;

import iskallia.vault.block.entity.VaultLootableTileEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class VaultLootableBlock extends Block {
   private final VaultLootableBlock.Type type;

   public VaultLootableBlock(VaultLootableBlock.Type type) {
      super(Properties.func_200950_a(Blocks.field_196602_ba));
      this.type = type;
   }

   public VaultLootableBlock.Type getType() {
      return this.type;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return new VaultLootableTileEntity().setType(this.getType());
   }

   public static class GeneratedBlockState {
      private final BlockState state;
      private final BiConsumer<ServerWorld, BlockPos> postProcess;

      public GeneratedBlockState(BlockState state) {
         this(state, (sWorld, pos) -> {});
      }

      public GeneratedBlockState(BlockState state, BiConsumer<ServerWorld, BlockPos> postProcess) {
         this.state = state;
         this.postProcess = postProcess;
      }

      public BlockState getState() {
         return this.state;
      }

      public BiConsumer<ServerWorld, BlockPos> getPostProcessor() {
         return this.postProcess;
      }
   }

   public static enum Type {
      ORE(VaultLootableTileEntity.VaultOreBlockGenerator::new),
      RICHITY(() -> ModConfigs.VAULT_LOOTABLES.RICHITY::get),
      RESOURCE(VaultLootableTileEntity.VaultResourceBlockGenerator::new),
      MISC(() -> ModConfigs.VAULT_LOOTABLES.MISC::get),
      VAULT_CHEST(() -> ModConfigs.VAULT_LOOTABLES.VAULT_CHEST::get),
      VAULT_TREASURE(() -> ModConfigs.VAULT_LOOTABLES.VAULT_TREASURE::get),
      VAULT_OBJECTIVE(VaultObjective::getObjectiveBlock);

      private final Supplier<VaultLootableTileEntity.Generator> generator;

      private Type(Supplier<VaultLootableTileEntity.Generator> generator) {
         this.generator = generator;
      }

      public VaultLootableBlock.GeneratedBlockState generateBlock(ServerWorld world, BlockPos pos, Random random, UUID playerUUID) {
         VaultLootableTileEntity.Generator gen = this.generator.get();
         BlockState generated = gen.generate(world, pos, random, this.name(), playerUUID);
         return gen instanceof VaultLootableTileEntity.ExtendedGenerator
            ? new VaultLootableBlock.GeneratedBlockState(generated, ((VaultLootableTileEntity.ExtendedGenerator)gen)::postProcess)
            : new VaultLootableBlock.GeneratedBlockState(generated);
      }
   }
}
