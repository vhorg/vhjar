package iskallia.vault.util;

import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameterSet;

public class LootUtils {
   public static boolean doesContextFulfillSet(LootContext ctx, LootParameterSet set) {
      for (LootParameter<?> required : set.func_216277_a()) {
         if (!ctx.func_216033_a(required)) {
            return false;
         }
      }

      return true;
   }
}
