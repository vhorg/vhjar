package iskallia.vault.init;

import iskallia.vault.container.AdvancedVendingContainer;
import iskallia.vault.container.GlobalDifficultyContainer;
import iskallia.vault.container.KeyPressContainer;
import iskallia.vault.container.OmegaStatueContainer;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.VaultCrateContainer;
import iskallia.vault.container.VendingMachineContainer;
import iskallia.vault.container.inventory.CatalystDecryptionContainer;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.container.inventory.ShardTradeContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.network.IContainerFactory;

public class ModContainers {
   public static ContainerType<SkillTreeContainer> SKILL_TREE_CONTAINER;
   public static ContainerType<VaultCrateContainer> VAULT_CRATE_CONTAINER;
   public static ContainerType<VendingMachineContainer> VENDING_MACHINE_CONTAINER;
   public static ContainerType<AdvancedVendingContainer> ADVANCED_VENDING_MACHINE_CONTAINER;
   public static ContainerType<RenamingContainer> RENAMING_CONTAINER;
   public static ContainerType<KeyPressContainer> KEY_PRESS_CONTAINER;
   public static ContainerType<OmegaStatueContainer> OMEGA_STATUE_CONTAINER;
   public static ContainerType<TransmogTableContainer> TRANSMOG_TABLE_CONTAINER;
   public static ContainerType<ScavengerChestContainer> SCAVENGER_CHEST_CONTAINER;
   public static ContainerType<CatalystDecryptionContainer> CATALYST_DECRYPTION_CONTAINER;
   public static ContainerType<ShardPouchContainer> SHARD_POUCH_CONTAINER;
   public static ContainerType<ShardTradeContainer> SHARD_TRADE_CONTAINER;
   public static ContainerType<CryochamberContainer> CRYOCHAMBER_CONTAINER;
   public static ContainerType<GlobalDifficultyContainer> GLOBAL_DIFFICULTY_CONTAINER;
   public static ContainerType<EtchingTradeContainer> ETCHING_TRADE_CONTAINER;

   public static void register(Register<ContainerType<?>> event) {
      SKILL_TREE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         UUID uniqueID = inventory.field_70458_d.func_110124_au();
         AbilityTree abilityTree = new AbilityTree(uniqueID);
         abilityTree.deserializeNBT(Optional.ofNullable(buffer.func_150793_b()).orElse(new CompoundNBT()));
         TalentTree talentTree = new TalentTree(uniqueID);
         talentTree.deserialize(Optional.ofNullable(buffer.func_150793_b()).orElse(new CompoundNBT()), false);
         ResearchTree researchTree = new ResearchTree(uniqueID);
         researchTree.deserializeNBT(Optional.ofNullable(buffer.func_150793_b()).orElse(new CompoundNBT()));
         return new SkillTreeContainer(windowId, abilityTree, talentTree, researchTree);
      });
      VAULT_CRATE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         return new VaultCrateContainer(windowId, world, pos, inventory, inventory.field_70458_d);
      });
      VENDING_MACHINE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         return new VendingMachineContainer(windowId, world, pos, inventory, inventory.field_70458_d);
      });
      ADVANCED_VENDING_MACHINE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         return new AdvancedVendingContainer(windowId, world, pos, inventory, inventory.field_70458_d);
      });
      RENAMING_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         CompoundNBT nbt = buffer.func_150793_b();
         return new RenamingContainer(windowId, nbt == null ? new CompoundNBT() : nbt);
      });
      KEY_PRESS_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         PlayerEntity player = inventory.field_70458_d;
         return new KeyPressContainer(windowId, player);
      });
      OMEGA_STATUE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         CompoundNBT nbt = buffer.func_150793_b();
         return new OmegaStatueContainer(windowId, nbt == null ? new CompoundNBT() : nbt);
      });
      TRANSMOG_TABLE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         PlayerEntity player = inventory.field_70458_d;
         return new TransmogTableContainer(windowId, player);
      });
      SCAVENGER_CHEST_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         Inventory inv = new Inventory(45);
         return new ScavengerChestContainer(windowId, inventory, inv, inv);
      });
      CATALYST_DECRYPTION_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         return new CatalystDecryptionContainer(windowId, world, pos, inventory);
      });
      SHARD_POUCH_CONTAINER = createContainerType((windowId, inventory, data) -> {
         int pouchSlot = data.readInt();
         return new ShardPouchContainer(windowId, inventory, pouchSlot);
      });
      SHARD_TRADE_CONTAINER = createContainerType((windowId, inventory, data) -> new ShardTradeContainer(windowId, inventory));
      CRYOCHAMBER_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         return new CryochamberContainer(windowId, world, pos, inventory);
      });
      GLOBAL_DIFFICULTY_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         CompoundNBT data = buffer.func_150793_b();
         return new GlobalDifficultyContainer(windowId, data);
      });
      ETCHING_TRADE_CONTAINER = createContainerType((windowId, inventory, data) -> new EtchingTradeContainer(windowId, inventory, data.readInt()));
      event.getRegistry()
         .registerAll(
            new ContainerType[]{
               (ContainerType)SKILL_TREE_CONTAINER.setRegistryName("ability_tree"),
               (ContainerType)VAULT_CRATE_CONTAINER.setRegistryName("vault_crate"),
               (ContainerType)VENDING_MACHINE_CONTAINER.setRegistryName("vending_machine"),
               (ContainerType)ADVANCED_VENDING_MACHINE_CONTAINER.setRegistryName("advanced_vending_machine"),
               (ContainerType)RENAMING_CONTAINER.setRegistryName("renaming_container"),
               (ContainerType)KEY_PRESS_CONTAINER.setRegistryName("key_press_container"),
               (ContainerType)OMEGA_STATUE_CONTAINER.setRegistryName("omega_statue_container"),
               (ContainerType)TRANSMOG_TABLE_CONTAINER.setRegistryName("transmog_table_container"),
               (ContainerType)SCAVENGER_CHEST_CONTAINER.setRegistryName("scavenger_chest_container"),
               (ContainerType)CATALYST_DECRYPTION_CONTAINER.setRegistryName("catalyst_decryption_container"),
               (ContainerType)SHARD_POUCH_CONTAINER.setRegistryName("shard_pouch_container"),
               (ContainerType)SHARD_TRADE_CONTAINER.setRegistryName("shard_trade_container"),
               (ContainerType)CRYOCHAMBER_CONTAINER.setRegistryName("cryochamber_container"),
               (ContainerType)GLOBAL_DIFFICULTY_CONTAINER.setRegistryName("global_difficulty_container"),
               (ContainerType)ETCHING_TRADE_CONTAINER.setRegistryName("etching_trade_container")
            }
         );
   }

   private static <T extends Container> ContainerType<T> createContainerType(IContainerFactory<T> factory) {
      return new ContainerType(factory);
   }
}
