package iskallia.vault.block.entity;

import iskallia.vault.container.VaultCharmControllerContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.VaultCharmData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class VaultCharmControllerTileEntity extends TileEntity implements INamedContainerProvider {
   public VaultCharmControllerTileEntity() {
      super(ModBlocks.VAULT_CHARM_CONTROLLER_TILE_ENTITY);
   }

   @Nonnull
   public ITextComponent func_145748_c_() {
      return new StringTextComponent("Vault Charm Inscription Table");
   }

   @Nullable
   public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      if (!(this.func_145831_w() instanceof ServerWorld)) {
         return null;
      } else {
         ServerWorld world = (ServerWorld)this.func_145831_w();
         if (!(playerEntity instanceof ServerPlayerEntity)) {
            return null;
         } else {
            ServerPlayerEntity player = (ServerPlayerEntity)playerEntity;
            CompoundNBT inventoryNbt = VaultCharmData.get(world).getInventory(player).serializeNBT();
            return new VaultCharmControllerContainer(windowId, playerInventory, inventoryNbt);
         }
      }
   }
}
