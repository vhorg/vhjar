package iskallia.vault.item.catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.config.VaultCrystalCatalystConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.EnumCodec;
import iskallia.vault.world.vault.modifier.VaultModifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifierRollResult {
   public static final Codec<ModifierRollResult> CODEC = RecordCodecBuilder.create(
      rollInstance -> rollInstance.group(
            EnumCodec.of(ModifierRollType.class).fieldOf("type").forGetter(outcome -> outcome.type),
            Codec.STRING.fieldOf("value").forGetter(outcome -> outcome.value)
         )
         .apply(rollInstance, ModifierRollResult::new)
   );
   private final ModifierRollType type;
   private final String value;

   private ModifierRollResult(ModifierRollType type, String value) {
      this.type = type;
      this.value = type == ModifierRollType.ADD_SPECIFIC_MODIFIER ? VaultModifier.migrateModifierName(value) : value;
   }

   public static ModifierRollResult ofModifier(String modifier) {
      return new ModifierRollResult(ModifierRollType.ADD_SPECIFIC_MODIFIER, modifier);
   }

   public static ModifierRollResult ofPool(String pool) {
      return new ModifierRollResult(ModifierRollType.ADD_RANDOM_MODIFIER, pool);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltipDescription(String prefix, boolean canAddDetail) {
      return this.getTooltipDescription(new StringTextComponent(prefix), canAddDetail);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltipDescription(IFormattableTextComponent prefix, boolean canAddDetail) {
      List<ITextComponent> description = new ArrayList<>();
      description.add(prefix.func_230529_a_(this.getDescription()));
      if (canAddDetail && Screen.func_231173_s_()) {
         IFormattableTextComponent modifierDescription = this.getModifierDescription();
         if (modifierDescription != null) {
            description.add(new StringTextComponent("   ").func_230529_a_(modifierDescription.func_240699_a_(TextFormatting.DARK_GRAY)));
         }
      }

      return description;
   }

   public ITextComponent getDescription() {
      IFormattableTextComponent name = new StringTextComponent(this.value);
      if (this.type == ModifierRollType.ADD_RANDOM_MODIFIER) {
         VaultCrystalCatalystConfig.TaggedPool pool = ModConfigs.VAULT_CRYSTAL_CATALYST.getPool(this.value);
         if (pool != null) {
            name = pool.getDisplayName();
         }
      } else {
         VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(this.value);
         if (modifier != null) {
            name.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(modifier.getColor())));
         }
      }

      return this.type.getDescription(name);
   }

   @Nullable
   public IFormattableTextComponent getModifierDescription() {
      if (this.type == ModifierRollType.ADD_SPECIFIC_MODIFIER) {
         VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(this.value);
         if (modifier != null) {
            return new StringTextComponent(modifier.getDescription());
         }
      }

      return null;
   }

   @Nullable
   public String getModifier(Random random, Predicate<String> modifierFilter) {
      if (this.type == ModifierRollType.ADD_SPECIFIC_MODIFIER) {
         if (!modifierFilter.test(this.value)) {
            return this.value;
         }
      } else {
         VaultCrystalCatalystConfig.TaggedPool pool = ModConfigs.VAULT_CRYSTAL_CATALYST.getPool(this.value);
         if (pool != null) {
            return pool.getModifier(random, modifierFilter);
         }
      }

      return null;
   }
}
