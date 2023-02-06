package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.mana.Mana;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class LowManaTalent extends PlayerTalent {
   @Expose
   private float manaThreshold;

   public LowManaTalent(int cost, float manaThreshold) {
      super(cost);
      this.manaThreshold = manaThreshold;
   }

   public LowManaTalent(int cost, int levelRequirement, float manaThreshold) {
      super(cost, levelRequirement);
      this.manaThreshold = manaThreshold;
   }

   public float getManaThreshold() {
      return this.manaThreshold;
   }

   public boolean shouldGetBenefits(LivingEntity entity) {
      if (entity instanceof ServerPlayer player) {
         float mana = Mana.get(player);
         float max = Mana.getMax(player);
         return mana / max < this.getManaThreshold();
      } else {
         return false;
      }
   }
}
