package iskallia.vault.block.base;

import iskallia.vault.core.Version;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.AltarProgressEvent;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.entity.entity.FloatingGodAltarItemEntity;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.world.data.ServerVaults;
import java.awt.Color;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FillableAltarTileEntity extends BlockEntity {
   protected static final Random rand = new Random();
   protected int currentProgress = 0;
   protected int maxProgress = 0;
   protected boolean consumed = false;
   protected PartialTile replacement;
   protected ResourceLocation reward;

   public FillableAltarTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   public int getCurrentProgress() {
      return this.currentProgress;
   }

   public int getMaxProgress() {
      return this.maxProgress;
   }

   public boolean isCompleted() {
      return this.currentProgress >= this.maxProgress;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public void setConsumed() {
      this.consumed = true;
   }

   public void makeProgress(ServerPlayer player, int progress) {
      this.currentProgress += progress;
      this.currentProgress = Math.min(this.currentProgress, this.maxProgress);
      this.sendUpdates();
      AltarProgressEvent.Data result = CommonEvents.ALTAR_PROGRESS
         .invoke((ServerLevel)this.level, player, this.getBlockState(), this.getBlockPos(), this, this.currentProgress, this.maxProgress, false);
      if (result.getProgress() >= result.getTotal()) {
         player.getLevel().playSound(null, player.getOnPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 2.0F, 1.0F);
      }
   }

   public abstract Component getRequirementName();

   public abstract VaultGod getVaultGod();

   public abstract Component getRequirementUnit();

   public abstract Color getFillColor();

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putInt("CurrentProgress", this.currentProgress);
      nbt.putInt("MaxProgress", this.maxProgress);
      nbt.putBoolean("Consumed", this.consumed);
      if (this.replacement != null) {
         nbt.putString("Replacement", this.replacement.toString());
      }

      if (this.reward != null) {
         nbt.putString("Reward", this.reward.toString());
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.currentProgress = nbt.getInt("CurrentProgress");
      this.maxProgress = nbt.contains("MaxProgress") ? nbt.getInt("MaxProgress") : -1;
      this.consumed = nbt.getBoolean("Consumed");
      if (nbt.contains("Replacement", 8)) {
         this.replacement = PartialTile.parse(nbt.getString("Replacement"), true).orElse(PartialTile.ERROR);
      }

      if (nbt.contains("Reward", 8)) {
         this.reward = new ResourceLocation(nbt.getString("Reward"));
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void placeReplacement(Level world, BlockPos pos) {
      if (this.replacement != null) {
         PartialTile tile = PartialTile.at(world, pos);
         this.replacement.fillInto(tile);
         tile.place(world, pos, 3);
      }
   }

   public void placeReward(Level world, BlockPos pos, int color, RandomSource random) {
      LootPool pool = VaultRegistry.LOOT_POOL.getKey(this.reward).get(Version.latest());
      pool.getRandomFlat(Version.latest(), random)
         .ifPresent(
            entry -> {
               ItemStack stack = entry.getStack(random);
               Vault vault = ServerVaults.get(world).orElse(null);
               VaultLevelItem.doInitializeVaultLoot(stack, vault, null);
               stack = DataTransferItem.doConvertStack(stack);
               DataInitializationItem.doInitialize(stack);
               Direction facing = this.getBlockState().hasProperty(FacedBlock.FACING)
                  ? (Direction)this.getBlockState().getValue(FacedBlock.FACING)
                  : Direction.NORTH;
               FloatingGodAltarItemEntity floatingItemEntity = new FloatingGodAltarItemEntity(
                  world,
                  pos.getX() + 0.5 - facing.getNormal().getX() / 3.0F,
                  pos.getY() + 0.5 - facing.getNormal().getY() / 3.0F,
                  pos.getZ() + 0.5 - facing.getNormal().getZ() / 3.0F,
                  stack
               );
               world.addFreshEntity(floatingItemEntity.setColor(color));
               world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 0.2F);
            }
         );
   }
}
