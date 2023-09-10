package iskallia.vault.block.entity;

import com.google.common.collect.Lists;
import iskallia.vault.VaultMod;
import iskallia.vault.container.VaultDiffuserUpgradedContainer;
import iskallia.vault.container.base.SimpleSidedContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.ClientboundTESyncMessage;
import iskallia.vault.network.message.DiffuserUpgradedParticleMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class VaultDiffuserUpgradedTileEntity extends BlockEntity implements MenuProvider {
   private static final Random rand = new Random();
   private static final BlockPos[] list = new BlockPos[]{
      BlockPos.ZERO.north().east(), BlockPos.ZERO.north().west(), BlockPos.ZERO.south().east(), BlockPos.ZERO.south().west()
   };
   private final VaultDiffuserUpgradedTileEntity.DiffuserInput inputInv = new VaultDiffuserUpgradedTileEntity.DiffuserInput();
   private final VaultDiffuserUpgradedTileEntity.DiffuserOutput outputInv = new VaultDiffuserUpgradedTileEntity.DiffuserOutput(1, this);
   private int processTick = 0;
   private int processTickLast = 0;
   private LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(
      this.inputInv, new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH}
   );
   private LazyOptional<? extends IItemHandler>[] outputHandlers = SidedInvWrapper.create(this.outputInv, new Direction[]{Direction.DOWN});

   public VaultDiffuserUpgradedTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_HARVESTER_ENTITY, pWorldPosition, pBlockState);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inputInv.stillValid(player) : false;
   }

   private static IntStream getSlots(Container p_59340_, Direction p_59341_) {
      return p_59340_ instanceof WorldlyContainer
         ? IntStream.of(((WorldlyContainer)p_59340_).getSlotsForFace(p_59341_))
         : IntStream.range(0, p_59340_.getContainerSize());
   }

   private static boolean isEmptyContainer(Container pContainer, Direction pDirection) {
      return getSlots(pContainer, pDirection).allMatch(p_59319_ -> pContainer.getItem(p_59319_).isEmpty());
   }

   private static boolean canTakeItemFromContainer(Container pContainer, ItemStack pStack, int pSlot, Direction pDirection) {
      return !(pContainer instanceof WorldlyContainer) || ((WorldlyContainer)pContainer).canTakeItemThroughFace(pSlot, pStack, pDirection);
   }

   public static boolean suckInItems(Level p_155553_, VaultDiffuserUpgradedTileEntity p_155554_) {
      Boolean ret = extractHook(p_155553_, p_155554_);
      if (ret != null) {
         return ret;
      } else {
         Container container = getSourceContainer(p_155553_, p_155554_.getBlockPos());
         if (container == null) {
            return false;
         } else {
            Direction direction = Direction.DOWN;
            return !isEmptyContainer(container, direction)
               && getSlots(container, direction).anyMatch(p_59363_ -> tryTakeInItemFromSlot(p_155554_, container, p_59363_, direction));
         }
      }
   }

   private static boolean isFull(IItemHandler itemHandler) {
      for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
         ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
         if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot)) {
            return false;
         }
      }

      return true;
   }

   private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
      for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
         stack = insertStack(source, destination, destInventory, stack, slot);
      }

      return stack;
   }

   private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
      ItemStack itemstack = destInventory.getStackInSlot(slot);
      if (destInventory.insertItem(slot, stack, true).getCount() != 64) {
         boolean insertedItem = false;
         boolean inventoryWasEmpty = isEmpty(destInventory);
         if (itemstack.isEmpty()) {
            destInventory.insertItem(slot, stack, false);
            stack = ItemStack.EMPTY;
            insertedItem = true;
         } else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
            int originalSize = stack.getCount();
            stack = destInventory.insertItem(slot, stack, false);
            insertedItem = originalSize < stack.getCount();
         }
      }

      return stack;
   }

   private static boolean isEmpty(IItemHandler itemHandler) {
      for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
         ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
         if (stackInSlot.getCount() > 0) {
            return false;
         }
      }

      return true;
   }

   public static boolean insertHook(VaultDiffuserUpgradedTileEntity diffuser) {
      return getItemHandler(diffuser.getLevel(), diffuser, Direction.DOWN).map(destinationResult -> {
         IItemHandler itemHandler = (IItemHandler)destinationResult.getKey();
         Object destination = destinationResult.getValue();
         if (isFull(itemHandler)) {
            return false;
         } else {
            if (!diffuser.outputInv.getItem(0).isEmpty()) {
               ItemStack originalSlotContents = diffuser.outputInv.getItem(0).copy();
               ItemStack insertStack = diffuser.outputInv.removeItem(0, 64);
               ItemStack remainder = putStackInInventoryAllSlots(diffuser, destination, itemHandler, insertStack);
               if (remainder.getCount() != 64) {
                  originalSlotContents.setCount(originalSlotContents.getCount() - 64 + remainder.getCount());
                  diffuser.outputInv.setItem(0, originalSlotContents);
                  return true;
               }

               diffuser.outputInv.setItem(0, originalSlotContents);
            }

            return false;
         }
      }).orElse(false);
   }

   @Nullable
   public static Boolean extractHook(Level level, VaultDiffuserUpgradedTileEntity dest) {
      return getItemHandler(level, dest, Direction.UP)
         .map(
            itemHandlerResult -> {
               IItemHandler handler = (IItemHandler)itemHandlerResult.getKey();

               for (int i = 0; i < handler.getSlots(); i++) {
                  int count = handler.getStackInSlot(i).getCount();
                  ItemStack extractItem = handler.extractItem(i, count, true);
                  if (!extractItem.isEmpty()) {
                     for (int j = 0; j < dest.inputInv.getContainerSize(); j++) {
                        ItemStack destStack = dest.inputInv.getItem(j);
                        if (dest.inputInv.canPlaceItem(j, extractItem)
                           && (
                              destStack.isEmpty()
                                 || destStack.getCount() < destStack.getMaxStackSize()
                                    && destStack.getCount() < dest.inputInv.getMaxStackSize()
                                    && ItemHandlerHelper.canItemStacksStack(extractItem, destStack)
                           )) {
                           extractItem = handler.extractItem(i, count, false);
                           if (destStack.isEmpty()) {
                              dest.inputInv.setItem(j, extractItem);
                           } else {
                              destStack.grow(count);
                              dest.inputInv.setItem(j, destStack);
                           }

                           dest.setChanged();
                           return true;
                        }
                     }
                  }
               }

               return false;
            }
         )
         .orElse(null);
   }

   private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, VaultDiffuserUpgradedTileEntity diffuser, Direction diffuserFacing) {
      double x = (double)diffuser.getBlockPos().getX() + diffuserFacing.getStepX();
      double y = (double)diffuser.getBlockPos().getY() + diffuserFacing.getStepY();
      double z = (double)diffuser.getBlockPos().getZ() + diffuserFacing.getStepZ();
      return getItemHandler(level, x, y, z, diffuserFacing.getOpposite());
   }

   public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, Direction side) {
      int i = Mth.floor(x);
      int j = Mth.floor(y);
      int k = Mth.floor(z);
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState state = worldIn.getBlockState(blockpos);
      if (state.hasBlockEntity()) {
         BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
         if (blockEntity != null) {
            return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).map(capability -> ImmutablePair.of(capability, blockEntity));
         }
      }

      return Optional.empty();
   }

   public static boolean addItem(Container p_59332_, ItemEntity p_59333_) {
      boolean flag = false;
      ItemStack itemstack = p_59333_.getItem().copy();
      ItemStack itemstack1 = addItem((Container)null, p_59332_, itemstack, (Direction)null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_59333_.discard();
      } else {
         p_59333_.setItem(itemstack1);
      }

      return flag;
   }

   public static ItemStack addItem(@Nullable Container pSource, Container pDestination, ItemStack pStack, @Nullable Direction pDirection) {
      if (pDestination instanceof WorldlyContainer worldlycontainer && pDirection != null) {
         int[] aint = worldlycontainer.getSlotsForFace(pDirection);

         for (int k = 0; k < aint.length && !pStack.isEmpty(); k++) {
            pStack = tryMoveInItem(pSource, pDestination, pStack, aint[k], pDirection);
         }
      } else {
         int i = pDestination.getContainerSize();

         for (int j = 0; j < i && !pStack.isEmpty(); j++) {
            pStack = tryMoveInItem(pSource, pDestination, pStack, j, pDirection);
         }
      }

      return pStack;
   }

   private static boolean canPlaceItemInContainer(Container pContainer, ItemStack pStack, int pSlot, @Nullable Direction pDirection) {
      return !pContainer.canPlaceItem(pSlot, pStack)
         ? false
         : !(pContainer instanceof WorldlyContainer) || ((WorldlyContainer)pContainer).canPlaceItemThroughFace(pSlot, pStack, pDirection);
   }

   private static boolean canMergeItems(ItemStack pStack1, ItemStack pStack2) {
      if (!pStack1.is(pStack2.getItem())) {
         return false;
      } else if (pStack1.getDamageValue() != pStack2.getDamageValue()) {
         return false;
      } else {
         return pStack1.getCount() > pStack1.getMaxStackSize() ? false : ItemStack.tagMatches(pStack1, pStack2);
      }
   }

   private static ItemStack tryMoveInItem(@Nullable Container pSource, Container pDestination, ItemStack pStack, int pSlot, @Nullable Direction pDirection) {
      ItemStack itemstack = pDestination.getItem(pSlot);
      if (canPlaceItemInContainer(pDestination, pStack, pSlot, pDirection)) {
         boolean flag = false;
         if (itemstack.isEmpty()) {
            pDestination.setItem(pSlot, pStack);
            pStack = ItemStack.EMPTY;
            flag = true;
         } else if (canMergeItems(itemstack, pStack)) {
            int i = pStack.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(pStack.getCount(), i);
            pStack.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            pDestination.setChanged();
         }
      }

      return pStack;
   }

   private static boolean tryTakeInItemFromSlot(VaultDiffuserUpgradedTileEntity diffuser, Container pContainer, int pSlot, Direction pDirection) {
      ItemStack itemstack = pContainer.getItem(pSlot);
      if (!itemstack.isEmpty() && canTakeItemFromContainer(pContainer, itemstack, pSlot, pDirection)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack stack = pContainer.getItem(pSlot);
         ItemStack itemstack2 = addItem(pContainer, diffuser.inputInv, pContainer.removeItem(pSlot, stack.getCount()), (Direction)null);
         if (itemstack2.isEmpty()) {
            pContainer.setChanged();
            return true;
         }

         pContainer.setItem(pSlot, itemstack1);
      }

      return false;
   }

   @Nullable
   private static Container getAttachedContainer(Level p_155593_, BlockPos p_155594_) {
      return getContainerAt(p_155593_, p_155594_.relative(Direction.DOWN));
   }

   @Nullable
   private static Container getSourceContainer(Level p_155593_, BlockPos p_155594_) {
      return getContainerAt(p_155593_, p_155594_.relative(Direction.UP));
   }

   @Nullable
   public static Container getContainerAt(Level p_59391_, BlockPos p_59392_) {
      return getContainerAt(p_59391_, p_59392_.getX() + 0.5, p_59392_.getY() + 0.5, p_59392_.getZ() + 0.5);
   }

   @Nullable
   private static Container getContainerAt(Level pLevel, double pX, double pY, double pZ) {
      Container container = null;
      BlockPos blockpos = new BlockPos(pX, pY, pZ);
      BlockState blockstate = pLevel.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block instanceof WorldlyContainerHolder) {
         container = ((WorldlyContainerHolder)block).getContainer(blockstate, pLevel, blockpos);
      } else if (blockstate.hasBlockEntity()) {
         BlockEntity blockentity = pLevel.getBlockEntity(blockpos);
         if (blockentity instanceof Container) {
            container = (Container)blockentity;
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
               container = ChestBlock.getContainer((ChestBlock)block, blockstate, pLevel, blockpos, true);
            }
         }
      }

      if (container == null) {
         List<Entity> list = pLevel.getEntities(
            (Entity)null, new AABB(pX - 0.5, pY - 0.5, pZ - 0.5, pX + 0.5, pY + 0.5, pZ + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR
         );
         if (!list.isEmpty()) {
            container = (Container)list.get(pLevel.random.nextInt(list.size()));
         }
      }

      return container;
   }

   private static boolean tryMoveItems(Level pLevel, BlockPos pBlockPos, BlockState pBlockState, VaultDiffuserUpgradedTileEntity diffuser, BooleanSupplier pB) {
      if (pLevel.isClientSide) {
         return false;
      } else {
         boolean flag = false;
         if (!diffuser.outputInv.isEmpty()) {
            flag = ejectItems(pLevel, pBlockPos, pBlockState, diffuser);
         }

         if (diffuser.outputInv.isEmpty()) {
            diffuser.sync();
         }

         flag |= pB.getAsBoolean();
         if (flag) {
            setChanged(pLevel, pBlockPos, pBlockState);
            return true;
         } else {
            return false;
         }
      }
   }

   private static boolean ejectItems(Level pLevel, BlockPos pPos, BlockState pState, VaultDiffuserUpgradedTileEntity diffuser) {
      if (insertHook(diffuser)) {
         return true;
      } else {
         Container container = getAttachedContainer(pLevel, pPos);
         if (container == null) {
            return false;
         } else if (isFullContainer(container, Direction.DOWN)) {
            return false;
         } else {
            for (int i = 0; i < diffuser.outputInv.getContainerSize(); i++) {
               if (!diffuser.outputInv.getItem(i).isEmpty()) {
                  ItemStack itemstack = diffuser.outputInv.getItem(i).copy();
                  ItemStack itemstack1 = addItem(diffuser.outputInv, container, diffuser.outputInv.removeItem(i, 64), Direction.DOWN);
                  if (itemstack1.getCount() != 64) {
                     itemstack.setCount(itemstack.getCount() - 64 + itemstack1.getCount());
                     diffuser.outputInv.setItem(0, itemstack);
                     container.setChanged();
                     return true;
                  }

                  diffuser.outputInv.setItem(i, itemstack);
               }
            }

            return false;
         }
      }
   }

   private static boolean isFullContainer(Container pContainer, Direction pDirection) {
      return getSlots(pContainer, pDirection).allMatch(slot -> {
         ItemStack itemstack = pContainer.getItem(slot);
         return itemstack.getCount() >= itemstack.getMaxStackSize();
      });
   }

   public static void tick(Level world, BlockPos pos, BlockState state, VaultDiffuserUpgradedTileEntity tile) {
      tile.processTickLast = tile.processTick;
      if (world.isClientSide()) {
         if (tile.getProgressPercent() > 0.0F && tile.getProgressPercent() < 0.3F) {
            Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5);

            for (BlockPos blockpos : list) {
               float f = blockpos.getX() / 3.5F;
               float f1 = 0.5F;
               float f2 = blockpos.getZ() / 3.5F;
               world.addParticle((ParticleOptions)ModParticles.REVERSE_DIFFUSER_UPGRADED.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
         }

         if (tile.getProgressPercent() > 0.6F && tile.getProgressPercent() < 0.9F) {
            Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5);

            for (BlockPos blockpos : list) {
               float f = blockpos.getX() / 2.5F;
               float f1 = -0.5F;
               float f2 = blockpos.getZ() / 2.5F;
               world.addParticle((ParticleOptions)ModParticles.DIFFUSER_UPGRADED.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
         }
      }

      if (!world.isClientSide()) {
         boolean bool = tryMoveItems(world, tile.getBlockPos(), tile.getBlockState(), tile, () -> suckInItems(world, tile));
         if (!tile.canCraft()) {
            tile.resetProcess(world);
         } else {
            if (tile.processTick == 0 && tile.level != null) {
               tile.level
                  .playSound(
                     null,
                     tile.getBlockPos(),
                     SoundEvents.CONDUIT_ACTIVATE,
                     SoundSource.BLOCKS,
                     0.15F + new Random().nextFloat() * 0.05F,
                     0.85F + new Random().nextFloat() * 0.05F
                  );
            }

            tile.processTick++;
            if (tile.processTick >= ModConfigs.VAULT_DIFFUSER.getProcessingTickTime()) {
               tile.finishCraft();
            }

            tile.setChanged();
            tile.sync();
         }
      }
   }

   private void finishCraft() {
      if (this.level != null) {
         this.level
            .playSound(
               null,
               this.getBlockPos(),
               SoundEvents.BEACON_POWER_SELECT,
               SoundSource.BLOCKS,
               0.15F + new Random().nextFloat() * 0.05F,
               0.85F + new Random().nextFloat() * 0.05F
            );
      }

      this.processTick = 0;

      for (int i = 0; i < this.inputInv.getContainerSize(); i++) {
         int output = this.getRecipeOutput(i);
         if (output != 0) {
            int count = this.inputInv.getItem(i).getCount();
            this.inputInv.removeItem(i, count);
            List<ItemStack> stacksToAdd = this.getUseRelatedOutput(ModConfigs.VAULT_DIFFUSER.generateOutput(output * count));
            if (this.outputInv.getItem(0).isEmpty()) {
               this.outputInv.setOverSizedStack(0, new OverSizedItemStack(stacksToAdd.get(0), output * count));
            } else {
               this.outputInv.setOverSizedStack(0, ((OverSizedItemStack)this.outputInv.getOverSizedContents().get(0)).addCopy(output * count));
            }

            this.outputInv.setChanged();
         }
      }

      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new DiffuserUpgradedParticleMessage(this.getBlockPos()));
   }

   private List<ItemStack> getUseRelatedOutput(List<ItemStack> output) {
      List<ItemStack> list = new ArrayList<>();
      output.forEach(stack -> {
         float out = stack.getCount();
         int resultCount = Mth.floor(out);
         if (resultCount < 1 && out > 0.0F && rand.nextFloat() < out) {
            resultCount++;
         }

         ItemStack copyOut = stack.copy();
         copyOut.setCount(resultCount);
         list.add(copyOut);
      });
      return list;
   }

   private boolean canCraft() {
      for (int i = 0; i < this.inputInv.getContainerSize(); i++) {
         int output = this.getRecipeOutput(i);
         if (output != 0) {
            return this.canAddItem();
         }
      }

      return false;
   }

   private boolean canAddItem() {
      ItemStack stack = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(ModConfigs.VAULT_DIFFUSER.getOutputItem()));
      ItemStack slotItem = this.outputInv.getItem(0);
      return slotItem.isEmpty() || ItemStack.isSameItemSameTags(slotItem, stack);
   }

   @Nullable
   private int getRecipeOutput(int index) {
      ItemStack input = this.inputInv.getItem(index);
      if (!this.isValidInput(input)) {
         return 0;
      } else {
         int output;
         if (ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(input.getItem().getRegistryName())) {
            output = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(input.getItem().getRegistryName());
         } else {
            output = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(VaultMod.id("default"));
         }

         return output;
      }
   }

   private void triggerItemChange() {
      if (this.canCraft() && this.processTick == 0) {
         this.startProcess(null);
      }
   }

   public boolean isValidInput(ItemStack input) {
      return input.isEmpty() ? false : ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(input.getItem().getRegistryName());
   }

   public void sync() {
      if (this.level != null && !this.level.isClientSide) {
         CompoundTag saveTag = new CompoundTag();
         this.saveAdditional(saveTag);
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
               new ClientboundTESyncMessage(this.worldPosition, saveTag)
            );
         this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
      }
   }

   private void resetProcess(@Nullable Level world) {
      this.startProcess(world);
   }

   private void startProcess(@Nullable Level world) {
      int prevTick = this.processTick;
      this.processTick = 0;
      this.setChanged();
      if (prevTick != this.processTick) {
         this.sync();
      }
   }

   public float getProgressPercent() {
      return (float)this.processTick / ModConfigs.VAULT_DIFFUSER.getProcessingTickTime();
   }

   public float getProgressLastPercent() {
      return (float)this.processTickLast / ModConfigs.VAULT_DIFFUSER.getProcessingTickTime();
   }

   public VaultDiffuserUpgradedTileEntity.DiffuserInput getInputInv() {
      return this.inputInv;
   }

   public VaultDiffuserUpgradedTileEntity.DiffuserOutput getOutputInv() {
      return this.outputInv;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.outputInv.load(tag.getCompound("output"));
      NBTHelper.deserializeSimpleContainer(this.inputInv, tag.getList("inventory", 10));
      this.processTick = tag.getInt("processTick");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      CompoundTag output = new CompoundTag();
      this.outputInv.save(output);
      tag.put("output", output);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inputInv));
      tag.putInt("processTick", this.processTick);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultDiffuserUpgradedContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP) {
            return this.handlers[0].cast();
         }

         if (facing == Direction.EAST) {
            return this.handlers[1].cast();
         }

         if (facing == Direction.NORTH) {
            return this.handlers[2].cast();
         }

         if (facing == Direction.WEST) {
            return this.handlers[3].cast();
         }

         if (facing == Direction.SOUTH) {
            return this.handlers[4].cast();
         }

         if (facing == Direction.DOWN) {
            return this.outputHandlers[0].cast();
         }
      }

      return super.getCapability(capability, facing);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      Arrays.stream(this.handlers).forEach(LazyOptional::invalidate);
      Arrays.stream(this.outputHandlers).forEach(LazyOptional::invalidate);
   }

   public void reviveCaps() {
      super.reviveCaps();
      this.handlers = SidedInvWrapper.create(this.inputInv, new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH});
      this.outputHandlers = SidedInvWrapper.create(this.outputInv, new Direction[]{Direction.DOWN});
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnDiffuserParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 40; i++) {
            Random random = level.getRandom();
            float rotation = random.nextFloat() * 360.0F;
            float length = random.nextFloat() / 10.0F + 0.1F;
            Vec3 offset = new Vec3(Math.cos(rotation) * length, 0.0, Math.sin(rotation) * length);
            level.addParticle(
               (ParticleOptions)ModParticles.DIFFUSER_COMPLETE.get(),
               true,
               pos.getX() + 0.5 + offset.x / 20.0,
               pos.getY() + random.nextDouble() * 0.15F + 0.8F,
               pos.getZ() + 0.5 + offset.z / 20.0,
               offset.x * 2.0,
               0.01,
               offset.z * 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            float rotation = random.nextFloat() * 360.0F;
            float length = random.nextFloat() / 10.0F + 0.1F;
            Vec3 offset = new Vec3(Math.cos(rotation) * length, 0.0, Math.sin(rotation) * length);
            level.addParticle(
               (ParticleOptions)ModParticles.DIFFUSER_COMPLETE.get(),
               true,
               pos.getX() + 0.5 + offset.x / 20.0,
               pos.getY() + random.nextDouble() * 0.15F + 0.8F,
               pos.getZ() + 0.5 + offset.z / 20.0,
               offset.x * 3.0,
               0.01,
               offset.z * 3.0
            );
         }
      }
   }

   public class DiffuserInput extends SimpleSidedContainer {
      public DiffuserInput() {
         super(9);
      }

      @Override
      public List<Direction> getAccessibleSlots(int slot) {
         return slot == 0
            ? Lists.newArrayList(new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH})
            : Lists.newArrayList(new Direction[]{Direction.DOWN});
      }

      public boolean canPlaceItem(int slot, ItemStack stack) {
         return ModConfigs.VAULT_DIFFUSER.contains(stack);
      }

      public void setChanged() {
         super.setChanged();
         VaultDiffuserUpgradedTileEntity.this.setChanged();
         VaultDiffuserUpgradedTileEntity.this.triggerItemChange();
      }

      public ItemStack addItem(ItemStack pStack) {
         ItemStack $$1 = pStack.copy();
         this.moveItemToOccupiedSlotsWithSameType($$1);
         if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.moveItemToEmptySlots($$1);
            return $$1.isEmpty() ? ItemStack.EMPTY : $$1;
         }
      }

      private void moveItemToEmptySlots(ItemStack pStack) {
         for (int $$1 = 1; $$1 < this.getContainerSize(); $$1++) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) {
               this.setItem($$1, pStack.copy());
               pStack.setCount(0);
               return;
            }
         }
      }

      private void moveItemToOccupiedSlotsWithSameType(ItemStack pStack) {
         for (int $$1 = 1; $$1 < this.getContainerSize(); $$1++) {
            ItemStack $$2 = this.getItem($$1);
            if (ItemStack.isSameItemSameTags($$2, pStack)) {
               this.moveItemsBetweenStacks(pStack, $$2);
               if (pStack.isEmpty()) {
                  return;
               }
            }
         }
      }

      private void moveItemsBetweenStacks(ItemStack p_19186_, ItemStack p_19187_) {
         int $$2 = Math.min(this.getMaxStackSize(), p_19187_.getMaxStackSize());
         int $$3 = Math.min(p_19186_.getCount(), $$2 - p_19187_.getCount());
         if ($$3 > 0) {
            p_19187_.grow($$3);
            p_19186_.shrink($$3);
            this.setChanged();
         }
      }
   }

   public class DiffuserOutput extends OverSizedInventory implements WorldlyContainer {
      private final Map<Direction, Set<Integer>> cachedSidedSlots = new HashMap<>();

      public DiffuserOutput(int size, BlockEntity tile) {
         super(size, tile);
         this.cacheSlots();
      }

      private void cacheSlots() {
         IntStream.range(0, this.getContainerSize())
            .forEach(slot -> this.getAccessibleSlots(slot).forEach(dir -> this.cachedSidedSlots.computeIfAbsent(dir, side -> new HashSet<>()).add(slot)));
      }

      public List<Direction> getAccessibleSlots(int slot) {
         return Lists.newArrayList(new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.DOWN});
      }

      public int[] getSlotsForFace(Direction side) {
         return Optional.ofNullable(this.cachedSidedSlots.get(side)).map(Collection::stream).orElse(Stream.empty()).mapToInt(Integer::intValue).toArray();
      }

      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return false;
      }

      public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
         return false;
      }

      public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
         return this.cachedSidedSlots.getOrDefault(side, Collections.emptySet()).contains(slot);
      }
   }
}
