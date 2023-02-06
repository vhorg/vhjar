package iskallia.vault.core.vault.abyss;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AbyssHelper {
   @OnlyIn(Dist.CLIENT)
   public static boolean hasAbyssEffectClient() {
      Player thisPlayer = Minecraft.getInstance().player;
      return thisPlayer == null ? false : MiscUtils.getVault(thisPlayer).map(AbyssHelper::hasAbyssEffect).orElse(false);
   }

   public static boolean hasAbyssEffect(Vault vault) {
      return getAbyssEffect(vault) > 0.0F;
   }

   public static float getAbyssEffect(Entity entity) {
      return MiscUtils.getVault(entity).map(AbyssHelper::getAbyssEffect).orElse(0.0F);
   }

   public static float getAbyssEffect(Vault vault) {
      return 0.0F;
   }

   public static float getAbyssDistanceModifier(Entity entity) {
      return MiscUtils.getVault(entity).map(vault -> getAbyssDistanceModifier(entity, vault)).orElse(0.0F);
   }

   public static float getAbyssDistanceModifier(Entity entity, Vault vault) {
      return getAbyssDistanceModifier(entity.blockPosition(), vault);
   }

   public static float getAbyssDistanceModifier(BlockPos pos, Vault vault) {
      return 0.0F;
   }
}
