package iskallia.vault.init;

import iskallia.vault.container.BountyContainer;
import iskallia.vault.container.InscriptionTableContainer;
import iskallia.vault.container.LootStatueContainer;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.container.RelicPedestalContainer;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.container.SpiritExtractorContainer;
import iskallia.vault.container.StatisticsTabContainer;
import iskallia.vault.container.ToolStationContainer;
import iskallia.vault.container.ToolViseContainerMenu;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.VaultCharmControllerContainer;
import iskallia.vault.container.VaultCrateContainer;
import iskallia.vault.container.VaultDiffuserContainer;
import iskallia.vault.container.VaultEndContainer;
import iskallia.vault.container.VaultEnhancementAltarContainer;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.container.VaultRecyclerContainer;
import iskallia.vault.container.WardrobeContainer;
import iskallia.vault.container.inventory.CatalystInfusionTableContainer;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.container.inventory.MagnetTableContainerMenu;
import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.stat.StatTotals;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import iskallia.vault.skill.talent.TalentTree;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModContainers {
   public static MenuType<StatisticsTabContainer> STATISTICS_TAB_CONTAINER;
   public static MenuType<NBTElementContainer<AbilityTree>> ABILITY_TAB_CONTAINER;
   public static MenuType<NBTElementContainer<TalentTree>> TALENT_TAB_CONTAINER;
   public static MenuType<NBTElementContainer<ArchetypeContainer>> ARCHETYPE_TAB_CONTAINER;
   public static MenuType<NBTElementContainer<ResearchTree>> RESEARCH_TAB_CONTAINER;
   public static MenuType<VaultCrateContainer> VAULT_CRATE_CONTAINER;
   public static MenuType<RenamingContainer> RENAMING_CONTAINER;
   public static MenuType<LootStatueContainer> LOOT_STATUE_CONTAINER;
   public static MenuType<TransmogTableContainer> TRANSMOG_TABLE_CONTAINER;
   public static MenuType<ScavengerChestContainer> SCAVENGER_CHEST_CONTAINER;
   public static MenuType<CatalystInfusionTableContainer> CATALYST_INFUSION_TABLE_CONTAINER;
   public static MenuType<ShardPouchContainer> SHARD_POUCH_CONTAINER;
   public static MenuType<ShardTradeContainer> SHARD_TRADE_CONTAINER;
   public static MenuType<CryochamberContainer> CRYOCHAMBER_CONTAINER;
   public static MenuType<EtchingTradeContainer> ETCHING_TRADE_CONTAINER;
   public static MenuType<VaultCharmControllerContainer> VAULT_CHARM_CONTROLLER_CONTAINER;
   public static MenuType<ToolViseContainerMenu> TOOL_VISE_CONTAINER;
   public static MenuType<MagnetTableContainerMenu> MAGNET_TABLE_CONTAINER;
   public static MenuType<VaultForgeContainer> VAULT_FORGE_CONTAINER;
   public static MenuType<ToolStationContainer> TOOL_STATION_CONTAINER;
   public static MenuType<InscriptionTableContainer> INSCRIPTION_TABLE_CONTAINER;
   public static MenuType<VaultArtisanStationContainer> VAULT_ARTISAN_STATION_CONTAINER;
   public static MenuType<VaultRecyclerContainer> VAULT_RECYCLER_CONTAINER;
   public static MenuType<VaultDiffuserContainer> VAULT_DIFFUSER_CONTAINER;
   public static MenuType<VaultEndContainer> VAULT_END_CONTAINER;
   public static MenuType<RelicPedestalContainer> RELIC_PEDESTAL_CONTAINER;
   public static MenuType<SpiritExtractorContainer> SPIRIT_EXTRACTOR_CONTAINER;
   public static MenuType<WardrobeContainer.Gear> WARDROBE_GEAR_CONTAINER;
   public static MenuType<WardrobeContainer.Hotbar> WARDROBE_HOTBAR_CONTAINER;
   public static MenuType<BountyContainer> BOUNTY_CONTAINER;
   public static MenuType<VaultEnhancementAltarContainer> ENHANCEMENT_ALTAR_CONTAINER;
   public static MenuType<ModifierWorkbenchContainer> MODIFIER_WORKBENCH_CONTAINER;

   public static void register(Register<MenuType<?>> event) {
      STATISTICS_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         StatTotals statTotals = new StatTotals();
         statTotals.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundTag()));
         return new StatisticsTabContainer(windowId, inventory, statTotals);
      });
      ABILITY_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         UUID uniqueID = inventory.player.getUUID();
         AbilityTree abilityTree = new AbilityTree(uniqueID);
         abilityTree.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundTag()));
         return new NBTElementContainer(() -> ABILITY_TAB_CONTAINER, windowId, inventory.player, abilityTree);
      });
      TALENT_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         UUID uniqueID = inventory.player.getUUID();
         TalentTree talentTree = new TalentTree(uniqueID);
         talentTree.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundTag()));
         return new NBTElementContainer(() -> TALENT_TAB_CONTAINER, windowId, inventory.player, talentTree);
      });
      ARCHETYPE_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         UUID uniqueID = inventory.player.getUUID();
         ArchetypeContainer archetypeContainer = new ArchetypeContainer(uniqueID);
         archetypeContainer.deserializeNBT(Optional.ofNullable(buffer.readNbt()).orElse(new CompoundTag()));
         return new NBTElementContainer(() -> ARCHETYPE_TAB_CONTAINER, windowId, inventory.player, archetypeContainer);
      });
      RESEARCH_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         ResearchTree researchTree = new ResearchTree(buffer.readNbt());
         return new NBTElementContainer(() -> RESEARCH_TAB_CONTAINER, windowId, inventory.player, researchTree);
      });
      VAULT_CRATE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultCrateContainer(windowId, world, pos, inventory, inventory.player);
      });
      RENAMING_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         CompoundTag nbt = buffer.readNbt();
         return new RenamingContainer(windowId, nbt == null ? new CompoundTag() : nbt);
      });
      LOOT_STATUE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         CompoundTag nbt = buffer.readNbt();
         return new LootStatueContainer(windowId, nbt == null ? new CompoundTag() : nbt);
      });
      TRANSMOG_TABLE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new TransmogTableContainer(windowId, world, pos, inventory);
      });
      SCAVENGER_CHEST_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         SimpleContainer inv = new SimpleContainer(45);
         return new ScavengerChestContainer(windowId, inventory, inv, inv);
      });
      CATALYST_INFUSION_TABLE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new CatalystInfusionTableContainer(windowId, world, pos, inventory);
      });
      SHARD_POUCH_CONTAINER = IForgeMenuType.create((windowId, inventory, data) -> {
         int pouchSlot = data.readInt();
         return new ShardPouchContainer(windowId, inventory, pouchSlot);
      });
      SHARD_TRADE_CONTAINER = IForgeMenuType.create((windowId, inventory, data) -> new ShardTradeContainer(windowId, inventory));
      CRYOCHAMBER_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new CryochamberContainer(windowId, world, pos, inventory);
      });
      ETCHING_TRADE_CONTAINER = IForgeMenuType.create((windowId, inventory, data) -> new EtchingTradeContainer(windowId, inventory, data.readInt()));
      VAULT_CHARM_CONTROLLER_CONTAINER = IForgeMenuType.create(
         (windowId, inventory, data) -> new VaultCharmControllerContainer(windowId, inventory, data.readNbt())
      );
      TOOL_VISE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new ToolViseContainerMenu(windowId, world, pos, inventory);
      });
      MAGNET_TABLE_CONTAINER = IForgeMenuType.create(MagnetTableContainerMenu::new);
      VAULT_FORGE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultForgeContainer(windowId, world, pos, inventory);
      });
      TOOL_STATION_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new ToolStationContainer(windowId, world, pos, inventory);
      });
      INSCRIPTION_TABLE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new InscriptionTableContainer(windowId, world, pos, inventory);
      });
      VAULT_ARTISAN_STATION_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultArtisanStationContainer(windowId, world, pos, inventory);
      });
      VAULT_RECYCLER_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultRecyclerContainer(windowId, world, pos, inventory);
      });
      VAULT_DIFFUSER_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultDiffuserContainer(windowId, world, pos, inventory);
      });
      VAULT_END_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         ArrayBitBuffer buffer2 = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
         return new VaultEndContainer(windowId, inventory, new VaultSnapshot(buffer2));
      });
      RELIC_PEDESTAL_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Player player = inventory.player;
         BlockPos blockPos = buffer.readBlockPos();
         return new RelicPedestalContainer(windowId, player, blockPos);
      });
      SPIRIT_EXTRACTOR_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         BlockPos blockPos = buffer.readBlockPos();
         return new SpiritExtractorContainer(windowId, inventory, blockPos);
      });
      WARDROBE_GEAR_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         BlockPos blockPos = buffer.readBlockPos();
         return new WardrobeContainer.Gear(windowId, inventory, blockPos);
      });
      WARDROBE_HOTBAR_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         BlockPos blockPos = buffer.readBlockPos();
         return new WardrobeContainer.Hotbar(windowId, inventory, blockPos);
      });
      BOUNTY_CONTAINER = IForgeMenuType.create((windowId, inv, data) -> {
         CompoundTag tag = data.readNbt();
         Level world = inv.player.getCommandSenderWorld();
         return new BountyContainer(windowId, world, inv, tag == null ? new CompoundTag() : tag);
      });
      ENHANCEMENT_ALTAR_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new VaultEnhancementAltarContainer(windowId, world, pos, inventory);
      });
      MODIFIER_WORKBENCH_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
         Level world = inventory.player.getCommandSenderWorld();
         BlockPos pos = buffer.readBlockPos();
         return new ModifierWorkbenchContainer(windowId, world, pos, inventory);
      });
      event.getRegistry()
         .registerAll(
            new MenuType[]{
               (MenuType)STATISTICS_TAB_CONTAINER.setRegistryName("statistics_tab"),
               (MenuType)ABILITY_TAB_CONTAINER.setRegistryName("ability_tab"),
               (MenuType)TALENT_TAB_CONTAINER.setRegistryName("talent_tab"),
               (MenuType)ARCHETYPE_TAB_CONTAINER.setRegistryName("archetype_tab"),
               (MenuType)RESEARCH_TAB_CONTAINER.setRegistryName("research_tab"),
               (MenuType)VAULT_CRATE_CONTAINER.setRegistryName("vault_crate"),
               (MenuType)RENAMING_CONTAINER.setRegistryName("renaming_container"),
               (MenuType)LOOT_STATUE_CONTAINER.setRegistryName("omega_statue_container"),
               (MenuType)TRANSMOG_TABLE_CONTAINER.setRegistryName("transmog_table_container"),
               (MenuType)SCAVENGER_CHEST_CONTAINER.setRegistryName("scavenger_chest_container"),
               (MenuType)CATALYST_INFUSION_TABLE_CONTAINER.setRegistryName("catalyst_infusion_table_container"),
               (MenuType)SHARD_POUCH_CONTAINER.setRegistryName("shard_pouch_container"),
               (MenuType)SHARD_TRADE_CONTAINER.setRegistryName("shard_trade_container"),
               (MenuType)CRYOCHAMBER_CONTAINER.setRegistryName("cryochamber_container"),
               (MenuType)ETCHING_TRADE_CONTAINER.setRegistryName("etching_trade_container"),
               (MenuType)VAULT_CHARM_CONTROLLER_CONTAINER.setRegistryName("looter_charm_controller_container"),
               (MenuType)TOOL_VISE_CONTAINER.setRegistryName("tool_vise_container"),
               (MenuType)MAGNET_TABLE_CONTAINER.setRegistryName("magnet_table_container"),
               (MenuType)VAULT_FORGE_CONTAINER.setRegistryName("vault_forge_container"),
               (MenuType)TOOL_STATION_CONTAINER.setRegistryName("tool_station_container"),
               (MenuType)INSCRIPTION_TABLE_CONTAINER.setRegistryName("inscription_table_container"),
               (MenuType)VAULT_ARTISAN_STATION_CONTAINER.setRegistryName("vault_artisan_station_container"),
               (MenuType)VAULT_RECYCLER_CONTAINER.setRegistryName("vault_recycler_container"),
               (MenuType)VAULT_DIFFUSER_CONTAINER.setRegistryName("vault_diffuser_container"),
               (MenuType)VAULT_END_CONTAINER.setRegistryName("vault_end_container"),
               (MenuType)RELIC_PEDESTAL_CONTAINER.setRegistryName("relic_pedestal_container"),
               (MenuType)SPIRIT_EXTRACTOR_CONTAINER.setRegistryName("spirit_extractor_container"),
               (MenuType)WARDROBE_GEAR_CONTAINER.setRegistryName("wardrobe_gear_container"),
               (MenuType)WARDROBE_HOTBAR_CONTAINER.setRegistryName("wardrobe_hotbar_container"),
               (MenuType)BOUNTY_CONTAINER.setRegistryName("bounty_container"),
               (MenuType)ENHANCEMENT_ALTAR_CONTAINER.setRegistryName("enhancement_altar_container"),
               (MenuType)MODIFIER_WORKBENCH_CONTAINER.setRegistryName("modifier_workbench_container")
            }
         );
   }
}
