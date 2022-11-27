package iskallia.vault.mixin;

import com.mojang.authlib.GameProfile;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerPlayer.class})
public abstract class MixinServerPlayerEntity extends Player {
   @Shadow
   public abstract ServerLevel getLevel();

   public MixinServerPlayerEntity(Level p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
      super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
   }

   @Redirect(
      method = {"restoreFrom"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
      )
   )
   public boolean yes(GameRules instance, Key<BooleanValue> key) {
      return ServerVaults.isVaultWorld(this.getLevel()) || instance.getBoolean(key);
   }
}
