package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class MonsterLevelModifier extends RaidModifier {
   public MonsterLevelModifier(String name) {
      super(false, false, name);
   }

   @Override
   public void affectRaidMob(MobEntity mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
   }

   @Override
   public ITextComponent getDisplay(float value) {
      return new StringTextComponent("+" + this.getLevelAdded(value) + " to Monster Level").func_240699_a_(TextFormatting.RED);
   }

   public int getLevelAdded(float value) {
      return Math.round(value);
   }
}
