package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultEntitiesConfig extends Config {
   @Expose
   private final List<VaultEntitiesConfig.DeathEffect> deathEffects = new ArrayList<>();
   @Expose
   private final List<VaultEntitiesConfig.ThrowEffect> throwEffects = new ArrayList<>();
   @Expose
   private final VaultEntitiesConfig.AuraEffect shiverEffect = new VaultEntitiesConfig.AuraEffect();
   @Expose
   private final VaultEntitiesConfig.AuraEffect swampZombieEffect = new VaultEntitiesConfig.AuraEffect();

   public List<EffectCloudAttribute.CloudConfig> getDeathEffects(int level, Entity entity) {
      List<EffectCloudAttribute.CloudConfig> effects = new ArrayList<>();

      for (VaultEntitiesConfig.DeathEffect deathEffect : this.deathEffects) {
         if (deathEffect.getFilter().test(entity)) {
            effects.addAll(
               deathEffect.getLevels()
                  .getForLevel(level)
                  .map(VaultEntitiesConfig.DeathEffect.Level::getConfig)
                  .map(Collections::singletonList)
                  .orElse(Collections.emptyList())
            );
         }
      }

      return effects;
   }

   public List<MobEffectInstance> getThrowEffects(int level, Entity entity) {
      List<MobEffectInstance> effects = new ArrayList<>();

      for (VaultEntitiesConfig.ThrowEffect throwEffect : this.throwEffects) {
         if (throwEffect.getFilter().test(entity)) {
            effects.addAll(throwEffect.getLevels().getForLevel(level).map(VaultEntitiesConfig.ThrowEffect.Level::getEffects).orElse(Collections.emptyList()));
         }
      }

      return effects;
   }

   public VaultEntitiesConfig.AuraEffect getShiverEffect() {
      return this.shiverEffect;
   }

   public VaultEntitiesConfig.AuraEffect getSwampZombieEffect() {
      return this.swampZombieEffect;
   }

   @Override
   public String getName() {
      return "vault_entities";
   }

   @Override
   protected void reset() {
      this.deathEffects.clear();
      this.throwEffects.clear();
      EffectCloudAttribute.CloudConfig config = new EffectCloudAttribute.CloudConfig("", VaultMod.id(""), 80, 5.0F, Color.GREEN.getRGB(), true, 1.0F);
      config.setAdditionalEffect(new EffectCloudAttribute.CloudEffectConfig(MobEffects.POISON.getRegistryName(), 900, 0));
      this.deathEffects.add(new VaultEntitiesConfig.DeathEffect(EntityPredicate.of("minecraft:slime", true).orElseThrow()).put(0, config));
      this.throwEffects
         .add(
            new VaultEntitiesConfig.ThrowEffect(EntityPredicate.of("minecraft:witch", true).orElseThrow())
               .put(0, new VaultEntitiesConfig.ThrowEffect.CustomEffect(MobEffects.POISON, 0, 120))
         );
   }

   public static class AuraEffect {
      @Expose
      protected int range;
      @Expose
      protected int effectDuration;
      @Expose
      protected int effectAmplifier;

      public int getRange() {
         return this.range;
      }

      public int getEffectDuration() {
         return this.effectDuration;
      }

      public int getEffectAmplifier() {
         return this.effectAmplifier;
      }
   }

   public static class DeathEffect {
      @Expose
      private EntityPredicate filter;
      @Expose
      private LevelEntryList<VaultEntitiesConfig.DeathEffect.Level> levels;

      public DeathEffect(EntityPredicate filter) {
         this.filter = filter;
         this.levels = new LevelEntryList<>();
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public LevelEntryList<VaultEntitiesConfig.DeathEffect.Level> getLevels() {
         return this.levels;
      }

      public VaultEntitiesConfig.DeathEffect put(int level, EffectCloudAttribute.CloudConfig config) {
         this.levels.put(new VaultEntitiesConfig.DeathEffect.Level(level, config));
         return this;
      }

      public static class Level implements LevelEntryList.ILevelEntry {
         @Expose
         private final int level;
         @Expose
         private final EffectCloudAttribute.CloudConfig config;

         public Level(int level, EffectCloudAttribute.CloudConfig config) {
            this.level = level;
            this.config = config;
         }

         @Override
         public int getLevel() {
            return this.level;
         }

         private EffectCloudAttribute.CloudConfig getConfig() {
            return this.config;
         }
      }
   }

   public static class ThrowEffect {
      @Expose
      private EntityPredicate filter;
      @Expose
      private LevelEntryList<VaultEntitiesConfig.ThrowEffect.Level> levels;

      public ThrowEffect(EntityPredicate filter) {
         this.filter = filter;
         this.levels = new LevelEntryList<>();
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public LevelEntryList<VaultEntitiesConfig.ThrowEffect.Level> getLevels() {
         return this.levels;
      }

      public VaultEntitiesConfig.ThrowEffect put(int level, VaultEntitiesConfig.ThrowEffect.CustomEffect... effects) {
         this.levels.put(new VaultEntitiesConfig.ThrowEffect.Level(level, Arrays.asList(effects)));
         return this;
      }

      public static class CustomEffect {
         @Expose
         private final ResourceLocation effect;
         @Expose
         private final int amplifier;
         @Expose
         private final int duration;

         public CustomEffect(MobEffect effect, int amplifier, int duration) {
            this(effect.getRegistryName(), amplifier, duration);
         }

         public CustomEffect(ResourceLocation effect, int amplifier, int duration) {
            this.effect = effect;
            this.amplifier = amplifier;
            this.duration = duration;
         }

         public MobEffectInstance makeEffect() {
            MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
            return new MobEffectInstance(effect, this.duration, this.amplifier, false, false, true);
         }
      }

      public static class Level implements LevelEntryList.ILevelEntry {
         @Expose
         private final int level;
         @Expose
         private final List<VaultEntitiesConfig.ThrowEffect.CustomEffect> effects = new ArrayList<>();

         public Level(int level, List<VaultEntitiesConfig.ThrowEffect.CustomEffect> effects) {
            this.level = level;
            this.effects.addAll(effects);
         }

         public List<MobEffectInstance> getEffects() {
            return this.effects.stream().map(VaultEntitiesConfig.ThrowEffect.CustomEffect::makeEffect).collect(Collectors.toList());
         }

         @Override
         public int getLevel() {
            return this.level;
         }
      }
   }
}
