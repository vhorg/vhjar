package iskallia.vault.world.vault.logic.objective.raid.modifier;

import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class ArtifactFragmentModifier extends RaidModifier {
   public ArtifactFragmentModifier(String name) {
      super(true, true, name);
   }

   @Override
   public void affectRaidMob(Mob mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerLevel world, BlockPos controller, ActiveRaid raid, float value) {
      if (!(rand.nextFloat() >= value)) {
         BlockPos at = controller.relative(Direction.UP, 3);
         FloatingItemEntity itemEntity = FloatingItemEntity.create(world, at, new ItemStack(ModItems.ARTIFACT_FRAGMENT));
         world.addFreshEntity(itemEntity);
      }
   }

   @Override
   public Component getDisplay(float value) {
      int percDisplay = Math.round(value * 100.0F);
      return new TextComponent("+" + percDisplay + "% Artifact Fragment chance").withStyle(ChatFormatting.GOLD);
   }
}
