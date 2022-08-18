package iskallia.vault.init;

import iskallia.vault.mixin.MixinBooleanValue;
import iskallia.vault.mixin.MixinIntegerValue;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.GameRules.IntegerValue;
import net.minecraft.world.GameRules.RuleKey;
import net.minecraft.world.GameRules.RuleType;
import net.minecraft.world.GameRules.RuleValue;

public class ModGameRules {
   public static RuleKey<BooleanValue> FINAL_VAULT_ALLOW_PARTY;
   public static RuleKey<IntegerValue> EXP_MULTIPLIER;

   public static void initialize() {
      FINAL_VAULT_ALLOW_PARTY = register("finalVaultAllowParty", Category.MISC, booleanRule(false));
      EXP_MULTIPLIER = register("vaultExpMultiplier", Category.MISC, integerRule(1));
   }

   public static <T extends RuleValue<T>> RuleKey<T> register(String name, Category category, RuleType<T> type) {
      return GameRules.func_234903_a_(name, category, type);
   }

   public static RuleType<BooleanValue> booleanRule(boolean defaultValue) {
      return MixinBooleanValue.create(defaultValue);
   }

   public static RuleType<IntegerValue> integerRule(int defaultValue) {
      return MixinIntegerValue.create(defaultValue);
   }
}
