package iskallia.vault.world.data;

import iskallia.vault.client.ClientPartyData;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.PartyMembersMessage;
import iskallia.vault.network.message.PartyStatusMessage;
import iskallia.vault.util.MiscUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class VaultPartyData extends SavedData {
   private static final Random rand = new Random();
   protected static final String DATA_NAME = "the_vault_VaultParty";
   protected VListNBT<VaultPartyData.Party, CompoundTag> activeParties = VListNBT.of(VaultPartyData.Party::new);

   private static VaultPartyData create(CompoundTag tag) {
      VaultPartyData data = new VaultPartyData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.activeParties.deserializeNBT(nbt.getList("ActiveParties", 10));
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("ActiveParties", this.activeParties.serializeNBT());
      return nbt;
   }

   public static VaultPartyData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static VaultPartyData get(MinecraftServer server) {
      return (VaultPartyData)server.overworld().getDataStorage().computeIfAbsent(VaultPartyData::create, VaultPartyData::new, "the_vault_VaultParty");
   }

   public static void broadcastPartyData(ServerLevel world) {
      VaultPartyData data = get(world);
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new PartyStatusMessage(data.activeParties.serializeNBT()));
   }

   public Optional<VaultPartyData.Party> getParty(UUID playerId) {
      return this.activeParties.stream().filter(party -> party.hasMember(playerId)).findFirst();
   }

   public boolean createParty(UUID playerId) {
      if (this.getParty(playerId).isPresent()) {
         return false;
      } else {
         VaultPartyData.Party newParty = new VaultPartyData.Party();
         newParty.addMember(playerId);
         this.activeParties.add(newParty);
         return true;
      }
   }

   public boolean disbandParty(UUID playerId) {
      Optional<VaultPartyData.Party> party = this.getParty(playerId);
      if (!party.isPresent()) {
         return false;
      } else {
         this.activeParties.remove(party.get());
         return true;
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      MinecraftServer serverInstance = ServerLifecycleHooks.getCurrentServer();
      if (event.phase == Phase.END) {
         if (serverInstance.getTickCount() % 20 == 0) {
            VaultPartyData vaultPartyData = get(serverInstance);
            vaultPartyData.activeParties.forEach(party -> {
               ListTag partyMembers = party.toClientMemberList();
               PartyMembersMessage pkt = new PartyMembersMessage(partyMembers);
               party.members.forEach(uuid -> {
                  ServerPlayer player = serverInstance.getPlayerList().getPlayer(uuid);
                  if (player != null) {
                     ModNetwork.CHANNEL.sendTo(pkt, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                  }
               });
            });
         }
      }
   }

   public static class Party implements INBTSerializable<CompoundTag> {
      private UUID leader = null;
      private final VListNBT<UUID, StringTag> members = VListNBT.ofUUID();
      private final VListNBT<UUID, StringTag> invites = VListNBT.ofUUID();

      public List<UUID> getMembers() {
         return Collections.unmodifiableList(this.members);
      }

      @Nullable
      public UUID getLeader() {
         return this.leader;
      }

      public boolean addMember(UUID member) {
         if (this.members.isEmpty()) {
            this.leader = member;
         }

         return this.members.add(member);
      }

      public boolean invite(UUID member) {
         if (this.invites.contains(member)) {
            return false;
         } else {
            this.invites.add(member);
            return true;
         }
      }

      public boolean remove(UUID member) {
         boolean removed = this.members.remove(member);
         if (removed && member.equals(this.leader)) {
            this.leader = MiscUtils.getRandomEntry(this.members, VaultPartyData.rand);
         }

         return removed;
      }

      public boolean confirmInvite(UUID member) {
         if (this.invites.contains(member) && this.invites.remove(member)) {
            this.members.add(member);
            return true;
         } else {
            return false;
         }
      }

      public boolean hasMember(UUID member) {
         return this.members.contains(member);
      }

      public ListTag toClientMemberList() {
         MinecraftServer serverInstance = ServerLifecycleHooks.getCurrentServer();
         ListTag partyMembers = new ListTag();

         for (UUID uuid : this.members) {
            ServerPlayer player = serverInstance.getPlayerList().getPlayer(uuid);
            if (player != null) {
               ClientPartyData.PartyMember partyMember = new ClientPartyData.PartyMember(player);
               partyMembers.add(partyMember.serializeNBT());
            }
         }

         return partyMembers;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         if (this.leader != null) {
            nbt.putUUID("leader", this.leader);
         }

         nbt.put("Members", this.members.serializeNBT());
         nbt.put("Invites", this.invites.serializeNBT());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.leader = null;
         if (nbt.hasUUID("leader")) {
            this.leader = nbt.getUUID("leader");
         }

         this.members.deserializeNBT(nbt.getList("Members", 8));
         this.invites.deserializeNBT(nbt.getList("Invites", 8));
      }
   }
}
