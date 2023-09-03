package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.ability.effect.spi.core.Cooldown;
import iskallia.vault.skill.ability.effect.spi.core.CooldownSkill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class TieredSkill extends LearnableSkill implements TickingSkill, CooldownSkill {
   private List<LearnableSkill> tiers;
   private int maxLearnableTier;
   private int tier;
   private int bonusTier;
   private static final ArrayAdapter<Skill> TIERS = Adapters.ofArray(Skill[]::new, Adapters.SKILL);

   public TieredSkill(int unlockLevel, int learnPointCost, int regretPointCost, Stream<LearnableSkill> tiers) {
      super(unlockLevel, learnPointCost, regretPointCost);
      this.tiers = tiers.toList();
      this.maxLearnableTier = this.tiers.size();
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   public TieredSkill() {
   }

   public int getUnmodifiedTier() {
      return this.tier;
   }

   public int getActualTier() {
      return this.tier + this.bonusTier;
   }

   public int getMaxLearnableTier() {
      return this.maxLearnableTier;
   }

   public LearnableSkill getChild() {
      return this.getChild(this.getActualTier());
   }

   public LearnableSkill getChild(int tier) {
      return tier <= 0 ? null : MiscUtils.getListEntrySafe(this.tiers, tier - 1);
   }

   @Override
   public void onTick(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(player -> this.updateBonusTier(AttributeSnapshotHelper.getInstance().getSnapshot(player), context.copy()));
   }

   public void updateBonusTier(AttributeSnapshot snapshot, SkillContext context) {
      int additional = snapshot.getAttributeValueList(ModGearAttributes.ABILITY_LEVEL).stream().filter(attribute -> {
         if (attribute.getAbility().equals("all_abilities") && this.hasParentOfType(AbilityTree.class)) {
            return true;
         } else {
            Skill current = this;

            while (!attribute.getAbility().equals(current.getId())) {
               current = current.getParent();
               if (current == null) {
                  return false;
               }
            }

            return true;
         }
      }).mapToInt(AbilityLevelAttribute::getLevelChange).sum();
      this.updateBonusTier(additional, context);
   }

   protected void updateBonusTier(int bonusTier, SkillContext context) {
      if (this.bonusTier != bonusTier) {
         if (this.tier > 0 && this.tier + this.bonusTier > 0) {
            this.regretCurrentTier(context);
         }

         this.bonusTier = bonusTier;
         if (this.tier > 0 && this.tier + this.bonusTier > 0) {
            this.learnCurrentTier(context);
         }
      }
   }

   @Override
   public int getUnlockLevel() {
      return this.tier >= this.tiers.size() ? 0 : this.tiers.get(this.tier).getUnlockLevel();
   }

   @Override
   public int getLearnPointCost() {
      return this.tier >= this.tiers.size() ? 0 : this.tiers.get(this.tier).getLearnPointCost();
   }

   @Override
   public int getSpentLearnPoints() {
      return this.getSpentLearnPoints(this.tier);
   }

   public int getSpentLearnPoints(int tier) {
      int points = 0;

      for (int i = 0; i < tier; i++) {
         points += this.tiers.get(i).getLearnPointCost();
      }

      return points;
   }

   @Override
   public int getRegretPointCost() {
      return this.tier <= 0 ? 0 : this.tiers.get(this.tier - 1).getRegretPointCost();
   }

   @Override
   public boolean isUnlocked() {
      return this.getUnmodifiedTier() > 0;
   }

   @Override
   public boolean canLearn(SkillContext context) {
      return this.tier < this.maxLearnableTier && this.tiers.get(this.tier).canLearn(context);
   }

   @Override
   public void learn(SkillContext context) {
      if (this.tier > 0) {
         this.regretCurrentTier(context.copy());
      }

      if (this.tier >= this.maxLearnableTier) {
         throw new IllegalStateException();
      } else {
         this.tier++;
         this.learnCurrentTier(context);
      }
   }

   @Override
   public boolean canRegret(SkillContext context) {
      return this.tier > 0 && this.tiers.get(this.tier - 1).canRegret(context);
   }

   @Override
   public void regret(SkillContext context) {
      this.regretCurrentTier(context);
      if (this.tier <= 0) {
         throw new IllegalStateException();
      } else {
         this.tier--;
         if (this.tier > 0) {
            this.learnCurrentTier(context.copy());
         }
      }
   }

   private void regretCurrentTier(SkillContext context) {
      MiscUtils.getListEntrySafe(this.tiers, this.tier + this.bonusTier - 1).regret(context);
   }

   private void learnCurrentTier(SkillContext context) {
      MiscUtils.getListEntrySafe(this.tiers, this.tier + this.bonusTier - 1).learn(context);
   }

   @Override
   public Optional<Skill> getForId(String id) {
      return super.getForId(id).or(() -> {
         for (Skill child : this.tiers) {
            Skill skill = child.getForId(id).orElse(null);
            if (skill != null) {
               return Optional.of(skill);
            }
         }

         return Optional.empty();
      });
   }

   public int getTierOf(String id) {
      for (int i = 0; i < this.tiers.size(); i++) {
         if (this.tiers.get(i).getId().equals(id)) {
            return i + 1;
         }
      }

      return 0;
   }

   @Override
   public <T> void iterate(Class<T> type, Consumer<T> action) {
      super.iterate(type, action);

      for (LearnableSkill child : this.tiers) {
         child.iterate(type, action);
      }
   }

   @Override
   public Skill mergeFrom(Skill other, SkillContext context) {
      other = super.mergeFrom(other, context);
      if (!(other instanceof TieredSkill tiered)) {
         context.setLearnPoints(context.getLearnPoints() + this.getSpentLearnPoints());
         return other;
      } else {
         int currentCost = this.getSpentLearnPoints();
         int newCost = tiered.getSpentLearnPoints(this.tier);
         if (currentCost < newCost) {
            context.setLearnPoints(context.getLearnPoints() + this.getSpentLearnPoints());
            return other;
         } else {
            context.setLearnPoints(context.getLearnPoints() + currentCost - newCost);
            List<LearnableSkill> copy = new ArrayList<>();

            for (int i = 0; i < tiered.tiers.size(); i++) {
               Skill merging = i >= this.tiers.size() ? null : this.tiers.get(i);
               Skill merged;
               if (merging != null) {
                  merged = merging.mergeFrom(tiered.tiers.get(i), context);
               } else {
                  merged = tiered.tiers.get(i).copy();
               }

               if (merged instanceof LearnableSkill) {
                  merged.setParent(this);
                  copy.add((LearnableSkill)merged);
               }
            }

            this.tier = this.tier > copy.size() ? 0 : this.tier;
            this.tiers = copy;
            this.maxLearnableTier = tiered.getMaxLearnableTier();

            while (this.tier > this.maxLearnableTier) {
               this.regret(context);
            }

            return this;
         }
      }
   }

   @Override
   public Optional<Cooldown> getCooldown() {
      Cooldown max = null;

      for (LearnableSkill child : this.tiers) {
         if (child instanceof CooldownSkill) {
            Cooldown cooldown = ((CooldownSkill)child).getCooldown().orElse(null);
            if (cooldown != null && (max == null || cooldown.isLargerThan(max))) {
               max = cooldown;
            }
         }
      }

      return Optional.ofNullable(max);
   }

   @Override
   public void putOnCooldown(int cooldownDelayTicks, SkillContext context) {
      LearnableSkill child = this.getChild();
      if (child instanceof CooldownSkill) {
         ((CooldownSkill)child).putOnCooldown(cooldownDelayTicks, context);
      }
   }

   @Override
   public <T extends Skill> T copy() {
      TieredSkill copy = new TieredSkill(this.unlockLevel, this.learnPointCost, this.regretPointCost, this.tiers.stream().map(Skill::copy));
      copy.parent = this.parent;
      copy.id = this.id;
      copy.name = this.name;
      copy.present = this.present;
      copy.learnPointCost = this.learnPointCost;
      copy.regretPointCost = this.regretPointCost;
      copy.unlockLevel = this.unlockLevel;
      copy.tier = this.tier;
      copy.bonusTier = this.bonusTier;
      copy.maxLearnableTier = this.maxLearnableTier;
      return (T)copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      TIERS.writeBits(this.tiers.toArray(Skill[]::new), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.maxLearnableTier), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.tier), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.bonusTier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.tiers = Arrays.stream(TIERS.readBits(buffer).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.maxLearnableTier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.tier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.bonusTier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         TIERS.writeNbt(this.tiers.toArray(Skill[]::new)).ifPresent(tag -> nbt.put("tiers", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxLearnableTier)).ifPresent(tag -> nbt.put("maxLearnableTier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.tier)).ifPresent(tag -> nbt.put("tier", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.bonusTier)).ifPresent(tag -> nbt.put("bonusTier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.tiers = Arrays.stream(TIERS.readNbt(nbt.get("tiers")).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.maxLearnableTier = Adapters.INT.readNbt(nbt.get("maxLearnableTier")).orElse(this.tiers.size());
      this.tier = Adapters.INT.readNbt(nbt.get("tier")).orElse(0);
      this.bonusTier = Adapters.INT.readNbt(nbt.get("bonusTier")).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         TIERS.writeJson(this.tiers.toArray(Skill[]::new)).ifPresent(element -> json.add("tiers", element));
         Adapters.INT.writeJson(Integer.valueOf(this.maxLearnableTier)).ifPresent(element -> json.add("maxLearnableTier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.tier)).ifPresent(element -> json.add("tier", element));
         Adapters.INT.writeJson(Integer.valueOf(this.bonusTier)).ifPresent(element -> json.add("bonusTier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.tiers = Arrays.stream(TIERS.readJson(json.get("tiers")).orElseThrow()).map(skill -> (LearnableSkill)skill).toList();
      this.maxLearnableTier = Adapters.INT.readJson(json.get("maxLearnableTier")).orElse(this.tiers.size());
      this.tier = Adapters.INT.readJson(json.get("tier")).orElse(0);
      this.bonusTier = Adapters.INT.readJson(json.get("bonusTier")).orElse(0);
      this.tiers.forEach(tier -> tier.setParent(this));
   }
}
