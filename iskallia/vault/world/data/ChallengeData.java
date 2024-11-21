package iskallia.vault.world.data;

import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.UpdateChallengeDataMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class ChallengeData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Challenges";
   public static List<ChallengeManager> CLIENT = new ArrayList<>();
   private final Map<UUID, ChallengeManager> managers = new HashMap<>();

   public boolean contains(UUID uuid) {
      return this.managers.containsKey(uuid);
   }

   public void add(ServerLevel world, ChallengeManager manager) {
      this.managers.put(manager.getUuid(), manager);
      manager.onAttach(world);
   }

   public void remove(UUID uuid) {
      this.managers.remove(uuid);
   }

   public static boolean shouldRenderObjectives() {
      for (ChallengeManager manager : CLIENT) {
         if (!manager.shouldRenderObjectives()) {
            return false;
         }
      }

      return true;
   }

   @SubscribeEvent
   public static void onServerStart(ServerStartingEvent event) {
      MinecraftServer server = event.getServer();
      ChallengeData data = get(server);
      data.managers.forEach((uuid, manager) -> {
         ServerLevel world = server.getLevel(manager.dimension);
         if (world != null) {
            manager.onAttach(world);
         }
      });
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         ChallengeData data = get(server);
         Set<UUID> toUpdate = new HashSet<>();
         data.managers.values().removeIf(manager -> {
            ServerLevel world = server.getLevel(manager.dimension);
            if (world != null && !manager.isDeleted()) {
               return false;
            } else {
               toUpdate.addAll(manager.players);
               manager.onRemove(server);
               manager.onDetach();
               return true;
            }
         });

         for (ChallengeManager manager : data.managers.values()) {
            toUpdate.addAll(manager.players);
         }

         for (UUID uuid : toUpdate) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
               data.sendUpdatesToClient(player);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void onRender(Post event) {
      if (event.getType() == ElementType.ALL) {
         for (ChallengeManager manager : CLIENT) {
            manager.onRender(event.getMatrixStack(), event.getPartialTicks(), event.getWindow());
         }
      }
   }

   public void sendUpdatesToClient(ServerPlayer player) {
      List<ChallengeManager> entries = new ArrayList<>();
      this.managers.forEach((uuid, entry) -> {
         if (entry.players.contains(player.getUUID())) {
            entries.add(entry);
         }
      });
      ModNetwork.CHANNEL.sendTo(new UpdateChallengeDataMessage(entries), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      ListTag list = new ListTag();
      this.managers.values().forEach(manager -> Adapters.CHALLENGE_MANAGER.writeNbt(manager).ifPresent(list::add));
      nbt.put("managers", list);
      return nbt;
   }

   public boolean isDirty() {
      return true;
   }

   private void read(CompoundTag nbt) {
      ListTag list = nbt.getList("managers", 10);
      this.managers.clear();

      for (Tag tag : list) {
         Adapters.CHALLENGE_MANAGER.readNbt(tag).ifPresent(manager -> this.managers.put(manager.getUuid(), manager));
      }
   }

   private static ChallengeData load(CompoundTag nbt) {
      ChallengeData data = new ChallengeData();
      data.read(nbt);
      return data;
   }

   public static ChallengeData get(MinecraftServer server) {
      return (ChallengeData)server.overworld().getDataStorage().computeIfAbsent(ChallengeData::load, ChallengeData::new, "the_vault_Challenges");
   }
}
