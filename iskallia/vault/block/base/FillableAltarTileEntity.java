package iskallia.vault.block.base;

import com.mojang.brigadier.StringReader;
import iskallia.vault.core.Version;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TileParser;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.init.ModBlocks;
import java.awt.Color;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
      CommonEvents.ALTAR_PROGRESS
         .invoke((ServerLevel)this.level, player, this.getBlockState(), this.getBlockPos(), this, this.currentProgress, this.maxProgress, false);
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
         this.replacement = new TileParser(new StringReader(nbt.getString("Replacement")), ModBlocks.ERROR_BLOCK, true).toTile();
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
         this.replacement.copyInto(tile);
         world.setBlock(pos, tile.getState().asBlockState(), 3);
         if (tile.getNbt() != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
               blockEntity.load(tile.getNbt());
            }
         }
      }
   }

   public void placeReward(Level world, BlockPos pos, int color, RandomSource random) {
      LootPool pool = VaultRegistry.LOOT_POOL.getKey(this.reward).get(Version.latest());
      pool.getRandomFlat(Version.latest(), random).ifPresent(entry -> {
         world.addFreshEntity(FloatingItemEntity.create(world, pos, entry.getStack(random)).setColor(color));
         world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 0.2F);
      });
   }
}
