package iskallia.vault.world.data;

import iskallia.vault.attribute.RegistryKeyAttribute;
import iskallia.vault.backup.BackupManager;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.world.vault.VaultRaid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
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
   private VMapNBT<UUID, VaultRaid> activeVaults = VMapNBT.ofUUID(VaultRaid::new);
   private Mutable nextVaultPos = BlockPos.field_177992_a.func_239590_i_();

   public VaultRaidData() {
      this("the_vault_VaultRaid");
   }

   public VaultRaidData(String name) {
      super(name);
   }

   public VaultRaid getActiveFor(ServerPlayerEntity player) {
      return this.getActiveFor(player.func_110124_au());
   }

   public VaultRaid getActiveFor(UUID playerId) {
      return this.activeVaults.get(playerId);
   }

   public VaultRaid getAt(ServerWorld world, BlockPos pos) {
      return this.activeVaults
         .values()
         .stream()
         .filter(vault -> world.func_234923_W_() == vault.getProperties().<RegistryKey<World>, RegistryKeyAttribute<World>>getValue(VaultRaid.DIMENSION))
         .filter(vault -> {
            Optional<MutableBoundingBox> box = vault.getProperties().getBase(VaultRaid.BOUNDING_BOX);
            return box.isPresent() && box.get().func_175898_b(pos);
         })
         .findFirst()
         .orElse(null);
   }

   public void remove(MinecraftServer server, UUID playerId) {
      VaultRaid vault = this.activeVaults.remove(playerId);
      if (vault != null) {
         ServerWorld world = server.func_71218_a(vault.getProperties().getValue(VaultRaid.DIMENSION));
         vault.getPlayer(playerId)
            .ifPresent(
               player -> {
                  if (!player.hasExited()) {
                     VaultRaid.REMOVE_SCAVENGER_ITEMS
                        .then(VaultRaid.REMOVE_INVENTORY_RESTORE_SNAPSHOTS)
                        .then(VaultRaid.GRANT_EXP_COMPLETE)
                        .then(VaultRaid.EXIT_SAFELY)
                        .execute(vault, player, world);
                  }
               }
            );
         PlayerStatsData.get(world).onVaultFinished(playerId, vault);
      }
   }

   public VaultRaid startVault(ServerWorld world, VaultRaid.Builder builder) {
      MinecraftServer server = world.func_73046_m();
      VaultRaid vault = builder.build();
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
            player.runIfPresent(server, BackupManager::createPlayerInventorySnapshot);
            this.remove(server, player.getPlayerId());
            this.activeVaults.put(player.getPlayerId(), vault);
            vault.getInitializer().execute(vault, player, destination);
         });
      });
      return vault;
   }

   public void tick(ServerWorld world) {
      Set<VaultRaid> vaults = new HashSet<>(this.activeVaults.values());
      vaults.stream()
         .filter(vault -> vault.getProperties().<RegistryKey<World>, RegistryKeyAttribute<World>>getValue(VaultRaid.DIMENSION) == world.func_234923_W_())
         .forEach(vault -> vault.tick(world));
      Set<UUID> completed = new HashSet<>();
      vaults.forEach(vault -> {
         if (vault.isFinished()) {
            vault.getPlayers().forEach(player -> completed.add(player.getPlayerId()));
         }
      });
      completed.forEach(uuid -> this.remove(world.func_73046_m(), uuid));
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
      this.activeVaults.deserializeNBT(nbt.func_150295_c("ActiveVaults", 10));
      int[] pos = nbt.func_74759_k("NextVaultPos");
      this.nextVaultPos = new Mutable(pos[0], pos[1], pos[2]);
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      nbt.func_218657_a("ActiveVaults", this.activeVaults.serializeNBT());
      nbt.func_74783_a("NextVaultPos", new int[]{this.nextVaultPos.func_177958_n(), this.nextVaultPos.func_177956_o(), this.nextVaultPos.func_177952_p()});
      return nbt;
   }

   public static VaultRaidData get(ServerWorld world) {
      return (VaultRaidData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(VaultRaidData::new, "the_vault_VaultRaid");
   }
}
