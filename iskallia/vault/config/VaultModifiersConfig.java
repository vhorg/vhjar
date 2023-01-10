package iskallia.vault.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.vault.modifier.modifier.ChanceArtifactModifier;
import iskallia.vault.world.vault.modifier.modifier.ChanceCatalystModifier;
import iskallia.vault.world.vault.modifier.modifier.ChanceChestTrapModifier;
import iskallia.vault.world.vault.modifier.modifier.ChanceSoulShardModifier;
import iskallia.vault.world.vault.modifier.modifier.DecoratorAddModifier;
import iskallia.vault.world.vault.modifier.modifier.EmptyModifier;
import iskallia.vault.world.vault.modifier.modifier.GameControlsModifier;
import iskallia.vault.world.vault.modifier.modifier.LootItemQuantityModifier;
import iskallia.vault.world.vault.modifier.modifier.LootItemRarityModifier;
import iskallia.vault.world.vault.modifier.modifier.MobAttributeModifier;
import iskallia.vault.world.vault.modifier.modifier.MobCurseOnHitModifier;
import iskallia.vault.world.vault.modifier.modifier.MobFrenzyModifier;
import iskallia.vault.world.vault.modifier.modifier.MobSpawnCountModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerAttributeModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerDurabilityDamageModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerEffectModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerNoExitModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerNoVaultFruitModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerStatModifier;
import iskallia.vault.world.vault.modifier.modifier.VaultLevelModifier;
import iskallia.vault.world.vault.modifier.modifier.VaultLootableWeightModifier;
import iskallia.vault.world.vault.modifier.modifier.VaultTimeModifier;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.registry.VaultModifierTypeRegistry;
import iskallia.vault.world.vault.modifier.spi.AbstractChanceModifier;
import iskallia.vault.world.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;

public class VaultModifiersConfig extends Config {
   public static final String KEY_MODIFIERS = "modifiers";
   @Expose
   @SerializedName("modifiers")
   private VaultModifiersConfig.ModifierTypeGroups modifierTypeGroups;

   @Override
   public String getName() {
      return "vault_modifiers";
   }

   @Override
   public <T extends Config> T readConfig() {
      VaultModifiersConfig config = super.readConfig();
      VaultModifierRegistry.clear();
      config.modifierTypeGroups
         .values()
         .stream()
         .flatMap(map -> map.entrySet().stream())
         .forEach(entry -> VaultModifierRegistry.register(entry.getKey(), entry.getValue()));
      VaultModifierRegistry.register(EmptyModifier.INSTANCE.getId(), EmptyModifier.INSTANCE);
      return (T)config;
   }

   @Override
   protected void reset() {
      this.modifierTypeGroups = new VaultModifiersConfig.ModifierTypeGroups();
      this.generateChanceArtifactModifiers();
      this.generateChanceCatalystModifiers();
      this.generateChanceChestTrapModifier();
      this.generateChanceSoulShardModifiers();
      this.generateDecoratorAddModifiers();
      this.generateLootItemQuantityModifiers();
      this.generateLootItemRarityModifiers();
      this.generateMobAttributeModifiers();
      this.generateMobCurseOnHitModifiers();
      this.generateMobFrenzyModifiers();
      this.generateMobSpawnCountModifiers();
      this.generatePlayerAttributeModifiers();
      this.generatePlayerDurabilityDamageModifiers();
      this.generatePlayerEffectModifier();
      this.generatePlayerInventoryRestoreModifiers();
      this.generatePlayerNoExitModifiers();
      this.generatePlayerNoVaultFruitModifiers();
      this.generatePlayerStatModifier();
      this.generateVaultLevelModifiers();
      this.generateVaultLootableWeightModifiers();
      this.generateVaultTimeModifiers();
      this.generateGameControlsModifiers();
   }

