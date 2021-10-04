package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.EffectConfig;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.RomanNumber;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EffectModifier extends TexturedVaultModifier {
   @Expose
   private final String effect;
   @Expose
   private final int value;
   @Expose
   private final String operator;
   @Expose
   private final String type;

   public EffectModifier(String name, ResourceLocation icon, Effect effect, int value, String operator, EffectConfig.Type type) {
      this(name, icon, Registry.field_212631_t.func_177774_c(effect).toString(), value, operator, type.toString());
   }

   public EffectModifier(String name, ResourceLocation icon, String effect, int value, String operator, String type) {
      super(name, icon);
      this.effect = effect;
      this.value = value;
      this.operator = operator;
      this.type = type;
      if (this.operator.equals("MULTIPLY")) {
         this.format(this.getColor(), "Multiples the current " + new ResourceLocation(this.effect).func_110623_a() + " amplifier by " + this.value + ".");
      } else if (this.operator.equals("ADD")) {
         this.format(this.getColor(), "Adds " + this.value + " to the current " + new ResourceLocation(this.effect).func_110623_a() + " amplifier.");
      } else if (this.operator.equals("SET")) {
         this.format(this.getColor(), "Gives " + new ResourceLocation(this.effect).func_110623_a() + " " + RomanNumber.toRoman(this.value) + ".");
      } else {
         this.format(this.getColor(), "Does absolutely nothing. Whoever wrote this config made a mistake...");
      }
   }

   public Effect getEffect() {
      return (Effect)Registry.field_212631_t.func_82594_a(new ResourceLocation(this.effect));
   }

   public int getAmplifier() {
      return this.value;
   }

   public String getOperator() {
      return this.operator;
   }

   public EffectTalent.Type getType() {
      return EffectTalent.Type.fromString(this.type);
   }

   public EffectTalent makeTalent() {
      EffectTalent.Operator operator = this.getOperator().equals("SET") ? EffectTalent.Operator.SET : EffectTalent.Operator.ADD;
      return new EffectTalent(0, this.getEffect(), this.getAmplifier(), this.getType(), operator);
   }

   public static enum Type {
      HIDDEN("hidden", false, false),
      PARTICLES_ONLY("particles_only", true, false),
      ICON_ONLY("icon_only", false, true),
      ALL("all", true, true);

      private static Map<String, EffectModifier.Type> STRING_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EffectModifier.Type::toString, o -> (EffectModifier.Type)o));
      private final String name;
      public final boolean showParticles;
      public final boolean showIcon;

      private Type(String name, boolean showParticles, boolean showIcon) {
         this.name = name;
         this.showParticles = showParticles;
         this.showIcon = showIcon;
      }

      public static EffectModifier.Type fromString(String type) {
         return STRING_TO_TYPE.get(type);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
