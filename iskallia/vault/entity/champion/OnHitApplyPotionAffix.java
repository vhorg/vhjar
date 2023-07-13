package iskallia.vault.entity.champion;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class OnHitApplyPotionAffix extends ChampionAffixBase implements IChampionOnHitAffix {
   public static final String TYPE = "on_hit_apply_potion";
   @Nullable
   private final MobEffect mobEffect;
   private final int duration;
   private final int amplifier;
   private final float chance;

   public OnHitApplyPotionAffix(String name, @Nullable MobEffect mobEffect, int duration, int amplifier, float chance) {
      super("on_hit_apply_potion", name);
      this.mobEffect = mobEffect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.chance = chance;
   }

   @Nullable
   public MobEffect getMobEffect() {
      return this.mobEffect;
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
         ret.putFloat("chance", this.chance);
         return ret;
      }
   }

   public static OnHitApplyPotionAffix deserialize(CompoundTag tag) {
      return new OnHitApplyPotionAffix(
         deserializeName(tag),
         (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("mob_effect"))),
         tag.getInt("duration"),
         tag.getInt("amplifier"),
         tag.getFloat("chance")
      );
   }

   @Override
   public void onChampionHitPlayer(LivingEntity champion, Player player, float amount) {
      if (this.mobEffect != null && !(player.getRandom().nextFloat() > this.chance)) {
         player.addEffect(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier));
      }
   }
}
