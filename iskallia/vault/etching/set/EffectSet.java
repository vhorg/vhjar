package iskallia.vault.etching.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public interface EffectSet {
   List<EffectSet.GrantedEffect> getGrantedEffects();

   public static class EffectConfig {
      @Expose
      private ResourceLocation effect;
      @Expose
      private int addedAmplifier;

      public EffectConfig(ResourceLocation effect, int addedAmplifier) {
         this.effect = effect;
         this.addedAmplifier = addedAmplifier;
      }

      @Nullable
      public MobEffect getEffect() {
         return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
      }

      public int getAddedAmplifier() {
         return this.addedAmplifier;
      }

      @Nullable
      public EffectSet.GrantedEffect createGrantedEffect() {
         MobEffect mobEffect = this.getEffect();
         return mobEffect != null ? new EffectSet.GrantedEffect(mobEffect, this.getAddedAmplifier()) : null;
      }
   }

   public record GrantedEffect(MobEffect effect, int addedAmplifier) {
      public EffectGearAttribute asGearAttribute() {
         return new EffectGearAttribute(this.effect(), this.addedAmplifier());
      }
   }
}
