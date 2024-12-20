package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
public class PlayerAbilitiesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAbilities";
   private final Map<UUID, AbilityTree> playerMap = new HashMap<>();
   private final Set<UUID> scheduledMerge = new HashSet<>();
   private AbilityTree previous;
   private final Set<UUID> scheduledRefund = new HashSet<>();
   private final Set<UUID> scheduledCorruptionCheck = new HashSet<>();
   private int version = 1;

   public AbilityTree getAbilities(Player player) {
      return this.getAbilities(player.getUUID());
   }

   public void setAbilities(Player player, AbilityTree abilityTree) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.getAbilities(serverPlayer).getAll(LearnableSkill.class, Skill::isUnlocked).forEach(skill -> skill.onRemove(SkillContext.of(serverPlayer)));
      }

      this.playerMap.put(player.getUUID(), abilityTree);
      if (player instanceof ServerPlayer serverPlayer) {
         abilityTree.getAll(LearnableSkill.class, Skill::isUnlocked).forEach(skill -> skill.onAdd(SkillContext.of(serverPlayer)));
      }

      this.setDirty();
   }

   public AbilityTree getAbilities(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, id -> ModConfigs.ABILITIES.get().orElse(new AbilityTree()).copy());
   }

   public static void setAbilityOnCooldown(ServerPlayer player, Class<?> type) {
      AbilityTree abilities = get(player.getLevel()).getAbilities(player);
      abilities.iterate(type, skill -> {
         if (skill instanceof Ability ability) {
            ability.putOnCooldown(SkillContext.of(player));
         }
      });
   }

   public static void deactivateAllAbilities(ServerPlayer player) {
      AbilityTree abilities = get(player.getLevel()).getAbilities(player);
      abilities.iterate(Ability.class, ability -> ability.setActive(false));
   }

   public boolean isDirty() {
      return true;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer()) {
            PlayerAbilitiesData data = get((ServerLevel)event.world);
            AbilityTree current = ModConfigs.ABILITIES.get().orElse(null);
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
            PlayerAbilitiesData data = get(player.getLevel());
            if (data.scheduledMerge.remove(player.getUUID())) {
               ModConfigs.ABILITIES.get().ifPresent(tree -> {
                  SkillContext context = SkillContext.of(player);
                  data.playerMap.put(player.getUUID(), (AbilityTree)data.playerMap.get(player.getUUID()).mergeFrom(tree.copy(), context));
                  PlayerVaultStats stats = PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player);
                  stats.setSkillPoints(context.getLearnPoints());
                  stats.setRegretPoints(context.getRegretPoints());
               });
            }

            data.getAbilities(player).onTick(SkillContext.of(player));
            if (data.scheduledRefund.remove(player.getUUID())) {
               PlayerVaultStats stats = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player);
               int skillOrbs = stats.getTotalSpentSkillPoints() + stats.getUnspentSkillPoints() - stats.getVaultLevel();
               stats.setSkillPoints(stats.getVaultLevel());
               stats.setTotalSpentSkillPoints(0);
               stats.setExpertisePoints(stats.getVaultLevel() / 5);
               stats.setTotalSpentExpertisePoints(0);
               ScheduledItemDropData drops = ScheduledItemDropData.get(player.getLevel());
               drops.addDrop(player, new ItemStack(ModItems.EXTRAORDINARY_BENITOITE, skillOrbs));
               drops.addDrop(player, new ItemStack(ModItems.BLACK_OPAL_GEM, skillOrbs * 4));
               drops.addDrop(player, new ItemStack(ModItems.VAULT_ESSENCE, skillOrbs * 4));
            }

            if (data.scheduledCorruptionCheck.remove(player.getUUID())) {
               PlayerVaultStats vaultStats = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player);
               int valid = vaultStats.getVaultLevel();
               if (QuestStatesData.get().getState(player).getCompleted().contains("learning_skills")) {
                  valid++;
               }

               int current = vaultStats.getUnspentSkillPoints() + data.playerMap.get(player.getUUID()).getSpentLearnPoints();
               current += PlayerTalentsData.get(player.getLevel()).getTalents(player).getSpentLearnPoints();
               if (current != valid) {
                  PlayerVaultStatsData.get(player.getLevel()).resetSkills(player, false);
                  vaultStats.setSkillPoints(valid);
                  PlayerVaultStatsData.get(player.getLevel()).setDirty();
                  MutableComponent message = TextComponent.EMPTY
                     .copy()
                     .append(
                        new TextComponent("Your abilities and talents have been reset due to a data corruption issue. You now have")
                           .withStyle(ChatFormatting.GRAY)
                     )
                     .append(new TextComponent(" " + valid + " ").withStyle(ChatFormatting.GREEN))
                     .append(new TextComponent("skill points available instead of").withStyle(ChatFormatting.GRAY))
                     .append(new TextComponent(" " + current).withStyle(ChatFormatting.RED))
                     .append(
                        new TextComponent(". This amount equals your level with an additional point if you completed the skill quest.")
                           .withStyle(ChatFormatting.GRAY)
                     );
                  player.sendMessage(message, Util.NIL_UUID);
                  VaultMod.LOGGER.warn("[" + player.getDisplayName().getString() + "] " + message.getString());
               }
            }
         }
      }
   }

   public void load(CompoundTag nbt) {
      this.playerMap.clear();
      this.scheduledMerge.clear();
      this.scheduledRefund.clear();
      this.scheduledCorruptionCheck.clear();
      this.version = nbt.getInt("Version");
      if (nbt.contains("PlayerEntries", 9)) {
         this.version = -1;
      } else {
         ListTag scheduledRefund = nbt.getList("ScheduledRefund", 8);

         for (int i = 0; i < scheduledRefund.size(); i++) {
            this.scheduledRefund.add(UUID.fromString(scheduledRefund.getString(i)));
         }
      }

      ListTag scheduledCorruptionCheck = nbt.getList("ScheduledCorruptionCheck", 8);

      for (int i = 0; i < scheduledCorruptionCheck.size(); i++) {
         this.scheduledCorruptionCheck.add(UUID.fromString(scheduledCorruptionCheck.getString(i)));
      }

      ListTag playerList = nbt.getList("Players", 8);
      ListTag abilitiesList = nbt.getList("Abilities", 10);
      if (playerList.size() != abilitiesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            Adapters.SKILL.readNbt(abilitiesList.getCompound(i)).ifPresent(tree -> {
               this.playerMap.put(playerUUID, (AbilityTree)tree);
               this.scheduledMerge.add(playerUUID);
            });
         }

         this.migrate(nbt);
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag abilitiesList = new ListTag();
      ListTag scheduledRefund = new ListTag();
      ListTag scheduledCorruptionCheck = new ListTag();
      this.playerMap.forEach((uuid, researchTree) -> Adapters.SKILL.writeNbt(researchTree).ifPresent(tag -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         abilitiesList.add(tag);
      }));
      this.scheduledRefund.forEach(uuid -> scheduledRefund.add(StringTag.valueOf(uuid.toString())));
      this.scheduledCorruptionCheck.forEach(uuid -> scheduledCorruptionCheck.add(StringTag.valueOf(uuid.toString())));
      nbt.putInt("Version", this.version);
      nbt.put("Players", playerList);
      nbt.put("Abilities", abilitiesList);
      nbt.put("ScheduledRefund", scheduledRefund);
      nbt.put("ScheduledCorruptionCheck", scheduledCorruptionCheck);
      return nbt;
   }

   private void migrate(CompoundTag nbt) {
      if (this.version == -1) {
         ListTag playerList = nbt.getList("PlayerEntries", 8);

         for (int i = 0; i < playerList.size(); i++) {
            this.scheduledRefund.add(UUID.fromString(playerList.getString(i)));
         }

         this.version++;
      }

      if (this.version == 0) {
         this.playerMap.forEach((uuid, tree) -> this.scheduledCorruptionCheck.add(uuid));
         this.version++;
      }
   }

   private static PlayerAbilitiesData create(CompoundTag tag) {
      PlayerAbilitiesData data = new PlayerAbilitiesData();
      data.load(tag);
      return data;
   }

   public static PlayerAbilitiesData get(ServerLevel world) {
      return get(world.getServer());
   }

   public static PlayerAbilitiesData get(MinecraftServer srv) {
      return (PlayerAbilitiesData)srv.overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerAbilitiesData::create, PlayerAbilitiesData::new, "the_vault_PlayerAbilities");
   }
}
