package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;

public class VaultRaidRoom extends VaultRoom {
   public static final ResourceLocation ID = Vault.id("raid_room");
   private boolean raidFinished = false;

   public VaultRaidRoom() {
      super(ID);
   }

   public VaultRaidRoom(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isRaidFinished() {
      return this.raidFinished;
   }

   public void setRaidFinished() {
      this.raidFinished = true;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74757_a("raidFinished", this.raidFinished);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.raidFinished = nbt.func_74767_n("raidFinished");
   }
}
