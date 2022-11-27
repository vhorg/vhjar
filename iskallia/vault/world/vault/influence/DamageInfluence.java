package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class DamageInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("dmg_dealt");
   private float damageDealtMultiplier;

   DamageInfluence() {
      super(ID);
   }

   public DamageInfluence(float damageDealtMultiplier) {
      this();
      this.damageDealtMultiplier = damageDealtMultiplier;
   }

   public float getDamageDealtMultiplier() {
      return this.damageDealtMultiplier;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putFloat("dmg", this.damageDealtMultiplier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.damageDealtMultiplier = tag.getFloat("dmg");
   }
}
