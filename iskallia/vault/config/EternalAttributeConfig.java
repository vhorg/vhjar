package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.FloatRangeEntry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

public class EternalAttributeConfig extends Config {
   @Expose
   private final Map<String, FloatRangeEntry> initialAttributes = new HashMap<>();
   @Expose
   private FloatRangeEntry healthPerLevel;
   @Expose
   private FloatRangeEntry damagePerLevel;
   @Expose
   private FloatRangeEntry moveSpeedPerLevel;

   @Override
   public String getName() {
      return "eternal_attributes";
   }

   public Map<Attribute, Float> createAttributes() {
      Map<Attribute, Float> selectedAttributes = new HashMap<>();
      this.initialAttributes.forEach((attrKey, valueRange) -> {
         Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attrKey));
         if (attribute != null) {
            selectedAttributes.put(attribute, valueRange.getRandom());
         }
      });
      return selectedAttributes;
   }

   public FloatRangeEntry getHealthRollRange() {
      return this.healthPerLevel;
   }

   public FloatRangeEntry getDamageRollRange() {
      return this.damagePerLevel;
   }

   public FloatRangeEntry getMoveSpeedRollRange() {
      return this.moveSpeedPerLevel;
   }

   @Override
   protected void reset() {
      this.initialAttributes.clear();
      this.initialAttributes.put(Attributes.MAX_HEALTH.getRegistryName().toString(), new FloatRangeEntry(20.0F, 30.0F));
      this.initialAttributes.put(Attributes.ATTACK_DAMAGE.getRegistryName().toString(), new FloatRangeEntry(4.0F, 7.0F));
      this.initialAttributes.put(Attributes.MOVEMENT_SPEED.getRegistryName().toString(), new FloatRangeEntry(0.2F, 0.23F));
      this.healthPerLevel = new FloatRangeEntry(4.0F, 8.0F);
      this.damagePerLevel = new FloatRangeEntry(2.0F, 3.0F);
      this.moveSpeedPerLevel = new FloatRangeEntry(0.02F, 0.03F);
   }
}
