package iskallia.vault.research;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.NetcodeUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class ResearchTree implements INBTSerializable<CompoundNBT> {
   protected UUID playerUUID;
   protected List<String> researchesDone;

   public ResearchTree(UUID playerUUID) {
      this.playerUUID = playerUUID;
      this.researchesDone = new LinkedList<>();
   }

   public List<String> getResearchesDone() {
      return this.researchesDone;
   }

   public boolean isResearched(String researchName) {
      return this.researchesDone.contains(researchName);
   }

   public void research(String researchName) {
      this.researchesDone.add(researchName);
   }

   public void resetAll() {
      this.researchesDone.clear();
   }

   public String restrictedBy(Item item, Restrictions.Type restrictionType) {
      for (Research research : ModConfigs.RESEARCHES.getAll()) {
         if (!this.researchesDone.contains(research.getName()) && research.restricts(item, restrictionType)) {
            return research.getName();
         }
      }

      return null;
   }

   public String restrictedBy(Block block, Restrictions.Type restrictionType) {
      for (Research research : ModConfigs.RESEARCHES.getAll()) {
         if (!this.researchesDone.contains(research.getName()) && research.restricts(block, restrictionType)) {
            return research.getName();
         }
      }

      return null;
   }

   public String restrictedBy(EntityType<?> entityType, Restrictions.Type restrictionType) {
      for (Research research : ModConfigs.RESEARCHES.getAll()) {
         if (!this.researchesDone.contains(research.getName()) && research.restricts(entityType, restrictionType)) {
            return research.getName();
         }
      }

      return null;
   }

   public void sync(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server,
         this.playerUUID,
         player -> ModNetwork.CHANNEL
            .sendTo(new ResearchTreeMessage(this, player.func_110124_au()), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_186854_a("playerUUID", this.playerUUID);
      ListNBT researches = new ListNBT();

      for (int i = 0; i < this.researchesDone.size(); i++) {
         CompoundNBT research = new CompoundNBT();
         research.func_74778_a("name", this.researchesDone.get(i));
         researches.add(i, research);
      }

      nbt.func_218657_a("researches", researches);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.playerUUID = nbt.func_186857_a("playerUUID");
      ListNBT researches = nbt.func_150295_c("researches", 10);
      this.researchesDone = new LinkedList<>();

      for (int i = 0; i < researches.size(); i++) {
         CompoundNBT researchNBT = researches.func_150305_b(i);
         String name = researchNBT.func_74779_i("name");
         this.researchesDone.add(name);
      }
   }
}
