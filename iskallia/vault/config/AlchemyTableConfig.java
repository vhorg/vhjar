package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.init.ModEffects;
import iskallia.vault.item.bottle.AbsorptionBottleEffect;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleEffectManager;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.item.bottle.CastAbilityBottleEffect;
import iskallia.vault.item.bottle.CleanseBottleEffect;
import iskallia.vault.item.bottle.CooldownReductionBottleEffect;
import iskallia.vault.item.bottle.ManaFlatBottleEffect;
import iskallia.vault.item.bottle.ManaPercentBottleEffect;
import iskallia.vault.item.bottle.PotionBottleEffect;
import iskallia.vault.world.data.DiscoveredAlchemyEffectsData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AlchemyTableConfig extends Config {
   @Expose
   private final List<AlchemyTableConfig.CraftableEffectConfig> craftableEffects = new ArrayList<>();
   private final Map<String, AlchemyTableConfig.CraftableEffectConfig> effectConfigs = new HashMap<>();

   @Override
   public String getName() {
      return "alchemy_table";
   }

   public List<AlchemyTableConfig.CraftableEffectConfig> getCraftableEffects() {
      return this.craftableEffects;
   }

   @Nullable
   public AlchemyTableConfig.CraftableEffectConfig getConfig(String effectId) {
      return this.effectConfigs.containsKey(effectId) ? this.effectConfigs.get(effectId) : null;
   }

   @Override
   protected void reset() {
      this.craftableEffects.clear();
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.GRANTED,
               "mana_flat",
               "mana_flat",
               "Flat Mana Restoration",
               "Restores %s Mana",
               TextColor.fromRgb(65535),
               Map.of(
                  BottleItem.Type.VIAL,
                  new ManaFlatBottleEffect("mana_flat", 50.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new ManaFlatBottleEffect("mana_flat", 100.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new ManaFlatBottleEffect("mana_flat", 150.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new ManaFlatBottleEffect("mana_flat", 200.0F).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "mana_percent",
               "mana_percent",
               "Percent Mana Restoration",
               "Restores %s%% Mana",
               TextColor.fromRgb(65535),
               Map.of(
                  BottleItem.Type.VIAL,
                  new ManaPercentBottleEffect("mana_percent", 0.25F).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new ManaPercentBottleEffect("mana_percent", 0.5F).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new ManaPercentBottleEffect("mana_percent", 0.75F).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new ManaPercentBottleEffect("mana_percent", 1.0F).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "cooldown_reduction",
               "cooldown_reduction",
               "Instant Cooldown Reduction",
               "Reduces Cooldowns by %s%%",
               TextColor.fromRgb(13362942),
               Map.of(
                  BottleItem.Type.VIAL,
                  new CooldownReductionBottleEffect("cooldown_reduction", 0.25F).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new CooldownReductionBottleEffect("cooldown_reduction", 0.5F).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new CooldownReductionBottleEffect("cooldown_reduction", 0.75F).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new CooldownReductionBottleEffect("cooldown_reduction", 1.0F).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "speed_potion",
               "potion",
               "Movement Speed Increase",
               "%1$ss Speed Increase",
               TextColor.fromRgb(8171462),
               Map.of(
                  BottleItem.Type.VIAL,
                  new PotionBottleEffect("speed_potion", MobEffects.MOVEMENT_SPEED, 200, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new PotionBottleEffect("speed_potion", MobEffects.MOVEMENT_SPEED, 300, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new PotionBottleEffect("speed_potion", MobEffects.MOVEMENT_SPEED, 400, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new PotionBottleEffect("speed_potion", MobEffects.MOVEMENT_SPEED, 500, 0).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "regeneration_potion",
               "potion",
               "Regeneration",
               "%1$ss Regeneration",
               TextColor.fromRgb(13458603),
               Map.of(
                  BottleItem.Type.VIAL,
                  new PotionBottleEffect("regeneration_potion", MobEffects.REGENERATION, 400, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new PotionBottleEffect("regeneration_potion", MobEffects.REGENERATION, 600, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new PotionBottleEffect("regeneration_potion", MobEffects.REGENERATION, 800, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new PotionBottleEffect("regeneration_potion", MobEffects.REGENERATION, 1200, 0).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "immortality_potion",
               "potion",
               "Immortality",
               "%1$ss Immortality",
               TextColor.fromRgb(7999),
               Map.of(
                  BottleItem.Type.VIAL,
                  new PotionBottleEffect("immortality_potion", ModEffects.IMMORTALITY, 40, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new PotionBottleEffect("immortality_potion", ModEffects.IMMORTALITY, 80, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new PotionBottleEffect("immortality_potion", ModEffects.IMMORTALITY, 120, 0).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new PotionBottleEffect("immortality_potion", ModEffects.IMMORTALITY, 160, 0).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "absorption",
               "absorption",
               "Absorption",
               "Adds %s Absorption Hearts",
               TextColor.fromRgb(16760576),
               Map.of(
                  BottleItem.Type.VIAL,
                  new AbsorptionBottleEffect("absorption", 2.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new AbsorptionBottleEffect("absorption", 4.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new AbsorptionBottleEffect("absorption", 6.0F).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new AbsorptionBottleEffect("absorption", 8.0F).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "cleanse",
               "cleanse",
               "Cleanse",
               "%ss of No Negative Effects",
               TextColor.fromRgb(16772541),
               Map.of(
                  BottleItem.Type.VIAL,
                  new CleanseBottleEffect("cleanse", 400).serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new CleanseBottleEffect("cleanse", 800).serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new CleanseBottleEffect("cleanse", 1200).serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new CleanseBottleEffect("cleanse", 1800).serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
      this.craftableEffects
         .add(
            new AlchemyTableConfig.CraftableEffectConfig(
               AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY,
               "cast_javelin",
               "cast_ability",
               "Cast Javelin",
               "Casts %1$s %2$s",
               TextColor.fromRgb(16772541),
               Map.of(
                  BottleItem.Type.VIAL,
                  new CastAbilityBottleEffect("cast_javelin", "javelin_4").serializeData(new CompoundTag()),
                  BottleItem.Type.POTION,
                  new CastAbilityBottleEffect("cast_javelin", "javelin_5").serializeData(new CompoundTag()),
                  BottleItem.Type.MIXTURE,
                  new CastAbilityBottleEffect("cast_javelin", "javelin_6").serializeData(new CompoundTag()),
                  BottleItem.Type.BREW,
                  new CastAbilityBottleEffect("cast_javelin", "javelin_7").serializeData(new CompoundTag())
               ),
               Map.of(
                  BottleItem.Type.VIAL,
                  List.of(new ItemEntry(Items.DIAMOND, 5)),
                  BottleItem.Type.POTION,
                  List.of(new ItemEntry(Items.DIAMOND, 6)),
                  BottleItem.Type.MIXTURE,
                  List.of(new ItemEntry(Items.DIAMOND, 7)),
                  BottleItem.Type.BREW,
                  List.of(new ItemEntry(Items.DIAMOND, 8))
               )
            )
         );
   }

   @Override
   public <T extends Config> T readConfig() {
      AlchemyTableConfig cfg = super.readConfig();

      for (AlchemyTableConfig.CraftableEffectConfig effectConfig : cfg.craftableEffects) {
         cfg.effectConfigs.put(effectConfig.effectId, effectConfig);
      }

      return (T)cfg;
   }

   @Override
   protected boolean isValid() {
      if (!super.isValid()) {
         return false;
      } else {
         Set<String> foundIdentifiers = new HashSet<>();

         for (AlchemyTableConfig.CraftableEffectConfig cfg : this.craftableEffects) {
            if (!foundIdentifiers.add(cfg.effectId)) {
               throw new IllegalArgumentException(
                  "Invalid Alchemy Table configuration (%s) - duplicate effect found: %s".formatted(this.getName(), cfg.effectId)
               );
            }
         }

         return true;
      }
   }

   public static class CraftableEffectConfig {
      @Expose
      private final String effectId;
      @Expose
      private final String type;
      @Expose
      private final String effectName;
      @Expose
      private final String tooltip;
      @Expose
      private final TextColor color;
      @Expose
      private final Map<BottleItem.Type, List<ItemEntry>> craftingCosts;
      @Expose
      private final AlchemyTableConfig.UnlockCategory unlockCategory;
      @Expose
      private final Map<BottleItem.Type, CompoundTag> typeSettings;

      public CraftableEffectConfig(
         AlchemyTableConfig.UnlockCategory unlockCategory,
         String effectId,
         String type,
         String effectName,
         String tooltip,
         TextColor color,
         Map<BottleItem.Type, CompoundTag> typeSettings,
         Map<BottleItem.Type, List<ItemEntry>> craftingCosts
      ) {
         this.unlockCategory = unlockCategory;
         this.effectId = effectId;
         this.type = type;
         this.effectName = effectName;
         this.tooltip = tooltip;
         this.color = color;
         this.typeSettings = typeSettings;
         this.craftingCosts = craftingCosts;
      }

      public boolean hasPrerequisites(Player player) {
         if (this.unlockCategory == AlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY) {
            if (player instanceof ServerPlayer sPlayer) {
               DiscoveredAlchemyEffectsData data = DiscoveredAlchemyEffectsData.get(sPlayer.getLevel());
               return data.hasDiscoveredEffect(player, this.effectId);
            } else {
               return ClientDiscoveredEntriesData.AlchemyEffects.getDiscoveredAlchemyEffects().contains(this.effectId);
            }
         } else {
            return this.unlockCategory == AlchemyTableConfig.UnlockCategory.GRANTED;
         }
      }

      public List<ItemEntry> getCraftingCost(BottleItem.Type bottleType) {
         return this.craftingCosts.getOrDefault(bottleType, Collections.emptyList());
      }

      public List<ItemStack> createCraftingCost(ItemStack input) {
         return input.isEmpty()
            ? Collections.emptyList()
            : BottleItem.getType(input)
               .map(bottleType -> this.getCraftingCost(bottleType).stream().map(ItemEntry::createItemStack).filter(stack -> !stack.isEmpty()).toList())
               .orElse(Collections.emptyList());
      }

      public AlchemyTableConfig.UnlockCategory getUnlockCategory() {
         return this.unlockCategory;
      }

      public String getEffectId() {
         return this.effectId;
      }

      public String getTooltip() {
         return this.tooltip;
      }

      public String getEffectName() {
         return this.effectName;
      }

      public Optional<BottleEffect> createEffect(BottleItem.Type bottleType) {
         return !this.typeSettings.containsKey(bottleType)
            ? Optional.empty()
            : BottleEffectManager.deserialize(this.type, this.effectId, this.typeSettings.get(bottleType));
      }

      public TextColor getColor() {
         return this.color;
      }
   }

   public static enum UnlockCategory {
      GRANTED("Granted at Start"),
      VAULT_DISCOVERY("Requires discovery in the Alchemy Archives");

      private final String display;

      private UnlockCategory(String display) {
         this.display = display;
      }

      public String formatDisplay(Object... args) {
         return this.display.formatted(args);
      }
   }
}
