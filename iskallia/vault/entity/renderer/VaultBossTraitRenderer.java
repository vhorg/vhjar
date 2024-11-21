package iskallia.vault.entity.renderer;

import iskallia.vault.client.render.PotionAuraRenderer;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.goal.PotionAuraGoal;

public class VaultBossTraitRenderer {
   private VaultBossTraitRenderer() {
   }

   public static void renderTraits(VaultBossEntity boss) {
      boss.getTraits().forEach(trait -> {
         if (trait instanceof PotionAuraGoal potionAuraGoal) {
            PotionAuraRenderer.INSTANCE.render(boss, potionAuraGoal.getMobEffect(), potionAuraGoal.getRange(), potionAuraGoal.effectOutsideOfRange());
         }
      });
   }
}
