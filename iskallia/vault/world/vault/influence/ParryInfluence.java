package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ParryInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("parry");
   private float additionalParry;

   public ParryInfluence() {
      super(ID);
   }

   public ParryInfluence(float additionalParry) {
      this();
      this.additionalParry = additionalParry;
   }

   public float getAdditionalParry() {
      return this.additionalParry;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("additionalParry", this.additionalParry);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.additionalParry = tag.func_74760_g("additionalParry");
   }
}
