package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ResistanceInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("resistance");
   private float additionalResistance;

   public ResistanceInfluence() {
      super(ID);
   }

   public ResistanceInfluence(float additionalResistance) {
      this();
      this.additionalResistance = additionalResistance;
   }

   public float getAdditionalResistance() {
      return this.additionalResistance;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("additionalResistance", this.additionalResistance);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.additionalResistance = tag.func_74760_g("additionalResistance");
   }
}
