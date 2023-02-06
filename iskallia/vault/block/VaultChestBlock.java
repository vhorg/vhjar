package iskallia.vault.block;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public class VaultChestBlock extends ChestBlock {
   public static final EnumProperty<VaultChestBlock.Variant> VARIANT = EnumProperty.create("variant", VaultChestBlock.Variant.class);
   private final VaultChestType type;

   protected VaultChestBlock(VaultChestType type, Properties builder, Supplier<BlockEntityType<? extends ChestBlockEntity>> tileSupplier) {
      super(builder, tileSupplier);
      this.type = type;
   }

   public VaultChestBlock(VaultChestType type, Properties builder) {
      this(type, builder, () -> ModBlocks.VAULT_CHEST_TILE_ENTITY);
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{VARIANT});
   }

   public VaultChestType getType() {
      return this.type;
   }

   public boolean hasStepBreaking() {
      return this == ModBlocks.GILDED_CHEST || this == ModBlocks.LIVING_CHEST || this == ModBlocks.ORNATE_CHEST;
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level pLevel, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.VAULT_CHEST_TILE_ENTITY, pLevel.isClientSide ? VaultChestTileEntity::tick : null);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (!(world.getBlockEntity(pos) instanceof VaultChestTileEntity chest && !player.isCreative())) {
         return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
      } else if (!this.hasStepBreaking()) {
         return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
      } else if (chest.isEmpty()) {
         return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
      } else {
         this.playerWillDestroy(world, pos, state, player);
         return true;
      }
   }

   public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
      if (!this.hasStepBreaking()) {
         super.playerDestroy(world, player, pos, state, te, stack);
      } else {
         player.awardStat(Stats.BLOCK_MINED.get(this));
         player.causeFoodExhaustion(0.005F);
         if (te instanceof VaultChestTileEntity chest) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
               ItemStack invStack = chest.getItem(slot);
               if (!invStack.isEmpty()) {
                  Block.popResource(world, pos, invStack);
                  chest.setItem(slot, ItemStack.EMPTY);
                  break;
               }
            }
         }
      }
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new VaultChestTileEntity(pPos, pState);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state == null ? null : (BlockState)((BlockState)state.setValue(TYPE, ChestType.SINGLE)).setValue(VARIANT, VaultChestBlock.Variant.NORMAL);
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
      if ((
            state.getBlock() == ModBlocks.ALTAR_CHEST_PLACEABLE
               || state.getBlock() == ModBlocks.GILDED_CHEST_PLACEABLE
               || state.getBlock() == ModBlocks.LIVING_CHEST_PLACEABLE
               || state.getBlock() == ModBlocks.ORNATE_CHEST_PLACEABLE
               || state.getBlock() == ModBlocks.TREASURE_CHEST_PLACEABLE
               || state.getBlock() == ModBlocks.WOODEN_CHEST_PLACEABLE
         )
         && level.getBlockEntity(pos) instanceof VaultChestTileEntity te) {
         return new MenuProvider() {
            public Component getDisplayName() {
               return te.getDisplayName();
            }

            @Nullable
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
               if (te.canOpen(player)) {
                  switch (te.getContainerSize()) {
                     case 36:
                        return new ChestMenu(MenuType.GENERIC_9x4, containerId, playerInventory, te, 4);
                     case 45:
                        return new ChestMenu(MenuType.GENERIC_9x5, containerId, playerInventory, te, 5);
                     case 54:
                        return new ChestMenu(MenuType.GENERIC_9x6, containerId, playerInventory, te, 6);
                     default:
                        return new ChestMenu(MenuType.GENERIC_9x3, containerId, playerInventory, te, 3);
                  }
               } else {
                  return null;
               }
            }
         };
      } else {
         return this == ModBlocks.TREASURE_CHEST && level.getBlockEntity(pos) instanceof ChestBlockEntity chest ? new MenuProvider() {
            public Component getDisplayName() {
               return chest.getDisplayName();
            }

            @Nullable
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
               if (chest.canOpen(player)) {
                  chest.unpackLootTable(player);
                  return ChestMenu.sixRows(containerId, playerInventory, chest);
               } else {
                  return null;
               }
            }
         } : super.getMenuProvider(state, level, pos);
      }
   }

   public static enum Variant implements StringRepresentable {
      NORMAL("normal"),
      PRESENT("present");

      private final String serializedName;

      private Variant(String serializedName) {
         this.serializedName = serializedName;
      }

      @Nonnull
      public String getSerializedName() {
         return this.serializedName;
      }
   }
}
