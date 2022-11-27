package iskallia.vault.block.entity;

import iskallia.vault.container.VaultCharmControllerContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.VaultCharmData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultCharmControllerTileEntity extends BlockEntity implements MenuProvider {
   public VaultCharmControllerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_CHARM_CONTROLLER_TILE_ENTITY, pos, state);
   }

   @Nonnull
   public Component getDisplayName() {
      return new TextComponent("Vault Charm Inscription Table");
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
      if (this.getLevel() instanceof ServerLevel world) {
         if (playerEntity instanceof ServerPlayer player) {
            CompoundTag inventoryNbt = VaultCharmData.get(world).getInventory(player).serializeNBT();
            return new VaultCharmControllerContainer(windowId, playerInventory, inventoryNbt);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
