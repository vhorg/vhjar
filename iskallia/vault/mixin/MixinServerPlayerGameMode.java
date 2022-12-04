package iskallia.vault.mixin;

import iskallia.vault.block.VaultCrateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerPlayerGameMode.class})
public class MixinServerPlayerGameMode {
   @Redirect(
      method = {"useItemOn"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;doesSneakBypassUse(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"
      )
   )
   public boolean doesSneakBypassUse(ItemStack instance, LevelReader levelReader, BlockPos pos, Player player) {
      BlockState state = levelReader.getBlockState(pos);
      return state.getBlock() instanceof VaultCrateBlock;
   }
}
