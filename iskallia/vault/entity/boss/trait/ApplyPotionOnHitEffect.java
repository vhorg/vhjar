package iskallia.vault.entity.boss.trait;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class ApplyPotionOnHitEffect implements IOnHitEffect, ITrait {
   public static final String TYPE = "apply_potion_on_hit";
   @Nullable
   private MobEffect mobEffect = null;
   private int duration;
   private int amplifier;
   private float chance;

   public ApplyPotionOnHitEffect setAttributes(MobEffect mobEffect, int duration, int amplifier, float chance) {
      this.mobEffect = mobEffect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.chance = chance;
      return this;
   }

   @Override
   public void onHit(VaultBossEntity boss, Player playerHit, float damage) {
      if (this.mobEffect != null && !(playerHit.level.random.nextFloat() > this.chance)) {
         playerHit.addEffect(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier));
      }
   }

   @Override
   public String getType() {
      return "apply_potion_on_hit";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addOnHitEffect(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof ApplyPotionOnHitEffect applyPotionOnHitEffect) {
         this.amplifier = this.amplifier + applyPotionOnHitEffect.amplifier + 1;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("MobEffect", this.mobEffect.getRegistryName().toString());
      nbt.putInt("Duration", this.duration);
      nbt.putInt("Amplifier", this.amplifier);
      nbt.putFloat("Chance", this.chance);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.mobEffect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("MobEffect")));
      this.duration = nbt.getInt("Duration");
      this.amplifier = nbt.getInt("Amplifier");
      this.chance = nbt.getFloat("Chance");
   }
}
