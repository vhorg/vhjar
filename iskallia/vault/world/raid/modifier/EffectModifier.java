package iskallia.vault.world.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.type.EffectAbility;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.RomanNumber;
import iskallia.vault.world.raid.VaultRaid;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class EffectModifier extends VaultModifier {
   @Expose
   private final String effect;
   @Expose
   private final int value;
   @Expose
   private final String operator;
   @Expose
   private final String type;

   public EffectModifier(String name, ResourceLocation icon, Effect effect, int value, String operator, EffectAbility.Type type) {
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

   public int getAmplifier(int oldValue) {
      if (this.operator.equals("MULTIPLY")) {
         return this.value * oldValue;
      } else if (this.operator.equals("ADD")) {
         return this.value + oldValue;
      } else {
         return this.operator.equals("SET") ? this.value : oldValue;
      }
   }

   public EffectTalent.Type getType() {
      return EffectTalent.Type.fromString(this.type);
   }

   @Override
   public void apply(VaultRaid raid) {
   }

   @Override
   public void tick(ServerWorld world, PlayerEntity player) {
      Tuple<Integer, EffectTalent> data = EffectTalent.getData(player, world, this.getEffect());
      if ((Integer)data.func_76341_a() >= 0) {
         EffectInstance activeEffect = player.func_70660_b(this.getEffect());
         int newAmplifier = this.getAmplifier((Integer)data.func_76341_a());
         if (newAmplifier >= 0) {
            EffectInstance newEffect = new EffectInstance(
               this.getEffect(),
               100,
               newAmplifier,
               false,
               ((EffectTalent)data.func_76340_b()).getType().showParticles,
               ((EffectTalent)data.func_76340_b()).getType().showIcon
            );
            player.func_195064_c(newEffect);
         } else if (activeEffect != null) {
            player.func_195063_d(activeEffect.func_188419_a());
         }
      }
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
