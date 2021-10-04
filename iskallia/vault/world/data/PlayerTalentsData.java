package iskallia.vault.world.data;

import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
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
public class PlayerTalentsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerTalents";
   private Map<UUID, TalentTree> playerMap = new HashMap<>();

   public PlayerTalentsData() {
      this("the_vault_PlayerTalents");
   }

   public PlayerTalentsData(String name) {
      super(name);
   }

   public TalentTree getTalents(PlayerEntity player) {
      return this.getTalents(player.func_110124_au());
   }

   public TalentTree getTalents(UUID uuid) {
      return this.playerMap.computeIfAbsent(uuid, TalentTree::new);
   }

   public PlayerTalentsData add(ServerPlayerEntity player, TalentNode<?>... nodes) {
      this.getTalents(player).add(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerTalentsData remove(ServerPlayerEntity player, TalentNode<?>... nodes) {
      this.getTalents(player).remove(player.func_184102_h(), nodes);
      this.func_76185_a();
      return this;
   }

   public PlayerTalentsData upgradeTalent(ServerPlayerEntity player, TalentNode<?> talentNode) {
      TalentTree talentTree = this.getTalents(player);
      talentTree.upgradeTalent(player.func_184102_h(), talentNode);
      talentTree.sync(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public PlayerTalentsData downgradeTalent(ServerPlayerEntity player, TalentNode<?> talentNode) {
      TalentTree talentTree = this.getTalents(player);
      talentTree.downgradeTalent(player.func_184102_h(), talentNode);
      talentTree.sync(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public PlayerTalentsData resetTalentTree(ServerPlayerEntity player) {
      UUID uniqueID = player.func_110124_au();
      TalentTree oldTalentTree = this.playerMap.get(uniqueID);
      if (oldTalentTree != null) {
         for (TalentNode<?> node : oldTalentTree.getNodes()) {
            if (node.isLearned()) {
               node.getTalent().onRemoved(player);
            }
         }
      }

      TalentTree talentTree = new TalentTree(uniqueID);
      this.playerMap.put(uniqueID, talentTree);
      talentTree.sync(player.func_184102_h());
      this.func_76185_a();
      return this;
   }

   public PlayerTalentsData tick(MinecraftServer server) {
      this.playerMap.values().forEach(abilityTree -> abilityTree.tick(server));
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
         get((ServerWorld)event.player.field_70170_p).getTalents(event.player);
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      ListNBT playerList = nbt.func_150295_c("PlayerEntries", 8);
      ListNBT talentList = nbt.func_150295_c("TalentEntries", 10);
      if (playerList.size() != talentList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.func_150307_f(i));
            this.getTalents(playerUUID).deserializeNBT(talentList.func_150305_b(i));
         }
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT playerList = new ListNBT();
      ListNBT talentList = new ListNBT();
      this.playerMap.forEach((uuid, talentTree) -> {
         playerList.add(StringNBT.func_229705_a_(uuid.toString()));
         talentList.add(talentTree.serializeNBT());
      });
      nbt.func_218657_a("PlayerEntries", playerList);
      nbt.func_218657_a("TalentEntries", talentList);
      return nbt;
   }

   public static PlayerTalentsData get(ServerWorld world) {
      return (PlayerTalentsData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerTalentsData::new, "the_vault_PlayerTalents");
   }
}
