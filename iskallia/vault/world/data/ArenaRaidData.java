package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.legacy.raid.ArenaRaid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ArenaRaidData extends SavedData {
   protected static final String DATA_NAME = "the_vault_ArenaRaid";
   private Map<UUID, Integer> raidsToStart = new HashMap<>();
   private Map<UUID, ArenaRaid> activeRaids = new HashMap<>();
   private Map<UUID, ItemStack> scheduledCrates = new HashMap<>();
   private int xOffset = 0;

   public ArenaRaid getAt(BlockPos pos) {
      return this.activeRaids.values().stream().filter(raid -> raid.box.isInside(pos)).findFirst().orElse(null);
   }

   public void remove(ServerLevel server, UUID playerId) {
      ArenaRaid v = this.activeRaids.remove(playerId);
      if (v != null) {
         v.finish(server, server.getServer().getPlayerList().getPlayer(playerId));
      }
   }

   public ArenaRaid getActiveFor(ServerPlayer player) {
      return this.activeRaids.get(player.getUUID());
   }

   public ArenaRaid startNew(ServerPlayer player) {
      player.displayClientMessage(new TextComponent("Generating arena, please wait...").withStyle(ChatFormatting.GREEN), true);
      ArenaRaid raid = new ArenaRaid(player.getUUID(), new BoundingBox(this.xOffset, 0, 0, this.xOffset += 2048, 256, 2048));
      if (this.activeRaids.containsKey(player.getUUID())) {
      }

      this.activeRaids.put(raid.getPlayerId(), raid);
      this.setDirty();
      MinecraftServer server = player.getServer();
      this.raidsToStart.put(player.getUUID(), server.getTickCount() + 100);
      return raid;
   }

   public void scheduleCrateToGive(ServerPlayer player, ItemStack crateStack) {
      this.scheduledCrates.put(player.getUUID(), crateStack);
   }

   public boolean isDirty() {
      return true;
   }

   public void tick(ServerLevel world) {
      this.activeRaids.values().forEach(vaultRaid -> vaultRaid.tick(world));
      boolean removed = false;
      List<Runnable> tasks = new ArrayList<>();

      for (ArenaRaid raid : this.activeRaids.values()) {
         if (raid.isComplete()) {
            tasks.add(() -> this.remove(world, raid.getPlayerId()));
            removed = true;
         }
      }

      tasks.forEach(Runnable::run);
      if (removed || this.activeRaids.size() > 0) {
         this.setDirty();
      }
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START) {
         ArenaRaidData arenaRaidData = get((ServerLevel)event.world);
         MinecraftServer server = event.world.getServer();
         int tickCounter = server.getTickCount();
         arenaRaidData.raidsToStart
            .forEach(
               (uuid, scheduledTick) -> {
                  if (tickCounter >= scheduledTick) {
                     NetcodeUtils.runIfPresent(
                        server,
                        uuid,
                        player -> {
                           ArenaRaid raid = arenaRaidData.getActiveFor(player);
                           server.submit(
                              () -> {
                                 try {
                                    ServerLevel world = server.getLevel(VaultMod.ARENA_KEY);
                                    ChunkPos chunkPos = new ChunkPos(
                                       raid.box.minX() + raid.box.getXSpan() / 2 >> 4, raid.box.minZ() + raid.box.getZSpan() / 2 >> 4
                                    );
                                    StructureStart start = ((ArenaStructure.Feature)ModFeatures.ARENA_FEATURE.value())
                                       .generate(
                                          world.registryAccess(),
                                          world.getChunkSource().getGenerator(),
                                          world.getChunkSource().getGenerator().getBiomeSource(),
                                          world.getStructureManager(),
                                          world.getSeed(),
                                          chunkPos,
                                          0,
                                          world,
                                          biomeHolder -> true
                                       );
                                    int chunkRadius = 64;

                                    for (int x = -chunkRadius; x <= chunkRadius; x += 17) {
                                       for (int z = -chunkRadius; z <= chunkRadius; z += 17) {
                                          world.getChunk(chunkPos.x + x, chunkPos.z + z, ChunkStatus.EMPTY, true)
                                             .setStartForFeature((ConfiguredStructureFeature)ModFeatures.ARENA_FEATURE.value(), start);
                                       }
                                    }

                                    raid.start(world, player, chunkPos);
                                 } catch (Exception var9) {
                                    var9.printStackTrace();
                                 }
                              }
                           );
                        }
                     );
                  }
               }
            );
         arenaRaidData.raidsToStart.values().removeIf(scheduledTick -> tickCounter >= scheduledTick);
         if (event.world.dimension() == VaultMod.ARENA_KEY) {
            arenaRaidData.tick((ServerLevel)event.world);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START) {
         ServerPlayer player = (ServerPlayer)event.player;
         ArenaRaidData arenaRaidData = get(player.getLevel());
         if (player.level.dimension() != VaultMod.ARENA_KEY && arenaRaidData.scheduledCrates.containsKey(player.getUUID())) {
            ItemStack crateStack = arenaRaidData.scheduledCrates.remove(player.getUUID());
            EntityHelper.giveItem(player, crateStack);
         }
      }
   }

   @SubscribeEvent
   public static void onLogout(PlayerLoggedOutEvent event) {
   }

   public static ArenaRaidData create(CompoundTag tag) {
      ArenaRaidData data = new ArenaRaidData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.activeRaids.clear();
      nbt.getList("ActiveRaids", 10).forEach(raidNBT -> {
         ArenaRaid raid = ArenaRaid.fromNBT((CompoundTag)raidNBT);
         this.activeRaids.put(raid.getPlayerId(), raid);
      });
      this.xOffset = nbt.getInt("XOffset");
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag raidsList = new ListTag();
      this.activeRaids.values().forEach(raid -> raidsList.add(raid.serializeNBT()));
      nbt.put("ActiveRaids", raidsList);
      nbt.putInt("XOffset", this.xOffset);
      return nbt;
   }

   public static ArenaRaidData get(ServerLevel world) {
      return (ArenaRaidData)world.getServer().overworld().getDataStorage().computeIfAbsent(ArenaRaidData::create, ArenaRaidData::new, "the_vault_ArenaRaid");
   }
}
