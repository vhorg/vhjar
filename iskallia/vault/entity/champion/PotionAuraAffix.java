package iskallia.vault.entity.champion;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionAuraAffix extends ChampionAffixBase implements IChampionTickableAffix {
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(64.0);
   private static final int CHECK_COOLDOWN = 5;
   public static final String TYPE = "potion_aura";
   @Nullable
   private final MobEffect mobEffect;
   private final int duration;
   private final int amplifier;
   private final int range;
   private final PotionAuraAffix.Target target;
   private long cooldownTime = -1L;

   public PotionAuraAffix(String name, @Nullable MobEffect mobEffect, int duration, int amplifier, int range, PotionAuraAffix.Target target) {
      super("potion_aura", name);
      this.mobEffect = mobEffect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.range = range;
      this.target = target;
   }

   @Nullable
   public MobEffect getMobEffect() {
      return this.mobEffect;
   }

   public int getRange() {
      return this.range;
   }

   @Override
   public void tick(LivingEntity entity) {
      if (this.mobEffect != null) {
         if (this.cooldownTime <= entity.getLevel().getGameTime()) {
            this.cooldownTime = entity.getLevel().getGameTime() + 5L;
         }

         if (this.target == PotionAuraAffix.Target.PLAYER) {
            entity.getLevel()
               .getNearbyPlayers(TARGETING_CONDITIONS, entity, entity.getBoundingBox().inflate(this.range))
               .forEach(player -> player.addEffect(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier)));
         } else if (this.target == PotionAuraAffix.Target.MOB) {
            entity.getLevel()
               .getEntities(
                  EntityTypeTest.forClass(LivingEntity.class), entity.getBoundingBox().inflate(this.range), e -> e != entity && !(e instanceof Player)
               )
               .forEach(mob -> mob.addEffect(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier, true, false)));
         }
      }
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag ret = super.serialize();
      if (this.mobEffect == null) {
         return ret;
      } else {
         ret.putString("mob_effect", this.mobEffect.getRegistryName().toString());
         ret.putInt("duration", this.duration);
         ret.putInt("amplifier", this.amplifier);
         ret.putInt("range", this.range);
         ret.putString("target", this.target.name().toLowerCase(Locale.ROOT));
         return ret;
      }
   }

   public static PotionAuraAffix deserialize(CompoundTag tag) {
      return new PotionAuraAffix(
         deserializeName(tag),
         (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("mob_effect"))),
         tag.getInt("duration"),
         tag.getInt("amplifier"),
         tag.getInt("range"),
         PotionAuraAffix.Target.valueOf(tag.getString("target").toUpperCase(Locale.ROOT))
      );
   }

   public static enum Target {
      PLAYER,
      MOB;
   }
}
