package iskallia.vault.util;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class LootUtils {
   public static boolean doesContextFulfillSet(LootContext ctx, LootContextParamSet set) {
      for (LootContextParam<?> required : set.getRequired()) {
         if (!ctx.hasParam(required)) {
            return false;
         }
      }

      return true;
   }
}
