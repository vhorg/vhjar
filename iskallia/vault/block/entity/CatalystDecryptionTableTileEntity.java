package iskallia.vault.block.entity;

import iskallia.vault.container.inventory.CatalystDecryptionContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.VaultCatalystItem;
import iskallia.vault.item.VaultInhibitorItem;
import iskallia.vault.item.crystal.VaultCrystalItem;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CatalystDecryptionTableTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
   private final ItemStackHandler handler = new ItemStackHandler(11) {
      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         CatalystDecryptionTableTileEntity.this.sendUpdates();
      }
   };

   public CatalystDecryptionTableTileEntity() {
      super(ModBlocks.CATALYST_DECRYPTION_TABLE_TILE_ENTITY);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public void func_73660_a() {
      for (int slot = 0; slot < this.handler.getSlots(); slot++) {
         ItemStack stack = this.handler.getStackInSlot(slot);
         if (stack.func_77973_b() instanceof VaultCatalystItem) {
            VaultCatalystItem.getSeed(stack);
         }

         if (stack.func_77973_b() instanceof VaultCrystalItem) {
            VaultCrystalItem.getSeed(stack);
         }

         if (stack.func_77973_b() instanceof VaultInhibitorItem) {
            VaultInhibitorItem.getSeed(stack);
         }
      }
   }

   public void func_230337_a_(BlockState state, CompoundNBT tag) {
      this.handler.deserializeNBT(tag.func_74775_l("inventory"));
      super.func_230337_a_(state, tag);
   }

   public CompoundNBT func_189515_b(CompoundNBT tag) {
      tag.func_218657_a("inventory", this.handler.serializeNBT());
      return super.func_189515_b(tag);
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? LazyOptional.of(() -> this.handler).cast() : super.getCapability(cap, side);
   }

   public ITextComponent func_145748_c_() {
      return new StringTextComponent("Catalyst Decryption Table");
   }

   @Nullable
   public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
      return this.func_145831_w() == null ? null : new CatalystDecryptionContainer(windowId, this.func_145831_w(), this.func_174877_v(), playerInventory);
   }
}
