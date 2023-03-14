package iskallia.vault.gear.attribute.ability.special.base;

import com.google.gson.JsonArray;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class SpecialAbilityModification<C extends SpecialAbilityModification.Config<C>> {
   protected static final DecimalFormat FORMAT = new DecimalFormat("0.##");
   private final ResourceLocation key;

   protected SpecialAbilityModification(ResourceLocation key) {
      this.key = key;
   }

   public ResourceLocation getKey() {
      return this.key;
   }

   public static <C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>> List<ConfiguredModification<C, T>> getModifications(
      LivingEntity entity, Class<T> modClass
   ) {
      List<ConfiguredModification<C, T>> modifications = new ArrayList<>();
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);

      for (SpecialAbilityGearAttribute<?, ?> attribute : snapshot.getAttributeValueList(ModGearAttributes.ABILITY_SPECIAL_MODIFICATION)) {
         if (modClass.isInstance(attribute.getModification())) {
            modifications.add(new ConfiguredModification<>((C)attribute.getModificationConfig(), (T)attribute.getModification()));
         }
      }

      return modifications;
   }

   public abstract Class<C> getConfigClass();

   public abstract C read(BitBuffer var1);

   public abstract C netRead(ByteBuf var1);

   public abstract C nbtRead(Tag var1);

   @Nullable
   public abstract MutableComponent getDisplay(C var1, Style var2, VaultGearModifier.AffixType var3);

   public static Style getAbilityStyle() {
      return Style.EMPTY.withColor(14076214);
   }

   public static Style getValueStyle() {
      return Style.EMPTY.withColor(6082075);
   }

   @Nullable
   public abstract MutableComponent getValueDisplay(C var1);

   public abstract void serializeTextElements(JsonArray var1, C var2, VaultGearModifier.AffixType var3);

   public abstract static class Config<C extends SpecialAbilityModification.Config<C>> {
      public abstract void write(BitBuffer var1, C var2);

      public abstract void netWrite(ByteBuf var1, C var2);

      public abstract Tag nbtWrite(C var1);
   }
}
