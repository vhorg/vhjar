package iskallia.vault.world.data;

import iskallia.vault.attribute.RegistryKeyAttribute;
import iskallia.vault.backup.BackupManager;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultMember;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultRaidData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_VaultRaid";
   private VListNBT<VaultRaid, CompoundNBT> vaults = VListNBT.of(VaultRaid::new);
   private Mutable nextVaultPos = BlockPos.field_177992_a.func_239590_i_();
   @Deprecated
   private VMapNBT<UUID, VaultRaid> activeVaults = VMapNBT.ofUUID(VaultRaid::new);

   public VaultRaidData() {
      this("the_vault_VaultRaid");
   }

   public VaultRaidData(String name) {
      super(name);
   }

   public VaultRaid get(UUID vaultId) {
      return vaultId == null
         ? null
         : this.vaults
            .stream()
            .filter(vault -> vaultId.equals(vault.getProperties().getBaseOrDefault(VaultRaid.IDENTIFIER, (UUID)null)))
            .findFirst()
            .orElse(null);
   }

   public VaultRaid getActiveFor(ServerPlayerEntity player) {
      return this.getActiveFor(player.func_110124_au());
   }

   public VaultRaid getActiveFor(UUID playerId) {
      return this.vaults.stream().filter(vault -> vault.getPlayer(playerId).isPresent()).findFirst().orElse(null);
   }

   public VaultRaid getAt(ServerWorld world, BlockPos pos) {
      return this.vaults
         .stream()
         .filter(vault -> world.func_234923_W_() == vault.getProperties().<RegistryKey<World>, RegistryKeyAttribute<World>>getValue(VaultRaid.DIMENSION))
         .filter(vault -> {
            Optional<MutableBoundingBox> box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX);
            return box.isPresent() && box.get().func_175898_b(pos);
         })
         .findFirst()
         .orElse(null);
   }

   public void remove(UUID vaultId) {
      this.vaults.removeIf(vault -> vault.getProperties().getValue(VaultRaid.IDENTIFIER).equals(vaultId));
   }

   public void remove(MinecraftServer server, UUID playerId, VaultRaid vault) {
      if (vault != null) {
         ServerWorld world = server.func_71218_a(vault.getProperties().getValue(VaultRaid.DIMENSION));
         vault.getPlayer(playerId).ifPresent(player -> {
            if (!player.hasExited() && !(player instanceof VaultMember)) {
               VaultRaid.REMOVE_SCAVENGER_ITEMS.then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS).then(VaultRaid.EXIT_SAFELY).execute(vault, player, world);
            }
         });
         PlayerStatsData.get(world).onVaultFinished(playerId, vault);
      }
   }

   public static ItemStack generateRaidRewardCrate() {
      ItemStack stack = new ItemStack(Items.field_221895_gF);
      CrystalData minerData = new CrystalData();
      minerData.setModifiable(false);
      minerData.setCanTriggerInfluences(false);
      minerData.setPreventsRandomModifiers(true);
      minerData.setSelectedObjective(VaultRaid.ARCHITECT_EVENT.get().getId());
      minerData.setTargetObjectiveCount(20);
      minerData.addModifier("Copious");
      minerData.addModifier("Rich");
      minerData.addModifier("Plentiful");
      minerData.addModifier("Endless");
      ItemStack miner = new ItemStack(ModItems.VAULT_CRYSTAL);
      miner.func_196082_o().func_218657_a("CrystalData", minerData.serializeNBT());
      CrystalData digsiteData = new CrystalData();
      digsiteData.setModifiable(false);
      digsiteData.setCanTriggerInfluences(false);
      digsiteData.setPreventsRandomModifiers(true);
      digsiteData.setSelectedObjective(VaultRaid.SCAVENGER_HUNT.get().getId());
      digsiteData.setTargetObjectiveCount(6);
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addGuaranteedRoom("digsite");
      digsiteData.addModifier("Super Lucky");
      digsiteData.addModifier("Super Lucky");
      digsiteData.addModifier("Locked");
      ItemStack digsite = new ItemStack(ModItems.VAULT_CRYSTAL);
      digsite.func_196082_o().func_218657_a("CrystalData", digsiteData.serializeNBT());
      NonNullList<ItemStack> raidContents = NonNullList.func_191196_a();
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.KNOWLEDGE_STAR));
      raidContents.add(new ItemStack(ModItems.KNOWLEDGE_STAR));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.UNIDENTIFIED_TREASURE_KEY));
      raidContents.add(new ItemStack(ModItems.LEGENDARY_TREASURE_OMEGA));
      raidContents.add(miner);
      raidContents.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
      raidContents.add(digsite);
      raidContents.add(new ItemStack(ModItems.LEGENDARY_TREASURE_OMEGA));
      raidContents.add(new ItemStack(ModItems.UNIDENTIFIED_TREASURE_KEY));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.VAULT_PLATINUM));
      raidContents.add(new ItemStack(ModItems.SKILL_ORB));
      raidContents.add(new ItemStack(ModItems.SKILL_ORB));
      raidContents.add(new ItemStack(ModItems.PANDORAS_BOX));
      stack.func_196082_o().func_218657_a("BlockEntityTag", new CompoundNBT());
      ItemStackHelper.func_191282_a(stack.func_196082_o().func_74775_l("BlockEntityTag"), raidContents);
      return stack;
   }

   public VaultRaid startVault(ServerWorld world, VaultRaid.Builder builder) {
      return this.startVault(world, builder, vault -> {});
   }

   public VaultRaid startVault(ServerWorld world, VaultRaid.Builder builder, Consumer<VaultRaid> onBuild) {
      MinecraftServer server = world.func_73046_m();
      VaultRaid vault = builder.build();
      onBuild.accept(vault);
      builder.getLevelInitializer().executeForAllPlayers(vault, world);
      Optional<RegistryKey<World>> dimension = vault.getProperties().getBase(VaultRaid.DIMENSION);
      if (dimension.isPresent()) {
         world = server.func_71218_a(dimension.get());
      } else {
         vault.getProperties().create(VaultRaid.DIMENSION, world.func_234923_W_());
      }

      ServerWorld destination = dimension.isPresent() ? server.func_71218_a(dimension.get()) : world;
      server.func_222817_e(() -> {
         vault.getGenerator().generate(destination, vault, this.nextVaultPos);
         vault.getPlayers().forEach(player -> {
            player.runIfPresent(server, sPlayer -> {
               BackupManager.createPlayerInventorySnapshot(sPlayer);
               if (PlayerSet.isActive(VaultGear.Set.PHOENIX, sPlayer)) {
                  PhoenixSetSnapshotData phoenixSetData = PhoenixSetSnapshotData.get(server);
                  if (phoenixSetData.hasSnapshot(sPlayer)) {
                     phoenixSetData.removeSnapshot(sPlayer);
                  }

                  phoenixSetData.createSnapshot(sPlayer);
               }
            });
            vault.getInitializer().execute(vault, player, destination);
         });
         this.vaults.add(vault);
      });
      return vault;
   }

   public void tick(ServerWorld world) {
      new ArrayList<>(this.vaults)
         .stream()
         .filter(vault -> vault.getProperties().<RegistryKey<World>, RegistryKeyAttribute<World>>getValue(VaultRaid.DIMENSION) == world.func_234923_W_())
         .forEach(vault -> vault.tick(world));
      this.vaults.removeIf(vault -> {
         if (vault.isFinished()) {
            vault.getPlayers().forEach(player -> this.remove(world.func_73046_m(), player.getPlayerId(), vault));
         }

         return vault.isFinished();
      });
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START) {
         get((ServerWorld)event.world).tick((ServerWorld)event.world);
      }
   }

   public boolean func_76188_b() {
      return true;
   }

   public void func_76184_a(CompoundNBT nbt) {
      Map<UUID, VaultRaid> foundVaults = new HashMap<>();
      ListNBT vaults = nbt.func_150295_c("ActiveVaults", 10);

      for (int i = 0; i < vaults.size(); i++) {
         CompoundNBT tag = vaults.func_150305_b(i);
         UUID playerId = UUID.fromString(tag.func_74779_i("Key"));
         VaultRaid vault = new VaultRaid();
         vault.deserializeNBT(tag.func_74775_l("Value"));
         UUID vaultId = vault.getProperties().getBaseOrDefault(VaultRaid.IDENTIFIER, Util.field_240973_b_);
         if (foundVaults.containsKey(vaultId)) {
            vault = foundVaults.get(vaultId);
         } else {
            foundVaults.put(vaultId, vault);
         }

         this.activeVaults.put(playerId, vault);
      }

      this.vaults.deserializeNBT(nbt.func_150295_c("Vaults", 10));
      this.vaults.addAll(this.activeVaults.values());
      int[] pos = nbt.func_74759_k("NextVaultPos");
      this.nextVaultPos = new Mutable(pos[0], pos[1], pos[2]);
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      nbt.func_218657_a("Vaults", this.vaults.serializeNBT());
      nbt.func_74783_a("NextVaultPos", new int[]{this.nextVaultPos.func_177958_n(), this.nextVaultPos.func_177956_o(), this.nextVaultPos.func_177952_p()});
      return nbt;
   }

   public static VaultRaidData get(ServerWorld world) {
      return (VaultRaidData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(VaultRaidData::new, "the_vault_VaultRaid");
   }
}
