package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.TalentTree;
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
public class PlayerTalentsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerTalents";
   private Map<UUID, TalentTree> playerMap = new HashMap<>();
   private final Set<UUID> scheduledMerge = new HashSet<>();
   private TalentTree previous;

   public TalentTree getTalents(Player player) {
      return this.getTalents(player.getUUID());
   }

   public TalentTree getTalents(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, uuid1 -> ModConfigs.TALENTS.get().orElse(new TalentTree()).copy());
   }

   public void setTalents(Player player, TalentTree talentTree) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.getTalents(player).getAll(LearnableSkill.class, Skill::isUnlocked).forEach(skill -> skill.onRemove(SkillContext.of(serverPlayer)));
      }

      this.playerMap.put(player.getUUID(), talentTree);
      if (player instanceof ServerPlayer serverPlayer) {
         talentTree.getAll(LearnableSkill.class, Skill::isUnlocked).forEach(skill -> skill.onAdd(SkillContext.of(serverPlayer)));
      }

      this.setDirty();
   }

   public PlayerTalentsData resetTalentTree(ServerPlayer player) {
      return this;
   }

   public boolean isDirty() {
      return true;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer()) {
            PlayerTalentsData data = get((ServerLevel)event.world);
            TalentTree current = ModConfigs.TALENTS.get().orElse(null);
            if (data.previous != current && current != null) {
               data.previous = current;
               data.scheduledMerge.addAll(data.playerMap.keySet());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer() && event.player instanceof ServerPlayer player) {
            PlayerTalentsData data = get(player.getLevel());
            if (data.scheduledMerge.remove(player.getUUID())) {
               ModConfigs.TALENTS.get().ifPresent(tree -> {
                  SkillContext context = SkillContext.of(player);
                  data.playerMap.get(player.getUUID()).mergeFrom(tree.copy(), context);
                  PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
                  stats.setSkillPoints(context.getLearnPoints());
                  stats.setRegretPoints(context.getRegretPoints());
                  AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
               });
            }

            data.getTalents(player).onTick(SkillContext.of(player));
         }
      }
   }

   private static PlayerTalentsData create(CompoundTag tag) {
      PlayerTalentsData data = new PlayerTalentsData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.playerMap.clear();
      this.scheduledMerge.clear();
      ListTag playerList = nbt.getList("Players", 8);
      ListTag talentList = nbt.getList("Talents", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            Adapters.SKILL.readNbt(talentList.getCompound(i)).ifPresent(tree -> {
               this.playerMap.put(playerUUID, (TalentTree)tree);
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
      nbt.put("Talents", talentList);
      return nbt;
   }

   public static PlayerTalentsData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerTalentsData get(MinecraftServer srv) {
      return (PlayerTalentsData)srv.overworld().getDataStorage().computeIfAbsent(PlayerTalentsData::create, PlayerTalentsData::new, "the_vault_PlayerTalents");
   }
}
