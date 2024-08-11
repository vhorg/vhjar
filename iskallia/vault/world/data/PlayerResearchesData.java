package iskallia.vault.world.data;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.PlayerReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class PlayerResearchesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerResearches";
   private final Map<UUID, ResearchTree> playerMap = new HashMap<>();
   private final List<List<PlayerReference>> researchTeams = new ArrayList<>();
   private final Map<PlayerReference, Set<PlayerReference>> invites = new HashMap<>();
   private boolean AE2ResearchTree = false;
   private final UUID AE2PlayerUUID = UUID.fromString("41c82c87-7afb-4024-ba57-13d2c99cae77");

   public ResearchTree getResearches(Player player) {
      return this.getResearches(player.getUUID());
   }

   public ResearchTree getResearches(UUID uuid) {
      if (uuid.equals(this.AE2PlayerUUID) && !this.AE2ResearchTree) {
         this.playerMap.computeIfAbsent(uuid, u -> {
            ResearchTree researchTree = ResearchTree.empty();
            ModConfigs.RESEARCHES.getAll().forEach(researchTree::research);
            this.AE2ResearchTree = true;
            return researchTree;
         });
      }

      return this.playerMap.computeIfAbsent(uuid, id -> ResearchTree.empty());
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server.getTickCount() % 20 == 0) {
         PlayerResearchesData data = get(server.overworld());
         AtomicBoolean dirty = new AtomicBoolean(false);

         for (Research research : ModConfigs.RESEARCHES.getAll()) {
            if (research.getCost() == 0) {
               data.playerMap.forEach((uuid, researchTree) -> {
                  if (!researchTree.isResearched(research)) {
                     researchTree.research(research);
                     dirty.set(true);
                  }
               });
            }
         }

         if (dirty.get()) {
            data.syncAll(server);
            data.setDirty();
         }
      }
   }

   public PlayerResearchesData research(ServerPlayer player, Research research) {
      ResearchTree researchTree = this.getResearches(player);
      researchTree.research(research);
      researchTree.getResearchShares().forEach(share -> {
         ResearchTree sharedTree = this.getResearches(share.getId());
         if (!sharedTree.isResearched(research)) {
            sharedTree.research(research);
            NetcodeUtils.runIfPresent(player.getServer(), share.getId(), other -> {
               MutableComponent ct = new TextComponent("").withStyle(ChatFormatting.GRAY);
               ct.append(player.getDisplayName().copy().withStyle(ChatFormatting.AQUA));
               ct.append(new TextComponent(" researched ").withStyle(ChatFormatting.GRAY));
               ct.append(new TextComponent(research.getName()).withStyle(ChatFormatting.AQUA));
               other.sendMessage(ct, Util.NIL_UUID);
            });
         }
      });
      this.syncAll(player.server);
      this.setDirty();
      return this;
   }

   public PlayerResearchesData removeResearch(ServerPlayer player, Research research) {
      ResearchTree researchTree = this.getResearches(player);
      researchTree.removeResearch(research);
      this.syncAll(player.server);
      this.setDirty();
      return this;
   }

   public PlayerResearchesData resetResearchTree(ServerPlayer player) {
      ResearchTree researchTree = this.getResearches(player);
      researchTree.resetResearches();
      this.leaveCurrentTeam(player);
      this.propagateTeams();
      this.syncAll(player.server);
      this.setDirty();
      return this;
   }

   public boolean isInTeam(UUID playerId) {
      return !this.getTeamMembers(playerId).isEmpty();
   }

   public List<PlayerReference> getTeamMembers(UUID playerId) {
      for (List<PlayerReference> team : this.researchTeams) {
         for (PlayerReference teamMember : team) {
            if (teamMember.getId().equals(playerId)) {
               return team;
            }
         }
      }

      return Collections.emptyList();
   }

   public boolean leaveCurrentTeam(Player player) {
      Iterator<List<PlayerReference>> teamsItr = this.researchTeams.iterator();

      while (teamsItr.hasNext()) {
         List<PlayerReference> team = teamsItr.next();
         if (team.removeIf(teamMember -> teamMember.getId().equals(player.getUUID()))) {
            if (team.size() <= 1) {
               teamsItr.remove();
            }

            this.clearInvitesAssociatedWith(player);
            this.propagateTeams();
            this.syncAll(ServerLifecycleHooks.getCurrentServer());
            this.setDirty();
            return true;
         }
      }

      return false;
   }

   public boolean acceptInvite(Player invitee, UUID issuer) {
      UUID inviteeId = invitee.getUUID();
      if (!this.getTeamMembers(inviteeId).isEmpty()) {
         return false;
      } else {
         PlayerReference inviteeRef = new PlayerReference(invitee);
         if (!this.getInvites(issuer).contains(inviteeRef)) {
            return false;
         } else {
            PlayerReference issuerRef = this.getInviteIssuerReference(issuer);
            List<PlayerReference> team = this.getTeamMembers(issuer);
            if (!team.isEmpty()) {
               team.add(inviteeRef);
            } else {
               List<PlayerReference> newTeam = new ArrayList<>();
               newTeam.add(inviteeRef);
               newTeam.add(issuerRef);
               this.researchTeams.add(newTeam);
            }

            this.clearInvitesAssociatedWith(invitee);
            this.propagateTeams();
            this.syncAll(ServerLifecycleHooks.getCurrentServer());
            this.setDirty();
            return true;
         }
      }
   }

   public boolean createInvite(Player issuer, Player invitee) {
      if (issuer.getUUID().equals(invitee.getUUID())) {
         return false;
      } else if (!this.getTeamMembers(invitee.getUUID()).isEmpty()) {
         return false;
      } else {
         this.invites.computeIfAbsent(new PlayerReference(issuer), id -> new HashSet<>()).add(new PlayerReference(invitee));
         return true;
      }
   }

   private Set<PlayerReference> getInvites(UUID issuer) {
      for (PlayerReference issuerRef : this.invites.keySet()) {
         if (issuerRef.getId().equals(issuer)) {
            return this.invites.get(issuerRef);
         }
      }

      return Collections.emptySet();
   }

   @Nullable
   private PlayerReference getInviteIssuerReference(UUID issuer) {
      for (PlayerReference issuerRef : this.invites.keySet()) {
         if (issuerRef.getId().equals(issuer)) {
            return issuerRef;
         }
      }

      return null;
   }

   private void clearInvitesAssociatedWith(Player player) {
      PlayerReference playerReference = new PlayerReference(player);
      this.invites.remove(playerReference);
      this.invites.values().forEach(invites -> invites.removeIf(playerReference::equals));
   }

   private void propagateTeams() {
      this.playerMap.forEach((playerId, researchTree) -> {
         researchTree.resetShares();
         this.getTeamMembers(playerId).forEach(ref -> {
            if (!ref.getId().equals(playerId)) {
               researchTree.addShare(ref);
            }
         });
      });
   }

   public void syncAll(MinecraftServer server) {
      server.getPlayerList().getPlayers().forEach(this::sync);
   }

   public void sync(ServerPlayer player) {
      this.getResearches(player).sync(player);
   }

   private static PlayerResearchesData create(CompoundTag tag) {
      PlayerResearchesData data = new PlayerResearchesData();
      data.load(tag);
      return data;
   }

   private void load(CompoundTag nbt) {
      if (nbt.contains("PlayerEntries")) {
         this.loadLegacy(nbt);
      } else {
         this.playerMap.clear();

         for (String key : nbt.getAllKeys()) {
            UUID playerId;
            try {
               playerId = UUID.fromString(key);
            } catch (IllegalArgumentException var7) {
               continue;
            }

            this.playerMap.put(playerId, new ResearchTree(nbt.getCompound(key)));
         }

         this.researchTeams.clear();
         ListTag teams = nbt.getList("sharedTeams", 9);

         for (int i = 0; i < teams.size(); i++) {
            List<PlayerReference> team = new ArrayList<>();
            ListTag teamMembers = teams.getList(i);

            for (int j = 0; j < teamMembers.size(); j++) {
               team.add(new PlayerReference(teamMembers.getCompound(j)));
            }

            this.researchTeams.add(team);
         }
      }
   }

   private void loadLegacy(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag researchesList = nbt.getList("ResearchEntries", 10);
      if (playerList.size() != researchesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.playerMap.put(playerUUID, new ResearchTree(researchesList.getCompound(i)));
         }
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag teams = new ListTag();
      this.researchTeams.forEach(teamList -> {
         ListTag teamMembers = new ListTag();
         teamList.forEach(ref -> teamMembers.add(ref.serialize()));
         teams.add(teamMembers);
      });
      nbt.put("sharedTeams", teams);
      this.playerMap.forEach((playerId, researchTree) -> nbt.put(playerId.toString(), researchTree.serializeNBT()));
      return nbt;
   }

   public static PlayerResearchesData get(ServerLevel world) {
      return (PlayerResearchesData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerResearchesData::create, PlayerResearchesData::new, "the_vault_PlayerResearches");
   }
}
