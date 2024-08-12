package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.antique.condition.AntiqueCondition;
import iskallia.vault.antique.condition.AntiqueConditionAnd;
import iskallia.vault.antique.condition.AntiqueConditionLevel;
import iskallia.vault.antique.condition.AntiqueConditionOr;
import iskallia.vault.antique.condition.AntiqueConditionTag;
import iskallia.vault.antique.condition.AntiqueConditionType;
import iskallia.vault.antique.condition.DropConditionType;
import iskallia.vault.antique.reward.AntiqueReward;
import iskallia.vault.antique.reward.AntiqueRewardSpecificGear;
import iskallia.vault.init.ModAntiques;
import iskallia.vault.init.ModItems;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AntiquesConfig extends Config {
   @Expose
   private final Map<ResourceLocation, AntiquesConfig.Entry> antiques = new HashMap<>();

   @Override
   public String getName() {
      return "antiques";
   }

   @Nullable
   public AntiquesConfig.Entry getAntiqueConfig(ResourceLocation id) {
      return this.antiques.get(id);
   }

   @Override
   protected void reset() {
      this.antiques.clear();
      AntiqueRegistry.sorted().forEach(antique -> {
         AntiquesConfig.Entry entry = new AntiquesConfig.Entry();
         entry.info = new AntiquesConfig.Info();
         entry.info.name = antique.getRegistryName().getPath();
         entry.info.subtext = entry.info.name + "_subtext";
         entry.info.rewardDescription = entry.info.name + "_reward";
         entry.reward = this.makeExemplaryReward();
         entry.condition = ModAntiques.Conditions.OR.provideCondition().cast();
         this.antiques.put(antique.getRegistryName(), entry);
      });
   }

   private AntiqueCondition makeExemplaryCondition() {
      AntiqueConditionOr mainOr = ModAntiques.Conditions.OR.provideCondition().cast();
      AntiqueConditionAnd desertCondition = ModAntiques.Conditions.AND.provideCondition().cast();
      AntiqueConditionTag desertTag = ModAntiques.Conditions.TAG.provideCondition().cast();
      desertTag.addTag("THEME_DESERT", "THEME_SANDY");
      AntiqueConditionType desertChestTag = ModAntiques.Conditions.TYPE.provideCondition().cast();
      desertChestTag.addType(DropConditionType.BLOCK);
      AntiqueConditionLevel desertLevel = ModAntiques.Conditions.LEVEL.provideCondition().cast();
      desertLevel.setMinLevel(10);
      desertLevel.setMaxLevel(50);
      desertCondition.addCondition(desertTag);
      desertCondition.addCondition(desertChestTag);
      desertCondition.addCondition(desertLevel);
      mainOr.addCondition(desertCondition);
      AntiqueConditionAnd forestCondition = ModAntiques.Conditions.AND.provideCondition().cast();
      AntiqueConditionTag forestTag = ModAntiques.Conditions.TAG.provideCondition().cast();
      forestTag.addTag("THEME_FOREST");
      AntiqueConditionType forestRewardTag = ModAntiques.Conditions.TYPE.provideCondition().cast();
      forestRewardTag.addType(DropConditionType.ENTITY, DropConditionType.REWARD_CRATE);
      AntiqueConditionLevel forestLevel = ModAntiques.Conditions.LEVEL.provideCondition().cast();
      forestLevel.setMinLevel(75);
      forestCondition.addCondition(forestTag);
      forestCondition.addCondition(forestRewardTag);
      forestCondition.addCondition(forestLevel);
      mainOr.addCondition(forestCondition);
      return mainOr;
   }

   private AntiqueReward makeExemplaryReward() {
      AntiqueRewardSpecificGear gearList = ModAntiques.Rewards.SPECIFIC_GEAR.provideReward().cast();
      gearList.addResult(new ItemStack(ModItems.HELMET), 1.0);
      gearList.setGuaranteedModifier("the_vault:base_lucky_hit_chance");
      gearList.setOperation(AntiqueRewardSpecificGear.ModifierOperation.HIGHEST_TIER);
      gearList.setReferenceType(AntiqueRewardSpecificGear.ModifierReferenceType.IDENTIFIER);
      return gearList;
   }

   public static class Entry {
      @Expose
      protected AntiquesConfig.Info info;
      @Expose
      protected AntiqueReward reward;
      @Expose
      protected AntiqueCondition condition;

      public AntiquesConfig.Info getInfo() {
         return this.info;
      }

      public AntiqueReward getReward() {
         return this.reward;
      }

      public AntiqueCondition getCondition() {
         return this.condition;
      }
   }

   public static class Info {
      @Expose
      protected String name;
      @Expose
      protected String subtext;
      @Expose
      protected String rewardDescription;
      @Expose
      protected int requiredCount = 1;

      public String getName() {
         return this.name;
      }

      public String getSubtext() {
         return this.subtext;
      }

      public String getRewardDescription() {
         return this.rewardDescription;
      }

      public int getRequiredCount() {
         return this.requiredCount;
      }
   }
}
