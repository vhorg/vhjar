package iskallia.vault.gear.attribute;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.tooltip.ModifierCategoryTooltip;
import iskallia.vault.util.MiscUtils;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VaultGearModifier<T> extends VaultGearAttributeInstance<T> {
   private int rolledTier = -1;
   private String modifierGroup = "";
   private ResourceLocation modifierIdentifier = null;
   private long gameTimeAdded = Long.MIN_VALUE;
   private VaultGearModifier.AffixCategorySet categories = new VaultGearModifier.AffixCategorySet();

   VaultGearModifier(VaultGearAttribute<T> attribute) {
      super(attribute);
   }

   public VaultGearModifier(VaultGearAttribute<T> attribute, T value) {
      super(attribute, value);
   }

   public void setRolledTier(int rolledTier) {
      this.rolledTier = rolledTier;
   }

   public int getRolledTier() {
      return this.rolledTier;
   }

   public boolean addCategory(VaultGearModifier.AffixCategory category) {
      return this.categories.add(category);
   }

   public VaultGearModifier.AffixCategorySet getCategories() {
      return this.categories;
   }

   public boolean removeCategory(VaultGearModifier.AffixCategory category) {
      return this.categories.remove(category);
   }

   public void clearCategories() {
      this.categories.clear();
   }

   public boolean hasCategory(VaultGearModifier.AffixCategory category) {
      return this.categories.contains(category);
   }

   public boolean hasAnyCategoryMatching(Predicate<VaultGearModifier.AffixCategory> categoryCheck) {
      return this.categories.stream().anyMatch(categoryCheck);
   }

   public boolean hasNoCategoryMatching(Predicate<VaultGearModifier.AffixCategory> categoryCheck) {
      return this.categories.stream().noneMatch(categoryCheck);
   }

   public void setModifierGroup(String modifierGroup) {
      this.modifierGroup = modifierGroup;
   }

   public String getModifierGroup() {
      return this.modifierGroup;
   }

   public void setModifierIdentifier(ResourceLocation modifierIdentifier) {
      this.modifierIdentifier = modifierIdentifier;
   }

   public ResourceLocation getModifierIdentifier() {
      return this.modifierIdentifier;
   }

   public void setGameTimeAdded(long gameTimeAdded) {
      this.gameTimeAdded = gameTimeAdded;
   }

   public long getGameTimeAdded() {
      return this.gameTimeAdded;
   }

   public boolean hasGameTimeAdded() {
      return this.getGameTimeAdded() != Long.MIN_VALUE;
   }

   public void resetGameTimeAdded() {
      this.setGameTimeAdded(Long.MIN_VALUE);
   }

   @Override
   public boolean canBeModified() {
      return this.hasNoCategoryMatching(cat -> !cat.canBeModified());
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Optional<MutableComponent> getDisplay(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, boolean displayDetail) {
      return super.getDisplay(data, type, stack, displayDetail)
         .map(text -> {
            for (VaultGearModifier.AffixCategory cat : this.categories) {
               text = cat.getModifierFormatter().apply(text);
            }

            return (MutableComponent)text;
         })
         .map(displayText -> {
            if (!this.hasGameTimeAdded()) {
               return (MutableComponent)displayText;
            } else {
               int showDuration = 600;
               long added = this.getGameTimeAdded();
               Level currentWorld = Minecraft.getInstance().level;
               if (currentWorld != null && currentWorld.getGameTime() - added <= showDuration) {
                  displayText.append(new TextComponent(" [new]").withStyle(ChatFormatting.GOLD));
                  return (MutableComponent)displayText;
               } else {
                  return (MutableComponent)displayText;
               }
            }
         })
         .map(
            displayText -> {
               if (!displayDetail) {
                  return (MutableComponent)displayText;
               } else {
                  Style txtStyle = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false).withUnderlined(false).withBold(false);
                  String categoryInfo = this.getCategories()
                     .stream()
                     .map(VaultGearModifier.AffixCategory::getTooltipDescriptor)
                     .collect(Collectors.joining(" "));
                  VaultGearTierConfig.ModifierConfigRange configRange = VaultGearTierConfig.getConfig(stack)
                     .map(tierCfg -> tierCfg.getTierConfigRange(this, data.getItemLevel()))
                     .orElse(VaultGearTierConfig.ModifierConfigRange.empty());
                  ConfigurableAttributeGenerator attributeGenerator = this.getAttribute().getGenerator();
                  MutableComponent cmpRangeDescriptor = new TextComponent(categoryInfo);
                  if (configRange.minAvailableConfig() != null && configRange.maxAvailableConfig() != null) {
                     MutableComponent minMaxRangeCmp = attributeGenerator.getConfigRangeDisplay(
                        this.getAttribute().getReader(), configRange.minAvailableConfig(), configRange.maxAvailableConfig()
                     );
                     if (minMaxRangeCmp != null) {
                        if (!cmpRangeDescriptor.getString().isBlank()) {
                           cmpRangeDescriptor.append(" ");
                        }

                        cmpRangeDescriptor.append(minMaxRangeCmp);
                        if (Screen.hasAltDown()) {
                           cmpRangeDescriptor.append(",");
                        }
                     }
                  }

                  if (Screen.hasAltDown()) {
                     if (!cmpRangeDescriptor.getString().isBlank()) {
                        cmpRangeDescriptor.append(" ");
                     }

                     if (configRange.tierConfig() != null) {
                        MutableComponent rangeCmp = attributeGenerator.getConfigRangeDisplay(this.getAttribute().getReader(), configRange.tierConfig());
                        if (rangeCmp != null) {
                           cmpRangeDescriptor.append("T%s: ".formatted(this.getRolledTier() + 1));
                           cmpRangeDescriptor.append(rangeCmp);
                        }
                     } else {
                        cmpRangeDescriptor.append("T%s".formatted(this.getRolledTier() + 1));
                     }
                  }

                  if (!cmpRangeDescriptor.getString().isBlank()) {
                     displayText.append(new TextComponent(" ").withStyle(txtStyle).append("(").append(cmpRangeDescriptor).append(")"));
                  }

                  return (MutableComponent)displayText;
               }
            }
         );
   }

   public Optional<MutableComponent> getConfigDisplay(ItemStack stack) {
      return VaultGearTierConfig.getConfig(stack).map(tierCfg -> tierCfg.getTierConfig(this)).map(cfg -> {
         ConfigurableAttributeGenerator configGen = this.getAttribute().getGenerator();
         return configGen.getConfigDisplay(this.getAttribute().getReader(), cfg);
      });
   }

   @Override
   public void write(BitBuffer buf) {
      super.write(buf);
      buf.writeInt(this.rolledTier);
      this.categories.write(buf);
      buf.writeString(this.modifierGroup);
      buf.writeNullable(this.modifierIdentifier, BitBuffer::writeIdentifier);
      buf.writeLong(this.gameTimeAdded);
   }

   @Override
   public void read(BitBuffer buf, GearDataVersion version) {
      super.read(buf, version);
      this.rolledTier = buf.readInt();
      this.categories = VaultGearModifier.AffixCategorySet.read(buf, version);
      this.modifierGroup = buf.readString();
      this.modifierIdentifier = buf.readNullable(BitBuffer::readIdentifier);
      this.gameTimeAdded = buf.readLong();
   }

   @Override
   public void toNbt(CompoundTag tag) {
      super.toNbt(tag);
      tag.putInt("rolledTier", this.rolledTier);
      this.categories.writeNbt(tag);
      tag.putString("modifierGroup", this.modifierGroup);
      if (this.modifierIdentifier != null) {
         tag.putString("modifierIdentifier", this.modifierIdentifier.toString());
      }

      tag.putLong("gameTimeAdded", this.gameTimeAdded);
   }

   @Override
   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      super.fromNbt(tag, version);
      this.rolledTier = tag.getInt("rolledTier");
      this.categories = VaultGearModifier.AffixCategorySet.readNbt(tag, version);
      this.modifierGroup = tag.getString("modifierGroup");
      if (tag.contains("modifierIdentifier", 8)) {
         this.modifierIdentifier = new ResourceLocation(tag.getString("modifierIdentifier"));
      }

      this.gameTimeAdded = tag.getLong("gameTimeAdded");
   }

   @Override
   public JsonObject serialize(VaultGearModifier.AffixType type) {
      JsonObject obj = super.serialize(type);
      obj.addProperty("legendary", this.categories.contains(VaultGearModifier.AffixCategory.LEGENDARY));
      obj.addProperty("category", this.categories.isEmpty() ? VaultGearModifier.AffixCategory.NONE.name() : this.categories.first().name());
      JsonArray categories = new JsonArray();
      this.categories.forEach(cat -> categories.add(cat.name()));
      obj.add("categories", categories);
      return obj;
   }

   public static enum AffixCategory {
      NONE,
      LEGENDARY("Legendary", ModifierCategoryTooltip::modifyLegendaryTooltip, VaultMod.id("textures/item/gear/legendary_overlay.png")),
      ABYSSAL("Abyssal", ModifierCategoryTooltip::modifyAbyssalTooltip),
      ABILITY_ENHANCEMENT("Enhancement", ModifierCategoryTooltip::modifyEnhancementTooltip),
      CRAFTED("Crafted", ModifierCategoryTooltip::modifyCraftedTooltip),
      FROZEN("Frozen", ModifierCategoryTooltip::modifyFrozenTooltip);

      private final String descriptor;
      private final Function<MutableComponent, MutableComponent> modifierFormatter;
      @Nullable
      private final ResourceLocation overlayIcon;

      private AffixCategory() {
         this("");
      }

      private AffixCategory(String descriptor) {
         this(descriptor, Function.identity());
      }

      private AffixCategory(String descriptor, Function<MutableComponent, MutableComponent> modifierFormatter) {
         this(descriptor, modifierFormatter, null);
      }

      private AffixCategory(String descriptor, Function<MutableComponent, MutableComponent> modifierFormatter, @Nullable ResourceLocation overlayIcon) {
         this.descriptor = descriptor;
         this.modifierFormatter = modifierFormatter;
         this.overlayIcon = overlayIcon;
      }

      @Nonnull
      public String getTooltipDescriptor() {
         return this.descriptor;
      }

      public Function<MutableComponent, MutableComponent> getModifierFormatter() {
         return this.modifierFormatter;
      }

      public boolean cannotBeModifiedByArtisanFoci() {
         return this == ABYSSAL || this == ABILITY_ENHANCEMENT || !this.canBeModified();
      }

      public boolean canBeModified() {
         return this != FROZEN;
      }

      public boolean cannotBeRolledByArtisanFoci() {
         return this == CRAFTED;
      }

      @Nullable
      public ResourceLocation getOverlayIcon() {
         return this.overlayIcon;
      }
   }

   public static class AffixCategorySet extends TreeSet<VaultGearModifier.AffixCategory> {
      public void write(BitBuffer buf) {
         buf.writeInt(this.size());
         this.forEach(cat -> buf.writeInt(cat.ordinal()));
      }

      public static VaultGearModifier.AffixCategorySet read(BitBuffer buf, GearDataVersion version) {
         VaultGearModifier.AffixCategorySet set = new VaultGearModifier.AffixCategorySet();
         if (GearDataVersion.V0_3.isLaterThan(version)) {
            if (buf.readBoolean()) {
               set.add(VaultGearModifier.AffixCategory.LEGENDARY);
            }
         } else if (GearDataVersion.V0_5.isLaterThan(version)) {
            VaultGearModifier.AffixCategory cat = MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, buf.readInt());
            set.add(cat);
         } else {
            int size = buf.readInt();

            for (int i = 0; i < size; i++) {
               set.add(MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, buf.readInt()));
            }
         }

         return set;
      }

      public void writeNbt(CompoundTag tag) {
         ListTag list = new ListTag();
         this.forEach(cat -> list.add(IntTag.valueOf(cat.ordinal())));
         tag.put("categories", list);
      }

      public static VaultGearModifier.AffixCategorySet readNbt(CompoundTag tag, GearDataVersion version) {
         VaultGearModifier.AffixCategorySet set = new VaultGearModifier.AffixCategorySet();
         if (tag.contains("legendary", 1)) {
            if (tag.getBoolean("legendary")) {
               set.add(VaultGearModifier.AffixCategory.LEGENDARY);
            }
         } else if (tag.contains("category", 3)) {
            VaultGearModifier.AffixCategory cat = MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, tag.getInt("category"));
            set.add(cat);
         } else {
            ListTag list = tag.getList("categories", 3);

            for (int i = 0; i < list.size(); i++) {
               set.add(MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, list.getInt(i)));
            }
         }

         return set;
      }
   }

   public static enum AffixType {
      IMPLICIT("Implicits", "Implicit", isPositive -> ""),
      PREFIX("Prefixes", "Prefix", isPositive -> isPositive ? "+" : ""),
      SUFFIX("Suffixes", "Suffix", isPositive -> isPositive ? "+" : "");

      private static final VaultGearModifier.AffixType[] EXPLICITS = new VaultGearModifier.AffixType[]{PREFIX, SUFFIX};
      private final String plural;
      private final String singular;
      private final Function<Boolean, String> affixPrefix;

      private AffixType(String plural, String singular, Function<Boolean, String> affixPrefix) {
         this.plural = plural;
         this.singular = singular;
         this.affixPrefix = affixPrefix;
      }

      public String getPlural() {
         return this.plural;
      }

      public String getSingular() {
         return this.singular;
      }

      public String getAffixPrefix(boolean isPositive) {
         return this.affixPrefix.apply(isPositive);
      }

      public MutableComponent getAffixPrefixComponent(boolean isPositive) {
         return new TextComponent(this.getAffixPrefix(isPositive));
      }

      public static VaultGearModifier.AffixType[] explicits() {
         return EXPLICITS;
      }
   }
}
