package iskallia.vault.world.data;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.pylon.PylonBuff;
import iskallia.vault.nbt.VListNBT;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
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
public class PlayerPylons extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerPylons";
   protected VListNBT<PylonBuff<?>, CompoundTag> buffs = (VListNBT<PylonBuff<?>, CompoundTag>)VListNBT.of(PylonBuff::fromNBT);
   protected boolean initialized;

   public static void add(Vault vault, Player player, PylonBuff.Config<?> config) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      PylonBuff<?> buff = config.build();
      buff.setPlayer(player);
      if (vault != null) {
         buff.setVault(vault);
      }

      buff.initServer(server);
      buff.onAdd(server);
      get().buffs.add(buff);
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         PlayerPylons pylons = get();
         if (!pylons.initialized) {
            pylons.buffs.forEach(pylon -> pylon.initServer(server));
            pylons.initialized = true;
         }

         pylons.buffs.forEach(buff -> buff.onTick(server));
         pylons.buffs.removeIf(pylon -> {
            if (pylon.isDone()) {
               pylon.onRemove(server);
               pylon.releaseServer();
               return true;
            } else {
               return false;
            }
         });
      }
   }

   @SubscribeEvent
   public static void onServerStop(ServerStoppedEvent event) {
      get().buffs.forEach(PylonBuff::releaseServer);
   }

   public boolean isDirty() {
      return true;
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      nbt.put("buffs", this.buffs.serializeNBT());
      return nbt;
   }

   private static PlayerPylons load(CompoundTag nbt) {
      PlayerPylons data = new PlayerPylons();
      data.buffs.deserializeNBT(nbt.getList("buffs", 10));
      return data;
   }

   public static PlayerPylons get() {
      return (PlayerPylons)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerPylons::load, PlayerPylons::new, "the_vault_PlayerPylons");
   }
}
