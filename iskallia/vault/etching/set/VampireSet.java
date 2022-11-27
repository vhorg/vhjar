package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class VampireSet extends EtchingSet<VampireSet.Config> implements GearAttributeSet {
   public VampireSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<VampireSet.Config> getConfigClass() {
      return VampireSet.Config.class;
   }

   public VampireSet.Config getDefaultConfig() {
      return new VampireSet.Config(0.05F);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{new VaultGearAttributeInstance<>(ModGearAttributes.LEECH, this.getConfig().getIncreasedLeech())}
      );
   }

   public static class Config {
      @Expose
      private float increasedLeech;

      public Config(float increasedLeech) {
         this.increasedLeech = increasedLeech;
      }

      public float getIncreasedLeech() {
         return this.increasedLeech;
      }
   }
}
