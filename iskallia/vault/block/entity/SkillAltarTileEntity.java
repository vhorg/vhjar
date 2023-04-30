package iskallia.vault.block.entity;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkillAltarTileEntity extends BlockEntity {
   private static final String OWNER_ID_TAG = "ownerId";
   @Nullable
   private UUID ownerId = null;
   private final OverSizedInventory regretOrbInventory = new OverSizedInventory(1, this::setChanged, player -> true);
   private int renderIconKeyIndex = -1;
   private long lastIconSwitchTime = 0L;
   private long nextIconSwitchTime = 0L;

   public SkillAltarTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.SKILL_ALTAR_TILE_ENTITY, pWorldPosition, pBlockState);
   }

   public void setOwner(UUID ownerId) {
      this.ownerId = ownerId;
   }

   public CompoundTag getUpdateTag() {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      if (this.ownerId != null) {
         tag.putUUID("ownerId", this.ownerId);
      }

      tag.put("regretOrbs", this.regretOrbInventory.getItem(0).save(new CompoundTag()));
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.ownerId = tag.contains("ownerId") ? tag.getUUID("ownerId") : null;
      this.regretOrbInventory.setItem(0, ItemStack.of(tag.getCompound("regretOrbs")));
   }

   public OverSizedInventory getRegretOrbInventory() {
      return this.regretOrbInventory;
   }

   public UUID getOwnerId() {
      return this.ownerId;
   }

   public void consumeOrbs(int count) {
      ItemStack regretOrbs = this.regretOrbInventory.getItem(0);
      regretOrbs.shrink(count);
      this.regretOrbInventory.setItem(0, regretOrbs);
      this.setChanged();
   }

   public int getRenderIconKeyIndex() {
      return this.renderIconKeyIndex;
   }

   public long getLastIconSwitchTime() {
      return this.lastIconSwitchTime;
   }

   public long getNextIconSwitchTime() {
      return this.nextIconSwitchTime;
   }

   public void switchToNextIcon(int nextIconKeyIndex, long lastIconSwitchTime, long nextIconSwitchTime) {
      this.renderIconKeyIndex = nextIconKeyIndex;
      this.lastIconSwitchTime = lastIconSwitchTime;
      this.nextIconSwitchTime = nextIconSwitchTime;
   }
}
