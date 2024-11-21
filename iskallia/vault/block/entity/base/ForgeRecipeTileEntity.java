package iskallia.vault.block.entity.base;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.integration.IntegrationRefinedStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class ForgeRecipeTileEntity extends BlockEntity implements MenuProvider, FilteredInputInventoryTileEntity, InventoryRetainerTileEntity {
   private final OverSizedInventory inventory;
   private final ResultContainer output = new ResultContainer() {
      public void setChanged() {
         super.setChanged();
         ForgeRecipeTileEntity.this.setChanged();
      }
   };
   private final List<ForgeRecipeType> supportedRecipeTypes;

   public ForgeRecipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize, ForgeRecipeType... supportedRecipeTypes) {
      super(type, pos, state);
      this.inventory = new OverSizedInventory.FilteredInsert(inventorySize, this, this::canInsertRecipeInput);
      this.supportedRecipeTypes = new ArrayList<>();
      this.supportedRecipeTypes.addAll(Arrays.asList(supportedRecipeTypes));
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public ResultContainer getResultContainer() {
      return this.output;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   public List<ForgeRecipeType> getSupportedRecipeTypes() {
      return this.supportedRecipeTypes;
   }

   public boolean stillValid(Player player) {
      return this.getLevel() != null && this.getLevel().getBlockEntity(this.getBlockPos()) == this ? this.getInventory().stillValid(player) : false;
   }

   protected boolean canInsertRecipeInput(int slot, @Nonnull ItemStack stack) {
      return stack.isEmpty()
         ? false
         : this.getSupportedRecipeTypes().stream().anyMatch(type -> type.getRecipeConfig().getInputItemCache().contains(stack.getItem()));
   }

   @Override
   public boolean isInventorySideAccessible(@Nullable Direction side) {
      return true;
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      if (IntegrationRefinedStorage.shouldPreventImportingCapability(this.getLevel(), this.getBlockPos(), side)) {
         return super.getCapability(cap, side);
      } else {
         return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.getInventoryCapability(side) : super.getCapability(cap, side);
      }
   }

   protected <T> LazyOptional<T> getInventoryCapability(Direction side) {
      return this.getFilteredInputCapability(side, new Container[]{this.inventory});
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
      return this.getLevel() == null ? null : this.createMenu(windowId, playerInventory);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.getInventory().load(tag);
      this.getResultContainer().setItem(0, ItemStack.of(tag.getCompound("result")));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.getInventory().save(tag);
      tag.put("result", this.getResultContainer().getItem(0).serializeNBT());
   }

   public static InventoryRetainerTileEntity.ContentDisplayInfo addInventoryTooltip(ItemStack stack, CompoundTag tag) {
      return InventoryRetainerTileEntity.displayContentsOverSized(5, stacks -> {
         ItemStack result = ItemStack.of(tag.getCompound("result"));
         if (!result.isEmpty()) {
            stacks.add(OverSizedItemStack.of(result));
         }

         stacks.addAll(OverSizedInventory.loadContents("inventory", tag));
      });
   }

   @Override
   public void storeInventoryContents(CompoundTag tag) {
      this.getInventory().save("inventory", tag);
      tag.put("result", this.getResultContainer().getItem(0).copy().serializeNBT());
   }

   @Override
   public void loadInventoryContents(CompoundTag tag) {
      this.getInventory().load("inventory", tag);
      this.getResultContainer().setItem(0, ItemStack.of(tag.getCompound("result")));
   }

   @Override
   public void clearInventoryContents() {
      this.getInventory().clearContent();
      this.getResultContainer().clearContent();
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnForgeParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 8; i++) {
            Random random = level.getRandom();
            Direction direction = level.getBlockState(pos).hasProperty(FacedBlock.FACING)
               ? (Direction)level.getBlockState(pos).getValue(FacedBlock.FACING)
               : Direction.NORTH;
            Axis direction$axis = direction.getAxis();
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            double d3 = -0.52;
            double d4 = random.nextDouble() * 0.6 - 0.3;
            double d5 = direction$axis == Axis.X ? direction.getStepX() * d3 : d4;
            double d6 = random.nextDouble() * 6.0 / 16.0;
            double d7 = direction$axis == Axis.Z ? direction.getStepZ() * d3 : d4;
            level.addParticle(ParticleTypes.SMOKE, x + d5, y + d6, z + d7, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.LAVA, x + d5, y + d6, z + d7, 0.0, 0.0, 0.0);
         }
      }
   }
}
