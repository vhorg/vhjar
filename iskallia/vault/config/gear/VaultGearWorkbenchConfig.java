package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientDiscoveredEntriesData;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultGearWorkbenchConfig extends Config {
   @Expose
   private final List<ItemEntry> costRemoveCraftedModifiers = new ArrayList<>();
   @Expose
   private final Map<VaultGearModifier.AffixType, List<VaultGearWorkbenchConfig.CraftableModifierConfig>> craftableModifiers = new LinkedHashMap<>();
   private Item gearItem;

   public static Map<Item, VaultGearWorkbenchConfig> registerConfigs() {
      Map<Item, VaultGearWorkbenchConfig> gearConfig = new HashMap<>();

      for (Item item : Arrays.asList(
         ModItems.HELMET,
         ModItems.CHESTPLATE,
         ModItems.LEGGINGS,
         ModItems.BOOTS,
         ModItems.SWORD,
         ModItems.AXE,
         ModItems.SHIELD,
         ModItems.IDOL_BENEVOLENT,
         ModItems.IDOL_MALEVOLENCE,
         ModItems.IDOL_OMNISCIENT,
         ModItems.IDOL_TIMEKEEPER,
         ModItems.WAND,
         ModItems.MAGNET
      )) {
         gearConfig.put(item, new VaultGearWorkbenchConfig(item).readConfig());
      }

      return gearConfig;
   }

   public static Optional<VaultGearWorkbenchConfig> getConfig(Item item) {
      return Optional.ofNullable(ModConfigs.VAULT_GEAR_WORKBENCH_CONFIG.get(item));
   }

   public VaultGearWorkbenchConfig(Item gearItem) {
      this.gearItem = gearItem;
   }

   public Item getGearItem() {
      return this.gearItem;
   }

   @Override
   public String getName() {
      return "gear_workbench%s%s".formatted(File.separator, this.gearItem.getRegistryName().getPath());
   }

   public List<ItemStack> getCostRemoveCraftedModifiers() {
      return this.costRemoveCraftedModifiers.stream().map(ItemEntry::createItemStack).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
   }

   public List<VaultGearWorkbenchConfig.CraftableModifierConfig> getAllCraftableModifiers() {
      List<VaultGearWorkbenchConfig.CraftableModifierConfig> all = new ArrayList<>();
      this.craftableModifiers.values().forEach(all::addAll);
      return all;
   }

   @Nullable
   public VaultGearWorkbenchConfig.CraftableModifierConfig getConfig(ResourceLocation modifierConfigIdentifier) {
      for (List<VaultGearWorkbenchConfig.CraftableModifierConfig> configs : this.craftableModifiers.values()) {
         for (VaultGearWorkbenchConfig.CraftableModifierConfig cfg : configs) {
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
      List<VaultGearWorkbenchConfig.CraftableModifierConfig> prefixes = new ArrayList<>();
      List<VaultGearWorkbenchConfig.CraftableModifierConfig> suffixes = new ArrayList<>();
      this.craftableModifiers.put(VaultGearModifier.AffixType.PREFIX, prefixes);
      this.craftableModifiers.put(VaultGearModifier.AffixType.SUFFIX, suffixes);
      prefixes.add(
         new VaultGearWorkbenchConfig.CraftableModifierConfig(
            VaultMod.id("crafted_armor"), 0, VaultGearWorkbenchConfig.UnlockCategory.LEVEL, new ItemEntry(Items.DIAMOND, 5)
         )
      );
   }

   @Override
   public <T extends Config> T readConfig() {
      VaultGearWorkbenchConfig cfg = super.readConfig();

      for (VaultGearWorkbenchConfig.CraftableModifierConfig modifierConfig : cfg.craftableModifiers
         .computeIfAbsent(VaultGearModifier.AffixType.PREFIX, affix -> new ArrayList<>())) {
         modifierConfig.affixGroup = VaultGearTierConfig.ModifierAffixTagGroup.CRAFTED_PREFIX;
         modifierConfig.gearItem = cfg.gearItem;
      }

      for (VaultGearWorkbenchConfig.CraftableModifierConfig modifierConfig : cfg.craftableModifiers
         .computeIfAbsent(VaultGearModifier.AffixType.SUFFIX, affix -> new ArrayList<>())) {
         modifierConfig.affixGroup = VaultGearTierConfig.ModifierAffixTagGroup.CRAFTED_SUFFIX;
         modifierConfig.gearItem = cfg.gearItem;
      }

      return (T)cfg;
   }

   @Override
   protected void onLoad(Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      if (oldConfigInstance instanceof VaultGearWorkbenchConfig cfg) {
         this.gearItem = cfg.gearItem;
      }
   }

   @Override
   protected boolean isValid() {
      if (!super.isValid()) {
         return false;
      } else {
         Set<ResourceLocation> foundIdentifiers = new HashSet<>();

         for (List<VaultGearWorkbenchConfig.CraftableModifierConfig> configs : this.craftableModifiers.values()) {
            for (VaultGearWorkbenchConfig.CraftableModifierConfig cfg : configs) {
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
      private final VaultGearWorkbenchConfig.UnlockCategory unlockCategory;
      @Expose
      private final List<ItemEntry> craftingCost;
      private VaultGearTierConfig.ModifierAffixTagGroup affixGroup;
      private Item gearItem;

      public CraftableModifierConfig(
         ResourceLocation modifierIdentifier, int modifierTier, VaultGearWorkbenchConfig.UnlockCategory unlockCategory, ItemEntry... craftingCost
      ) {
         this(VaultMod.id(modifierIdentifier.getPath() + "_t" + modifierTier), modifierIdentifier, modifierTier, unlockCategory, Arrays.asList(craftingCost));
      }

      public CraftableModifierConfig(
         ResourceLocation workbenchCraftIdentifier,
         ResourceLocation modifierIdentifier,
         int modifierTier,
         VaultGearWorkbenchConfig.UnlockCategory unlockCategory,
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
                     DiscoveredWorkbenchModifiersData data = DiscoveredWorkbenchModifiersData.get(sPlayer.getLevel());
                     return data.hasDiscoveredCraft(player, this.gearItem, this.workbenchCraftIdentifier);
                  }

                  return ClientDiscoveredEntriesData.WorkbenchCrafts.getDiscoveredWorkbenchCrafts(this.gearItem).contains(this.workbenchCraftIdentifier);
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
            if (ModifierWorkbenchHelper.hasCraftedModifier(input)) {
               VaultGearWorkbenchConfig.getConfig(input.getItem()).map(VaultGearWorkbenchConfig::getCostRemoveCraftedModifiers).ifPresent(inputs::addAll);
            }

            return inputs;
         }
      }

      public VaultGearWorkbenchConfig.UnlockCategory getUnlockCategory() {
         return this.unlockCategory;
      }

      public VaultGearTierConfig.ModifierAffixTagGroup getAffixGroup() {
         return this.affixGroup;
      }

      public ResourceLocation getWorkbenchCraftIdentifier() {
         return this.workbenchCraftIdentifier;
      }

      public ResourceLocation getModifierIdentifier() {
         return this.modifierIdentifier;
      }

      public Optional<VaultGearModifier<?>> createModifier() {
         return VaultGearTierConfig.getConfig(this.gearItem)
            .flatMap(cfg -> cfg.createModifier(this.affixGroup, this.modifierIdentifier, this.modifierTier, Config.rand));
      }

      public int getMinLevel() {
         return VaultGearTierConfig.getConfig(this.gearItem)
            .flatMap(cfg -> cfg.getConfiguredModifierTier(this.affixGroup, this.modifierIdentifier, this.modifierTier))
            .map(VaultGearTierConfig.ModifierOutcome::tier)
            .map(VaultGearTierConfig.ModifierTier::getMinLevel)
            .orElse(Integer.MIN_VALUE);
      }
   }

   public static enum UnlockCategory {
      LEVEL("Requires Level %s"),
      VAULT_DISCOVERY("Requires discovery in the Modifier Archives");

      private final String display;

      private UnlockCategory(String display) {
         this.display = display;
      }

      public String formatDisplay(Object... args) {
         return this.display.formatted(args);
      }
   }
}
