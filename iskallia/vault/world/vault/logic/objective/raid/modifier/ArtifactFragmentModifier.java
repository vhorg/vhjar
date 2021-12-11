package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.entity.FloatingItemEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class ArtifactFragmentModifier extends RaidModifier {
   public ArtifactFragmentModifier(String name) {
      super(true, true, name);
   }

   @Override
   public void affectRaidMob(MobEntity mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
      if (!(rand.nextFloat() >= value)) {
         BlockPos at = controller.func_177967_a(Direction.UP, 3);
         FloatingItemEntity itemEntity = FloatingItemEntity.create(world, at, new ItemStack(ModItems.ARTIFACT_FRAGMENT));
         world.func_217376_c(itemEntity);
      }
   }

   @Override
   public ITextComponent getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new StringTextComponent("+" + percDisplay + "% Artifact Fragment chance").func_240699_a_(TextFormatting.GOLD);
   }
}
