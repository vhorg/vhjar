package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class AssassinSet extends EtchingSet<AssassinSet.Config> implements GearAttributeSet {
   public AssassinSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<AssassinSet.Config> getConfigClass() {
      return AssassinSet.Config.class;
   }

   public AssassinSet.Config getDefaultConfig() {
      return new AssassinSet.Config(0.4F);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{
            new VaultGearAttributeInstance<>(ModGearAttributes.FATAL_STRIKE_CHANCE, this.getConfig().getIncreasedFatalStrikeChance())
         }
      );
   }

   public static class Config {
      @Expose
      private float increasedFatalStrikeChance;

      public Config(float increasedFatalStrikeChance) {
         this.increasedFatalStrikeChance = increasedFatalStrikeChance;
      }

      public float getIncreasedFatalStrikeChance() {
         return this.increasedFatalStrikeChance;
      }
   }
}
