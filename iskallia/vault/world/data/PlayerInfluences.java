package iskallia.vault.world.data;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModSounds;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.calc.GodAffinityHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerInfluences extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerInfluences";
   protected VMapNBT<UUID, PlayerInfluences.Entry> entries = VMapNBT.ofUUID(PlayerInfluences.Entry::new);

   public static void attemptFavour(Player player, VaultGod god, RandomSource random) {
      float chance = GodAffinityHelper.getAffinityPercent(player, god);
      if (!(random.nextFloat() >= chance)) {
         BlockPos pos = player.blockPosition();
         player.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.FAVOUR_UP, SoundSource.PLAYERS, 0.4F, 0.7F);
         Component msg = new TextComponent("You gained a favour!").withStyle(god.getChatColor());
         player.displayClientMessage(msg, true);
         setFavour(player.getUUID(), god);
      }
   }

   public static Optional<VaultGod> getFavour(UUID player) {
      return !get().entries.containsKey(player) ? Optional.empty() : Optional.ofNullable(get().entries.get(player).favour);
   }

   public static void setFavour(UUID player, VaultGod god) {
      get().entries.computeIfAbsent(player, uuid -> new PlayerInfluences.Entry()).favour = god;
   }

   public static Optional<VaultGod> consumeFavour(UUID player) {
      if (!get().entries.containsKey(player)) {
         return Optional.empty();
      } else {
         Optional<VaultGod> result = Optional.ofNullable(get().entries.get(player).favour);
         get().entries.get(player).favour = null;
         return result;
      }
   }

   public static int getReputation(UUID player, VaultGod god) {
      return !get().entries.containsKey(player) ? 0 : get().entries.get(player).reputation.getOrDefault(god, 0);
   }

   public static void addReputation(UUID player, VaultGod god, int reputation) {
      get().entries.computeIfAbsent(player, uuid -> new PlayerInfluences.Entry()).addReputation(god, reputation);
   }

   private static PlayerInfluences load(CompoundTag nbt) {
      PlayerInfluences data = new PlayerInfluences();
      data.entries.deserializeNBT(nbt.getList("entries", 10));
      return data;
   }

   public boolean isDirty() {
      return true;
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("entries", this.entries.serializeNBT());
      return nbt;
   }

   public static PlayerInfluences get() {
      return (PlayerInfluences)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerInfluences::load, PlayerInfluences::new, "the_vault_PlayerInfluences");
   }

   private static class Entry implements INBTSerializable<CompoundTag> {
      private Map<VaultGod, Integer> reputation = new HashMap<>();
      private VaultGod favour;

      public void addReputation(VaultGod god, int reputation) {
         int total = this.reputation.values().stream().mapToInt(Integer::intValue).sum();
         int remove = total + reputation - 100;
         this.reputation.put(god, this.reputation.getOrDefault(god, 0) + reputation);
         Random random = new Random();

         for (int i = 0; i < remove; i++) {
            List<VaultGod> available = this.reputation.entrySet().stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).toList();
            VaultGod target = available.get(random.nextInt(available.size()));
            this.reputation.put(target, this.reputation.get(target) - 1);
         }
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag reputationList = new ListTag();
         this.reputation.forEach((vaultGod, reputation) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("God", vaultGod.getName());
            entry.putInt("Reputation", reputation);
            reputationList.add(entry);
         });
         nbt.put("Reputations", reputationList);
         if (this.favour != null) {
            nbt.putString("Favour", this.favour.getName());
         }

         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.reputation.clear();
         ListTag reputationList = nbt.getList("Reputations", 10);

         for (int i = 0; i < reputationList.size(); i++) {
            CompoundTag entry = reputationList.getCompound(i);
            this.reputation.put(VaultGod.fromName(entry.getString("God")), entry.getInt("Reputation"));
         }

         if (nbt.contains("Favour", 8)) {
            this.favour = VaultGod.fromName(nbt.getString("Favour"));
         } else {
            this.favour = null;
         }
      }
   }
}
