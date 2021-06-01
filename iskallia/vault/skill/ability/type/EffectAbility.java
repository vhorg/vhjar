package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;

public class EffectAbility extends PlayerAbility {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;
   @Expose
   private final String type;

   public EffectAbility(int cost, Effect effect, int amplifier, EffectAbility.Type type) {
      this(cost, Registry.field_212631_t.func_177774_c(effect).toString(), amplifier, type.toString());
   }

   public EffectAbility(int cost, Effect effect, int amplifier, EffectAbility.Type type, PlayerAbility.Behavior behavior) {
      this(cost, Registry.field_212631_t.func_177774_c(effect).toString(), amplifier, type.toString(), behavior);
   }

   public EffectAbility(int cost, String effect, int amplifier, String type) {
      super(cost, PlayerAbility.Behavior.PRESS_TO_TOGGLE);
      this.effect = effect;
      this.amplifier = amplifier;
      this.type = type;
   }

   public EffectAbility(int cost, String effect, int amplifier, String type, PlayerAbility.Behavior behavior) {
      super(cost, behavior);
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

   @Override
   public void onRemoved(PlayerEntity player) {
      player.func_195063_d(this.getEffect());
   }

   @Override
   public void onBlur(PlayerEntity player) {
      player.func_195063_d(this.getEffect());
   }

   @Override
   public void onTick(PlayerEntity player, boolean active) {
      if (!active) {
         player.func_195063_d(this.getEffect());
      } else {
         EffectInstance activeEffect = player.func_70660_b(this.getEffect());
         EffectInstance newEffect = new EffectInstance(
            this.getEffect(), Integer.MAX_VALUE, this.getAmplifier(), false, this.getType().showParticles, this.getType().showIcon
         );
         if (activeEffect == null) {
            player.func_195064_c(newEffect);
         }
      }
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      if (active) {
         this.playEffects(player);
      }
   }

   public void playEffects(PlayerEntity player) {
      if (this.getEffect() == Effects.field_76441_p) {
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.INVISIBILITY_SFX,
               SoundCategory.MASTER,
               0.175F,
               1.0F
            );
         player.func_213823_a(ModSounds.INVISIBILITY_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
      } else if (this.getEffect() == Effects.field_76439_r) {
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.NIGHT_VISION_SFX,
               SoundCategory.MASTER,
               0.0375F,
               1.0F
            );
         player.func_213823_a(ModSounds.NIGHT_VISION_SFX, SoundCategory.MASTER, 0.15F, 1.0F);
      }
   }

   public static enum Type {
      HIDDEN("hidden", false, false),
      PARTICLES_ONLY("particles_only", true, false),
      ICON_ONLY("icon_only", false, true),
      ALL("all", true, true);

      private static Map<String, EffectAbility.Type> STRING_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EffectAbility.Type::toString, o -> (EffectAbility.Type)o));
      private final String name;
      public final boolean showParticles;
      public final boolean showIcon;

      private Type(String name, boolean showParticles, boolean showIcon) {
         this.name = name;
         this.showParticles = showParticles;
         this.showIcon = showIcon;
      }

      public static EffectAbility.Type fromString(String type) {
         return STRING_TO_TYPE.get(type);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
