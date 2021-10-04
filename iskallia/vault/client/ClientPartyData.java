package iskallia.vault.client;

import iskallia.vault.world.data.VaultPartyData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.util.INBTSerializable;

public class ClientPartyData {
   private static final List<VaultPartyData.Party> parties = new ArrayList<>();
   private static final Map<UUID, ClientPartyData.PartyMember> cachedPartyMembers = new HashMap<>();

   @Nullable
   public static VaultPartyData.Party getParty(UUID playerUUID) {
      for (VaultPartyData.Party party : parties) {
         if (party.hasMember(playerUUID)) {
            return party;
         }
      }

      return null;
   }

   @Nullable
   public static ClientPartyData.PartyMember getCachedMember(@Nullable UUID playerUUID) {
      return playerUUID == null ? null : cachedPartyMembers.get(playerUUID);
   }

   public static void receivePartyUpdate(ListNBT partyData) {
      parties.clear();

      for (int i = 0; i < partyData.size(); i++) {
         CompoundNBT data = partyData.func_150305_b(i);
         VaultPartyData.Party party = new VaultPartyData.Party();
         party.deserializeNBT(data);
         parties.add(party);
      }
   }

   public static void receivePartyMembers(ListNBT partyMembers) {
      for (int i = 0; i < partyMembers.size(); i++) {
         CompoundNBT nbt = partyMembers.func_150305_b(i);
         ClientPartyData.PartyMember partyMember = new ClientPartyData.PartyMember();
         partyMember.deserializeNBT(nbt);
         cachedPartyMembers.put(partyMember.playerUUID, partyMember);
      }
   }

   public static class PartyMember implements INBTSerializable<CompoundNBT> {
      public UUID playerUUID;
      public float healthPts;
      public ClientPartyData.PartyMember.Status status = ClientPartyData.PartyMember.Status.NORMAL;

      public PartyMember() {
      }

      public PartyMember(PlayerEntity player) {
         this.playerUUID = player.func_110124_au();
         this.healthPts = player.func_110143_aJ();
         if (this.healthPts <= 0.0F) {
            this.status = ClientPartyData.PartyMember.Status.DEAD;
         } else {
            for (EffectInstance potionEffect : player.func_70651_bq()) {
               Effect potion = potionEffect.func_188419_a();
               if (potion == Effects.field_76436_u) {
                  this.status = ClientPartyData.PartyMember.Status.POISONED;
                  break;
               }

               if (potion == Effects.field_82731_v) {
                  this.status = ClientPartyData.PartyMember.Status.WITHERED;
                  break;
               }
            }
         }
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_186854_a("PlayerUUID", this.playerUUID);
         nbt.func_74776_a("HealthPts", this.healthPts);
         nbt.func_74768_a("StatusIndex", this.status.ordinal());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.playerUUID = nbt.func_186857_a("PlayerUUID");
         this.healthPts = nbt.func_74760_g("HealthPts");
         this.status = ClientPartyData.PartyMember.Status.values()[nbt.func_74762_e("StatusIndex")];
      }

      public static enum Status {
         NORMAL,
         POISONED,
         WITHERED,
         DEAD;
      }
   }
}
