package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public class MonsterLevelModifier extends RaidModifier {
   public MonsterLevelModifier(String name) {
      super(false, false, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public Component getDisplay(float value) {
      return new TextComponent("+" + this.getLevelAdded(value) + " to Monster Level").withStyle(ChatFormatting.RED);
   }

   public int getLevelAdded(float value) {
      return Math.round(value);
   }
}
