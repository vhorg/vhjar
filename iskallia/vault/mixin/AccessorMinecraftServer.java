package iskallia.vault.mixin;

import java.util.Map;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftServer.class})
public interface AccessorMinecraftServer {
   @Accessor("executor")
   Executor getWorkerExecutor();

   @Accessor("storageSource")
   LevelStorageAccess getSession();

   @Accessor("levels")
   Map<ResourceKey<Level>, ServerLevel> getWorldsMap();

   @Accessor(
      value = "perWorldTickTimes",
      remap = false
   )
   Map<ResourceKey<Level>, long[]> getPerWorldTickTimes();
}
