package iskallia.vault.skill.talent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface Talent {
   int getLearningCost();

   int getRegretCost();

   int getLevelRequirement();

   void onAdded(Player var1);

   void tick(ServerPlayer var1);

   void onRemoved(Player var1);
}
