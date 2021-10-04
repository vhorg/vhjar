package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EffectConfig extends AbilityConfig {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;
   @Expose
   private final String type;

   public EffectConfig(int cost, Effect effect, int amplifier, EffectConfig.Type type, AbilityConfig.Behavior behavior) {
      this(cost, Registry.field_212631_t.func_177774_c(effect).toString(), amplifier, type.toString(), behavior);
   }

   public EffectConfig(int cost, Effect effect, int amplifier, EffectConfig.Type type, AbilityConfig.Behavior behavior, int cooldown) {
      this(cost, Registry.field_212631_t.func_177774_c(effect).toString(), amplifier, type.toString(), behavior, cooldown);
   }

   public EffectConfig(int cost, String effect, int amplifier, String type, AbilityConfig.Behavior behavior) {
      this(cost, effect, amplifier, type, behavior, 200);
   }

   public EffectConfig(int cost, String effect, int amplifier, String type, AbilityConfig.Behavior behavior, int cooldown) {
      super(cost, behavior, cooldown);
      this.effect = effect;
      this.amplifier = amplifier;
      this.type = type;
   }

   public Effect getEffect() {
      return (Effect)Registry.field_212631_t.func_82594_a(new ResourceLocation(this.effect));
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public EffectTalent.Type getType() {
      return EffectTalent.Type.fromString(this.type);
   }

   public static enum Type {
      HIDDEN("hidden", false, false),
      PARTICLES_ONLY("particles_only", true, false),
      ICON_ONLY("icon_only", false, true),
      ALL("all", true, true);

      private static final Map<String, EffectConfig.Type> STRING_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EffectConfig.Type::toString, o -> (EffectConfig.Type)o));
      private final String name;
      public final boolean showParticles;
      public final boolean showIcon;

      private Type(String name, boolean showParticles, boolean showIcon) {
         this.name = name;
         this.showParticles = showParticles;
         this.showIcon = showIcon;
      }

      public static EffectConfig.Type fromString(String type) {
         return STRING_TO_TYPE.get(type);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
