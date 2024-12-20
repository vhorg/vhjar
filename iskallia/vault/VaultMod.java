package iskallia.vault;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.core.SkyVaultsPreset;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.dump.VaultDataDump;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModificationRegistry;
import iskallia.vault.init.ModClientCommands;
import iskallia.vault.init.ModCommands;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModFluids;
import iskallia.vault.init.ModLootConditions;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModPotions;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.integration.IntegrationDankStorage;
import iskallia.vault.integration.IntegrationMinimap;
import iskallia.vault.integration.IntegrationWorldMap;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.scheduler.DailyScheduler;
import iskallia.vault.world.data.DiscoveredAlchemyEffectsData;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.DiscoveredTrinketsData;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.PlayerBlackMarketData;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerHistoricFavoritesData;
import iskallia.vault.world.data.PlayerProficiencyData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SkillAltarData;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod("the_vault")
public class VaultMod {
   public static final String MOD_ID = "the_vault";
   public static final Logger LOGGER = LogManager.getLogger();
   public static ResourceKey<Level> ARENA_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, id("arena"));
   public static ResourceKey<Level> OTHER_SIDE_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, id("the_other_side"));
   public static DamageSource DMG_VAULT_TIMER = new DamageSource("vault_timer").bypassArmor().bypassInvul().bypassMagic();

   public VaultMod() {
      MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onCommandRegister);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoad);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onBiomeLoadPost);
      MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onPlayerLoggedIn);
      MinecraftForge.EVENT_BUS.addListener(ServerScheduler.INSTANCE::onServerTick);
      MinecraftForge.EVENT_BUS.addListener(DailyScheduler::start);
      MinecraftForge.EVENT_BUS.addListener(DailyScheduler::stop);
      MinecraftForge.EVENT_BUS.addListener(VaultDataDump::onStart);
      SpecialAbilityModificationRegistry.init();
      this.registerDeferredRegistries();
      ModCommands.registerArgumentTypes();
      if (ModList.get().isLoaded("dankstorage")) {
         MinecraftForge.EVENT_BUS.register(IntegrationDankStorage.class);
      }

      if (ModList.get().isLoaded("curios")) {
         FMLJavaModLoadingContext.get().getModEventBus().addListener(IntegrationCurios::registerHeadSlot);
      }

      if (ModList.get().isLoaded("xaerominimap") || ModList.get().isLoaded("xaeroworldmap")) {
         MinecraftForge.EVENT_BUS.register(IntegrationWorldMap.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("xaerominimap")) {
         MinecraftForge.EVENT_BUS.register(IntegrationMinimap.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT) {
         MinecraftForge.EVENT_BUS.addListener(ClientScheduler.INSTANCE::onClientTick);
         MinecraftForge.EVENT_BUS.addListener(this::onClientCommandRegister);
         SkyVaultsPreset.register();
      }

      CommonEvents.init();
      GeckoLib.initialize();
   }

   public void registerDeferredRegistries() {
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      ModParticles.REGISTRY.register(modEventBus);
      ModFluids.REGISTRY.register(modEventBus);
      ModPotions.REGISTRY.register(modEventBus);
      ModEntities.ENTITY_DATA_SERIALIZERS.register(modEventBus);
      ModLootConditions.LOOT_CONDITION_TYPES.register(modEventBus);
   }

   public void onCommandRegister(RegisterCommandsEvent event) {
      ModCommands.registerCommands(event.getDispatcher(), event.getEnvironment());
   }

   public void onClientCommandRegister(RegisterClientCommandsEvent event) {
      ModClientCommands.registerCommands(event.getDispatcher());
   }

   public void onBiomeLoad(BiomeLoadingEvent event) {
      event.getGeneration().addFeature(Decoration.UNDERGROUND_ORES, ModFeatures.PLACED_CHROMATIC_IRON_ORE_SMALL);
      event.getGeneration().addFeature(Decoration.UNDERGROUND_ORES, ModFeatures.PLACED_CHROMATIC_IRON_ORE_LARGE);
      event.getGeneration().addFeature(Decoration.UNDERGROUND_ORES, ModFeatures.PLACED_VAULT_STONE);
   }

   public void onBiomeLoadPost(BiomeLoadingEvent event) {
      if (Objects.equals(event.getName(), Biomes.THE_VOID.location())) {
         for (Decoration stage : Decoration.values()) {
            event.getGeneration().getFeatures(stage).clear();
         }
      }
   }

   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      ServerPlayer player = (ServerPlayer)event.getPlayer();
      ServerLevel serverWorld = player.getLevel();
      MinecraftServer server = player.getServer();
      PlayerVaultStatsData.get(serverWorld).getVaultStats(player).sync(server);
      PlayerResearchesData.get(serverWorld).sync(player);
      PlayerAbilitiesData.get(serverWorld).getAbilities(player).sync(SkillContext.of(player));
      PlayerTalentsData.get(serverWorld).getTalents(player).sync(SkillContext.of(player));
      PlayerExpertisesData.get(serverWorld).getExpertises(player).sync(SkillContext.of(player));
      PlayerArchetypeData.get(serverWorld).getArchetypeContainer(player).syncToClient(server);
      PlayerProficiencyData.get(serverWorld).sendProficiencyInformation(player);
      EternalsData.get(serverWorld).syncTo(player);
      PlayerBlackMarketData.get(serverWorld).getBlackMarket(player).syncToClient(server);
      PlayerHistoricFavoritesData.get(serverWorld).getHistoricFavorites(player).syncToClient(server);
      DiscoveredModelsData.get(serverWorld).syncTo(player);
      DiscoveredTrinketsData.get(serverWorld).syncTo(player);
      DiscoveredWorkbenchModifiersData.get(serverWorld).syncTo(player);
      DiscoveredAlchemyEffectsData.get(serverWorld).syncTo(player);
      SkillAltarData.get(serverWorld).syncTo(player);
      ModConfigs.SOUL_SHARD.syncTo(ModConfigs.SOUL_SHARD, player);
      ModConfigs.OMEGA_SOUL_SHARD.syncTo(ModConfigs.OMEGA_SOUL_SHARD, player);
      ModConfigs.GEAR_RECIPES.syncTo(ModConfigs.GEAR_RECIPES, player);
      ModConfigs.JEWEL_RECIPES.syncTo(ModConfigs.JEWEL_RECIPES, player);
      ModConfigs.TRINKET_RECIPES.syncTo(ModConfigs.TRINKET_RECIPES, player);
      ModConfigs.TOOL_RECIPES.syncTo(ModConfigs.TOOL_RECIPES, player);
      ModConfigs.INSCRIPTION_RECIPES.syncTo(ModConfigs.INSCRIPTION_RECIPES, player);
      ModConfigs.CATALYST_RECIPES.syncTo(ModConfigs.CATALYST_RECIPES, player);
      ModConfigs.JEWEL_CRAFTING_RECIPES.syncTo(ModConfigs.JEWEL_CRAFTING_RECIPES, player);
      DiscoveredModelsData.get(serverWorld).ensureResearchDiscoverables(player);
   }

   public static String sId(String name) {
      return "the_vault:" + name;
   }

   public static ResourceLocation id(String name) {
      return new ResourceLocation("the_vault", name);
   }
}
