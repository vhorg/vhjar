package iskallia.vault.mixin;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.core.SkyVaultsPreset;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft.ExperimentalDialogType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.RegistryAccess.Writable;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldStem;
import net.minecraft.server.WorldStem.DataPackConfigSupplier;
import net.minecraft.server.WorldStem.WorldDataSupplier;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {Minecraft.class},
   priority = 1001
)
public abstract class MixinMinecraft extends ReentrantBlockableEventLoop<Runnable> {
   @Shadow
   @Final
   private static Logger LOGGER;

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

   @Shadow
   public abstract WorldStem makeWorldStem(PackRepository var1, boolean var2, DataPackConfigSupplier var3, WorldDataSupplier var4) throws InterruptedException, ExecutionException;

   @Shadow
   protected static PackRepository createPackRepository(LevelStorageAccess p_205143_) {
      return null;
   }

   @Inject(
      method = {"createLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void createLevel(String worldName, LevelSettings worldSettings, RegistryAccess registries, WorldGenSettings worldGenSettings, CallbackInfo ci) {
      LevelStem overworld = (LevelStem)worldGenSettings.dimensions().get(new ResourceLocation("overworld"));
      if (overworld != null && overworld.generator() instanceof SkyVaultsChunkGenerator) {
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

   @Overwrite
   public void loadLevel(String pLevelName) {
      this.doLoadLevel(
         pLevelName,
         DataPackConfigSupplier::loadFromWorld,
         storageAccess -> (resourceManager, config) -> {
            Writable newRegistry = RegistryAccess.builtinCopy();
            DynamicOps<Tag> ops = RegistryOps.createAndLoad(NbtOps.INSTANCE, newRegistry, resourceManager);
            WorldData data = storageAccess.getDataTag(ops, config, newRegistry.allElementsLifecycle());
            if (data == null) {
               throw new IllegalStateException("Failed to load world");
            } else {
               WorldGenSettings newWorldGenSettings = data.worldGenSettings();
               if (((LevelStem)newWorldGenSettings.dimensions().get(LevelStem.NETHER)).generator() instanceof SkyVaultsChunkGenerator) {
                  SkyVaultsPreset.build(
                     (WritableRegistry<LevelStem>)newWorldGenSettings.dimensions(),
                     newRegistry,
                     newWorldGenSettings.seed(),
                     newWorldGenSettings.generateFeatures()
                  );
               }

               return Pair.of(data, newRegistry.freeze());
            }
         },
         false,
         ExperimentalDialogType.BACKUP,
         false
      );
   }

   @Overwrite
   public WorldStem makeWorldStem(LevelStorageAccess storageAccess, boolean p_205154_) throws ExecutionException, InterruptedException {
      PackRepository packRepository = createPackRepository(storageAccess);
      return this.makeWorldStem(
         packRepository,
         p_205154_,
         DataPackConfigSupplier.loadFromWorld(storageAccess),
         (resourceManager, config) -> {
            Writable newRegistry = RegistryAccess.builtinCopy();
            DynamicOps<Tag> ops = RegistryOps.createAndLoad(NbtOps.INSTANCE, newRegistry, resourceManager);
            WorldData data = storageAccess.getDataTag(ops, config, newRegistry.allElementsLifecycle());
            if (data == null) {
               throw new IllegalStateException("Failed to load world");
            } else {
               WorldGenSettings newWorldGenSettings = data.worldGenSettings();
               if (((LevelStem)newWorldGenSettings.dimensions().get(LevelStem.NETHER)).generator() instanceof SkyVaultsChunkGenerator) {
                  SkyVaultsPreset.build(
                     (WritableRegistry<LevelStem>)newWorldGenSettings.dimensions(),
                     newRegistry,
                     newWorldGenSettings.seed(),
                     newWorldGenSettings.generateFeatures()
                  );
               }

               return Pair.of(data, newRegistry.freeze());
            }
         }
      );
   }
}
