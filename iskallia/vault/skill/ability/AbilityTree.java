package iskallia.vault.skill.ability;

import com.google.common.collect.Iterables;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.effect.AbilityEffect;
import iskallia.vault.util.NetcodeUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class AbilityTree implements INBTSerializable<CompoundNBT> {
   private static final Comparator<AbilityNode<?, ?>> ABILITY_COMPARATOR = Comparator.comparing(node -> node.getGroup().getParentName());
   private final UUID uuid;
   private final SortedSet<AbilityNode<?, ?>> nodes = new TreeSet<>(ABILITY_COMPARATOR);
   private final HashMap<AbilityNode<?, ?>, Integer> cooldowns = new HashMap<>();
   private AbilityNode<?, ?> selectedAbility = null;
   private boolean active = false;
   private boolean swappingPerformed = false;
   private boolean swappingLocked = false;

   public AbilityTree(UUID uuid) {
      this.uuid = uuid;
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

   public AbilityNode<?, ?> getNodeOf(AbilityEffect<?> ability) {
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

   public boolean isActive() {
      return this.active;
   }

   public void setSwappingLocked(boolean swappingLocked) {
      this.swappingLocked = swappingLocked;
   }

   public AbilityTree scrollUp(MinecraftServer server) {
      return this.updateNewSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         abilityIndex = ++abilityIndex % learnedNodes.size();
         return learnedNodes.get(abilityIndex);
      });
   }

   public AbilityTree scrollDown(MinecraftServer server) {
      return this.updateNewSelectedAbility(server, selected -> {
         List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
         int abilityIndex = learnedNodes.indexOf(selected);
         if (--abilityIndex < 0) {
            abilityIndex += learnedNodes.size();
         }

         return learnedNodes.get(abilityIndex);
      });
   }

   private AbilityTree updateNewSelectedAbility(MinecraftServer server, Function<AbilityNode<?, ?>, AbilityNode<?, ?>> changeNodeFn) {
      List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
      if (this.swappingLocked) {
         return this;
      } else {
         if (!learnedNodes.isEmpty()) {
            boolean prevActive = this.active;
            this.active = false;
            AbilityNode<?, ?> selectedAbilityNode = this.getSelectedAbility();
            if (selectedAbilityNode != null) {
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  AbilityConfig selectedAbilityConfig = selectedAbilityNode.getAbilityConfig();
                  selectedAbilityNode.onBlur(player);
                  if (prevActive) {
                     if (selectedAbilityConfig.getBehavior() == AbilityConfig.Behavior.PRESS_TO_TOGGLE) {
                        if (selectedAbilityNode.onAction(player, false)) {
                           this.putOnCooldown(server, selectedAbilityNode, ModConfigs.ABILITIES.getCooldown(selectedAbilityNode, player));
                        }
                     } else if (selectedAbilityConfig.getBehavior() != AbilityConfig.Behavior.HOLD_TO_ACTIVATE) {
                        this.putOnCooldown(server, selectedAbilityNode, ModConfigs.ABILITIES.getCooldown(selectedAbilityNode, player));
                     }
                  }
               });
            }

            AbilityNode<?, ?> nextAttempt = changeNodeFn.apply(selectedAbilityNode);
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
      AbilityNode<?, ?> focusedAbilityNode = this.getSelectedAbility();
      if (focusedAbilityNode != null) {
         AbilityConfig focusedAbilityConfig = focusedAbilityNode.getAbilityConfig();
         if (focusedAbilityConfig.getBehavior() == AbilityConfig.Behavior.HOLD_TO_ACTIVATE) {
            this.active = true;
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               focusedAbilityNode.onAction(player, true);
               this.notifyActivity(server, focusedAbilityNode.getGroup(), 0, ModConfigs.ABILITIES.getCooldown(focusedAbilityNode, player), true);
            });
         }
      }
   }

   public void keyUp(MinecraftServer server) {
      this.swappingLocked = false;
      AbilityNode<?, ?> focusedAbilityNode = this.getSelectedAbility();
      if (focusedAbilityNode != null) {
         if (this.swappingPerformed) {
            this.swappingPerformed = false;
         } else if (!this.isOnCooldown(focusedAbilityNode)) {
            AbilityConfig focusedAbilityConfig = focusedAbilityNode.getAbilityConfig();
            AbilityConfig.Behavior behavior = focusedAbilityConfig.getBehavior();
            if (behavior == AbilityConfig.Behavior.PRESS_TO_TOGGLE) {
               this.active = !this.active;
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  if (focusedAbilityNode.onAction(player, this.active)) {
                     this.putOnCooldown(server, focusedAbilityNode, ModConfigs.ABILITIES.getCooldown(focusedAbilityNode, player));
                  }
               });
            } else if (behavior == AbilityConfig.Behavior.HOLD_TO_ACTIVATE) {
               this.active = false;
               NetcodeUtils.runIfPresent(server, this.uuid, player -> focusedAbilityNode.onAction(player, this.active));
               this.notifyActivity(server);
            } else if (behavior == AbilityConfig.Behavior.RELEASE_TO_PERFORM) {
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  if (focusedAbilityNode.onAction(player, this.active)) {
                     this.putOnCooldown(server, focusedAbilityNode, ModConfigs.ABILITIES.getCooldown(focusedAbilityNode, player));
                  }
               });
            }
         }
      }
   }

   public void quickSelectAbility(MinecraftServer server, String selectAbility) {
      List<AbilityNode<?, ?>> learnedNodes = this.getLearnedNodes();
      if (!learnedNodes.isEmpty()) {
         boolean prevActive = this.active;
         this.active = false;
         AbilityNode<?, ?> selectedAbilityNode = this.getSelectedAbility();
         if (selectedAbilityNode != null) {
            AbilityConfig abilityConfig = selectedAbilityNode.getAbilityConfig();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               selectedAbilityNode.onBlur(player);
               if (prevActive) {
                  if (abilityConfig.getBehavior() == AbilityConfig.Behavior.PRESS_TO_TOGGLE) {
                     if (selectedAbilityNode.onAction(player, this.active)) {
                        this.putOnCooldown(server, selectedAbilityNode, ModConfigs.ABILITIES.getCooldown(selectedAbilityNode, player));
                     }
                  } else if (abilityConfig.getBehavior() != AbilityConfig.Behavior.HOLD_TO_ACTIVATE) {
                     this.putOnCooldown(server, selectedAbilityNode, ModConfigs.ABILITIES.getCooldown(selectedAbilityNode, player));
                  }
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
      AbilityNode<?, ?> focusedAbility = this.getSelectedAbility();
      if (focusedAbility != null) {
         AbilityConfig.Behavior behavior = focusedAbility.getAbilityConfig().getBehavior();
         if (behavior == AbilityConfig.Behavior.HOLD_TO_ACTIVATE) {
            this.active = false;
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

   public boolean selectSpecialization(String ability, @Nullable String specialization) {
      AbilityNode<?, ?> node = this.getNodeByName(ability);
      if (node != null) {
         node.setSpecialization(specialization);
         return true;
      } else {
         return false;
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
            this.putOnCooldown(server, nodex, 0, ModConfigs.ABILITIES.getCooldown(nodex, player));
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

   public void tick(ServerPlayerEntity sPlayer) {
      AbilityNode<?, ?> selectedAbility = this.getSelectedAbility();
      if (selectedAbility != null) {
         selectedAbility.onTick(sPlayer, this.isActive());
      }

      for (AbilityNode<?, ?> ability : this.cooldowns.keySet()) {
         this.cooldowns.computeIfPresent(ability, (index, cooldown) -> cooldown - 1);
         this.notifyCooldown(
            sPlayer.func_184102_h(), ability.getGroup(), this.cooldowns.getOrDefault(ability, 0), ModConfigs.ABILITIES.getCooldown(ability, sPlayer)
         );
      }

      this.cooldowns.entrySet().removeIf(cooldown -> cooldown.getValue() <= 0);
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
         player -> ModNetwork.CHANNEL.sendTo(new AbilityKnownOnesMessage(this), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public void syncFocusedIndex(MinecraftServer server) {
      AbilityNode<?, ?> selected = this.getSelectedAbility();
      if (selected != null) {
         NetcodeUtils.runIfPresent(
            server,
            this.uuid,
            player -> ModNetwork.CHANNEL
               .sendTo(new AbilityFocusMessage(selected.getGroup()), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
         );
      }
   }

   public void notifyActivity(MinecraftServer server) {
      AbilityNode<?, ?> selected = this.getSelectedAbility();
      if (selected != null) {
         NetcodeUtils.runIfPresent(
            server,
            this.uuid,
            player -> this.notifyActivity(
               server, selected.getGroup(), this.cooldowns.getOrDefault(selected, 0), ModConfigs.ABILITIES.getCooldown(selected, player), this.active
            )
         );
      }
   }

   public boolean isOnCooldown(AbilityNode<?, ?> abilityNode) {
      return this.getCooldown(abilityNode) > 0;
   }

   public int getCooldown(AbilityNode<?, ?> abilityNode) {
      return this.cooldowns.getOrDefault(abilityNode, 0);
   }

   public void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> ability, int cooldownTicks) {
      this.putOnCooldown(server, ability, cooldownTicks, cooldownTicks);
   }

   public void putOnCooldown(MinecraftServer server, @Nonnull AbilityNode<?, ?> ability, int cooldownTicks, int maxCooldown) {
      this.cooldowns.put(ability, cooldownTicks);
      this.notifyCooldown(server, ability.getGroup(), cooldownTicks, maxCooldown);
   }

   public void notifyCooldown(MinecraftServer server, @Nonnull AbilityGroup<?, ?> ability, int cooldown, int maxCooldown) {
      this.notifyActivity(server, ability, cooldown, maxCooldown, AbilityTree.ActivityFlag.NO_OP);
   }

   public void notifyActivity(MinecraftServer server, @Nonnull AbilityGroup<?, ?> ability, int cooldown, int maxCooldown, boolean active) {
      this.notifyActivity(
         server, ability, cooldown, maxCooldown, active ? AbilityTree.ActivityFlag.ACTIVATE_ABILITY : AbilityTree.ActivityFlag.DEACTIVATE_ABILITY
      );
   }

   public void notifyActivity(MinecraftServer server, @Nonnull AbilityGroup<?, ?> ability, int cooldown, int maxCooldown, AbilityTree.ActivityFlag activeFlag) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL
            .sendTo(
               new AbilityActivityMessage(ability, cooldown, maxCooldown, activeFlag), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT
            )
      );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT list = new ListNBT();
      this.nodes.stream().map(AbilityNode::serializeNBT).forEach(list::add);
      nbt.func_218657_a("Nodes", list);
      AbilityNode<?, ?> selected = this.getSelectedAbility();
      if (selected != null) {
         nbt.func_74778_a("SelectedAbility", selected.getGroup().getParentName());
      }

      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      ListNBT list = nbt.func_150295_c("Nodes", 10);
      this.nodes.clear();

      for (int i = 0; i < list.size(); i++) {
         this.add(null, AbilityNode.fromNBT(list.func_150305_b(i)));
      }

      if (nbt.func_150297_b("SelectedAbility", 8)) {
         this.setSelectedAbility(this.getNodeByName(nbt.func_74779_i("SelectedAbility")));
      }
   }

   public static enum ActivityFlag {
      NO_OP,
      DEACTIVATE_ABILITY,
      ACTIVATE_ABILITY;
   }
}
