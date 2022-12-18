package iskallia.vault.world.data;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerAbilitiesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAbilities";
   private final Map<UUID, AbilityTree> playerMap = new HashMap<>();

   public AbilityTree getAbilities(Player player) {
      return this.getAbilities(player.getUUID());
   }

   public AbilityTree getAbilities(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, id -> new AbilityTree(id, this::setDirty));
   }

   public PlayerAbilitiesData add(ServerPlayer player, AbilityNode<?, ?>... nodes) {
      this.getAbilities(player).add(player.getServer(), nodes);
      this.setDirty();
      return this;
   }

   public PlayerAbilitiesData remove(ServerPlayer player, AbilityNode<?, ?>... nodes) {
      AbilityTree abilities = this.getAbilities(player);
      abilities.remove(player.getServer(), nodes);
      abilities.sync(player.server);
      this.setDirty();
      return this;
   }

   public PlayerAbilitiesData upgradeAbility(ServerPlayer player, AbilityNode<?, ?> abilityNode) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.upgradeAbility(player.getServer(), abilityNode);
      abilityTree.sync(player.server);
      this.setDirty();
      return this;
   }

   public PlayerAbilitiesData downgradeAbility(ServerPlayer player, AbilityNode<?, ?> abilityNode) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.downgradeAbility(player.getServer(), abilityNode);
      abilityTree.sync(player.server);
      this.setDirty();
      return this;
   }

   public PlayerAbilitiesData selectSpecialization(ServerPlayer player, AbilityNode<?, ?> node, @Nullable String specialization) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.selectSpecialization(player, node, specialization);
      abilityTree.sync(player.server);
      this.setDirty();
      return this;
   }

   public PlayerAbilitiesData resetAbilityTree(ServerPlayer player) {
      UUID uniqueID = player.getUUID();
      AbilityTree oldAbilityTree = this.playerMap.get(uniqueID);
      if (oldAbilityTree != null) {
         for (AbilityNode<?, ?> node : oldAbilityTree.getNodes()) {
            if (node.isLearned()) {
               node.onRemoved(player);
            }
         }
      }

      AbilityTree abilityTree = new AbilityTree(uniqueID, this::setDirty);
      this.playerMap.put(uniqueID, abilityTree);
      abilityTree.sync(player.server);
      this.setDirty();
      return this;
   }

   public static void setAbilityOnCooldown(ServerPlayer player, String abilityName) {
      AbilityTree abilities = get(player.getLevel()).getAbilities(player);
      AbilityNode<?, ?> node = abilities.getNodeByName(abilityName);
      if (node != null) {
         AbstractAbilityConfig config = node.getAbilityConfig();
         if (config != null) {
            abilities.putOnCooldown(player, node);
         }
      }
   }

   public static void deactivateAllAbilities(ServerPlayer player) {
      AbilityTree abilities = get(player.getLevel()).getAbilities(player);
      abilities.deactivateAllAbilities();
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer() && event.player instanceof ServerPlayer serverPlayer) {
            get(serverPlayer.getLevel()).getAbilities(serverPlayer).tick(serverPlayer);
         }
      }
   }

   public void load(CompoundTag nbt) {
      ListTag playerList = nbt.getList("PlayerEntries", 8);
      ListTag abilitiesList = nbt.getList("AbilityEntries", 10);
      if (playerList.size() != abilitiesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getAbilities(playerUUID).deserializeNBT(abilitiesList.getCompound(i));
         }
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag playerList = new ListTag();
      ListTag abilitiesList = new ListTag();
      this.playerMap.forEach((uuid, researchTree) -> {
         playerList.add(StringTag.valueOf(uuid.toString()));
         abilitiesList.add(researchTree.serializeNBT());
      });
      nbt.put("PlayerEntries", playerList);
      nbt.put("AbilityEntries", abilitiesList);
      return nbt;
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
