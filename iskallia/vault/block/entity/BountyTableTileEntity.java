package iskallia.vault.block.entity;

import iskallia.vault.container.BountyContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.BountyData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BountyTableTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(1, this);

   public BountyTableTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.BOUNTY_TABLE_TILE_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public void load(@NotNull CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
   }

   protected void saveAdditional(@NotNull CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
   }

   @NotNull
   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player) {
      if (this.getLevel() == null) {
         return null;
      } else {
         ServerPlayer sPlayer = (ServerPlayer)player;
         BountyData data = BountyData.get();
         int vaultLevel = PlayerVaultStatsData.get(sPlayer.getLevel()).getVaultStats(sPlayer).getVaultLevel();
         CompoundTag tag = data.getAllBountiesAsTagFor(sPlayer.getUUID());
         tag.put("pos", NbtUtils.writeBlockPos(this.getBlockPos()));
         tag.putInt("vaultLevel", vaultLevel);
         return new BountyContainer(containerId, this.getLevel(), inv, tag);
      }
   }
}
