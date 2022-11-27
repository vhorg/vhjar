package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public class ModifierDoublingModifier extends RaidModifier {
   public ModifierDoublingModifier(String name) {
      super(false, true, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public Component getDisplay(float value) {
      return new TextComponent("Doubles values of all existing modifiers").withStyle(ChatFormatting.GREEN);
   }
}
