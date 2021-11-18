package iskallia.vault.world.data;

import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerSetsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerSets";
   private final Map<UUID, SetTree> playerMap = new HashMap<>();

   public PlayerSetsData() {
      this("the_vault_PlayerSets");
   }

   public PlayerSetsData(String name) {
      super(name);
   }

   public SetTree getSets(PlayerEntity player) {
      return this.getSets(player.func_110124_au());
   }

   public SetTree getSets(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, SetTree::new);
   }

   public PlayerSetsData add(ServerPlayerEntity player, SetNode<?>... nodes) {
      this.getSets(player).add(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerSetsData remove(ServerPlayerEntity player, SetNode<?>... nodes) {
      this.getSets(player).remove(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerSetsData resetSetTree(ServerPlayerEntity player) {
      UUID uniqueID = player.func_110124_au();
      SetTree oldTalentTree = this.playerMap.get(uniqueID);
      if (oldTalentTree != null) {
         for (SetNode<?> node : oldTalentTree.getNodes()) {
            if (node.isActive()) {
               node.getSet().onRemoved(player);
            }
         }
      }

      SetTree setTree = new SetTree(uniqueID);
      this.playerMap.put(uniqueID, setTree);
      this.func_76185_a();
      return this;
   }

   public PlayerSetsData tick(MinecraftServer server) {
      this.playerMap.values().forEach(setTree -> setTree.tick(server));
      return this;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER) {
         get((ServerWorld)event.world).tick(((ServerWorld)event.world).func_73046_m());
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.side == LogicalSide.SERVER) {
         get((ServerWorld)event.player.field_70170_p).getSets(event.player);
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT talentList = nbt.func_150295_c("SetEntries", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getSets(playerUUID).deserializeNBT(talentList.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT talentList = new ListNBT();
      this.playerMap.forEach((uuid, abilityTree) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         talentList.add(abilityTree.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("SetEntries", talentList);
      return nbt;
   }

   public static PlayerSetsData get(ServerWorld world) {
      return (PlayerSetsData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerSetsData::new, "the_vault_PlayerSets");
   }
}
