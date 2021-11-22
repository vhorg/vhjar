package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.block.AdvancedVendingBlock;
import iskallia.vault.block.BloodAltarBlock;
import iskallia.vault.block.BowHatBlock;
import iskallia.vault.block.CatalystDecryptionTableBlock;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.EtchingVendorControllerBlock;
import iskallia.vault.block.FinalVaultPortalBlock;
import iskallia.vault.block.KeyPressBlock;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.MazeBlock;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.OmegaStatueBlock;
import iskallia.vault.block.OmegaVariantStatueBlock;
import iskallia.vault.block.PuzzleRuneBlock;
import iskallia.vault.block.RelicStatueBlock;
import iskallia.vault.block.ScavengerChestBlock;
import iskallia.vault.block.ScavengerTreasureBlock;
import iskallia.vault.block.SoulAltarBlock;
import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.StatueCauldronBlock;
import iskallia.vault.block.StatueDragonHeadBlock;
import iskallia.vault.block.TimeAltarBlock;
import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.block.TrophyBlock;
import iskallia.vault.block.UnknownVaultDoorBlock;
import iskallia.vault.block.VaultAltarBlock;
import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.block.VaultBedrockBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.block.VaultLootableBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultRockBlock;
import iskallia.vault.block.VaultRuneBlock;
import iskallia.vault.block.VaultTreasureChestBlock;
import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.XPAltarBlock;
import iskallia.vault.block.entity.AdvancedVendingTileEntity;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.BloodAltarTileEntity;
import iskallia.vault.block.entity.CatalystDecryptionTableTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.ObeliskTileEntity;
import iskallia.vault.block.entity.RelicStatueTileEntity;
import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.block.entity.ScavengerTreasureTileEntity;
import iskallia.vault.block.entity.SoulAltarTileEntity;
import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.block.entity.StatueCauldronTileEntity;
import iskallia.vault.block.entity.TimeAltarTileEntity;
import iskallia.vault.block.entity.TrophyStatueTileEntity;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.block.entity.VaultDoorTileEntity;
import iskallia.vault.block.entity.VaultLootableTileEntity;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.block.entity.VaultRuneTileEntity;
import iskallia.vault.block.entity.VaultTreasureChestTileEntity;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.block.entity.XpAltarTileEntity;
import iskallia.vault.block.item.AdvancedVendingMachineBlockItem;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.RelicStatueBlockItem;
import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.block.item.VendingMachineBlockItem;
import iskallia.vault.block.render.AdvancedVendingRenderer;
import iskallia.vault.block.render.CryoChamberRenderer;
import iskallia.vault.block.render.FillableAltarRenderer;
import iskallia.vault.block.render.LootStatueRenderer;
import iskallia.vault.block.render.RelicStatueRenderer;
import iskallia.vault.block.render.ScavengerChestRenderer;
import iskallia.vault.block.render.StatueCauldronRenderer;
import iskallia.vault.block.render.VaultAltarRenderer;
import iskallia.vault.block.render.VaultChestRenderer;
import iskallia.vault.block.render.VaultRuneRenderer;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.client.render.VaultISTER;
import iskallia.vault.fluid.block.VoidFluidBlock;
import iskallia.vault.util.StatueType;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModBlocks {
   public static final VaultPortalBlock VAULT_PORTAL = new VaultPortalBlock();
   public static final FinalVaultPortalBlock FINAL_VAULT_PORTAL = new FinalVaultPortalBlock();
   public static final VaultAltarBlock VAULT_ALTAR = new VaultAltarBlock();
   public static final VaultOreBlock ALEXANDRITE_ORE = new VaultOreBlock(ModItems.ALEXANDRITE_GEM);
   public static final VaultOreBlock BENITOITE_ORE = new VaultOreBlock(ModItems.BENITOITE_GEM);
   public static final VaultOreBlock LARIMAR_ORE = new VaultOreBlock(ModItems.LARIMAR_GEM);
   public static final VaultOreBlock BLACK_OPAL_ORE = new VaultOreBlock(ModItems.BLACK_OPAL_GEM);
   public static final VaultOreBlock PAINITE_ORE = new VaultOreBlock(ModItems.PAINITE_GEM);
   public static final VaultOreBlock ISKALLIUM_ORE = new VaultOreBlock(ModItems.ISKALLIUM_GEM);
   public static final VaultOreBlock GORGINITE_ORE = new VaultOreBlock(ModItems.GORGINITE_GEM);
   public static final VaultOreBlock SPARKLETINE_ORE = new VaultOreBlock(ModItems.SPARKLETINE_GEM);
   public static final VaultOreBlock WUTODIE_ORE = new VaultOreBlock(ModItems.WUTODIE_GEM);
   public static final VaultOreBlock ASHIUM_ORE = new VaultOreBlock(ModItems.ASHIUM_GEM);
   public static final VaultOreBlock BOMIGNITE_ORE = new VaultOreBlock(ModItems.BOMIGNITE_GEM);
   public static final VaultOreBlock FUNSOIDE_ORE = new VaultOreBlock(ModItems.FUNSOIDE_GEM);
   public static final VaultOreBlock TUBIUM_ORE = new VaultOreBlock(ModItems.TUBIUM_GEM);
   public static final VaultOreBlock UPALINE_ORE = new VaultOreBlock(ModItems.UPALINE_GEM);
   public static final VaultOreBlock PUFFIUM_ORE = new VaultOreBlock(ModItems.PUFFIUM_GEM);
   public static final VaultOreBlock ECHO_ORE = new VaultOreBlock(ModItems.ECHO_GEM);
   public static final Block UNKNOWN_ORE = new VaultLootableBlock(VaultLootableBlock.Type.ORE);
   public static final VaultRockBlock VAULT_ROCK_ORE = new VaultRockBlock();
   public static final DoorBlock ISKALLIUM_DOOR = new VaultDoorBlock(ModItems.ISKALLIUM_KEY);
   public static final DoorBlock GORGINITE_DOOR = new VaultDoorBlock(ModItems.GORGINITE_KEY);
   public static final DoorBlock SPARKLETINE_DOOR = new VaultDoorBlock(ModItems.SPARKLETINE_KEY);
   public static final DoorBlock ASHIUM_DOOR = new VaultDoorBlock(ModItems.ASHIUM_KEY);
   public static final DoorBlock BOMIGNITE_DOOR = new VaultDoorBlock(ModItems.BOMIGNITE_KEY);
   public static final DoorBlock FUNSOIDE_DOOR = new VaultDoorBlock(ModItems.FUNSOIDE_KEY);
   public static final DoorBlock TUBIUM_DOOR = new VaultDoorBlock(ModItems.TUBIUM_KEY);
   public static final DoorBlock UPALINE_DOOR = new VaultDoorBlock(ModItems.UPALINE_KEY);
   public static final DoorBlock PUFFIUM_DOOR = new VaultDoorBlock(ModItems.PUFFIUM_KEY);
   public static final DoorBlock UNKNOWN_DOOR = new UnknownVaultDoorBlock();
   public static final VaultRuneBlock VAULT_RUNE_BLOCK = new VaultRuneBlock();
   public static final VaultArtifactBlock VAULT_ARTIFACT = new VaultArtifactBlock();
   public static final VaultCrateBlock VAULT_CRATE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_ARENA = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_SCAVENGER = new VaultCrateBlock();
   public static final ObeliskBlock OBELISK = new ObeliskBlock();
   public static final VendingMachineBlock VENDING_MACHINE = new VendingMachineBlock();
   public static final AdvancedVendingBlock ADVANCED_VENDING_MACHINE = new AdvancedVendingBlock();
   public static final VaultBedrockBlock VAULT_BEDROCK = new VaultBedrockBlock();
   public static final Block VAULT_STONE = new Block(
      net.minecraft.block.AbstractBlock.Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F).func_235827_a_((a, b, c, d) -> false)
   );
   public static final GlassBlock VAULT_GLASS = new GlassBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150359_w).func_200948_a(-1.0F, 3600000.0F)
   );
   public static final RelicStatueBlock RELIC_STATUE = new RelicStatueBlock();
   public static final LootStatueBlock GIFT_NORMAL_STATUE = new LootStatueBlock(StatueType.GIFT_NORMAL);
   public static final LootStatueBlock GIFT_MEGA_STATUE = new LootStatueBlock(StatueType.GIFT_MEGA);
   public static final BowHatBlock BOW_HAT = new BowHatBlock();
   public static final StatueDragonHeadBlock STATUE_DRAGON_HEAD = new StatueDragonHeadBlock();
   public static final LootStatueBlock VAULT_PLAYER_LOOT_STATUE = new LootStatueBlock(StatueType.VAULT_BOSS);
   public static final CryoChamberBlock CRYO_CHAMBER = new CryoChamberBlock();
   public static final KeyPressBlock KEY_PRESS = new KeyPressBlock();
   public static final Block VAULT_DIAMOND_BLOCK = new Block(net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150484_ah));
   public static final MazeBlock MAZE_BLOCK = new MazeBlock();
   public static final PuzzleRuneBlock PUZZLE_RUNE_BLOCK = new PuzzleRuneBlock();
   public static final Block YELLOW_PUZZLE_CONCRETE = new Block(net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_196858_iR));
   public static final Block PINK_PUZZLE_CONCRETE = new Block(net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_196858_iR));
   public static final Block GREEN_PUZZLE_CONCRETE = new Block(net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_196858_iR));
   public static final Block BLUE_PUZZLE_CONCRETE = new Block(net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_196858_iR));
   public static final OmegaStatueBlock OMEGA_STATUE = new OmegaStatueBlock();
   public static final OmegaVariantStatueBlock OMEGA_STATUE_VARIANT = new OmegaVariantStatueBlock();
   public static final TrophyBlock TROPHY_STATUE = new TrophyBlock();
   public static final TransmogTableBlock TRANSMOG_TABLE = new TransmogTableBlock();
   public static final Block VAULT_LOOT_RICHITY = new VaultLootableBlock(VaultLootableBlock.Type.RICHITY);
   public static final Block VAULT_LOOT_RESOURCE = new VaultLootableBlock(VaultLootableBlock.Type.RESOURCE);
   public static final Block VAULT_LOOT_MISC = new VaultLootableBlock(VaultLootableBlock.Type.MISC);
   public static final Block UNKNOWN_VAULT_CHEST = new VaultLootableBlock(VaultLootableBlock.Type.VAULT_CHEST);
   public static final Block UNKNOWN_TREASURE_CHEST = new VaultLootableBlock(VaultLootableBlock.Type.VAULT_TREASURE);
   public static final Block UNKNOWN_VAULT_OBJECTIVE = new VaultLootableBlock(VaultLootableBlock.Type.VAULT_OBJECTIVE);
   public static final Block VAULT_CHEST = new VaultChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150486_ae).func_200948_a(40.0F, 5.0F)
   );
   public static final Block VAULT_TREASURE_CHEST = new VaultTreasureChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150486_ae).func_200948_a(-1.0F, 3600000.0F)
   );
   public static final Block VAULT_ALTAR_CHEST = new VaultChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150486_ae).func_200948_a(-1.0F, 3600000.0F)
   );
   public static final Block VAULT_COOP_CHEST = new VaultChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200945_a(Material.field_151576_e)
         .func_200948_a(2.0F, 3600000.0F)
         .func_200947_a(SoundType.field_185851_d)
   );
   public static final Block VAULT_BONUS_CHEST = new VaultChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200945_a(Material.field_151576_e)
         .func_200948_a(2.0F, 3600000.0F)
         .func_200947_a(SoundType.field_185851_d)
   );
   public static final XPAltarBlock XP_ALTAR = new XPAltarBlock();
   public static final BloodAltarBlock BLOOD_ALTAR = new BloodAltarBlock();
   public static final TimeAltarBlock TIME_ALTAR = new TimeAltarBlock();
   public static final SoulAltarBlock SOUL_ALTAR = new SoulAltarBlock();
   public static final StatueCauldronBlock STATUE_CAULDRON = new StatueCauldronBlock();
   public static final ScavengerChestBlock SCAVENGER_CHEST = new ScavengerChestBlock(
      net.minecraft.block.AbstractBlock.Properties.func_200950_a(Blocks.field_150486_ae).func_200948_a(-1.0F, 3600000.0F)
   );
   public static final ScavengerTreasureBlock SCAVENGER_TREASURE = new ScavengerTreasureBlock();
   public static final StabilizerBlock STABILIZER = new StabilizerBlock();
   public static final CatalystDecryptionTableBlock CATALYST_DECRYPTION_TABLE = new CatalystDecryptionTableBlock();
   public static final EtchingVendorControllerBlock ETCHING_CONTROLLER_BLOCK = new EtchingVendorControllerBlock();
   public static final FlowingFluidBlock VOID_LIQUID_BLOCK = new VoidFluidBlock(
      ModFluids.VOID_LIQUID,
      net.minecraft.block.AbstractBlock.Properties.func_200949_a(Material.field_151586_h, MaterialColor.field_151646_E)
         .func_200942_a()
         .func_200944_c()
         .func_200943_b(100.0F)
         .func_235838_a_(state -> 15)
         .func_222380_e()
   );
   public static final RelicStatueBlockItem RELIC_STATUE_BLOCK_ITEM = new RelicStatueBlockItem();
   public static final LootStatueBlockItem GIFT_NORMAL_STATUE_BLOCK_ITEM = new LootStatueBlockItem(GIFT_NORMAL_STATUE, StatueType.GIFT_NORMAL);
   public static final LootStatueBlockItem GIFT_MEGA_STATUE_BLOCK_ITEM = new LootStatueBlockItem(GIFT_MEGA_STATUE, StatueType.GIFT_MEGA);
   public static final LootStatueBlockItem VAULT_PLAYER_LOOT_STATUE_BLOCK_ITEM = new LootStatueBlockItem(VAULT_PLAYER_LOOT_STATUE, StatueType.VAULT_BOSS);
   public static final LootStatueBlockItem OMEGA_STATUE_BLOCK_ITEM = new LootStatueBlockItem(OMEGA_STATUE, StatueType.OMEGA);
   public static final LootStatueBlockItem OMEGA_STATUE_VARIANT_BLOCK_ITEM = new LootStatueBlockItem(OMEGA_STATUE_VARIANT, StatueType.OMEGA_VARIANT);
   public static final TrophyStatueBlockItem TROPHY_STATUE_BLOCK_ITEM = new TrophyStatueBlockItem(TROPHY_STATUE);
   public static final VendingMachineBlockItem VENDING_MACHINE_BLOCK_ITEM = new VendingMachineBlockItem(VENDING_MACHINE);
   public static final AdvancedVendingMachineBlockItem ADVANCED_VENDING_BLOCK_ITEM = new AdvancedVendingMachineBlockItem(ADVANCED_VENDING_MACHINE);
   public static final PuzzleRuneBlock.Item PUZZLE_RUNE_BLOCK_ITEM = new PuzzleRuneBlock.Item(
      PUZZLE_RUNE_BLOCK, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1)
   );
   public static final BlockItem VAULT_CHEST_ITEM = new BlockItem(
      VAULT_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final BlockItem VAULT_TREASURE_CHEST_ITEM = new BlockItem(
      VAULT_TREASURE_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final BlockItem VAULT_ALTAR_CHEST_ITEM = new BlockItem(
      VAULT_ALTAR_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final BlockItem VAULT_COOP_CHEST_ITEM = new BlockItem(
      VAULT_COOP_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final BlockItem VAULT_BONUS_CHEST_ITEM = new BlockItem(
      VAULT_BONUS_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final BlockItem SCAVENGER_CHEST_ITEM = new BlockItem(
      SCAVENGER_CHEST, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).setISTER(() -> ModBlocks.VaultStackRendererProvider.INSTANCE)
   );
   public static final TileEntityType<VaultAltarTileEntity> VAULT_ALTAR_TILE_ENTITY = Builder.func_223042_a(VaultAltarTileEntity::new, new Block[]{VAULT_ALTAR})
      .func_206865_a(null);
   public static final TileEntityType<VaultRuneTileEntity> VAULT_RUNE_TILE_ENTITY = Builder.func_223042_a(
         VaultRuneTileEntity::new, new Block[]{VAULT_RUNE_BLOCK}
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultCrateTileEntity> VAULT_CRATE_TILE_ENTITY = Builder.func_223042_a(
         VaultCrateTileEntity::new, new Block[]{VAULT_CRATE, VAULT_CRATE_ARENA}
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultPortalTileEntity> VAULT_PORTAL_TILE_ENTITY = Builder.func_223042_a(
         VaultPortalTileEntity::new, new Block[]{VAULT_PORTAL}
      )
      .func_206865_a(null);
   public static final TileEntityType<VendingMachineTileEntity> VENDING_MACHINE_TILE_ENTITY = Builder.func_223042_a(
         VendingMachineTileEntity::new, new Block[]{VENDING_MACHINE}
      )
      .func_206865_a(null);
   public static final TileEntityType<AdvancedVendingTileEntity> ADVANCED_VENDING_MACHINE_TILE_ENTITY = Builder.func_223042_a(
         AdvancedVendingTileEntity::new, new Block[]{ADVANCED_VENDING_MACHINE}
      )
      .func_206865_a(null);
   public static final TileEntityType<RelicStatueTileEntity> RELIC_STATUE_TILE_ENTITY = Builder.func_223042_a(
         RelicStatueTileEntity::new, new Block[]{RELIC_STATUE}
      )
      .func_206865_a(null);
   public static final TileEntityType<LootStatueTileEntity> LOOT_STATUE_TILE_ENTITY = Builder.func_223042_a(
         LootStatueTileEntity::new, new Block[]{GIFT_NORMAL_STATUE, GIFT_MEGA_STATUE, VAULT_PLAYER_LOOT_STATUE, OMEGA_STATUE, OMEGA_STATUE_VARIANT}
      )
      .func_206865_a(null);
   public static final TileEntityType<TrophyStatueTileEntity> TROPHY_STATUE_TILE_ENTITY = Builder.func_223042_a(
         TrophyStatueTileEntity::new, new Block[]{TROPHY_STATUE}
      )
      .func_206865_a(null);
   public static final TileEntityType<CryoChamberTileEntity> CRYO_CHAMBER_TILE_ENTITY = Builder.func_223042_a(
         CryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .func_206865_a(null);
   public static final TileEntityType<AncientCryoChamberTileEntity> ANCIENT_CRYO_CHAMBER_TILE_ENTITY = Builder.func_223042_a(
         AncientCryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultDoorTileEntity> VAULT_DOOR_TILE_ENTITY = Builder.func_223042_a(
         VaultDoorTileEntity::new,
         new Block[]{
            ISKALLIUM_DOOR, GORGINITE_DOOR, SPARKLETINE_DOOR, ASHIUM_DOOR, BOMIGNITE_DOOR, FUNSOIDE_DOOR, TUBIUM_DOOR, UPALINE_DOOR, PUFFIUM_DOOR, UNKNOWN_DOOR
         }
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultLootableTileEntity> VAULT_LOOTABLE_TILE_ENTITY = Builder.func_223042_a(
         VaultLootableTileEntity::new,
         new Block[]{
            UNKNOWN_ORE, VAULT_LOOT_RICHITY, VAULT_LOOT_RESOURCE, VAULT_LOOT_MISC, UNKNOWN_VAULT_CHEST, UNKNOWN_TREASURE_CHEST, UNKNOWN_VAULT_OBJECTIVE
         }
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultChestTileEntity> VAULT_CHEST_TILE_ENTITY = Builder.func_223042_a(
         VaultChestTileEntity::new, new Block[]{VAULT_CHEST, VAULT_ALTAR_CHEST, VAULT_COOP_CHEST, VAULT_BONUS_CHEST}
      )
      .func_206865_a(null);
   public static final TileEntityType<VaultTreasureChestTileEntity> VAULT_TREASURE_CHEST_TILE_ENTITY = Builder.func_223042_a(
         VaultTreasureChestTileEntity::new, new Block[]{VAULT_TREASURE_CHEST}
      )
      .func_206865_a(null);
   public static final TileEntityType<XpAltarTileEntity> XP_ALTAR_TILE_ENTITY = Builder.func_223042_a(XpAltarTileEntity::new, new Block[]{XP_ALTAR})
      .func_206865_a(null);
   public static final TileEntityType<BloodAltarTileEntity> BLOOD_ALTAR_TILE_ENTITY = Builder.func_223042_a(BloodAltarTileEntity::new, new Block[]{BLOOD_ALTAR})
      .func_206865_a(null);
   public static final TileEntityType<TimeAltarTileEntity> TIME_ALTAR_TILE_ENTITY = Builder.func_223042_a(TimeAltarTileEntity::new, new Block[]{TIME_ALTAR})
      .func_206865_a(null);
   public static final TileEntityType<SoulAltarTileEntity> SOUL_ALTAR_TILE_ENTITY = Builder.func_223042_a(SoulAltarTileEntity::new, new Block[]{SOUL_ALTAR})
      .func_206865_a(null);
   public static final TileEntityType<StatueCauldronTileEntity> STATUE_CAULDRON_TILE_ENTITY = Builder.func_223042_a(
         StatueCauldronTileEntity::new, new Block[]{STATUE_CAULDRON}
      )
      .func_206865_a(null);
   public static final TileEntityType<ObeliskTileEntity> OBELISK_TILE_ENTITY = Builder.func_223042_a(ObeliskTileEntity::new, new Block[]{OBELISK})
      .func_206865_a(null);
   public static final TileEntityType<ScavengerChestTileEntity> SCAVENGER_CHEST_TILE_ENTITY = Builder.func_223042_a(
         ScavengerChestTileEntity::new, new Block[]{SCAVENGER_CHEST}
      )
      .func_206865_a(null);
   public static final TileEntityType<ScavengerTreasureTileEntity> SCAVENGER_TREASURE_TILE_ENTITY = Builder.func_223042_a(
         ScavengerTreasureTileEntity::new, new Block[]{SCAVENGER_TREASURE}
      )
      .func_206865_a(null);
   public static final TileEntityType<StabilizerTileEntity> STABILIZER_TILE_ENTITY = Builder.func_223042_a(StabilizerTileEntity::new, new Block[]{STABILIZER})
      .func_206865_a(null);
   public static final TileEntityType<CatalystDecryptionTableTileEntity> CATALYST_DECRYPTION_TABLE_TILE_ENTITY = Builder.func_223042_a(
         CatalystDecryptionTableTileEntity::new, new Block[]{CATALYST_DECRYPTION_TABLE}
      )
      .func_206865_a(null);
   public static final TileEntityType<EtchingVendorControllerTileEntity> ETCHING_CONTROLLER_TILE_ENTITY = Builder.func_223042_a(
         EtchingVendorControllerTileEntity::new, new Block[]{ETCHING_CONTROLLER_BLOCK}
      )
      .func_206865_a(null);

   public static void registerBlocks(Register<Block> event) {
      registerBlock(event, VAULT_PORTAL, Vault.id("vault_portal"));
      registerBlock(event, FINAL_VAULT_PORTAL, Vault.id("final_vault_portal"));
      registerBlock(event, VAULT_ALTAR, Vault.id("vault_altar"));
      registerBlock(event, ALEXANDRITE_ORE, Vault.id("ore_alexandrite"));
      registerBlock(event, BENITOITE_ORE, Vault.id("ore_benitoite"));
      registerBlock(event, LARIMAR_ORE, Vault.id("ore_larimar"));
      registerBlock(event, BLACK_OPAL_ORE, Vault.id("ore_black_opal"));
      registerBlock(event, PAINITE_ORE, Vault.id("ore_painite"));
      registerBlock(event, ISKALLIUM_ORE, Vault.id("ore_iskallium"));
      registerBlock(event, GORGINITE_ORE, Vault.id("ore_gorginite"));
      registerBlock(event, SPARKLETINE_ORE, Vault.id("ore_sparkletine"));
      registerBlock(event, WUTODIE_ORE, Vault.id("ore_wutodie"));
      registerBlock(event, ASHIUM_ORE, Vault.id("ore_ashium"));
      registerBlock(event, BOMIGNITE_ORE, Vault.id("ore_bomignite"));
      registerBlock(event, FUNSOIDE_ORE, Vault.id("ore_funsoide"));
      registerBlock(event, TUBIUM_ORE, Vault.id("ore_tubium"));
      registerBlock(event, UPALINE_ORE, Vault.id("ore_upaline"));
      registerBlock(event, PUFFIUM_ORE, Vault.id("ore_puffium"));
      registerBlock(event, ECHO_ORE, Vault.id("ore_echo"));
      registerBlock(event, UNKNOWN_ORE, Vault.id("ore_unknown"));
      registerBlock(event, VAULT_ROCK_ORE, Vault.id("ore_vault_rock"));
      registerBlock(event, ISKALLIUM_DOOR, Vault.id("door_iskallium"));
      registerBlock(event, GORGINITE_DOOR, Vault.id("door_gorginite"));
      registerBlock(event, SPARKLETINE_DOOR, Vault.id("door_sparkletine"));
      registerBlock(event, ASHIUM_DOOR, Vault.id("door_ashium"));
      registerBlock(event, BOMIGNITE_DOOR, Vault.id("door_bomignite"));
      registerBlock(event, FUNSOIDE_DOOR, Vault.id("door_funsoide"));
      registerBlock(event, TUBIUM_DOOR, Vault.id("door_tubium"));
      registerBlock(event, UPALINE_DOOR, Vault.id("door_upaline"));
      registerBlock(event, PUFFIUM_DOOR, Vault.id("door_puffium"));
      registerBlock(event, UNKNOWN_DOOR, Vault.id("door_unknown"));
      registerBlock(event, VAULT_RUNE_BLOCK, Vault.id("vault_rune_block"));
      registerBlock(event, VAULT_ARTIFACT, Vault.id("vault_artifact"));
      registerBlock(event, VAULT_CRATE, Vault.id("vault_crate"));
      registerBlock(event, VAULT_CRATE_ARENA, Vault.id("vault_crate_arena"));
      registerBlock(event, VAULT_CRATE_SCAVENGER, Vault.id("vault_crate_scavenger"));
      registerBlock(event, OBELISK, Vault.id("obelisk"));
      registerBlock(event, VENDING_MACHINE, Vault.id("vending_machine"));
      registerBlock(event, ADVANCED_VENDING_MACHINE, Vault.id("advanced_vending_machine"));
      registerBlock(event, VAULT_BEDROCK, Vault.id("vault_bedrock"));
      registerBlock(event, VAULT_STONE, Vault.id("vault_stone"));
      registerBlock(event, VAULT_GLASS, Vault.id("vault_glass"));
      registerBlock(event, RELIC_STATUE, Vault.id("relic_statue"));
      registerBlock(event, GIFT_NORMAL_STATUE, Vault.id("gift_normal_statue"));
      registerBlock(event, GIFT_MEGA_STATUE, Vault.id("gift_mega_statue"));
      registerBlock(event, BOW_HAT, Vault.id("bow_hat"));
      registerBlock(event, STATUE_DRAGON_HEAD, Vault.id("statue_dragon"));
      registerBlock(event, VAULT_PLAYER_LOOT_STATUE, Vault.id("vault_player_loot_statue"));
      registerBlock(event, CRYO_CHAMBER, Vault.id("cryo_chamber"));
      registerBlock(event, KEY_PRESS, Vault.id("key_press"));
      registerBlock(event, VAULT_DIAMOND_BLOCK, Vault.id("vault_diamond_block"));
      registerBlock(event, MAZE_BLOCK, Vault.id("maze_block"));
      registerBlock(event, PUZZLE_RUNE_BLOCK, Vault.id("puzzle_rune_block"));
      registerBlock(event, YELLOW_PUZZLE_CONCRETE, Vault.id("yellow_puzzle_concrete"));
      registerBlock(event, PINK_PUZZLE_CONCRETE, Vault.id("pink_puzzle_concrete"));
      registerBlock(event, GREEN_PUZZLE_CONCRETE, Vault.id("green_puzzle_concrete"));
      registerBlock(event, BLUE_PUZZLE_CONCRETE, Vault.id("blue_puzzle_concrete"));
      registerBlock(event, OMEGA_STATUE, Vault.id("omega_statue"));
      registerBlock(event, OMEGA_STATUE_VARIANT, Vault.id("omega_statue_variant"));
      registerBlock(event, TROPHY_STATUE, Vault.id("trophy_statue"));
      registerBlock(event, TRANSMOG_TABLE, Vault.id("transmog_table"));
      registerBlock(event, VAULT_LOOT_RICHITY, Vault.id("vault_richity"));
      registerBlock(event, VAULT_LOOT_RESOURCE, Vault.id("vault_resource"));
      registerBlock(event, VAULT_LOOT_MISC, Vault.id("vault_misc"));
      registerBlock(event, UNKNOWN_VAULT_CHEST, Vault.id("unknown_vault_chest"));
      registerBlock(event, UNKNOWN_TREASURE_CHEST, Vault.id("unknown_treasure_chest"));
      registerBlock(event, UNKNOWN_VAULT_OBJECTIVE, Vault.id("unknown_vault_objective"));
      registerBlock(event, VAULT_CHEST, Vault.id("vault_chest"));
      registerBlock(event, VAULT_TREASURE_CHEST, Vault.id("vault_treasure_chest"));
      registerBlock(event, VAULT_ALTAR_CHEST, Vault.id("vault_altar_chest"));
      registerBlock(event, VAULT_COOP_CHEST, Vault.id("vault_coop_chest"));
      registerBlock(event, VAULT_BONUS_CHEST, Vault.id("vault_bonus_chest"));
      registerBlock(event, XP_ALTAR, Vault.id("xp_altar"));
      registerBlock(event, BLOOD_ALTAR, Vault.id("blood_altar"));
      registerBlock(event, TIME_ALTAR, Vault.id("time_altar"));
      registerBlock(event, SOUL_ALTAR, Vault.id("soul_altar"));
      registerBlock(event, STATUE_CAULDRON, Vault.id("statue_cauldron"));
      registerBlock(event, VOID_LIQUID_BLOCK, Vault.id("void_liquid"));
      registerBlock(event, SCAVENGER_CHEST, Vault.id("scavenger_chest"));
      registerBlock(event, SCAVENGER_TREASURE, Vault.id("scavenger_treasure"));
      registerBlock(event, STABILIZER, Vault.id("stabilizer"));
      registerBlock(event, CATALYST_DECRYPTION_TABLE, Vault.id("catalyst_decryption_table"));
      registerBlock(event, ETCHING_CONTROLLER_BLOCK, Vault.id("etching_vendor_controller"));
   }

   public static void registerTileEntities(Register<TileEntityType<?>> event) {
      registerTileEntity(event, VAULT_ALTAR_TILE_ENTITY, Vault.id("vault_altar_tile_entity"));
      registerTileEntity(event, VAULT_RUNE_TILE_ENTITY, Vault.id("vault_rune_tile_entity"));
      registerTileEntity(event, VAULT_CRATE_TILE_ENTITY, Vault.id("vault_crate_tile_entity"));
      registerTileEntity(event, VAULT_PORTAL_TILE_ENTITY, Vault.id("vault_portal_tile_entity"));
      registerTileEntity(event, VENDING_MACHINE_TILE_ENTITY, Vault.id("vending_machine_tile_entity"));
      registerTileEntity(event, ADVANCED_VENDING_MACHINE_TILE_ENTITY, Vault.id("advanced_vending_machine_tile_entity"));
      registerTileEntity(event, RELIC_STATUE_TILE_ENTITY, Vault.id("relic_statue_tile_entity"));
      registerTileEntity(event, LOOT_STATUE_TILE_ENTITY, Vault.id("loot_statue_tile_entity"));
      registerTileEntity(event, TROPHY_STATUE_TILE_ENTITY, Vault.id("trophy_statue_tile_entity"));
      registerTileEntity(event, CRYO_CHAMBER_TILE_ENTITY, Vault.id("cryo_chamber_tile_entity"));
      registerTileEntity(event, ANCIENT_CRYO_CHAMBER_TILE_ENTITY, Vault.id("ancient_cryo_chamber_tile_entity"));
      registerTileEntity(event, VAULT_DOOR_TILE_ENTITY, Vault.id("vault_door_tile_entity"));
      registerTileEntity(event, VAULT_LOOTABLE_TILE_ENTITY, Vault.id("vault_lootable_tile_entity"));
      registerTileEntity(event, VAULT_CHEST_TILE_ENTITY, Vault.id("vault_chest_tile_entity"));
      registerTileEntity(event, VAULT_TREASURE_CHEST_TILE_ENTITY, Vault.id("vault_treasure_chest_tile_entity"));
      registerTileEntity(event, XP_ALTAR_TILE_ENTITY, Vault.id("xp_altar_tile_entity"));
      registerTileEntity(event, BLOOD_ALTAR_TILE_ENTITY, Vault.id("blood_altar_tile_entity"));
      registerTileEntity(event, TIME_ALTAR_TILE_ENTITY, Vault.id("time_altar_tile_entity"));
      registerTileEntity(event, SOUL_ALTAR_TILE_ENTITY, Vault.id("soul_altar_tile_entity"));
      registerTileEntity(event, STATUE_CAULDRON_TILE_ENTITY, Vault.id("statue_cauldron_tile_entity"));
      registerTileEntity(event, OBELISK_TILE_ENTITY, Vault.id("obelisk_tile_entity"));
      registerTileEntity(event, SCAVENGER_CHEST_TILE_ENTITY, Vault.id("scavenger_chest_tile_entity"));
      registerTileEntity(event, SCAVENGER_TREASURE_TILE_ENTITY, Vault.id("scavenger_treasure_tile_entity"));
      registerTileEntity(event, STABILIZER_TILE_ENTITY, Vault.id("stabilizer_tile_entity"));
      registerTileEntity(event, CATALYST_DECRYPTION_TABLE_TILE_ENTITY, Vault.id("catalyst_decryption_table_tile_entity"));
      registerTileEntity(event, ETCHING_CONTROLLER_TILE_ENTITY, Vault.id("etching_vendor_controller_tile_entity"));
   }

   public static void registerTileEntityRenderers() {
      ClientRegistry.bindTileEntityRenderer(VAULT_ALTAR_TILE_ENTITY, VaultAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VAULT_RUNE_TILE_ENTITY, VaultRuneRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VENDING_MACHINE_TILE_ENTITY, VendingMachineRenderer::new);
      ClientRegistry.bindTileEntityRenderer(ADVANCED_VENDING_MACHINE_TILE_ENTITY, AdvancedVendingRenderer::new);
      ClientRegistry.bindTileEntityRenderer(RELIC_STATUE_TILE_ENTITY, RelicStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(LOOT_STATUE_TILE_ENTITY, LootStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(TROPHY_STATUE_TILE_ENTITY, LootStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      ClientRegistry.bindTileEntityRenderer(ANCIENT_CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VAULT_CHEST_TILE_ENTITY, VaultChestRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VAULT_TREASURE_CHEST_TILE_ENTITY, VaultChestRenderer::new);
      ClientRegistry.bindTileEntityRenderer(XP_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(BLOOD_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(TIME_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(SOUL_ALTAR_TILE_ENTITY, FillableAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(STATUE_CAULDRON_TILE_ENTITY, StatueCauldronRenderer::new);
      ClientRegistry.bindTileEntityRenderer(SCAVENGER_CHEST_TILE_ENTITY, ScavengerChestRenderer::new);
   }

   public static void registerBlockItems(Register<Item> event) {
      registerBlockItem(event, VAULT_PORTAL);
      registerBlockItem(event, FINAL_VAULT_PORTAL);
      registerBlockItem(event, VAULT_ALTAR, 1);
      registerBlockItem(event, ALEXANDRITE_ORE);
      registerBlockItem(event, BENITOITE_ORE);
      registerBlockItem(event, LARIMAR_ORE);
      registerBlockItem(event, BLACK_OPAL_ORE);
      registerBlockItem(event, PAINITE_ORE);
      registerBlockItem(event, ISKALLIUM_ORE);
      registerBlockItem(event, GORGINITE_ORE);
      registerBlockItem(event, SPARKLETINE_ORE);
      registerBlockItem(event, WUTODIE_ORE);
      registerBlockItem(event, ASHIUM_ORE);
      registerBlockItem(event, BOMIGNITE_ORE);
      registerBlockItem(event, FUNSOIDE_ORE);
      registerBlockItem(event, TUBIUM_ORE);
      registerBlockItem(event, UPALINE_ORE);
      registerBlockItem(event, PUFFIUM_ORE);
      registerBlockItem(event, ECHO_ORE);
      registerBlockItem(event, UNKNOWN_ORE);
      registerBlockItem(event, VAULT_ROCK_ORE);
      registerTallBlockItem(event, ISKALLIUM_DOOR);
      registerTallBlockItem(event, GORGINITE_DOOR);
      registerTallBlockItem(event, SPARKLETINE_DOOR);
      registerTallBlockItem(event, ASHIUM_DOOR);
      registerTallBlockItem(event, BOMIGNITE_DOOR);
      registerTallBlockItem(event, FUNSOIDE_DOOR);
      registerTallBlockItem(event, TUBIUM_DOOR);
      registerTallBlockItem(event, UPALINE_DOOR);
      registerTallBlockItem(event, PUFFIUM_DOOR);
      registerTallBlockItem(event, UNKNOWN_DOOR);
      registerBlockItem(event, VAULT_RUNE_BLOCK);
      registerBlockItem(event, VAULT_ARTIFACT, 1);
      registerBlockItem(event, VAULT_CRATE, 1, Properties::func_234689_a_);
      registerBlockItem(event, VAULT_CRATE_ARENA, 1, Properties::func_234689_a_);
      registerBlockItem(event, VAULT_CRATE_SCAVENGER, 1, Properties::func_234689_a_);
      registerBlockItem(event, OBELISK, 1);
      registerBlockItem(event, VENDING_MACHINE, VENDING_MACHINE_BLOCK_ITEM);
      registerBlockItem(event, ADVANCED_VENDING_MACHINE, ADVANCED_VENDING_BLOCK_ITEM);
      registerBlockItem(event, VAULT_BEDROCK);
      registerBlockItem(event, VAULT_STONE);
      registerBlockItem(event, VAULT_GLASS);
      registerBlockItem(event, RELIC_STATUE, RELIC_STATUE_BLOCK_ITEM);
      registerBlockItem(event, GIFT_NORMAL_STATUE, GIFT_NORMAL_STATUE_BLOCK_ITEM);
      registerBlockItem(event, GIFT_MEGA_STATUE, GIFT_MEGA_STATUE_BLOCK_ITEM);
      registerBlockItem(event, BOW_HAT, 1);
      registerBlockItem(event, STATUE_DRAGON_HEAD, 1);
      registerBlockItem(event, VAULT_PLAYER_LOOT_STATUE, VAULT_PLAYER_LOOT_STATUE_BLOCK_ITEM);
      registerBlockItem(event, CRYO_CHAMBER);
      registerBlockItem(event, KEY_PRESS);
      registerBlockItem(event, VAULT_DIAMOND_BLOCK);
      registerBlockItem(event, PUZZLE_RUNE_BLOCK, PUZZLE_RUNE_BLOCK_ITEM);
      registerBlockItem(event, YELLOW_PUZZLE_CONCRETE);
      registerBlockItem(event, PINK_PUZZLE_CONCRETE);
      registerBlockItem(event, GREEN_PUZZLE_CONCRETE);
      registerBlockItem(event, BLUE_PUZZLE_CONCRETE);
      registerBlockItem(event, OMEGA_STATUE, OMEGA_STATUE_BLOCK_ITEM);
      registerBlockItem(event, OMEGA_STATUE_VARIANT, OMEGA_STATUE_VARIANT_BLOCK_ITEM);
      registerBlockItem(event, TROPHY_STATUE, TROPHY_STATUE_BLOCK_ITEM);
      registerBlockItem(event, TRANSMOG_TABLE);
      registerBlockItem(event, VAULT_LOOT_RICHITY);
      registerBlockItem(event, VAULT_LOOT_RESOURCE);
      registerBlockItem(event, VAULT_LOOT_MISC);
      registerBlockItem(event, UNKNOWN_VAULT_CHEST);
      registerBlockItem(event, UNKNOWN_TREASURE_CHEST);
      registerBlockItem(event, UNKNOWN_VAULT_OBJECTIVE);
      registerBlockItem(event, VAULT_CHEST, VAULT_CHEST_ITEM);
      registerBlockItem(event, VAULT_TREASURE_CHEST, VAULT_TREASURE_CHEST_ITEM);
      registerBlockItem(event, VAULT_ALTAR_CHEST, VAULT_ALTAR_CHEST_ITEM);
      registerBlockItem(event, VAULT_COOP_CHEST, VAULT_COOP_CHEST_ITEM);
      registerBlockItem(event, VAULT_BONUS_CHEST, VAULT_BONUS_CHEST_ITEM);
      registerBlockItem(event, XP_ALTAR);
      registerBlockItem(event, BLOOD_ALTAR);
      registerBlockItem(event, TIME_ALTAR);
      registerBlockItem(event, SOUL_ALTAR);
      registerBlockItem(event, STATUE_CAULDRON, 1);
      registerBlockItem(event, SCAVENGER_CHEST, SCAVENGER_CHEST_ITEM);
      registerBlockItem(event, SCAVENGER_TREASURE);
      registerBlockItem(event, STABILIZER);
      registerBlockItem(event, CATALYST_DECRYPTION_TABLE);
      registerBlockItem(event, ETCHING_CONTROLLER_BLOCK);
   }

   private static void registerBlock(Register<Block> event, Block block, ResourceLocation id) {
      block.setRegistryName(id);
      event.getRegistry().register(block);
   }

   private static <T extends TileEntity> void registerTileEntity(Register<TileEntityType<?>> event, TileEntityType<?> type, ResourceLocation id) {
      type.setRegistryName(id);
      event.getRegistry().register(type);
   }

   private static void registerBlockItemWithEffect(Register<Item> event, Block block, int maxStackSize, Consumer<Properties> adjustProperties) {
      Properties properties = new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(maxStackSize);
      adjustProperties.accept(properties);
      BlockItem blockItem = new BlockItem(block, properties) {
         public boolean func_77636_d(ItemStack stack) {
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
      Properties properties = new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(maxStackSize);
      adjustProperties.accept(properties);
      registerBlockItem(event, block, new BlockItem(block, properties));
   }

   private static void registerBlockItem(Register<Item> event, Block block, BlockItem blockItem) {
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerTallBlockItem(Register<Item> event, Block block) {
      TallBlockItem tallBlockItem = new TallBlockItem(block, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(64));
      tallBlockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(tallBlockItem);
   }

   @OnlyIn(Dist.CLIENT)
   private static class VaultStackRendererProvider implements Callable<ItemStackTileEntityRenderer> {
      public static final ModBlocks.VaultStackRendererProvider INSTANCE = new ModBlocks.VaultStackRendererProvider();

      public ItemStackTileEntityRenderer call() throws Exception {
         return VaultISTER.INSTANCE;
      }
   }
}
