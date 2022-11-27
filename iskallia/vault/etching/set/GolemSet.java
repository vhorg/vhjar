package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class GolemSet extends EtchingSet<GolemSet.Config> implements GearAttributeSet {
   public GolemSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<GolemSet.Config> getConfigClass() {
      return GolemSet.Config.class;
   }

   public GolemSet.Config getDefaultConfig() {
      return new GolemSet.Config(0.2F);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{new VaultGearAttributeInstance<>(ModGearAttributes.RESISTANCE_CAP, this.getConfig().getAdditionalResistanceCap())}
      );
   }

   public static class Config {
      @Expose
      private float additionalResistanceCap;

      public Config(float additionalResistanceCap) {
         this.additionalResistanceCap = additionalResistanceCap;
      }

      public float getAdditionalResistanceCap() {
         return this.additionalResistanceCap;
      }
   }
}
