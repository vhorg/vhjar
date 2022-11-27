package iskallia.vault.util;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class VHSmpUtil {
   public static boolean isArenaWorld(@Nullable Entity entity) {
      return entity == null ? false : isArenaWorld(entity.getLevel().dimension().location());
   }

   public static boolean isArenaWorld(Level level) {
      return isArenaWorld(level.dimension().location());
   }

   public static boolean isArenaWorld(ResourceKey<Level> dimKey) {
      return isArenaWorld(dimKey.location());
   }

   public static boolean isArenaWorld(ResourceLocation key) {
      String dimStr = key.getPath();
      if (!dimStr.startsWith("arena_")) {
         return false;
      } else {
         String vaultIdPart = dimStr.substring(6);

         try {
            UUID.fromString(vaultIdPart);
            return true;
         } catch (IllegalArgumentException var4) {
            return false;
         }
      }
   }
}
