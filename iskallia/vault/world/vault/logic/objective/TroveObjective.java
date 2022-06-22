package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.logic.task.VaultTask;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class TroveObjective extends VaultObjective {
   public TroveObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return Blocks.field_150350_a.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      return null;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return this.getVaultName();
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Vault Trove").func_240699_a_(TextFormatting.GOLD);
   }

   @Override
   public int getVaultTimerStart(int vaultTime) {
      return 12000;
   }

   @Override
   public boolean preventsEatingExtensionFruit(MinecraftServer srv, VaultRaid vault) {
      return true;
   }

   @Override
   public boolean preventsMobSpawning() {
      return true;
   }

   @Override
   public boolean preventsTrappedChests() {
      return true;
   }

   @Override
   public boolean preventsInfluences() {
      return true;
   }

   @Nonnull
   @Override
   public Supplier<? extends VaultGenerator> getVaultGenerator() {
      return VaultRaid.TROVE_GENERATOR;
   }
}
