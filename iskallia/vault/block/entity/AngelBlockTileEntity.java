package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AngelBlockTileEntity extends BlockEntity {
   private static final String OWNER_TAG = "owner";
   private UUID owner;
   public int tickCount;
   private float activeRotation;

   public AngelBlockTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.ANGEL_BLOCK_TILE_ENTITY, pPos, pState);
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
      ModBlocks.ANGEL_BLOCK.addPlayerAngelBlock(owner, this.level.dimension(), this.worldPosition);
   }

   public UUID getOwner() {
      return this.owner;
   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AngelBlockTileEntity pBlockEntity) {
      pBlockEntity.tickCount++;
      pBlockEntity.activeRotation++;
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.owner != null) {
         tag.putUUID("owner", this.owner);
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      if (tag.contains("owner")) {
         this.owner = tag.getUUID("owner");
      }
   }

   public void onLoad() {
      super.onLoad();
      if (this.owner != null) {
         ModBlocks.ANGEL_BLOCK.addPlayerAngelBlock(this.owner, this.level.dimension(), this.worldPosition);
      }
   }

   public float getActiveRotation(float p_59198_) {
      return (this.activeRotation + p_59198_) * -0.0375F;
   }
}
