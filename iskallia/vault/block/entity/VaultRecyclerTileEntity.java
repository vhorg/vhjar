package iskallia.vault.block.entity;

import com.google.common.collect.Lists;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.container.VaultRecyclerContainer;
import iskallia.vault.container.base.SimpleSidedContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.network.message.RecyclerParticleMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.PacketDistributor;

public class VaultRecyclerTileEntity extends BlockEntity implements MenuProvider {
   private static final Random rand = new Random();
   private final VaultRecyclerTileEntity.RecyclerInventory inventory = new VaultRecyclerTileEntity.RecyclerInventory();
   private UUID gearIdProcessing = null;
   private int processTick = 0;
   private LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this.inventory, new Direction[]{Direction.UP, Direction.DOWN});

   public VaultRecyclerTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_RECYCLER_ENTITY, pWorldPosition, pBlockState);
   }

   public static void tick(Level world, BlockPos pos, BlockState state, VaultRecyclerTileEntity tile) {
      if (!world.isClientSide() && tile.gearIdProcessing != null) {
         if (!tile.canCraft()) {
            tile.resetProcess(world);
         } else {
            tile.processTick++;
            if (tile.processTick >= ModConfigs.VAULT_RECYCLER.getProcessingTickTime()) {
               tile.finishCraft();
            }

            tile.setChanged();
            if (world instanceof ServerLevel serverWorld) {
               serverWorld.sendBlockUpdated(pos, state, state, 3);
            }
         }
      }
   }

   private void finishCraft() {
      VaultRecyclerConfig.RecyclerOutput output = this.getRecipeOutput();
      if (output != null) {
         if (this.level != null) {
            this.level
               .playSound(
                  null,
                  this.getBlockPos(),
                  SoundEvents.GENERIC_EXTINGUISH_FIRE,
                  SoundSource.BLOCKS,
                  0.5F + new Random().nextFloat() * 0.25F,
                  0.75F + new Random().nextFloat() * 0.25F
               );
         }

         ItemStack input = this.inventory.getItem(0).copy();
         this.inventory.removeItem(0, 1);
         MiscUtils.addStackToSlot(this.inventory, 1, this.getUseRelatedOutput(input, output.generateMainOutput()));
         MiscUtils.addStackToSlot(this.inventory, 2, this.getUseRelatedOutput(input, output.generateExtraOutput1()));
         MiscUtils.addStackToSlot(this.inventory, 3, this.getUseRelatedOutput(input, output.generateExtraOutput2()));
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new RecyclerParticleMessage(this.getBlockPos()));
      }
   }

   private ItemStack getUseRelatedOutput(ItemStack input, ItemStack output) {
      float chance = this.getResultPercentage(input);
      float out = output.getCount() * chance;
      int resultCount = Mth.floor(out);
      if (resultCount < 1 && out > 0.0F && rand.nextFloat() < out) {
         resultCount++;
      }

      ItemStack copyOut = output.copy();
      copyOut.setCount(resultCount);
      return copyOut;
   }

   private boolean canCraft() {
      VaultRecyclerConfig.RecyclerOutput output = this.getRecipeOutput();
      if (output == null) {
         return false;
      } else if (!MiscUtils.canFullyMergeIntoSlot(this.inventory, 1, output.getMainOutputMatching())) {
         return false;
      } else {
         return !MiscUtils.canFullyMergeIntoSlot(this.inventory, 2, output.getExtraOutput1Matching())
            ? false
            : MiscUtils.canFullyMergeIntoSlot(this.inventory, 3, output.getExtraOutput2Matching());
      }
   }

   @Nullable
   private VaultRecyclerConfig.RecyclerOutput getRecipeOutput() {
      ItemStack input = this.inventory.getItem(0);
      if (!this.isValidInput(input)) {
         return null;
      } else {
         VaultRecyclerConfig.RecyclerOutput output;
         if (input.is(ModItems.TRINKET)) {
            output = ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput();
         } else {
            output = ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput();
         }

         return output;
      }
   }

   private void triggerItemChange() {
      ItemStack input = this.inventory.getItem(0);
      if (!this.isValidInput(input)) {
         this.resetProcess(null);
      } else {
         AttributeGearData.readUUID(input).ifPresent(itemId -> {
            if ((this.gearIdProcessing == null || !this.gearIdProcessing.equals(itemId)) && this.canCraft()) {
               this.startProcess(null, itemId);
            }
         });
      }
   }

   public boolean isValidInput(ItemStack input) {
      if (input.isEmpty()) {
         return false;
      } else {
         Item inputItem = input.getItem();
         return !(inputItem instanceof VaultGearItem) && !(inputItem instanceof TrinketItem) ? false : AttributeGearData.hasData(input);
      }
   }

   public float getResultPercentage(ItemStack input) {
      if (input.isEmpty()) {
         return 0.0F;
      } else if (input.getItem() instanceof VaultGearItem) {
         return VaultGearData.read(input).getState() != VaultGearState.IDENTIFIED ? 1.0F : 1.0F - (float)input.getDamageValue() / input.getMaxDamage();
      } else if (input.getItem() instanceof TrinketItem) {
         return !TrinketItem.isIdentified(input) ? 1.0F : 1.0F - (float)TrinketItem.getUsedVaults(input).size() / TrinketItem.getUses(input);
      } else {
         return 0.0F;
      }
   }

   private void resetProcess(@Nullable Level world) {
      this.startProcess(world, null);
   }

   private void startProcess(@Nullable Level world, UUID id) {
      int prevTick = this.processTick;
      this.gearIdProcessing = id;
      this.processTick = 0;
      this.setChanged();
      if (prevTick != this.processTick && world instanceof ServerLevel serverWorld) {
         serverWorld.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public float getProgressPercent() {
      return (float)this.processTick / ModConfigs.VAULT_RECYCLER.getProcessingTickTime();
   }

   public VaultRecyclerTileEntity.RecyclerInventory getInventory() {
      return this.inventory;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
      this.processTick = tag.getInt("processTick");
      if (tag.hasUUID("gearIdProcessing")) {
         this.gearIdProcessing = tag.getUUID("gearIdProcessing");
      }
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
      tag.putInt("processTick", this.processTick);
      if (this.gearIdProcessing != null) {
         tag.putUUID("gearIdProcessing", this.gearIdProcessing);
      }
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultRecyclerContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
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

         if (facing == Direction.DOWN) {
            return this.handlers[1].cast();
         }
      }

      return super.getCapability(capability, facing);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      Arrays.stream(this.handlers).forEach(LazyOptional::invalidate);
   }

   public void reviveCaps() {
      super.reviveCaps();
      this.handlers = SidedInvWrapper.create(this.inventory, new Direction[]{Direction.UP, Direction.DOWN});
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnRecycleParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 4; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LAVA,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.25,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LAVA,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 10.0,
               random.nextDouble() * 0.05,
               offset.z / 10.0
            );
         }
      }
   }

   public class RecyclerInventory extends SimpleSidedContainer {
      public RecyclerInventory() {
         super(4);
      }

      @Override
      public List<Direction> getAccessibleSlots(int slot) {
         return slot == 0 ? Lists.newArrayList(new Direction[]{Direction.UP}) : Lists.newArrayList(new Direction[]{Direction.DOWN});
      }

      public boolean canPlaceItem(int slot, ItemStack stack) {
         if (stack.isEmpty()) {
            return false;
         } else {
            return slot != 0 ? false : stack.is(ModItems.TRINKET) || stack.getItem() instanceof VaultGearItem;
         }
      }

      public void setChanged() {
         super.setChanged();
         VaultRecyclerTileEntity.this.setChanged();
         VaultRecyclerTileEntity.this.triggerItemChange();
      }
   }
}
