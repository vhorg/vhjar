package iskallia.vault.block.entity;

import iskallia.vault.container.inventory.CatalystInfusionTableContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.VaultCatalystInfusedItem;
import iskallia.vault.world.VaultMode;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CatalystInfusionTableTileEntity extends BlockEntity implements MenuProvider {
   public static final String TAG_CATALYST_ITEM_STACK_HANDLER = "catalyst";
   public static final String TAG_INFUSER_ITEM_STACK_HANDLER = "infuser";
   public static final String TAG_OUTPUT_ITEM_STACK_HANDLER = "output";
   public static final String TAG_PROGRESS = "progress";
   private final ItemStackHandler catalystStackHandler = new CatalystInfusionTableTileEntity.CatalystInfusionTableStackHandler(
      this, itemStack -> itemStack.getItem() == ModItems.VAULT_CATALYST
   );
   private final ItemStackHandler infuserStackHandler = new CatalystInfusionTableTileEntity.CatalystInfusionTableStackHandler(
      this, itemStack -> ItemStack.isSameItemSameTags(ModConfigs.CATALYST_INFUSION_TABLE.getInfusionItem(), itemStack)
   );
   private final ItemStackHandler outputStackHandler = new CatalystInfusionTableTileEntity.CatalystInfusionTableStackHandler(
      this, itemStack -> itemStack.getItem() == ModItems.VAULT_CATALYST_INFUSED
   );
   private int progress;

   public CatalystInfusionTableTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.CATALYST_INFUSION_TABLE_TILE_ENTITY, pos, state);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      }

      this.setChanged();
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void serverTick(Level world, BlockPos pos, BlockState state, CatalystInfusionTableTileEntity tile) {
      boolean active = tile.isActive();
      boolean sendUpdates = false;
      if (active) {
         tile.progress++;
         tile.playInfusionEffects((ServerLevel)world);
         sendUpdates = true;
      } else if (tile.progress > 0) {
         tile.progress = 0;
         sendUpdates = true;
      }

      if (active && tile.progress >= ModConfigs.CATALYST_INFUSION_TABLE.getInfusionTimeTicks()) {
         tile.catalystStackHandler.getStackInSlot(0).shrink(1);
         tile.infuserStackHandler.getStackInSlot(0).shrink(1);
         ItemStack itemStack = new ItemStack(ModItems.VAULT_CATALYST_INFUSED);
         VaultCatalystInfusedItem.initializeModifiers(
            itemStack, ((VaultMode.GameRuleValue)world.getGameRules().getRule(ModGameRules.MODE)).get() == VaultMode.CASUAL
         );
         tile.outputStackHandler.setStackInSlot(0, itemStack);
         tile.progress = 0;
      }

      if (sendUpdates) {
         tile.sendUpdates();
      }
   }

   private void playInfusionEffects(ServerLevel world) {
      float progress = (float)this.progress / ModConfigs.CATALYST_INFUSION_TABLE.getInfusionTimeTicks();
      float speed = progress * 0.05F;
      if (speed > 0.0F) {
         if (progress < 0.5) {
            world.sendParticles(
               ParticleTypes.PORTAL, this.worldPosition.getX() + 0.5, this.getBlockPos().getY() + 1.0, this.getBlockPos().getZ() + 0.5, 3, 0.0, 0.0, 0.0, speed
            );
         }

         if (progress > 0.75) {
            world.sendParticles(
               ParticleTypes.REVERSE_PORTAL,
               this.worldPosition.getX() + 0.5,
               this.getBlockPos().getY() + 1.18,
               this.getBlockPos().getZ() + 0.5,
               3,
               0.0,
               0.0,
               0.0,
               speed * 0.2
            );
         }

         if (progress > 0.95) {
            world.sendParticles(
               ParticleTypes.REVERSE_PORTAL,
               this.worldPosition.getX() + 0.5,
               this.getBlockPos().getY() + 1.18,
               this.getBlockPos().getZ() + 0.5,
               3,
               0.0,
               0.0,
               0.0,
               speed * 0.3
            );
         }
      }
   }

   public Stream<ItemStackHandler> getItemStackHandlers() {
      return Stream.of(this.catalystStackHandler, this.infuserStackHandler, this.outputStackHandler);
   }

   public ItemStackHandler getCatalystStackHandler() {
      return this.catalystStackHandler;
   }

   public ItemStackHandler getInfuserStackHandler() {
      return this.infuserStackHandler;
   }

   public ItemStackHandler getOutputStackHandler() {
      return this.outputStackHandler;
   }

   public boolean isActive() {
      return !this.catalystStackHandler.getStackInSlot(0).isEmpty()
         && !this.infuserStackHandler.getStackInSlot(0).isEmpty()
         && this.outputStackHandler.getStackInSlot(0).isEmpty();
   }

   public float getProgress() {
      return (float)this.progress / ModConfigs.CATALYST_INFUSION_TABLE.getInfusionTimeTicks();
   }

   public void load(@Nonnull CompoundTag tag) {
      super.load(tag);
      this.catalystStackHandler.deserializeNBT(tag.getCompound("catalyst"));
      this.infuserStackHandler.deserializeNBT(tag.getCompound("infuser"));
      this.outputStackHandler.deserializeNBT(tag.getCompound("output"));
      this.progress = tag.getInt("progress");
   }

   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("catalyst", this.catalystStackHandler.serializeNBT());
      tag.put("infuser", this.infuserStackHandler.serializeNBT());
      tag.put("output", this.outputStackHandler.serializeNBT());
      tag.putInt("progress", this.progress);
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @javax.annotation.Nullable Direction side) {
      if (side != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return switch (side) {
            case UP -> LazyOptional.of(() -> this.catalystStackHandler).cast();
            case SOUTH, NORTH, EAST, WEST -> LazyOptional.of(() -> this.infuserStackHandler).cast();
            case DOWN -> LazyOptional.of(() -> this.outputStackHandler).cast();
            default -> throw new IncompatibleClassChangeError();
         };
      } else {
         return super.getCapability(capability, side);
      }
   }

   @Nonnull
   public Component getDisplayName() {
      return new TranslatableComponent("block.the_vault.catalyst_infusion_table");
   }

   @javax.annotation.Nullable
   @ParametersAreNonnullByDefault
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
      return this.getLevel() == null ? null : new CatalystInfusionTableContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   private static class CatalystInfusionTableStackHandler extends ItemStackHandler {
      private final CatalystInfusionTableTileEntity tile;
      private final Predicate<ItemStack> itemFilter;

      public CatalystInfusionTableStackHandler(CatalystInfusionTableTileEntity tile, Predicate<ItemStack> itemFilter) {
         this.tile = tile;
         this.itemFilter = itemFilter;
      }

      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         this.tile.sendUpdates();
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         return this.itemFilter.test(stack);
      }
   }
}
