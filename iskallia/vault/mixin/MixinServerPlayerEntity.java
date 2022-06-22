package iskallia.vault.mixin;

import com.mojang.authlib.GameProfile;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.GameRules.RuleKey;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerPlayerEntity.class})
public abstract class MixinServerPlayerEntity extends PlayerEntity {
   @Shadow
   public abstract ServerWorld func_71121_q();

   public MixinServerPlayerEntity(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
      super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
   }

   @Redirect(
      method = {"copyFrom"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$RuleKey;)Z"
      )
   )
   public boolean yes(GameRules instance, RuleKey<BooleanValue> key) {
      VaultRaid vault = VaultRaidData.get(this.func_71121_q()).getActiveFor(this.func_110124_au());
      return vault != null || instance.func_223586_b(key);
   }
}
