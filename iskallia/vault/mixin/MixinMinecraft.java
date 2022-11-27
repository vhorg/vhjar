package iskallia.vault.mixin;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.core.SkyVaultsPreset;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft.ExperimentalDialogType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.RegistryAccess.Writable;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldStem.DataPackConfigSupplier;
import net.minecraft.server.WorldStem.WorldDataSupplier;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft extends ReentrantBlockableEventLoop<Runnable> {
   @Shadow
   @Final
   private static Logger LOGGER;
   private boolean bypassLoading = false;

   public MixinMinecraft(String p_18765_) {
      super(p_18765_);
   }

   @Shadow
   protected abstract void doLoadLevel(
      String var1,
      Function<LevelStorageAccess, DataPackConfigSupplier> var2,
      Function<LevelStorageAccess, WorldDataSupplier> var3,
      boolean var4,
      ExperimentalDialogType var5,
      boolean var6
   );

   @Inject(
      method = {"createLevel"},
      at = {@At("HEAD")}
   )
   private void createLevelHead(String worldName, LevelSettings settings, RegistryAccess registries, WorldGenSettings worldGenSettings, CallbackInfo ci) {
      LevelStem overworld = (LevelStem)worldGenSettings.dimensions().get(new ResourceLocation("overworld"));
      if (overworld != null && overworld.generator() instanceof SkyVaultsChunkGenerator) {
         this.bypassLoading = true;
      }
   }

   @Inject(
      method = {"createLevel"},
      at = {@At("RETURN")}
   )
   private void createLevelReturn(String worldName, LevelSettings settings, RegistryAccess registries, WorldGenSettings worldGenSettings, CallbackInfo ci) {
      this.bypassLoading = false;
   }

   @Inject(
      method = {"createLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void createLevel(String worldName, LevelSettings worldSettings, RegistryAccess registries, WorldGenSettings worldGenSettings, CallbackInfo ci) {
      if (this.bypassLoading) {
         this.doLoadLevel(
            worldName,
            p_210684_ -> worldSettings::getDataPackConfig,
            p_210718_ -> (p_210712_, p_210713_) -> {
               Writable newRegistry = RegistryAccess.builtinCopy();
               DynamicOps<JsonElement> ops1 = RegistryOps.create(JsonOps.INSTANCE, registries);
               DynamicOps<JsonElement> ops2 = RegistryOps.createAndLoad(JsonOps.INSTANCE, newRegistry, p_210712_);
               DataResult<WorldGenSettings> result = WorldGenSettings.CODEC
                  .encodeStart(ops1, worldGenSettings)
                  .setLifecycle(Lifecycle.stable())
                  .flatMap(accumulated -> WorldGenSettings.CODEC.parse(ops2, accumulated));
               WorldGenSettings newWorldGenSettings = (WorldGenSettings)result.getOrThrow(
                  false, Util.prefix("Error reading worldgen settings after loading data packs: ", LOGGER::error)
               );
               SkyVaultsPreset.build(
                  (WritableRegistry<LevelStem>)newWorldGenSettings.dimensions(),
                  newRegistry,
                  newWorldGenSettings.seed(),
                  newWorldGenSettings.generateFeatures()
               );
               return Pair.of(new PrimaryLevelData(worldSettings, newWorldGenSettings, Lifecycle.stable()), newRegistry.freeze());
            },
            false,
            ExperimentalDialogType.CREATE,
            true
         );
         ci.cancel();
      }
   }
}
