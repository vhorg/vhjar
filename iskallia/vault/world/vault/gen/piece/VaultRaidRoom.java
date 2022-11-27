package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class VaultRaidRoom extends VaultRoom {
   public static final ResourceLocation ID = VaultMod.id("raid_room");
   private boolean raidFinished = false;

   public VaultRaidRoom() {
      super(ID);
   }

   public VaultRaidRoom(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isRaidFinished() {
      return this.raidFinished;
   }

   public void setRaidFinished() {
      this.raidFinished = true;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putBoolean("raidFinished", this.raidFinished);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.raidFinished = nbt.getBoolean("raidFinished");
   }
}
