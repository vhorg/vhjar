package iskallia.vault.world.data;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerAbilitiesData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerAbilities";
   private final Map<UUID, AbilityTree> playerMap = new HashMap<>();

   public PlayerAbilitiesData() {
      super("the_vault_PlayerAbilities");
   }

   public PlayerAbilitiesData(String name) {
      super(name);
   }

   public AbilityTree getAbilities(PlayerEntity player) {
      return this.getAbilities(player.func_110124_au());
   }

   public AbilityTree getAbilities(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, AbilityTree::new);
   }

   public PlayerAbilitiesData add(ServerPlayerEntity player, AbilityNode<?, ?>... nodes) {
      this.getAbilities(player).add(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerAbilitiesData remove(ServerPlayerEntity player, AbilityNode<?, ?>... nodes) {
      this.getAbilities(player).remove(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerAbilitiesData upgradeAbility(ServerPlayerEntity player, AbilityNode<?, ?> abilityNode) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.upgradeAbility(player.func_184102_h(), abilityNode);
      abilityTree.sync(player.field_71133_b);
      this.func_76185_a();
      return this;
   }

   public PlayerAbilitiesData downgradeAbility(ServerPlayerEntity player, AbilityNode<?, ?> abilityNode) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.downgradeAbility(player.func_184102_h(), abilityNode);
      abilityTree.sync(player.field_71133_b);
      this.func_76185_a();
      return this;
   }

   public PlayerAbilitiesData selectSpecialization(ServerPlayerEntity player, String ability, @Nullable String specialization) {
      AbilityTree abilityTree = this.getAbilities(player);
      abilityTree.selectSpecialization(ability, specialization);
      abilityTree.sync(player.field_71133_b);
      this.func_76185_a();
      return this;
   }

   public PlayerAbilitiesData resetAbilityTree(ServerPlayerEntity player) {
      UUID uniqueID = player.func_110124_au();
      AbilityTree oldAbilityTree = this.playerMap.get(uniqueID);
      if (oldAbilityTree != null) {
         for (AbilityNode<?, ?> node : oldAbilityTree.getNodes()) {
            if (node.isLearned()) {
               node.onRemoved(player);
            }
         }
      }

      AbilityTree abilityTree = new AbilityTree(uniqueID);
      this.playerMap.put(uniqueID, abilityTree);
      abilityTree.sync(player.field_71133_b);
      this.func_76185_a();
      return this;
   }

   public static void setAbilityOnCooldown(ServerPlayerEntity player, String abilityName) {
      AbilityTree abilities = get(player.func_71121_q()).getAbilities(player);
      AbilityNode<?, ?> abilityNode = abilities.getNodeByName(abilityName);
      abilities.putOnCooldown(player.func_184102_h(), abilityNode, ModConfigs.ABILITIES.getCooldown(abilityNode, player));
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer() && event.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
            get(sPlayer.func_71121_q()).getAbilities(sPlayer).tick(sPlayer);
         }
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT abilitiesList = nbt.func_150295_c("AbilityEntries", 10);
      if (playerList.size() != abilitiesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getAbilities(playerUUID).deserializeNBT(abilitiesList.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT abilitiesList = new ListNBT();
      this.playerMap.forEach((uuid, researchTree) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         abilitiesList.add(researchTree.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("AbilityEntries", abilitiesList);
      return nbt;
   }

   public static PlayerAbilitiesData get(ServerWorld world) {
      return get(world.func_73046_m());
   }

   public static PlayerAbilitiesData get(MinecraftServer srv) {
      return (PlayerAbilitiesData)srv.func_241755_D_().func_217481_x().func_215752_a(PlayerAbilitiesData::new, "the_vault_PlayerAbilities");
   }
}
