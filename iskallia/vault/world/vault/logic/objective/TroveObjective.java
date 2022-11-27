package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultGenerator;
import iskallia.vault.world.vault.logic.task.VaultTask;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public class TroveObjective extends VaultObjective {
   public TroveObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      return null;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return this.getVaultName();
   }

   @Override
   public Component getVaultName() {
      return new TextComponent("Vault Trove").withStyle(ChatFormatting.GOLD);
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
