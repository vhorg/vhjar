package iskallia.vault.mixin;

import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.VirtualWorlds;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.event.TickEvent.Phase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftServer.class})
public abstract class MixinMinecraftServer {
   @Shadow
   protected abstract boolean initServer() throws IOException;

   @Redirect(
      method = {"tickChildren"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V"
      )
   )
   public void tickChildren(ServerLevel instance, BooleanSupplier hasTimeLeft) {
      if (!(instance instanceof VirtualWorld)) {
         instance.tick(hasTimeLeft);
      }
   }

   @Inject(
      method = {"tickChildren"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
         ordinal = 0,
         shift = Shift.AFTER
      )}
   )
   public void tickChildrenStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      VirtualWorlds.tick(hasTimeLeft, Phase.START);
   }

   @Inject(
      method = {"tickChildren"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
         ordinal = 1,
         shift = Shift.BEFORE
      )}
   )
   public void tickChildrenEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      VirtualWorlds.tick(hasTimeLeft, Phase.END);
   }

   @Inject(
      method = {"createLevels"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/border/WorldBorder;applySettings(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V",
         shift = Shift.BEFORE
      )}
   )
   public void loadVWorlds(ChunkProgressListener holder, CallbackInfo ci) {
      VirtualWorlds.load();
   }

   @Inject(
      method = {"setInitialSpawn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void setInitialSpawn(ServerLevel world, ServerLevelData data, boolean p_177899_, boolean p_177900_, CallbackInfo ci) {
      if (world.getChunkSource().getGenerator() instanceof SkyVaultsChunkGenerator) {
         ci.cancel();
      }
   }
}