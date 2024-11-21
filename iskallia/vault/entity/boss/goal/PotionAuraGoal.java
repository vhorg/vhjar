package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionAuraGoal extends Goal implements ITrait {
   public static final int MAX_DISTANCE = 64;
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(64.0);
   public static String TYPE = "potion_aura";
   private static final int CHECK_COOLDOWN = 5;
   @Nullable
   private MobEffect mobEffect;
   private int duration;
   private int amplifier;
   private int range;
   private boolean invertEffectRange;
   private final VaultBossBaseEntity boss;
   private long cooldownTime = -1L;

   public PotionAuraGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
   }

   public PotionAuraGoal setAttributes(MobEffect mobEffect, int duration, int amplifier, int range, boolean invertEffectRange) {
      this.mobEffect = mobEffect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.range = range;
      this.invertEffectRange = invertEffectRange;
      return this;
   }

   @Override
   public String getType() {
      return TYPE;
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof PotionAuraGoal potionAuraGoal) {
         this.amplifier = this.amplifier + potionAuraGoal.amplifier + 1;
      }
   }

   public boolean canUse() {
      return true;
   }

   public void tick() {
      if (this.mobEffect != null && !this.boss.getLevel().isClientSide() && this.cooldownTime <= this.boss.getLevel().getGameTime()) {
         this.cooldownTime = this.boss.getLevel().getGameTime() + 5L;
         if (this.effectOutsideOfRange()) {
            this.boss.getLevel().getNearbyPlayers(TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(64.0)).forEach(player -> {
               if (!(player.distanceTo(this.boss) < this.range)) {
                  this.applyEffectTo(player);
               }
            });
         } else {
            this.boss.getLevel().getNearbyPlayers(TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(this.range)).forEach(this::applyEffectTo);
         }
      }
   }

   private void applyEffectTo(Player player) {
      if (player.hasEffect(this.mobEffect)) {
         if (player.getEffect(this.mobEffect).getAmplifier() >= this.amplifier) {
            return;
         }

         player.removeEffect(this.mobEffect);
      }

      player.addEffect(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier));
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("MobEffect", this.mobEffect.getRegistryName().toString());
      nbt.putInt("Duration", this.duration);
      nbt.putInt("Amplifier", this.amplifier);
      nbt.putInt("Range", this.range);
      nbt.putBoolean("InvertEffectRange", this.invertEffectRange);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.mobEffect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("MobEffect")));
      this.duration = nbt.getInt("Duration");
      this.amplifier = nbt.getInt("Amplifier");
      this.range = nbt.getInt("Range");
      this.invertEffectRange = nbt.getBoolean("InvertEffectRange");
   }

   public int getRange() {
      return this.range;
   }

   public MobEffect getMobEffect() {
      return this.mobEffect;
   }

   public boolean effectOutsideOfRange() {
      return this.invertEffectRange;
   }
}
