package iskallia.vault.mixin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ContainerOpenersCounter.class})
public abstract class MixinContainerOpenersCounter {
   @Shadow
   protected abstract boolean isOwnContainer(Player var1);

   @Overwrite
   private int getOpenCount(Level world, BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      AABB aabb = new AABB(i - 20.0F, j - 20.0F, k - 20.0F, i + 1 + 20.0F, j + 1 + 20.0F, k + 1 + 20.0F);
      List<Player> players = world.getEntities(EntityTypeTest.forClass(Player.class), aabb, this::isOwnContainer);
      return (int)players.stream().filter(player -> {
         double reach = player.getReachDistance();
         if (reach != 0.0) {
            reach += 0.5;
         }

         return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= reach * reach;
      }).count();
   }
}
