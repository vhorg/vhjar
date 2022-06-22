package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.logic.objective.architect.modifier.FinalKnowledgeModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.FinalMobHealthModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.FinalVaultModifierModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.FinalVaultTimeModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class FinalArchitectEventConfig extends Config {
   @Expose
   private List<FinalKnowledgeModifier> KNOWLEDGE_MODIFIERS;
   @Expose
   private List<FinalMobHealthModifier> MOB_HEALTH_MODIFIERS;
   @Expose
   private List<FinalVaultModifierModifier> VAULT_MODIFIER_MODIFIERS;
   @Expose
   private List<FinalVaultTimeModifier> VAULT_TIME_MODIFIERS;
   @Expose
   private int bossKillsNeeded;
   @Expose
   private int totalKnowledgeNeeded;
   @Expose
   private WeightedList<FinalArchitectEventConfig.ModifierPair> pairs;

   @Override
   public String getName() {
      return "final_architect_event";
   }

   public List<VoteModifier> getAll() {
      return Stream.of(this.KNOWLEDGE_MODIFIERS, this.MOB_HEALTH_MODIFIERS, this.VAULT_MODIFIER_MODIFIERS, this.VAULT_TIME_MODIFIERS)
         .flatMap(Collection::stream)
         .collect(Collectors.toList());
   }

   @Nullable
   public VoteModifier getModifier(String modifierName) {
      return modifierName == null
         ? null
         : this.getAll().stream().filter(modifier -> modifierName.equalsIgnoreCase(modifier.getName())).findFirst().orElse(null);
   }

   public FinalArchitectEventConfig.ModifierPair getRandomPair() {
      return this.pairs.getRandom(rand);
   }

   public int getBossKillsNeeded() {
      return this.bossKillsNeeded;
   }

   public int getTotalKnowledgeNeeded() {
      return this.totalKnowledgeNeeded;
   }

   @Override
   protected void reset() {
      this.KNOWLEDGE_MODIFIERS = Arrays.asList(
         new FinalKnowledgeModifier("Knowledge1", "+3 Knowledge", 3), new FinalKnowledgeModifier("Knowledge2", "+5 Knowledge", 5)
      );
      this.MOB_HEALTH_MODIFIERS = Arrays.asList(
         new FinalMobHealthModifier("MobHealth1", "+10% Mob Health", 0.1F), new FinalMobHealthModifier("MobHealth2", "+20% Mob Health", 0.2F)
      );
      this.VAULT_MODIFIER_MODIFIERS = Arrays.asList(new FinalVaultModifierModifier("AddCrowded", "Add Crowded", "Crowded"));
      this.VAULT_TIME_MODIFIERS = Arrays.asList(
         new FinalVaultTimeModifier("AddMinute", "Adds 1 Minute", 60), new FinalVaultTimeModifier("RemoveMinute", "Removes 1 Minute", -60)
      );
      this.bossKillsNeeded = 10;
      this.totalKnowledgeNeeded = 20;
      this.pairs = new WeightedList<>();
      this.pairs.add(new FinalArchitectEventConfig.ModifierPair("Knowledge1", "AddCrowded"), 5);
      this.pairs.add(new FinalArchitectEventConfig.ModifierPair("Knowledge2", "RemoveMinute"), 10);
   }

   public static class ModifierPair {
      @Expose
      private String positive;
      @Expose
      private String negative;

      public ModifierPair(String positive, String negative) {
         this.positive = positive;
         this.negative = negative;
      }

      public String getPositive() {
         return this.positive;
      }

      public String getNegative() {
         return this.negative;
      }
   }
}
