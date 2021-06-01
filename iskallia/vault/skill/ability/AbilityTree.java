package iskallia.vault.skill.ability;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.type.PlayerAbility;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class AbilityTree implements INBTSerializable<CompoundNBT> {
   private final UUID uuid;
   private List<AbilityNode<?>> nodes = new ArrayList<>();
   private HashMap<Integer, Integer> cooldowns = new HashMap<>();
   private int focusedAbilityIndex;
   private boolean active;
   private boolean swappingPerformed;
   private boolean swappingLocked;

   public AbilityTree(UUID uuid) {
      this.uuid = uuid;
      this.add(
         null, ModConfigs.ABILITIES.getAll().stream().map(abilityGroup -> new AbilityNode<>((AbilityGroup<?>)abilityGroup, 0)).toArray(AbilityNode[]::new)
      );
   }

   public List<AbilityNode<?>> getNodes() {
      return this.nodes;
   }

   public List<AbilityNode<?>> learnedNodes() {
      return this.nodes.stream().filter(AbilityNode::isLearned).collect(Collectors.toList());
   }

   public AbilityNode<?> getFocusedAbility() {
      List<AbilityNode<?>> learnedNodes = this.learnedNodes();
      return learnedNodes.size() == 0 ? null : learnedNodes.get(this.focusedAbilityIndex);
   }

   public AbilityNode<?> getNodeOf(AbilityGroup<?> abilityGroup) {
      return this.getNodeByName(abilityGroup.getParentName());
   }

   public AbilityNode<?> getNodeByName(String name) {
      Optional<AbilityNode<?>> abilityWrapped = this.nodes.stream().filter(node -> node.getGroup().getParentName().equals(name)).findFirst();
      if (!abilityWrapped.isPresent()) {
         AbilityNode<?> abilityNode = new AbilityNode<>(ModConfigs.ABILITIES.getByName(name), 0);
         this.nodes.add(abilityNode);
         return abilityNode;
      } else {
         return abilityWrapped.get();
      }
   }

   public boolean isActive() {
      return this.active;
   }

   public void setSwappingLocked(boolean swappingLocked) {
      this.swappingLocked = swappingLocked;
   }

   public AbilityTree scrollUp(MinecraftServer server) {
      List<AbilityNode<?>> learnedNodes = this.learnedNodes();
      if (this.swappingLocked) {
         return this;
      } else {
         if (learnedNodes.size() != 0) {
            boolean prevActive = this.active;
            this.active = false;
            AbilityNode<?> previouslyFocused = this.getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               previouslyFocused.getAbility().onBlur(player);
               if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
                  previouslyFocused.getAbility().onAction(player, this.active);
               }

               if (prevActive && this.getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
                  this.putOnCooldown(server, this.focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(this.getFocusedAbility(), player));
               }
            });
            this.focusedAbilityIndex++;
            if (this.focusedAbilityIndex >= learnedNodes.size()) {
               this.focusedAbilityIndex = this.focusedAbilityIndex - learnedNodes.size();
            }

            AbilityNode<?> newFocused = this.getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> newFocused.getAbility().onFocus(player));
            this.swappingPerformed = true;
            this.syncFocusedIndex(server);
            this.notifyActivity(server);
         }

         return this;
      }
   }

   public AbilityTree scrollDown(MinecraftServer server) {
      List<AbilityNode<?>> learnedNodes = this.learnedNodes();
      if (this.swappingLocked) {
         return this;
      } else {
         if (learnedNodes.size() != 0) {
            boolean prevActive = this.active;
            this.active = false;
            AbilityNode<?> previouslyFocused = this.getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> {
               previouslyFocused.getAbility().onBlur(player);
               if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
                  previouslyFocused.getAbility().onAction(player, this.active);
               }

               if (prevActive && this.getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
                  this.putOnCooldown(server, this.focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(this.getFocusedAbility(), player));
               }
            });
            this.focusedAbilityIndex--;
            if (this.focusedAbilityIndex < 0) {
               this.focusedAbilityIndex = this.focusedAbilityIndex + learnedNodes.size();
            }

            AbilityNode<?> newFocused = this.getFocusedAbility();
            NetcodeUtils.runIfPresent(server, this.uuid, player -> newFocused.getAbility().onFocus(player));
            this.swappingPerformed = true;
            this.syncFocusedIndex(server);
            this.notifyActivity(server);
         }

         return this;
      }
   }

   public void keyDown(MinecraftServer server) {
      AbilityNode<?> focusedAbility = this.getFocusedAbility();
      if (focusedAbility != null) {
         PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();
         if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
            this.active = true;
            NetcodeUtils.runIfPresent(server, this.uuid, player -> focusedAbility.getAbility().onAction(player, this.active));
            this.notifyActivity(server, this.focusedAbilityIndex, 0, this.active);
         }
      }
   }

   public void keyUp(MinecraftServer server) {
      AbilityNode<?> focusedAbility = this.getFocusedAbility();
      this.swappingLocked = false;
      if (focusedAbility != null) {
         if (this.swappingPerformed) {
            this.swappingPerformed = false;
         } else if (this.cooldowns.getOrDefault(this.focusedAbilityIndex, 0) <= 0) {
            PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();
            if (behavior == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
               this.active = !this.active;
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  focusedAbility.getAbility().onAction(player, this.active);
                  this.putOnCooldown(server, this.focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(this.getFocusedAbility(), player));
               });
            } else if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
               this.active = false;
               NetcodeUtils.runIfPresent(server, this.uuid, player -> focusedAbility.getAbility().onAction(player, this.active));
               this.notifyActivity(server);
            } else if (behavior == PlayerAbility.Behavior.RELEASE_TO_PERFORM) {
               NetcodeUtils.runIfPresent(server, this.uuid, player -> {
                  focusedAbility.getAbility().onAction(player, this.active);
                  this.putOnCooldown(server, this.focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(this.getFocusedAbility(), player));
               });
            }
         }
      }
   }

   public void quickSelectAbility(MinecraftServer server, int abilityIndex) {
      List<AbilityNode<?>> learnedNodes = this.learnedNodes();
      if (learnedNodes.size() != 0) {
         boolean prevActive = this.active;
         this.active = false;
         AbilityNode<?> previouslyFocused = this.getFocusedAbility();
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            previouslyFocused.getAbility().onBlur(player);
            if (prevActive && previouslyFocused.getAbility().getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
               previouslyFocused.getAbility().onAction(player, this.active);
            }

            if (prevActive && this.getFocusedAbility().getAbility().getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
               this.putOnCooldown(server, this.focusedAbilityIndex, ModConfigs.ABILITIES.cooldownOf(this.getFocusedAbility(), player));
            }
         });
         this.focusedAbilityIndex = abilityIndex;
         AbilityNode<?> newFocused = this.getFocusedAbility();
         NetcodeUtils.runIfPresent(server, this.uuid, player -> newFocused.getAbility().onFocus(player));
         this.syncFocusedIndex(server);
      }
   }

   public void cancelKeyDown(MinecraftServer server) {
      AbilityNode<?> focusedAbility = this.getFocusedAbility();
      if (focusedAbility != null) {
         PlayerAbility.Behavior behavior = focusedAbility.getAbility().getBehavior();
         if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
            this.active = false;
            this.swappingLocked = false;
            this.swappingPerformed = false;
         }

         this.notifyActivity(server);
      }
   }

   public void putOnCooldown(MinecraftServer server, int abilityIndex, int cooldownTicks) {
      this.cooldowns.put(abilityIndex, cooldownTicks);
      this.notifyActivity(server, abilityIndex, cooldownTicks, 0);
   }

   public AbilityTree upgradeAbility(MinecraftServer server, AbilityNode<?> abilityNode) {
      this.remove(server, abilityNode);
      AbilityGroup<?> abilityGroup = ModConfigs.ABILITIES.getByName(abilityNode.getGroup().getParentName());
      AbilityNode<?> upgradedAbilityNode = new AbilityNode<>(abilityGroup, abilityNode.getLevel() + 1);
      this.add(server, upgradedAbilityNode);
      return this;
   }

   public AbilityTree add(MinecraftServer server, AbilityNode<?>... nodes) {
      for (AbilityNode<?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.getAbility().onAdded(player);
            }
         });
         this.nodes.add(node);
      }

      this.focusedAbilityIndex = MathHelper.func_76125_a(this.focusedAbilityIndex, 0, this.learnedNodes().size() - 1);
      return this;
   }

   public AbilityTree remove(MinecraftServer server, AbilityNode<?>... nodes) {
      List<AbilityNode<?>> learnedNodes = this.learnedNodes();

      for (int i = 0; i < learnedNodes.size(); i++) {
         this.putOnCooldown(server, i, 0);
      }

      for (AbilityNode<?> node : nodes) {
         NetcodeUtils.runIfPresent(server, this.uuid, player -> {
            if (node.isLearned()) {
               node.getAbility().onRemoved(player);
            }
         });
         this.nodes.remove(node);
      }

      this.focusedAbilityIndex = MathHelper.func_76125_a(this.focusedAbilityIndex, 0, this.learnedNodes().size() - 1);
      return this;
   }

   public void tick(PlayerTickEvent event) {
      AbilityNode<?> focusedAbility = this.getFocusedAbility();
      if (focusedAbility != null) {
         focusedAbility.getAbility().onTick(event.player, this.isActive());
      }

      for (Integer abilityIndex : this.cooldowns.keySet()) {
         this.cooldowns.computeIfPresent(abilityIndex, (index, cooldown) -> cooldown - 1);
         this.notifyCooldown(event.player.func_184102_h(), abilityIndex, this.cooldowns.getOrDefault(abilityIndex, 0));
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
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL
            .sendTo(new AbilityFocusMessage(this.focusedAbilityIndex), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public void notifyActivity(MinecraftServer server) {
      this.notifyActivity(server, this.focusedAbilityIndex, this.cooldowns.getOrDefault(this.focusedAbilityIndex, 0), this.active);
   }

   public void notifyCooldown(MinecraftServer server, int abilityIndex, int cooldown) {
      this.notifyActivity(server, abilityIndex, cooldown, 0);
   }

   public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, boolean active) {
      this.notifyActivity(server, abilityIndex, cooldown, active ? 2 : 1);
   }

   public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, int activeFlag) {
      NetcodeUtils.runIfPresent(
         server,
         this.uuid,
         player -> ModNetwork.CHANNEL
            .sendTo(new AbilityActivityMessage(abilityIndex, cooldown, activeFlag), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT)
      );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT list = new ListNBT();
      this.nodes.stream().map(AbilityNode::serializeNBT).forEach(list::add);
      nbt.func_218657_a("Nodes", list);
      nbt.func_74768_a("FocusedIndex", this.focusedAbilityIndex);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      ListNBT list = nbt.func_150295_c("Nodes", 10);
      this.nodes.clear();

      for (int i = 0; i < list.size(); i++) {
         this.add(null, AbilityNode.fromNBT(list.func_150305_b(i), PlayerAbility.class));
      }

      this.focusedAbilityIndex = MathHelper.func_76125_a(nbt.func_74762_e("FocusedIndex"), 0, this.learnedNodes().size() - 1);
   }
}
