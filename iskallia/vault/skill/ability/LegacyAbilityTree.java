package iskallia.vault.skill.ability;

import com.google.common.collect.Iterables;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.util.NetcodeUtils;
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

public class LegacyAbilityTree implements INBTSerializable<CompoundTag> {
   private static final Comparator<AbilityNode<?, ?>> ABILITY_COMPARATOR = Comparator.comparing(node -> node.getGroup().getParentName());
   private final UUID uuid;
   private final Runnable onChange;
   private final SortedSet<AbilityNode<?, ?>> nodes = new TreeSet<>(ABILITY_COMPARATOR);
   private final HashMap<AbilityNode<?, ?>, LegacyAbilityTree.Cooldown> cooldowns = new HashMap<>();
   private AbilityNode<?, ?> selectedAbility = null;
   private final Set<AbilityNode<?, ?>> activeAbilitySet = new HashSet<>();
   private final Set<AbilityNode<?, ?>> toDeactivateAbilitySet = new HashSet<>();
   private boolean swappingPerformed = false;
   private boolean swappingLocked = false;
   private static final BiFunction<AbilityNode<?, ?>, LegacyAbilityTree.Cooldown, LegacyAbilityTree.Cooldown> DECREMENT_COOLDOWN_BI_FUNCTION = (index, cooldown) -> cooldown.decrement();
   private static final Predicate<Entry<AbilityNode<?, ?>, LegacyAbilityTree.Cooldown>> COOLDOWN_REMOVAL_PREDICATE = cooldown -> cooldown.getValue().remainingCooldownTicks
      <= 0;
   private static final LegacyAbilityTree.Cooldown DEFAULT_COOLDOWN = new LegacyAbilityTree.Cooldown(0, 0, 0);
   private static final String TAG_NODES = "Nodes";
   private static final String TAG_SELECTED_ABILITY = "SelectedAbility";
   private static final String TAG_ACTIVE_ABILITY_SET = "ActiveAbilitySet";

   public LegacyAbilityTree(UUID uuid) {
      this(uuid, () -> {});
   }

   public LegacyAbilityTree(UUID uuid, Runnable onChange) {
      this.uuid = uuid;
      this.onChange = onChange;
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
      return null;
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

   public void deactivateAllAbilities() {
      this.toDeactivateAbilitySet.addAll(this.activeAbilitySet);
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

   public LegacyAbilityTree scrollUp(MinecraftServer server) {
      return this.scrollSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         abilityIndex = ++abilityIndex % learnedNodes.size();
         return learnedNodes.get(abilityIndex);
      });
   }