   private void generateGameControlsModifiers() {
      VaultModifierTypeRegistry.getIdFor(GameControlsModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new GameControlsModifier(
                     VaultMod.id("jupiter_gravity"),
                     new GameControlsModifier.Properties(true, true, false, false),
                     new VaultModifier.Display(
                        "Jupiter Gravity", TextColor.parseColor("#7738c9"), "No Jump", "No Jump", VaultMod.id("gui/modifiers/jupiter_gravity")
                     )
                  )
               )
               .put(
                  new GameControlsModifier(
                     VaultMod.id("crab_walk"),
                     new GameControlsModifier.Properties(false, false, true, false),
                     new VaultModifier.Display(
                        "Crab Walk",
                        TextColor.parseColor("#7738c9"),
                        "The only way is sideways",
                        "The only way is sideways",
                        VaultMod.id("gui/modifiers/crab_walk")
                     )
                  )
               )
               .put(
                  new GameControlsModifier(
                     VaultMod.id("confused"),
                     new GameControlsModifier.Properties(true, true, true, true),
                     new VaultModifier.Display(
                        "Confused", TextColor.parseColor("#7738c9"), "Left and Right swapped", "Left and Right swapped", VaultMod.id("gui/modifiers/confused")
                     )
                  )
               )
         );
   }

   private void generateLootItemRarityModifiers() {
      VaultModifierTypeRegistry.getIdFor(LootItemRarityModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new LootItemRarityModifier(
                     VaultMod.id("item_rarity"),
                     new LootItemRarityModifier.Properties(0.1, null),
                     new VaultModifier.Display(
                        "Item Rarity", TextColor.parseColor("#7738c9"), "+10% Item Rarity", "+%d%% Item Rarity", VaultMod.id("gui/modifiers/item_rarity")
                     )
                  )
               )
         );
   }

   private void generateLootItemQuantityModifiers() {
      VaultModifierTypeRegistry.getIdFor(LootItemQuantityModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new LootItemQuantityModifier(
                     VaultMod.id("item_quantity"),
                     new LootItemQuantityModifier.Properties(0.1, null),
                     new VaultModifier.Display(
                        "Item Quantity",
                        TextColor.parseColor("#38c9c0"),
                        "+10% Item Quantity",
                        "+%d%% Item Quantity",
                        VaultMod.id("gui/modifiers/item_quantity")
                     )
                  )
               )
         );
   }

   private void generateMobFrenzyModifiers() {
      VaultModifierTypeRegistry.getIdFor(MobFrenzyModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new MobFrenzyModifier(
                     VaultMod.id("frenzy"),
                     new MobFrenzyModifier.Properties(3.0F, 0.1F, 1.0F, null),
                     new VaultModifier.Display(
                        "Frenzy",
                        TextColor.parseColor("#FC7C5C"),
                        "+300% Mob Damage, +10% Mob Speed, Mob Health reduced to 1",
                        VaultMod.id("gui/modifiers/frenzy")
                     )
                  )
               )
         );
   }

   private void generatePlayerAttributeModifiers() {
      VaultModifierTypeRegistry.getIdFor(PlayerAttributeModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerAttributeModifier(
                     VaultMod.id("limited"),
                     new EntityAttributeModifier.Properties(EntityAttributeModifier.ModifierType.MAX_HEALTH_ADDITIVE, -2.0, null),
                     new VaultModifier.Display("Limited", TextColor.parseColor("#631f1f"), "-2 Max HP", "-%d Max HP", VaultMod.id("gui/modifiers/limited"))
                  )
               )
               .put(
                  new PlayerAttributeModifier(
                     VaultMod.id("draining"),
                     new EntityAttributeModifier.Properties(EntityAttributeModifier.ModifierType.MANA_REGEN_ADDITIVE_PERCENTILE, -0.2F, null),
                     new VaultModifier.Display(
                        "Draining", TextColor.parseColor("#7738c9"), "-20% Mana Regeneration", "-%d%% Mana Regeneration", VaultMod.id("gui/modifiers/draining")
                     )
                  )
               )
         );
   }

   private void generateChanceSoulShardModifiers() {
      VaultModifierTypeRegistry.getIdFor(ChanceSoulShardModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new ChanceSoulShardModifier(
                     VaultMod.id("soul_hunter"),
                     new AbstractChanceModifier.Properties(0.1F, null),
                     new VaultModifier.Display(
                        "Soul Hunter", TextColor.parseColor("#6410a1"), "+10% Soul Shards", "+%d%% Soul Shards", VaultMod.id("gui/modifiers/soul_hunter")
                     )
                  )
               )
         );
   }

   private void generateChanceCatalystModifiers() {
      VaultModifierTypeRegistry.getIdFor(ChanceCatalystModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new ChanceCatalystModifier(
                     VaultMod.id("prismatic"),
                     new AbstractChanceModifier.Properties(0.1F, null),
                     new VaultModifier.Display(
                        "Prismatic",
                        TextColor.parseColor("#FC00E3"),
                        "+10% Catalyst Fragments",
                        "+%d%% Catalyst Fragments",
                        VaultMod.id("gui/modifiers/more_catalyst")
                     )
                  )
               )
         );
   }

   private void generateVaultLootableWeightModifiers() {
      VaultModifierTypeRegistry.getIdFor(VaultLootableWeightModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new VaultLootableWeightModifier(
                     VaultMod.id("plentiful"),
                     new VaultLootableWeightModifier.Properties(PlaceholderBlock.Type.ORE, 2.0, null),
                     new VaultModifier.Display(
                        "Plentiful",
                        TextColor.parseColor("#FF85FF"),
                        "Multiplies Vault Ore generation by 2",
                        "Multiplies Vault Ore generation by %d",
                        VaultMod.id("gui/modifiers/plentiful")
                     )
                  )
               )
         );
   }

   private void generatePlayerStatModifier() {
      VaultModifierTypeRegistry.getIdFor(PlayerStatModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerStatModifier(
                     VaultMod.id("clumsy"),
                     new PlayerStatModifier.Properties(PlayerStat.BLOCK_CHANCE, -0.1F, null),
                     new VaultModifier.Display("Clumsy", TextColor.parseColor("#CB866D"), "-10% Parry", "-%d%% Parry", VaultMod.id("gui/modifiers/clumsy"))
                  )
               )
               .put(
                  new PlayerStatModifier(
                     VaultMod.id("vulnerable"),
                     new PlayerStatModifier.Properties(PlayerStat.RESISTANCE, -0.1F, null),
                     new VaultModifier.Display(
                        "Vulnerable", TextColor.parseColor("#CA9A5B"), "-10% Resistance", "-%d%% Resistance", VaultMod.id("gui/modifiers/vulnerable")
                     )
                  )
               )
               .put(
                  new PlayerStatModifier(
                     VaultMod.id("inert"),
                     new PlayerStatModifier.Properties(PlayerStat.COOLDOWN_REDUCTION, -0.1F, null),
                     new VaultModifier.Display(
                        "Inert", TextColor.parseColor("#6DACB5"), "-10% Cooldown Reduction", "-%d%% Cooldown Reduction", VaultMod.id("gui/modifiers/inert")
                     )
                  )
               )
         );
   }

   private void generateMobCurseOnHitModifiers() {
      VaultModifierTypeRegistry.getIdFor(MobCurseOnHitModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new MobCurseOnHitModifier(
                     VaultMod.id("poisonous"),
                     new MobCurseOnHitModifier.Properties(MobEffects.POISON, 3, 120, 0.1F),
                     new VaultModifier.Display(
                        "Poisonous",
                        TextColor.parseColor("#84BF17"),
                        "+10% increased chance mobs Poison on hit",
                        "+%d%% increased chance mobs Poison on hit",
                        VaultMod.id("gui/modifiers/hex_poison")
                     )
                  )
               )
               .put(
                  new MobCurseOnHitModifier(
                     VaultMod.id("wither"),
                     new MobCurseOnHitModifier.Properties(MobEffects.WITHER, 3, 120, 0.1F),
                     new VaultModifier.Display(
                        "Withering",
                        TextColor.parseColor("#5A5851"),
                        "+10% increased chance mobs Wither on hit",
                        "+%d%% increased chance mobs Wither on hit",
                        VaultMod.id("gui/modifiers/hex_wither")
                     )
                  )
               )
               .put(
                  new MobCurseOnHitModifier(
                     VaultMod.id("fatiguing"),
                     new MobCurseOnHitModifier.Properties(MobEffects.DIG_SLOWDOWN, 4, 200, 0.1F),
                     new VaultModifier.Display(
                        "Fatiguing",
                        TextColor.parseColor("#9B3E56"),
                        "+10% increased chance mobs Fatigue on hit",
                        "+%d%% increased chance mobs Fatigue on hit",
                        VaultMod.id("gui/modifiers/hex_chaining")
                     )
                  )
               )
               .put(
                  new MobCurseOnHitModifier(
                     VaultMod.id("freezing"),
                     new MobCurseOnHitModifier.Properties(MobEffects.MOVEMENT_SLOWDOWN, 5, 200, 0.1F),
                     new VaultModifier.Display(
                        "Freezing",
                        TextColor.parseColor("#2FFBF4"),
                        "+10% increased chance mobs Slow on hit",
                        "+%d%% increased chance mobs Slow on hit",
                        VaultMod.id("gui/modifiers/hex_chilling")
                     )
                  )
               )
         );
   }

   private void generatePlayerEffectModifier() {
      VaultModifierTypeRegistry.getIdFor(PlayerEffectModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("lucky"),
                     new PlayerEffectModifier.Properties(MobEffects.LUCK, 1, null),
                     new VaultModifier.Display("Lucky", TextColor.parseColor("#FFE900"), "+1 Luck", "+%d Luck", VaultMod.id("gui/modifiers/lucky"))
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("unlucky"),
                     new PlayerEffectModifier.Properties(MobEffects.UNLUCK, 1, null),
                     new VaultModifier.Display("Unlucky", TextColor.parseColor("#9F5300"), "-1 Luck", "-%d Luck", VaultMod.id("gui/modifiers/unlucky"))
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("hunger"),
                     new PlayerEffectModifier.Properties(MobEffects.HUNGER, 1, null),
                     new VaultModifier.Display("Hunger", TextColor.parseColor("#E8DACD"), "+1 Hunger", "+%d Hunger", VaultMod.id("gui/modifiers/hunger"))
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("tired"),
                     new PlayerEffectModifier.Properties(MobEffects.DIG_SLOWDOWN, 1, null),
                     new VaultModifier.Display(
                        "Tired", TextColor.parseColor("#E8E9E1"), "+1 Mining Fatigue", "+%d Mining Fatigue", VaultMod.id("gui/modifiers/tired")
                     )
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("slowed"),
                     new PlayerEffectModifier.Properties(MobEffects.MOVEMENT_SLOWDOWN, 1, null),
                     new VaultModifier.Display("Slowed", TextColor.parseColor("#4C6786"), "+1 Slowness", "+%d Slowness", VaultMod.id("gui/modifiers/slowed"))
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("weakened"),
                     new PlayerEffectModifier.Properties(MobEffects.WEAKNESS, 1, null),
                     new VaultModifier.Display(
                        "Weakened", TextColor.parseColor("#9F5300"), "+1 Weakness", "+%d Weakness", VaultMod.id("gui/modifiers/weakness")
                     )
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("speedy"),
                     new PlayerEffectModifier.Properties(MobEffects.MOVEMENT_SPEED, 1, null),
                     new VaultModifier.Display("Speedy", TextColor.parseColor("#00CDFF"), "+1 Speed", "+%d Speed", VaultMod.id("gui/modifiers/speed"))
                  )
               )
               .put(
                  new PlayerEffectModifier(
                     VaultMod.id("stronk"),
                     new PlayerEffectModifier.Properties(MobEffects.DAMAGE_BOOST, 1, null),
                     new VaultModifier.Display("Stronk", TextColor.parseColor("#5E12E5"), "+1 Strength", "+%d Strength", VaultMod.id("gui/modifiers/stronk"))
                  )
               )
         );
   }

   private void generateMobAttributeModifiers() {
      VaultModifierTypeRegistry.getIdFor(MobAttributeModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new MobAttributeModifier(
                     VaultMod.id("chunky_mobs"),
                     new EntityAttributeModifier.Properties(EntityAttributeModifier.ModifierType.MAX_HEALTH_ADDITIVE_PERCENTILE, 0.2, null),
                     new VaultModifier.Display(
                        "Chunky Mobs", TextColor.parseColor("#00FFFF"), "+20% Mob Health", "+%d%% Mob Health", VaultMod.id("gui/modifiers/chunky_mobs")
                     )
                  )
               )
               .put(
                  new MobAttributeModifier(
                     VaultMod.id("furious_mobs"),
                     new EntityAttributeModifier.Properties(EntityAttributeModifier.ModifierType.ATTACK_DAMAGE_ADDITIVE_PERCENTILE, 0.2, null),
                     new VaultModifier.Display(
                        "Furious Mobs", TextColor.parseColor("#00FFFF"), "+20% Mob Damage", "+%d%% Mob Damage", VaultMod.id("gui/modifiers/furious_mobs")
                     )
                  )
               )
               .put(
                  new MobAttributeModifier(
                     VaultMod.id("speedy_mobs"),
                     new EntityAttributeModifier.Properties(EntityAttributeModifier.ModifierType.SPEED_ADDITIVE_PERCENTILE, 0.05, null),
                     new VaultModifier.Display(
                        "Speedy Mobs", TextColor.parseColor("#00FFFF"), "+5% Mob Speed", "+%d%% Mob Speed", VaultMod.id("gui/modifiers/speedy_mobs")
                     )
                  )
               )
         );
   }

   private void generateChanceChestTrapModifier() {
      VaultModifierTypeRegistry.getIdFor(ChanceChestTrapModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new ChanceChestTrapModifier(
                     VaultMod.id("trapped"),
                     new AbstractChanceModifier.Properties(0.1F, null),
                     new VaultModifier.Display(
                        "Trapped", TextColor.parseColor("#D35B00"), "+10% Trap Chance", "+%d%% Trap Chance", VaultMod.id("gui/modifiers/trapped")
                     )
                  )
               )
               .put(
                  new ChanceChestTrapModifier(
                     VaultMod.id("looters_dream"),
                     new AbstractChanceModifier.Properties(0.0F, null),
                     new VaultModifier.Display("Looter's Dream", TextColor.parseColor("#A3E2F5"), "No Trap Chance", VaultMod.id("gui/modifiers/safezone"))
                  )
               )
         );
   }

   private void generatePlayerNoVaultFruitModifiers() {
      VaultModifierTypeRegistry.getIdFor(PlayerNoVaultFruitModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerNoVaultFruitModifier(
                     VaultMod.id("rotten"),
                     new PlayerNoVaultFruitModifier.Properties(),
                     new VaultModifier.Display("Rotten", TextColor.parseColor("#A0AF5B"), "Vault Powerups have no effect", VaultMod.id("gui/modifiers/rotten"))
                  )
               )
         );
   }

   private void generateVaultTimeModifiers() {
      VaultModifierTypeRegistry.getIdFor(VaultTimeModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new VaultTimeModifier(
                     VaultMod.id("extended"),
                     new VaultTimeModifier.Properties(1200, null),
                     new VaultModifier.Display(
                        "Extended", TextColor.parseColor("#2F86AE"), "+1 minute Vault Time", "+%d minute%s Vault Time", VaultMod.id("gui/modifiers/extended")
                     )
                  )
               )
               .put(
                  new VaultTimeModifier(
                     VaultMod.id("rushed"),
                     new VaultTimeModifier.Properties(-1200, null),
                     new VaultModifier.Display(
                        "Rushed", TextColor.parseColor("#FFCD6F"), "-1 minute Vault Time", "-%d minute%s Vault Time", VaultMod.id("gui/modifiers/rush")
                     )
                  )
               )
         );
   }

   private void generatePlayerNoExitModifiers() {
      VaultModifierTypeRegistry.getIdFor(PlayerNoExitModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerNoExitModifier(
                     VaultMod.id("raffle"),
                     new PlayerNoExitModifier.Properties(),
                     new VaultModifier.Display("Raffle", TextColor.parseColor("#C5001B"), "Locks the vault", VaultMod.id("gui/modifiers/raffle"))
                  )
               )
               .put(
                  new PlayerNoExitModifier(
                     VaultMod.id("locked"),
                     new PlayerNoExitModifier.Properties(),
                     new VaultModifier.Display("Locked", TextColor.parseColor("#FF0000"), "Locks the vault", VaultMod.id("gui/modifiers/locked"))
                  )
               )
         );
   }

   private void generateMobSpawnCountModifiers() {
      VaultModifierTypeRegistry.getIdFor(MobSpawnCountModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new MobSpawnCountModifier(
                     VaultMod.id("crowded"),
                     new MobSpawnCountModifier.Properties(1, null),
                     new VaultModifier.Display(
                        "Crowded", TextColor.parseColor("#E83F24"), "+1 Mob Spawns", "+%d Mob Spawns", VaultMod.id("gui/modifiers/crowded")
                     )
                  )
               )
               .put(
                  new MobSpawnCountModifier(
                     VaultMod.id("personal_space"),
                     new MobSpawnCountModifier.Properties(1, null),
                     new VaultModifier.Display(
                        "Personal Space", TextColor.parseColor("#F9B1FF"), "-1 Mob Spawns", "-%d Mob Spawns", VaultMod.id("gui/modifiers/personalspace")
                     )
                  )
               )
         );
   }

   private void generateVaultLevelModifiers() {
      VaultModifierTypeRegistry.getIdFor(VaultLevelModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new VaultLevelModifier(
                     VaultMod.id("difficult"),
                     new VaultLevelModifier.Properties(3, null),
                     new VaultModifier.Display(
                        "Difficult", TextColor.parseColor("#E20000"), "+3 Vault Level", "+%d Vault Level", VaultMod.id("gui/modifiers/difficult")
                     )
                  )
               )
               .put(
                  new VaultLevelModifier(
                     VaultMod.id("easy"),
                     new VaultLevelModifier.Properties(3, null),
                     new VaultModifier.Display("Easy", TextColor.parseColor("#70FF2A"), "-3 Vault Level", "-%d Vault Level", VaultMod.id("gui/modifiers/easy"))
                  )
               )
         );
   }

   private void generatePlayerInventoryRestoreModifiers() {
      VaultModifierTypeRegistry.getIdFor(PlayerInventoryRestoreModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerInventoryRestoreModifier(
                     VaultMod.id("phoenix"),
                     new PlayerInventoryRestoreModifier.Properties(false, 1.0F, 1.0F),
                     new VaultModifier.Display(
                        "Phoenix",
                        TextColor.parseColor("#FF8900"),
                        "All items you entered with will be restored on death",
                        VaultMod.id("gui/modifiers/phoenix")
                     )
                  )
               )
               .put(
                  new PlayerInventoryRestoreModifier(
                     VaultMod.id("afterlife"),
                     new PlayerInventoryRestoreModifier.Properties(true, 1.0F, 0.5F),
                     new VaultModifier.Display(
                        "Afterlife",
                        TextColor.parseColor("#0FA6E3"),
                        "All items you entered with will be restored on death; No artifact can be found",
                        VaultMod.id("gui/modifiers/afterlife")
                     )
                  )
               )
               .put(
                  new PlayerInventoryRestoreModifier(
                     VaultMod.id("beginners_grace"),
                     new PlayerInventoryRestoreModifier.Properties(false, 0.0F, 1.0F),
                     new VaultModifier.Display(
                        "Beginners Grace",
                        TextColor.parseColor("#FF8900"),
                        "All items you entered with will be restored on death; No experience on death",
                        VaultMod.id("gui/modifiers/beginners_grace")
                     )
                  )
               )
         );
   }

   private void generatePlayerDurabilityDamageModifiers() {
      VaultModifierTypeRegistry.getIdFor(PlayerDurabilityDamageModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new PlayerDurabilityDamageModifier(
                     VaultMod.id("frail"),
                     new PlayerDurabilityDamageModifier.Properties(0.2F, null),
                     new VaultModifier.Display(
                        "Frail", TextColor.parseColor("#7B7E7F"), "+20% Durability Damage", "+%d%% Durability Damage", VaultMod.id("gui/modifiers/frail")
                     )
                  )
               )
               .put(
                  new PlayerDurabilityDamageModifier(
                     VaultMod.id("reinforced"),
                     new PlayerDurabilityDamageModifier.Properties(-0.2F, null),
                     new VaultModifier.Display(
                        "Reinforced",
                        TextColor.parseColor("#9550FF"),
                        "-20% Durability Damage",
                        "-%d%% Durability Damage",
                        VaultMod.id("gui/modifiers/reinforced")
                     )
                  )
               )
         );
   }

   private void generateChanceArtifactModifiers() {
      VaultModifierTypeRegistry.getIdFor(ChanceArtifactModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new ChanceArtifactModifier(
                     VaultMod.id("treasure_hunting"),
                     new AbstractChanceModifier.Properties(0.1F, null),
                     new VaultModifier.Display(
                        "Treasure Hunting",
                        TextColor.parseColor("#EBFF8D"),
                        "+10% Artifact Chance",
                        "+%d%% Artifact Chance",
                        VaultMod.id("gui/modifiers/more_artifact1")
                     )
                  )
               )
         );
   }

   private void generateDecoratorAddModifiers() {
      VaultModifierTypeRegistry.getIdFor(DecoratorAddModifier.class)
         .ifPresent(
            typeId -> this.modifierTypeGroups
               .group(typeId)
               .put(
                  new DecoratorAddModifier(
                     VaultMod.id("gilded"),
                     new DecoratorAddModifier.Properties(
                        PartialTile.of(
                           (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.GILDED_CHEST)
                        ),
                        1,
                        true,
                        null
                     ),
                     new VaultModifier.Display("Gilded", TextColor.parseColor("#FFEC00"), "Adds Gilded Chests", VaultMod.id("gui/modifiers/gilded"))
                  )
               )
               .put(
                  new DecoratorAddModifier(
                     VaultMod.id("living"),
                     new DecoratorAddModifier.Properties(
                        PartialTile.of(
                           (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.LIVING_CHEST)
                        ),
                        1,
                        true,
                        null
                     ),
                     new VaultModifier.Display("Living", TextColor.parseColor("#5FC76A"), "Adds Living Chests", VaultMod.id("gui/modifiers/living"))
                  )
               )
               .put(
                  new DecoratorAddModifier(
                     VaultMod.id("ornate"),
                     new DecoratorAddModifier.Properties(
                        PartialTile.of(
                           (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.ORNATE_CHEST)
                        ),
                        1,
                        true,
                        null
                     ),
                     new VaultModifier.Display("Ornate", TextColor.parseColor("#8E5fC7"), "Adds Ornate Chests", VaultMod.id("gui/modifiers/ornate"))
                  )
               )
               .put(
                  new DecoratorAddModifier(
                     VaultMod.id("coin_pile"),
                     new DecoratorAddModifier.Properties(
                        PartialTile.of((BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.COIN_STACKS)),
                        1,
                        true,
                        null
                     ),
                     new VaultModifier.Display("Coins", TextColor.parseColor("#C7C05F"), "Adds Coin Piles to the vault", VaultMod.id("gui/modifiers/coins"))
                  )
               )
         );
   }

   private static class ModifierTypeGroup extends TreeMap<ResourceLocation, VaultModifier<?>> {
      private ModifierTypeGroup() {
         super(Comparator.comparing(ResourceLocation::getPath));
      }

      private VaultModifiersConfig.ModifierTypeGroup put(VaultModifier<?> modifier) {
         this.put(modifier.getId(), modifier);
         return this;
      }
   }

   public static class ModifierTypeGroups extends TreeMap<ResourceLocation, Map<ResourceLocation, VaultModifier<?>>> {
      private ModifierTypeGroups() {
         super(Comparator.comparing(ResourceLocation::getPath));
      }

      private ModifierTypeGroups(Comparator<? super ResourceLocation> comparator) {
         super(comparator);
      }

      private VaultModifiersConfig.ModifierTypeGroup group(ResourceLocation type) {
         VaultModifiersConfig.ModifierTypeGroup modifierTypeGroup = new VaultModifiersConfig.ModifierTypeGroup();
         this.put(type, modifierTypeGroup);
         return modifierTypeGroup;
      }

      public static class Serializer
         implements JsonDeserializer<VaultModifiersConfig.ModifierTypeGroups>,
         JsonSerializer<VaultModifiersConfig.ModifierTypeGroups> {
         public static final String KEY_PROPERTIES = "properties";
         public static final String KEY_DISPLAY = "display";

         public VaultModifiersConfig.ModifierTypeGroups deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
               throw new JsonParseException("Not a json object: %s".formatted(json));
            } else {
               VaultModifiersConfig.ModifierTypeGroups modifierTypeGroups = new VaultModifiersConfig.ModifierTypeGroups(
                  Comparator.comparing(ResourceLocation::getPath)
               );
               JsonObject object = json.getAsJsonObject();
               object.keySet()
                  .forEach(
                     modifierTypeKey -> {
                        ResourceLocation modifierTypeResourceLocation = new ResourceLocation(modifierTypeKey);
                        VaultModifierTypeRegistry.get(modifierTypeResourceLocation)
                           .ifPresentOrElse(
                              modifierType -> {
                                 VaultModifiersConfig.ModifierTypeGroup modifierTypeGroup = new VaultModifiersConfig.ModifierTypeGroup();
                                 JsonObject modifierTypeGroupObject = object.getAsJsonObject(modifierTypeKey);
                                 modifierTypeGroupObject.keySet()
                                    .forEach(
                                       modifierId -> {
                                          JsonObject modifierObject = modifierTypeGroupObject.getAsJsonObject(modifierId);
                                          JsonObject modifierPropertiesObject = modifierObject.getAsJsonObject("properties");
                                          JsonObject modifierDisplayObject = modifierObject.getAsJsonObject("display");
                                          VaultModifier<?> vaultModifier = modifierType.factory()
                                             .createVaultModifier(
                                                new ResourceLocation(modifierId),
                                                context.deserialize(modifierPropertiesObject, modifierType.modifierPropertyClass()),
                                                (VaultModifier.Display)context.deserialize(modifierDisplayObject, VaultModifier.Display.class)
                                             );
                                          modifierTypeGroup.put(vaultModifier);
                                       }
                                    );
                                 modifierTypeGroups.put(modifierTypeResourceLocation, modifierTypeGroup);
                              },
                              () -> VaultMod.LOGGER
                                 .error(
                                    "%s missing registration for modifier type %s"
                                       .formatted(VaultModifierTypeRegistry.class.getSimpleName(), modifierTypeResourceLocation)
                                 )
                           );
                     }
                  );
               return modifierTypeGroups;
            }
         }

         public JsonElement serialize(VaultModifiersConfig.ModifierTypeGroups src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject modifierGroupsObject = new JsonObject();
            src.forEach(
               (modifierTypeResourceLocation, modifierConfigGroup) -> VaultModifierTypeRegistry.get(modifierTypeResourceLocation)
                  .ifPresentOrElse(
                     modifierType -> {
                        JsonObject modifierGroupObject = new JsonObject();
                        modifierConfigGroup.forEach(
                           (modifierId, config) -> modifierGroupObject.add(modifierId.toString(), context.serialize(config, modifierType.modifierClass()))
                        );
                        modifierGroupsObject.add(modifierTypeResourceLocation.toString(), modifierGroupObject);
                     },
                     () -> VaultMod.LOGGER
                        .error(
                           "%s missing registration for modifier type %s"
                              .formatted(VaultModifierTypeRegistry.class.getSimpleName(), modifierTypeResourceLocation)
                        )
                  )
            );
            return modifierGroupsObject;
         }
      }
   }
}
