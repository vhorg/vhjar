package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class LeechInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("leech");
   private float leechPercent;

   LeechInfluence() {
      super(ID);
   }

   public LeechInfluence(float leechPercent) {
      this();
      this.leechPercent = leechPercent;
   }

   public float getLeechPercent() {
      return this.leechPercent;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("leechPercent", this.leechPercent);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.leechPercent = tag.func_74760_g("leechPercent");
   }
}
