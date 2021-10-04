package iskallia.vault.item.catalyst;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.VaultCrystalCatalystConfig;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import javax.annotation.Nullable;

public class SingleModifierOutcome {
   @Expose
   private final ModifierRollType type;
   @Expose
   private final String pool;

   public SingleModifierOutcome(ModifierRollType type, String pool) {
      this.type = type;
      this.pool = pool;
   }

   @Nullable
   public ModifierRollResult resolve(Random rand) {
      VaultCrystalCatalystConfig.TaggedPool pool = ModConfigs.VAULT_CRYSTAL_CATALYST.getPool(this.pool);
      if (pool != null) {
         return this.type == ModifierRollType.ADD_SPECIFIC_MODIFIER
            ? ModifierRollResult.ofModifier(pool.getModifier(rand))
            : ModifierRollResult.ofPool(this.pool);
      } else {
         return null;
      }
   }

   public ModifierRollType getType() {
      return this.type;
   }

   public String getPool() {
      return this.pool;
   }
}
