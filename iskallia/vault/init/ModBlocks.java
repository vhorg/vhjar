package iskallia.vault.init;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.VaultMod;
import iskallia.vault.block.AlchemyArchiveBlock;
import iskallia.vault.block.AlchemyTableBlock;
import iskallia.vault.block.AncientCopperButtonBlock;
import iskallia.vault.block.AncientCopperConduitBlock;
import iskallia.vault.block.AncientCopperTrapDoorBlock;
import iskallia.vault.block.AngelBlock;
import iskallia.vault.block.AnimalPenBlock;
import iskallia.vault.block.AnimatrixBlock;
import iskallia.vault.block.ArtifactProjectorBlock;
import iskallia.vault.block.AscensionForgeBlock;
import iskallia.vault.block.BarredDoorBlock;
import iskallia.vault.block.BarredTrapDoorBlock;
import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.block.BountyBlock;
import iskallia.vault.block.CakeBlock;
import iskallia.vault.block.CardEssenceExtractorBlock;
import iskallia.vault.block.CatalystInfusionBlock;
import iskallia.vault.block.ChallengeControllerBlock;
import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.block.ConnectingCarpet;
import iskallia.vault.block.ConvertedSparkBlock;
import iskallia.vault.block.CrakeColumnBlock;
import iskallia.vault.block.CrakePedestalBlock;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.CrystalBuddingBlock;
import iskallia.vault.block.CrystalClusterBlock;
import iskallia.vault.block.CrystalWorkbenchBlock;
import iskallia.vault.block.CubeBlock;
import iskallia.vault.block.CustomEntitySpawnerBlock;
import iskallia.vault.block.DebagnetizerBlock;
import iskallia.vault.block.DemagnetizerBlock;
import iskallia.vault.block.DivineAltarBlock;
import iskallia.vault.block.DungeonDoorBlock;
import iskallia.vault.block.EasterEggBlock;
import iskallia.vault.block.EliteSpawnerBlock;
import iskallia.vault.block.ErrorBlock;
import iskallia.vault.block.EtchingVendorControllerBlock;
import iskallia.vault.block.EternalPedestalBlock;
import iskallia.vault.block.FinalVaultFrameBlock;
import iskallia.vault.block.FloatingTextBlock;
import iskallia.vault.block.FoliageDecorBlock;
import iskallia.vault.block.GateLockBlock;
import iskallia.vault.block.GildedCandelabraBlock;
import iskallia.vault.block.GridGatewayBlock;
import iskallia.vault.block.HeraldControllerBlock;
import iskallia.vault.block.HeraldTrophyBlock;
import iskallia.vault.block.HologramBlock;
import iskallia.vault.block.HourglassBlock;
import iskallia.vault.block.IdentificationStandBlock;
import iskallia.vault.block.InscriptionTableBlock;
import iskallia.vault.block.JewelCraftingTableBlock;
import iskallia.vault.block.JunkGemBlock;
import iskallia.vault.block.LodestoneBlock;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.LootStatueUpperBlock;
import iskallia.vault.block.MVPCrownBlock;
import iskallia.vault.block.MagnetTableBlock;
import iskallia.vault.block.MazeBlock;
import iskallia.vault.block.MeatBlock;
import iskallia.vault.block.MobBarrier;
import iskallia.vault.block.ModifierDiscoveryBlock;
import iskallia.vault.block.ModifierWorkbenchBlock;
import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.OfferingPillarBlock;
import iskallia.vault.block.OrnateChainBlock;
import iskallia.vault.block.PillarBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.PylonBlock;
import iskallia.vault.block.RelicPedestalBlock;
import iskallia.vault.block.RottenMeatBlock;
import iskallia.vault.block.ScavengerAltarBlock;
import iskallia.vault.block.ScavengerTreasureBlock;
import iskallia.vault.block.SconceWallBlock;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.block.SootLayerBlock;
import iskallia.vault.block.SoulPlaqueBlock;
import iskallia.vault.block.SparkBlock;
import iskallia.vault.block.SpiritExtractorBlock;
import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.TaskBuilderBlock;
import iskallia.vault.block.TaskPillarBlock;
import iskallia.vault.block.ToolStationBlock;
import iskallia.vault.block.ToolViseBlock;
import iskallia.vault.block.TotemManaRegenBlock;
import iskallia.vault.block.TotemMobDamageBlock;
import iskallia.vault.block.TotemPlayerDamageBlock;
import iskallia.vault.block.TotemPlayerHealthBlock;
import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.block.TreasurePedestalBlock;
import iskallia.vault.block.TreasureSandBlock;
import iskallia.vault.block.TrophyBlock;
import iskallia.vault.block.VaultAltarBlock;
import iskallia.vault.block.VaultAnvilBlock;
import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.block.VaultArtisanStationBlock;
import iskallia.vault.block.VaultBarrelBlock;
import iskallia.vault.block.VaultBars;
import iskallia.vault.block.VaultBedrockBlock;
import iskallia.vault.block.VaultCharmControllerBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDiffuserBlock;
import iskallia.vault.block.VaultDiffuserUpgradedBlock;
import iskallia.vault.block.VaultEnchanterBlock;
import iskallia.vault.block.VaultEnhancementAltar;
import iskallia.vault.block.VaultForgeBlock;
import iskallia.vault.block.VaultJewelApplicationStationBlock;
import iskallia.vault.block.VaultJewelCuttingStationBlock;
import iskallia.vault.block.VaultLogBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultRecyclerBlock;
import iskallia.vault.block.VaultRockBlock;
import iskallia.vault.block.VaultSmallSweetsBlock;
import iskallia.vault.block.VaultSweetsBlock;
import iskallia.vault.block.VaultTinySweetsBlock;
import iskallia.vault.block.VelvetBed;
import iskallia.vault.block.VendorDoorBlock;
import iskallia.vault.block.WardrobeBlock;
import iskallia.vault.block.WildSpawnerBlock;
import iskallia.vault.block.base.GodAltarBlock;
import iskallia.vault.block.base.GodAltarTileEntity;
import iskallia.vault.block.challenge.EliteControllerProxyBlock;
import iskallia.vault.block.challenge.RaidControllerProxyBlock;
import iskallia.vault.block.discoverable.DiscoverTriggeringBlock;
import iskallia.vault.block.entity.AlchemyArchiveTileEntity;
import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.block.entity.AncientCopperConduitTileEntity;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.AngelBlockTileEntity;
import iskallia.vault.block.entity.AnimalPenTileEntity;
import iskallia.vault.block.entity.AnimatrixTileEntity;
import iskallia.vault.block.entity.ArtifactProjectorTileEntity;
import iskallia.vault.block.entity.AscensionForgeTileEntity;
import iskallia.vault.block.entity.BlackMarketTileEntity;
import iskallia.vault.block.entity.BountyTableTileEntity;
import iskallia.vault.block.entity.CardEssenceExtractorTileEntity;
import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.block.entity.CoinPilesTileEntity;
import iskallia.vault.block.entity.ConvertedSparkTileEntity;
import iskallia.vault.block.entity.CrakePedestalTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.CrystalBuddingBlockEntity;
import iskallia.vault.block.entity.CrystalWorkbenchTileEntity;
import iskallia.vault.block.entity.CubeTileEntity;
import iskallia.vault.block.entity.CustomEntitySpawnerTileEntity;
import iskallia.vault.block.entity.DebagnetizerTileEntity;
import iskallia.vault.block.entity.DemagnetizerTileEntity;
import iskallia.vault.block.entity.DivineAltarTileEntity;
import iskallia.vault.block.entity.DungeonDoorTileEntity;
import iskallia.vault.block.entity.EliteSpawnerTileEntity;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.block.entity.EternalPedestalTileEntity;
import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.block.entity.FloatingTextTileEntity;
import iskallia.vault.block.entity.FoliageDecorTileEntity;
import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.block.entity.GridGatewayTileEntity;
import iskallia.vault.block.entity.HeraldControllerTileEntity;
import iskallia.vault.block.entity.HeraldTrophyTileEntity;
import iskallia.vault.block.entity.HologramTileEntity;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.block.entity.IdentificationStandTileEntity;
import iskallia.vault.block.entity.InscriptionTableTileEntity;
import iskallia.vault.block.entity.JewelCraftingTableTileEntity;
import iskallia.vault.block.entity.LodestoneTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.MagnetTableTile;
import iskallia.vault.block.entity.MobBarrierTileEntity;
import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.block.entity.ObeliskTileEntity;
import iskallia.vault.block.entity.OfferingPillarTileEntity;
import iskallia.vault.block.entity.PylonTileEntity;
import iskallia.vault.block.entity.RelicPedestalTileEntity;
import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import iskallia.vault.block.entity.ScavengerTreasureTileEntity;
import iskallia.vault.block.entity.ShopPedestalBlockTile;
import iskallia.vault.block.entity.SkillAltarTileEntity;
import iskallia.vault.block.entity.SoulPlaqueTileEntity;
import iskallia.vault.block.entity.SparkTileEntity;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.block.entity.TaskPillarTileEntity;
import iskallia.vault.block.entity.ToolStationTileEntity;
import iskallia.vault.block.entity.ToolViseTile;
import iskallia.vault.block.entity.TotemManaRegenTileEntity;
import iskallia.vault.block.entity.TotemMobDamageTileEntity;
import iskallia.vault.block.entity.TotemPlayerDamageTileEntity;
import iskallia.vault.block.entity.TotemPlayerHealthTileEntity;
import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.block.entity.TreasureDoorTileEntity;
import iskallia.vault.block.entity.TreasurePedestalTileEntity;
import iskallia.vault.block.entity.TreasureSandTileEntity;
import iskallia.vault.block.entity.TrophyTileEntity;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.block.entity.VaultCharmControllerTileEntity;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.block.entity.VaultDiffuserTileEntity;
import iskallia.vault.block.entity.VaultDiffuserUpgradedTileEntity;
import iskallia.vault.block.entity.VaultEnchanterTileEntity;
import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.block.entity.VaultJewelApplicationStationTileEntity;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.block.entity.VaultOreTileEntity;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.block.entity.VaultRecyclerTileEntity;
import iskallia.vault.block.entity.VelvetBedTileEntity;
import iskallia.vault.block.entity.VendorDoorTileEntity;
import iskallia.vault.block.entity.WardrobeTileEntity;
import iskallia.vault.block.entity.WildSpawnerTileEntity;
import iskallia.vault.block.entity.challenge.elite.EliteControllerProxyBlockEntity;
import iskallia.vault.block.entity.challenge.raid.RaidControllerBlockEntity;
import iskallia.vault.block.entity.challenge.raid.RaidControllerProxyBlockEntity;
import iskallia.vault.block.entity.challenge.raid.elite.EliteControllerBlockEntity;
import iskallia.vault.block.entity.challenge.xmark.XMarkControllerBlockEntity;
import iskallia.vault.block.item.DungeonDoorBlockItem;
import iskallia.vault.block.item.EasterEggBlockItem;
import iskallia.vault.block.item.FinalVaultFrameBlockItem;
import iskallia.vault.block.item.GodAltarBlockItem;
import iskallia.vault.block.item.HeraldTrophyItem;
import iskallia.vault.block.item.HourglassBlockItem;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.PlaceholderBlockItem;
import iskallia.vault.block.item.SoulPlaqueBlockItem;
import iskallia.vault.block.item.TreasureDoorBlockItem;
import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.block.item.VaultOreBlockItem;
import iskallia.vault.block.item.VendorDoorBlockItem;
import iskallia.vault.block.render.AlchemyTableRenderer;
import iskallia.vault.block.render.AncientCopperConduitRenderer;
import iskallia.vault.block.render.AngelBlockRenderer;
import iskallia.vault.block.render.AnimalPenRenderer;
import iskallia.vault.block.render.AnimatrixRenderer;
import iskallia.vault.block.render.ArtifactProjectorRenderer;
import iskallia.vault.block.render.BlackMarketRenderer;
import iskallia.vault.block.render.BountyTableRenderer;
import iskallia.vault.block.render.ChallengeControllerRenderer;
import iskallia.vault.block.render.ConvertedSparkRenderer;
import iskallia.vault.block.render.CrakePedestalRenderer;
import iskallia.vault.block.render.CryoChamberRenderer;
import iskallia.vault.block.render.CrystalWorkbenchRenderer;
import iskallia.vault.block.render.DivineAltarRenderer;
import iskallia.vault.block.render.EliteControllerProxyRenderer;
import iskallia.vault.block.render.EnhancementAltarRenderer;
import iskallia.vault.block.render.EternalPedestalRenderer;
import iskallia.vault.block.render.FinalVaultFrameRenderer;
import iskallia.vault.block.render.FloatingTextRenderer;
import iskallia.vault.block.render.FoliageDecorRenderer;
import iskallia.vault.block.render.GateLockRenderer;
import iskallia.vault.block.render.GodAltarRenderer;
import iskallia.vault.block.render.GridGatewayRenderer;
import iskallia.vault.block.render.HeraldControllerRenderer;
import iskallia.vault.block.render.HeraldTrophyRenderer;
import iskallia.vault.block.render.HologramRenderer;
import iskallia.vault.block.render.HourglassRenderer;
import iskallia.vault.block.render.IdentificationStandRenderer;
import iskallia.vault.block.render.JewelApplicationStationRenderer;
import iskallia.vault.block.render.JewelCraftingTableRenderer;
import iskallia.vault.block.render.JewelCuttingStationRenderer;
import iskallia.vault.block.render.LootStatueRenderer;
import iskallia.vault.block.render.MagnetTableRenderer;
import iskallia.vault.block.render.MobBarrierRenderer;
import iskallia.vault.block.render.ModifierDiscoveryRenderer;
import iskallia.vault.block.render.ModifierWorkbenchRenderer;
import iskallia.vault.block.render.MonolithRenderer;
import iskallia.vault.block.render.OfferingPillarRenderer;
import iskallia.vault.block.render.PotionModifierDiscoveryRenderer;
import iskallia.vault.block.render.PylonRenderer;
import iskallia.vault.block.render.RaidControllerProxyRenderer;
import iskallia.vault.block.render.RelicPedestalRenderer;
import iskallia.vault.block.render.ScavengerAltarRenderer;
import iskallia.vault.block.render.ShopPedestalBlockTileRenderer;
import iskallia.vault.block.render.SkillAltarRenderer;
import iskallia.vault.block.render.SoulPlaqueRenderer;
import iskallia.vault.block.render.SparkRenderer;
import iskallia.vault.block.render.SpawnerRenderer;
import iskallia.vault.block.render.SpiritExtractorRenderer;
import iskallia.vault.block.render.TaskPillarRenderer;
import iskallia.vault.block.render.ToolStationRenderer;
import iskallia.vault.block.render.ToolViseRenderer;
import iskallia.vault.block.render.TotemManaRegenRenderer;
import iskallia.vault.block.render.TotemMobDamageRenderer;
import iskallia.vault.block.render.TotemPlayerDamageRenderer;
import iskallia.vault.block.render.TotemPlayerHealthRenderer;
import iskallia.vault.block.render.TreasurePedestalTileRenderer;
import iskallia.vault.block.render.TrophyRenderer;
import iskallia.vault.block.render.VaultAltarRenderer;
import iskallia.vault.block.render.VaultChestRenderer;
import iskallia.vault.block.render.VaultDiffuserRenderer;
import iskallia.vault.block.render.VaultDiffuserUpgradedRenderer;
import iskallia.vault.block.render.VaultEnchanterRenderer;
import iskallia.vault.block.render.VaultPortalRenderer;
import iskallia.vault.block.render.VelvetBedRenderer;
import iskallia.vault.block.render.WardrobeRenderer;
import iskallia.vault.client.render.AncientCopperConduitBlockISTER;
import iskallia.vault.client.render.AngelBlockISTER;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.fluid.block.VoidFluidBlock;
import iskallia.vault.item.CoinBlockItem;
import iskallia.vault.item.IdentifiedArtifactItem;
import iskallia.vault.item.SconceItem;
import iskallia.vault.item.VaultChestBlockItem;
import iskallia.vault.item.VaultSweetsBlockItem;
import iskallia.vault.item.VelvetBedItem;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModBlocks {
   public static final ErrorBlock ERROR_BLOCK = new ErrorBlock();
   public static final FloatingTextBlock FLOATING_TEXT = new FloatingTextBlock();
   public static final VaultPortalBlock VAULT_PORTAL = new VaultPortalBlock();
   public static final HeraldTrophyBlock HERALD_TROPHY = new HeraldTrophyBlock();
   public static final FinalVaultFrameBlock FINAL_VAULT_FRAME = new FinalVaultFrameBlock();
   public static final VaultAltarBlock VAULT_ALTAR = new VaultAltarBlock();
   public static final VaultOreBlock ALEXANDRITE_ORE = new VaultOreBlock(ModItems.ALEXANDRITE_GEM);
   public static final VaultOreBlock ASHIUM_ORE = new VaultOreBlock(ModItems.ASHIUM_GEM);
   public static final VaultOreBlock BENITOITE_ORE = new VaultOreBlock(ModItems.BENITOITE_GEM);
   public static final VaultOreBlock BLACK_OPAL_ORE = new VaultOreBlock(ModItems.BLACK_OPAL_GEM);
   public static final VaultOreBlock BOMIGNITE_ORE = new VaultOreBlock(ModItems.BOMIGNITE_GEM);
   public static final VaultOreBlock ECHO_ORE = new VaultOreBlock(ModItems.ECHO_GEM);
   public static final VaultOreBlock GORGINITE_ORE = new VaultOreBlock(ModItems.GORGINITE_GEM);
   public static final VaultOreBlock ISKALLIUM_ORE = new VaultOreBlock(ModItems.ISKALLIUM_GEM);
   public static final VaultOreBlock LARIMAR_ORE = new VaultOreBlock(ModItems.LARIMAR_GEM);
   public static final VaultOreBlock PAINITE_ORE = new VaultOreBlock(ModItems.PAINITE_GEM);
   public static final VaultOreBlock PETZANITE_ORE = new VaultOreBlock(ModItems.PETZANITE_GEM);
   public static final VaultOreBlock PUFFIUM_ORE = new VaultOreBlock(ModItems.PUFFIUM_GEM);
   public static final VaultOreBlock SPARKLETINE_ORE = new VaultOreBlock(ModItems.SPARKLETINE_GEM);
   public static final VaultOreBlock TUBIUM_ORE = new VaultOreBlock(ModItems.TUBIUM_GEM);
   public static final VaultOreBlock UPALINE_ORE = new VaultOreBlock(ModItems.UPALINE_GEM);
   public static final VaultOreBlock WUTODIE_ORE = new VaultOreBlock(ModItems.WUTODIE_GEM);
   public static final VaultOreBlock XENIUM_ORE = new VaultOreBlock(ModItems.XENIUM_GEM);
   public static final VaultRockBlock VAULT_ROCK_ORE = new VaultRockBlock();
   public static final TreasureDoorBlock TREASURE_DOOR = new TreasureDoorBlock();
   public static final DungeonDoorBlock DUNGEON_DOOR = new DungeonDoorBlock();
   public static final VendorDoorBlock VENDOR_DOOR = new VendorDoorBlock();
   public static final GateLockBlock GATE_LOCK = new GateLockBlock();
   public static final HologramBlock HOLOGRAM = new HologramBlock();
   public static final HeraldControllerBlock HERALD_CONTROLLER = new HeraldControllerBlock();
   public static final ChallengeControllerBlock RAID_CONTROLLER = new ChallengeControllerBlock(() -> ModBlocks.RAID_CONTROLLER_TILE_ENTITY);
   public static final ChallengeControllerBlock X_MARK_CONTROLLER = new ChallengeControllerBlock(() -> ModBlocks.X_MARK_CONTROLLER_TILE_ENTITY);
   public static final ChallengeControllerBlock ELITE_CONTROLLER = new ChallengeControllerBlock(() -> ModBlocks.ELITE_CONTROLLER_TILE_ENTITY);
   public static final RaidControllerProxyBlock RAID_CONTROLLER_PROXY = new RaidControllerProxyBlock();
   public static final EliteControllerProxyBlock ELITE_CONTROLLER_PROXY = new EliteControllerProxyBlock();
   public static final VaultArtifactBlock VAULT_ARTIFACT = new VaultArtifactBlock();
   public static final VaultCrateBlock VAULT_CRATE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_CAKE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_ARENA = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_SCAVENGER = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_CHAMPION = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_BOUNTY = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_MONOLITH = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_ELIXIR = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_PARADOX = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_BINGO = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_BINGO_FULL = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_OFFERING_BOSS = new VaultCrateBlock();
   public static final ObeliskBlock OBELISK = new ObeliskBlock();
   public static final MonolithBlock MONOLITH = new MonolithBlock();
   public static final LodestoneBlock LODESTONE = new LodestoneBlock();
   public static final CrakePedestalBlock CRAKE_PEDESTAL = new CrakePedestalBlock();
   public static final CrakeColumnBlock CRAKE_COLUMN = new CrakeColumnBlock();
   public static final GridGatewayBlock GRID_GATEWAY = new GridGatewayBlock();
   public static final TaskPillarBlock TASK_PILLAR = new TaskPillarBlock();
   public static final TaskBuilderBlock TASK_BUILDER = new TaskBuilderBlock();
   public static final PylonBlock PYLON = new PylonBlock();
   public static final VaultEnhancementAltar ENHANCEMENT_ALTAR = new VaultEnhancementAltar();
   public static final MVPCrownBlock MVP_CROWN = new MVPCrownBlock();
   public static final EasterEggBlock EASTER_EGG = new EasterEggBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).lightLevel(state -> 9)
   );
   public static final VaultBedrockBlock VAULT_BEDROCK = new VaultBedrockBlock();
   public static final Block VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F));
   public static final Block CHISELED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final Block POLISHED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final SlabBlock POLISHED_VAULT_STONE_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final StairBlock POLISHED_VAULT_STONE_STAIRS = new StairBlock(
      POLISHED_VAULT_STONE::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE)
   );
   public static final Block BUMBO_POLISHED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final Block VAULT_STONE_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final SlabBlock VAULT_STONE_BRICK_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final StairBlock VAULT_STONE_BRICK_STAIRS = new StairBlock(
      VAULT_STONE_BRICKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE)
   );
   public static final Block VAULT_STONE_BRICKS_CRACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final PillarBlock VAULT_STONE_PILLAR = new PillarBlock();
   public static final Block VAULT_COBBLESTONE = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE).strength(2.0F, 6.0F)
   );
   public static final GlassBlock VAULT_GLASS = new GlassBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.GLASS).strength(-1.0F, 3600000.0F)
   );
   public static final RelicPedestalBlock RELIC_PEDESTAL = new RelicPedestalBlock();
   public static final LootStatueBlock LOOT_STATUE = new LootStatueBlock();
   public static final LootStatueUpperBlock LOOT_STATUE_UPPER = new LootStatueUpperBlock();
   public static final ShopPedestalBlock SHOP_PEDESTAL = new ShopPedestalBlock();
   public static final TreasurePedestalBlock TREASURE_PEDESTAL = new TreasurePedestalBlock();
   public static final CryoChamberBlock CRYO_CHAMBER = new CryoChamberBlock();
   public static final Block VAULT_DIAMOND_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK));
   public static final MazeBlock MAZE_BLOCK = new MazeBlock();
   public static final TrophyBlock TROPHY_STATUE = new TrophyBlock();
   public static final TransmogTableBlock TRANSMOG_TABLE = new TransmogTableBlock();
   public static final VaultForgeBlock VAULT_FORGE = new VaultForgeBlock();
   public static final ToolStationBlock TOOL_STATION = new ToolStationBlock();
   public static final InscriptionTableBlock INSCRIPTION_TABLE = new InscriptionTableBlock();
   public static final VaultArtisanStationBlock VAULT_ARTISAN_STATION = new VaultArtisanStationBlock();
   public static final VaultJewelCuttingStationBlock VAULT_JEWEL_CUTTING_STATION = new VaultJewelCuttingStationBlock();
   public static final VaultJewelApplicationStationBlock VAULT_JEWEL_APPLICATION_STATION = new VaultJewelApplicationStationBlock();
   public static final JewelCraftingTableBlock JEWEL_CRAFTING_TABLE = new JewelCraftingTableBlock();
   public static final CrystalWorkbenchBlock CRYSTAL_WORKBENCH = new CrystalWorkbenchBlock();
   public static final VaultRecyclerBlock VAULT_RECYCLER = new VaultRecyclerBlock();
   public static final VaultDiffuserBlock VAULT_DIFFUSER = new VaultDiffuserBlock();
   public static final VaultDiffuserUpgradedBlock VAULT_HARVESTER = new VaultDiffuserUpgradedBlock();
   public static final ModifierWorkbenchBlock MODIFIER_WORKBENCH = new ModifierWorkbenchBlock();
   public static final ModifierDiscoveryBlock MODIFIER_DISCOVERY = new ModifierDiscoveryBlock();
   public static final AlchemyArchiveBlock ALCHEMY_ARCHIVE = new AlchemyArchiveBlock();
   public static final AlchemyTableBlock ALCHEMY_TABLE = new AlchemyTableBlock();
   public static final VaultEnchanterBlock VAULT_ENCHANTER = new VaultEnchanterBlock();
   public static final IdentificationStandBlock IDENTIFICATION_STAND = new IdentificationStandBlock();
   public static final CardEssenceExtractorBlock CARD_ESSENCE_EXTRACTOR = new CardEssenceExtractorBlock();
   public static final Block WOODEN_CHEST = new VaultChestBlock(
      VaultChestType.WOODEN, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(40.0F, 5.0F)
   );
   public static final Block GILDED_CHEST = new VaultChestBlock(
      VaultChestType.GILDED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(0.6F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block LIVING_CHEST = new VaultChestBlock(
      VaultChestType.LIVING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.6F, 5.0F)
   );
   public static final Block ORNATE_CHEST = new VaultChestBlock(
      VaultChestType.ORNATE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.6F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block HARDENED_CHEST = new VaultChestBlock(
      VaultChestType.HARDENED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(40.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block FLESH_CHEST = new VaultChestBlock(
      VaultChestType.FLESH,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.6F, 5.0F).sound(SoundType.HONEY_BLOCK)
   );
   public static final Block ENIGMA_CHEST = new VaultChestBlock(
      VaultChestType.ENIGMA, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.6F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block TREASURE_CHEST = new VaultChestBlock(
      VaultChestType.TREASURE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(-1.0F, 5.0F).sound(SoundType.METAL)
   );
   public static final Block ALTAR_CHEST = new VaultChestBlock(
      VaultChestType.ALTAR, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(-1.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block ORNATE_STRONGBOX = new VaultChestBlock(
      VaultChestType.ORNATE,
      true,
      true,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(50.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block GILDED_STRONGBOX = new VaultChestBlock(
      VaultChestType.GILDED,
      true,
      true,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(50.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block LIVING_STRONGBOX = new VaultChestBlock(
      VaultChestType.LIVING,
      true,
      true,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(50.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block ORNATE_BARREL = new VaultBarrelBlock(
      VaultChestType.ORNATE,
      true,
      false,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BARREL).strength(60.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block GILDED_BARREL = new VaultBarrelBlock(
      VaultChestType.GILDED,
      true,
      false,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BARREL).strength(60.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block LIVING_BARREL = new VaultBarrelBlock(
      VaultChestType.LIVING,
      true,
      false,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BARREL).strength(60.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block WOODEN_BARREL = new VaultBarrelBlock(
      VaultChestType.WOODEN,
      true,
      false,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BARREL).strength(60.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block WOODEN_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.WOODEN, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F)
   );
   public static final Block GILDED_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.GILDED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(0.3F).sound(SoundType.STONE)
   );
   public static final Block LIVING_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.LIVING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F)
   );
   public static final Block ORNATE_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.ORNATE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.STONE)
   );
   public static final Block TREASURE_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.TREASURE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.METAL)
   );
   public static final Block ALTAR_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.ALTAR, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.STONE)
   );
   public static final Block HARDENED_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.HARDENED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.STONE)
   );
   public static final Block ENIGMA_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.ENIGMA, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.STONE)
   );
   public static final Block FLESH_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.FLESH, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(0.3F).sound(SoundType.STONE)
   );
   public static final GodAltarBlock GOD_ALTAR = new GodAltarBlock();
   public static final HourglassBlock HOURGLASS = new HourglassBlock();
   public static final DivineAltarBlock DIVINE_ALTAR = new DivineAltarBlock();
   public static final ScavengerAltarBlock SCAVENGER_ALTAR = new ScavengerAltarBlock();
   public static final ScavengerTreasureBlock SCAVENGER_TREASURE = new ScavengerTreasureBlock();
   public static final StabilizerBlock STABILIZER = new StabilizerBlock();
   public static final CatalystInfusionBlock CATALYST_INFUSION_TABLE = new CatalystInfusionBlock();
   public static final EtchingVendorControllerBlock ETCHING_CONTROLLER_BLOCK = new EtchingVendorControllerBlock();
   public static final VaultCharmControllerBlock VAULT_CHARM_CONTROLLER_BLOCK = new VaultCharmControllerBlock();
   public static final PlaceholderBlock PLACEHOLDER = new PlaceholderBlock();
   public static final CoinPileBlock COIN_PILE = new CoinPileBlock();
   public static final CoinPileDecorBlock BRONZE_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock SILVER_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock GOLD_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock PLATINUM_COIN_PILE = new CoinPileDecorBlock();
   public static final ToolViseBlock TOOL_VISE = new ToolViseBlock();
   public static final MagnetTableBlock MAGNET_TABLE = new MagnetTableBlock();
   public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = new DemagnetizerBlock();
   public static final DebagnetizerBlock DEBAGNETIZER = new DebagnetizerBlock();
   public static final OreBlock CHROMATIC_IRON_ORE = new OreBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE)
         .requiresCorrectToolForDrops()
         .strength(3.0F, 3.0F)
         .sound(SoundType.DEEPSLATE)
   );
   public static final Block RAW_CHROMATIC_IRON_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK));
   public static final Block CHROMATIC_IRON_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
   public static final Block CHROMATIC_STEEL_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
   public static final Block BLACK_CHROMATIC_STEEL_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
   public static final Block YELLOW_PUZZLE_CONCRETE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE));
   public static final Block PINK_PUZZLE_CONCRETE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE));
   public static final Block GREEN_PUZZLE_CONCRETE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE));
   public static final Block BLUE_PUZZLE_CONCRETE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE));
   public static final SoulPlaqueBlock SOUL_PLAQUE = new SoulPlaqueBlock();
   public static final Block VAULT_MEAT_BLOCK = new MeatBlock();
   public static final Block PACKED_VAULT_MEAT_BLOCK = new MeatBlock();
   public static final TreasureSandBlock TREASURE_SAND = new TreasureSandBlock();
   public static final EliteSpawnerBlock ELITE_SPAWNER = new EliteSpawnerBlock();
   public static final WildSpawnerBlock WILD_SPAWNER = new WildSpawnerBlock();
   public static final CustomEntitySpawnerBlock CUSTOM_ENTITY_SPAWNER = new CustomEntitySpawnerBlock();
   public static final SpiritExtractorBlock SPIRIT_EXTRACTOR = new SpiritExtractorBlock();
   public static final WardrobeBlock WARDROBE = new WardrobeBlock();
   public static final SkillAltarBlock SKILL_ALTAR = new SkillAltarBlock();
   public static final CakeBlock CAKE = new CakeBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.CAKE).strength(-1.0F, 3600000.0F).sound(SoundType.WOOL)
   );
   public static final Block MAGIC_SILK_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL));
   public static final DiscoverTriggeringBlock SUGAR_PLUM_FAIRY_FLOWER = new DiscoverTriggeringBlock(
      List.of(
         new Pair(DiscoverTriggeringBlock.ARMOR_MODEL_DISCOVERY, ModDynamicModels.Armor.STRESS_FLOWER.getId()),
         new Pair(DiscoverTriggeringBlock.GEAR_MODEL_DISCOVERY, ModDynamicModels.Swords.ALLIUMBLADE.getId())
      ),
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.ALLIUM),
      Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0)
   );
   public static final DiscoverTriggeringBlock GOLDEN_TOOTH = new DiscoverTriggeringBlock(
      Collections.singletonList(new Pair(DiscoverTriggeringBlock.ARMOR_MODEL_DISCOVERY, ModDynamicModels.Armor.BUMBO.getId())),
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)
   );
   public static final CubeBlock CUBE_BLOCK = new CubeBlock();
   public static final BountyBlock BOUNTY_BLOCK = new BountyBlock();
   public static final BlackMarketBlock BLACK_MARKET = new BlackMarketBlock();
   public static final AnimalPenBlock ANIMAL_PEN = new AnimalPenBlock();
   public static final AnimalPenBlock ANIMAL_JAR = new AnimalPenBlock();
   public static final CrystalBuddingBlock CRYSTAL_BUDDING = new CrystalBuddingBlock();
   public static final CrystalClusterBlock CRYSTAL_CLUSTER = new CrystalClusterBlock(SoundType.AMETHYST_CLUSTER, 5, 7, 3.0);
   public static final CrystalClusterBlock LARGE_CRYSTAL_BUD = new CrystalClusterBlock(SoundType.MEDIUM_AMETHYST_BUD, 4, 5, 3.0);
   public static final CrystalClusterBlock MEDIUM_CRYSTAL_BUD = new CrystalClusterBlock(SoundType.LARGE_AMETHYST_BUD, 2, 4, 3.0);
   public static final CrystalClusterBlock SMALL_CRYSTAL_BUD = new CrystalClusterBlock(SoundType.SMALL_AMETHYST_BUD, 1, 3, 4.0);
   public static final VaultAnvilBlock VAULT_ANVIL = new VaultAnvilBlock();
   public static final Block WUTODIC_SILVER_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
   public static final EternalPedestalBlock ETERNAL_PEDESTAL = new EternalPedestalBlock();
   public static final AngelBlock ANGEL_BLOCK = new AngelBlock();
   public static final AncientCopperConduitBlock ANCIENT_COPPER_CONDUIT_BLOCK = new AncientCopperConduitBlock();
   public static final MobBarrier MOB_BARRIER = new MobBarrier();
   public static final SparkBlock SPARK = new SparkBlock();
   public static final ConvertedSparkBlock CONVERTED_SPARK = new ConvertedSparkBlock();
   public static final ArtifactProjectorBlock ARTIFACT_PROJECTOR_BLOCK = new ArtifactProjectorBlock();
   public static final FoliageDecorBlock LIVING_ROCK_PLANTER = new FoliageDecorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.DIRT).sound(SoundType.ROOTED_DIRT)
   );
   public static final AnimatrixBlock ANIMATRIX_BLOCK = new AnimatrixBlock();
   public static final JunkGemBlock TOPAZ_BLOCK = new JunkGemBlock();
   public static final TorchBlock GILDED_SCONCE = new TorchBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TORCH), ParticleTypes.FLAME
   );
   public static final SconceWallBlock GILDED_SCONCE_WALL = new SconceWallBlock();
   public static SconceItem GILDED_SCONCE_ITEM = new SconceItem(VaultMod.id("gilded_sconce"), GILDED_SCONCE, GILDED_SCONCE_WALL);
   public static final OrnateChainBlock ORNATE_CHAIN = new OrnateChainBlock();
   public static final OrnateChainBlock ORNATE_CHAIN_RUSTY = new OrnateChainBlock();
   public static final Block ORNATE_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS_RUSTY = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BLOCK_CHISELED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BLOCK_TILED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final PillarBlock ORNATE_BLOCK_PILLAR = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS_CHIPPED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS_CRACKED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS_NETHERITE = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final VelvetBed VELVET_BED = new VelvetBed();
   public static VelvetBedItem VELVET_BED_ITEM = new VelvetBedItem(
      VaultMod.id("velvet_bed"), VELVET_BED, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1)
   );
   public static final Block VELVET_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.RED_WOOL));
   public static final Block VELVET_BLOCK_CHISELED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.RED_WOOL));
   public static final Block VELVET_BLOCK_STRIPS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.RED_WOOL));
   public static final ConnectingCarpet VELVET_CARPET = new ConnectingCarpet(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.RED_CARPET)
   );
   public static final Block ORNATE_BLOCK_VELVET = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BRICKS_VELVET = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final PillarBlock ORNATE_BLOCK_VELVET_PILLAR = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final Block ORNATE_BLOCK_VELVET_CHISELED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F, 6.0F)
   );
   public static final SootLayerBlock SOOT = new SootLayerBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SNOW).sound(SoundType.GRAVEL).color(MaterialColor.COLOR_BLACK)
   );
   public static final SootLayerBlock VAULT_MOSS = new SootLayerBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).sound(SoundType.MOSS)
   );
   public static final Block GILDED_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final PillarBlock GILDED_BLOCK_PILLAR = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE).strength(5.0F, 6.0F)
   );
   public static final Block GILDED_BLOCK_CHISELED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_BLOCK_BUMBO = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_BRICKS_CRACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_BRICKS_DULL = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_BRICKS_CRACKED_DULL = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block GILDED_COBBLE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BLACKSTONE));
   public static final Block ANCIENT_COPPER_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK));
   public static final Block ANCIENT_COPPER_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK));
   public static final Block ANCIENT_COPPER_SMALL_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK));
   public static final PillarBlock ANCIENT_COPPER_BLOCK_PILLAR = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_BLOCK_EXPOSED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK));
   public static final Block ANCIENT_COPPER_BRICKS_EXPOSED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_SMALL_BRICKS_EXPOSED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final PillarBlock ANCIENT_COPPER_BLOCK_PILLAR_EXPOSED = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_BLOCK_WEATHERED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_BRICKS_WEATHERED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_SMALL_BRICKS_WEATHERED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final PillarBlock ANCIENT_COPPER_BLOCK_PILLAR_WEATHERED = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_BLOCK_OXIDIZED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_BRICKS_OXIDIZED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final Block ANCIENT_COPPER_SMALL_BRICKS_OXIDIZED = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final PillarBlock ANCIENT_COPPER_BLOCK_PILLAR_OXIDIZED = new PillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
   );
   public static final AncientCopperTrapDoorBlock ANCIENT_COPPER_TRAPDOOR = new AncientCopperTrapDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
         .requiresCorrectToolForDrops()
         .strength(5.0F)
         .noOcclusion()
         .isValidSpawn((state, blockGetter, pos, entityType) -> false)
   );
   public static final AncientCopperButtonBlock ANCIENT_COPPER_BUTTON = new AncientCopperButtonBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.COPPER)
   );
   public static final AncientCopperTrapDoorBlock ANCIENT_COPPER_TRAPDOOR_EXPOSED = new AncientCopperTrapDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
         .requiresCorrectToolForDrops()
         .strength(5.0F)
         .noOcclusion()
         .isValidSpawn((state, blockGetter, pos, entityType) -> false)
   );
   public static final AncientCopperButtonBlock ANCIENT_COPPER_BUTTON_EXPOSED = new AncientCopperButtonBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.COPPER)
   );
   public static final AncientCopperTrapDoorBlock ANCIENT_COPPER_TRAPDOOR_WEATHERED = new AncientCopperTrapDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
         .requiresCorrectToolForDrops()
         .strength(5.0F)
         .noOcclusion()
         .isValidSpawn((state, blockGetter, pos, entityType) -> false)
   );
   public static final AncientCopperButtonBlock ANCIENT_COPPER_BUTTON_WEATHERED = new AncientCopperButtonBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.COPPER)
   );
   public static final AncientCopperTrapDoorBlock ANCIENT_COPPER_TRAPDOOR_OXIDIZED = new AncientCopperTrapDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
         .requiresCorrectToolForDrops()
         .strength(5.0F)
         .noOcclusion()
         .isValidSpawn((state, blockGetter, pos, entityType) -> false)
   );
   public static final AncientCopperButtonBlock ANCIENT_COPPER_BUTTON_OXIDIZED = new AncientCopperButtonBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.COPPER)
   );
   public static final RottenMeatBlock ROTTEN_MEAT_BLOCK = new RottenMeatBlock();
   public static final Block WOODEN_LOG = VaultLogBlock.log(() -> ModBlocks.STRIPPED_WOODEN_LOG, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block STRIPPED_WOODEN_LOG = VaultLogBlock.stripped(MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block WOODEN_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final SlabBlock WOODEN_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_SLAB));
   public static final StairBlock WOODEN_STAIRS = new StairBlock(
      WOODEN_PLANKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)
   );
   public static final Block OVERGROWN_WOODEN_LOG = VaultLogBlock.log(
      () -> ModBlocks.STRIPPED_OVERGROWN_WOODEN_LOG, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK
   );
   public static final Block STRIPPED_OVERGROWN_WOODEN_LOG = VaultLogBlock.stripped(MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block OVERGROWN_WOODEN_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final SlabBlock OVERGROWN_WOODEN_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_SLAB));
   public static final StairBlock OVERGROWN_WOODEN_STAIRS = new StairBlock(
      OVERGROWN_WOODEN_PLANKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)
   );
   public static final Block SANDY_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_SMALL_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_BRICKS_CRACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_SMALL_BRICKS_CRACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_BLOCK_POLISHED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_BLOCK_CHISELED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block SANDY_BLOCK_BUMBO = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SANDSTONE));
   public static final Block LIVING_ROCK_BLOCK_COBBLE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block LIVING_ROCK_BLOCK_POLISHED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block LIVING_ROCK_BLOCK_STACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block LIVING_ROCK_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block MOSSY_LIVING_ROCK_BLOCK_COBBLE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block MOSSY_LIVING_ROCK_BLOCK_POLISHED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block MOSSY_LIVING_ROCK_BLOCK_STACKED = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final Block MOSSY_LIVING_ROCK_BRICKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.TUFF));
   public static final RotatedPillarBlock MOSSY_BONE_BLOCK = new RotatedPillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)
   );
   public static final VaultSweetsBlock VAULT_SWEETS_BLOCK = new VaultSweetsBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CAKE)
   );
   public static final VaultSmallSweetsBlock VAULT_SMALL_SWEETS_BLOCK = new VaultSmallSweetsBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CAKE)
   );
   public static final VaultTinySweetsBlock VAULT_SWEETS = new VaultTinySweetsBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CAKE)
   );
   public static final GildedCandelabraBlock GILDED_CANDELABRA = new GildedCandelabraBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.METAL)
         .strength(1.0F)
         .explosionResistance(2.0F)
         .lightLevel(state -> state.getValue(GildedCandelabraBlock.LIT) ? 15 : 0)
   );
   public static final Block CHROMATIC_LOG = VaultLogBlock.log(() -> ModBlocks.STRIPPED_CHROMATIC_LOG, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block STRIPPED_CHROMATIC_LOG = VaultLogBlock.stripped(MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block CHROMATIC_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final SlabBlock CHROMATIC_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_SLAB));
   public static final StairBlock CHROMATIC_STAIRS = new StairBlock(
      CHROMATIC_PLANKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)
   );
   public static final Block DRIFTWOOD_LOG = VaultLogBlock.log(() -> ModBlocks.STRIPPED_DRIFTWOOD_LOG, MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block STRIPPED_DRIFTWOOD_LOG = VaultLogBlock.stripped(MaterialColor.COLOR_PINK, MaterialColor.COLOR_PINK);
   public static final Block DRIFTWOOD_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final SlabBlock DRIFTWOOD_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_SLAB));
   public static final StairBlock DRIFTWOOD_STAIRS = new StairBlock(
      DRIFTWOOD_PLANKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)
   );
   public static final SlabBlock VAULT_STONE_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_SLAB));
   public static final StairBlock VAULT_STONE_STAIRS = new StairBlock(
      VAULT_STONE::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS)
   );
   public static final Block IDONA_BRICK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS));
   public static final Block IDONA_CHISELED_BRICK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS));
   public static final Block IDONA_DARK_SMOOTH_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS));
   public static final Block IDONA_LIGHT_SMOOTH_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS));
   public static final Block IDONA_GEM_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS));
   public static final Block TENOS_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.STONE)
   );
   public static final Block TENOS_CHISELED_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.STONE)
   );
   public static final Block TENOS_DARK_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.STONE)
   );
   public static final Block TENOS_LIGHT_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.STONE)
   );
   public static final Block TENOS_GEM_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.STONE)
   );
   public static final Block TENOS_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final Block TENOS_VERTICAL_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final Block TENOS_LOG = VaultLogBlock.log(() -> ModBlocks.VELARA_STRIPPED_LOG, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN);
   public static final Block TENOS_STRIPPED_LOG = new RotatedPillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
   );
   public static final Block TENOS_BOOKSHELF = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BOOKSHELF));
   public static final Block TENOS_BOOKSHELF_EMPTY = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.BOOKSHELF));
   public static final Block VELARA_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.AZALEA)
   );
   public static final Block VELARA_CHISELED_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.AZALEA)
   );
   public static final Block VELARA_DARK_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.AZALEA)
   );
   public static final Block VELARA_LIGHT_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.AZALEA)
   );
   public static final Block VELARA_GEM_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).sound(SoundType.AZALEA)
   );
   public static final Block VELARA_LOG = VaultLogBlock.log(() -> ModBlocks.VELARA_STRIPPED_LOG, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN);
   public static final Block VELARA_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final Block VELARA_VERTICAL_PLANKS = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final Block VELARA_PLANKS_STAIRS = new StairBlock(
      VELARA_PLANKS::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
   );
   public static final Block VELARA_PLANKS_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
   public static final Block VELARA_STRIPPED_LOG = new RotatedPillarBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
   );
   public static final Block VELARA_MOSSY_LOG = VaultLogBlock.log(() -> VELARA_STRIPPED_LOG, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN);
   public static final Block VELARA_MOSSY_LOG_BLOOMING = VaultLogBlock.log(() -> VELARA_STRIPPED_LOG, MaterialColor.COLOR_GREEN, MaterialColor.COLOR_GREEN);
   public static final Block VELARA_MOSSY_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK));
   public static final Block VELARA_LEAVES = new LeavesBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES));
   public static final Block VELARA_BUSH = new LeavesBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES));
   public static final Block VELARA_VINE = new VineBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.VINE));
   public static final Block WENDARR_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE).sound(SoundType.SAND)
   );
   public static final Block WENDARR_CHISELED_BRICK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE).sound(SoundType.SAND)
   );
   public static final Block WENDARR_DARK_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE).sound(SoundType.SAND)
   );
   public static final Block WENDARR_LIGHT_SMOOTH_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE).sound(SoundType.SAND)
   );
   public static final Block WENDARR_GEM_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHISELED_SANDSTONE).sound(SoundType.SAND)
   );
   public static final Block WENDARR_JEWEL_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS));
   public static final Block WENDARR_JEWEL_GLASS = new GlassBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS)
   );
   public static final Block WENDARR_JEWEL_GLASS_PANE = new StainedGlassPaneBlock(
      DyeColor.YELLOW, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS_PANE)
   );
   public static final Block COMPRESSED_SOOT_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK));
   public static final Block VAULT_ASH = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SAND).sound(SoundType.SAND));
   public static final Block DRY_VAULT_ASH = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.SAND).sound(SoundType.SAND));
   public static final Block FOOLS_GOLD_BLOCK = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).noOcclusion()
   );
   public static final AscensionForgeBlock ASCENSION_FORGE = new AscensionForgeBlock();
   public static final OfferingPillarBlock OFFERING_PILLAR = new OfferingPillarBlock();
   public static final DoorBlock BARRED_DOOR = new BarredDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_DOOR).strength(500.0F).noOcclusion()
   );
   public static final BarredTrapDoorBlock BARRED_TRAPDOOR = new BarredTrapDoorBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_TRAPDOOR).strength(-1.0F, 360000.0F).noDrops().noOcclusion()
   );
   public static final VaultBars VAULT_BARS = new VaultBars(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.IRON_BARS).strength(-1.0F, 360000.0F).noOcclusion()
   );
   public static final TotemPlayerHealthBlock TOTEM_PLAYER_HEALTH = new TotemPlayerHealthBlock();
   public static final TotemMobDamageBlock TOTEM_MOB_DAMAGE = new TotemMobDamageBlock();
   public static final TotemManaRegenBlock TOTEM_MANA_REGEN = new TotemManaRegenBlock();
   public static final TotemPlayerDamageBlock TOTEM_PLAYER_DAMAGE = new TotemPlayerDamageBlock();
   public static final LiquidBlock VOID_LIQUID_BLOCK = new VoidFluidBlock(
      ModFluids.VOID_LIQUID,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.WATER, MaterialColor.COLOR_BLACK)
         .noCollission()
         .randomTicks()
         .strength(100.0F)
         .noDrops()
   );
   public static final HeraldTrophyItem HERALD_TROPHY_BLOCK_ITEM = new HeraldTrophyItem(HERALD_TROPHY);
   public static final SoulPlaqueBlockItem SOUL_PLAQUE_BLOCK_ITEM = new SoulPlaqueBlockItem(SOUL_PLAQUE);
   public static final FinalVaultFrameBlockItem FINAL_VAULT_FRAME_BLOCK_ITEM = new FinalVaultFrameBlockItem(FINAL_VAULT_FRAME);
   public static final LootStatueBlockItem LOOT_STATUE_ITEM = new LootStatueBlockItem(LOOT_STATUE);
   public static final TrophyStatueBlockItem TROPHY_STATUE_BLOCK_ITEM = new TrophyStatueBlockItem(TROPHY_STATUE);
   public static final EasterEggBlockItem EASTER_EGG_BLOCK_ITEM = new EasterEggBlockItem(EASTER_EGG);
   public static final BlockItem WOODEN_CHEST_ITEM = new VaultChestBlockItem(WOODEN_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_CHEST_ITEM = new VaultChestBlockItem(GILDED_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_CHEST_ITEM = new VaultChestBlockItem(LIVING_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_CHEST_ITEM = new VaultChestBlockItem(ORNATE_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem TREASURE_CHEST_ITEM = new VaultChestBlockItem(TREASURE_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ALTAR_CHEST_ITEM = new VaultChestBlockItem(ALTAR_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_STRONGBOX_ITEM = new VaultChestBlockItem(ORNATE_STRONGBOX, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_STRONGBOX_ITEM = new VaultChestBlockItem(GILDED_STRONGBOX, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_STRONGBOX_ITEM = new VaultChestBlockItem(LIVING_STRONGBOX, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_BARREL_ITEM = new VaultChestBlockItem(ORNATE_BARREL, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_BARREL_ITEM = new VaultChestBlockItem(GILDED_BARREL, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_BARREL_ITEM = new VaultChestBlockItem(LIVING_BARREL, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem WOODEN_BARREL_ITEM = new VaultChestBlockItem(WOODEN_BARREL, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem HARDENED_CHEST_ITEM = new VaultChestBlockItem(HARDENED_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ENIGMA_CHEST_ITEM = new VaultChestBlockItem(ENIGMA_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem FLESH_CHEST_ITEM = new VaultChestBlockItem(FLESH_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem WOODEN_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(WOODEN_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(GILDED_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(LIVING_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(ORNATE_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem TREASURE_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(
      TREASURE_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP)
   );
   public static final BlockItem ALTAR_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(ALTAR_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem HARDENED_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(
      HARDENED_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP)
   );
   public static final BlockItem ENIGMA_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(ENIGMA_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem FLESH_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(FLESH_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem SCAVENGER_ALTAR_ITEM = new BlockItem(SCAVENGER_ALTAR, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_BRONZE = new CoinBlockItem(BRONZE_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_SILVER = new CoinBlockItem(SILVER_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_GOLD = new CoinBlockItem(GOLD_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_PLATINUM = new CoinBlockItem(PLATINUM_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem ANGEL_BLOCK_ITEM = new BlockItem(ANGEL_BLOCK, new Properties().tab(ModItems.VAULT_MOD_GROUP)) {
      public void initializeClient(Consumer<IItemRenderProperties> consumer) {
         consumer.accept(AngelBlockISTER.INSTANCE);
      }
   };
   public static BlockItem ANCIENT_COPPER_CONDUIT_BLOCK_ITEM = new BlockItem(ANCIENT_COPPER_CONDUIT_BLOCK, new Properties().tab(ModItems.VAULT_MOD_GROUP)) {
      public void initializeClient(Consumer<IItemRenderProperties> consumer) {
         consumer.accept(AncientCopperConduitBlockISTER.INSTANCE);
      }
   };
   public static BlockItem VAULT_SWEETS_ITEM = new VaultSweetsBlockItem(VAULT_SWEETS, new Builder().fast().nutrition(3).saturationMod(0.5F).build());
   public static final BlockEntityType<VaultOreTileEntity> VAULT_ORE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultOreTileEntity::new,
         new Block[]{
            ALEXANDRITE_ORE,
            ASHIUM_ORE,
            BENITOITE_ORE,
            BLACK_OPAL_ORE,
            BOMIGNITE_ORE,
            ECHO_ORE,
            GORGINITE_ORE,
            ISKALLIUM_ORE,
            LARIMAR_ORE,
            PAINITE_ORE,
            PETZANITE_ORE,
            PUFFIUM_ORE,
            SPARKLETINE_ORE,
            TUBIUM_ORE,
            UPALINE_ORE,
            WUTODIE_ORE,
            XENIUM_ORE
         }
      )
      .build(null);
   public static final BlockEntityType<FloatingTextTileEntity> FLOATING_TEXT_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         FloatingTextTileEntity::new, new Block[]{FLOATING_TEXT}
      )
      .build(null);
   public static final BlockEntityType<VaultAltarTileEntity> VAULT_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultAltarTileEntity::new, new Block[]{VAULT_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<HeraldTrophyTileEntity> HERALD_TROPHY_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         HeraldTrophyTileEntity::new, new Block[]{HERALD_TROPHY}
      )
      .build(null);
   public static final BlockEntityType<FinalVaultFrameTileEntity> FINAL_VAULT_FRAME_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         FinalVaultFrameTileEntity::new, new Block[]{FINAL_VAULT_FRAME}
      )
      .build(null);
   public static final BlockEntityType<VaultCrateTileEntity> VAULT_CRATE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultCrateTileEntity::new,
         new Block[]{
            VAULT_CRATE,
            VAULT_CRATE_CAKE,
            VAULT_CRATE_ARENA,
            VAULT_CRATE_SCAVENGER,
            VAULT_CRATE_CHAMPION,
            VAULT_CRATE_BOUNTY,
            VAULT_CRATE,
            VAULT_CRATE_MONOLITH,
            VAULT_CRATE_ELIXIR,
            VAULT_CRATE_PARADOX
         }
      )
      .build(null);
   public static final BlockEntityType<VaultPortalTileEntity> VAULT_PORTAL_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultPortalTileEntity::new, new Block[]{VAULT_PORTAL}
      )
      .build(null);
   public static final BlockEntityType<RelicPedestalTileEntity> RELIC_STATUE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         RelicPedestalTileEntity::new, new Block[]{RELIC_PEDESTAL}
      )
      .build(null);
   public static final BlockEntityType<LootStatueTileEntity> LOOT_STATUE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         LootStatueTileEntity::new, new Block[]{LOOT_STATUE}
      )
      .build(null);
   public static final BlockEntityType<ShopPedestalBlockTile> SHOP_PEDESTAL_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ShopPedestalBlockTile::new, new Block[]{SHOP_PEDESTAL}
      )
      .build(null);
   public static final BlockEntityType<TreasurePedestalTileEntity> TREASURE_PEDESTAL_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TreasurePedestalTileEntity::new, new Block[]{TREASURE_PEDESTAL}
      )
      .build(null);
   public static final BlockEntityType<TrophyTileEntity> TROPHY_STATUE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TrophyTileEntity::new, new Block[]{TROPHY_STATUE}
      )
      .build(null);
   public static final BlockEntityType<CryoChamberTileEntity> CRYO_CHAMBER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .build(null);
   public static final BlockEntityType<AncientCryoChamberTileEntity> ANCIENT_CRYO_CHAMBER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AncientCryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .build(null);
   public static final BlockEntityType<TreasureDoorTileEntity> TREASURE_DOOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TreasureDoorTileEntity::new, new Block[]{TREASURE_DOOR}
      )
      .build(null);
   public static final BlockEntityType<DungeonDoorTileEntity> DUNGEON_DOOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         DungeonDoorTileEntity::new, new Block[]{DUNGEON_DOOR}
      )
      .build(null);
   public static final BlockEntityType<VendorDoorTileEntity> VENDOR_DOOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VendorDoorTileEntity::new, new Block[]{VENDOR_DOOR}
      )
      .build(null);
   public static final BlockEntityType<GateLockTileEntity> GATE_LOCK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         GateLockTileEntity::new, new Block[]{GATE_LOCK}
      )
      .build(null);
   public static final BlockEntityType<HologramTileEntity> HOLOGRAM_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         HologramTileEntity::new, new Block[]{HOLOGRAM}
      )
      .build(null);
   public static final BlockEntityType<SoulPlaqueTileEntity> SOUL_PLAQUE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         SoulPlaqueTileEntity::new, new Block[]{SOUL_PLAQUE}
      )
      .build(null);
   public static final BlockEntityType<HeraldControllerTileEntity> HERALD_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         HeraldControllerTileEntity::new, new Block[]{HERALD_CONTROLLER}
      )
      .build(null);
   public static final BlockEntityType<RaidControllerBlockEntity> RAID_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         RaidControllerBlockEntity::new, new Block[]{RAID_CONTROLLER}
      )
      .build(null);
   public static final BlockEntityType<XMarkControllerBlockEntity> X_MARK_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         XMarkControllerBlockEntity::new, new Block[]{X_MARK_CONTROLLER}
      )
      .build(null);
   public static final BlockEntityType<EliteControllerBlockEntity> ELITE_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         EliteControllerBlockEntity::new, new Block[]{ELITE_CONTROLLER}
      )
      .build(null);
   public static final BlockEntityType<RaidControllerProxyBlockEntity> RAID_CONTROLLER_PROXY_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         RaidControllerProxyBlockEntity::new, new Block[]{RAID_CONTROLLER_PROXY}
      )
      .build(null);
   public static final BlockEntityType<EliteControllerProxyBlockEntity> ELITE_CONTROLLER_PROXY_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         EliteControllerProxyBlockEntity::new, new Block[]{ELITE_CONTROLLER_PROXY}
      )
      .build(null);
   public static final BlockEntityType<VaultChestTileEntity> VAULT_CHEST_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultChestTileEntity::new,
         new Block[]{
            WOODEN_CHEST,
            ALTAR_CHEST,
            GILDED_CHEST,
            LIVING_CHEST,
            ORNATE_CHEST,
            TREASURE_CHEST,
            HARDENED_CHEST,
            ENIGMA_CHEST,
            FLESH_CHEST,
            WOODEN_CHEST_PLACEABLE,
            ALTAR_CHEST_PLACEABLE,
            GILDED_CHEST_PLACEABLE,
            LIVING_CHEST_PLACEABLE,
            HARDENED_CHEST_PLACEABLE,
            ENIGMA_CHEST_PLACEABLE,
            FLESH_CHEST_PLACEABLE,
            ORNATE_CHEST_PLACEABLE,
            TREASURE_CHEST_PLACEABLE,
            ORNATE_STRONGBOX,
            GILDED_STRONGBOX,
            LIVING_STRONGBOX,
            ORNATE_BARREL,
            GILDED_BARREL,
            LIVING_BARREL,
            WOODEN_BARREL
         }
      )
      .build(null);
   public static final BlockEntityType<GodAltarTileEntity> GOD_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         GodAltarTileEntity::new, new Block[]{GOD_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<ObeliskTileEntity> OBELISK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ObeliskTileEntity::new, new Block[]{OBELISK}
      )
      .build(null);
   public static final BlockEntityType<MonolithTileEntity> MONOLITH_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         MonolithTileEntity::new, new Block[]{MONOLITH}
      )
      .build(null);
   public static final BlockEntityType<CrakePedestalTileEntity> CRAKE_PEDESTAL_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CrakePedestalTileEntity::new, new Block[]{CRAKE_PEDESTAL}
      )
      .build(null);
   public static final BlockEntityType<LodestoneTileEntity> LODESTONE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         LodestoneTileEntity::new, new Block[]{LODESTONE}
      )
      .build(null);
   public static final BlockEntityType<PylonTileEntity> PYLON_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         PylonTileEntity::new, new Block[]{PYLON}
      )
      .build(null);
   public static final BlockEntityType<VaultEnhancementAltarTileEntity> ENHANCEMENT_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultEnhancementAltarTileEntity::new, new Block[]{ENHANCEMENT_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<HourglassTileEntity> HOURGLASS_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         HourglassTileEntity::new, new Block[]{HOURGLASS}
      )
      .build(null);
   public static final BlockEntityType<ScavengerAltarTileEntity> SCAVENGER_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ScavengerAltarTileEntity::new, new Block[]{SCAVENGER_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<DivineAltarTileEntity> DIVINE_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         DivineAltarTileEntity::new, new Block[]{DIVINE_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<ScavengerTreasureTileEntity> SCAVENGER_TREASURE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ScavengerTreasureTileEntity::new, new Block[]{SCAVENGER_TREASURE}
      )
      .build(null);
   public static final BlockEntityType<StabilizerTileEntity> STABILIZER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         StabilizerTileEntity::new, new Block[]{STABILIZER}
      )
      .build(null);
   public static final BlockEntityType<CatalystInfusionTableTileEntity> CATALYST_INFUSION_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CatalystInfusionTableTileEntity::new, new Block[]{CATALYST_INFUSION_TABLE}
      )
      .build(null);
   public static final BlockEntityType<EtchingVendorControllerTileEntity> ETCHING_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         EtchingVendorControllerTileEntity::new, new Block[]{ETCHING_CONTROLLER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<VaultCharmControllerTileEntity> VAULT_CHARM_CONTROLLER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultCharmControllerTileEntity::new, new Block[]{VAULT_CHARM_CONTROLLER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<ToolViseTile> TOOL_VISE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ToolViseTile::new, new Block[]{TOOL_VISE}
      )
      .build(null);
   public static final BlockEntityType<MagnetTableTile> MAGNET_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         MagnetTableTile::new, new Block[]{MAGNET_TABLE}
      )
      .build(null);
   public static final BlockEntityType<DemagnetizerTileEntity> DEMAGNETIZER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         DemagnetizerTileEntity::new, new Block[]{DEMAGNETIZER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<DebagnetizerTileEntity> DEBAGNETIZER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         DebagnetizerTileEntity::new, new Block[]{DEBAGNETIZER}
      )
      .build(null);
   public static final BlockEntityType<CoinPilesTileEntity> COIN_PILE_TILE = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CoinPilesTileEntity::new, new Block[]{COIN_PILE}
      )
      .build(null);
   public static final BlockEntityType<TreasureSandTileEntity> TREASURE_SAND_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TreasureSandTileEntity::new, new Block[]{TREASURE_SAND}
      )
      .build(null);
   public static final BlockEntityType<VaultForgeTileEntity> VAULT_FORGE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultForgeTileEntity::new, new Block[]{VAULT_FORGE}
      )
      .build(null);
   public static final BlockEntityType<ToolStationTileEntity> TOOL_STATION_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ToolStationTileEntity::new, new Block[]{TOOL_STATION}
      )
      .build(null);
   public static final BlockEntityType<InscriptionTableTileEntity> INSCRIPTION_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         InscriptionTableTileEntity::new, new Block[]{INSCRIPTION_TABLE}
      )
      .build(null);
   public static final BlockEntityType<VaultArtisanStationTileEntity> VAULT_ARTISAN_STATION_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultArtisanStationTileEntity::new, new Block[]{VAULT_ARTISAN_STATION}
      )
      .build(null);
   public static final BlockEntityType<VaultJewelCuttingStationTileEntity> VAULT_JEWEL_CUTTING_STATION_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultJewelCuttingStationTileEntity::new, new Block[]{VAULT_JEWEL_CUTTING_STATION}
      )
      .build(null);
   public static final BlockEntityType<VaultJewelApplicationStationTileEntity> VAULT_JEWEL_APPLICATION_STATION_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultJewelApplicationStationTileEntity::new, new Block[]{VAULT_JEWEL_APPLICATION_STATION}
      )
      .build(null);
   public static final BlockEntityType<JewelCraftingTableTileEntity> JEWEL_CRAFTING_TABLE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         JewelCraftingTableTileEntity::new, new Block[]{JEWEL_CRAFTING_TABLE}
      )
      .build(null);
   public static final BlockEntityType<CrystalWorkbenchTileEntity> CRYSTAL_WORKBENCH_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CrystalWorkbenchTileEntity::new, new Block[]{CRYSTAL_WORKBENCH}
      )
      .build(null);
   public static final BlockEntityType<VaultRecyclerTileEntity> VAULT_RECYCLER_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultRecyclerTileEntity::new, new Block[]{VAULT_RECYCLER}
      )
      .build(null);
   public static final BlockEntityType<VaultDiffuserTileEntity> VAULT_DIFFUSER_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultDiffuserTileEntity::new, new Block[]{VAULT_DIFFUSER}
      )
      .build(null);
   public static final BlockEntityType<VaultDiffuserUpgradedTileEntity> VAULT_HARVESTER_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultDiffuserUpgradedTileEntity::new, new Block[]{VAULT_HARVESTER}
      )
      .build(null);
   public static final BlockEntityType<ModifierWorkbenchTileEntity> MODIFIER_WORKBENCH_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ModifierWorkbenchTileEntity::new, new Block[]{MODIFIER_WORKBENCH}
      )
      .build(null);
   public static final BlockEntityType<AlchemyTableTileEntity> ALCHEMY_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AlchemyTableTileEntity::new, new Block[]{ALCHEMY_TABLE}
      )
      .build(null);
   public static final BlockEntityType<VaultEnchanterTileEntity> VAULT_ENCHANTER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VaultEnchanterTileEntity::new, new Block[]{VAULT_ENCHANTER}
      )
      .build(null);
   public static final BlockEntityType<IdentificationStandTileEntity> IDENTIFICATION_STAND_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         IdentificationStandTileEntity::new, new Block[]{IDENTIFICATION_STAND}
      )
      .build(null);
   public static final BlockEntityType<CardEssenceExtractorTileEntity> CARD_ESSENCE_EXTRACTOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CardEssenceExtractorTileEntity::new, new Block[]{CARD_ESSENCE_EXTRACTOR}
      )
      .build(null);
   public static final BlockEntityType<ModifierDiscoveryTileEntity> MODIFIER_DISCOVERY_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ModifierDiscoveryTileEntity::new, new Block[]{MODIFIER_DISCOVERY}
      )
      .build(null);
   public static final BlockEntityType<AlchemyArchiveTileEntity> ALCHEMY_ARCHIVE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AlchemyArchiveTileEntity::new, new Block[]{ALCHEMY_ARCHIVE}
      )
      .build(null);
   public static final BlockEntityType<AnimalPenTileEntity> ANIMAL_PEN_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AnimalPenTileEntity::new, new Block[]{ANIMAL_PEN}
      )
      .build(null);
   public static final BlockEntityType<EliteSpawnerTileEntity> ELITE_SPAWNER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         EliteSpawnerTileEntity::new, new Block[]{ELITE_SPAWNER}
      )
      .build(null);
   public static final BlockEntityType<WildSpawnerTileEntity> WILD_SPAWNER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         WildSpawnerTileEntity::new, new Block[]{WILD_SPAWNER}
      )
      .build(null);
   public static final BlockEntityType<CustomEntitySpawnerTileEntity> CUSTOM_ENTITY_SPAWNER_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CustomEntitySpawnerTileEntity::new, new Block[]{CUSTOM_ENTITY_SPAWNER}
      )
      .build(null);
   public static final BlockEntityType<CubeTileEntity> CUBE_BLOCK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CubeTileEntity::new, new Block[]{CUBE_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<SpiritExtractorTileEntity> SPIRIT_EXTRACTOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         SpiritExtractorTileEntity::new, new Block[]{SPIRIT_EXTRACTOR}
      )
      .build(null);
   public static final BlockEntityType<WardrobeTileEntity> WARDROBE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         WardrobeTileEntity::new, new Block[]{WARDROBE}
      )
      .build(null);
   public static final BlockEntityType<SkillAltarTileEntity> SKILL_ALTAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         SkillAltarTileEntity::new, new Block[]{SKILL_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<BountyTableTileEntity> BOUNTY_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         BountyTableTileEntity::new, new Block[]{BOUNTY_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<CrystalBuddingBlockEntity> CRYSTAL_BUDDING_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         CrystalBuddingBlockEntity::new, new Block[]{CRYSTAL_BUDDING}
      )
      .build(null);
   public static final BlockEntityType<BlackMarketTileEntity> BLACK_MARKET_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         BlackMarketTileEntity::new, new Block[]{BLACK_MARKET}
      )
      .build(null);
   public static final BlockEntityType<FoliageDecorTileEntity> FOLIAGE_DECOR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         FoliageDecorTileEntity::new, new Block[]{LIVING_ROCK_PLANTER}
      )
      .build(null);
   public static final BlockEntityType<TransmogTableTileEntity> TRANSMOG_TABLE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TransmogTableTileEntity::new, new Block[]{TRANSMOG_TABLE}
      )
      .build(null);
   public static final BlockEntityType<EternalPedestalTileEntity> ETERNAL_PEDESTAL_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         EternalPedestalTileEntity::new, new Block[]{ETERNAL_PEDESTAL}
      )
      .build(null);
   public static final BlockEntityType<TotemPlayerHealthTileEntity> TOTEM_PLAYER_HEALTH_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TotemPlayerHealthTileEntity::new, new Block[]{TOTEM_PLAYER_HEALTH}
      )
      .build(null);
   public static final BlockEntityType<TotemMobDamageTileEntity> TOTEM_MOB_DAMAGE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TotemMobDamageTileEntity::new, new Block[]{TOTEM_MOB_DAMAGE}
      )
      .build(null);
   public static final BlockEntityType<TotemManaRegenTileEntity> TOTEM_MANA_REGEN_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TotemManaRegenTileEntity::new, new Block[]{TOTEM_MANA_REGEN}
      )
      .build(null);
   public static final BlockEntityType<TotemPlayerDamageTileEntity> TOTEM_PLAYER_DAMAGE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TotemPlayerDamageTileEntity::new, new Block[]{TOTEM_PLAYER_DAMAGE}
      )
      .build(null);
   public static final BlockEntityType<AngelBlockTileEntity> ANGEL_BLOCK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AngelBlockTileEntity::new, new Block[]{ANGEL_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<AncientCopperConduitTileEntity> ANCIENT_COPPER_CONDUIT_BLOCK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AncientCopperConduitTileEntity::new, new Block[]{ANCIENT_COPPER_CONDUIT_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<MobBarrierTileEntity> MOB_BARRIER_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         MobBarrierTileEntity::new, new Block[]{MOB_BARRIER}
      )
      .build(null);
   public static final BlockEntityType<ArtifactProjectorTileEntity> ARTIFACT_PROJECTOR_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ArtifactProjectorTileEntity::new, new Block[]{ARTIFACT_PROJECTOR_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<VelvetBedTileEntity> VELVET_BED_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         VelvetBedTileEntity::new, new Block[]{VELVET_BED}
      )
      .build(null);
   public static final BlockEntityType<SparkTileEntity> SPARK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         SparkTileEntity::new, new Block[]{SPARK}
      )
      .build(null);
   public static final BlockEntityType<ConvertedSparkTileEntity> CONVERTED_SPARK_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         ConvertedSparkTileEntity::new, new Block[]{CONVERTED_SPARK}
      )
      .build(null);
   public static final BlockEntityType<AnimatrixTileEntity> ANIMATRIX_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AnimatrixTileEntity::new, new Block[]{ANIMATRIX_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<AscensionForgeTileEntity> ASCENSION_FORGE_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         AscensionForgeTileEntity::new, new Block[]{ASCENSION_FORGE}
      )
      .build(null);
   public static final BlockEntityType<OfferingPillarTileEntity> OFFERING_PILLAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         OfferingPillarTileEntity::new, new Block[]{OFFERING_PILLAR}
      )
      .build(null);
   public static final BlockEntityType<GridGatewayTileEntity> GRID_GATEWAY_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         GridGatewayTileEntity::new, new Block[]{GRID_GATEWAY}
      )
      .build(null);
   public static final BlockEntityType<TaskPillarTileEntity> TASK_PILLAR_TILE_ENTITY = net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
         TaskPillarTileEntity::new, new Block[]{TASK_PILLAR}
      )
      .build(null);

   public static void registerBlocks(Register<Block> event) {
      registerBlock(event, ERROR_BLOCK, VaultMod.id("error_block"));
      registerBlock(event, FLOATING_TEXT, VaultMod.id("floating_text"));
      registerBlock(event, VAULT_PORTAL, VaultMod.id("vault_portal"));
      registerBlock(event, HERALD_TROPHY, VaultMod.id("herald_trophy"));
      registerBlock(event, FINAL_VAULT_FRAME, VaultMod.id("final_vault_frame"));
      registerBlock(event, VAULT_ALTAR, VaultMod.id("vault_altar"));
      registerBlock(event, ALEXANDRITE_ORE, VaultMod.id("ore_alexandrite"));
      registerBlock(event, ASHIUM_ORE, VaultMod.id("ore_ashium"));
      registerBlock(event, BENITOITE_ORE, VaultMod.id("ore_benitoite"));
      registerBlock(event, BLACK_OPAL_ORE, VaultMod.id("ore_black_opal"));
      registerBlock(event, BOMIGNITE_ORE, VaultMod.id("ore_bomignite"));
      registerBlock(event, ECHO_ORE, VaultMod.id("ore_echo"));
      registerBlock(event, GORGINITE_ORE, VaultMod.id("ore_gorginite"));
      registerBlock(event, ISKALLIUM_ORE, VaultMod.id("ore_iskallium"));
      registerBlock(event, LARIMAR_ORE, VaultMod.id("ore_larimar"));
      registerBlock(event, PAINITE_ORE, VaultMod.id("ore_painite"));
      registerBlock(event, PETZANITE_ORE, VaultMod.id("ore_petzanite"));
      registerBlock(event, PUFFIUM_ORE, VaultMod.id("ore_puffium"));
      registerBlock(event, SPARKLETINE_ORE, VaultMod.id("ore_sparkletine"));
      registerBlock(event, TUBIUM_ORE, VaultMod.id("ore_tubium"));
      registerBlock(event, UPALINE_ORE, VaultMod.id("ore_upaline"));
      registerBlock(event, WUTODIE_ORE, VaultMod.id("ore_wutodie"));
      registerBlock(event, XENIUM_ORE, VaultMod.id("ore_xenium"));
      registerBlock(event, VAULT_ROCK_ORE, VaultMod.id("ore_vault_rock"));
      registerBlock(event, TREASURE_DOOR, VaultMod.id("treasure_door"));
      registerBlock(event, DUNGEON_DOOR, VaultMod.id("dungeon_door"));
      registerBlock(event, VENDOR_DOOR, VaultMod.id("vendor_door"));
      registerBlock(event, GATE_LOCK, VaultMod.id("gate_lock"));
      registerBlock(event, HOLOGRAM, VaultMod.id("hologram"));
      registerBlock(event, HERALD_CONTROLLER, VaultMod.id("herald_controller"));
      registerBlock(event, RAID_CONTROLLER, VaultMod.id("raid_controller"));
      registerBlock(event, X_MARK_CONTROLLER, VaultMod.id("x_mark_controller"));
      registerBlock(event, ELITE_CONTROLLER, VaultMod.id("elite_controller"));
      registerBlock(event, RAID_CONTROLLER_PROXY, VaultMod.id("raid_controller_proxy"));
      registerBlock(event, ELITE_CONTROLLER_PROXY, VaultMod.id("elite_controller_proxy"));
      registerBlock(event, VAULT_ARTIFACT, VaultMod.id("vault_artifact"));
      registerBlock(event, VAULT_CRATE, VaultMod.id("vault_crate"));
      registerBlock(event, VAULT_CRATE_CAKE, VaultMod.id("vault_crate_cake"));
      registerBlock(event, VAULT_CRATE_ARENA, VaultMod.id("vault_crate_arena"));
      registerBlock(event, VAULT_CRATE_SCAVENGER, VaultMod.id("vault_crate_scavenger"));
      registerBlock(event, VAULT_CRATE_CHAMPION, VaultMod.id("vault_crate_champion"));
      registerBlock(event, VAULT_CRATE_BOUNTY, VaultMod.id("vault_crate_bounty"));
      registerBlock(event, VAULT_CRATE_MONOLITH, VaultMod.id("vault_crate_monolith"));
      registerBlock(event, VAULT_CRATE_ELIXIR, VaultMod.id("vault_crate_elixir"));
      registerBlock(event, VAULT_CRATE_PARADOX, VaultMod.id("vault_crate_paradox"));
      registerBlock(event, VAULT_CRATE_BINGO, VaultMod.id("vault_crate_bingo"));
      registerBlock(event, VAULT_CRATE_BINGO_FULL, VaultMod.id("vault_crate_bingo_full"));
      registerBlock(event, VAULT_CRATE_OFFERING_BOSS, VaultMod.id("vault_crate_offering_boss"));
      registerBlock(event, OBELISK, VaultMod.id("obelisk"));
      registerBlock(event, MONOLITH, VaultMod.id("monolith"));
      registerBlock(event, LODESTONE, VaultMod.id("lodestone"));
      registerBlock(event, CRAKE_PEDESTAL, VaultMod.id("crake_pedestal"));
      registerBlock(event, CRAKE_COLUMN, VaultMod.id("crake_column"));
      registerBlock(event, GRID_GATEWAY, VaultMod.id("grid_gateway"));
      registerBlock(event, TASK_PILLAR, VaultMod.id("task_pillar"));
      registerBlock(event, TASK_BUILDER, VaultMod.id("task_builder"));
      registerBlock(event, PYLON, VaultMod.id("pylon"));
      registerBlock(event, ENHANCEMENT_ALTAR, VaultMod.id("enhancement_altar"));
      registerBlock(event, MVP_CROWN, VaultMod.id("mvp_crown"));
      registerBlock(event, EASTER_EGG, VaultMod.id("easter_egg"));
      registerBlock(event, VAULT_BEDROCK, VaultMod.id("vault_bedrock"));
      registerBlock(event, VAULT_STONE, VaultMod.id("vault_stone"));
      registerBlock(event, CHISELED_VAULT_STONE, VaultMod.id("chiseled_vault_stone"));
      registerBlock(event, POLISHED_VAULT_STONE, VaultMod.id("polished_vault_stone"));
      registerBlock(event, POLISHED_VAULT_STONE_SLAB, VaultMod.id("polished_vault_stone_slab"));
      registerBlock(event, POLISHED_VAULT_STONE_STAIRS, VaultMod.id("polished_vault_stone_stairs"));
      registerBlock(event, BUMBO_POLISHED_VAULT_STONE, VaultMod.id("bumbo_polished_vault_stone"));
      registerBlock(event, VAULT_STONE_BRICKS, VaultMod.id("vault_stone_bricks"));
      registerBlock(event, VAULT_STONE_BRICK_SLAB, VaultMod.id("vault_stone_brick_slab"));
      registerBlock(event, VAULT_STONE_BRICK_STAIRS, VaultMod.id("vault_stone_brick_stairs"));
      registerBlock(event, VAULT_STONE_BRICKS_CRACKED, VaultMod.id("vault_stone_bricks_cracked"));
      registerBlock(event, VAULT_STONE_PILLAR, VaultMod.id("vault_stone_pillar"));
      registerBlock(event, VAULT_COBBLESTONE, VaultMod.id("vault_cobblestone"));
      registerBlock(event, VAULT_GLASS, VaultMod.id("vault_glass"));
      registerBlock(event, RELIC_PEDESTAL, VaultMod.id("relic_pedestal"));
      registerBlock(event, LOOT_STATUE, VaultMod.id("loot_statue"));
      registerBlock(event, LOOT_STATUE_UPPER, VaultMod.id("loot_statue_upper"));
      registerBlock(event, SHOP_PEDESTAL, VaultMod.id("shop_pedestal"));
      registerBlock(event, TREASURE_PEDESTAL, VaultMod.id("treasure_pedestal"));
      registerBlock(event, CRYO_CHAMBER, VaultMod.id("cryo_chamber"));
      registerBlock(event, VAULT_DIAMOND_BLOCK, VaultMod.id("vault_diamond_block"));
      registerBlock(event, MAZE_BLOCK, VaultMod.id("maze_block"));
      registerBlock(event, TROPHY_STATUE, VaultMod.id("trophy_statue"));
      registerBlock(event, TRANSMOG_TABLE, VaultMod.id("transmog_table"));
      registerBlock(event, VAULT_FORGE, VaultMod.id("vault_forge"));
      registerBlock(event, TOOL_STATION, VaultMod.id("tool_station"));
      registerBlock(event, INSCRIPTION_TABLE, VaultMod.id("inscription_table"));
      registerBlock(event, VAULT_ARTISAN_STATION, VaultMod.id("vault_artisan_station"));
      registerBlock(event, VAULT_JEWEL_CUTTING_STATION, VaultMod.id("vault_jewel_cutting_station"));
      registerBlock(event, VAULT_JEWEL_APPLICATION_STATION, VaultMod.id("vault_jewel_application_station"));
      registerBlock(event, JEWEL_CRAFTING_TABLE, VaultMod.id("jewel_crafting_table"));
      registerBlock(event, CRYSTAL_WORKBENCH, VaultMod.id("crystal_workbench"));
      registerBlock(event, VAULT_RECYCLER, VaultMod.id("vault_recycler"));
      registerBlock(event, VAULT_DIFFUSER, VaultMod.id("vault_diffuser"));
      registerBlock(event, VAULT_HARVESTER, VaultMod.id("vault_harvester"));
      registerBlock(event, MODIFIER_WORKBENCH, VaultMod.id("modifier_workbench"));
      registerBlock(event, MODIFIER_DISCOVERY, VaultMod.id("modifier_discovery"));
      registerBlock(event, ALCHEMY_ARCHIVE, VaultMod.id("alchemy_archive"));
      registerBlock(event, ALCHEMY_TABLE, VaultMod.id("alchemy_table"));
      registerBlock(event, VAULT_ENCHANTER, VaultMod.id("vault_enchanter"));
      registerBlock(event, IDENTIFICATION_STAND, VaultMod.id("identification_stand"));
      registerBlock(event, CARD_ESSENCE_EXTRACTOR, VaultMod.id("card_essence_extractor"));
      registerBlock(event, WOODEN_CHEST, VaultMod.id("wooden_chest"));
      registerBlock(event, GILDED_CHEST, VaultMod.id("gilded_chest"));
      registerBlock(event, LIVING_CHEST, VaultMod.id("living_chest"));
      registerBlock(event, ORNATE_CHEST, VaultMod.id("ornate_chest"));
      registerBlock(event, TREASURE_CHEST, VaultMod.id("treasure_chest"));
      registerBlock(event, ALTAR_CHEST, VaultMod.id("altar_chest"));
      registerBlock(event, ORNATE_STRONGBOX, VaultMod.id("ornate_strongbox"));
      registerBlock(event, GILDED_STRONGBOX, VaultMod.id("gilded_strongbox"));
      registerBlock(event, LIVING_STRONGBOX, VaultMod.id("living_strongbox"));
      registerBlock(event, ORNATE_BARREL, VaultMod.id("ornate_barrel"));
      registerBlock(event, GILDED_BARREL, VaultMod.id("gilded_barrel"));
      registerBlock(event, LIVING_BARREL, VaultMod.id("living_barrel"));
      registerBlock(event, WOODEN_BARREL, VaultMod.id("wooden_barrel"));
      registerBlock(event, HARDENED_CHEST, VaultMod.id("hardened_chest"));
      registerBlock(event, ENIGMA_CHEST, VaultMod.id("enigma_chest"));
      registerBlock(event, FLESH_CHEST, VaultMod.id("flesh_chest"));
      registerBlock(event, WOODEN_CHEST_PLACEABLE, VaultMod.id("wooden_chest_placeable"));
      registerBlock(event, GILDED_CHEST_PLACEABLE, VaultMod.id("gilded_chest_placeable"));
      registerBlock(event, LIVING_CHEST_PLACEABLE, VaultMod.id("living_chest_placeable"));
      registerBlock(event, ORNATE_CHEST_PLACEABLE, VaultMod.id("ornate_chest_placeable"));
      registerBlock(event, TREASURE_CHEST_PLACEABLE, VaultMod.id("treasure_chest_placeable"));
      registerBlock(event, ALTAR_CHEST_PLACEABLE, VaultMod.id("altar_chest_placeable"));
      registerBlock(event, HARDENED_CHEST_PLACEABLE, VaultMod.id("hardened_chest_placeable"));
      registerBlock(event, ENIGMA_CHEST_PLACEABLE, VaultMod.id("enigma_chest_placeable"));
      registerBlock(event, FLESH_CHEST_PLACEABLE, VaultMod.id("flesh_chest_placeable"));
      registerBlock(event, GOD_ALTAR, VaultMod.id("god_altar"));
      registerBlock(event, VOID_LIQUID_BLOCK, VaultMod.id("void_liquid"));
      registerBlock(event, HOURGLASS, VaultMod.id("hourglass"));
      registerBlock(event, SCAVENGER_ALTAR, VaultMod.id("scavenger_altar"));
      registerBlock(event, DIVINE_ALTAR, VaultMod.id("divine_altar"));
      registerBlock(event, SCAVENGER_TREASURE, VaultMod.id("scavenger_treasure"));
      registerBlock(event, STABILIZER, VaultMod.id("stabilizer"));
      registerBlock(event, CATALYST_INFUSION_TABLE, VaultMod.id("catalyst_infusion_table"));
      registerBlock(event, ETCHING_CONTROLLER_BLOCK, VaultMod.id("etching_vendor_controller"));
      registerBlock(event, VAULT_CHARM_CONTROLLER_BLOCK, VaultMod.id("vault_charm_controller"));
      registerBlock(event, PLACEHOLDER, VaultMod.id("placeholder"));
      registerBlock(event, COIN_PILE, VaultMod.id("coin_pile"));
      registerBlock(event, BRONZE_COIN_PILE, VaultMod.id("vault_bronze"));
      registerBlock(event, SILVER_COIN_PILE, VaultMod.id("vault_silver"));
      registerBlock(event, GOLD_COIN_PILE, VaultMod.id("vault_gold"));
      registerBlock(event, PLATINUM_COIN_PILE, VaultMod.id("vault_platinum"));
      registerBlock(event, TOOL_VISE, VaultMod.id("tool_vise"));
      registerBlock(event, MAGNET_TABLE, VaultMod.id("magnet_modification_table"));
      registerBlock(event, DEMAGNETIZER_BLOCK, VaultMod.id("demagnetizer"));
      registerBlock(event, DEBAGNETIZER, VaultMod.id("debagnetizer"));
      registerBlock(event, CHROMATIC_IRON_ORE, VaultMod.id("chromatic_iron_ore"));
      registerBlock(event, RAW_CHROMATIC_IRON_BLOCK, VaultMod.id("raw_chromatic_iron_block"));
      registerBlock(event, CHROMATIC_IRON_BLOCK, VaultMod.id("chromatic_iron_block"));
      registerBlock(event, CHROMATIC_STEEL_BLOCK, VaultMod.id("chromatic_steel_block"));
      registerBlock(event, BLACK_CHROMATIC_STEEL_BLOCK, VaultMod.id("black_chromatic_steel_block"));
      registerBlock(event, VAULT_MEAT_BLOCK, VaultMod.id("vault_meat_block"));
      registerBlock(event, PACKED_VAULT_MEAT_BLOCK, VaultMod.id("packed_vault_meat_block"));
      registerBlock(event, TREASURE_SAND, VaultMod.id("treasure_sand"));
      registerBlock(event, ELITE_SPAWNER, VaultMod.id("elite_spawner"));
      registerBlock(event, WILD_SPAWNER, VaultMod.id("wild_spawner"));
      registerBlock(event, CUSTOM_ENTITY_SPAWNER, VaultMod.id("custom_entity_spawner"));
      registerBlock(event, SPIRIT_EXTRACTOR, VaultMod.id("spirit_extractor"));
      registerBlock(event, WARDROBE, VaultMod.id("wardrobe"));
      registerBlock(event, SKILL_ALTAR, VaultMod.id("skill_altar"));
      registerBlock(event, CAKE, VaultMod.id("cake"));
      registerBlock(event, MAGIC_SILK_BLOCK, VaultMod.id("magic_silk_block"));
      registerBlock(event, YELLOW_PUZZLE_CONCRETE, VaultMod.id("yellow_puzzle_concrete"));
      registerBlock(event, PINK_PUZZLE_CONCRETE, VaultMod.id("pink_puzzle_concrete"));
      registerBlock(event, GREEN_PUZZLE_CONCRETE, VaultMod.id("green_puzzle_concrete"));
      registerBlock(event, BLUE_PUZZLE_CONCRETE, VaultMod.id("blue_puzzle_concrete"));
      registerBlock(event, SOUL_PLAQUE, VaultMod.id("soul_plaque"));
      registerBlock(event, SUGAR_PLUM_FAIRY_FLOWER, VaultMod.id("sugar_plum_fairy_flower"));
      registerBlock(event, GOLDEN_TOOTH, VaultMod.id("golden_tooth"));
      registerBlock(event, CUBE_BLOCK, VaultMod.id("cube_block"));
      registerBlock(event, BOUNTY_BLOCK, VaultMod.id("bounty_block"));
      registerBlock(event, BLACK_MARKET, VaultMod.id("black_market"));
      registerBlock(event, ANIMAL_PEN, VaultMod.id("animal_pen"));
      registerBlock(event, ANIMAL_JAR, VaultMod.id("animal_jar"));
      registerBlock(event, CRYSTAL_BUDDING, VaultMod.id("crystal_budding"));
      registerBlock(event, CRYSTAL_CLUSTER, VaultMod.id("crystal_cluster"));
      registerBlock(event, LARGE_CRYSTAL_BUD, VaultMod.id("crystal_bud_large"));
      registerBlock(event, MEDIUM_CRYSTAL_BUD, VaultMod.id("crystal_bud_medium"));
      registerBlock(event, SMALL_CRYSTAL_BUD, VaultMod.id("crystal_bud_small"));
      registerBlock(event, VAULT_ANVIL, VaultMod.id("vault_anvil"));
      registerBlock(event, WUTODIC_SILVER_BLOCK, VaultMod.id("wutodic_silver_block"));
      registerBlock(event, ETERNAL_PEDESTAL, VaultMod.id("eternal_pedestal"));
      registerBlock(event, VAULT_STONE_SLAB, VaultMod.id("vault_stone_slab"));
      registerBlock(event, VAULT_STONE_STAIRS, VaultMod.id("vault_stone_stairs"));
      registerBlock(event, ANGEL_BLOCK, VaultMod.id("angel_block"));
      registerBlock(event, ANCIENT_COPPER_CONDUIT_BLOCK, VaultMod.id("ancient_copper_conduit_block"));
      registerBlock(event, MOB_BARRIER, VaultMod.id("mob_barrier"));
      registerBlock(event, SPARK, VaultMod.id("spark"));
      registerBlock(event, CONVERTED_SPARK, VaultMod.id("converted_spark"));
      registerBlock(event, ARTIFACT_PROJECTOR_BLOCK, VaultMod.id("artifact_projector_block"));
      registerBlock(event, LIVING_ROCK_PLANTER, VaultMod.id("living_rock_planter"));
      registerBlock(event, ANIMATRIX_BLOCK, VaultMod.id("animatrix_block"));
      registerBlock(event, ASCENSION_FORGE, VaultMod.id("ascension_forge"));
      registerBlock(event, IDONA_BRICK, VaultMod.id("idona_brick"));
      registerBlock(event, IDONA_CHISELED_BRICK, VaultMod.id("idona_brick_chiseled"));
      registerBlock(event, IDONA_DARK_SMOOTH_BLOCK, VaultMod.id("idona_dark_smooth_brick"));
      registerBlock(event, IDONA_LIGHT_SMOOTH_BLOCK, VaultMod.id("idona_light_smooth_brick"));
      registerBlock(event, IDONA_GEM_BLOCK, VaultMod.id("idona_gem_block"));
      registerBlock(event, TENOS_BRICK, VaultMod.id("tenos_brick"));
      registerBlock(event, TENOS_CHISELED_BRICK, VaultMod.id("tenos_brick_chiseled"));
      registerBlock(event, TENOS_DARK_SMOOTH_BLOCK, VaultMod.id("tenos_dark_smooth_brick"));
      registerBlock(event, TENOS_LIGHT_SMOOTH_BLOCK, VaultMod.id("tenos_light_smooth_brick"));
      registerBlock(event, TENOS_GEM_BLOCK, VaultMod.id("tenos_gem_block"));
      registerBlock(event, TENOS_PLANKS, VaultMod.id("tenos_planks"));
      registerBlock(event, TENOS_VERTICAL_PLANKS, VaultMod.id("tenos_vertical_planks"));
      registerBlock(event, TENOS_LOG, VaultMod.id("tenos_log"));
      registerBlock(event, TENOS_STRIPPED_LOG, VaultMod.id("tenos_stripped_log"));
      registerBlock(event, TENOS_BOOKSHELF, VaultMod.id("tenos_bookshelf"));
      registerBlock(event, TENOS_BOOKSHELF_EMPTY, VaultMod.id("tenos_bookshelf_empty"));
      registerBlock(event, VELARA_BRICK, VaultMod.id("velara_brick"));
      registerBlock(event, VELARA_CHISELED_BRICK, VaultMod.id("velara_brick_chiseled"));
      registerBlock(event, VELARA_DARK_SMOOTH_BLOCK, VaultMod.id("velara_dark_smooth_brick"));
      registerBlock(event, VELARA_LIGHT_SMOOTH_BLOCK, VaultMod.id("velara_light_smooth_brick"));
      registerBlock(event, VELARA_GEM_BLOCK, VaultMod.id("velara_gem_block"));
      registerBlock(event, VELARA_LOG, VaultMod.id("velara_log"));
      registerBlock(event, VELARA_PLANKS, VaultMod.id("velara_planks"));
      registerBlock(event, VELARA_VERTICAL_PLANKS, VaultMod.id("velara_vertical_planks"));
      registerBlock(event, VELARA_PLANKS_STAIRS, VaultMod.id("velara_planks_stairs"));
      registerBlock(event, VELARA_PLANKS_SLAB, VaultMod.id("velara_planks_slab"));
      registerBlock(event, VELARA_STRIPPED_LOG, VaultMod.id("velara_stripped_log"));
      registerBlock(event, VELARA_MOSSY_LOG, VaultMod.id("velara_mossy_log"));
      registerBlock(event, VELARA_MOSSY_LOG_BLOOMING, VaultMod.id("velara_mossy_blooming_log"));
      registerBlock(event, VELARA_MOSSY_BLOCK, VaultMod.id("velara_mossy_block"));
      registerBlock(event, VELARA_LEAVES, VaultMod.id("velara_leaves"));
      registerBlock(event, VELARA_BUSH, VaultMod.id("velara_bush"));
      registerBlock(event, VELARA_VINE, VaultMod.id("velara_vine"));
      registerBlock(event, WENDARR_BRICK, VaultMod.id("wendarr_brick"));
      registerBlock(event, WENDARR_CHISELED_BRICK, VaultMod.id("wendarr_brick_chiseled"));
      registerBlock(event, WENDARR_DARK_SMOOTH_BLOCK, VaultMod.id("wendarr_dark_smooth_brick"));
      registerBlock(event, WENDARR_LIGHT_SMOOTH_BLOCK, VaultMod.id("wendarr_light_smooth_brick"));
      registerBlock(event, WENDARR_GEM_BLOCK, VaultMod.id("wendarr_gem_block"));
      registerBlock(event, WENDARR_JEWEL_BLOCK, VaultMod.id("wendarr_jewel_block"));
      registerBlock(event, WENDARR_JEWEL_GLASS, VaultMod.id("wendarr_jewel_glass"));
      registerBlock(event, WENDARR_JEWEL_GLASS_PANE, VaultMod.id("wendarr_jewel_glass_pane"));
      registerBlock(event, ORNATE_CHAIN, VaultMod.id("ornate_chain"));
      registerBlock(event, ORNATE_CHAIN_RUSTY, VaultMod.id("ornate_chain_rusty"));
      registerBlock(event, ORNATE_BLOCK, VaultMod.id("ornate_block"));
      registerBlock(event, ORNATE_BRICKS, VaultMod.id("ornate_bricks"));
      registerBlock(event, ORNATE_BRICKS_RUSTY, VaultMod.id("ornate_bricks_rusty"));
      registerBlock(event, ORNATE_BLOCK_CHISELED, VaultMod.id("ornate_block_chiseled"));
      registerBlock(event, ORNATE_BLOCK_TILED, VaultMod.id("ornate_block_tiled"));
      registerBlock(event, ORNATE_BLOCK_PILLAR, VaultMod.id("ornate_block_pillar"));
      registerBlock(event, ORNATE_BRICKS_CHIPPED, VaultMod.id("ornate_bricks_chipped"));
      registerBlock(event, ORNATE_BRICKS_CRACKED, VaultMod.id("ornate_bricks_cracked"));
      registerBlock(event, ORNATE_BRICKS_NETHERITE, VaultMod.id("ornate_bricks_netherite"));
      registerBlock(event, VELVET_BED, VaultMod.id("velvet_bed"));
      registerBlock(event, VELVET_BLOCK, VaultMod.id("velvet_block"));
      registerBlock(event, VELVET_BLOCK_CHISELED, VaultMod.id("velvet_block_chiseled"));
      registerBlock(event, VELVET_BLOCK_STRIPS, VaultMod.id("velvet_block_strips"));
      registerBlock(event, VELVET_CARPET, VaultMod.id("velvet_carpet"));
      registerBlock(event, ORNATE_BLOCK_VELVET, VaultMod.id("ornate_block_velvet"));
      registerBlock(event, ORNATE_BRICKS_VELVET, VaultMod.id("ornate_bricks_velvet"));
      registerBlock(event, ORNATE_BLOCK_VELVET_CHISELED, VaultMod.id("ornate_block_velvet_chiseled"));
      registerBlock(event, ORNATE_BLOCK_VELVET_PILLAR, VaultMod.id("ornate_block_velvet_pillar"));
      registerBlock(event, SOOT, VaultMod.id("soot"));
      registerBlock(event, TOPAZ_BLOCK, VaultMod.id("topaz_block"));
      registerBlock(event, GILDED_SCONCE, VaultMod.id("gilded_sconce"));
      registerBlock(event, GILDED_SCONCE_WALL, VaultMod.id("gilded_sconce_wall"));
      registerBlock(event, GILDED_BLOCK, VaultMod.id("gilded_block"));
      registerBlock(event, GILDED_BLOCK_PILLAR, VaultMod.id("gilded_block_pillar"));
      registerBlock(event, GILDED_BLOCK_CHISELED, VaultMod.id("gilded_block_chiseled"));
      registerBlock(event, GILDED_BLOCK_BUMBO, VaultMod.id("gilded_block_bumbo"));
      registerBlock(event, GILDED_BRICKS, VaultMod.id("gilded_bricks"));
      registerBlock(event, GILDED_BRICKS_CRACKED, VaultMod.id("gilded_bricks_cracked"));
      registerBlock(event, GILDED_BRICKS_DULL, VaultMod.id("gilded_bricks_dull"));
      registerBlock(event, GILDED_BRICKS_CRACKED_DULL, VaultMod.id("gilded_bricks_cracked_dull"));
      registerBlock(event, GILDED_COBBLE, VaultMod.id("gilded_cobble"));
      registerBlock(event, ANCIENT_COPPER_BLOCK, VaultMod.id("ancient_copper_block"));
      registerBlock(event, ANCIENT_COPPER_BRICKS, VaultMod.id("ancient_copper_bricks"));
      registerBlock(event, ANCIENT_COPPER_SMALL_BRICKS, VaultMod.id("ancient_copper_small_bricks"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_PILLAR, VaultMod.id("ancient_copper_block_pillar"));
      registerBlock(event, ANCIENT_COPPER_TRAPDOOR, VaultMod.id("ancient_copper_trapdoor"));
      registerBlock(event, ANCIENT_COPPER_BUTTON, VaultMod.id("ancient_copper_button"));
      registerBlock(event, ANCIENT_COPPER_TRAPDOOR_EXPOSED, VaultMod.id("ancient_copper_trapdoor_exposed"));
      registerBlock(event, ANCIENT_COPPER_BUTTON_EXPOSED, VaultMod.id("ancient_copper_button_exposed"));
      registerBlock(event, ANCIENT_COPPER_TRAPDOOR_WEATHERED, VaultMod.id("ancient_copper_trapdoor_weathered"));
      registerBlock(event, ANCIENT_COPPER_BUTTON_WEATHERED, VaultMod.id("ancient_copper_button_weathered"));
      registerBlock(event, ANCIENT_COPPER_TRAPDOOR_OXIDIZED, VaultMod.id("ancient_copper_trapdoor_oxidized"));
      registerBlock(event, ANCIENT_COPPER_BUTTON_OXIDIZED, VaultMod.id("ancient_copper_button_oxidized"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_EXPOSED, VaultMod.id("ancient_copper_block_exposed"));
      registerBlock(event, ANCIENT_COPPER_BRICKS_EXPOSED, VaultMod.id("ancient_copper_bricks_exposed"));
      registerBlock(event, ANCIENT_COPPER_SMALL_BRICKS_EXPOSED, VaultMod.id("ancient_copper_small_bricks_exposed"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_PILLAR_EXPOSED, VaultMod.id("ancient_copper_block_pillar_exposed"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_WEATHERED, VaultMod.id("ancient_copper_block_weathered"));
      registerBlock(event, ANCIENT_COPPER_BRICKS_WEATHERED, VaultMod.id("ancient_copper_bricks_weathered"));
      registerBlock(event, ANCIENT_COPPER_SMALL_BRICKS_WEATHERED, VaultMod.id("ancient_copper_small_bricks_weathered"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_PILLAR_WEATHERED, VaultMod.id("ancient_copper_block_pillar_weathered"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_OXIDIZED, VaultMod.id("ancient_copper_block_oxidized"));
      registerBlock(event, ANCIENT_COPPER_BRICKS_OXIDIZED, VaultMod.id("ancient_copper_bricks_oxidized"));
      registerBlock(event, ANCIENT_COPPER_SMALL_BRICKS_OXIDIZED, VaultMod.id("ancient_copper_small_bricks_oxidized"));
      registerBlock(event, ANCIENT_COPPER_BLOCK_PILLAR_OXIDIZED, VaultMod.id("ancient_copper_block_pillar_oxidized"));
      registerBlock(event, GILDED_CANDELABRA, VaultMod.id("gilded_candelabra"));
      registerBlock(event, VAULT_BARS, VaultMod.id("vault_bars"));
      registerBlock(event, WOODEN_LOG, VaultMod.id("wooden_log"));
      registerBlock(event, STRIPPED_WOODEN_LOG, VaultMod.id("stripped_wooden_log"));
      registerBlock(event, WOODEN_PLANKS, VaultMod.id("wooden_planks"));
      registerBlock(event, WOODEN_SLAB, VaultMod.id("wooden_slab"));
      registerBlock(event, WOODEN_STAIRS, VaultMod.id("wooden_stairs"));
      registerBlock(event, SANDY_BLOCK, VaultMod.id("sandy_block"));
      registerBlock(event, SANDY_BRICKS, VaultMod.id("sandy_bricks"));
      registerBlock(event, SANDY_SMALL_BRICKS, VaultMod.id("sandy_small_bricks"));
      registerBlock(event, SANDY_BRICKS_CRACKED, VaultMod.id("sandy_bricks_cracked"));
      registerBlock(event, SANDY_SMALL_BRICKS_CRACKED, VaultMod.id("sandy_small_bricks_cracked"));
      registerBlock(event, SANDY_BLOCK_POLISHED, VaultMod.id("sandy_block_polished"));
      registerBlock(event, SANDY_BLOCK_CHISELED, VaultMod.id("sandy_block_chiseled"));
      registerBlock(event, SANDY_BLOCK_BUMBO, VaultMod.id("sandy_block_bumbo"));
      registerBlock(event, OVERGROWN_WOODEN_LOG, VaultMod.id("overgrown_wooden_log"));
      registerBlock(event, STRIPPED_OVERGROWN_WOODEN_LOG, VaultMod.id("stripped_overgrown_wooden_log"));
      registerBlock(event, OVERGROWN_WOODEN_PLANKS, VaultMod.id("overgrown_wooden_planks"));
      registerBlock(event, OVERGROWN_WOODEN_SLAB, VaultMod.id("overgrown_wooden_slab"));
      registerBlock(event, OVERGROWN_WOODEN_STAIRS, VaultMod.id("overgrown_wooden_stairs"));
      registerBlock(event, ROTTEN_MEAT_BLOCK, VaultMod.id("rotten_meat_block"));
      registerBlock(event, LIVING_ROCK_BLOCK_COBBLE, VaultMod.id("living_rock_block_cobble"));
      registerBlock(event, LIVING_ROCK_BLOCK_POLISHED, VaultMod.id("living_rock_block_polished"));
      registerBlock(event, LIVING_ROCK_BLOCK_STACKED, VaultMod.id("living_rock_block_stacked"));
      registerBlock(event, LIVING_ROCK_BRICKS, VaultMod.id("living_rock_bricks"));
      registerBlock(event, MOSSY_LIVING_ROCK_BLOCK_COBBLE, VaultMod.id("mossy_living_rock_block_cobble"));
      registerBlock(event, MOSSY_LIVING_ROCK_BLOCK_POLISHED, VaultMod.id("mossy_living_rock_block_polished"));
      registerBlock(event, MOSSY_LIVING_ROCK_BLOCK_STACKED, VaultMod.id("mossy_living_rock_block_stacked"));
      registerBlock(event, MOSSY_LIVING_ROCK_BRICKS, VaultMod.id("mossy_living_rock_bricks"));
      registerBlock(event, MOSSY_BONE_BLOCK, VaultMod.id("mossy_bone_block"));
      registerBlock(event, VAULT_MOSS, VaultMod.id("vault_moss"));
      registerBlock(event, VAULT_SWEETS_BLOCK, VaultMod.id("vault_sweets_block"));
      registerBlock(event, VAULT_SMALL_SWEETS_BLOCK, VaultMod.id("vault_small_sweets_block"));
      registerBlock(event, VAULT_SWEETS, VaultMod.id("vault_sweets"));
      registerBlock(event, CHROMATIC_LOG, VaultMod.id("chromatic_log"));
      registerBlock(event, STRIPPED_CHROMATIC_LOG, VaultMod.id("stripped_chromatic_log"));
      registerBlock(event, CHROMATIC_PLANKS, VaultMod.id("chromatic_planks"));
      registerBlock(event, CHROMATIC_SLAB, VaultMod.id("chromatic_slab"));
      registerBlock(event, CHROMATIC_STAIRS, VaultMod.id("chromatic_stairs"));
      registerBlock(event, DRIFTWOOD_LOG, VaultMod.id("driftwood_log"));
      registerBlock(event, STRIPPED_DRIFTWOOD_LOG, VaultMod.id("stripped_driftwood_log"));
      registerBlock(event, DRIFTWOOD_PLANKS, VaultMod.id("driftwood_planks"));
      registerBlock(event, DRIFTWOOD_SLAB, VaultMod.id("driftwood_slab"));
      registerBlock(event, DRIFTWOOD_STAIRS, VaultMod.id("driftwood_stairs"));
      registerBlock(event, COMPRESSED_SOOT_BLOCK, VaultMod.id("compressed_soot_block"));
      registerBlock(event, VAULT_ASH, VaultMod.id("vault_ash"));
      registerBlock(event, DRY_VAULT_ASH, VaultMod.id("dry_vault_ash"));
      registerBlock(event, FOOLS_GOLD_BLOCK, VaultMod.id("fools_gold"));
      registerBlock(event, TOTEM_PLAYER_HEALTH, VaultMod.id("totem_of_rejuvenation"));
      registerBlock(event, TOTEM_MOB_DAMAGE, VaultMod.id("totem_of_hatred"));
      registerBlock(event, TOTEM_MANA_REGEN, VaultMod.id("totem_of_spirit"));
      registerBlock(event, TOTEM_PLAYER_DAMAGE, VaultMod.id("totem_of_wrath"));
      registerBlock(event, OFFERING_PILLAR, VaultMod.id("offering_pillar"));
      registerBlock(event, BARRED_DOOR, VaultMod.id("barred_door"));
      registerBlock(event, BARRED_TRAPDOOR, VaultMod.id("barred_trapdoor"));
   }

   public static void registerTileEntities(Register<BlockEntityType<?>> event) {
      registerTileEntity(event, VAULT_ORE_TILE_ENTITY, VaultMod.id("vault_ore_tile_entity"));
      registerTileEntity(event, FLOATING_TEXT_TILE_ENTITY, VaultMod.id("floating_text_tile_entity"));
      registerTileEntity(event, VAULT_ALTAR_TILE_ENTITY, VaultMod.id("vault_altar_tile_entity"));
      registerTileEntity(event, HERALD_TROPHY_TILE_ENTITY, VaultMod.id("herald_trophy_tile_entity"));
      registerTileEntity(event, FINAL_VAULT_FRAME_TILE_ENTITY, VaultMod.id("final_vault_frame_tile_entity"));
      registerTileEntity(event, VAULT_CRATE_TILE_ENTITY, VaultMod.id("vault_crate_tile_entity"));
      registerTileEntity(event, VAULT_PORTAL_TILE_ENTITY, VaultMod.id("vault_portal_tile_entity"));
      registerTileEntity(event, RELIC_STATUE_TILE_ENTITY, VaultMod.id("relic_statue_tile_entity"));
      registerTileEntity(event, LOOT_STATUE_TILE_ENTITY, VaultMod.id("loot_statue_tile_entity"));
      registerTileEntity(event, SHOP_PEDESTAL_TILE_ENTITY, VaultMod.id("shop_pedestal_tile_entity"));
      registerTileEntity(event, TREASURE_PEDESTAL_TILE_ENTITY, VaultMod.id("treasure_pedestal_tile_entity"));
      registerTileEntity(event, TROPHY_STATUE_TILE_ENTITY, VaultMod.id("trophy_statue_tile_entity"));
      registerTileEntity(event, CRYO_CHAMBER_TILE_ENTITY, VaultMod.id("cryo_chamber_tile_entity"));
      registerTileEntity(event, ANCIENT_CRYO_CHAMBER_TILE_ENTITY, VaultMod.id("ancient_cryo_chamber_tile_entity"));
      registerTileEntity(event, TREASURE_DOOR_TILE_ENTITY, VaultMod.id("treasure_door_tile_entity"));
      registerTileEntity(event, DUNGEON_DOOR_TILE_ENTITY, VaultMod.id("dungeon_door_tile_entity"));
      registerTileEntity(event, VENDOR_DOOR_TILE_ENTITY, VaultMod.id("vendor_door_tile_entity"));
      registerTileEntity(event, GATE_LOCK_TILE_ENTITY, VaultMod.id("gate_lock_tile_entity"));
      registerTileEntity(event, HOLOGRAM_TILE_ENTITY, VaultMod.id("hologram_tile_entity"));
      registerTileEntity(event, SOUL_PLAQUE_TILE_ENTITY, VaultMod.id("soul_plaque_tile_entity"));
      registerTileEntity(event, HERALD_CONTROLLER_TILE_ENTITY, VaultMod.id("herald_controller_tile_entity"));
      registerTileEntity(event, RAID_CONTROLLER_TILE_ENTITY, VaultMod.id("raid_controller_tile_entity"));
      registerTileEntity(event, X_MARK_CONTROLLER_TILE_ENTITY, VaultMod.id("x_mark_controller_tile_entity"));
      registerTileEntity(event, ELITE_CONTROLLER_TILE_ENTITY, VaultMod.id("elite_controller_tile_entity"));
      registerTileEntity(event, RAID_CONTROLLER_PROXY_TILE_ENTITY, VaultMod.id("raid_controller_proxy_tile_entity"));
      registerTileEntity(event, ELITE_CONTROLLER_PROXY_TILE_ENTITY, VaultMod.id("elite_controller_proxy_tile_entity"));
      registerTileEntity(event, VAULT_CHEST_TILE_ENTITY, VaultMod.id("vault_chest_tile_entity"));
      registerTileEntity(event, GOD_ALTAR_TILE_ENTITY, VaultMod.id("god_altar_tile_entity"));
      registerTileEntity(event, OBELISK_TILE_ENTITY, VaultMod.id("obelisk_tile_entity"));
      registerTileEntity(event, MONOLITH_TILE_ENTITY, VaultMod.id("monolith_tile_entity"));
      registerTileEntity(event, CRAKE_PEDESTAL_TILE_ENTITY, VaultMod.id("crake_pedestal_tile_entity"));
      registerTileEntity(event, LODESTONE_TILE_ENTITY, VaultMod.id("lodestone_tile_entity"));
      registerTileEntity(event, PYLON_TILE_ENTITY, VaultMod.id("pylon_tile_entity"));
      registerTileEntity(event, ENHANCEMENT_ALTAR_TILE_ENTITY, VaultMod.id("enhancement_altar_tile_entity"));
      registerTileEntity(event, HOURGLASS_TILE_ENTITY, VaultMod.id("hourglass_tile_entity"));
      registerTileEntity(event, SCAVENGER_ALTAR_TILE_ENTITY, VaultMod.id("scavenger_altar_tile_entity"));
      registerTileEntity(event, DIVINE_ALTAR_TILE_ENTITY, VaultMod.id("divine_altar_tile_entity"));
      registerTileEntity(event, SCAVENGER_TREASURE_TILE_ENTITY, VaultMod.id("scavenger_treasure_tile_entity"));
      registerTileEntity(event, STABILIZER_TILE_ENTITY, VaultMod.id("stabilizer_tile_entity"));
      registerTileEntity(event, CATALYST_INFUSION_TABLE_TILE_ENTITY, VaultMod.id("catalyst_infusion_table_tile_entity"));
      registerTileEntity(event, ETCHING_CONTROLLER_TILE_ENTITY, VaultMod.id("etching_vendor_controller_tile_entity"));
      registerTileEntity(event, VAULT_CHARM_CONTROLLER_TILE_ENTITY, VaultMod.id("vault_charm_controller_tile_entity"));
      registerTileEntity(event, TOOL_VISE_TILE_ENTITY, VaultMod.id("tool_vise_tile_entity"));
      registerTileEntity(event, MAGNET_TABLE_TILE_ENTITY, VaultMod.id("magnet_modification_table_tile_entity"));
      registerTileEntity(event, DEMAGNETIZER_TILE_ENTITY, VaultMod.id("demagnetizer_tile_entity"));
      registerTileEntity(event, DEBAGNETIZER_TILE_ENTITY, VaultMod.id("debagnetizer_tile_entity"));
      registerTileEntity(event, COIN_PILE_TILE, VaultMod.id("coin_pile_tile"));
      registerTileEntity(event, TREASURE_SAND_TILE_ENTITY, VaultMod.id("treasure_sand_tile_entity"));
      registerTileEntity(event, VAULT_FORGE_TILE_ENTITY, VaultMod.id("vault_forge_tile_entity"));
      registerTileEntity(event, TOOL_STATION_TILE_ENTITY, VaultMod.id("tool_station_tile_entity"));
      registerTileEntity(event, INSCRIPTION_TABLE_TILE_ENTITY, VaultMod.id("inscription_table_tile_entity"));
      registerTileEntity(event, VAULT_ARTISAN_STATION_ENTITY, VaultMod.id("vault_artisan_station_tile_entity"));
      registerTileEntity(event, VAULT_JEWEL_CUTTING_STATION_ENTITY, VaultMod.id("vault_jewel_cutting_station_tile_entity"));
      registerTileEntity(event, VAULT_JEWEL_APPLICATION_STATION_ENTITY, VaultMod.id("vault_jewel_application_station_tile_entity"));
      registerTileEntity(event, JEWEL_CRAFTING_TABLE_ENTITY, VaultMod.id("jewel_crafting_table_tile_entity"));
      registerTileEntity(event, CRYSTAL_WORKBENCH_ENTITY, VaultMod.id("crystal_workbench_tile_entity"));
      registerTileEntity(event, VAULT_RECYCLER_ENTITY, VaultMod.id("vault_recycler_tile_entity"));
      registerTileEntity(event, VAULT_DIFFUSER_ENTITY, VaultMod.id("vault_diffuser_tile_entity"));
      registerTileEntity(event, VAULT_HARVESTER_ENTITY, VaultMod.id("vault_harvester_tile_entity"));
      registerTileEntity(event, MODIFIER_WORKBENCH_ENTITY, VaultMod.id("modifier_workbench_tile_entity"));
      registerTileEntity(event, ALCHEMY_TABLE_TILE_ENTITY, VaultMod.id("alchemy_table_tile_entity"));
      registerTileEntity(event, VAULT_ENCHANTER_TILE_ENTITY, VaultMod.id("vault_enchanter_tile_entity"));
      registerTileEntity(event, IDENTIFICATION_STAND_TILE_ENTITY, VaultMod.id("identification_stand_tile_entity"));
      registerTileEntity(event, CARD_ESSENCE_EXTRACTOR_TILE_ENTITY, VaultMod.id("card_essence_extractor_tile_entity"));
      registerTileEntity(event, MODIFIER_DISCOVERY_ENTITY, VaultMod.id("modifier_discovery_tile_entity"));
      registerTileEntity(event, ALCHEMY_ARCHIVE_TILE_ENTITY, VaultMod.id("alchemy_archive_tile_entity"));
      registerTileEntity(event, ANIMAL_PEN_ENTITY, VaultMod.id("animal_pen_tile_entity"));
      registerTileEntity(event, ELITE_SPAWNER_TILE_ENTITY, VaultMod.id("elite_spawner_tile_entity"));
      registerTileEntity(event, WILD_SPAWNER_TILE_ENTITY, VaultMod.id("wild_spawner_tile_entity"));
      registerTileEntity(event, CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, VaultMod.id("custom_entity_spawner_tile_entity"));
      registerTileEntity(event, CUBE_BLOCK_TILE_ENTITY, VaultMod.id("cube_block_tile_entity"));
      registerTileEntity(event, SPIRIT_EXTRACTOR_TILE_ENTITY, VaultMod.id("spirit_extractor_tile_entity"));
      registerTileEntity(event, WARDROBE_TILE_ENTITY, VaultMod.id("wardrobe_tile_entity"));
      registerTileEntity(event, SKILL_ALTAR_TILE_ENTITY, VaultMod.id("skill_altar_tile_entity"));
      registerTileEntity(event, BOUNTY_TABLE_TILE_ENTITY, VaultMod.id("bounty_table_tile_entity"));
      registerTileEntity(event, BLACK_MARKET_TILE_ENTITY, VaultMod.id("black_market_tile_entity"));
      registerTileEntity(event, TRANSMOG_TABLE_TILE_ENTITY, VaultMod.id("transmog_table_tile_entity"));
      registerTileEntity(event, CRYSTAL_BUDDING_TILE_ENTITY, VaultMod.id("crystal_budding_tile_entity"));
      registerTileEntity(event, ETERNAL_PEDESTAL_TILE_ENTITY, VaultMod.id("eternal_pedestal_tile_entity"));
      registerTileEntity(event, TOTEM_PLAYER_HEALTH_TILE_ENTITY, VaultMod.id("totem_player_health_tile_entity"));
      registerTileEntity(event, TOTEM_MOB_DAMAGE_TILE_ENTITY, VaultMod.id("totem_mob_damage_tile_entity"));
      registerTileEntity(event, TOTEM_MANA_REGEN_TILE_ENTITY, VaultMod.id("totem_mana_regen_tile_entity"));
      registerTileEntity(event, TOTEM_PLAYER_DAMAGE_TILE_ENTITY, VaultMod.id("totem_player_damage_tile_entity"));
      registerTileEntity(event, ANGEL_BLOCK_TILE_ENTITY, VaultMod.id("angel_block_tile_entity"));
      registerTileEntity(event, MOB_BARRIER_ENTITY, VaultMod.id("mob_barrier_entity"));
      registerTileEntity(event, ANCIENT_COPPER_CONDUIT_BLOCK_TILE_ENTITY, VaultMod.id("ancient_copper_conduit_block_entity"));
      registerTileEntity(event, ARTIFACT_PROJECTOR_ENTITY, VaultMod.id("artifact_projector_entity"));
      registerTileEntity(event, VELVET_BED_TILE_ENTITY, VaultMod.id("velvet_bed_tile_entity"));
      registerTileEntity(event, SPARK_TILE_ENTITY, VaultMod.id("spark_tile_entity"));
      registerTileEntity(event, CONVERTED_SPARK_TILE_ENTITY, VaultMod.id("converted_spark_tile_entity"));
      registerTileEntity(event, FOLIAGE_DECOR_TILE_ENTITY, VaultMod.id("foliage_decor_tile_entity"));
      registerTileEntity(event, ANIMATRIX_TILE_ENTITY, VaultMod.id("animatrix_tile_entity"));
      registerTileEntity(event, ASCENSION_FORGE_TILE_ENTITY, VaultMod.id("ascension_forge_tile_entity"));
      registerTileEntity(event, OFFERING_PILLAR_TILE_ENTITY, VaultMod.id("offering_pillar_tile_entity"));
      registerTileEntity(event, GRID_GATEWAY_TILE_ENTITY, VaultMod.id("grid_gateway_tile_entity"));
      registerTileEntity(event, TASK_PILLAR_TILE_ENTITY, VaultMod.id("task_pillar_tile_entity"));
   }

   public static void registerTileEntityRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer(FLOATING_TEXT_TILE_ENTITY, FloatingTextRenderer::new);
      event.registerBlockEntityRenderer(HERALD_TROPHY_TILE_ENTITY, HeraldTrophyRenderer::new);
      event.registerBlockEntityRenderer(FINAL_VAULT_FRAME_TILE_ENTITY, FinalVaultFrameRenderer::new);
      event.registerBlockEntityRenderer(VAULT_ALTAR_TILE_ENTITY, VaultAltarRenderer::new);
      event.registerBlockEntityRenderer(RELIC_STATUE_TILE_ENTITY, RelicPedestalRenderer::new);
      event.registerBlockEntityRenderer(LOOT_STATUE_TILE_ENTITY, LootStatueRenderer::new);
      event.registerBlockEntityRenderer(SHOP_PEDESTAL_TILE_ENTITY, ShopPedestalBlockTileRenderer::new);
      event.registerBlockEntityRenderer(TREASURE_PEDESTAL_TILE_ENTITY, TreasurePedestalTileRenderer::new);
      event.registerBlockEntityRenderer(TROPHY_STATUE_TILE_ENTITY, TrophyRenderer::new);
      event.registerBlockEntityRenderer(CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      event.registerBlockEntityRenderer(ANCIENT_CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      event.registerBlockEntityRenderer(VAULT_CHEST_TILE_ENTITY, VaultChestRenderer::new);
      event.registerBlockEntityRenderer(SCAVENGER_ALTAR_TILE_ENTITY, ScavengerAltarRenderer::new);
      event.registerBlockEntityRenderer(DIVINE_ALTAR_TILE_ENTITY, DivineAltarRenderer::new);
      event.registerBlockEntityRenderer(GOD_ALTAR_TILE_ENTITY, GodAltarRenderer::new);
      event.registerBlockEntityRenderer(HOURGLASS_TILE_ENTITY, HourglassRenderer::new);
      event.registerBlockEntityRenderer(VAULT_PORTAL_TILE_ENTITY, VaultPortalRenderer::new);
      event.registerBlockEntityRenderer(MAGNET_TABLE_TILE_ENTITY, MagnetTableRenderer::new);
      event.registerBlockEntityRenderer(TOOL_VISE_TILE_ENTITY, ToolViseRenderer::new);
      event.registerBlockEntityRenderer(ELITE_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(WILD_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(SPIRIT_EXTRACTOR_TILE_ENTITY, SpiritExtractorRenderer::new);
      event.registerBlockEntityRenderer(WARDROBE_TILE_ENTITY, WardrobeRenderer::new);
      event.registerBlockEntityRenderer(SKILL_ALTAR_TILE_ENTITY, SkillAltarRenderer::new);
      event.registerBlockEntityRenderer(VAULT_DIFFUSER_ENTITY, VaultDiffuserRenderer::new);
      event.registerBlockEntityRenderer(VAULT_HARVESTER_ENTITY, VaultDiffuserUpgradedRenderer::new);
      event.registerBlockEntityRenderer(BLACK_MARKET_TILE_ENTITY, BlackMarketRenderer::new);
      event.registerBlockEntityRenderer(FOLIAGE_DECOR_TILE_ENTITY, FoliageDecorRenderer::new);
      event.registerBlockEntityRenderer(ALCHEMY_TABLE_TILE_ENTITY, AlchemyTableRenderer::new);
      event.registerBlockEntityRenderer(ANIMAL_PEN_ENTITY, AnimalPenRenderer::new);
      event.registerBlockEntityRenderer(ETERNAL_PEDESTAL_TILE_ENTITY, EternalPedestalRenderer::new);
      event.registerBlockEntityRenderer(PYLON_TILE_ENTITY, PylonRenderer::new);
      event.registerBlockEntityRenderer(CRAKE_PEDESTAL_TILE_ENTITY, CrakePedestalRenderer::new);
      event.registerBlockEntityRenderer(TOOL_STATION_TILE_ENTITY, ToolStationRenderer::new);
      event.registerBlockEntityRenderer(VAULT_JEWEL_CUTTING_STATION_ENTITY, JewelCuttingStationRenderer::new);
      event.registerBlockEntityRenderer(VAULT_JEWEL_APPLICATION_STATION_ENTITY, JewelApplicationStationRenderer::new);
      event.registerBlockEntityRenderer(JEWEL_CRAFTING_TABLE_ENTITY, JewelCraftingTableRenderer::new);
      event.registerBlockEntityRenderer(CRYSTAL_WORKBENCH_ENTITY, CrystalWorkbenchRenderer::new);
      event.registerBlockEntityRenderer(ENHANCEMENT_ALTAR_TILE_ENTITY, EnhancementAltarRenderer::new);
      event.registerBlockEntityRenderer(MODIFIER_DISCOVERY_ENTITY, ModifierDiscoveryRenderer::new);
      event.registerBlockEntityRenderer(ALCHEMY_ARCHIVE_TILE_ENTITY, PotionModifierDiscoveryRenderer::new);
      event.registerBlockEntityRenderer(MODIFIER_WORKBENCH_ENTITY, ModifierWorkbenchRenderer::new);
      event.registerBlockEntityRenderer(VAULT_ENCHANTER_TILE_ENTITY, VaultEnchanterRenderer::new);
      event.registerBlockEntityRenderer(IDENTIFICATION_STAND_TILE_ENTITY, IdentificationStandRenderer::new);
      event.registerBlockEntityRenderer(TOTEM_PLAYER_HEALTH_TILE_ENTITY, TotemPlayerHealthRenderer::new);
      event.registerBlockEntityRenderer(TOTEM_MOB_DAMAGE_TILE_ENTITY, TotemMobDamageRenderer::new);
      event.registerBlockEntityRenderer(TOTEM_MANA_REGEN_TILE_ENTITY, TotemManaRegenRenderer::new);
      event.registerBlockEntityRenderer(TOTEM_PLAYER_DAMAGE_TILE_ENTITY, TotemPlayerDamageRenderer::new);
      event.registerBlockEntityRenderer(ANGEL_BLOCK_TILE_ENTITY, AngelBlockRenderer::new);
      event.registerBlockEntityRenderer(ANCIENT_COPPER_CONDUIT_BLOCK_TILE_ENTITY, AncientCopperConduitRenderer::new);
      event.registerBlockEntityRenderer(MOB_BARRIER_ENTITY, MobBarrierRenderer::new);
      event.registerBlockEntityRenderer(ARTIFACT_PROJECTOR_ENTITY, ArtifactProjectorRenderer::new);
      event.registerBlockEntityRenderer(BOUNTY_TABLE_TILE_ENTITY, BountyTableRenderer::new);
      event.registerBlockEntityRenderer(VELVET_BED_TILE_ENTITY, VelvetBedRenderer::new);
      event.registerBlockEntityRenderer(GATE_LOCK_TILE_ENTITY, GateLockRenderer::new);
      event.registerBlockEntityRenderer(HOLOGRAM_TILE_ENTITY, HologramRenderer::new);
      event.registerBlockEntityRenderer(SOUL_PLAQUE_TILE_ENTITY, SoulPlaqueRenderer::new);
      event.registerBlockEntityRenderer(SPARK_TILE_ENTITY, SparkRenderer::new);
      event.registerBlockEntityRenderer(HERALD_CONTROLLER_TILE_ENTITY, HeraldControllerRenderer::new);
      event.registerBlockEntityRenderer(RAID_CONTROLLER_TILE_ENTITY, ChallengeControllerRenderer::new);
      event.registerBlockEntityRenderer(X_MARK_CONTROLLER_TILE_ENTITY, ChallengeControllerRenderer::new);
      event.registerBlockEntityRenderer(ELITE_CONTROLLER_TILE_ENTITY, ChallengeControllerRenderer::new);
      event.registerBlockEntityRenderer(RAID_CONTROLLER_PROXY_TILE_ENTITY, RaidControllerProxyRenderer::new);
      event.registerBlockEntityRenderer(ELITE_CONTROLLER_PROXY_TILE_ENTITY, EliteControllerProxyRenderer::new);
      event.registerBlockEntityRenderer(CONVERTED_SPARK_TILE_ENTITY, ConvertedSparkRenderer::new);
      event.registerBlockEntityRenderer(ANIMATRIX_TILE_ENTITY, AnimatrixRenderer::new);
      event.registerBlockEntityRenderer(MONOLITH_TILE_ENTITY, MonolithRenderer::new);
      event.registerBlockEntityRenderer(OFFERING_PILLAR_TILE_ENTITY, OfferingPillarRenderer::new);
      event.registerBlockEntityRenderer(GRID_GATEWAY_TILE_ENTITY, GridGatewayRenderer::new);
      event.registerBlockEntityRenderer(TASK_PILLAR_TILE_ENTITY, TaskPillarRenderer::new);
   }

   public static void registerBlockItems(Register<Item> event) {
      registerBlockItem(event, FLOATING_TEXT);
      registerBlockItem(event, VAULT_PORTAL);
      registerBlockItem(event, HERALD_TROPHY, HERALD_TROPHY_BLOCK_ITEM);
      registerBlockItem(event, SOUL_PLAQUE, SOUL_PLAQUE_BLOCK_ITEM);
      registerBlockItem(event, FINAL_VAULT_FRAME, FINAL_VAULT_FRAME_BLOCK_ITEM);
      registerBlockItem(event, VAULT_ALTAR, 1);
      registerBlockItem(event, ALEXANDRITE_ORE, new VaultOreBlockItem(ALEXANDRITE_ORE));
      registerBlockItem(event, ASHIUM_ORE, new VaultOreBlockItem(ASHIUM_ORE));
      registerBlockItem(event, BENITOITE_ORE, new VaultOreBlockItem(BENITOITE_ORE));
      registerBlockItem(event, BLACK_OPAL_ORE, new VaultOreBlockItem(BLACK_OPAL_ORE));
      registerBlockItem(event, BOMIGNITE_ORE, new VaultOreBlockItem(BOMIGNITE_ORE));
      registerBlockItem(event, ECHO_ORE, new VaultOreBlockItem(ECHO_ORE));
      registerBlockItem(event, GORGINITE_ORE, new VaultOreBlockItem(GORGINITE_ORE));
      registerBlockItem(event, ISKALLIUM_ORE, new VaultOreBlockItem(ISKALLIUM_ORE));
      registerBlockItem(event, LARIMAR_ORE, new VaultOreBlockItem(LARIMAR_ORE));
      registerBlockItem(event, PAINITE_ORE, new VaultOreBlockItem(PAINITE_ORE));
      registerBlockItem(event, PETZANITE_ORE, new VaultOreBlockItem(PETZANITE_ORE));
      registerBlockItem(event, PUFFIUM_ORE, new VaultOreBlockItem(PUFFIUM_ORE));
      registerBlockItem(event, SPARKLETINE_ORE, new VaultOreBlockItem(SPARKLETINE_ORE));
      registerBlockItem(event, TUBIUM_ORE, new VaultOreBlockItem(TUBIUM_ORE));
      registerBlockItem(event, UPALINE_ORE, new VaultOreBlockItem(UPALINE_ORE));
      registerBlockItem(event, WUTODIE_ORE, new VaultOreBlockItem(WUTODIE_ORE));
      registerBlockItem(event, XENIUM_ORE, new VaultOreBlockItem(XENIUM_ORE));
      registerBlockItem(event, VAULT_ROCK_ORE);
      registerBlockItem(event, VAULT_ARTIFACT, new IdentifiedArtifactItem(VAULT_ARTIFACT, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1)));
      registerBlockItem(event, VAULT_CRATE, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_CAKE, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_ARENA, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_SCAVENGER, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_CHAMPION, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_BOUNTY, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_MONOLITH, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_ELIXIR, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_PARADOX, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_BINGO, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_BINGO_FULL, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_OFFERING_BOSS, 1, Properties::fireResistant);
      registerBlockItem(event, OBELISK, 1);
      registerBlockItem(event, MONOLITH, 1);
      registerBlockItem(event, LODESTONE, 1);
      registerBlockItem(event, CRAKE_PEDESTAL, 1);
      registerBlockItem(event, CRAKE_COLUMN);
      registerBlockItem(event, GRID_GATEWAY);
      registerBlockItem(event, TASK_BUILDER);
      registerBlockItem(event, PYLON, 1);
      registerBlockItem(event, ENHANCEMENT_ALTAR, 1);
      registerBlockItem(event, MVP_CROWN, 1);
      registerBlockItem(event, EASTER_EGG, EASTER_EGG_BLOCK_ITEM);
      registerBlockItem(event, VAULT_BEDROCK);
      registerBlockItem(event, VAULT_STONE);
      registerBlockItem(event, VAULT_COBBLESTONE);
      registerBlockItem(event, CHISELED_VAULT_STONE);
      registerBlockItem(event, POLISHED_VAULT_STONE);
      registerBlockItem(event, POLISHED_VAULT_STONE_SLAB);
      registerBlockItem(event, POLISHED_VAULT_STONE_STAIRS);
      registerBlockItem(event, BUMBO_POLISHED_VAULT_STONE);
      registerBlockItem(event, VAULT_STONE_BRICKS);
      registerBlockItem(event, VAULT_STONE_BRICK_SLAB);
      registerBlockItem(event, VAULT_STONE_BRICK_STAIRS);
      registerBlockItem(event, VAULT_STONE_BRICKS_CRACKED);
      registerBlockItem(event, VAULT_STONE_PILLAR);
      registerBlockItem(event, VAULT_GLASS);
      registerBlockItem(event, RELIC_PEDESTAL);
      registerBlockItem(event, LOOT_STATUE, LOOT_STATUE_ITEM);
      registerBlockItem(event, SHOP_PEDESTAL);
      registerBlockItem(event, TREASURE_PEDESTAL);
      registerBlockItem(event, CRYO_CHAMBER);
      registerBlockItem(event, VAULT_DIAMOND_BLOCK);
      registerBlockItem(event, TROPHY_STATUE, TROPHY_STATUE_BLOCK_ITEM);
      registerBlockItem(event, TRANSMOG_TABLE);
      registerBlockItem(event, WOODEN_CHEST, WOODEN_CHEST_ITEM);
      registerBlockItem(event, ALTAR_CHEST, ALTAR_CHEST_ITEM);
      registerBlockItem(event, HARDENED_CHEST, HARDENED_CHEST_ITEM);
      registerBlockItem(event, ENIGMA_CHEST, ENIGMA_CHEST_ITEM);
      registerBlockItem(event, FLESH_CHEST, FLESH_CHEST_ITEM);
      registerBlockItem(event, GILDED_CHEST, GILDED_CHEST_ITEM);
      registerBlockItem(event, ORNATE_CHEST, ORNATE_CHEST_ITEM);
      registerBlockItem(event, TREASURE_CHEST, TREASURE_CHEST_ITEM);
      registerBlockItem(event, LIVING_CHEST, LIVING_CHEST_ITEM);
      registerBlockItem(event, ORNATE_STRONGBOX, ORNATE_STRONGBOX_ITEM);
      registerBlockItem(event, GILDED_STRONGBOX, GILDED_STRONGBOX_ITEM);
      registerBlockItem(event, LIVING_STRONGBOX, LIVING_STRONGBOX_ITEM);
      registerBlockItem(event, ORNATE_BARREL, ORNATE_BARREL_ITEM);
      registerBlockItem(event, GILDED_BARREL, GILDED_BARREL_ITEM);
      registerBlockItem(event, LIVING_BARREL, LIVING_BARREL_ITEM);
      registerBlockItem(event, WOODEN_BARREL, WOODEN_BARREL_ITEM);
      registerBlockItem(event, WOODEN_CHEST_PLACEABLE, WOODEN_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, ALTAR_CHEST_PLACEABLE, ALTAR_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, GILDED_CHEST_PLACEABLE, GILDED_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, ORNATE_CHEST_PLACEABLE, ORNATE_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, TREASURE_CHEST_PLACEABLE, TREASURE_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, LIVING_CHEST_PLACEABLE, LIVING_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, HARDENED_CHEST_PLACEABLE, HARDENED_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, ENIGMA_CHEST_PLACEABLE, ENIGMA_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, FLESH_CHEST_PLACEABLE, FLESH_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, GOD_ALTAR, new GodAltarBlockItem());
      registerBlockItem(event, HOURGLASS, new HourglassBlockItem(HOURGLASS));
      registerBlockItem(event, SCAVENGER_ALTAR, SCAVENGER_ALTAR_ITEM);
      registerBlockItem(event, DIVINE_ALTAR);
      registerBlockItem(event, SCAVENGER_TREASURE);
      registerBlockItem(event, STABILIZER);
      registerBlockItem(event, CATALYST_INFUSION_TABLE);
      registerBlockItem(event, ETCHING_CONTROLLER_BLOCK);
      registerBlockItem(event, VAULT_CHARM_CONTROLLER_BLOCK);
      registerBlockItem(event, PLACEHOLDER, new PlaceholderBlockItem());
      registerBlockItem(event, TREASURE_DOOR, new TreasureDoorBlockItem());
      registerBlockItem(event, GATE_LOCK);
      registerBlockItem(event, HOLOGRAM);
      registerBlockItem(event, HERALD_CONTROLLER);
      registerBlockItem(event, RAID_CONTROLLER, 1);
      registerBlockItem(event, X_MARK_CONTROLLER, 1);
      registerBlockItem(event, ELITE_CONTROLLER, 1);
      registerBlockItem(event, RAID_CONTROLLER_PROXY, 1);
      registerBlockItem(event, ELITE_CONTROLLER_PROXY, 1);
      registerBlockItem(event, DUNGEON_DOOR, new DungeonDoorBlockItem());
      registerBlockItem(event, VENDOR_DOOR, new VendorDoorBlockItem());
      registerBlockItem(event, COIN_PILE);
      registerBlockItem(event, BRONZE_COIN_PILE, VAULT_BRONZE);
      registerBlockItem(event, SILVER_COIN_PILE, VAULT_SILVER);
      registerBlockItem(event, GOLD_COIN_PILE, VAULT_GOLD);
      registerBlockItem(event, PLATINUM_COIN_PILE, VAULT_PLATINUM);
      registerBlockItem(event, TOOL_VISE);
      registerBlockItem(event, MAGNET_TABLE);
      registerBlockItem(event, DEMAGNETIZER_BLOCK);
      registerBlockItem(event, DEBAGNETIZER);
      registerBlockItem(event, CHROMATIC_IRON_ORE);
      registerBlockItem(event, RAW_CHROMATIC_IRON_BLOCK);
      registerBlockItem(event, CHROMATIC_IRON_BLOCK);
      registerBlockItem(event, CHROMATIC_STEEL_BLOCK);
      registerBlockItem(event, BLACK_CHROMATIC_STEEL_BLOCK);
      registerBlockItem(event, VAULT_MEAT_BLOCK);
      registerBlockItem(event, PACKED_VAULT_MEAT_BLOCK);
      registerBlockItem(event, TREASURE_SAND);
      registerBlockItem(event, VAULT_FORGE);
      registerBlockItem(event, TOOL_STATION);
      registerBlockItem(event, INSCRIPTION_TABLE);
      registerBlockItem(event, VAULT_ARTISAN_STATION);
      registerBlockItem(event, VAULT_JEWEL_CUTTING_STATION);
      registerBlockItem(event, VAULT_JEWEL_APPLICATION_STATION);
      registerBlockItem(event, JEWEL_CRAFTING_TABLE);
      registerBlockItem(event, CRYSTAL_WORKBENCH);
      registerBlockItem(event, VAULT_RECYCLER);
      registerBlockItem(event, VAULT_DIFFUSER);
      registerBlockItem(event, VAULT_HARVESTER);
      registerBlockItem(event, MODIFIER_WORKBENCH);
      registerBlockItem(event, MODIFIER_DISCOVERY);
      registerBlockItem(event, ALCHEMY_ARCHIVE);
      registerBlockItem(event, ALCHEMY_TABLE);
      registerBlockItem(event, VAULT_ENCHANTER);
      registerBlockItem(event, IDENTIFICATION_STAND);
      registerBlockItem(event, CARD_ESSENCE_EXTRACTOR);
      registerBlockItem(event, ELITE_SPAWNER);
      registerBlockItem(event, WILD_SPAWNER);
      registerBlockItem(event, CUSTOM_ENTITY_SPAWNER);
      registerBlockItem(event, SPIRIT_EXTRACTOR);
      registerBlockItem(event, WARDROBE);
      registerBlockItem(event, SKILL_ALTAR);
      registerBlockItem(event, MAGIC_SILK_BLOCK);
      registerBlockItem(event, YELLOW_PUZZLE_CONCRETE);
      registerBlockItem(event, PINK_PUZZLE_CONCRETE);
      registerBlockItem(event, GREEN_PUZZLE_CONCRETE);
      registerBlockItem(event, BLUE_PUZZLE_CONCRETE);
      registerBlockItem(event, SUGAR_PLUM_FAIRY_FLOWER);
      registerBlockItem(event, GOLDEN_TOOTH);
      registerBlockItem(event, CUBE_BLOCK);
      registerBlockItem(event, BOUNTY_BLOCK);
      registerBlockItem(event, BLACK_MARKET);
      registerBlockItem(event, ANIMAL_PEN);
      registerBlockItem(event, CRYSTAL_BUDDING);
      registerBlockItem(event, SMALL_CRYSTAL_BUD);
      registerBlockItem(event, MEDIUM_CRYSTAL_BUD);
      registerBlockItem(event, LARGE_CRYSTAL_BUD);
      registerBlockItem(event, VAULT_ANVIL);
      registerBlockItem(event, WUTODIC_SILVER_BLOCK);
      registerBlockItem(event, ETERNAL_PEDESTAL);
      registerBlockItem(event, ANGEL_BLOCK, ANGEL_BLOCK_ITEM);
      registerBlockItem(event, ANCIENT_COPPER_CONDUIT_BLOCK, ANCIENT_COPPER_CONDUIT_BLOCK_ITEM);
      registerBlockItem(event, MOB_BARRIER);
      registerBlockItem(event, ARTIFACT_PROJECTOR_BLOCK);
      registerBlockItem(event, LIVING_ROCK_PLANTER);
      registerBlockItem(event, ANIMATRIX_BLOCK);
      registerBlockItem(event, ASCENSION_FORGE);
      registerBlockItem(event, ORNATE_CHAIN);
      registerBlockItem(event, ORNATE_CHAIN_RUSTY);
      registerBlockItem(event, ORNATE_BLOCK);
      registerBlockItem(event, ORNATE_BRICKS);
      registerBlockItem(event, ORNATE_BRICKS_RUSTY);
      registerBlockItem(event, ORNATE_BLOCK_CHISELED);
      registerBlockItem(event, ORNATE_BLOCK_TILED);
      registerBlockItem(event, ORNATE_BLOCK_PILLAR);
      registerBlockItem(event, ORNATE_BRICKS_CRACKED);
      registerBlockItem(event, ORNATE_BRICKS_CHIPPED);
      registerBlockItem(event, ORNATE_BRICKS_NETHERITE);
      registerBlockItem(event, VELVET_BLOCK);
      registerBlockItem(event, VELVET_BLOCK_CHISELED);
      registerBlockItem(event, VELVET_BLOCK_STRIPS);
      registerBlockItem(event, VELVET_CARPET);
      registerBlockItem(event, ORNATE_BLOCK_VELVET);
      registerBlockItem(event, ORNATE_BRICKS_VELVET);
      registerBlockItem(event, ORNATE_BLOCK_VELVET_PILLAR);
      registerBlockItem(event, ORNATE_BLOCK_VELVET_CHISELED);
      registerBlockItem(event, SOOT);
      registerBlockItem(event, VAULT_BARS);
      registerBlockItem(event, TOPAZ_BLOCK);
      event.getRegistry().register(GILDED_SCONCE_ITEM);
      event.getRegistry().register(VELVET_BED_ITEM);
      registerBlockItem(event, GILDED_BLOCK);
      registerBlockItem(event, GILDED_BLOCK_PILLAR);
      registerBlockItem(event, GILDED_BLOCK_CHISELED);
      registerBlockItem(event, GILDED_BLOCK_BUMBO);
      registerBlockItem(event, GILDED_BRICKS);
      registerBlockItem(event, GILDED_BRICKS_CRACKED);
      registerBlockItem(event, GILDED_BRICKS_DULL);
      registerBlockItem(event, GILDED_BRICKS_CRACKED_DULL);
      registerBlockItem(event, GILDED_COBBLE);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK);
      registerBlockItem(event, ANCIENT_COPPER_BRICKS);
      registerBlockItem(event, ANCIENT_COPPER_SMALL_BRICKS);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_PILLAR);
      registerBlockItem(event, ANCIENT_COPPER_TRAPDOOR);
      registerBlockItem(event, ANCIENT_COPPER_BUTTON);
      registerBlockItem(event, ANCIENT_COPPER_TRAPDOOR_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_BUTTON_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_TRAPDOOR_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_BUTTON_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_TRAPDOOR_OXIDIZED);
      registerBlockItem(event, ANCIENT_COPPER_BUTTON_OXIDIZED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_BRICKS_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_SMALL_BRICKS_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_PILLAR_EXPOSED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_BRICKS_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_SMALL_BRICKS_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_PILLAR_WEATHERED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_OXIDIZED);
      registerBlockItem(event, ANCIENT_COPPER_BRICKS_OXIDIZED);
      registerBlockItem(event, ANCIENT_COPPER_SMALL_BRICKS_OXIDIZED);
      registerBlockItem(event, ANCIENT_COPPER_BLOCK_PILLAR_OXIDIZED);
      registerBlockItem(event, IDONA_BRICK);
      registerBlockItem(event, IDONA_CHISELED_BRICK);
      registerBlockItem(event, IDONA_DARK_SMOOTH_BLOCK);
      registerBlockItem(event, IDONA_LIGHT_SMOOTH_BLOCK);
      registerBlockItem(event, IDONA_GEM_BLOCK);
      registerBlockItem(event, TENOS_BRICK);
      registerBlockItem(event, TENOS_CHISELED_BRICK);
      registerBlockItem(event, TENOS_DARK_SMOOTH_BLOCK);
      registerBlockItem(event, TENOS_LIGHT_SMOOTH_BLOCK);
      registerBlockItem(event, TENOS_GEM_BLOCK);
      registerBlockItem(event, TENOS_PLANKS);
      registerBlockItem(event, TENOS_VERTICAL_PLANKS);
      registerBlockItem(event, TENOS_LOG);
      registerBlockItem(event, TENOS_STRIPPED_LOG);
      registerBlockItem(event, TENOS_BOOKSHELF);
      registerBlockItem(event, TENOS_BOOKSHELF_EMPTY);
      registerBlockItem(event, VELARA_BRICK);
      registerBlockItem(event, VELARA_CHISELED_BRICK);
      registerBlockItem(event, VELARA_DARK_SMOOTH_BLOCK);
      registerBlockItem(event, VELARA_LIGHT_SMOOTH_BLOCK);
      registerBlockItem(event, VELARA_GEM_BLOCK);
      registerBlockItem(event, VELARA_LOG);
      registerBlockItem(event, VELARA_PLANKS);
      registerBlockItem(event, VELARA_VERTICAL_PLANKS);
      registerBlockItem(event, VELARA_PLANKS_STAIRS);
      registerBlockItem(event, VELARA_PLANKS_SLAB);
      registerBlockItem(event, VELARA_STRIPPED_LOG);
      registerBlockItem(event, VELARA_MOSSY_LOG);
      registerBlockItem(event, VELARA_MOSSY_LOG_BLOOMING);
      registerBlockItem(event, VELARA_MOSSY_BLOCK);
      registerBlockItem(event, VELARA_LEAVES);
      registerBlockItem(event, VELARA_BUSH);
      registerBlockItem(event, VELARA_VINE);
      registerBlockItem(event, WENDARR_BRICK);
      registerBlockItem(event, WENDARR_CHISELED_BRICK);
      registerBlockItem(event, WENDARR_DARK_SMOOTH_BLOCK);
      registerBlockItem(event, WENDARR_LIGHT_SMOOTH_BLOCK);
      registerBlockItem(event, WENDARR_GEM_BLOCK);
      registerBlockItem(event, WENDARR_JEWEL_BLOCK);
      registerBlockItem(event, WENDARR_JEWEL_GLASS);
      registerBlockItem(event, WENDARR_JEWEL_GLASS_PANE);
      registerBlockItem(event, ROTTEN_MEAT_BLOCK);
      registerBlockItem(event, LIVING_ROCK_BLOCK_COBBLE);
      registerBlockItem(event, LIVING_ROCK_BLOCK_POLISHED);
      registerBlockItem(event, LIVING_ROCK_BLOCK_STACKED);
      registerBlockItem(event, LIVING_ROCK_BRICKS);
      registerBlockItem(event, MOSSY_LIVING_ROCK_BLOCK_COBBLE);
      registerBlockItem(event, MOSSY_LIVING_ROCK_BLOCK_POLISHED);
      registerBlockItem(event, MOSSY_LIVING_ROCK_BLOCK_STACKED);
      registerBlockItem(event, MOSSY_LIVING_ROCK_BRICKS);
      registerBlockItem(event, MOSSY_BONE_BLOCK);
      registerBlockItem(event, VAULT_MOSS);
      registerBlockItem(event, VAULT_SWEETS_BLOCK);
      registerBlockItem(event, VAULT_SMALL_SWEETS_BLOCK);
      registerBlockItem(event, VAULT_SWEETS, VAULT_SWEETS_ITEM);
      registerBlockItem(event, GILDED_CANDELABRA);
      registerBlockItem(event, WOODEN_PLANKS);
      registerBlockItem(event, WOODEN_SLAB);
      registerBlockItem(event, WOODEN_STAIRS);
      registerBlockItem(event, WOODEN_LOG);
      registerBlockItem(event, STRIPPED_WOODEN_LOG);
      registerBlockItem(event, OVERGROWN_WOODEN_PLANKS);
      registerBlockItem(event, OVERGROWN_WOODEN_SLAB);
      registerBlockItem(event, OVERGROWN_WOODEN_STAIRS);
      registerBlockItem(event, OVERGROWN_WOODEN_LOG);
      registerBlockItem(event, STRIPPED_OVERGROWN_WOODEN_LOG);
      registerBlockItem(event, SANDY_BLOCK);
      registerBlockItem(event, SANDY_BRICKS);
      registerBlockItem(event, SANDY_SMALL_BRICKS);
      registerBlockItem(event, SANDY_BRICKS_CRACKED);
      registerBlockItem(event, SANDY_SMALL_BRICKS_CRACKED);
      registerBlockItem(event, SANDY_BLOCK_POLISHED);
      registerBlockItem(event, SANDY_BLOCK_CHISELED);
      registerBlockItem(event, SANDY_BLOCK_BUMBO);
      registerBlockItem(event, VAULT_STONE_SLAB);
      registerBlockItem(event, VAULT_STONE_STAIRS);
      registerBlockItem(event, CHROMATIC_LOG);
      registerBlockItem(event, STRIPPED_CHROMATIC_LOG);
      registerBlockItem(event, CHROMATIC_PLANKS);
      registerBlockItem(event, CHROMATIC_SLAB);
      registerBlockItem(event, CHROMATIC_STAIRS);
      registerBlockItem(event, DRIFTWOOD_LOG);
      registerBlockItem(event, STRIPPED_DRIFTWOOD_LOG);
      registerBlockItem(event, DRIFTWOOD_PLANKS);
      registerBlockItem(event, DRIFTWOOD_SLAB);
      registerBlockItem(event, DRIFTWOOD_STAIRS);
      registerBlockItem(event, COMPRESSED_SOOT_BLOCK);
      registerBlockItem(event, VAULT_ASH);
      registerBlockItem(event, DRY_VAULT_ASH);
      registerBlockItem(event, FOOLS_GOLD_BLOCK);
      registerBlockItem(event, OFFERING_PILLAR);
      registerBlockItem(event, BARRED_DOOR, new DoubleHighBlockItem(BARRED_DOOR, new Properties().tab(ModItems.VAULT_MOD_GROUP)));
      registerBlockItem(event, BARRED_TRAPDOOR);
   }

   private static void registerBlock(Register<Block> event, Block block, ResourceLocation id) {
      block.setRegistryName(id);
      event.getRegistry().register(block);
   }

   private static <T extends BlockEntity> void registerTileEntity(Register<BlockEntityType<?>> event, BlockEntityType<?> type, ResourceLocation id) {
      type.setRegistryName(id);
      event.getRegistry().register(type);
   }

   private static void registerBlockItemWithEffect(Register<Item> event, Block block, int maxStackSize, Consumer<Properties> adjustProperties) {
      Properties properties = new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize);
      adjustProperties.accept(properties);
      BlockItem blockItem = new BlockItem(block, properties) {
         public boolean isFoil(ItemStack stack) {
            return true;
         }
      };
      registerBlockItem(event, block, blockItem);
   }

   private static void registerBlockItem(Register<Item> event, Block block) {
      registerBlockItem(event, block, 64);
   }

   private static void registerBlockItem(Register<Item> event, Block block, int maxStackSize) {
      registerBlockItem(event, block, maxStackSize, properties -> {});
   }

   private static void registerBlockItem(Register<Item> event, Block block, int maxStackSize, Consumer<Properties> adjustProperties) {
      Properties properties = new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize);
      adjustProperties.accept(properties);
      registerBlockItem(event, block, new BlockItem(block, properties));
   }

   private static void registerBlockItem(Register<Item> event, Block block, BlockItem blockItem) {
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerTallBlockItem(Register<Item> event, Block block) {
      DoubleHighBlockItem tallBlockItem = new DoubleHighBlockItem(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(64));
      tallBlockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(tallBlockItem);
   }
}
