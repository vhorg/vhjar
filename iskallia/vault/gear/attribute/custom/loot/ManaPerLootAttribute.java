package iskallia.vault.gear.attribute.custom.loot;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.FloatRollRangeEntry;
import iskallia.vault.config.entry.IntRollRangeEntry;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.mana.Mana;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ManaPerLootAttribute extends LootTriggerAttribute {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
   private final float manaGenerationChance;
   private final int manaGenerated;

   public ManaPerLootAttribute(ResourceLocation tileGroupId, String displayName, float manaGenerationChance, int manaGenerated) {
      super(tileGroupId, displayName);
      this.manaGenerationChance = manaGenerationChance;
      this.manaGenerated = manaGenerated;
   }

   public float getManaGenerationChance() {
      return this.manaGenerationChance;
   }

   public int getManaGenerated() {
      return this.manaGenerated;
   }

   @Override
   public void trigger(BlockEntity tile, RandomSource random, ServerPlayer player) {
      if (random.nextFloat() < this.getManaGenerationChance()) {
         Mana.increase(player, ManaAction.PLAYER_ACTION, this.getManaGenerated());
      }
   }

   @Override
   public String toString() {
      return "ManaPerLootAttribute{tileGroupId="
         + this.getTileGroupId()
         + ", displayName='"
         + this.getDisplayName()
         + "', manaGenerationChance="
         + this.manaGenerationChance
         + ", manaGenerated="
         + this.manaGenerated
         + "}";
   }

   public static VaultGearAttributeType<ManaPerLootAttribute> type() {
      return VaultGearAttributeType.of(
         (buf, attribute) -> {
            buf.writeIdentifier(attribute.getTileGroupId());
            buf.writeString(attribute.getDisplayName());
            buf.writeFloat(attribute.getManaGenerationChance());
            buf.writeInt(attribute.getManaGenerated());
         },
         buf -> new ManaPerLootAttribute(buf.readIdentifier(), buf.readString(), buf.readFloat(), buf.readInt()),
         (buf, attribute) -> {
            NetcodeUtils.writeIdentifier(buf, attribute.getTileGroupId());
            NetcodeUtils.writeString(buf, attribute.getDisplayName());
            buf.writeFloat(attribute.getManaGenerationChance());
            buf.writeInt(attribute.getManaGenerated());
         },
         buf -> new ManaPerLootAttribute(NetcodeUtils.readIdentifier(buf), NetcodeUtils.readString(buf), buf.readFloat(), buf.readInt()),
         VaultGearAttributeType.GSON::toJsonTree,
         tag -> {
            CompoundTag compoundTag = (CompoundTag)tag;
            ResourceLocation tileGroupId = new ResourceLocation(compoundTag.getString("tileGroupId"));
            String displayName = compoundTag.getString("displayName");
            float manaGenerationChance = compoundTag.getFloat("manaGenerationChance");
            int manaGenerated = compoundTag.getInt("manaGenerated");
            return new ManaPerLootAttribute(tileGroupId, displayName, manaGenerationChance, manaGenerated);
         },
         attribute -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("tileGroupId", attribute.getTileGroupId().toString());
            compoundTag.putString("displayName", attribute.getDisplayName());
            compoundTag.putFloat("manaGenerationChance", attribute.getManaGenerationChance());
            compoundTag.putInt("manaGenerated", attribute.getManaGenerated());
            return compoundTag;
         }
      );
   }

   public static ManaPerLootAttribute.AttributeComparator comparator() {
      return new ManaPerLootAttribute.AttributeComparator();
   }

   public static ManaPerLootAttribute.Generator generator() {
      return new ManaPerLootAttribute.Generator();
   }

   public static ManaPerLootAttribute.Reader reader() {
      return new ManaPerLootAttribute.Reader();
   }

   private static class AttributeComparator extends VaultGearAttributeComparator<ManaPerLootAttribute> {
      public Optional<ManaPerLootAttribute> merge(ManaPerLootAttribute thisValue, ManaPerLootAttribute thatValue) {
         if (!Objects.equals(thisValue.getTileGroupId(), thatValue.getTileGroupId())) {
            return Optional.empty();
         } else {
            return thisValue.getManaGenerated() != thatValue.getManaGenerated()
               ? Optional.empty()
               : Optional.of(
                  new ManaPerLootAttribute(
                     thisValue.getTileGroupId(),
                     thisValue.getDisplayName(),
                     thisValue.getManaGenerationChance() + thatValue.getManaGenerationChance(),
                     thisValue.getManaGenerated()
                  )
               );
         }
      }

      public Optional<ManaPerLootAttribute> difference(ManaPerLootAttribute thisValue, ManaPerLootAttribute thatValue) {
         return Optional.empty();
      }

      @Nonnull
      @Override
      public Comparator<ManaPerLootAttribute> getComparator() {
         return Comparator.comparing(ManaPerLootAttribute::getManaGenerated).thenComparing(ManaPerLootAttribute::getManaGenerationChance);
      }
   }

   public static class Config extends LootTriggerAttribute.Config {
      @Expose
      private final FloatRollRangeEntry manaGenerationChance;
      @Expose
      private final IntRollRangeEntry manaGenerated;

      public Config(ResourceLocation tileEntityGroupId, String displayName, FloatRollRangeEntry manaGenerationChance, IntRollRangeEntry manaGenerated) {
         super(tileEntityGroupId, displayName);
         this.manaGenerationChance = manaGenerationChance;
         this.manaGenerated = manaGenerated;
      }

      public FloatRollRangeEntry getManaGenerationChance() {
         return this.manaGenerationChance;
      }

      public IntRollRangeEntry getManaGenerated() {
         return this.manaGenerated;
      }
   }

   public static class Generator extends ConfigurableAttributeGenerator<ManaPerLootAttribute, ManaPerLootAttribute.Config> {
      @Nullable
      @Override
      public Class<ManaPerLootAttribute.Config> getConfigurationObjectClass() {
         return ManaPerLootAttribute.Config.class;
      }

      @Nullable
      public MutableComponent getConfigRangeDisplay(
         VaultGearModifierReader<ManaPerLootAttribute> reader, ManaPerLootAttribute.Config min, ManaPerLootAttribute.Config max
      ) {
         return this.getChanceDisplay(min.getManaGenerationChance().getMin())
            .append("-")
            .append(this.getChanceDisplay(max.getManaGenerationChance().getMax()))
            .append(", ")
            .append(String.valueOf(min.getManaGenerated().getMin()))
            .append("-")
            .append(String.valueOf(max.getManaGenerated().getMax()));
      }

      private MutableComponent getChanceDisplay(float value) {
         return new TextComponent(ManaPerLootAttribute.FORMAT.format(value * 100.0F) + "%");
      }

      @Nullable
      public MutableComponent getConfigDisplay(VaultGearModifierReader<ManaPerLootAttribute> reader, ManaPerLootAttribute.Config object) {
         MutableComponent range = this.getConfigRangeDisplay(reader, object);
         MutableComponent display = new TextComponent(object.getDisplayName());
         return new TextComponent("")
            .withStyle(reader.getColoredTextStyle())
            .append(range.withStyle(reader.getColoredTextStyle()))
            .append(" Mana per ")
            .append(display)
            .append(" looted");
      }

      public ManaPerLootAttribute generateRandomValue(ManaPerLootAttribute.Config object, Random random) {
         JavaRandom rand = JavaRandom.ofScrambled(random.nextLong());
         float genChance = object.getManaGenerationChance().getRandom(rand);
         int genAmount = object.getManaGenerated().getRandom(rand);
         return new ManaPerLootAttribute(object.getTileEntityGroupId(), object.getDisplayName(), genChance, genAmount);
      }

      @Override
      public Optional<ManaPerLootAttribute> getMinimumValue(List<ManaPerLootAttribute.Config> configurations) {
         Comparator<ManaPerLootAttribute.Config> cfgCmp = Comparator.comparing(config -> config.getManaGenerated().getMin());
         cfgCmp = cfgCmp.thenComparing(config -> config.getManaGenerationChance().getMin());
         return configurations.stream()
            .min(cfgCmp)
            .map(
               config -> new ManaPerLootAttribute(
                  config.getTileEntityGroupId(), config.getDisplayName(), config.getManaGenerationChance().getMin(), config.getManaGenerated().getMin()
               )
            );
      }

      @Override
      public Optional<ManaPerLootAttribute> getMaximumValue(List<ManaPerLootAttribute.Config> configurations) {
         Comparator<ManaPerLootAttribute.Config> cfgCmp = Comparator.comparing(config -> config.getManaGenerated().getRolledMaximum());
         cfgCmp = cfgCmp.thenComparing(config -> config.getManaGenerationChance().getRolledMaximum());
         return configurations.stream()
            .max(cfgCmp)
            .map(
               config -> new ManaPerLootAttribute(
                  config.getTileEntityGroupId(),
                  config.getDisplayName(),
                  config.getManaGenerationChance().getRolledMaximum(),
                  config.getManaGenerated().getRolledMaximum()
               )
            );
      }

      public Optional<Float> getRollPercentage(ManaPerLootAttribute value, List<ManaPerLootAttribute.Config> configurations) {
         return MiscUtils.getFloatValueRange(
            value.getManaGenerationChance(),
            this.getMinimumValue(configurations),
            this.getMaximumValue(configurations),
            ManaPerLootAttribute::getManaGenerationChance
         );
      }
   }

   private static class Reader extends VaultGearModifierReader<ManaPerLootAttribute> {
      protected Reader() {
         super("", 65535);
      }

      private Style getHighlightStyle() {
         return Style.EMPTY.withColor(20479);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<ManaPerLootAttribute> instance, VaultGearModifier.AffixType type) {
         ManaPerLootAttribute manaLootAttr = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(manaLootAttr);
         if (valueDisplay == null) {
            return null;
         } else {
            MutableComponent manaGenerated = new TextComponent(String.valueOf(manaLootAttr.getManaGenerated()));
            return new TextComponent(type.getAffixPrefix(manaLootAttr.getManaGenerationChance() >= 0.0F))
               .withStyle(this.getColoredTextStyle())
               .append(valueDisplay.withStyle(this.getHighlightStyle()))
               .append(new TextComponent(" chance to generate ").withStyle(this.getColoredTextStyle()))
               .append(manaGenerated.withStyle(this.getHighlightStyle()))
               .append(new TextComponent(" Mana per ").withStyle(this.getColoredTextStyle()))
               .append(new TextComponent(manaLootAttr.getDisplayName()).withStyle(this.getHighlightStyle()))
               .append(new TextComponent(" looted").withStyle(this.getColoredTextStyle()));
         }
      }

      @Nullable
      public MutableComponent getValueDisplay(ManaPerLootAttribute value) {
         return new TextComponent(ManaPerLootAttribute.FORMAT.format(value.getManaGenerationChance() * 100.0F) + "%");
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<ManaPerLootAttribute> instance, VaultGearModifier.AffixType type) {
         ManaPerLootAttribute manaLootAttr = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(manaLootAttr);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(manaLootAttr.getManaGenerationChance() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add(" chance to generate ");
            out.add(String.valueOf(manaLootAttr.getManaGenerated()));
            out.add(" Mana per ");
            out.add(manaLootAttr.getDisplayName());
            out.add(" looted");
         }
      }
   }
}
