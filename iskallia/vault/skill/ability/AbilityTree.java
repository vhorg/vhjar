package iskallia.vault.skill.ability;

import com.google.common.collect.Iterables;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.calc.CooldownHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public class AbilityTree implements INBTSerializable<CompoundTag> {
   private static final Comparator<AbilityNode<?, ?>> ABILITY_COMPARATOR = Comparator.comparing(node -> node.getGroup().getParentName());
   private final UUID uuid;
   private final Runnable onChange;
   private final SortedSet<AbilityNode<?, ?>> nodes = new TreeSet<>(ABILITY_COMPARATOR);
   private final HashMap<AbilityNode<?, ?>, Integer> cooldowns = new HashMap<>();
   private AbilityNode<?, ?> selectedAbility = null;
   private final Set<AbilityNode<?, ?>> activeAbilitySet = new HashSet<>();
   private final Set<AbilityNode<?, ?>> toDeactivateAbilitySet = new HashSet<>();
   private boolean swappingPerformed = false;
   private boolean swappingLocked = false;
   private static final BiFunction<AbilityNode<?, ?>, Integer, Integer> DECREMENT_COOLDOWN_BI_FUNCTION = (index, cooldown) -> cooldown - 1;
   private static final Predicate<Entry<AbilityNode<?, ?>, Integer>> COOLDOWN_REMOVAL_PREDICATE = cooldown -> cooldown.getValue() <= 0;
   private static final String TAG_NODES = "Nodes";
   private static final String TAG_SELECTED_ABILITY = "SelectedAbility";
   private static final String TAG_ACTIVE_ABILITY_SET = "ActiveAbilitySet";

   public AbilityTree(UUID uuid) {
      this(uuid, () -> {});
   }

   public AbilityTree(UUID uuid, Runnable onChange) {
      this.uuid = uuid;
      this.onChange = onChange;
      this.add(
         null, ModConfigs.ABILITIES.getAll().stream().map(abilityGroup -> new AbilityNode(abilityGroup.getParentName(), 0, null)).collect(Collectors.toList())
      );
   }

   public Set<AbilityNode<?, ?>> getNodes() {
      return this.nodes;
   }

   public List<AbilityNode<?, ?>> getLearnedNodes() {
      return this.getNodes().stream().filter(AbilityNode::isLearned).sorted(ABILITY_COMPARATOR).collect(Collectors.toList());
   }

   @Nullable
   public AbilityNode<?, ?> getSelectedAbility() {
      this.updateSelectedAbility();
      return this.selectedAbility;
   }

   @Nullable
   private AbilityNode<?, ?> setSelectedAbility(@Nullable AbilityNode<?, ?> abilityNode) {
      this.selectedAbility = abilityNode;
      return this.getSelectedAbility();
   }

   public AbilityNode<?, ?> getNodeOf(AbilityGroup<?, ?> abilityGroup) {
      return this.getNodeByName(abilityGroup.getParentName());
   }

   public AbilityNode<?, ?> getNodeOf(AbstractAbility<?> ability) {
      return this.getNodeByName(ability.getAbilityGroupName());
   }

   public AbilityNode<?, ?> getNodeByName(String name) {
      return this.getNodes().stream().filter(node -> node.getGroup().getParentName().equals(name)).findFirst().orElseGet(() -> {
         AbilityGroup<?, ?> group = ModConfigs.ABILITIES.getAbilityGroupByName(name);
         AbilityNode<?, ?> abilityNode = new AbilityNode(group.getParentName(), 0, null);
         this.nodes.add(abilityNode);
         return abilityNode;
      });
   }

   public boolean isAbilityActive(AbilityNode<?, ?> node) {
      return this.activeAbilitySet.contains(node);
   }

   private boolean activateAbility(AbilityNode<?, ?> node) {
      this.onChange.run();
      return !this.activeAbilitySet.add(node);
   }

   private boolean deactivateAbility(AbilityNode<?, ?> node) {
      this.onChange.run();
      return this.activeAbilitySet.remove(node);
   }

   private void deactivateAbilityDeferred(AbilityNode<?, ?> node) {
      this.toDeactivateAbilitySet.add(node);
   }

   private void toggleAbility(AbilityNode<?, ?> node) {
      if (this.activeAbilitySet.contains(node)) {
         this.deactivateAbility(node);
      } else {
         this.activateAbility(node);
      }
   }

   private boolean isAbilitySynchronous(AbilityNode<?, ?> node) {
      return node.getKeyBehavior() != KeyBehavior.TOGGLE_ON_RELEASE;
   }

   public void setSwappingLocked(boolean swappingLocked) {
      this.swappingLocked = swappingLocked;
   }

   public AbilityTree scrollUp(MinecraftServer server) {
      return this.scrollSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         abilityIndex = ++abilityIndex % learnedNodes.size();
         return learnedNodes.get(abilityIndex);
      });
   }

   public AbilityTree scrollDown(MinecraftServer server) {
      return this.scrollSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         if (--abilityIndex < 0) {
            abilityIndex += learnedNodes.size();
         }

         return learnedNodes.get(abilityIndex);
      });
   }

   private AbilityTree scrollSelectedAbility(MinecraftServer server, Function<AbilityNode<?, ?>, AbilityNode<?, ?>> changeNodeFn) {
      List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
      if (this.swappingLocked) {
         return this;
      } else {
         if (!learnedNodes.isEmpty()) {
            AbilityNode<?, ?> node = this.getSelectedAbility();
            if (node != null) {
               boolean shouldCooldown = this.isAbilitySynchronous(node) && this.deactivateAbility(node);
               KeyBehavior keyBehavior = node.getKeyBehavior();
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  node.onBlur(player);
                  if (shouldCooldown && keyBehavior != KeyBehavior.ACTIVATE_ON_HOLD) {
                     this.putOnCooldown(player, node);
                  }
               });
            }

            AbilityNode<?, ?> nextAttempt = changeNodeFn.apply(node);
            AbilityNode<?, ?> nextSelection = this.setSelectedAbility(nextAttempt);
            if (nextSelection != null) {
               NetcodeUtils.runIfPresent(server, this.uuid, nextSelection::onFocus);
            }

            this.swappingPerformed = true;
            this.syncFocusedIndex(server);
            this.notifyActivity(server);
         }

         return this;
      }
   }

   public void keyDown(MinecraftServer server) {
      AbilityNode<?, ?> node = this.getSelectedAbility();
      if (node != null && !this.isOnCooldown(node)) {
         if (node.getKeyBehavior() == KeyBehavior.ACTIVATE_ON_HOLD) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               AbilityActionResult result = node.onAction(player, true);
               if (result == AbilityActionResult.SUCCESS_COOLDOWN) {
                  this.deactivateAbility(node);
                  this.putOnCooldown(player, node);
               } else if (result == AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED) {
                  this.activateAbility(node);
                  this.notifyActivity(server, node.getGroup(), 0, CooldownHelper.adjustCooldown(player, node), true);
               }

               this.notifyActivity(server);
            });
         }
      }
   }

   public void keyUp(MinecraftServer server) {
      this.swappingLocked = false;
      AbilityNode<?, ?> node = this.getSelectedAbility();
      if (node != null) {
         if (this.swappingPerformed) {
            this.swappingPerformed = false;
         } else if (!this.isOnCooldown(node)) {
            KeyBehavior keyBehavior = node.getKeyBehavior();
            if (keyBehavior == KeyBehavior.TOGGLE_ON_RELEASE) {
               this.toggleAbility(node);
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  if (node.onAction(player, this.isAbilityActive(node)) == AbilityActionResult.SUCCESS_COOLDOWN) {
                     this.putOnCooldown(player, node);
                  }
               });
            } else if (keyBehavior == KeyBehavior.ACTIVATE_ON_HOLD) {
               if (this.isAbilityActive(node)) {
                  this.deactivateAbility(node);
                  NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                     if (node.onAction(player, false) == AbilityActionResult.SUCCESS_COOLDOWN) {
                        this.putOnCooldown(player, node);
                     }
                  });
               }

               this.notifyActivity(server);
            } else if (keyBehavior == KeyBehavior.INSTANT_ON_RELEASE) {
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  if (node.onAction(player, this.isAbilityActive(node)) == AbilityActionResult.SUCCESS_COOLDOWN) {
                     this.putOnCooldown(player, node);
                  }
               });
            }
         }
      }
   }

   public void quickSelectAbility(MinecraftServer server, String selectAbility) {
      List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
      if (!learnedNodes.isEmpty()) {
         AbilityNode<?, ?> node = this.getSelectedAbility();
         if (node != null) {
            boolean shouldCooldown = this.isAbilitySynchronous(node) && this.deactivateAbility(node);
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               node.onBlur(player);
               if (shouldCooldown && node.getKeyBehavior() != KeyBehavior.ACTIVATE_ON_HOLD) {
                  this.putOnCooldown(player, node);
               }
            });
         }

         AbilityNode<?, ?> toSelect = null;

         for (AbilityNode<?, ?> learnedNode : learnedNodes) {
            if (learnedNode.getGroup().getParentName().equals(selectAbility)) {
               toSelect = learnedNode;
               break;
            }
         }

         AbilityNode<?, ?> newFocused = this.setSelectedAbility(toSelect);
         if (newFocused != null) {
            NetcodeUtils.runIfPresent(server, this.uuid, newFocused::onFocus);
         }

         this.syncFocusedIndex(server);
      }
   }

   public void cancelKeyDown(MinecraftServer server) {
      AbilityNode<?, ?> node = this.getSelectedAbility();
      if (node != null) {
         if (node.getKeyBehavior() == KeyBehavior.ACTIVATE_ON_HOLD) {
            this.deactivateAbility(node);
            this.swappingLocked = false;
            this.swappingPerformed = false;
         }

         this.notifyActivity(server);
      }
   }

   public void upgradeAbility(MinecraftServer server, AbilityNode<?, ?> abilityNode) {
      this.remove(server, abilityNode);
      AbilityNode<?, ?> upgradedAbilityNode = new AbilityNode(
         abilityNode.getGroup().getParentName(), abilityNode.getLevel() + 1, abilityNode.getSpecialization()
      );
      this.add(server, upgradedAbilityNode);
      this.setSelectedAbility(upgradedAbilityNode);
   }

   public void downgradeAbility(MinecraftServer server, AbilityNode<?, ?> abilityNode) {
      this.remove(server, abilityNode);
      int targetLevel = abilityNode.getLevel() - 1;
      AbilityNode<?, ?> downgradedAbilityNode = new AbilityNode(
         abilityNode.getGroup().getParentName(), Math.max(targetLevel, 0), abilityNode.getSpecialization()
      );
      this.add(server, downgradedAbilityNode);
      if (targetLevel > 0) {
         this.setSelectedAbility(downgradedAbilityNode);
      } else {
         this.updateSelectedAbility();
      }
   }

   public void selectSpecialization(ServerPlayer serverPlayer, AbilityNode<?, ?> node, @Nullable String specialization) {
      if (node != null) {
         if (this.isAbilityActive(node) && node.getKeyBehavior() == KeyBehavior.TOGGLE_ON_RELEASE) {
            this.deactivateAbility(node);
            NetcodeUtils.runIfPresent(serverPlayer.getServer(), this.uuid, player -> {
               if (node.onAction(player, false) == AbilityActionResult.SUCCESS_COOLDOWN) {
                  this.putOnCooldown(player, node);
               }
            });
         }

         node.setSpecialization(specialization);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void selectSpecialization(String ability, @Nullable String specialization) {
      AbilityNode<?, ?> node = this.getNodeByName(ability);
      if (node != null) {
         node.setSpecialization(specialization);
      }
   }

   public AbilityTree add(@Nullable MinecraftServer server, AbilityNode<?, ?>... nodes) {
      return this.add(server, Arrays.asList(nodes));
   }

   public AbilityTree add(@Nullable MinecraftServer server, Collection<AbilityNode<?, ?>> nodes) {
      for (AbilityNode<?, ?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.onAdded(player);
            }
         });
         this.nodes.add(node);
      }

      this.updateSelectedAbility();
      return this;
   }

   public AbilityTree remove(MinecraftServer server, AbilityNode<?, ?>... nodes) {
      NetcodeUtils.runIfPresent(server, this.uuid, player -> {
         for (AbilityNode<?, ?> nodex : this.getLearnedNodes()) {
            AbstractAbilityConfig config = nodex.getAbilityConfig();
            if (config != null) {
               this.putOnCooldown(server, nodex, 0, CooldownHelper.adjustCooldown(player, nodex));
            }
         }
      });

      for (AbilityNode<?, ?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.onRemoved(player);
            }
         });
         this.nodes.remove(node);
      }

      this.updateSelectedAbility();
      return this;
   }

   private void updateSelectedAbility() {
      if (this.getLearnedNodes().isEmpty()) {
         this.selectedAbility = null;
      } else {
         if (this.selectedAbility == null) {
            this.selectedAbility = (AbilityNode<?, ?>)Iterables.getFirst(this.getLearnedNodes(), null);
         } else {
            boolean containsSelected = false;

            for (AbilityNode<?, ?> ability : this.getLearnedNodes()) {
               if (ability.getGroup().equals(this.selectedAbility.getGroup())) {
                  containsSelected = true;
                  break;
               }
            }

            if (!containsSelected) {
               this.selectedAbility = (AbilityNode<?, ?>)Iterables.getFirst(this.getLearnedNodes(), null);
            }
         }
      }
   }

   public void tick(ServerPlayer serverPlayer) {
      AbilityNode<?, ?> selectedNode = this.getSelectedAbility();
      if (selectedNode != null && !this.isAbilityActive(selectedNode) && selectedNode.onTick(serverPlayer, false) == AbilityTickResult.COOLDOWN) {
         this.putOnCooldown(serverPlayer, selectedNode);
         this.notifyActivity(serverPlayer.getServer());
      }

      Iterator<AbilityNode<?, ?>> iterator = this.activeAbilitySet.iterator();

      while (iterator.hasNext()) {
         AbilityNode<?, ?> abilityNode = iterator.next();
         if (abilityNode.onTick(serverPlayer, true) == AbilityTickResult.COOLDOWN) {
            iterator.remove();
            this.putOnCooldown(serverPlayer, abilityNode);
            this.notifyActivity(serverPlayer.getServer());
         }
      }

      for (AbilityNode<?, ?> abilityNode : this.toDeactivateAbilitySet) {
         this.deactivateAbility(abilityNode);
      }

      this.toDeactivateAbilitySet.clear();

      for (AbilityNode<?, ?> node : this.cooldowns.keySet()) {
         this.cooldowns.computeIfPresent(node, DECREMENT_COOLDOWN_BI_FUNCTION);
         AbstractAbilityConfig config = node.getAbilityConfig();
         if (config != null) {
            this.notifyCooldown(
               serverPlayer.getServer(), node.getGroup(), this.cooldowns.getOrDefault(node, 0), CooldownHelper.adjustCooldown(serverPlayer, node)
            );
         }
      }

      this.cooldowns.entrySet().removeIf(COOLDOWN_REMOVAL_PREDICATE);
   }

   public void sync(MinecraftServer server) {
      this.syncTree(server);
      this.syncFocusedIndex(server);
      this.notifyActivity(server);
   }

   public void syncTree(MinecraftServer server) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL.sendTo(new AbilityKnownOnesMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public void syncFocusedIndex(MinecraftServer server) {
      AbilityNode<?, ?> selected = this.getSelectedAbility();
      if (selected != null) {
         NetcodeUtils.runIfPresent(
            server,
            this.uuid,
            player -> ModNetwork.CHANNEL.sendTo(new AbilityFocusMessage(selected.getGroup()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
         );
      }
   }

   public void notifyActivity(MinecraftServer server) {
      AbilityNode<?, ?> node = this.getSelectedAbility();
      if (node != null) {
         AbstractAbilityConfig config = node.getAbilityConfig();
         if (config != null) {
            if (!this.isAbilityActive(node)) {
               NetcodeUtils.runIfPresent(
                  server,
                  this.uuid,
                  player -> this.notifyActivity(
                     server, node.getGroup(), this.cooldowns.getOrDefault(node, 0), CooldownHelper.adjustCooldown(player, node), this.isAbilityActive(node)
                  )
               );
            }

            for (AbilityNode<?, ?> abilityNode : this.activeAbilitySet) {
               NetcodeUtils.runIfPresent(
                  server,
                  this.uuid,
                  player -> this.notifyActivity(
                     server,
                     abilityNode.getGroup(),
                     this.cooldowns.getOrDefault(abilityNode, 0),
                     CooldownHelper.adjustCooldown(player, abilityNode),
                     this.isAbilityActive(abilityNode)
                  )
               );
            }
         }
      }
   }

   public boolean isOnCooldown(AbilityNode<?, ?> abilityNode) {
      return this.getCooldown(abilityNode) > 0;
   }

   public int getCooldown(AbilityNode<?, ?> abilityNode) {
      return this.cooldowns.getOrDefault(abilityNode, 0);
   }

   public void putOnCooldown(ServerPlayer serverPlayer, @Nonnull AbilityNode<?, ?> abilityNode) {
      this.putOnCooldown(serverPlayer.getServer(), abilityNode, CooldownHelper.adjustCooldown(serverPlayer, abilityNode));
   }

   private void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> abilityNode, int cooldownTicks) {
      this.putOnCooldown(server, abilityNode, cooldownTicks, cooldownTicks);
   }

   private void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> abilityNode, int cooldownTicks, int maxCooldownTicks) {
      if (this.getSelectedAbility() == abilityNode) {
         this.deactivateAbilityDeferred(abilityNode);
      }

      this.cooldowns.put(abilityNode, cooldownTicks);
      this.notifyCooldown(server, abilityNode.getGroup(), cooldownTicks, maxCooldownTicks);
   }

   private void notifyCooldown(MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks) {
      this.notifyActivity(server, abilityGroup, cooldownTicks, maxCooldownTicks, AbilityTree.ActivityFlag.NO_OP);
   }

   private void notifyActivity(MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks, boolean active) {
      this.notifyActivity(
         server,
         abilityGroup,
         cooldownTicks,
         maxCooldownTicks,
         active ? AbilityTree.ActivityFlag.ACTIVATE_ABILITY : AbilityTree.ActivityFlag.DEACTIVATE_ABILITY
      );
   }

   private void notifyActivity(
      MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks, AbilityTree.ActivityFlag activeFlag
   ) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL
            .sendTo(
               new AbilityActivityMessage(abilityGroup, cooldownTicks, maxCooldownTicks, activeFlag),
               player.connection.connection,
               NetworkDirection.PLAY_TO_CLIENT
            )
      );
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();
      this.nodes.stream().map(AbilityNode::serializeNBT).forEach(list::add);
      nbt.put("Nodes", list);
      AbilityNode<?, ?> selected = this.getSelectedAbility();
      if (selected != null) {
         nbt.putString("SelectedAbility", selected.getGroup().getParentName());
      }

      if (!this.activeAbilitySet.isEmpty()) {
         ListTag activeList = new ListTag();
         this.activeAbilitySet.stream().map(abilityNode -> abilityNode.getGroup().getParentName()).map(StringTag::valueOf).forEach(activeList::add);
         nbt.put("ActiveAbilitySet", activeList);
      }

      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag list = nbt.getList("Nodes", 10);
      this.nodes.clear();

      for (int i = 0; i < list.size(); i++) {
         this.add(null, AbilityNode.fromNBT(list.getCompound(i)));
      }

      if (nbt.contains("SelectedAbility", 8)) {
         this.setSelectedAbility(this.getNodeByName(nbt.getString("SelectedAbility")));
      }

      if (nbt.contains("ActiveAbilitySet", 9)) {
         ListTag activeList = nbt.getList("ActiveAbilitySet", 8);

         for (int i = 0; i < activeList.size(); i++) {
            this.activeAbilitySet.add(this.getNodeByName(activeList.getString(i)));
         }
      }
   }

   public static enum ActivityFlag {
      NO_OP,
      DEACTIVATE_ABILITY,
      ACTIVATE_ABILITY;
   }
}
