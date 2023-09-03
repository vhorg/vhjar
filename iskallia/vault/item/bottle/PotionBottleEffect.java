package iskallia.vault.item.bottle;

import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionBottleEffect extends BottleEffect {
   public static final String TYPE = "potion";
   private final MobEffect potion;
   private final int duration;
   private final int amplifier;

   public PotionBottleEffect(String effectId, MobEffect potion, int duration, int amplifier) {
      super(effectId);
      this.potion = potion;
      this.duration = duration;
      this.amplifier = amplifier;
   }

   @Override
   public String getType() {
      return "potion";
   }

   @Override
   public void trigger(ServerPlayer player) {
      MobEffectInstance effect = player.getEffect(this.potion);
      int totalAmplifier;
      if (effect instanceof PotionBottleEffect.BottleMobEffectInstance) {
         totalAmplifier = effect.getAmplifier();
      } else {
         totalAmplifier = effect == null ? this.amplifier : effect.getAmplifier() + this.amplifier + 1;
      }

      if (effect != null) {
         player.removeEffectNoUpdate(this.potion);
      }

      player.addEffect(new PotionBottleEffect.BottleMobEffectInstance(this.potion, this.duration, totalAmplifier));
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putString("potion", this.potion.getRegistryName().toString());
      tag.putInt("duration", this.duration);
      tag.putInt("amplifier", this.amplifier);
      return tag;
   }

   @Override
   public String getTooltipText(String tooltipFormat) {
      return String.format(
         tooltipFormat, this.duration / 20, this.potion.getDisplayName().getString(), Language.getInstance().getOrDefault("potion.potency." + this.amplifier)
      );
   }

   public static BottleEffect deserialize(String effectId, CompoundTag tag) {
      MobEffect potion = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("potion")));
      return new PotionBottleEffect(effectId, potion, tag.getInt("duration"), tag.getInt("amplifier"));
   }

   public static class BottleMobEffectInstance extends MobEffectInstance {
      public BottleMobEffectInstance(MobEffect potion, int duration, int totalAmplifier) {
         super(potion, duration, totalAmplifier, false, false, true);
      }

      public boolean tick(LivingEntity pEntity, Runnable p_19554_) {
         return super.tick(pEntity, p_19554_);
      }
   }
}
