package iskallia.vault.research;

import iskallia.vault.config.ResearchGroupConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.research.group.ResearchGroup;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.PlayerReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class ResearchTree implements INBTSerializable<CompoundTag> {
   protected final List<String> researchesDone = new ArrayList<>();
   protected final List<PlayerReference> researchShares = new ArrayList<>();

   private ResearchTree() {
   }

   public ResearchTree(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public static ResearchTree empty() {
      return new ResearchTree();
   }

   public List<String> getResearchesDone() {
      return Collections.unmodifiableList(this.researchesDone);
   }

   public boolean isResearched(Research research) {
      return this.isResearched(research.getName());
   }

   public boolean isResearched(String researchName) {
      return this.researchesDone.contains(researchName);
   }

   public void research(Research research) {
      this.researchesDone.add(research.getName());
   }

   public void removeResearch(Research research) {
      this.researchesDone.remove(research.getName());
   }

   public void resetResearches() {
      this.researchesDone.clear();
   }

   public List<PlayerReference> getResearchShares() {
      return Collections.unmodifiableList(this.researchShares);
   }

   public void addShare(PlayerReference reference) {
      this.researchShares.add(reference);
   }

   public void resetShares() {
      this.researchShares.clear();
   }

   public int getResearchCost(Research research) {
      float cost = research.getCost();
      ResearchGroupConfig config = ModConfigs.RESEARCH_GROUPS;
      ResearchGroup thisGroup = config.getResearchGroup(research);
      String thisGroupId = config.getResearchGroupId(thisGroup);

      for (String doneResearch : this.getResearchesDone()) {
         ResearchGroup otherGroup = config.getResearchGroup(doneResearch);
         if (otherGroup != null) {
            cost += otherGroup.getGroupIncreasedResearchCost(thisGroupId);
         }
      }

      cost *= 1.0F + this.getTeamResearchCostIncreaseMultiplier();
      return Math.max(1, Math.round(cost));
   }

   public float getTeamResearchCostIncreaseMultiplier() {
      return this.researchShares.size() * 0.5F;
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

   public void sync(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new ResearchTreeMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag researches = new ListTag();
      this.researchesDone.forEach(researchName -> {
         CompoundTag research = new CompoundTag();
         research.putString("name", researchName);
         researches.add(research);
      });
      nbt.put("researches", researches);
      ListTag shares = new ListTag();
      this.researchShares.forEach(share -> shares.add(share.serialize()));
      nbt.put("shares", shares);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag researches = nbt.getList("researches", 10);
      this.researchesDone.clear();

      for (int i = 0; i < researches.size(); i++) {
         CompoundTag researchNBT = researches.getCompound(i);
         String name = researchNBT.getString("name");
         this.researchesDone.add(name);
      }

      ListTag shares = nbt.getList("shares", 10);
      this.researchShares.clear();

      for (int i = 0; i < shares.size(); i++) {
         this.researchShares.add(new PlayerReference(shares.getCompound(i)));
      }
   }
}
