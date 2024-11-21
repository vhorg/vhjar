package iskallia.vault.config;

import iskallia.vault.config.entry.FloatRangeEntry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class EternalAttributeConfig extends Config {
   @Override
   public String getName() {
      return "eternal_attributes";
   }

   public Map<Attribute, Float> createAttributes() {
      return new HashMap<>();
   }

   public FloatRangeEntry getHealthRollRange() {
      return FloatRangeEntry.EMPTY;
   }

   public FloatRangeEntry getDamageRollRange() {
      return FloatRangeEntry.EMPTY;
   }

   public FloatRangeEntry getMoveSpeedRollRange() {
      return FloatRangeEntry.EMPTY;
   }

   @Override
   protected void reset() {
   }
}
