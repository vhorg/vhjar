package iskallia.vault.world.data;

import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerTalentsData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerTalents";
   private Map<UUID, TalentTree> playerMap = new HashMap<>();

   public TalentTree getTalents(Player player) {
      return this.getTalents(player.getUUID());
   }

   public TalentTree getTalents(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, TalentTree::new);
   }

   public PlayerTalentsData add(ServerPlayer player, TalentNode<?>... nodes) {
      TalentTree talents = this.getTalents(player);
      talents.add(player.getServer(), nodes);
      talents.sync(player.server);
      this.setDirty();
      return this;
   }

   public PlayerTalentsData remove(ServerPlayer player, TalentNode<?>... nodes) {
      this.getTalents(player).remove(player.getServer(), nodes);
      this.setDirty();
      return this;
   }

   public PlayerTalentsData upgradeTalent(ServerPlayer player, TalentNode<?> talentNode) {
      TalentTree talentTree = this.getTalents(player);
      talentTree.upgradeTalent(player.getServer(), talentNode);
      talentTree.sync(player.getServer());
      this.setDirty();
      return this;
   }

   public PlayerTalentsData downgradeTalent(ServerPlayer player, TalentNode<?> talentNode) {
      TalentTree talentTree = this.getTalents(player);
      talentTree.downgradeTalent(player.getServer(), talentNode);
      talentTree.sync(player.getServer());
      this.setDirty();
      return this;
   }

   public PlayerTalentsData resetTalentTree(ServerPlayer player) {
      UUID uniqueID = player.getUUID();
      TalentTree oldTalentTree = this.playerMap.get(uniqueID);
      if (oldTalentTree != null) {
         for (TalentNode<?> node : oldTalentTree.getNodes()) {
            if (node.isLearned()) {
               node.getTalent().onRemoved(player);
            }
         }
      }

      TalentTree talentTree = new TalentTree(uniqueID);
      this.playerMap.put(uniqueID, talentTree);
      talentTree.sync(player.getServer());
      this.setDirty();
      return this;
   }

   public PlayerTalentsData tick(MinecraftServer server) {
      this.playerMap.values().forEach(abilityTree -> abilityTree.tick(server));
      return this;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER) {
         get((ServerLevel)event.world).tick(((ServerLevel)event.world).getServer());
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER) {
         get((ServerLevel)event.player.level).getTalents(event.player);
      }
   }

   private static PlayerTalentsData create(CompoundTag tag) {
      PlayerTalentsData data = new PlayerTalentsData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag talentList = nbt.getList("TalentEntries", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getTalents(playerUUID).deserializeNBT(talentList.getCompound(i));
         }

         this.setDirty();
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag talentList = new ListTag();
      this.playerMap.forEach((uuid, talentTree) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         talentList.add(talentTree.serializeNBT());
      });
      nbt.put("PlayerEntries", playerList);
      nbt.put("TalentEntries", talentList);
      return nbt;
   }

   public static PlayerTalentsData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerTalentsData get(MinecraftServer srv) {
      return (PlayerTalentsData)srv.overworld().getDataStorage().computeIfAbsent(PlayerTalentsData::create, PlayerTalentsData::new, "the_vault_PlayerTalents");
   }
}
