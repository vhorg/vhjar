package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.effects.AttributeTrinket;
import iskallia.vault.gear.trinket.effects.DamageImmunityTrinket;
import iskallia.vault.gear.trinket.effects.ExplosionBlockPreventionTrinket;
import iskallia.vault.gear.trinket.effects.InstantItemUseTrinket;
import iskallia.vault.gear.trinket.effects.MultiJumpTrinket;
import iskallia.vault.gear.trinket.effects.PotionEffectTrinket;
import iskallia.vault.gear.trinket.effects.VanillaAttributeTrinket;
import iskallia.vault.gear.trinket.effects.VaultExperienceTrinket;
import iskallia.vault.gear.trinket.effects.VaultTimeExtensionTrinket;
import iskallia.vault.gear.trinket.effects.WallClimbingTrinket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModTrinkets {
   public static final AttributeTrinket<Float> CHROMATIC_POWDER = new AttributeTrinket<>(
      VaultMod.id("chromatic_powder"), ModGearAttributes.DURABILITY_WEAR_REDUCTION, 0.25F
   );
   public static final AttributeTrinket<Float> IDONAS_BLADE = new AttributeTrinket<>(VaultMod.id("idonas_blade"), ModGearAttributes.SOUL_CHANCE, 1.0F);
   public static final AttributeTrinket<Float> TENOS_CHARM = new AttributeTrinket<>(VaultMod.id("tenos_charm"), ModGearAttributes.ITEM_QUANTITY, 0.25F);
   public static final AttributeTrinket<Float> LUCKY_GOOSE = new AttributeTrinket<>(VaultMod.id("lucky_goose"), ModGearAttributes.ITEM_RARITY, 0.5F);
   public static final VanillaAttributeTrinket PHYLACTERY = new VanillaAttributeTrinket(
      VaultMod.id("phylactery"), ModAttributes.MANA_REGEN, 1.0, Operation.MULTIPLY_TOTAL
   );
   public static final VanillaAttributeTrinket CRYSTAL_BALL = new VanillaAttributeTrinket(
      VaultMod.id("crystal_ball"), ModAttributes.MANA_MAX, 0.5, Operation.MULTIPLY_TOTAL
   );
   public static final VanillaAttributeTrinket GIANTS_HEART = new VanillaAttributeTrinket(
      VaultMod.id("giants_heart"), Attributes.MAX_HEALTH, 0.25, Operation.MULTIPLY_TOTAL
   );
   public static final VanillaAttributeTrinket HIGH_HEELS = new VanillaAttributeTrinket(
      VaultMod.id("high_heels"), (Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0, Operation.ADDITION
   );
   public static final DamageImmunityTrinket ELVEN_SOCKS = new DamageImmunityTrinket(VaultMod.id("elven_socks"), DamageSource::isFall, false);
   public static final DamageImmunityTrinket CARAPACE = new DamageImmunityTrinket(VaultMod.id("carapace"), DamageSource::isFire, true);
   public static final PotionEffectTrinket VELARAS_PETAL = new PotionEffectTrinket(VaultMod.id("velaras_petal"), MobEffects.REGENERATION, 1);
   public static final PotionEffectTrinket NIGHT_VISION_GOGGLES = new PotionEffectTrinket(VaultMod.id("night_vision_goggles"), MobEffects.NIGHT_VISION, 1);
   public static final VaultTimeExtensionTrinket WENDARRS_HOURGLASS = new VaultTimeExtensionTrinket(VaultMod.id("wendarrs_hourglass"), 3000);
   public static final ExplosionBlockPreventionTrinket CAT_IN_A_JAR = new ExplosionBlockPreventionTrinket(VaultMod.id("cat_in_a_jar"));
   public static final WallClimbingTrinket CENTIPEDE_FEET = new WallClimbingTrinket(VaultMod.id("centipede_feet"));
   public static final MultiJumpTrinket CHROMATIC_FEATHER = new MultiJumpTrinket(VaultMod.id("chromatic_feather"));
   public static final InstantItemUseTrinket GLUTTONY_PENDANT = new InstantItemUseTrinket(VaultMod.id("gluttony_pendant"));
   public static final VaultExperienceTrinket GOLDEN_BURGER = new VaultExperienceTrinket(VaultMod.id("golden_burger"));

   public static void init(Register<TrinketEffect<?>> event) {
      IForgeRegistry<TrinketEffect<?>> registry = event.getRegistry();
      registry.register(CHROMATIC_POWDER);
      registry.register(IDONAS_BLADE);
      registry.register(TENOS_CHARM);
      registry.register(LUCKY_GOOSE);
      registry.register(PHYLACTERY);
      registry.register(CRYSTAL_BALL);
      registry.register(GIANTS_HEART);
      registry.register(HIGH_HEELS);
      registry.register(ELVEN_SOCKS);
      registry.register(CARAPACE);
      registry.register(VELARAS_PETAL);
      registry.register(NIGHT_VISION_GOGGLES);
      registry.register(WENDARRS_HOURGLASS);
      registry.register(CAT_IN_A_JAR);
      registry.register(CENTIPEDE_FEET);
      registry.register(CHROMATIC_FEATHER);
      registry.register(GLUTTONY_PENDANT);
      registry.register(GOLDEN_BURGER);
   }
}
