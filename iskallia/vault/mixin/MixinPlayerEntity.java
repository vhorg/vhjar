package iskallia.vault.mixin;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.GameRules.RuleKey;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerEntity extends LivingEntity {
   protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World worldIn) {
      super(type, worldIn);
   }

   @Redirect(
      method = {"dropInventory"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$RuleKey;)Z"
      )
   )
   public boolean yes(GameRules instance, RuleKey<BooleanValue> key) {
      VaultRaid vault = VaultRaidData.get((ServerWorld)this.field_70170_p).getActiveFor(this.func_110124_au());
      return vault != null || instance.func_223586_b(key);
   }
}
