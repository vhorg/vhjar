package iskallia.vault.event.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class VaultLevelUpEvent extends Event {
   private final ServerPlayer player;
   private final int exp;
   private final int initialLevel;
   private final int newLevel;

   public VaultLevelUpEvent(ServerPlayer player, int exp, int initialLevel, int newLevel) {
      this.player = player;
      this.exp = exp;
      this.initialLevel = initialLevel;
      this.newLevel = newLevel;
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }

   public int getExp() {
      return this.exp;
   }

   public int getInitialLevel() {
      return this.initialLevel;
   }

   public int getNewLevel() {
      return this.newLevel;
   }
}
