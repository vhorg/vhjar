package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class DamageTakenModifier extends RaidModifier {
   public DamageTakenModifier(String name) {
      super(true, false, name);
   }

   @Override
   public void affectRaidMob(MobEntity mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public ITextComponent getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new StringTextComponent("+" + percDisplay + "% increased Damage taken").func_240699_a_(TextFormatting.RED);
   }
}
