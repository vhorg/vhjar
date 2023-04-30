package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.AlchemyTableHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.BottleItem;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.DiscoveredAlchemyModifiersData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultAlchemyTableConfig extends Config {
   @Expose
   private final List<ItemEntry> costRemoveCraftedModifiers = new ArrayList<>();
   @Expose
   private final Map<VaultGearModifier.AffixType, List<VaultAlchemyTableConfig.CraftableModifierConfig>> craftableModifiers = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "alchemy_table";
   }

   public List<ItemStack> getCostRemoveCraftedModifiers() {
      return this.costRemoveCraftedModifiers.stream().map(ItemEntry::createItemStack).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
   }

   public List<VaultAlchemyTableConfig.CraftableModifierConfig> getAllCraftableModifiers() {
      List<VaultAlchemyTableConfig.CraftableModifierConfig> all = new ArrayList<>();
      this.craftableModifiers.values().forEach(all::addAll);
      return all;
   }

   @Nullable
   public VaultAlchemyTableConfig.CraftableModifierConfig getConfig(ResourceLocation modifierConfigIdentifier) {
      for (List<VaultAlchemyTableConfig.CraftableModifierConfig> configs : this.craftableModifiers.values()) {
         for (VaultAlchemyTableConfig.CraftableModifierConfig cfg : configs) {
            if (cfg.workbenchCraftIdentifier.equals(modifierConfigIdentifier)) {
               return cfg;
            }
         }
      }

      return null;
   }

   @Override
   protected void reset() {
      this.costRemoveCraftedModifiers.clear();
      this.costRemoveCraftedModifiers.add(new ItemEntry(new ItemStack(ModItems.NULLIFYING_FOCUS, 5)));
      this.craftableModifiers.clear();
      List<VaultAlchemyTableConfig.CraftableModifierConfig> prefixes = new ArrayList<>();
      List<VaultAlchemyTableConfig.CraftableModifierConfig> suffixes = new ArrayList<>();
      this.craftableModifiers.put(VaultGearModifier.AffixType.PREFIX, prefixes);
      this.craftableModifiers.put(VaultGearModifier.AffixType.SUFFIX, suffixes);
      prefixes.add(
         new VaultAlchemyTableConfig.CraftableModifierConfig(
            VaultMod.id("crafted_armor"), 0, VaultAlchemyTableConfig.UnlockCategory.LEVEL, new ItemEntry(Items.DIAMOND, 5)
         )
      );
   }

   @Override
   public <T extends Config> T readConfig() {
      VaultAlchemyTableConfig cfg = super.readConfig();

      for (VaultAlchemyTableConfig.CraftableModifierConfig modifierConfig : cfg.craftableModifiers
         .computeIfAbsent(VaultGearModifier.AffixType.PREFIX, affix -> new ArrayList<>())) {
         modifierConfig.affixGroup = VaultGearTierConfig.ModifierAffixTagGroup.CRAFTED_PREFIX;
      }

      for (VaultAlchemyTableConfig.CraftableModifierConfig modifierConfig : cfg.craftableModifiers
         .computeIfAbsent(VaultGearModifier.AffixType.SUFFIX, affix -> new ArrayList<>())) {
         modifierConfig.affixGroup = VaultGearTierConfig.ModifierAffixTagGroup.CRAFTED_SUFFIX;
      }

      return (T)cfg;
   }

   @Override
   protected boolean isValid() {
      if (!super.isValid()) {
         return false;
      } else {
         Set<ResourceLocation> foundIdentifiers = new HashSet<>();

         for (List<VaultAlchemyTableConfig.CraftableModifierConfig> configs : this.craftableModifiers.values()) {
            for (VaultAlchemyTableConfig.CraftableModifierConfig cfg : configs) {
               if (!foundIdentifiers.add(cfg.workbenchCraftIdentifier)) {
                  throw new IllegalArgumentException(
                     "Invalid Gear Workbench configuration (%s) - duplicate identifier found: %s".formatted(this.getName(), cfg.workbenchCraftIdentifier)
                  );
               }
            }
         }

         return true;
      }
   }

   public static class CraftableModifierConfig {
      @Expose
      private final ResourceLocation workbenchCraftIdentifier;
      @Expose
      private final ResourceLocation modifierIdentifier;
      @Expose
      private final int modifierTier;
      @Expose
      private final VaultAlchemyTableConfig.UnlockCategory unlockCategory;
      @Expose
      private final List<ItemEntry> craftingCost;
      private VaultGearTierConfig.ModifierAffixTagGroup affixGroup;

      public CraftableModifierConfig(
         ResourceLocation modifierIdentifier, int modifierTier, VaultAlchemyTableConfig.UnlockCategory unlockCategory, ItemEntry... craftingCost
      ) {
         this(VaultMod.id(modifierIdentifier.getPath() + "_t" + modifierTier), modifierIdentifier, modifierTier, unlockCategory, Arrays.asList(craftingCost));
      }

      public CraftableModifierConfig(
         ResourceLocation workbenchCraftIdentifier,
         ResourceLocation modifierIdentifier,
         int modifierTier,
         VaultAlchemyTableConfig.UnlockCategory unlockCategory,
         List<ItemEntry> craftingCost
      ) {
         this.workbenchCraftIdentifier = workbenchCraftIdentifier;
         this.modifierIdentifier = modifierIdentifier;
         this.modifierTier = modifierTier;
         this.unlockCategory = unlockCategory;
         this.craftingCost = craftingCost;
      }

      public boolean hasPrerequisites(Player player) {
         int level = this.getMinLevel();
         if (level == Integer.MIN_VALUE) {
            return false;
         } else {
            switch (this.unlockCategory) {
               case LEVEL:
                  return SidedHelper.getVaultLevel(player) >= level;
               case VAULT_DISCOVERY:
                  if (player instanceof ServerPlayer sPlayer) {
                     DiscoveredAlchemyModifiersData data = DiscoveredAlchemyModifiersData.get(sPlayer.getLevel());
                     return data.hasDiscoveredCraft(player, this.workbenchCraftIdentifier);
                  }

                  return ClientDiscoveredEntriesData.AlchemyCrafts.getDiscoveredWorkbenchCrafts().contains(this.workbenchCraftIdentifier);
               default:
                  return false;
            }
         }
      }

      public List<ItemEntry> getCraftingCost() {
         return this.craftingCost;
      }

      public List<ItemStack> createCraftingCost(ItemStack input) {
         if (input.isEmpty()) {
            return Collections.emptyList();
         } else {
            List<ItemStack> inputs = this.getCraftingCost()
               .stream()
               .map(ItemEntry::createItemStack)
               .filter(stack -> !stack.isEmpty())
               .collect(Collectors.toList());
            if (AlchemyTableHelper.hasCraftedModifier(input)) {
               BottleItem.getType(input).map(t -> ModConfigs.VAULT_ALCHEMY_TABLE.getCostRemoveCraftedModifiers()).ifPresent(inputs::addAll);
            }

            return inputs;
         }
      }

      public VaultAlchemyTableConfig.UnlockCategory getUnlockCategory() {
         return this.unlockCategory;
      }

      public VaultGearTierConfig.ModifierAffixTagGroup getAffixGroup() {
         return this.affixGroup;
      }

      public ResourceLocation getWorkbenchCraftIdentifier() {
         return this.workbenchCraftIdentifier;
      }

      public Optional<VaultGearModifier<?>> createModifier() {
         return VaultGearTierConfig.getConfig(ModItems.BOTTLE)
            .flatMap(cfg -> cfg.createModifier(this.affixGroup, this.modifierIdentifier, this.modifierTier, Config.rand));
      }

      public int getMinLevel() {
         return VaultGearTierConfig.getConfig(ModItems.BOTTLE)
            .flatMap(cfg -> cfg.getConfiguredModifierTier(this.affixGroup, this.modifierIdentifier, this.modifierTier))
            .map(VaultGearTierConfig.ModifierOutcome::tier)
            .map(VaultGearTierConfig.ModifierTier::getMinLevel)
            .orElse(Integer.MIN_VALUE);
      }
   }

   public static enum UnlockCategory {
      LEVEL("Requires Level %s"),
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
