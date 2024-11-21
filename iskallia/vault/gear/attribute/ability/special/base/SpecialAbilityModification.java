package iskallia.vault.gear.attribute.ability.special.base;

import com.google.gson.JsonArray;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class SpecialAbilityModification<C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue> {
   protected static final DecimalFormat FORMAT = new DecimalFormat("0.##");
   private final ResourceLocation key;

   protected SpecialAbilityModification(ResourceLocation key) {
      this.key = key;
   }

   public final ResourceLocation getKey() {
      return this.key;
   }

   protected <T extends SpecialAbilityModification<C, V>> SpecialAbilityGearAttribute<T, V> of(String abilityKey, T modification, V value) {
      return new SpecialAbilityGearAttribute<>(abilityKey, modification, value);
   }

   public static <M extends SpecialAbilityModification<C, V>, C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue> List<ConfiguredModification<M, C, V>> getModifications(
      LivingEntity entity, Class<M> modClass
   ) {
      List<ConfiguredModification<M, C, V>> modifications = new ArrayList<>();
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);

      for (SpecialAbilityGearAttribute<?, ?> attribute : snapshot.getAttributeValueList(ModGearAttributes.ABILITY_SPECIAL_MODIFICATION)) {
         if (modClass.isInstance(attribute.getModification())) {
            modifications.add(new ConfiguredModification<>((M)attribute.getModification(), (V)attribute.getValue()));
         }
      }

      return modifications;
   }

   public abstract Class<C> getConfigClass();

   public abstract Function<BitBuffer, V> readValue();

   public abstract Function<ByteBuf, V> netReadValue();

   public abstract Function<Tag, V> nbtReadValue();

   @Nullable
   public abstract MutableComponent getDisplay(SpecialAbilityGearAttribute<?, V> var1, Style var2, VaultGearModifier.AffixType var3);

   @Nullable
   public abstract MutableComponent getValueDisplay(V var1);

   public abstract void serializeTextElements(JsonArray var1, SpecialAbilityGearAttribute<?, V> var2, VaultGearModifier.AffixType var3);

   @Nullable
   public <T extends SpecialAbilityModification<C, V>> MutableComponent getConfigRangeDisplay(
      VaultGearModifierReader<SpecialAbilityGearAttribute<T, V>> reader,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V> min,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V> max
   ) {
      return null;
   }

   public <T extends SpecialAbilityModification<C, V>> Optional<SpecialAbilityGearAttribute<T, V>> getMinimumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations
   ) {
      return Optional.empty();
   }

   public <T extends SpecialAbilityModification<C, V>> Optional<SpecialAbilityGearAttribute<T, V>> getMaximumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations
   ) {
      return Optional.empty();
   }

   public <T extends SpecialAbilityModification<C, V>> Optional<Float> getRollPercentage(
      SpecialAbilityGearAttribute<T, V> value, List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations
   ) {
      return Optional.empty();
   }
}
