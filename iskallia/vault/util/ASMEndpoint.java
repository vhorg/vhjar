package iskallia.vault.util;

import net.minecraft.network.play.ServerPlayNetHandler;

public class ASMEndpoint {
   public static double getOverriddenSeenEntityReachMaximum(ServerPlayNetHandler handler, double original) {
      return 9.99999999E8;
   }
}
