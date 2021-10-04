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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber
public class VaultPartyData extends WorldSavedData {
   private static final Random rand = new Random();
   protected static final String DATA_NAME = "the_vault_VaultParty";
   protected VListNBT<VaultPartyData.Party, CompoundNBT> activeParties = VListNBT.of(VaultPartyData.Party::new);

   public VaultPartyData() {
      this("the_vault_VaultParty");
   }

   public VaultPartyData(String name) {
      super(name);
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.activeParties.deserializeNBT(nbt.func_150295_c("ActiveParties", 10));
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      nbt.func_218657_a("ActiveParties", this.activeParties.serializeNBT());
      return nbt;
   }

   public static VaultPartyData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static VaultPartyData get(MinecraftServer server) {
      return (VaultPartyData)server.func_241755_D_().func_217481_x().func_215752_a(VaultPartyData::new, "the_vault_VaultParty");
   }

   public static void broadcastPartyData(ServerWorld world) {
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
      MinecraftServer serverInstance = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      if (event.phase == Phase.END) {
         if (serverInstance.func_71259_af() % 20 == 0) {
            VaultPartyData vaultPartyData = get(serverInstance);
            vaultPartyData.activeParties.forEach(party -> {
               ListNBT partyMembers = party.toClientMemberList();
               PartyMembersMessage pkt = new PartyMembersMessage(partyMembers);
               party.members.forEach(uuid -> {
                  ServerPlayerEntity player = serverInstance.func_184103_al().func_177451_a(uuid);
                  if (player != null) {
                     ModNetwork.CHANNEL.sendTo(pkt, player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
                  }
               });
            });
         }
      }
   }

   public static class Party implements INBTSerializable<CompoundNBT> {
      private UUID leader = null;
      private final VListNBT<UUID, StringNBT> members = VListNBT.ofUUID();
      private final VListNBT<UUID, StringNBT> invites = VListNBT.ofUUID();

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

      public ListNBT toClientMemberList() {
         MinecraftServer serverInstance = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
         ListNBT partyMembers = new ListNBT();

         for (UUID uuid : this.members) {
            ServerPlayerEntity player = serverInstance.func_184103_al().func_177451_a(uuid);
            if (player != null) {
               ClientPartyData.PartyMember partyMember = new ClientPartyData.PartyMember(player);
               partyMembers.add(partyMember.serializeNBT());
            }
         }

         return partyMembers;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         if (this.leader != null) {
            nbt.func_186854_a("leader", this.leader);
         }

         nbt.func_218657_a("Members", this.members.serializeNBT());
         nbt.func_218657_a("Invites", this.invites.serializeNBT());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.leader = null;
         if (nbt.func_186855_b("leader")) {
            this.leader = nbt.func_186857_a("leader");
         }

         this.members.deserializeNBT(nbt.func_150295_c("Members", 8));
         this.invites.deserializeNBT(nbt.func_150295_c("Invites", 8));
      }
   }
}
