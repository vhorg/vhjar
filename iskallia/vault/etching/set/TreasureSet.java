package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class TreasureSet extends EtchingSet<TreasureSet.Config> implements GearAttributeSet {
   public TreasureSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<TreasureSet.Config> getConfigClass() {
      return TreasureSet.Config.class;
   }

   public TreasureSet.Config getDefaultConfig() {
      return new TreasureSet.Config(2.0F);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{new VaultGearAttributeInstance<>(ModGearAttributes.ITEM_RARITY, this.getConfig().getIncreasedItemRarity())}
      );
   }

   public static class Config {
      @Expose
      private float increasedItemRarity;

      public Config(float increasedItemRarity) {
         this.increasedItemRarity = increasedItemRarity;
      }

      public float getIncreasedItemRarity() {
         return this.increasedItemRarity;
      }
   }
}
