package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancement;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.util.data.WeightedList;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class PaxelEnhancementConfig extends Config {
   @Expose
   private WeightedList<String> ENHANCEMENT_WEIGHTS;

   @Override
   public String getName() {
      return "paxel_enhancement";
   }

   @Nullable
   public PaxelEnhancement getRandomEnhancement(Random random) {
      String enhancementSid = this.ENHANCEMENT_WEIGHTS.getRandom(random);
      return enhancementSid == null ? null : PaxelEnhancements.REGISTRY.get(new ResourceLocation(enhancementSid));
   }

   @Override
   protected void reset() {
      this.ENHANCEMENT_WEIGHTS = new WeightedList<>();
      PaxelEnhancements.REGISTRY.keySet().forEach(enhancementId -> {
         String enhancementSid = enhancementId.toString();
         this.ENHANCEMENT_WEIGHTS.add(enhancementSid, 1);
      });
   }
}
