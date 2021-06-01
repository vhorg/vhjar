package iskallia.vault.init;

import iskallia.vault.container.AdvancedVendingContainer;
import iskallia.vault.container.GlobalTraderContainer;
import iskallia.vault.container.KeyPressContainer;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.container.VaultCrateContainer;
import iskallia.vault.container.VendingMachineContainer;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentTree;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
   public static ContainerType<GlobalTraderContainer> TRADER_CONTAINER;

   public static void register(Register<ContainerType<?>> event) {
      SKILL_TREE_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         UUID uniqueID = inventory.field_70458_d.func_110124_au();
         AbilityTree abilityTree = new AbilityTree(uniqueID);
         abilityTree.deserializeNBT(Optional.ofNullable(buffer.func_150793_b()).orElse(new CompoundNBT()));
         TalentTree talentTree = new TalentTree(uniqueID);
         talentTree.deserializeNBT(Optional.ofNullable(buffer.func_150793_b()).orElse(new CompoundNBT()));
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
      TRADER_CONTAINER = createContainerType((windowId, inventory, buffer) -> {
         World world = inventory.field_70458_d.func_130014_f_();
         BlockPos pos = buffer.func_179259_c();
         CompoundNBT nbt = buffer.func_150793_b();
         ListNBT playerTrades = nbt == null ? null : nbt.func_150295_c("PlayerTradesList", 10);
         return new GlobalTraderContainer(windowId, world, pos, inventory, inventory.field_70458_d, playerTrades);
      });
      event.getRegistry()
         .registerAll(
            new ContainerType[]{
               (ContainerType)SKILL_TREE_CONTAINER.setRegistryName("ability_tree"),
               (ContainerType)VAULT_CRATE_CONTAINER.setRegistryName("vault_crate"),
               (ContainerType)VENDING_MACHINE_CONTAINER.setRegistryName("vending_machine"),
               (ContainerType)ADVANCED_VENDING_MACHINE_CONTAINER.setRegistryName("advanced_vending_machine"),
               (ContainerType)RENAMING_CONTAINER.setRegistryName("renaming_container"),
               (ContainerType)KEY_PRESS_CONTAINER.setRegistryName("key_press_container"),
               (ContainerType)TRADER_CONTAINER.setRegistryName("trader_container")
            }
         );
   }

   private static <T extends Container> ContainerType<T> createContainerType(IContainerFactory<T> factory) {
      return new ContainerType(factory);
   }
}
