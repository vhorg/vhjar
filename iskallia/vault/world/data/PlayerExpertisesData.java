package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerExpertisesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerExpertises";
   private Map<UUID, ExpertiseTree> playerMap = new HashMap<>();
   private final Set<UUID> scheduledMerge = new HashSet<>();
   private ExpertiseTree previous;

   public ExpertiseTree getExpertises(Player player) {
      return this.getExpertises(player.getUUID());
   }

   public ExpertiseTree getExpertises(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, uuid1 -> ModConfigs.EXPERTISES.getAll().copy());
   }

   public void resetAllPlayerExpertiseTrees(ServerLevel level) {
      this.playerMap.clear();
      this.setDirty();

      for (ServerPlayer player : level.players()) {
         this.getExpertises(player).sync(SkillContext.of(player));
      }
   }

   public PlayerExpertisesData resetExpertiseTree(ServerPlayer player) {
      this.playerMap.remove(player.getUUID());
      this.setDirty();
      this.getExpertises(player).sync(SkillContext.of(player));
      return this;
   }

   public boolean isDirty() {
      return true;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer()) {
            PlayerExpertisesData data = get((ServerLevel)event.world);
            if (data.previous != ModConfigs.EXPERTISES.getAll()) {
               data.previous = ModConfigs.EXPERTISES.getAll();
               data.scheduledMerge.addAll(data.playerMap.keySet());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer() && event.player instanceof ServerPlayer player) {
            PlayerExpertisesData data = get(player.getLevel());
            if (data.scheduledMerge.remove(player.getUUID())) {
               SkillContext context = SkillContext.of(player);
               data.playerMap.get(player.getUUID()).mergeFrom(ModConfigs.EXPERTISES.getAll().copy(), context);
               PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
               stats.setSkillPoints(context.getLearnPoints());
               stats.setRegretPoints(context.getRegretPoints());
               AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
            }

            data.getExpertises(player).onTick(SkillContext.of(player));
         }
      }
   }

   private static PlayerExpertisesData create(CompoundTag tag) {
      PlayerExpertisesData data = new PlayerExpertisesData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.playerMap.clear();
      this.scheduledMerge.clear();
      ListTag playerList = nbt.getList("Players", 8);
      ListTag talentList = nbt.getList("Expertises", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            Adapters.SKILL.readNbt(talentList.getCompound(i)).ifPresent(tree -> {
               this.playerMap.put(playerUUID, (ExpertiseTree)tree);
               this.scheduledMerge.add(playerUUID);
            });
         }

         this.setDirty();
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag talentList = new ListTag();
      this.playerMap.forEach((uuid, researchTree) -> Adapters.SKILL.writeNbt(researchTree).ifPresent(tag -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         talentList.add(tag);
      }));
      nbt.put("Players", playerList);
      nbt.put("Expertises", talentList);
      return nbt;
   }

   public static PlayerExpertisesData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerExpertisesData get(MinecraftServer srv) {
      return (PlayerExpertisesData)srv.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerExpertisesData::create, PlayerExpertisesData::new, "the_vault_PlayerExpertises");
   }
}
