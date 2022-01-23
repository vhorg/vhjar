package iskallia.vault;

import iskallia.vault.dump.VaultDataDump;
import iskallia.vault.init.ModCommands;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModFluids;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModPotions;
import iskallia.vault.integration.IntegrationDankStorage;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.scheduler.DailyScheduler;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SoulShardTraderData;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.event.VaultListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("the_vault")
public class Vault {
   public static final String MOD_ID = "the_vault";
   public static final Logger LOGGER = LogManager.getLogger();
   public static RegistryKey<World> VAULT_KEY = RegistryKey.func_240903_a_(Registry.field_239699_ae_, id("vault"));
   public static RegistryKey<World> OTHER_SIDE_KEY = RegistryKey.func_240903_a_(Registry.field_239699_ae_, id("the_other_side"));

   public Vault() {
      MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onCommandRegister);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoad);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBiomeLoadPost);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onPlayerLoggedIn);
      MinecraftForge.EVENT_BUS.addListener(VaultListener::onEvent);
      MinecraftForge.EVENT_BUS.addListener(ServerScheduler.INSTANCE::onServerTick);
      MinecraftForge.EVENT_BUS.addListener(VaultJigsawHelper::preloadVaultRooms);
      MinecraftForge.EVENT_BUS.addListener(PlayerVaultStatsData::onStartup);
      MinecraftForge.EVENT_BUS.addListener(DailyScheduler::start);
      MinecraftForge.EVENT_BUS.addListener(DailyScheduler::stop);
      MinecraftForge.EVENT_BUS.addListener(VaultDataDump::onStart);
      this.registerDeferredRegistries();
      ModCommands.registerArgumentTypes();
      if (ModList.get().isLoaded("dankstorage")) {
         MinecraftForge.EVENT_BUS.register(IntegrationDankStorage.class);
      }
   }

   public void registerDeferredRegistries() {
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      ModParticles.REGISTRY.register(modEventBus);
      ModFluids.REGISTRY.register(modEventBus);
      ModPotions.REGISTRY.register(modEventBus);
   }

   public void onCommandRegister(RegisterCommandsEvent event) {
      ModCommands.registerCommands(event.getDispatcher(), event.getEnvironment());
   }

   public void onBiomeLoad(BiomeLoadingEvent event) {
      event.getGeneration().func_242513_a(Decoration.UNDERGROUND_ORES, ModFeatures.VAULT_ROCK_ORE);
   }

   public void onBiomeLoadPost(BiomeLoadingEvent event) {
      if (event.getName().equals(id("spoopy"))) {
         for (Decoration stage : Decoration.values()) {
            event.getGeneration().getFeatures(stage).clear();
         }

         event.getGeneration().func_242513_a(Decoration.UNDERGROUND_DECORATION, ModFeatures.BREADCRUMB_CHEST);
      }
   }

   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
      ServerWorld serverWorld = player.func_71121_q();
      MinecraftServer server = player.func_184102_h();
      PlayerVaultStatsData.get(serverWorld).getVaultStats(player).sync(server);
      PlayerResearchesData.get(serverWorld).getResearches(player).sync(server);
      PlayerAbilitiesData.get(serverWorld).getAbilities(player).sync(server);
      PlayerTalentsData.get(serverWorld).getTalents(player).sync(server);
      EternalsData.get(serverWorld).syncTo(player);
      SoulShardTraderData.get(serverWorld).syncTo(player);
      ModConfigs.SOUL_SHARD.syncTo(player);
      GlobalDifficultyData.get(serverWorld).openDifficultySelection(player);
   }

   public static String sId(String name) {
      return "the_vault:" + name;
   }

   public static ResourceLocation id(String name) {
      return new ResourceLocation("the_vault", name);
   }
}
