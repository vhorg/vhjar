package iskallia.vault.gear.attribute;

import com.google.gson.JsonObject;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.tooltip.ModifierCategoryTooltip;
import iskallia.vault.util.MiscUtils;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
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
   private VaultGearModifier.AffixCategory category = VaultGearModifier.AffixCategory.NONE;

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

   public void setCategory(@Nonnull VaultGearModifier.AffixCategory category) {
      this.category = category;
   }

   @Nonnull
   public VaultGearModifier.AffixCategory getCategory() {
      return this.category;
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

   @OnlyIn(Dist.CLIENT)
   @Override
   public Optional<MutableComponent> getDisplay(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, boolean displayDetail) {
      return super.getDisplay(data, type, stack, displayDetail).map(this.category.getModifierFormatter()).map(displayText -> {
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
      }).map(displayText -> {
         if (!displayDetail) {
            return (MutableComponent)displayText;
         } else {
            MutableComponent tierDisplay = this.getConfigRangeDisplay(stack).orElse(new TextComponent(""));
            Style txtStyle = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false).withUnderlined(false).withBold(false);
            String categoryInfo = this.getCategory().getTooltipDescriptor();
            if (tierDisplay.getString().isEmpty()) {
               displayText.append(new TextComponent(" (%sT%s)".formatted(categoryInfo, this.getRolledTier() + 1)).setStyle(txtStyle));
            } else {
               displayText.append(new TextComponent(" (%sT%s: ".formatted(categoryInfo, this.getRolledTier() + 1)).setStyle(txtStyle));
               displayText.append(tierDisplay.setStyle(txtStyle));
               displayText.append(new TextComponent(")").setStyle(txtStyle));
            }

            return (MutableComponent)displayText;
         }
      });
   }

   public <V> Optional<V> withModifierConfig(ItemStack stack, Function<Object, V> configFn) {
      return VaultGearTierConfig.getConfig(stack.getItem()).map(tierCfg -> tierCfg.getTierConfig(this)).map(configFn);
   }

   public Optional<MutableComponent> getConfigRangeDisplay(ItemStack stack) {
      return this.withModifierConfig(stack, cfg -> {
         ConfigurableAttributeGenerator configGen = this.getAttribute().getGenerator();
         return configGen.getConfigRangeDisplay(this.getAttribute().getReader(), cfg);
      });
   }

   public Optional<MutableComponent> getConfigDisplay(ItemStack stack) {
      return this.withModifierConfig(stack, cfg -> {
         ConfigurableAttributeGenerator configGen = this.getAttribute().getGenerator();
         return configGen.getConfigDisplay(this.getAttribute().getReader(), cfg);
      });
   }

   @Override
   public void write(BitBuffer buf) {
      super.write(buf);
      buf.writeInt(this.rolledTier);
      this.category.write(buf);
      buf.writeString(this.modifierGroup);
      buf.writeNullable(this.modifierIdentifier, BitBuffer::writeIdentifier);
      buf.writeLong(this.gameTimeAdded);
   }

   @Override
   public void read(BitBuffer buf, GearDataVersion version) {
      super.read(buf, version);
      this.rolledTier = buf.readInt();
      this.category = VaultGearModifier.AffixCategory.read(buf, version);
      this.modifierGroup = buf.readString();
      this.modifierIdentifier = buf.readNullable(BitBuffer::readIdentifier);
      this.gameTimeAdded = buf.readLong();
   }

   @Override
   public void toNbt(CompoundTag tag) {
      super.toNbt(tag);
      tag.putInt("rolledTier", this.rolledTier);
      this.category.writeNbt(tag);
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
      this.category = VaultGearModifier.AffixCategory.readNbt(tag, version);
      this.modifierGroup = tag.getString("modifierGroup");
      if (tag.contains("modifierIdentifier", 8)) {
         this.modifierIdentifier = new ResourceLocation(tag.getString("modifierIdentifier"));
      }

      this.gameTimeAdded = tag.getLong("gameTimeAdded");
   }

   @Override
   public JsonObject serialize(VaultGearModifier.AffixType type) {
      JsonObject obj = super.serialize(type);
      obj.addProperty("legendary", this.category == VaultGearModifier.AffixCategory.LEGENDARY);
      obj.addProperty("category", this.category.name());
      return obj;
   }

   public static enum AffixCategory {
      NONE,
      LEGENDARY("Legendary", ModifierCategoryTooltip::modifyLegendaryTooltip),
      ABYSSAL("Abyssal", ModifierCategoryTooltip::modifyAbyssalTooltip),
      ABILITY_ENHANCEMENT("Enhancement", ModifierCategoryTooltip::modifyEnhancementTooltip),
      CRAFTED("Crafted", ModifierCategoryTooltip::modifyCraftedTooltip);

      private final String descriptor;
      private final Function<MutableComponent, MutableComponent> modifierFormatter;

      private AffixCategory() {
         this("");
      }

      private AffixCategory(String descriptor) {
         this(descriptor, Function.identity());
      }

      private AffixCategory(String descriptor, Function<MutableComponent, MutableComponent> modifierFormatter) {
         this.descriptor = descriptor.isEmpty() ? descriptor : descriptor + " ";
         this.modifierFormatter = modifierFormatter;
      }

      @Nonnull
      public String getTooltipDescriptor() {
         return this.descriptor;
      }

      public Function<MutableComponent, MutableComponent> getModifierFormatter() {
         return this.modifierFormatter;
      }

      public boolean isModifiableByArtisanFoci() {
         return this != ABYSSAL && this != ABILITY_ENHANCEMENT;
      }

      public void write(BitBuffer buf) {
         buf.writeInt(this.ordinal());
      }

      public static VaultGearModifier.AffixCategory read(BitBuffer buf, GearDataVersion version) {
         if (GearDataVersion.V0_3.isLaterThan(version)) {
            return buf.readBoolean() ? LEGENDARY : NONE;
         } else {
            return MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, buf.readInt());
         }
      }

      public void writeNbt(CompoundTag tag) {
         tag.putInt("category", this.ordinal());
      }

      public static VaultGearModifier.AffixCategory readNbt(CompoundTag tag, GearDataVersion version) {
         if (tag.contains("legendary", 1)) {
            return tag.getBoolean("legendary") ? LEGENDARY : NONE;
         } else {
            return MiscUtils.getEnumEntry(VaultGearModifier.AffixCategory.class, tag.getInt("category"));
         }
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
