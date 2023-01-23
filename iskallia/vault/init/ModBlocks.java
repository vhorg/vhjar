package iskallia.vault.init;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.VaultMod;
import iskallia.vault.block.AnimalPenBlock;
import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.block.BloodAltarBlock;
import iskallia.vault.block.BountyBlock;
import iskallia.vault.block.CakeBlock;
import iskallia.vault.block.CatalystInfusionBlock;
import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.CrystalBuddingBlock;
import iskallia.vault.block.CrystalClusterBlock;
import iskallia.vault.block.CubeBlock;
import iskallia.vault.block.CustomEntitySpawnerBlock;
import iskallia.vault.block.DemagnetizerBlock;
import iskallia.vault.block.EliteSpawnerBlock;
import iskallia.vault.block.ErrorBlock;
import iskallia.vault.block.EtchingVendorControllerBlock;
import iskallia.vault.block.EternalPedestalBlock;
import iskallia.vault.block.FinalVaultFrameBlock;
import iskallia.vault.block.FloatingTextBlock;
import iskallia.vault.block.HourglassBlock;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.LootStatueUpperBlock;
import iskallia.vault.block.MVPCrownBlock;
import iskallia.vault.block.MagnetTableBlock;
import iskallia.vault.block.MazeBlock;
import iskallia.vault.block.MeatBlock;
import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.OtherSidePortalBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.PylonBlock;
import iskallia.vault.block.RelicPedestalBlock;
import iskallia.vault.block.ScavengerAltarBlock;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.ScavengerTreasureBlock;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.block.SoulAltarBlock;
import iskallia.vault.block.SpiritExtractorBlock;
import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.TimeAltarBlock;
import iskallia.vault.block.ToolViseBlock;
import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.block.TreasureSandBlock;
import iskallia.vault.block.TrophyBlock;
import iskallia.vault.block.VaultAltarBlock;
import iskallia.vault.block.VaultAnvilBlock;
import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.block.VaultArtisanStationBlock;
import iskallia.vault.block.VaultBedrockBlock;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.block.VaultCharmControllerBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDiffuserBlock;
import iskallia.vault.block.VaultForgeBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultRaidControllerBlock;
import iskallia.vault.block.VaultRecyclerBlock;
import iskallia.vault.block.VaultRockBlock;
import iskallia.vault.block.WildSpawnerBlock;
import iskallia.vault.block.XPAltarBlock;
import iskallia.vault.block.discoverable.DiscoverTriggeringBlock;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.AnimalPenTileEntity;
import iskallia.vault.block.entity.BlackMarketTileEntity;
import iskallia.vault.block.entity.BloodAltarTileEntity;
import iskallia.vault.block.entity.BountyTableTileEntity;
import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.block.entity.CoinPilesTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.CrystalBuddingBlockEntity;
import iskallia.vault.block.entity.CubeTileEntity;
import iskallia.vault.block.entity.CustomEntitySpawnerTileEntity;
import iskallia.vault.block.entity.DemagnetizerTileEntity;
import iskallia.vault.block.entity.EliteSpawnerTileEntity;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.block.entity.EternalPedestalTileEntity;
import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.block.entity.FloatingTextTileEntity;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.MagnetTableTile;
import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.block.entity.ObeliskTileEntity;
import iskallia.vault.block.entity.OtherSidePortalTileEntity;
import iskallia.vault.block.entity.PylonTileEntity;
import iskallia.vault.block.entity.RelicPedestalTileEntity;
import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.block.entity.ScavengerTreasureTileEntity;
import iskallia.vault.block.entity.ShopPedestalBlockTile;
import iskallia.vault.block.entity.SoulAltarTileEntity;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.block.entity.TimeAltarTileEntity;
import iskallia.vault.block.entity.ToolViseTile;
import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.block.entity.TreasureDoorTileEntity;
import iskallia.vault.block.entity.TreasureSandTileEntity;
import iskallia.vault.block.entity.TrophyTileEntity;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.block.entity.VaultChampionTrophyTileEntity;
import iskallia.vault.block.entity.VaultCharmControllerTileEntity;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.block.entity.VaultDiffuserTileEntity;
import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import iskallia.vault.block.entity.VaultRecyclerTileEntity;
import iskallia.vault.block.entity.WildSpawnerTileEntity;
import iskallia.vault.block.entity.XpAltarTileEntity;
import iskallia.vault.block.item.FinalVaultFrameBlockItem;
import iskallia.vault.block.item.HourglassBlockItem;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.PlaceholderBlockItem;
import iskallia.vault.block.item.TreasureDoorBlockItem;
import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.block.item.VaultChampionTrophyBlockItem;
import iskallia.vault.block.item.VaultOreBlockItem;
import iskallia.vault.block.render.AnimalPenRenderer;
import iskallia.vault.block.render.BlackMarketRenderer;
import iskallia.vault.block.render.CryoChamberRenderer;
import iskallia.vault.block.render.EternalPedestalRenderer;
import iskallia.vault.block.render.FillableAltarRenderer;
import iskallia.vault.block.render.FinalVaultFrameRenderer;
import iskallia.vault.block.render.FloatingTextRenderer;
import iskallia.vault.block.render.HourglassRenderer;
import iskallia.vault.block.render.LootStatueRenderer;
import iskallia.vault.block.render.MagnetTableRenderer;
import iskallia.vault.block.render.PylonRenderer;
import iskallia.vault.block.render.RelicPedestalRenderer;
import iskallia.vault.block.render.ScavengerAltarRenderer;
import iskallia.vault.block.render.ShopPedestalBlockTileRenderer;
import iskallia.vault.block.render.SpawnerRenderer;
import iskallia.vault.block.render.SpiritExtractorRenderer;
import iskallia.vault.block.render.ToolViseRenderer;
import iskallia.vault.block.render.TrophyRenderer;
import iskallia.vault.block.render.VaultAltarRenderer;
import iskallia.vault.block.render.VaultChampionTrophyRenderer;
import iskallia.vault.block.render.VaultChestRenderer;
import iskallia.vault.block.render.VaultDiffuserRenderer;
import iskallia.vault.block.render.VaultPortalRenderer;
import iskallia.vault.block.render.VaultRaidControllerRenderer;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.fluid.block.VoidFluidBlock;
import iskallia.vault.item.CoinBlockItem;
import iskallia.vault.item.VaultChestBlockItem;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModBlocks {
   public static final ErrorBlock ERROR_BLOCK = new ErrorBlock();
   public static final FloatingTextBlock FLOATING_TEXT = new FloatingTextBlock();
   public static final VaultPortalBlock VAULT_PORTAL = new VaultPortalBlock();
   public static final VaultChampionTrophy VAULT_CHAMPION_TROPHY = new VaultChampionTrophy();
   public static final FinalVaultFrameBlock FINAL_VAULT_FRAME = new FinalVaultFrameBlock();
   public static final OtherSidePortalBlock OTHER_SIDE_PORTAL = new OtherSidePortalBlock();
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
   public static final VaultOreBlock VAULTSTONE_ALEXANDRITE_ORE = new VaultOreBlock(ModItems.ALEXANDRITE_GEM);
   public static final VaultOreBlock VAULTSTONE_ASHIUM_ORE = new VaultOreBlock(ModItems.ASHIUM_GEM);
   public static final VaultOreBlock VAULTSTONE_BENITOITE_ORE = new VaultOreBlock(ModItems.BENITOITE_GEM);
   public static final VaultOreBlock VAULTSTONE_BLACK_OPAL_ORE = new VaultOreBlock(ModItems.BLACK_OPAL_GEM);
   public static final VaultOreBlock VAULTSTONE_BOMIGNITE_ORE = new VaultOreBlock(ModItems.BOMIGNITE_GEM);
   public static final VaultOreBlock VAULTSTONE_ECHO_ORE = new VaultOreBlock(ModItems.ECHO_GEM);
   public static final VaultOreBlock VAULTSTONE_GORGINITE_ORE = new VaultOreBlock(ModItems.GORGINITE_GEM);
   public static final VaultOreBlock VAULTSTONE_ISKALLIUM_ORE = new VaultOreBlock(ModItems.ISKALLIUM_GEM);
   public static final VaultOreBlock VAULTSTONE_LARIMAR_ORE = new VaultOreBlock(ModItems.LARIMAR_GEM);
   public static final VaultOreBlock VAULTSTONE_PAINITE_ORE = new VaultOreBlock(ModItems.PAINITE_GEM);
   public static final VaultOreBlock VAULTSTONE_PETZANITE_ORE = new VaultOreBlock(ModItems.PETZANITE_GEM);
   public static final VaultOreBlock VAULTSTONE_SPARKLETINE_ORE = new VaultOreBlock(ModItems.SPARKLETINE_GEM);
   public static final VaultOreBlock VAULTSTONE_TUBIUM_ORE = new VaultOreBlock(ModItems.TUBIUM_GEM);
   public static final VaultOreBlock VAULTSTONE_UPALINE_ORE = new VaultOreBlock(ModItems.UPALINE_GEM);
   public static final VaultOreBlock VAULTSTONE_WUTODIE_ORE = new VaultOreBlock(ModItems.WUTODIE_GEM);
   public static final VaultOreBlock VAULTSTONE_XENIUM_ORE = new VaultOreBlock(ModItems.XENIUM_GEM);
   public static final VaultRockBlock VAULT_ROCK_ORE = new VaultRockBlock();
   public static final TreasureDoorBlock TREASURE_DOOR = new TreasureDoorBlock();
   public static final VaultArtifactBlock VAULT_ARTIFACT = new VaultArtifactBlock();
   public static final VaultCrateBlock VAULT_CRATE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_CAKE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_ARENA = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_SCAVENGER = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_CHAMPION = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_BOUNTY = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_MONOLITH = new VaultCrateBlock();
   public static final ObeliskBlock OBELISK = new ObeliskBlock();
   public static final MonolithBlock MONOLITH = new MonolithBlock();
   public static final PylonBlock PYLON = new PylonBlock();
   public static final MVPCrownBlock MVP_CROWN = new MVPCrownBlock();
   public static final VaultBedrockBlock VAULT_BEDROCK = new VaultBedrockBlock();
   public static final Block VAULT_STONE = new Block(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).isValidSpawn((a, b, c, d) -> false)
   );
   public static final Block CHISELED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final Block POLISHED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
   public static final Block BUMBO_POLISHED_VAULT_STONE = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(VAULT_STONE));
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
   public static final CryoChamberBlock CRYO_CHAMBER = new CryoChamberBlock();
   public static final Block VAULT_DIAMOND_BLOCK = new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK));
   public static final MazeBlock MAZE_BLOCK = new MazeBlock();
   public static final TrophyBlock TROPHY_STATUE = new TrophyBlock();
   public static final TransmogTableBlock TRANSMOG_TABLE = new TransmogTableBlock();
   public static final VaultForgeBlock VAULT_FORGE = new VaultForgeBlock();
   public static final VaultArtisanStationBlock VAULT_ARTISAN_STATION = new VaultArtisanStationBlock();
   public static final VaultRecyclerBlock VAULT_RECYCLER = new VaultRecyclerBlock();
   public static final VaultDiffuserBlock VAULT_DIFFUSER = new VaultDiffuserBlock();
   public static final Block WOODEN_CHEST = new VaultChestBlock(
      VaultChestType.WOODEN, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(40.0F, 5.0F)
   );
   public static final Block GILDED_CHEST = new VaultChestBlock(
      VaultChestType.GILDED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(2.4F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block LIVING_CHEST = new VaultChestBlock(
      VaultChestType.LIVING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.4F, 5.0F)
   );
   public static final Block ORNATE_CHEST = new VaultChestBlock(
      VaultChestType.ORNATE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.4F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block TREASURE_CHEST = new VaultChestBlock(
      VaultChestType.TREASURE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(-1.0F, 5.0F).sound(SoundType.METAL)
   );
   public static final Block ALTAR_CHEST = new VaultChestBlock(
      VaultChestType.ALTAR, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(-1.0F, 5.0F).sound(SoundType.STONE)
   );
   public static final Block WOODEN_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.WOODEN, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F)
   );
   public static final Block GILDED_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.GILDED, net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.STONE).strength(2.5F).sound(SoundType.STONE)
   );
   public static final Block LIVING_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.LIVING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F)
   );
   public static final Block ORNATE_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.ORNATE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F).sound(SoundType.STONE)
   );
   public static final Block TREASURE_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.TREASURE, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F).sound(SoundType.METAL)
   );
   public static final Block ALTAR_CHEST_PLACEABLE = new VaultChestBlock(
      VaultChestType.ALTAR, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F).sound(SoundType.STONE)
   );
   public static final XPAltarBlock XP_ALTAR = new XPAltarBlock();
   public static final BloodAltarBlock BLOOD_ALTAR = new BloodAltarBlock();
   public static final TimeAltarBlock TIME_ALTAR = new TimeAltarBlock();
   public static final SoulAltarBlock SOUL_ALTAR = new SoulAltarBlock();
   public static final HourglassBlock HOURGLASS = new HourglassBlock();
   public static final ScavengerChestBlock SCAVENGER_CHEST = new ScavengerChestBlock(
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.CHEST).strength(-1.0F, 3600000.0F)
   );
   public static final ScavengerAltarBlock SCAVENGER_ALTAR = new ScavengerAltarBlock();
   public static final ScavengerTreasureBlock SCAVENGER_TREASURE = new ScavengerTreasureBlock();
   public static final StabilizerBlock STABILIZER = new StabilizerBlock();
   public static final CatalystInfusionBlock CATALYST_INFUSION_TABLE = new CatalystInfusionBlock();
   public static final EtchingVendorControllerBlock ETCHING_CONTROLLER_BLOCK = new EtchingVendorControllerBlock();
   public static final VaultCharmControllerBlock VAULT_CHARM_CONTROLLER_BLOCK = new VaultCharmControllerBlock();
   public static final VaultRaidControllerBlock RAID_CONTROLLER_BLOCK = new VaultRaidControllerBlock();
   public static final PlaceholderBlock PLACEHOLDER = new PlaceholderBlock();
   public static final CoinPileBlock COIN_PILE = new CoinPileBlock();
   public static final CoinPileDecorBlock BRONZE_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock SILVER_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock GOLD_COIN_PILE = new CoinPileDecorBlock();
   public static final CoinPileDecorBlock PLATINUM_COIN_PILE = new CoinPileDecorBlock();
   public static final ToolViseBlock TOOL_VISE = new ToolViseBlock();
   public static final MagnetTableBlock MAGNET_TABLE = new MagnetTableBlock();
   public static final DemagnetizerBlock DEMAGNETIZER_BLOCK = new DemagnetizerBlock();
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
   public static final Block VAULT_MEAT_BLOCK = new MeatBlock();
   public static final Block PACKED_VAULT_MEAT_BLOCK = new MeatBlock();
   public static final TreasureSandBlock TREASURE_SAND = new TreasureSandBlock();
   public static final EliteSpawnerBlock ELITE_SPAWNER = new EliteSpawnerBlock();
   public static final WildSpawnerBlock WILD_SPAWNER = new WildSpawnerBlock();
   public static final CustomEntitySpawnerBlock CUSTOM_ENTITY_SPAWNER = new CustomEntitySpawnerBlock();
   public static final SpiritExtractorBlock SPIRIT_EXTRACTOR = new SpiritExtractorBlock();
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
   public static final SlabBlock VAULT_STONE_SLAB = new SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_SLAB));
   public static final StairBlock VAULT_STONE_STAIRS = new StairBlock(
      VAULT_STONE::defaultBlockState, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS)
   );
   public static final LiquidBlock VOID_LIQUID_BLOCK = new VoidFluidBlock(
      ModFluids.VOID_LIQUID,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(Material.WATER, MaterialColor.COLOR_BLACK)
         .noCollission()
         .randomTicks()
         .strength(100.0F)
         .noDrops()
   );
   public static final VaultChampionTrophyBlockItem VAULT_CHAMPION_TROPHY_BLOCK_ITEM = new VaultChampionTrophyBlockItem(VAULT_CHAMPION_TROPHY);
   public static final FinalVaultFrameBlockItem FINAL_VAULT_FRAME_BLOCK_ITEM = new FinalVaultFrameBlockItem(FINAL_VAULT_FRAME);
   public static final LootStatueBlockItem LOOT_STATUE_ITEM = new LootStatueBlockItem(LOOT_STATUE);
   public static final TrophyStatueBlockItem TROPHY_STATUE_BLOCK_ITEM = new TrophyStatueBlockItem(TROPHY_STATUE);
   public static final BlockItem WOODEN_CHEST_ITEM = new VaultChestBlockItem(WOODEN_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_CHEST_ITEM = new VaultChestBlockItem(GILDED_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_CHEST_ITEM = new VaultChestBlockItem(LIVING_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_CHEST_ITEM = new VaultChestBlockItem(ORNATE_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem TREASURE_CHEST_ITEM = new VaultChestBlockItem(TREASURE_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ALTAR_CHEST_ITEM = new VaultChestBlockItem(ALTAR_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem WOODEN_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(WOODEN_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem GILDED_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(GILDED_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem LIVING_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(LIVING_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem ORNATE_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(ORNATE_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem TREASURE_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(
      TREASURE_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP)
   );
   public static final BlockItem ALTAR_CHEST_ITEM_PLACEABLE = new VaultChestBlockItem(ALTAR_CHEST_PLACEABLE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem SCAVENGER_CHEST_ITEM = new VaultChestBlockItem(SCAVENGER_CHEST, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockItem SCAVENGER_ALTAR_ITEM = new BlockItem(SCAVENGER_ALTAR, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_BRONZE = new CoinBlockItem(BRONZE_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_SILVER = new CoinBlockItem(SILVER_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_GOLD = new CoinBlockItem(GOLD_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static BlockItem VAULT_PLATINUM = new CoinBlockItem(PLATINUM_COIN_PILE, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   public static final BlockEntityType<FloatingTextTileEntity> FLOATING_TEXT_TILE_ENTITY = Builder.of(FloatingTextTileEntity::new, new Block[]{FLOATING_TEXT})
      .build(null);
   public static final BlockEntityType<VaultAltarTileEntity> VAULT_ALTAR_TILE_ENTITY = Builder.of(VaultAltarTileEntity::new, new Block[]{VAULT_ALTAR})
      .build(null);
   public static final BlockEntityType<VaultChampionTrophyTileEntity> VAULT_CHAMPION_TROPHY_TILE_ENTITY = Builder.of(
         VaultChampionTrophyTileEntity::new, new Block[]{VAULT_CHAMPION_TROPHY}
      )
      .build(null);
   public static final BlockEntityType<FinalVaultFrameTileEntity> FINAL_VAULT_FRAME_TILE_ENTITY = Builder.of(
         FinalVaultFrameTileEntity::new, new Block[]{FINAL_VAULT_FRAME}
      )
      .build(null);
   public static final BlockEntityType<VaultCrateTileEntity> VAULT_CRATE_TILE_ENTITY = Builder.of(
         VaultCrateTileEntity::new,
         new Block[]{
            VAULT_CRATE,
            VAULT_CRATE_CAKE,
            VAULT_CRATE_ARENA,
            VAULT_CRATE_SCAVENGER,
            VAULT_CRATE_CHAMPION,
            VAULT_CRATE_BOUNTY,
            VAULT_CRATE,
            VAULT_CRATE_MONOLITH
         }
      )
      .build(null);
   public static final BlockEntityType<VaultPortalTileEntity> VAULT_PORTAL_TILE_ENTITY = Builder.of(VaultPortalTileEntity::new, new Block[]{VAULT_PORTAL})
      .build(null);
   public static final BlockEntityType<OtherSidePortalTileEntity> OTHER_SIDE_PORTAL_TILE_ENTITY = Builder.of(
         OtherSidePortalTileEntity::new, new Block[]{OTHER_SIDE_PORTAL}
      )
      .build(null);
   public static final BlockEntityType<RelicPedestalTileEntity> RELIC_STATUE_TILE_ENTITY = Builder.of(RelicPedestalTileEntity::new, new Block[]{RELIC_PEDESTAL})
      .build(null);
   public static final BlockEntityType<LootStatueTileEntity> LOOT_STATUE_TILE_ENTITY = Builder.of(LootStatueTileEntity::new, new Block[]{LOOT_STATUE})
      .build(null);
   public static final BlockEntityType<ShopPedestalBlockTile> SHOP_PEDESTAL_TILE_ENTITY = Builder.of(ShopPedestalBlockTile::new, new Block[]{SHOP_PEDESTAL})
      .build(null);
   public static final BlockEntityType<TrophyTileEntity> TROPHY_STATUE_TILE_ENTITY = Builder.of(TrophyTileEntity::new, new Block[]{TROPHY_STATUE}).build(null);
   public static final BlockEntityType<CryoChamberTileEntity> CRYO_CHAMBER_TILE_ENTITY = Builder.of(CryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER})
      .build(null);
   public static final BlockEntityType<AncientCryoChamberTileEntity> ANCIENT_CRYO_CHAMBER_TILE_ENTITY = Builder.of(
         AncientCryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .build(null);
   public static final BlockEntityType<TreasureDoorTileEntity> TREASURE_DOOR_TILE_ENTITY = Builder.of(TreasureDoorTileEntity::new, new Block[]{TREASURE_DOOR})
      .build(null);
   public static final BlockEntityType<VaultChestTileEntity> VAULT_CHEST_TILE_ENTITY = Builder.of(
         VaultChestTileEntity::new,
         new Block[]{
            WOODEN_CHEST,
            ALTAR_CHEST,
            GILDED_CHEST,
            LIVING_CHEST,
            ORNATE_CHEST,
            TREASURE_CHEST,
            WOODEN_CHEST_PLACEABLE,
            ALTAR_CHEST_PLACEABLE,
            GILDED_CHEST_PLACEABLE,
            LIVING_CHEST_PLACEABLE,
            ORNATE_CHEST_PLACEABLE,
            TREASURE_CHEST_PLACEABLE
         }
      )
      .build(null);
   public static final BlockEntityType<XpAltarTileEntity> XP_ALTAR_TILE_ENTITY = Builder.of(XpAltarTileEntity::new, new Block[]{XP_ALTAR}).build(null);
   public static final BlockEntityType<BloodAltarTileEntity> BLOOD_ALTAR_TILE_ENTITY = Builder.of(BloodAltarTileEntity::new, new Block[]{BLOOD_ALTAR})
      .build(null);
   public static final BlockEntityType<TimeAltarTileEntity> TIME_ALTAR_TILE_ENTITY = Builder.of(TimeAltarTileEntity::new, new Block[]{TIME_ALTAR}).build(null);
   public static final BlockEntityType<SoulAltarTileEntity> SOUL_ALTAR_TILE_ENTITY = Builder.of(SoulAltarTileEntity::new, new Block[]{SOUL_ALTAR}).build(null);
   public static final BlockEntityType<ObeliskTileEntity> OBELISK_TILE_ENTITY = Builder.of(ObeliskTileEntity::new, new Block[]{OBELISK}).build(null);
   public static final BlockEntityType<MonolithTileEntity> MONOLITH_TILE_ENTITY = Builder.of(MonolithTileEntity::new, new Block[]{MONOLITH}).build(null);
   public static final BlockEntityType<PylonTileEntity> PYLON_TILE_ENTITY = Builder.of(PylonTileEntity::new, new Block[]{PYLON}).build(null);
   public static final BlockEntityType<HourglassTileEntity> HOURGLASS_TILE_ENTITY = Builder.of(HourglassTileEntity::new, new Block[]{HOURGLASS}).build(null);
   public static final BlockEntityType<ScavengerChestTileEntity> SCAVENGER_CHEST_TILE_ENTITY = Builder.of(
         ScavengerChestTileEntity::new, new Block[]{SCAVENGER_CHEST}
      )
      .build(null);
   public static final BlockEntityType<ScavengerAltarTileEntity> SCAVENGER_ALTAR_TILE_ENTITY = Builder.of(
         ScavengerAltarTileEntity::new, new Block[]{SCAVENGER_ALTAR}
      )
      .build(null);
   public static final BlockEntityType<ScavengerTreasureTileEntity> SCAVENGER_TREASURE_TILE_ENTITY = Builder.of(
         ScavengerTreasureTileEntity::new, new Block[]{SCAVENGER_TREASURE}
      )
      .build(null);
   public static final BlockEntityType<StabilizerTileEntity> STABILIZER_TILE_ENTITY = Builder.of(StabilizerTileEntity::new, new Block[]{STABILIZER})
      .build(null);
   public static final BlockEntityType<CatalystInfusionTableTileEntity> CATALYST_INFUSION_TABLE_TILE_ENTITY = Builder.of(
         CatalystInfusionTableTileEntity::new, new Block[]{CATALYST_INFUSION_TABLE}
      )
      .build(null);
   public static final BlockEntityType<EtchingVendorControllerTileEntity> ETCHING_CONTROLLER_TILE_ENTITY = Builder.of(
         EtchingVendorControllerTileEntity::new, new Block[]{ETCHING_CONTROLLER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<VaultCharmControllerTileEntity> VAULT_CHARM_CONTROLLER_TILE_ENTITY = Builder.of(
         VaultCharmControllerTileEntity::new, new Block[]{VAULT_CHARM_CONTROLLER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<VaultRaidControllerTileEntity> RAID_CONTROLLER_TILE_ENTITY = Builder.of(
         VaultRaidControllerTileEntity::new, new Block[]{RAID_CONTROLLER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<ToolViseTile> TOOL_VISE_TILE_ENTITY = Builder.of(ToolViseTile::new, new Block[]{TOOL_VISE}).build(null);
   public static final BlockEntityType<MagnetTableTile> MAGNET_TABLE_TILE_ENTITY = Builder.of(MagnetTableTile::new, new Block[]{MAGNET_TABLE}).build(null);
   public static final BlockEntityType<DemagnetizerTileEntity> DEMAGNETIZER_TILE_ENTITY = Builder.of(
         DemagnetizerTileEntity::new, new Block[]{DEMAGNETIZER_BLOCK}
      )
      .build(null);
   public static final BlockEntityType<CoinPilesTileEntity> COIN_PILE_TILE = Builder.of(CoinPilesTileEntity::new, new Block[]{COIN_PILE}).build(null);
   public static final BlockEntityType<TreasureSandTileEntity> TREASURE_SAND_TILE_ENTITY = Builder.of(TreasureSandTileEntity::new, new Block[]{TREASURE_SAND})
      .build(null);
   public static final BlockEntityType<VaultForgeTileEntity> VAULT_FORGE_TILE_ENTITY = Builder.of(VaultForgeTileEntity::new, new Block[]{VAULT_FORGE})
      .build(null);
   public static final BlockEntityType<VaultArtisanStationTileEntity> VAULT_ARTISAN_STATION_ENTITY = Builder.of(
         VaultArtisanStationTileEntity::new, new Block[]{VAULT_ARTISAN_STATION}
      )
      .build(null);
   public static final BlockEntityType<VaultRecyclerTileEntity> VAULT_RECYCLER_ENTITY = Builder.of(VaultRecyclerTileEntity::new, new Block[]{VAULT_RECYCLER})
      .build(null);
   public static final BlockEntityType<VaultDiffuserTileEntity> VAULT_DIFFUSER_ENTITY = Builder.of(VaultDiffuserTileEntity::new, new Block[]{VAULT_DIFFUSER})
      .build(null);
   public static final BlockEntityType<AnimalPenTileEntity> ANIMAL_PEN_ENTITY = Builder.of(AnimalPenTileEntity::new, new Block[]{ANIMAL_PEN}).build(null);
   public static final BlockEntityType<EliteSpawnerTileEntity> ELITE_SPAWNER_TILE_ENTITY = Builder.of(EliteSpawnerTileEntity::new, new Block[]{ELITE_SPAWNER})
      .build(null);
   public static final BlockEntityType<WildSpawnerTileEntity> WILD_SPAWNER_TILE_ENTITY = Builder.of(WildSpawnerTileEntity::new, new Block[]{WILD_SPAWNER})
      .build(null);
   public static final BlockEntityType<CustomEntitySpawnerTileEntity> CUSTOM_ENTITY_SPAWNER_TILE_ENTITY = Builder.of(
         CustomEntitySpawnerTileEntity::new, new Block[]{CUSTOM_ENTITY_SPAWNER}
      )
      .build(null);
   public static final BlockEntityType<CubeTileEntity> CUBE_BLOCK_TILE_ENTITY = Builder.of(CubeTileEntity::new, new Block[]{CUBE_BLOCK}).build(null);
   public static final BlockEntityType<SpiritExtractorTileEntity> SPIRIT_EXTRACTOR_TILE_ENTITY = Builder.of(
         SpiritExtractorTileEntity::new, new Block[]{SPIRIT_EXTRACTOR}
      )
      .build(null);
   public static final BlockEntityType<BountyTableTileEntity> BOUNTY_TABLE_TILE_ENTITY = Builder.of(BountyTableTileEntity::new, new Block[]{BOUNTY_BLOCK})
      .build(null);
   public static final BlockEntityType<CrystalBuddingBlockEntity> CRYSTAL_BUDDING_TILE_ENTITY = Builder.of(
         CrystalBuddingBlockEntity::new, new Block[]{CRYSTAL_BUDDING}
      )
      .build(null);
   public static final BlockEntityType<BlackMarketTileEntity> BLACK_MARKET_TILE_ENTITY = Builder.of(BlackMarketTileEntity::new, new Block[]{BLACK_MARKET})
      .build(null);
   public static final BlockEntityType<TransmogTableTileEntity> TRANSMOG_TABLE_TILE_ENTITY = Builder.of(
         TransmogTableTileEntity::new, new Block[]{TRANSMOG_TABLE}
      )
      .build(null);
   public static final BlockEntityType<EternalPedestalTileEntity> ETERNAL_PEDESTAL_TILE_ENTITY = Builder.of(
         EternalPedestalTileEntity::new, new Block[]{ETERNAL_PEDESTAL}
      )
      .build(null);

   public static void registerBlocks(Register<Block> event) {
      registerBlock(event, ERROR_BLOCK, VaultMod.id("error_block"));
      registerBlock(event, FLOATING_TEXT, VaultMod.id("floating_text"));
      registerBlock(event, VAULT_PORTAL, VaultMod.id("vault_portal"));
      registerBlock(event, VAULT_CHAMPION_TROPHY, VaultMod.id("vault_champion_trophy"));
      registerBlock(event, FINAL_VAULT_FRAME, VaultMod.id("final_vault_frame"));
      registerBlock(event, OTHER_SIDE_PORTAL, VaultMod.id("other_side_portal"));
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
      registerBlock(event, VAULTSTONE_ALEXANDRITE_ORE, VaultMod.id("vaultstone_ore_alexandrite"));
      registerBlock(event, VAULTSTONE_ASHIUM_ORE, VaultMod.id("vaultstone_ore_ashium"));
      registerBlock(event, VAULTSTONE_BENITOITE_ORE, VaultMod.id("vaultstone_ore_benitoite"));
      registerBlock(event, VAULTSTONE_BLACK_OPAL_ORE, VaultMod.id("vaultstone_ore_black_opal"));
      registerBlock(event, VAULTSTONE_BOMIGNITE_ORE, VaultMod.id("vaultstone_ore_bomignite"));
      registerBlock(event, VAULTSTONE_ECHO_ORE, VaultMod.id("vaultstone_ore_echo"));
      registerBlock(event, VAULTSTONE_GORGINITE_ORE, VaultMod.id("vaultstone_ore_gorginite"));
      registerBlock(event, VAULTSTONE_ISKALLIUM_ORE, VaultMod.id("vaultstone_ore_iskallium"));
      registerBlock(event, VAULTSTONE_LARIMAR_ORE, VaultMod.id("vaultstone_ore_larimar"));
      registerBlock(event, VAULTSTONE_PAINITE_ORE, VaultMod.id("vaultstone_ore_painite"));
      registerBlock(event, VAULTSTONE_PETZANITE_ORE, VaultMod.id("vaultstone_ore_petzanite"));
      registerBlock(event, VAULTSTONE_SPARKLETINE_ORE, VaultMod.id("vaultstone_ore_sparkletine"));
      registerBlock(event, VAULTSTONE_TUBIUM_ORE, VaultMod.id("vaultstone_ore_tubium"));
      registerBlock(event, VAULTSTONE_UPALINE_ORE, VaultMod.id("vaultstone_ore_upaline"));
      registerBlock(event, VAULTSTONE_WUTODIE_ORE, VaultMod.id("vaultstone_ore_wutodie"));
      registerBlock(event, VAULTSTONE_XENIUM_ORE, VaultMod.id("vaultstone_ore_xenium"));
      registerBlock(event, VAULT_ROCK_ORE, VaultMod.id("ore_vault_rock"));
      registerBlock(event, TREASURE_DOOR, VaultMod.id("treasure_door"));
      registerBlock(event, VAULT_ARTIFACT, VaultMod.id("vault_artifact"));
      registerBlock(event, VAULT_CRATE, VaultMod.id("vault_crate"));
      registerBlock(event, VAULT_CRATE_CAKE, VaultMod.id("vault_crate_cake"));
      registerBlock(event, VAULT_CRATE_ARENA, VaultMod.id("vault_crate_arena"));
      registerBlock(event, VAULT_CRATE_SCAVENGER, VaultMod.id("vault_crate_scavenger"));
      registerBlock(event, VAULT_CRATE_CHAMPION, VaultMod.id("vault_crate_champion"));
      registerBlock(event, VAULT_CRATE_BOUNTY, VaultMod.id("vault_crate_bounty"));
      registerBlock(event, VAULT_CRATE_MONOLITH, VaultMod.id("vault_crate_monolith"));
      registerBlock(event, OBELISK, VaultMod.id("obelisk"));
      registerBlock(event, MONOLITH, VaultMod.id("monolith"));
      registerBlock(event, PYLON, VaultMod.id("pylon"));
      registerBlock(event, MVP_CROWN, VaultMod.id("mvp_crown"));
      registerBlock(event, VAULT_BEDROCK, VaultMod.id("vault_bedrock"));
      registerBlock(event, VAULT_STONE, VaultMod.id("vault_stone"));
      registerBlock(event, CHISELED_VAULT_STONE, VaultMod.id("chiseled_vault_stone"));
      registerBlock(event, POLISHED_VAULT_STONE, VaultMod.id("polished_vault_stone"));
      registerBlock(event, BUMBO_POLISHED_VAULT_STONE, VaultMod.id("bumbo_polished_vault_stone"));
      registerBlock(event, VAULT_COBBLESTONE, VaultMod.id("vault_cobblestone"));
      registerBlock(event, VAULT_GLASS, VaultMod.id("vault_glass"));
      registerBlock(event, RELIC_PEDESTAL, VaultMod.id("relic_pedestal"));
      registerBlock(event, LOOT_STATUE, VaultMod.id("loot_statue"));
      registerBlock(event, LOOT_STATUE_UPPER, VaultMod.id("loot_statue_upper"));
      registerBlock(event, SHOP_PEDESTAL, VaultMod.id("shop_pedestal"));
      registerBlock(event, CRYO_CHAMBER, VaultMod.id("cryo_chamber"));
      registerBlock(event, VAULT_DIAMOND_BLOCK, VaultMod.id("vault_diamond_block"));
      registerBlock(event, MAZE_BLOCK, VaultMod.id("maze_block"));
      registerBlock(event, TROPHY_STATUE, VaultMod.id("trophy_statue"));
      registerBlock(event, TRANSMOG_TABLE, VaultMod.id("transmog_table"));
      registerBlock(event, VAULT_FORGE, VaultMod.id("vault_forge"));
      registerBlock(event, VAULT_ARTISAN_STATION, VaultMod.id("vault_artisan_station"));
      registerBlock(event, VAULT_RECYCLER, VaultMod.id("vault_recycler"));
      registerBlock(event, VAULT_DIFFUSER, VaultMod.id("vault_diffuser"));
      registerBlock(event, WOODEN_CHEST, VaultMod.id("wooden_chest"));
      registerBlock(event, GILDED_CHEST, VaultMod.id("gilded_chest"));
      registerBlock(event, LIVING_CHEST, VaultMod.id("living_chest"));
      registerBlock(event, ORNATE_CHEST, VaultMod.id("ornate_chest"));
      registerBlock(event, TREASURE_CHEST, VaultMod.id("treasure_chest"));
      registerBlock(event, ALTAR_CHEST, VaultMod.id("altar_chest"));
      registerBlock(event, WOODEN_CHEST_PLACEABLE, VaultMod.id("wooden_chest_placeable"));
      registerBlock(event, GILDED_CHEST_PLACEABLE, VaultMod.id("gilded_chest_placeable"));
      registerBlock(event, LIVING_CHEST_PLACEABLE, VaultMod.id("living_chest_placeable"));
      registerBlock(event, ORNATE_CHEST_PLACEABLE, VaultMod.id("ornate_chest_placeable"));
      registerBlock(event, TREASURE_CHEST_PLACEABLE, VaultMod.id("treasure_chest_placeable"));
      registerBlock(event, ALTAR_CHEST_PLACEABLE, VaultMod.id("altar_chest_placeable"));
      registerBlock(event, XP_ALTAR, VaultMod.id("xp_altar"));
      registerBlock(event, BLOOD_ALTAR, VaultMod.id("blood_altar"));
      registerBlock(event, TIME_ALTAR, VaultMod.id("time_altar"));
      registerBlock(event, SOUL_ALTAR, VaultMod.id("soul_altar"));
      registerBlock(event, VOID_LIQUID_BLOCK, VaultMod.id("void_liquid"));
      registerBlock(event, HOURGLASS, VaultMod.id("hourglass"));
      registerBlock(event, SCAVENGER_CHEST, VaultMod.id("scavenger_chest"));
      registerBlock(event, SCAVENGER_ALTAR, VaultMod.id("scavenger_altar"));
      registerBlock(event, SCAVENGER_TREASURE, VaultMod.id("scavenger_treasure"));
      registerBlock(event, STABILIZER, VaultMod.id("stabilizer"));
      registerBlock(event, CATALYST_INFUSION_TABLE, VaultMod.id("catalyst_infusion_table"));
      registerBlock(event, ETCHING_CONTROLLER_BLOCK, VaultMod.id("etching_vendor_controller"));
      registerBlock(event, VAULT_CHARM_CONTROLLER_BLOCK, VaultMod.id("vault_charm_controller"));
      registerBlock(event, RAID_CONTROLLER_BLOCK, VaultMod.id("raid_controller"));
      registerBlock(event, PLACEHOLDER, VaultMod.id("placeholder"));
      registerBlock(event, COIN_PILE, VaultMod.id("coin_pile"));
      registerBlock(event, BRONZE_COIN_PILE, VaultMod.id("vault_bronze"));
      registerBlock(event, SILVER_COIN_PILE, VaultMod.id("vault_silver"));
      registerBlock(event, GOLD_COIN_PILE, VaultMod.id("vault_gold"));
      registerBlock(event, PLATINUM_COIN_PILE, VaultMod.id("vault_platinum"));
      registerBlock(event, TOOL_VISE, VaultMod.id("tool_vise"));
      registerBlock(event, MAGNET_TABLE, VaultMod.id("magnet_modification_table"));
      registerBlock(event, DEMAGNETIZER_BLOCK, VaultMod.id("demagnetizer"));
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
      registerBlock(event, CAKE, VaultMod.id("cake"));
      registerBlock(event, MAGIC_SILK_BLOCK, VaultMod.id("magic_silk_block"));
      registerBlock(event, YELLOW_PUZZLE_CONCRETE, VaultMod.id("yellow_puzzle_concrete"));
      registerBlock(event, PINK_PUZZLE_CONCRETE, VaultMod.id("pink_puzzle_concrete"));
      registerBlock(event, GREEN_PUZZLE_CONCRETE, VaultMod.id("green_puzzle_concrete"));
      registerBlock(event, BLUE_PUZZLE_CONCRETE, VaultMod.id("blue_puzzle_concrete"));
      registerBlock(event, SUGAR_PLUM_FAIRY_FLOWER, VaultMod.id("sugar_plum_fairy_flower"));
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
   }

   public static void registerTileEntities(Register<BlockEntityType<?>> event) {
      registerTileEntity(event, FLOATING_TEXT_TILE_ENTITY, VaultMod.id("floating_text_tile_entity"));
      registerTileEntity(event, VAULT_ALTAR_TILE_ENTITY, VaultMod.id("vault_altar_tile_entity"));
      registerTileEntity(event, VAULT_CHAMPION_TROPHY_TILE_ENTITY, VaultMod.id("vault_champion_trophy_tile_entity"));
      registerTileEntity(event, FINAL_VAULT_FRAME_TILE_ENTITY, VaultMod.id("final_vault_frame_tile_entity"));
      registerTileEntity(event, VAULT_CRATE_TILE_ENTITY, VaultMod.id("vault_crate_tile_entity"));
      registerTileEntity(event, VAULT_PORTAL_TILE_ENTITY, VaultMod.id("vault_portal_tile_entity"));
      registerTileEntity(event, OTHER_SIDE_PORTAL_TILE_ENTITY, VaultMod.id("other_side_portal_tile_entity"));
      registerTileEntity(event, RELIC_STATUE_TILE_ENTITY, VaultMod.id("relic_statue_tile_entity"));
      registerTileEntity(event, LOOT_STATUE_TILE_ENTITY, VaultMod.id("loot_statue_tile_entity"));
      registerTileEntity(event, SHOP_PEDESTAL_TILE_ENTITY, VaultMod.id("shop_pedestal_tile_entity"));
      registerTileEntity(event, TROPHY_STATUE_TILE_ENTITY, VaultMod.id("trophy_statue_tile_entity"));
      registerTileEntity(event, CRYO_CHAMBER_TILE_ENTITY, VaultMod.id("cryo_chamber_tile_entity"));
      registerTileEntity(event, ANCIENT_CRYO_CHAMBER_TILE_ENTITY, VaultMod.id("ancient_cryo_chamber_tile_entity"));
      registerTileEntity(event, TREASURE_DOOR_TILE_ENTITY, VaultMod.id("treasure_door_tile_entity"));
      registerTileEntity(event, VAULT_CHEST_TILE_ENTITY, VaultMod.id("vault_chest_tile_entity"));
      registerTileEntity(event, XP_ALTAR_TILE_ENTITY, VaultMod.id("xp_altar_tile_entity"));
      registerTileEntity(event, BLOOD_ALTAR_TILE_ENTITY, VaultMod.id("blood_altar_tile_entity"));
      registerTileEntity(event, TIME_ALTAR_TILE_ENTITY, VaultMod.id("time_altar_tile_entity"));
      registerTileEntity(event, SOUL_ALTAR_TILE_ENTITY, VaultMod.id("soul_altar_tile_entity"));
      registerTileEntity(event, OBELISK_TILE_ENTITY, VaultMod.id("obelisk_tile_entity"));
      registerTileEntity(event, MONOLITH_TILE_ENTITY, VaultMod.id("monolith_tile_entity"));
      registerTileEntity(event, PYLON_TILE_ENTITY, VaultMod.id("pylon_tile_entity"));
      registerTileEntity(event, HOURGLASS_TILE_ENTITY, VaultMod.id("hourglass_tile_entity"));
      registerTileEntity(event, SCAVENGER_CHEST_TILE_ENTITY, VaultMod.id("scavenger_chest_tile_entity"));
      registerTileEntity(event, SCAVENGER_ALTAR_TILE_ENTITY, VaultMod.id("scavenger_altar_tile_entity"));
      registerTileEntity(event, SCAVENGER_TREASURE_TILE_ENTITY, VaultMod.id("scavenger_treasure_tile_entity"));
      registerTileEntity(event, STABILIZER_TILE_ENTITY, VaultMod.id("stabilizer_tile_entity"));
      registerTileEntity(event, CATALYST_INFUSION_TABLE_TILE_ENTITY, VaultMod.id("catalyst_infusion_table_tile_entity"));
      registerTileEntity(event, ETCHING_CONTROLLER_TILE_ENTITY, VaultMod.id("etching_vendor_controller_tile_entity"));
      registerTileEntity(event, VAULT_CHARM_CONTROLLER_TILE_ENTITY, VaultMod.id("vault_charm_controller_tile_entity"));
      registerTileEntity(event, RAID_CONTROLLER_TILE_ENTITY, VaultMod.id("raid_controller_tile_entity"));
      registerTileEntity(event, TOOL_VISE_TILE_ENTITY, VaultMod.id("tool_vise_tile_entity"));
      registerTileEntity(event, MAGNET_TABLE_TILE_ENTITY, VaultMod.id("magnet_modification_table_tile_entity"));
      registerTileEntity(event, DEMAGNETIZER_TILE_ENTITY, VaultMod.id("demagnetizer_tile_entity"));
      registerTileEntity(event, COIN_PILE_TILE, VaultMod.id("coin_pile_tile"));
      registerTileEntity(event, TREASURE_SAND_TILE_ENTITY, VaultMod.id("treasure_sand_tile_entity"));
      registerTileEntity(event, VAULT_FORGE_TILE_ENTITY, VaultMod.id("vault_forge_tile_entity"));
      registerTileEntity(event, VAULT_ARTISAN_STATION_ENTITY, VaultMod.id("vault_artisan_station_tile_entity"));
      registerTileEntity(event, VAULT_RECYCLER_ENTITY, VaultMod.id("vault_recycler_tile_entity"));
      registerTileEntity(event, VAULT_DIFFUSER_ENTITY, VaultMod.id("vault_diffuser_tile_entity"));
      registerTileEntity(event, ANIMAL_PEN_ENTITY, VaultMod.id("animal_pen_tile_entity"));
      registerTileEntity(event, ELITE_SPAWNER_TILE_ENTITY, VaultMod.id("elite_spawner_tile_entity"));
      registerTileEntity(event, WILD_SPAWNER_TILE_ENTITY, VaultMod.id("wild_spawner_tile_entity"));
      registerTileEntity(event, CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, VaultMod.id("custom_entity_spawner_tile_entity"));
      registerTileEntity(event, CUBE_BLOCK_TILE_ENTITY, VaultMod.id("cube_block_tile_entity"));
      registerTileEntity(event, SPIRIT_EXTRACTOR_TILE_ENTITY, VaultMod.id("spirit_extractor_tile_entity"));
      registerTileEntity(event, BOUNTY_TABLE_TILE_ENTITY, VaultMod.id("bounty_table_tile_entity"));
      registerTileEntity(event, BLACK_MARKET_TILE_ENTITY, VaultMod.id("black_market_tile_entity"));
      registerTileEntity(event, TRANSMOG_TABLE_TILE_ENTITY, VaultMod.id("transmog_table_tile_entity"));
      registerTileEntity(event, CRYSTAL_BUDDING_TILE_ENTITY, VaultMod.id("crystal_budding_tile_entity"));
      registerTileEntity(event, ETERNAL_PEDESTAL_TILE_ENTITY, VaultMod.id("eternal_pedestal_tile_entity"));
   }

   public static void registerTileEntityRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer(FLOATING_TEXT_TILE_ENTITY, FloatingTextRenderer::new);
      event.registerBlockEntityRenderer(VAULT_CHAMPION_TROPHY_TILE_ENTITY, VaultChampionTrophyRenderer::new);
      event.registerBlockEntityRenderer(FINAL_VAULT_FRAME_TILE_ENTITY, FinalVaultFrameRenderer::new);
      event.registerBlockEntityRenderer(VAULT_ALTAR_TILE_ENTITY, VaultAltarRenderer::new);
      event.registerBlockEntityRenderer(RELIC_STATUE_TILE_ENTITY, RelicPedestalRenderer::new);
      event.registerBlockEntityRenderer(LOOT_STATUE_TILE_ENTITY, LootStatueRenderer::new);
      event.registerBlockEntityRenderer(SHOP_PEDESTAL_TILE_ENTITY, ShopPedestalBlockTileRenderer::new);
      event.registerBlockEntityRenderer(TROPHY_STATUE_TILE_ENTITY, TrophyRenderer::new);
      event.registerBlockEntityRenderer(CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      event.registerBlockEntityRenderer(ANCIENT_CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      event.registerBlockEntityRenderer(VAULT_CHEST_TILE_ENTITY, VaultChestRenderer::new);
      event.registerBlockEntityRenderer(SCAVENGER_CHEST_TILE_ENTITY, VaultChestRenderer::new);
      event.registerBlockEntityRenderer(SCAVENGER_ALTAR_TILE_ENTITY, ScavengerAltarRenderer::new);
      event.registerBlockEntityRenderer(XP_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      event.registerBlockEntityRenderer(BLOOD_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      event.registerBlockEntityRenderer(TIME_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      event.registerBlockEntityRenderer(SOUL_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      event.registerBlockEntityRenderer(HOURGLASS_TILE_ENTITY, HourglassRenderer::new);
      event.registerBlockEntityRenderer(VAULT_PORTAL_TILE_ENTITY, VaultPortalRenderer::new);
      event.registerBlockEntityRenderer(RAID_CONTROLLER_TILE_ENTITY, VaultRaidControllerRenderer::new);
      event.registerBlockEntityRenderer(MAGNET_TABLE_TILE_ENTITY, MagnetTableRenderer::new);
      event.registerBlockEntityRenderer(TOOL_VISE_TILE_ENTITY, ToolViseRenderer::new);
      event.registerBlockEntityRenderer(ELITE_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(WILD_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, SpawnerRenderer::new);
      event.registerBlockEntityRenderer(SPIRIT_EXTRACTOR_TILE_ENTITY, SpiritExtractorRenderer::new);
      event.registerBlockEntityRenderer(VAULT_DIFFUSER_ENTITY, VaultDiffuserRenderer::new);
      event.registerBlockEntityRenderer(BLACK_MARKET_TILE_ENTITY, BlackMarketRenderer::new);
      event.registerBlockEntityRenderer(ANIMAL_PEN_ENTITY, AnimalPenRenderer::new);
      event.registerBlockEntityRenderer(ETERNAL_PEDESTAL_TILE_ENTITY, EternalPedestalRenderer::new);
      event.registerBlockEntityRenderer(PYLON_TILE_ENTITY, PylonRenderer::new);
   }

   public static void registerBlockItems(Register<Item> event) {
      registerBlockItem(event, FLOATING_TEXT);
      registerBlockItem(event, VAULT_PORTAL);
      registerBlockItem(event, VAULT_CHAMPION_TROPHY, VAULT_CHAMPION_TROPHY_BLOCK_ITEM);
      registerBlockItem(event, FINAL_VAULT_FRAME, FINAL_VAULT_FRAME_BLOCK_ITEM);
      registerBlockItem(event, OTHER_SIDE_PORTAL);
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
      registerBlockItem(event, VAULTSTONE_ALEXANDRITE_ORE);
      registerBlockItem(event, VAULTSTONE_ASHIUM_ORE);
      registerBlockItem(event, VAULTSTONE_BENITOITE_ORE);
      registerBlockItem(event, VAULTSTONE_BLACK_OPAL_ORE);
      registerBlockItem(event, VAULTSTONE_BOMIGNITE_ORE);
      registerBlockItem(event, VAULTSTONE_ECHO_ORE);
      registerBlockItem(event, VAULTSTONE_GORGINITE_ORE);
      registerBlockItem(event, VAULTSTONE_ISKALLIUM_ORE);
      registerBlockItem(event, VAULTSTONE_LARIMAR_ORE);
      registerBlockItem(event, VAULTSTONE_PAINITE_ORE);
      registerBlockItem(event, VAULTSTONE_PETZANITE_ORE);
      registerBlockItem(event, VAULTSTONE_SPARKLETINE_ORE);
      registerBlockItem(event, VAULTSTONE_TUBIUM_ORE);
      registerBlockItem(event, VAULTSTONE_UPALINE_ORE);
      registerBlockItem(event, VAULTSTONE_WUTODIE_ORE);
      registerBlockItem(event, VAULTSTONE_XENIUM_ORE);
      registerBlockItem(event, VAULT_ROCK_ORE);
      registerBlockItem(event, VAULT_ARTIFACT, 1);
      registerBlockItem(event, VAULT_CRATE, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_CAKE, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_ARENA, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_SCAVENGER, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_CHAMPION, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_BOUNTY, 1, Properties::fireResistant);
      registerBlockItem(event, VAULT_CRATE_MONOLITH, 1, Properties::fireResistant);
      registerBlockItem(event, OBELISK, 1);
      registerBlockItem(event, MONOLITH, 1);
      registerBlockItem(event, PYLON, 1);
      registerBlockItem(event, MVP_CROWN, 1);
      registerBlockItem(event, VAULT_BEDROCK);
      registerBlockItem(event, VAULT_STONE);
      registerBlockItem(event, VAULT_COBBLESTONE);
      registerBlockItem(event, CHISELED_VAULT_STONE);
      registerBlockItem(event, POLISHED_VAULT_STONE);
      registerBlockItem(event, BUMBO_POLISHED_VAULT_STONE);
      registerBlockItem(event, VAULT_GLASS);
      registerBlockItem(event, RELIC_PEDESTAL);
      registerBlockItem(event, LOOT_STATUE, LOOT_STATUE_ITEM);
      registerBlockItem(event, SHOP_PEDESTAL);
      registerBlockItem(event, CRYO_CHAMBER);
      registerBlockItem(event, VAULT_DIAMOND_BLOCK);
      registerBlockItem(event, TROPHY_STATUE, TROPHY_STATUE_BLOCK_ITEM);
      registerBlockItem(event, TRANSMOG_TABLE);
      registerBlockItem(event, WOODEN_CHEST, WOODEN_CHEST_ITEM);
      registerBlockItem(event, ALTAR_CHEST, ALTAR_CHEST_ITEM);
      registerBlockItem(event, GILDED_CHEST, GILDED_CHEST_ITEM);
      registerBlockItem(event, ORNATE_CHEST, ORNATE_CHEST_ITEM);
      registerBlockItem(event, TREASURE_CHEST, TREASURE_CHEST_ITEM);
      registerBlockItem(event, LIVING_CHEST, LIVING_CHEST_ITEM);
      registerBlockItem(event, WOODEN_CHEST_PLACEABLE, WOODEN_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, ALTAR_CHEST_PLACEABLE, ALTAR_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, GILDED_CHEST_PLACEABLE, GILDED_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, ORNATE_CHEST_PLACEABLE, ORNATE_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, TREASURE_CHEST_PLACEABLE, TREASURE_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, LIVING_CHEST_PLACEABLE, LIVING_CHEST_ITEM_PLACEABLE);
      registerBlockItem(event, XP_ALTAR);
      registerBlockItem(event, BLOOD_ALTAR);
      registerBlockItem(event, TIME_ALTAR);
      registerBlockItem(event, SOUL_ALTAR);
      registerBlockItem(event, HOURGLASS, new HourglassBlockItem(HOURGLASS));
      registerBlockItem(event, SCAVENGER_CHEST, SCAVENGER_CHEST_ITEM);
      registerBlockItem(event, SCAVENGER_ALTAR, SCAVENGER_ALTAR_ITEM);
      registerBlockItem(event, SCAVENGER_TREASURE);
      registerBlockItem(event, STABILIZER);
      registerBlockItem(event, CATALYST_INFUSION_TABLE);
      registerBlockItem(event, ETCHING_CONTROLLER_BLOCK);
      registerBlockItem(event, VAULT_CHARM_CONTROLLER_BLOCK);
      registerBlockItem(event, RAID_CONTROLLER_BLOCK);
      registerBlockItem(event, PLACEHOLDER, new PlaceholderBlockItem());
      registerBlockItem(event, TREASURE_DOOR, new TreasureDoorBlockItem());
      registerBlockItem(event, COIN_PILE);
      registerBlockItem(event, BRONZE_COIN_PILE, VAULT_BRONZE);
      registerBlockItem(event, SILVER_COIN_PILE, VAULT_SILVER);
      registerBlockItem(event, GOLD_COIN_PILE, VAULT_GOLD);
      registerBlockItem(event, PLATINUM_COIN_PILE, VAULT_PLATINUM);
      registerBlockItem(event, TOOL_VISE);
      registerBlockItem(event, MAGNET_TABLE);
      registerBlockItem(event, DEMAGNETIZER_BLOCK);
      registerBlockItem(event, CHROMATIC_IRON_ORE);
      registerBlockItem(event, RAW_CHROMATIC_IRON_BLOCK);
      registerBlockItem(event, CHROMATIC_IRON_BLOCK);
      registerBlockItem(event, CHROMATIC_STEEL_BLOCK);
      registerBlockItem(event, BLACK_CHROMATIC_STEEL_BLOCK);
      registerBlockItem(event, VAULT_MEAT_BLOCK);
      registerBlockItem(event, PACKED_VAULT_MEAT_BLOCK);
      registerBlockItem(event, TREASURE_SAND);
      registerBlockItem(event, VAULT_FORGE);
      registerBlockItem(event, VAULT_ARTISAN_STATION);
      registerBlockItem(event, VAULT_RECYCLER);
      registerBlockItem(event, VAULT_DIFFUSER);
      registerBlockItem(event, ELITE_SPAWNER);
      registerBlockItem(event, WILD_SPAWNER);
      registerBlockItem(event, CUSTOM_ENTITY_SPAWNER);
      registerBlockItem(event, SPIRIT_EXTRACTOR);
      registerBlockItem(event, MAGIC_SILK_BLOCK);
      registerBlockItem(event, YELLOW_PUZZLE_CONCRETE);
      registerBlockItem(event, PINK_PUZZLE_CONCRETE);
      registerBlockItem(event, GREEN_PUZZLE_CONCRETE);
      registerBlockItem(event, BLUE_PUZZLE_CONCRETE);
      registerBlockItem(event, SUGAR_PLUM_FAIRY_FLOWER);
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
      registerBlockItem(event, VAULT_STONE_SLAB);
      registerBlockItem(event, VAULT_STONE_STAIRS);
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
