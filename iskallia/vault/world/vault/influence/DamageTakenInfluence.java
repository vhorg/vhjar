package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class DamageTakenInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("dmg_taken");
   private float damageTakenMultiplier;

   DamageTakenInfluence() {
      super(ID);
   }

   public DamageTakenInfluence(float damageTakenMultiplier) {
      this();
      this.damageTakenMultiplier = damageTakenMultiplier;
   }

   public float getDamageTakenMultiplier() {
      return this.damageTakenMultiplier;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putFloat("dmg", this.damageTakenMultiplier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.damageTakenMultiplier = tag.getFloat("dmg");
   }
}
