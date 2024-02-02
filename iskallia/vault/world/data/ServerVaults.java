package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.ThreadingMode;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.nbt.VListNBT;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ServerVaults extends SavedData {
   protected static final String DATA_NAME = "the_vault_Vaults";
   private final VListNBT<Vault, LongArrayTag> vaults = new VListNBT<>(new ArrayList<>(), vault -> {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      buffer.writeEnum(vault.get(Vault.VERSION));
      vault.write(buffer, new DiskSyncContext(vault.get(Vault.VERSION)));
      return new LongArrayTag(buffer.toLongArray());
   }, nbt -> {
      ArrayBitBuffer buffer = ArrayBitBuffer.backing(nbt.getAsLongArray(), 0);
      Vault vault = new Vault().read(buffer, new DiskSyncContext(buffer.readEnum(Version.class)));
      getWorld(vault).ifPresent(vault::initServer);
      return vault;
   });

   protected ServerVaults() {
   }

   public boolean isDirty() {
      return true;
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         get(ServerLifecycleHooks.getCurrentServer()).vaults.removeIf(vault -> {
            if (vault.has(Vault.FINISHED)) {
               VaultSnapshots.onVaultEnded(vault);
               return true;
            } else {
               return false;
            }
         });
      }
   }

   @SubscribeEvent
   public static void onServerStop(ServerStoppedEvent event) {
      get(ServerLifecycleHooks.getCurrentServer()).vaults.forEach(Vault::releaseServer);
   }

   public static Vault add(Vault vault) {
      get(ServerLifecycleHooks.getCurrentServer()).vaults.add(vault);
      VirtualWorld world = VirtualWorld.create(getWorldId(vault), ThreadingMode.CONCURRENT);
      VirtualWorlds.register(world);
      vault.initServer(world);
      VaultSnapshots.onVaultStarted(vault);
      return vault;
   }

   public static ResourceLocation getWorldId(Vault vault) {
      return VaultMod.id("vault_" + vault.get(Vault.ID).toString());
   }

   public static Optional<VirtualWorld> getWorld(Vault vault) {
      if (vault == null) {
         return Optional.empty();
      } else {
         for (VirtualWorld world : VirtualWorlds.getAll()) {
            if (getWorldId(vault).equals(world.dimension().location())) {
               return Optional.of(world);
            }
         }

         return Optional.empty();
      }
   }

   public static List<Vault> getAll() {
      return get(ServerLifecycleHooks.getCurrentServer()).vaults;
   }

   public static Optional<Vault> get(UUID uuid) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server == null ? Optional.empty() : get(server).vaults.stream().filter(vault -> vault.get(Vault.ID).equals(uuid)).findFirst();
   }

   public static Optional<Vault> get(Level world) {
      if (world != null && !world.isClientSide) {
         for (Vault vault : get(ServerLifecycleHooks.getCurrentServer()).vaults) {
            if (world.dimension().location().toString().contains(vault.get(Vault.ID).toString())) {
               return Optional.of(vault);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public static boolean remove(Vault vault) {
      return get(ServerLifecycleHooks.getCurrentServer()).vaults.remove(vault);
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("vaults", this.vaults.serializeNBT());
      return nbt;
   }

   public void load(CompoundTag nbt) {
      this.vaults.deserializeNBT(nbt.getList("vaults", 12));
   }

   public static ServerVaults get(MinecraftServer server) {
      return (ServerVaults)server.overworld().getDataStorage().computeIfAbsent(ServerVaults::create, ServerVaults::new, "the_vault_Vaults");
   }

   private static ServerVaults create(CompoundTag tag) {
      ServerVaults data = new ServerVaults();
      data.load(tag);
      return data;
   }
}
