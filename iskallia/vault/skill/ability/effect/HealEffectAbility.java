package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HealEffectAbility extends AbstractHealAbility {
   private List<MobEffect> removeEffects;
   private HealEffectAbility.RemovalStrategy removalStrategy;
   private static final ArrayAdapter<MobEffect> EFFECTS = Adapters.ofArray(MobEffect[]::new, Adapters.EFFECT.asNullable());
   private static final EnumAdapter<HealEffectAbility.RemovalStrategy> STRATEGY_ORDINAL = Adapters.ofEnum(
      HealEffectAbility.RemovalStrategy.class, EnumAdapter.Mode.ORDINAL
   );
   private static final EnumAdapter<HealEffectAbility.RemovalStrategy> STRATEGY_NAME = Adapters.ofEnum(
      HealEffectAbility.RemovalStrategy.class, EnumAdapter.Mode.NAME
   );

   public HealEffectAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      List<MobEffect> removeEffects,
      HealEffectAbility.RemovalStrategy removalStrategy
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.removeEffects = removeEffects;
      this.removalStrategy = removalStrategy;
   }

   public HealEffectAbility() {
   }

   public List<MobEffect> getRemoveEffects() {
      return this.removeEffects;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         this.removalStrategy.apply(player, this);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         ((ServerLevel)player.level).sendParticles(ParticleTypes.BUBBLE_POP, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
         ((ServerLevel)player.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
      });
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.CLEANSE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.CLEANSE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      EFFECTS.writeBits(this.removeEffects.toArray(MobEffect[]::new), buffer);
      STRATEGY_ORDINAL.writeBits(this.removalStrategy, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.removeEffects = Arrays.stream(EFFECTS.readBits(buffer).orElseThrow()).collect(Collectors.toList());
      this.removeEffects.removeIf(Objects::isNull);
      this.removalStrategy = STRATEGY_ORDINAL.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         EFFECTS.writeNbt(this.removeEffects.toArray(MobEffect[]::new)).ifPresent(tag -> nbt.put("removeEffects", tag));
         STRATEGY_NAME.writeNbt(this.removalStrategy).ifPresent(tag -> nbt.put("removalStrategy", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.removeEffects = Arrays.stream(EFFECTS.readNbt(nbt.get("removeEffects")).orElse(new MobEffect[0])).collect(Collectors.toList());
      this.removeEffects.removeIf(Objects::isNull);
      this.removalStrategy = STRATEGY_NAME.readNbt(nbt.get("removalStrategy")).orElse(HealEffectAbility.RemovalStrategy.ALL_HARMFUL);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         EFFECTS.writeJson(this.removeEffects.toArray(MobEffect[]::new)).ifPresent(element -> json.add("removeEffects", element));
         STRATEGY_NAME.writeJson(this.removalStrategy).ifPresent(element -> json.add("removalStrategy", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.removeEffects = Arrays.stream(EFFECTS.readJson(json.get("removeEffects")).orElse(new MobEffect[0])).collect(Collectors.toList());
      this.removeEffects.removeIf(Objects::isNull);
      this.removalStrategy = STRATEGY_NAME.readJson(json.get("removalStrategy")).orElse(HealEffectAbility.RemovalStrategy.ALL_HARMFUL);
   }

   @FunctionalInterface
   private interface IRemovalStrategy {
      void apply(ServerPlayer var1, HealEffectAbility var2);
   }

   public static enum RemovalStrategy implements HealEffectAbility.IRemovalStrategy {
      DEFINED_ONLY((player, config) -> config.getRemoveEffects().forEach(player::removeEffect)),
      ALL_HARMFUL(
         (player, config) -> player.getActiveEffects()
            .stream()
            .filter(instance -> instance.getEffect().getCategory() == MobEffectCategory.HARMFUL)
            .forEach(instance -> player.removeEffect(instance.getEffect()))
      );

      private final HealEffectAbility.IRemovalStrategy removalStrategy;

      private RemovalStrategy(HealEffectAbility.IRemovalStrategy removalStrategy) {
         this.removalStrategy = removalStrategy;
      }

      @Override
      public void apply(ServerPlayer player, HealEffectAbility config) {
         this.removalStrategy.apply(player, config);
      }
   }
}
