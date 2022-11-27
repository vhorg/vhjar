package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionEffectTrinket extends TrinketEffect<PotionEffectTrinket.Config> implements GearAttributeTrinket {
   private final MobEffect effect;
   private final int addedAmplifier;

   public PotionEffectTrinket(ResourceLocation name, MobEffect effect, int addedAmplifier) {
      super(name);
      this.effect = effect;
      this.addedAmplifier = addedAmplifier;
   }

   @Override
   public Class<PotionEffectTrinket.Config> getConfigClass() {
      return PotionEffectTrinket.Config.class;
   }

   public PotionEffectTrinket.Config getDefaultConfig() {
      return new PotionEffectTrinket.Config(this.effect.getRegistryName(), this.addedAmplifier);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      PotionEffectTrinket.Config cfg = this.getConfig();
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{
            new VaultGearAttributeInstance<>(ModGearAttributes.EFFECT, new EffectGearAttribute(cfg.getEffect(), cfg.getAddedAmplifier()))
         }
      );
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private ResourceLocation effect;
      @Expose
      private int addedAmplifier;

      public Config(ResourceLocation effect, int addedAmplifier) {
         this.effect = effect;
         this.addedAmplifier = addedAmplifier;
      }

      public MobEffect getEffect() {
         return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
      }

      public int getAddedAmplifier() {
         return this.addedAmplifier;
      }
   }
}
