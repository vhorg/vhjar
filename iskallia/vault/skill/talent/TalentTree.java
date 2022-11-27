package iskallia.vault.skill.talent;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class TalentTree implements INBTSerializable<CompoundTag> {
   private final UUID uuid;
   private final List<TalentNode<?>> nodes = new ArrayList<>();

   public TalentTree(UUID uuid) {
      this.uuid = uuid;
      this.add(null, ModConfigs.TALENTS.getAll().stream().map(talentGroup -> new TalentNode<>((TalentGroup<?>)talentGroup, 0)).toArray(TalentNode[]::new));
   }

   public List<TalentNode<?>> getNodes() {
      return this.nodes;
   }

   public List<TalentNode<?>> getLearnedNodes() {
      return this.getNodes().stream().filter(TalentNode::isLearned).collect(Collectors.toList());
   }

   public <T extends Talent> List<TalentNode<T>> getLearnedNodes(Class<T> talentGroupType) {
      return this.getNodes()
         .stream()
         .filter(TalentNode::isLearned)
         .filter(talentNode -> talentGroupType.isAssignableFrom(talentNode.getTalent().getClass()))
         .map(node -> node)
         .collect(Collectors.toList());
   }

   public boolean hasLearnedNode(TalentGroup<?> talentGroup) {
      return this.getLearnedNodes().stream().anyMatch(node -> node.getGroup().getParentName().equals(talentGroup.getParentName()));
   }

   public <T extends Talent> Collection<T> getTalents(Class<T> talentType) {
      return this.getNodes()
         .stream()
         .filter(TalentNode::isLearned)
         .map(TalentNode::getTalent)
         .filter(talent -> talentType.isAssignableFrom(talent.getClass()))
         .map(talent -> (Talent)talent)
         .collect(Collectors.toList());
   }

   @Nonnull
   public <T extends Talent> TalentNode<T> getNodeOf(TalentGroup<T> talentGroup) {
      return this.getNodeByName(talentGroup.getParentName());
   }

   @Nonnull
   public <T extends Talent> TalentNode<T> getNodeByName(String name) {
      Optional<TalentNode<T>> talentWrapped = this.nodes
         .stream()
         .filter(node -> node.getGroup().getParentName().equals(name))
         .map(node -> (TalentNode<T>)node)
         .findFirst();
      if (!talentWrapped.isPresent()) {
         TalentNode<?> talentNode = new TalentNode<>(ModConfigs.TALENTS.getByName(name), 0);
         this.nodes.add(talentNode);
         return (TalentNode<T>)talentNode;
      } else {
         return talentWrapped.get();
      }
   }

   public TalentTree upgradeTalent(MinecraftServer server, TalentNode<?> talentNode) {
      this.remove(server, talentNode);
      TalentNode<?> upgradedTalentNode = new TalentNode<>(talentNode.getGroup(), talentNode.getLevel() + 1);
      this.add(server, upgradedTalentNode);
      return this;
   }

   public TalentTree downgradeTalent(MinecraftServer server, TalentNode<?> talentNode) {
      this.remove(server, talentNode);
      int targetLevel = talentNode.getLevel() - 1;
      TalentNode<?> upgradedTalentNode = new TalentNode<>(talentNode.getGroup(), Math.max(targetLevel, 0));
      this.add(server, upgradedTalentNode);
      return this;
   }

   public TalentTree add(MinecraftServer server, TalentNode<?>... nodes) {
      for (TalentNode<?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.getTalent().onAdded(player);
            }

            AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
         });
         this.nodes.add(node);
      }

      return this;
   }

   public TalentTree tick(MinecraftServer server) {
      NetcodeUtils.runIfPresent(server, this.uuid, player -> this.nodes.stream().filter(TalentNode::isLearned).forEach(node -> node.getTalent().tick(player)));
      return this;
   }

   public TalentTree remove(MinecraftServer server, TalentNode<?>... nodes) {
      for (TalentNode<?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.getTalent().onRemoved(player);
            }

            AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
         });
         this.nodes.remove(node);
      }

      return this;
   }

   public void sync(MinecraftServer server) {
      this.syncTree(server);
   }

   public void syncTree(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server, this.uuid, player -> ModNetwork.CHANNEL.sendTo(new KnownTalentsMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();
      this.nodes.stream().map(TalentNode::serializeNBT).forEach(list::add);
      nbt.put("Nodes", list);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag list = nbt.getList("Nodes", 10);
      this.nodes.clear();

      for (int i = 0; i < list.size(); i++) {
         TalentNode<?> talent = new TalentNode(list.getCompound(i));
         if (talent != null) {
            this.add(null, talent);
         }
      }
   }
}
