package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.vault.VaultRaid;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class VaultRoom extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("room");
   private boolean cakeEaten = false;
   private BlockPos cakePos = null;
   private VListNBT<UUID, StringTag> sandIds = VListNBT.ofUUID();

   protected VaultRoom(ResourceLocation id) {
      super(id);
   }

   public VaultRoom() {
      this(ID);
   }

   protected VaultRoom(ResourceLocation id, ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(id, template, boundingBox, rotation);
   }

   public VaultRoom(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      this(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
   }

   public void setCakeEaten(boolean cakeEaten) {
      this.cakeEaten = cakeEaten;
   }

   public boolean isCakeEaten() {
      return this.cakeEaten;
   }

   public void setCakePos(BlockPos cakePos) {
      this.cakePos = cakePos;
   }

   public List<UUID> getSandId() {
      return this.sandIds;
   }

   public void addSandId(UUID sandId) {
      this.sandIds.add(sandId);
   }

   @Nullable
   public BlockPos getCakePos() {
      return this.cakePos;
   }

   public Vec3i getCenter() {
      return this.getBoundingBox().getCenter();
   }

   public BlockPos getTunnelConnectorPos(Direction dir) {
      Vec3i center = this.getCenter();
      BlockPos size = new BlockPos(this.getBoundingBox().getLength()).offset(2, 2, 2);
      return new BlockPos(center).offset(dir.getStepX() * size.getX(), 0, dir.getStepZ() * size.getZ());
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putBoolean("cakeEaten", this.cakeEaten);
      if (this.cakePos != null) {
         tag.put("cakePos", NBTHelper.serializeBlockPos(this.cakePos));
      }

      tag.put("sandIds", this.sandIds.serializeNBT());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.cakeEaten = tag.getBoolean("cakeEaten");
      if (tag.contains("cakePos", 10)) {
         this.cakePos = NBTHelper.deserializeBlockPos(tag.getCompound("cakePos"));
      }

      this.sandIds.deserializeNBT(tag.getList("sandIds", 8));
   }
}
