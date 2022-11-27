package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class VaultModifierVotingSession {
   private int votingDuration = 600;
   private final LinkedHashMap<ResourceLocation, Integer> modifierOptions = new LinkedHashMap<>();
   private final Set<String> votedPlayers = new HashSet<>();

   public static VaultModifierVotingSession create() {
      VaultModifierVotingSession session = new VaultModifierVotingSession();
      Random rand = new Random();
      int amount = 2 + (rand.nextFloat() < 0.2F ? 1 : 0);

      for (int i = 0; i < amount; i++) {
         ResourceLocation modifierId;
         do {
            modifierId = ModConfigs.RAID_EVENT_CONFIG.getRandomModifier().getId();
         } while (session.modifierOptions.containsKey(modifierId));

         session.modifierOptions.put(modifierId, Integer.valueOf(0));
      }

      return session;
   }

   public List<ResourceLocation> getModifierIds() {
      return new ArrayList<>(this.modifierOptions.keySet());
   }

   public List<VaultModifier> getModifiers() {
      return this.getModifierIds().stream().map(VaultModifierRegistry::getOpt).flatMap(Optional::stream).collect(Collectors.toList());
   }

   public int getVotingDuration() {
      return this.votingDuration;
   }

   public void addVote(String player, String modifierName) {
      if (!this.votedPlayers.contains(player)) {
         VaultModifier vaultModifier = VaultModifierRegistry.getAll()
            .filter(group -> group.getDisplayName().equalsIgnoreCase(modifierName))
            .findFirst()
            .orElse(null);
         if (vaultModifier != null) {
            ResourceLocation modifierId = vaultModifier.getId();
            if (this.modifierOptions.containsKey(modifierId)) {
               int amount = this.modifierOptions.getOrDefault(modifierId, 0);
               this.modifierOptions.put(modifierId, Integer.valueOf(amount + 1));
               this.votedPlayers.add(player);
            }
         }
      }
   }

   public void tick() {
      this.votingDuration--;
   }

   public boolean isFinished() {
      return this.votingDuration <= 0;
   }

   public void finish(VaultRaid vault, ServerLevel world) {
      ResourceLocation voted = this.getVotedModifier();
      if (voted != null) {
         VaultModifier<?> modifier = VaultModifierRegistry.<VaultModifier<?>>getOpt(voted).orElse(null);
         if (modifier != null) {
            int minutes = ModConfigs.RAID_EVENT_CONFIG.getTemporaryModifierMinutes();
            Component ct = new TextComponent("Added ")
               .withStyle(ChatFormatting.GRAY)
               .append(modifier.getNameComponent())
               .append(new TextComponent(" for ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent(minutes + " minutes!").withStyle(ChatFormatting.GOLD));
            vault.getModifiers().addTemporaryModifier(modifier, 1, minutes * 60 * 20);
            vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.getServer(), sPlayer -> sPlayer.sendMessage(ct, Util.NIL_UUID)));
         }
      }
   }

   @Nullable
   public ResourceLocation getVotedModifier() {
      List<ResourceLocation> modifiers = new ArrayList<>(this.modifierOptions.keySet());
      Collections.shuffle(modifiers);
      int max = -1;
      ResourceLocation selectedModifier = null;

      for (ResourceLocation modifier : modifiers) {
         int amount = this.modifierOptions.getOrDefault(modifier, 0);
         if (amount > max) {
            selectedModifier = modifier;
            max = amount;
         }
      }

      return selectedModifier;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("votingDuration", this.votingDuration);
      CompoundTag votes = new CompoundTag();
      this.modifierOptions.forEach((resourceLocation, integer) -> votes.putInt(resourceLocation.toString(), integer));
      tag.put("votes", votes);
      ListTag players = new ListTag();
      this.votedPlayers.forEach(player -> players.add(StringTag.valueOf(player)));
      tag.put("players", players);
      return tag;
   }

   public static VaultModifierVotingSession deserialize(CompoundTag tag) {
      VaultModifierVotingSession session = new VaultModifierVotingSession();
      session.votingDuration = tag.getInt("votingDuration");
      CompoundTag votes = tag.getCompound("votes");
      votes.getAllKeys().forEach(key -> session.modifierOptions.put(new ResourceLocation(key), Integer.valueOf(votes.getInt(key))));
      ListTag players = tag.getList("players", 8);

      for (int i = 0; i < players.size(); i++) {
         session.votedPlayers.add(players.getString(i));
      }

      return session;
   }
}
