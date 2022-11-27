package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public class MonsterAmountModifier extends RaidModifier {
   public MonsterAmountModifier(String name) {
      super(true, false, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public Component getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new TextComponent("+" + percDisplay + "% increased Amount of Monsters").withStyle(ChatFormatting.RED);
   }
}
