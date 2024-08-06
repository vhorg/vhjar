package iskallia.vault.mixin;

import iskallia.vault.entity.boss.VaultBossEntity;
import java.util.List;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AreaEffectCloud.class})
public class MixinAreaEffectCloud {
   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      )
   )
   public List<? extends Entity> getEntitiesOfClassOrJustPlayersWhenThrownByVaultBoss(Level level, Class<? extends Entity> entityClass, AABB aabb) {
      return this instanceof AreaEffectCloud areaEffectCloud && areaEffectCloud.getOwner() instanceof VaultBossEntity
         ? level.getEntitiesOfClass(Player.class, aabb)
         : level.getEntitiesOfClass(entityClass, aabb);
   }
}
