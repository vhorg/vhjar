package iskallia.vault.mixin;

import iskallia.vault.core.vault.TeamTaskManager;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Scoreboard.class})
public class MixinScoreboard {
   @Inject(
      method = {"addPlayerToTeam"},
      at = {@At("TAIL")}
   )
   private void addPlayerToTeamTasks(String playerName, PlayerTeam playerTeam, CallbackInfoReturnable<Boolean> cir) {
      TeamTaskManager.playerAddedToTeam(playerName, playerTeam.getName());
   }

   @Inject(
      method = {"Lnet/minecraft/world/scores/Scoreboard;removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V"},
      at = {@At("TAIL")}
   )
   private void removePlayerFromTeamTasks(String playerName, PlayerTeam playerTeam, CallbackInfo ci) {
      TeamTaskManager.playerRemovedFromTeam(playerTeam, playerName, playerTeam.getName());
   }
}
