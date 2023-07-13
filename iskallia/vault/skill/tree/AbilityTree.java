package iskallia.vault.skill.tree;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.skill.ability.cooldown.AbilityCooldownManager;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.HoldAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbility;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class AbilityTree extends SkillTree {
   private SpecializedSkill selected;

   public SpecializedSkill getSelected() {
      return this.selected;
   }

   public Optional<Ability> getSelectedAbility(Player player) {
      return Optional.ofNullable(this.selected)
         .map(SpecializedSkill::getSpecialization)
         .filter(skill -> skill instanceof TieredSkill)
         .map(skill -> ((TieredSkill)skill).getChild())
         .filter(skill -> skill instanceof Ability)
         .map(skill -> (Ability)skill);
   }

   public void onKeyUp(SkillContext context, Player player) {
      this.getSelectedAbility(player).ifPresent(selected -> {
         if (AbilityCooldownManager.isOnCooldown(player, selected)) {
            player.level.playSound((Player)null, player, ModSounds.ABILITY_ON_COOLDOWN, SoundSource.PLAYERS, 1.0F, 1.0F);
         } else {
            selected.onKeyUp(context);
         }
      });
   }

   public void onKeyDown(SkillContext context, Player player) {
      this.getSelectedAbility(player).ifPresent(selected -> {
         if (!AbilityCooldownManager.isOnCooldown(player, selected)) {
            selected.onKeyDown(context);
         }
      });
   }

   public void onCancelKeyDown(SkillContext context, Player player) {
      this.getSelectedAbility(player).ifPresent(selected -> {
         if (!AbilityCooldownManager.isOnCooldown(player, selected)) {
            selected.onCancelKeyDown(context);
         }
      });
   }

   @Nonnull
   public List<TieredSkill> getLearnedAbilities() {
      List<TieredSkill> abilityList = new ArrayList<>();
      this.iterate(TieredSkill.class, ability -> {
         if (ability.isUnlocked() && ((SpecializedSkill)ability.getParent()).getSpecialization() == ability) {
            abilityList.add(ability);
         }
      });
      return abilityList;
   }

   public int getIndexOf(String ability) {
      List<TieredSkill> nodes = this.getLearnedAbilities();

      for (int i = 0; i < nodes.size(); i++) {
         TieredSkill node = nodes.get(i);
         if (node.getId().equals(ability)) {
            return i;
         }
      }

      return -1;
   }

   public void onScrollDown(SkillContext context, ServerPlayer player) {
      AbilityTree abilityTree = PlayerAbilitiesData.get(player.server).getAbilities(player);
      List<TieredSkill> abilities = this.getLearnedAbilities();
      Optional<Ability> abilityNode = this.getSelectedAbility(player);
      if (!abilityNode.isEmpty()) {
         int selectedAbilityIndex = this.getIndexOf(abilityNode.get().getParent().getId());
         if (selectedAbilityIndex != -1) {
            int nextIndex = selectedAbilityIndex + 1;
            if (nextIndex >= abilities.size()) {
               nextIndex -= abilities.size();
            }

            Ability nextAbilityNode = (Ability)abilities.get(nextIndex).getChild();
            if (nextAbilityNode != abilityNode.get()) {
               abilityTree.onQuickSelect(nextAbilityNode.getParent().getParent().getId(), context, player);
            }
         }
      }
   }

   public void onScrollUp(SkillContext context, ServerPlayer player) {
      AbilityTree abilityTree = PlayerAbilitiesData.get(player.server).getAbilities(player);
      List<TieredSkill> abilities = this.getLearnedAbilities();
      Optional<Ability> abilityNode = this.getSelectedAbility(player);
      if (!abilityNode.isEmpty()) {
         int selectedAbilityIndex = this.getIndexOf(abilityNode.get().getParent().getId());
         if (selectedAbilityIndex != -1) {
            int previousIndex = selectedAbilityIndex - 1;
            if (previousIndex < 0) {
               previousIndex += abilities.size();
            }

            Ability previousAbilityNode = (Ability)abilities.get(previousIndex).getChild();
            if (previousAbilityNode != abilityNode.get()) {
               abilityTree.onQuickSelect(previousAbilityNode.getParent().getParent().getId(), context, player);
            }
         }
      }
   }

   public void onQuickSelect(String id, SkillContext context, Player player) {
      this.getSelectedAbility(player).ifPresent(selected -> {
         boolean shouldCooldown;
         if (!(selected instanceof ToggleAbility) && selected.isActive()) {
            shouldCooldown = true;
            selected.setActive(false);
         } else {
            shouldCooldown = false;
         }

         selected.onBlur(context);
         if (shouldCooldown && !(selected instanceof HoldAbility)) {
            selected.putOnCooldown(context);
         }
      });
      this.getForId(id).ifPresent(skill -> {
         if (skill instanceof SpecializedSkill specialized) {
            this.selected = specialized;
            this.getSelectedAbility(player).ifPresent(ability -> ability.onFocus(context));
         }
      });
      this.sync(context);
   }

   public void specialize(String id, SkillContext context) {
      this.getForId(id).ifPresent(skill -> {
         if (skill.getParent() instanceof SpecializedSkill specialized) {
            specialized.specialize(id, context);
         }
      });
   }

   @Override
   public void onTick(SkillContext context) {
      super.onTick(context);
      if (context.getSource().as(Entity.class).filter(entity -> entity.level.getGameTime() % 20L == 0L).isPresent()) {
         this.sync(context);
      }

      if (this.selected == null) {
         for (Skill skill : this.skills) {
            if (skill instanceof SpecializedSkill specialized && specialized.isUnlocked()) {
               this.selected = specialized;
               break;
            }
         }
      }
   }

   public void sync(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(player -> ModNetwork.CHANNEL.sendTo(new AbilityKnownOnesMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      if (!(super.mergeFrom(other, context) instanceof AbilityTree tree)) {
         return this;
      } else {
         ArrayList copy = new ArrayList();
         HashSet removed = new HashSet<>(this.skills.stream().map(Skill::getId).filter(Objects::nonNull).toList());

         for (Skill skill : tree.skills) {
            removed.remove(skill.getId());
            Skill merging = this.getForId(skill.getId()).orElse(null);
            Skill merged;
            if (merging != null) {
               merged = merging.mergeFrom(skill, context);
            } else {
               merged = skill;
            }

            if (merged != null) {
               merged.setParent(this);
               copy.add(merged);
            }
         }

         this.skills = copy;
         if (this.selected != null) {
            this.selected = (SpecializedSkill)this.getForId(this.selected.getId()).filter(skill -> skill instanceof SpecializedSkill).orElse(null);
         }

         for (String id : removed) {
            this.getForId(id).ifPresent(skill -> {
               if (skill instanceof LearnableSkill learnable) {
                  context.setLearnPoints(context.getLearnPoints() + learnable.getLearnPointCost());
               }
            });
         }

         return this;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UTF_8.asNullable().writeBits(this.selected == null ? null : this.selected.getId(), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.selected = Adapters.UTF_8.asNullable().readBits(buffer).flatMap(this::getForId).filter(skill -> skill instanceof SpecializedSkill).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UTF_8.asNullable().writeNbt(this.selected == null ? null : this.selected.getId()).ifPresent(tag -> nbt.put("selected", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.selected = Adapters.UTF_8
         .asNullable()
         .readNbt(nbt.get("selected"))
         .flatMap(this::getForId)
         .filter(skill -> skill instanceof SpecializedSkill)
         .orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UTF_8.asNullable().writeJson(this.selected == null ? null : this.selected.getId()).ifPresent(element -> json.add("selected", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.selected = Adapters.UTF_8
         .asNullable()
         .readJson(json.get("selected"))
         .flatMap(this::getForId)
         .filter(skill -> skill instanceof SpecializedSkill)
         .orElse(null);
   }
}
