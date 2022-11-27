package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class LootStatueTileEntity extends SkinnableTileEntity {
   private int currentTick = 0;
   private ItemStack lootItem = ItemStack.EMPTY;
   private int chipCount = 0;
   private ItemStack standItem;
   private BlockState stand = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
   private int wobbleTime = 0;

   public LootStatueTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.LOOT_STATUE_TILE_ENTITY, pos, state);
   }

   public LootStatueTileEntity(BlockEntityType type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public BlockState getStand() {
      return this.stand;
   }

   public int getCurrentTick() {
      return this.currentTick;
   }

   @Nonnull
   public ItemStack getLootItem() {
      return this.lootItem;
   }

   public void setLootItem(@Nonnull ItemStack stack) {
      this.lootItem = stack;
      this.setChanged();
      this.sendUpdates();
   }

   @Override
   protected void updateSkin() {
   }

   public boolean addChip() {
      if (this.chipCount >= ModConfigs.STATUE_LOOT.getMaxAccelerationChips()) {
         return false;
      } else {
         this.chipCount++;
         this.sendUpdates();
         return true;
      }
   }

   public ItemStack removeChip() {
      ItemStack stack = ItemStack.EMPTY;
      if (this.chipCount > 0) {
         this.chipCount--;
         stack = new ItemStack(ModItems.ACCELERATION_CHIP, 1);
         this.sendUpdates();
      }

      return stack;
   }

   public int getChipCount() {
      return this.chipCount;
   }

   public int getWobbleTime() {
      return this.wobbleTime;
   }

   public int getMaxWobble() {
      return 40;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, LootStatueTileEntity tile) {
      if (!level.isClientSide) {
         if (tile.currentTick++ >= tile.getModifiedInterval()) {
            tile.currentTick = 0;
            if (!tile.lootItem.isEmpty()) {
               ItemStack stack = tile.lootItem.copy();
               if (tile.poopItem(stack, false)) {
                  tile.setChanged();
               }
            }
         }
      } else if (tile.wobbleTime > 0) {
         tile.wobbleTime--;
      }
   }

   private int getModifiedInterval() {
      int interval = ModConfigs.STATUE_LOOT.getInterval();
      return this.getChipCount() == 0 ? interval : interval - ModConfigs.STATUE_LOOT.getIntervalDecrease(this.getChipCount());
   }

   private boolean poopItem(ItemStack stack, boolean simulate) {
      assert this.level != null;

      BlockPos down = this.getBlockPos().below();
      BlockEntity tileEntity = this.level.getBlockEntity(down);
      if (tileEntity != null) {
         LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
         handler.ifPresent(h -> ItemHandlerHelper.insertItemStacked(h, stack, simulate));
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().expandTowards(0.0, 2.0, 0.0);
   }

   public void saveAdditional(@NotNull CompoundTag nbt) {
      String nickname = this.skin.getLatestNickname();
      if (nickname != null) {
         nbt.putString("PlayerNickname", nickname);
      }

      if (this.chipCount != 0) {
         nbt.putInt("ChipCount", this.chipCount);
      }

      nbt.putInt("CurrentTick", this.getCurrentTick());
      nbt.put("LootItem", this.getLootItem().serializeNBT());
      if (this.stand.getBlock() != Blocks.SMOOTH_STONE_SLAB) {
         nbt.put("Stand", NbtUtils.writeBlockState(this.stand));
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("PlayerNickname")) {
         this.skin.updateSkin(nbt.getString("PlayerNickname"));
      }

      if (nbt.contains("ChipCount")) {
         this.chipCount = nbt.getInt("ChipCount");
      }

      this.lootItem = ItemStack.of(nbt.getCompound("LootItem"));
      this.currentTick = nbt.getInt("CurrentTick");
      if (nbt.contains("Stand")) {
         this.stand = NbtUtils.readBlockState(nbt.getCompound("Stand"));
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void setStand(ItemStack stack, BlockItem blockItem) {
      this.standItem = stack;
      this.stand = blockItem.getBlock().defaultBlockState();
      this.sendUpdates();
   }

   public void wobble() {
      this.wobbleTime = this.getMaxWobble();
   }
}
