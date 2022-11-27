package iskallia.vault.client;

import iskallia.vault.world.data.VaultPartyData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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

   public static void receivePartyUpdate(ListTag partyData) {
      parties.clear();

      for (int i = 0; i < partyData.size(); i++) {
         CompoundTag data = partyData.getCompound(i);
         VaultPartyData.Party party = new VaultPartyData.Party();
         party.deserializeNBT(data);
         parties.add(party);
      }
   }

   public static void receivePartyMembers(ListTag partyMembers) {
      for (int i = 0; i < partyMembers.size(); i++) {
         CompoundTag nbt = partyMembers.getCompound(i);
         ClientPartyData.PartyMember partyMember = new ClientPartyData.PartyMember();
         partyMember.deserializeNBT(nbt);
         cachedPartyMembers.put(partyMember.playerUUID, partyMember);
      }
   }

   public static class PartyMember implements INBTSerializable<CompoundTag> {
      public UUID playerUUID;
      public float healthPts;
      public ClientPartyData.PartyMember.Status status = ClientPartyData.PartyMember.Status.NORMAL;

      public PartyMember() {
      }

      public PartyMember(Player player) {
         this.playerUUID = player.getUUID();
         this.healthPts = player.getHealth();
         if (this.healthPts <= 0.0F) {
            this.status = ClientPartyData.PartyMember.Status.DEAD;
         } else {
            for (MobEffectInstance potionEffect : player.getActiveEffects()) {
               MobEffect potion = potionEffect.getEffect();
               if (potion == MobEffects.POISON) {
                  this.status = ClientPartyData.PartyMember.Status.POISONED;
                  break;
               }

               if (potion == MobEffects.WITHER) {
                  this.status = ClientPartyData.PartyMember.Status.WITHERED;
                  break;
               }
            }
         }
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putUUID("PlayerUUID", this.playerUUID);
         nbt.putFloat("HealthPts", this.healthPts);
         nbt.putInt("StatusIndex", this.status.ordinal());
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.playerUUID = nbt.getUUID("PlayerUUID");
         this.healthPts = nbt.getFloat("HealthPts");
         this.status = ClientPartyData.PartyMember.Status.values()[nbt.getInt("StatusIndex")];
      }

      public static enum Status {
         NORMAL,
         POISONED,
         WITHERED,
         DEAD;
      }
   }
}
