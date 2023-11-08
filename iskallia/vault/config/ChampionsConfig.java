package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.entity.champion.LeechOnHitAffix;
import iskallia.vault.entity.champion.OnHitApplyPotionAffix;
import iskallia.vault.entity.champion.PotionAuraAffix;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ChampionsConfig extends Config {
   @Expose
   public float defaultChampionChance;
   @Expose
   public Map<EntityPredicate, Float> entityChampionChance = new HashMap<>();
   @Expose
   public List<ChampionsConfig.AttributeOverride> defaultAttributeOverrides = new ArrayList<>();
   @Expose
   public Map<EntityPredicate, List<ChampionsConfig.AttributeOverride>> entityAttributeOverrides = new LinkedHashMap<>();
   @Expose
   public Map<EntityPredicate, WeightedList<CompoundTag>> entityAffixesData = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "champions";
   }

   @Override
   protected void reset() {
      PartialEntity zombiePredicate;
      try {
         zombiePredicate = PartialEntity.parse("minecraft:zombie");
      } catch (Exception var4) {
         return;
      }

      this.defaultChampionChance = 0.01F;
      this.entityChampionChance.clear();
      this.entityChampionChance.put(zombiePredicate, 0.05F);
      List<ChampionsConfig.AttributeOverride> attributes = new ArrayList<>();
      attributes.add(new ChampionsConfig.AttributeOverride(Attributes.MAX_HEALTH.getRegistryName().toString(), 1.5, "multiply"));
      attributes.add(new ChampionsConfig.AttributeOverride(Attributes.ATTACK_DAMAGE.getRegistryName().toString(), 2.0, "multiply"));
      attributes.add(new ChampionsConfig.AttributeOverride(ModAttributes.CRIT_CHANCE.getRegistryName().toString(), 0.3, "set"));
      this.defaultAttributeOverrides.clear();
      this.defaultAttributeOverrides.addAll(attributes);
      this.entityAttributeOverrides.clear();
      this.entityAttributeOverrides.put(zombiePredicate, attributes);
      this.entityAffixesData.clear();
      WeightedList<CompoundTag> affixes = new WeightedList<>();
      affixes.add(new OnHitApplyPotionAffix("Poisonous", MobEffects.POISON, 60, 0, 0.1F).serialize(), 3);
      affixes.add(new OnHitApplyPotionAffix("Withering", MobEffects.WITHER, 40, 0, 0.05F).serialize(), 1);
      affixes.add(new OnHitApplyPotionAffix("Draining", MobEffects.HUNGER, 80, 1, 0.3F).serialize(), 2);
      affixes.add(new OnHitApplyPotionAffix("Freezing", ModEffects.FREEZE, 80, 1, 0.3F).serialize(), 2);
      affixes.add(new PotionAuraAffix("Elevating", MobEffects.LEVITATION, 20, 0, 10, PotionAuraAffix.Target.PLAYER).serialize(), 3);
      affixes.add(new PotionAuraAffix("Reinforcing", MobEffects.DAMAGE_RESISTANCE, 20, 0, 10, PotionAuraAffix.Target.MOB).serialize(), 3);
      affixes.add(new LeechOnHitAffix("Leeching", 0.5F).serialize(), 2);
      this.entityAffixesData.put(zombiePredicate, affixes);
   }

   public static class AttributeOverride {
      @Expose
      public String NAME;
      @Expose
      public double VALUE;
      @Expose
      public String OPERATOR;

      public AttributeOverride(String name, double value, String operator) {
         this.NAME = name;
         this.VALUE = value;
         this.OPERATOR = operator;
      }

      public void applyTo(AttributeInstance instance) {
         if (this.OPERATOR.equalsIgnoreCase("multiply")) {
            instance.setBaseValue(instance.getBaseValue() * this.VALUE);
         } else if (this.OPERATOR.equalsIgnoreCase("add")) {
            instance.setBaseValue(instance.getBaseValue() + this.VALUE);
         } else if (this.OPERATOR.equalsIgnoreCase("set")) {
            instance.setBaseValue(this.VALUE);
         }
      }
   }
}