   public LegacyAbilityTree scrollDown(MinecraftServer server) {
      return this.scrollSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         if (--abilityIndex < 0) {
            abilityIndex += learnedNodes.size();
         }

         return learnedNodes.get(abilityIndex);
      });
   }

   private LegacyAbilityTree scrollSelectedAbility(MinecraftServer server, Function<AbilityNode<?, ?>, AbilityNode<?, ?>> changeNodeFn) {
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
               if (result.startCooldown()) {
                  this.deactivateAbility(node);
                  this.putOnCooldown(player, node);
               } else if (result.isSuccess()) {
                  this.activateAbility(node);
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
                  AbilityActionResult result = node.onAction(player, this.isAbilityActive(node));
                  if (result.startCooldown()) {
                     this.putOnCooldown(player, node, result.getCooldownDelayTicks());
                  }
               });
            } else if (keyBehavior == KeyBehavior.ACTIVATE_ON_HOLD) {
               if (this.isAbilityActive(node)) {
                  this.deactivateAbility(node);
                  NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                     AbilityActionResult result = node.onAction(player, false);
                     if (result.startCooldown()) {
                        this.putOnCooldown(player, node, result.getCooldownDelayTicks());
                     }
                  });
               }

               this.notifyActivity(server);
            } else if (keyBehavior == KeyBehavior.INSTANT_ON_RELEASE) {
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  AbilityActionResult result = node.onAction(player, this.isAbilityActive(node));
                  if (result.startCooldown()) {
                     this.putOnCooldown(player, node, result.getCooldownDelayTicks());
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
      NetcodeUtils.runIfPresent(server, this.uuid, player -> {
         if (abilityNode.getLevel() == 1) {
            this.selectSpecialization(player, abilityNode, null);
         }

         if (this.isAbilityActive(abilityNode)) {
            this.deactivateAbility(abilityNode);
            this.putOnCooldown(player, abilityNode);
         }
      });
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
               if (node.onAction(player, false).startCooldown()) {
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

   public LegacyAbilityTree add(@Nullable MinecraftServer server, AbilityNode<?, ?>... nodes) {
      return this.add(server, Arrays.asList(nodes));
   }

   public LegacyAbilityTree add(@Nullable MinecraftServer server, Collection<AbilityNode<?, ?>> nodes) {
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

   public LegacyAbilityTree remove(MinecraftServer server, AbilityNode<?, ?>... nodes) {
      NetcodeUtils.runIfPresent(server, this.uuid, player -> {
         for (AbilityNode var3 : this.getLearnedNodes()) {
            ;
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
      }

      this.cooldowns.entrySet().removeIf(COOLDOWN_REMOVAL_PREDICATE);
   }

   public void sync(MinecraftServer server) {
      this.syncTree(server);
      this.syncFocusedIndex(server);
      this.notifyActivity(server);
   }

   public void syncTree(MinecraftServer server) {
      NetcodeUtils.runIfPresent(server, this.uuid, player -> {});
   }

   public void syncFocusedIndex(MinecraftServer server) {
   }

   public void notifyActivity(MinecraftServer server) {
      AbilityNode<?, ?> node = this.getSelectedAbility();
      if (node != null) {
         if (!this.isAbilityActive(node)) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {});
         }

         for (AbilityNode<?, ?> abilityNode : this.activeAbilitySet) {
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {});
         }
      }
   }

   public boolean isOnCooldown(AbilityNode<?, ?> abilityNode) {
      return this.getCooldown(abilityNode) > 0;
   }

   private int getCooldown(AbilityNode<?, ?> abilityNode) {
      return this.cooldowns.getOrDefault(abilityNode, DEFAULT_COOLDOWN).remainingCooldownTicks;
   }

   public void putOnCooldown(ServerPlayer serverPlayer, @Nonnull AbilityNode<?, ?> abilityNode) {
   }

   public void putOnCooldown(ServerPlayer serverPlayer, @Nonnull AbilityNode<?, ?> abilityNode, int cooldownDelayTicks) {
   }

   private void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> abilityNode, int cooldownTicks, int cooldownDelayTicks) {
      this.putOnCooldown(server, abilityNode, cooldownTicks, cooldownTicks, cooldownDelayTicks);
   }

   private void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> abilityNode, int cooldownTicks, int maxCooldownTicks, int cooldownDelayTicks) {
      if (this.getSelectedAbility() == abilityNode) {
         this.deactivateAbilityDeferred(abilityNode);
      }

      this.cooldowns.put(abilityNode, new LegacyAbilityTree.Cooldown(cooldownTicks, maxCooldownTicks, cooldownDelayTicks));
      this.notifyCooldown(server, abilityNode.getGroup(), cooldownTicks, maxCooldownTicks);
   }

   private void notifyCooldown(MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks) {
      this.notifyActivity(server, abilityGroup, cooldownTicks, maxCooldownTicks, LegacyAbilityTree.ActivityFlag.NO_OP);
   }

   private void notifyActivity(MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks, boolean active) {
      this.notifyActivity(
         server,
         abilityGroup,
         cooldownTicks,
         maxCooldownTicks,
         active ? LegacyAbilityTree.ActivityFlag.ACTIVATE_ABILITY : LegacyAbilityTree.ActivityFlag.DEACTIVATE_ABILITY
      );
   }

   private void notifyActivity(
      MinecraftServer server, @Nonnull AbilityGroup<?, ?> abilityGroup, int cooldownTicks, int maxCooldownTicks, LegacyAbilityTree.ActivityFlag activeFlag
   ) {
      NetcodeUtils.runIfPresent(server, this.uuid, player -> {});
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
         this.setSelectedAbility(this.getNodeByName(LegacyAbilityMapper.mapAbilityName(nbt.getString("SelectedAbility"))));
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

   public static class Cooldown {
      public final int maxCooldownTicks;
      public int remainingCooldownTicks;
      public int remainingCooldownDelayTicks;

      public Cooldown(int maxCooldownTicks, int remainingCooldownTicks, int remainingCooldownDelayTicks) {
         this.maxCooldownTicks = maxCooldownTicks;
         this.remainingCooldownTicks = remainingCooldownTicks;
         this.remainingCooldownDelayTicks = remainingCooldownDelayTicks;
      }

      public LegacyAbilityTree.Cooldown decrement() {
         if (this.remainingCooldownDelayTicks > 0) {
            this.remainingCooldownDelayTicks--;
         } else {
            this.remainingCooldownTicks--;
         }

         return this;
      }
   }
}
