package iskallia.vault.block.base;

import com.mojang.brigadier.StringReader;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TileParser;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FillableAltarTileEntity extends BlockEntity {
   protected static final Random rand = new Random();
   private int currentProgress = 0;
   protected int maxProgress = 0;
   protected PartialTile replacement = PartialTile.of(Blocks.AIR);

   public FillableAltarTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   public boolean initialized() {
      return this.getMaxProgress() > 0;
   }

   public int getCurrentProgress() {
      return this.currentProgress;
   }

   public int getMaxProgress() {
      return this.maxProgress;
   }

   public boolean isMaxedOut() {
      return this.currentProgress >= this.getMaxProgress();
   }

   public float progressPercentage() {
      return Math.min((float)this.getCurrentProgress() / this.getMaxProgress(), 1.0F);
   }

   public void makeProgress(ServerPlayer sPlayer, int deltaProgress, Consumer<ServerPlayer> onComplete) {
      if (this.initialized()) {
         this.currentProgress += deltaProgress;
         this.sendUpdates();
         if (this.isMaxedOut()) {
            onComplete.accept(sPlayer);
         }
      }
   }

   public static <A extends FillableAltarTileEntity> void tick(Level level, BlockPos pos, BlockState state, A tile) {
      if (!tile.initialized()) {
         ;
      }
   }

   protected float getMaxProgressMultiplier(UUID playerUUID) {
      if (this.getLevel() instanceof ServerLevel sWorld) {
         int favour = PlayerFavourData.get(sWorld).getFavour(playerUUID, this.getAssociatedVaultGod());
         return favour < 0 ? 1.0F + 0.2F * (Math.abs(favour) / 6.0F) : 1.0F - 0.75F * (Math.min((float)favour, 8.0F) / 8.0F);
      } else {
         return 1.0F;
      }
   }

   public abstract Component getRequirementName();

   public abstract PlayerFavourData.VaultGodType getAssociatedVaultGod();

   public abstract Component getRequirementUnit();

   public abstract Color getFillColor();

   protected abstract Optional<Integer> calcMaxProgress(VaultRaid var1);

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putInt("CurrentProgress", this.currentProgress);
      nbt.putInt("MaxProgress", this.maxProgress);
      nbt.putString("Replacement", this.replacement.toString());
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.currentProgress = nbt.getInt("CurrentProgress");
      this.maxProgress = nbt.contains("MaxProgress") ? nbt.getInt("MaxProgress") : -1;
      if (nbt.contains("Replacement")) {
         this.replacement = new TileParser(new StringReader(nbt.getString("Replacement")), ModBlocks.ERROR_BLOCK, true).toTile();
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
