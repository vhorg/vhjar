package iskallia.vault.entity.boss.trait;

import iskallia.vault.entity.boss.VaultBossEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class LifeLeechOnHitEffect implements IOnHitEffect, ITrait {
   public static final String TYPE = "life_leech_on_hit";
   private float leechPercentage = 0.1F;

   public LifeLeechOnHitEffect setAttributes(float leechPercentage) {
      this.leechPercentage = leechPercentage;
      return this;
   }

   @Override
   public void onHit(VaultBossEntity boss, Player playerHit, float damage) {
      boss.heal(damage * this.leechPercentage);
   }

   @Override
   public String getType() {
      return "life_leech_on_hit";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addOnHitEffect(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof LifeLeechOnHitEffect addedLifeLeech) {
         this.leechPercentage = this.leechPercentage + addedLifeLeech.leechPercentage;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putFloat("LeechPercentage", this.leechPercentage);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.leechPercentage = nbt.getFloat("LeechPercentage");
   }
}
