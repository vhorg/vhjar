package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.block.AdvancedVendingBlock;
import iskallia.vault.block.BowHatBlock;
import iskallia.vault.block.CapacitorBlock;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.FinalVaultPortalBlock;
import iskallia.vault.block.GlobalTraderBlock;
import iskallia.vault.block.KeyPressBlock;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.MVPCrownBlock;
import iskallia.vault.block.MazeBlock;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.PuzzleRuneBlock;
import iskallia.vault.block.RelicStatueBlock;
import iskallia.vault.block.StatueDragonHeadBlock;
import iskallia.vault.block.VaultAltarBlock;
import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.block.VaultBedrockBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultRuneBlock;
import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.entity.AdvancedVendingTileEntity;
import iskallia.vault.block.entity.CapacitorTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.block.entity.GlobalTraderTileEntity;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.PlayerStatueTileEntity;
import iskallia.vault.block.entity.RelicStatueTileEntity;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.block.entity.VaultRuneTileEntity;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.block.item.AdvancedVendingMachineBlockItem;
import iskallia.vault.block.item.GlobalTraderBlockItem;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.PlayerStatueBlockItem;
import iskallia.vault.block.item.RelicStatueBlockItem;
import iskallia.vault.block.item.VendingMachineBlockItem;
import iskallia.vault.block.render.AdvancedVendingRenderer;
import iskallia.vault.block.render.CryoChamberRenderer;
import iskallia.vault.block.render.GlobalTraderRenderer;
import iskallia.vault.block.render.LootStatueRenderer;
import iskallia.vault.block.render.PlayerStatueRenderer;
import iskallia.vault.block.render.RelicStatueRenderer;
import iskallia.vault.block.render.VaultAltarRenderer;
import iskallia.vault.block.render.VaultRuneRenderer;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.util.StatueType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModBlocks {
   public static final VaultPortalBlock VAULT_PORTAL = new VaultPortalBlock();
   public static final FinalVaultPortalBlock FINAL_VAULT_PORTAL = new FinalVaultPortalBlock();
   public static final VaultAltarBlock VAULT_ALTAR = new VaultAltarBlock();
   public static final OreBlock ALEXANDRITE_ORE = new VaultOreBlock();
   public static final OreBlock BENITOITE_ORE = new VaultOreBlock();
   public static final OreBlock LARIMAR_ORE = new VaultOreBlock();
   public static final OreBlock BLACK_OPAL_ORE = new VaultOreBlock();
   public static final OreBlock PAINITE_ORE = new VaultOreBlock();
   public static final OreBlock ISKALLIUM_ORE = new VaultOreBlock();
   public static final OreBlock RENIUM_ORE = new VaultOreBlock();
   public static final OreBlock GORGINITE_ORE = new VaultOreBlock();
   public static final OreBlock SPARKLETINE_ORE = new VaultOreBlock();
   public static final OreBlock WUTODIE_ORE = new VaultOreBlock();
   public static final OreBlock VAULT_ROCK_ORE = new VaultOreBlock();
   public static final DoorBlock ALEXANDRITE_DOOR = new VaultDoorBlock(ModItems.ALEXANDRITE_KEY);
   public static final DoorBlock BENITOITE_DOOR = new VaultDoorBlock(ModItems.BENITOITE_KEY);
   public static final DoorBlock LARIMAR_DOOR = new VaultDoorBlock(ModItems.LARIMAR_KEY);
   public static final DoorBlock BLACK_OPAL_DOOR = new VaultDoorBlock(ModItems.BLACK_OPAL_KEY);
   public static final DoorBlock PAINITE_DOOR = new VaultDoorBlock(ModItems.PAINITE_KEY);
   public static final DoorBlock ISKALLIUM_DOOR = new VaultDoorBlock(ModItems.ISKALLIUM_KEY);
   public static final DoorBlock RENIUM_DOOR = new VaultDoorBlock(ModItems.RENIUM_KEY);
   public static final DoorBlock GORGINITE_DOOR = new VaultDoorBlock(ModItems.GORGINITE_KEY);
   public static final DoorBlock SPARKLETINE_DOOR = new VaultDoorBlock(ModItems.SPARKLETINE_KEY);
   public static final DoorBlock WUTODIE_DOOR = new VaultDoorBlock(ModItems.WUTODIE_KEY);
   public static final VaultRuneBlock VAULT_RUNE_BLOCK = new VaultRuneBlock();
   public static final VaultArtifactBlock ARTIFACT_1 = new VaultArtifactBlock(1);
   public static final VaultArtifactBlock ARTIFACT_2 = new VaultArtifactBlock(2);
   public static final VaultArtifactBlock ARTIFACT_3 = new VaultArtifactBlock(3);
   public static final VaultArtifactBlock ARTIFACT_4 = new VaultArtifactBlock(4);
   public static final VaultArtifactBlock ARTIFACT_5 = new VaultArtifactBlock(5);
   public static final VaultArtifactBlock ARTIFACT_6 = new VaultArtifactBlock(6);
   public static final VaultArtifactBlock ARTIFACT_7 = new VaultArtifactBlock(7);
   public static final VaultArtifactBlock ARTIFACT_8 = new VaultArtifactBlock(8);
   public static final VaultArtifactBlock ARTIFACT_9 = new VaultArtifactBlock(9);
   public static final VaultArtifactBlock ARTIFACT_10 = new VaultArtifactBlock(10);
   public static final VaultArtifactBlock ARTIFACT_11 = new VaultArtifactBlock(11);
   public static final VaultArtifactBlock ARTIFACT_12 = new VaultArtifactBlock(12);
   public static final VaultArtifactBlock ARTIFACT_13 = new VaultArtifactBlock(13);
   public static final VaultArtifactBlock ARTIFACT_14 = new VaultArtifactBlock(14);
   public static final VaultArtifactBlock ARTIFACT_15 = new VaultArtifactBlock(15);
   public static final VaultArtifactBlock ARTIFACT_16 = new VaultArtifactBlock(16);
   public static final VaultCrateBlock VAULT_CRATE = new VaultCrateBlock();
   public static final VaultCrateBlock VAULT_CRATE_ARENA = new VaultCrateBlock();
   public static final ObeliskBlock OBELISK = new ObeliskBlock();
   public static final MVPCrownBlock MVP_CROWN = new MVPCrownBlock();
   public static final PlayerStatueBlock PLAYER_STATUE = new PlayerStatueBlock();
   public static final VendingMachineBlock VENDING_MACHINE = new VendingMachineBlock();
   public static final AdvancedVendingBlock ADVANCED_VENDING_MACHINE = new AdvancedVendingBlock();
   public static final VaultBedrockBlock VAULT_BEDROCK = new VaultBedrockBlock();
   public static final RelicStatueBlock RELIC_STATUE = new RelicStatueBlock();
   public static final LootStatueBlock GIFT_NORMAL_STATUE = new LootStatueBlock(StatueType.GIFT_NORMAL);
   public static final LootStatueBlock GIFT_MEGA_STATUE = new LootStatueBlock(StatueType.GIFT_MEGA);
   public static final BowHatBlock BOW_HAT = new BowHatBlock();
   public static final StatueDragonHeadBlock STATUE_DRAGON_HEAD = new StatueDragonHeadBlock();
   public static final LootStatueBlock VAULT_PLAYER_LOOT_STATUE = new LootStatueBlock(StatueType.VAULT_BOSS);
   public static final LootStatueBlock ARENA_PLAYER_LOOT_STATUE = new LootStatueBlock(StatueType.VAULT_BOSS);
   public static final CryoChamberBlock CRYO_CHAMBER = new CryoChamberBlock();
   public static final CapacitorBlock CAPACITOR = new CapacitorBlock();
   public static final KeyPressBlock KEY_PRESS = new KeyPressBlock();
   public static final Block VAULT_DIAMOND_BLOCK = new Block(Properties.func_200950_a(Blocks.field_150484_ah));
   public static final GlobalTraderBlock GLOBAL_TRADER = new GlobalTraderBlock();
   public static final MazeBlock MAZE_BLOCK = new MazeBlock();
   public static final PuzzleRuneBlock PUZZLE_RUNE_BLOCK = new PuzzleRuneBlock();
   public static final PlayerStatueBlockItem PLAYER_STATUE_BLOCK_ITEM = new PlayerStatueBlockItem();
   public static final RelicStatueBlockItem RELIC_STATUE_BLOCK_ITEM = new RelicStatueBlockItem();
   public static final LootStatueBlockItem GIFT_NORMAL_STATUE_BLOCK_ITEM = new LootStatueBlockItem(GIFT_NORMAL_STATUE);
   public static final LootStatueBlockItem GIFT_MEGA_STATUE_BLOCK_ITEM = new LootStatueBlockItem(GIFT_MEGA_STATUE);
   public static final LootStatueBlockItem VAULT_PLAYER_LOOT_STATUE_BLOCK_ITEM = new LootStatueBlockItem(VAULT_PLAYER_LOOT_STATUE);
   public static final LootStatueBlockItem ARENA_PLAYER_LOOT_STATUE_BLOCK_ITEM = new LootStatueBlockItem(ARENA_PLAYER_LOOT_STATUE);
   public static final VendingMachineBlockItem VENDING_MACHINE_BLOCK_ITEM = new VendingMachineBlockItem(VENDING_MACHINE);
   public static final AdvancedVendingMachineBlockItem ADVANCED_VENDING_BLOCK_ITEM = new AdvancedVendingMachineBlockItem(ADVANCED_VENDING_MACHINE);
   public static final GlobalTraderBlockItem GLOBAL_TRADER_BLOCK_ITEM = new GlobalTraderBlockItem(GLOBAL_TRADER);
   public static final PuzzleRuneBlock.Item PUZZLE_RUNE_BLOCK_ITEM = new PuzzleRuneBlock.Item(
      PUZZLE_RUNE_BLOCK, new net.minecraft.item.Item.Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1)
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
   public static final TileEntityType<PlayerStatueTileEntity> PLAYER_STATUE_TILE_ENTITY = Builder.func_223042_a(
         PlayerStatueTileEntity::new, new Block[]{PLAYER_STATUE}
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
         LootStatueTileEntity::new, new Block[]{GIFT_NORMAL_STATUE, GIFT_MEGA_STATUE, VAULT_PLAYER_LOOT_STATUE, ARENA_PLAYER_LOOT_STATUE}
      )
      .func_206865_a(null);
   public static final TileEntityType<CryoChamberTileEntity> CRYO_CHAMBER_TILE_ENTITY = Builder.func_223042_a(
         CryoChamberTileEntity::new, new Block[]{CRYO_CHAMBER}
      )
      .func_206865_a(null);
   public static final TileEntityType<CapacitorTileEntity> CAPACITOR_TILE_ENTITY = Builder.func_223042_a(CapacitorTileEntity::new, new Block[]{CAPACITOR})
      .func_206865_a(null);
   public static final TileEntityType<GlobalTraderTileEntity> GLOBAL_TRADER_TILE_ENTITY = Builder.func_223042_a(
         GlobalTraderTileEntity::new, new Block[]{GLOBAL_TRADER}
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
      registerBlock(event, RENIUM_ORE, Vault.id("ore_renium"));
      registerBlock(event, GORGINITE_ORE, Vault.id("ore_gorginite"));
      registerBlock(event, SPARKLETINE_ORE, Vault.id("ore_sparkletine"));
      registerBlock(event, WUTODIE_ORE, Vault.id("ore_wutodie"));
      registerBlock(event, VAULT_ROCK_ORE, Vault.id("ore_vault_rock"));
      registerBlock(event, ALEXANDRITE_DOOR, Vault.id("door_alexandrite"));
      registerBlock(event, BENITOITE_DOOR, Vault.id("door_benitoite"));
      registerBlock(event, LARIMAR_DOOR, Vault.id("door_larimar"));
      registerBlock(event, BLACK_OPAL_DOOR, Vault.id("door_black_opal"));
      registerBlock(event, PAINITE_DOOR, Vault.id("door_painite"));
      registerBlock(event, ISKALLIUM_DOOR, Vault.id("door_iskallium"));
      registerBlock(event, RENIUM_DOOR, Vault.id("door_renium"));
      registerBlock(event, GORGINITE_DOOR, Vault.id("door_gorginite"));
      registerBlock(event, SPARKLETINE_DOOR, Vault.id("door_sparkletine"));
      registerBlock(event, WUTODIE_DOOR, Vault.id("door_wutodie"));
      registerBlock(event, VAULT_RUNE_BLOCK, Vault.id("vault_rune_block"));
      registerBlock(event, ARTIFACT_1, Vault.id("artifact_1"));
      registerBlock(event, ARTIFACT_2, Vault.id("artifact_2"));
      registerBlock(event, ARTIFACT_3, Vault.id("artifact_3"));
      registerBlock(event, ARTIFACT_4, Vault.id("artifact_4"));
      registerBlock(event, ARTIFACT_5, Vault.id("artifact_5"));
      registerBlock(event, ARTIFACT_6, Vault.id("artifact_6"));
      registerBlock(event, ARTIFACT_7, Vault.id("artifact_7"));
      registerBlock(event, ARTIFACT_8, Vault.id("artifact_8"));
      registerBlock(event, ARTIFACT_9, Vault.id("artifact_9"));
      registerBlock(event, ARTIFACT_10, Vault.id("artifact_10"));
      registerBlock(event, ARTIFACT_11, Vault.id("artifact_11"));
      registerBlock(event, ARTIFACT_12, Vault.id("artifact_12"));
      registerBlock(event, ARTIFACT_13, Vault.id("artifact_13"));
      registerBlock(event, ARTIFACT_14, Vault.id("artifact_14"));
      registerBlock(event, ARTIFACT_15, Vault.id("artifact_15"));
      registerBlock(event, ARTIFACT_16, Vault.id("artifact_16"));
      registerBlock(event, VAULT_CRATE, Vault.id("vault_crate"));
      registerBlock(event, VAULT_CRATE_ARENA, Vault.id("vault_crate_arena"));
      registerBlock(event, OBELISK, Vault.id("obelisk"));
      registerBlock(event, MVP_CROWN, Vault.id("mvp_crown"));
      registerBlock(event, PLAYER_STATUE, Vault.id("player_statue"));
      registerBlock(event, VENDING_MACHINE, Vault.id("vending_machine"));
      registerBlock(event, ADVANCED_VENDING_MACHINE, Vault.id("advanced_vending_machine"));
      registerBlock(event, VAULT_BEDROCK, Vault.id("vault_bedrock"));
      registerBlock(event, RELIC_STATUE, Vault.id("relic_statue"));
      registerBlock(event, GIFT_NORMAL_STATUE, Vault.id("gift_normal_statue"));
      registerBlock(event, GIFT_MEGA_STATUE, Vault.id("gift_mega_statue"));
      registerBlock(event, BOW_HAT, Vault.id("bow_hat"));
      registerBlock(event, STATUE_DRAGON_HEAD, Vault.id("statue_dragon"));
      registerBlock(event, VAULT_PLAYER_LOOT_STATUE, Vault.id("vault_player_loot_statue"));
      registerBlock(event, ARENA_PLAYER_LOOT_STATUE, Vault.id("arena_player_loot_statue"));
      registerBlock(event, CRYO_CHAMBER, Vault.id("cryo_chamber"));
      registerBlock(event, CAPACITOR, Vault.id("capacitor"));
      registerBlock(event, KEY_PRESS, Vault.id("key_press"));
      registerBlock(event, VAULT_DIAMOND_BLOCK, Vault.id("vault_diamond_block"));
      registerBlock(event, GLOBAL_TRADER, Vault.id("global_trader"));
      registerBlock(event, MAZE_BLOCK, Vault.id("maze_block"));
      registerBlock(event, PUZZLE_RUNE_BLOCK, Vault.id("puzzle_rune_block"));
   }

   public static void registerTileEntities(Register<TileEntityType<?>> event) {
      registerTileEntity(event, VAULT_ALTAR_TILE_ENTITY, Vault.id("vault_altar_tile_entity"));
      registerTileEntity(event, VAULT_RUNE_TILE_ENTITY, Vault.id("vault_rune_tile_entity"));
      registerTileEntity(event, VAULT_CRATE_TILE_ENTITY, Vault.id("vault_crate_tile_entity"));
      registerTileEntity(event, VAULT_PORTAL_TILE_ENTITY, Vault.id("vault_portal_tile_entity"));
      registerTileEntity(event, PLAYER_STATUE_TILE_ENTITY, Vault.id("player_statue_tile_entity"));
      registerTileEntity(event, VENDING_MACHINE_TILE_ENTITY, Vault.id("vending_machine_tile_entity"));
      registerTileEntity(event, ADVANCED_VENDING_MACHINE_TILE_ENTITY, Vault.id("advanced_vending_machine_tile_entity"));
      registerTileEntity(event, RELIC_STATUE_TILE_ENTITY, Vault.id("relic_statue_tile_entity"));
      registerTileEntity(event, LOOT_STATUE_TILE_ENTITY, Vault.id("loot_statue_tile_entity"));
      registerTileEntity(event, CRYO_CHAMBER_TILE_ENTITY, Vault.id("cryo_chamber_tile_entity"));
      registerTileEntity(event, CAPACITOR_TILE_ENTITY, Vault.id("capacitor_tile_entity"));
      registerTileEntity(event, GLOBAL_TRADER_TILE_ENTITY, Vault.id("global_trader_tile_entity"));
   }

   public static void registerTileEntityRenderers() {
      ClientRegistry.bindTileEntityRenderer(VAULT_ALTAR_TILE_ENTITY, VaultAltarRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VAULT_RUNE_TILE_ENTITY, VaultRuneRenderer::new);
      ClientRegistry.bindTileEntityRenderer(PLAYER_STATUE_TILE_ENTITY, PlayerStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(VENDING_MACHINE_TILE_ENTITY, VendingMachineRenderer::new);
      ClientRegistry.bindTileEntityRenderer(ADVANCED_VENDING_MACHINE_TILE_ENTITY, AdvancedVendingRenderer::new);
      ClientRegistry.bindTileEntityRenderer(RELIC_STATUE_TILE_ENTITY, RelicStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(LOOT_STATUE_TILE_ENTITY, LootStatueRenderer::new);
      ClientRegistry.bindTileEntityRenderer(CRYO_CHAMBER_TILE_ENTITY, CryoChamberRenderer::new);
      ClientRegistry.bindTileEntityRenderer(GLOBAL_TRADER_TILE_ENTITY, GlobalTraderRenderer::new);
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
      registerBlockItem(event, RENIUM_ORE);
      registerBlockItem(event, GORGINITE_ORE);
      registerBlockItem(event, SPARKLETINE_ORE);
      registerBlockItem(event, WUTODIE_ORE);
      registerBlockItem(event, VAULT_ROCK_ORE);
      registerTallBlockItem(event, ALEXANDRITE_DOOR);
      registerTallBlockItem(event, BENITOITE_DOOR);
      registerTallBlockItem(event, LARIMAR_DOOR);
      registerTallBlockItem(event, BLACK_OPAL_DOOR);
      registerTallBlockItem(event, PAINITE_DOOR);
      registerTallBlockItem(event, ISKALLIUM_DOOR);
      registerTallBlockItem(event, RENIUM_DOOR);
      registerTallBlockItem(event, GORGINITE_DOOR);
      registerTallBlockItem(event, SPARKLETINE_DOOR);
      registerTallBlockItem(event, WUTODIE_DOOR);
      registerBlockItem(event, VAULT_RUNE_BLOCK);
      registerBlockItem(event, ARTIFACT_1, 1);
      registerBlockItem(event, ARTIFACT_2, 1);
      registerBlockItem(event, ARTIFACT_3, 1);
      registerBlockItem(event, ARTIFACT_4, 1);
      registerBlockItem(event, ARTIFACT_5, 1);
      registerBlockItem(event, ARTIFACT_6, 1);
      registerBlockItem(event, ARTIFACT_7, 1);
      registerBlockItem(event, ARTIFACT_8, 1);
      registerBlockItem(event, ARTIFACT_9, 1);
      registerBlockItem(event, ARTIFACT_10, 1);
      registerBlockItem(event, ARTIFACT_11, 1);
      registerBlockItem(event, ARTIFACT_12, 1);
      registerBlockItem(event, ARTIFACT_13, 1);
      registerBlockItem(event, ARTIFACT_14, 1);
      registerBlockItem(event, ARTIFACT_15, 1);
      registerBlockItem(event, ARTIFACT_16, 1);
      registerBlockItem(event, VAULT_CRATE, 1, true);
      registerBlockItem(event, VAULT_CRATE_ARENA, 1, true);
      registerBlockItem(event, OBELISK, 1);
      registerBlockItem(event, MVP_CROWN, 1);
      registerBlockItem(event, PLAYER_STATUE, PLAYER_STATUE_BLOCK_ITEM);
      registerBlockItem(event, VENDING_MACHINE, VENDING_MACHINE_BLOCK_ITEM);
      registerBlockItem(event, ADVANCED_VENDING_MACHINE, ADVANCED_VENDING_BLOCK_ITEM);
      registerBlockItem(event, VAULT_BEDROCK);
      registerBlockItem(event, RELIC_STATUE, RELIC_STATUE_BLOCK_ITEM);
      registerBlockItem(event, GIFT_NORMAL_STATUE, GIFT_NORMAL_STATUE_BLOCK_ITEM);
      registerBlockItem(event, GIFT_MEGA_STATUE, GIFT_MEGA_STATUE_BLOCK_ITEM);
      registerBlockItem(event, BOW_HAT, 1);
      registerBlockItem(event, STATUE_DRAGON_HEAD, 1);
      registerBlockItem(event, VAULT_PLAYER_LOOT_STATUE, VAULT_PLAYER_LOOT_STATUE_BLOCK_ITEM);
      registerBlockItem(event, ARENA_PLAYER_LOOT_STATUE, ARENA_PLAYER_LOOT_STATUE_BLOCK_ITEM);
      registerBlockItem(event, CRYO_CHAMBER);
      registerBlockItem(event, CAPACITOR);
      registerBlockItem(event, KEY_PRESS);
      registerBlockItem(event, VAULT_DIAMOND_BLOCK);
      registerBlockItem(event, GLOBAL_TRADER, GLOBAL_TRADER_BLOCK_ITEM);
      registerBlockItem(event, PUZZLE_RUNE_BLOCK, PUZZLE_RUNE_BLOCK_ITEM);
   }

   private static void registerBlock(Register<Block> event, Block block, ResourceLocation id) {
      block.setRegistryName(id);
      event.getRegistry().register(block);
   }

   private static <T extends TileEntity> void registerTileEntity(Register<TileEntityType<?>> event, TileEntityType<?> type, ResourceLocation id) {
      type.setRegistryName(id);
      event.getRegistry().register(type);
   }

   private static void registerBlockItem(Register<Item> event, Block block) {
      BlockItem blockItem = new BlockItem(block, new net.minecraft.item.Item.Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(64));
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerBlockItem(Register<Item> event, Block block, int maxStackSize, final boolean showGlint) {
      BlockItem blockItem = new BlockItem(block, new net.minecraft.item.Item.Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(maxStackSize)) {
         public boolean func_77636_d(ItemStack stack) {
            return showGlint;
         }
      };
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerBlockItem(Register<Item> event, Block block, int maxStackSize) {
      BlockItem blockItem = new BlockItem(block, new net.minecraft.item.Item.Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(maxStackSize));
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerBlockItem(Register<Item> event, Block block, BlockItem blockItem) {
      blockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(blockItem);
   }

   private static void registerTallBlockItem(Register<Item> event, Block block) {
      TallBlockItem tallBlockItem = new TallBlockItem(block, new net.minecraft.item.Item.Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(64));
      tallBlockItem.setRegistryName(block.getRegistryName());
      event.getRegistry().register(tallBlockItem);
   }
}
