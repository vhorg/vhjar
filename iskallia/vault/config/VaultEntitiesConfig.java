package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultEntitiesConfig extends Config {
   @Expose
   private final LevelEntryList<VaultEntitiesConfig.SlimeEffectLevel> slimeClouds = new LevelEntryList<>();
   @Expose
   private final LevelEntryList<VaultEntitiesConfig.WitchEffectsLevel> witchThrownEffects = new LevelEntryList<>();

   public Optional<EffectCloudAttribute.CloudConfig> getSlimeEffectConfig(int level) {
      return this.slimeClouds.getForLevel(level).map(VaultEntitiesConfig.SlimeEffectLevel::getConfig);
   }

   public List<MobEffectInstance> getWitchAdditionalThrownEffects(int level) {
      return this.witchThrownEffects.getForLevel(level).map(VaultEntitiesConfig.WitchEffectsLevel::getEffects).orElse(Collections.emptyList());
   }

   @Override
   public String getName() {
      return "vault_entities";
   }

   @Override
   protected void reset() {
      this.slimeClouds.clear();
      EffectCloudAttribute.CloudConfig config = new EffectCloudAttribute.CloudConfig("", VaultMod.id(""), 80, 5.0F, Color.GREEN.getRGB(), true, 1.0F);
      config.setAdditionalEffect(new EffectCloudAttribute.CloudEffectConfig(MobEffects.POISON.getRegistryName(), 900, 0));
      this.slimeClouds.add(new VaultEntitiesConfig.SlimeEffectLevel(0, config));
      this.witchThrownEffects.clear();
      this.witchThrownEffects.add(new VaultEntitiesConfig.WitchEffectsLevel(0).addEffect(new VaultEntitiesConfig.CustomEffect(MobEffects.POISON, 0, 900)));
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

   public static class SlimeEffectLevel implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final EffectCloudAttribute.CloudConfig config;

      public SlimeEffectLevel(int level, EffectCloudAttribute.CloudConfig config) {
         this.level = level;
         this.config = config;
      }

      private EffectCloudAttribute.CloudConfig getConfig() {
         return this.config;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   public static class WitchEffectsLevel implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final List<VaultEntitiesConfig.CustomEffect> effects = new ArrayList<>();

      public WitchEffectsLevel(int level) {
         this.level = level;
      }

      private VaultEntitiesConfig.WitchEffectsLevel addEffect(VaultEntitiesConfig.CustomEffect effect) {
         this.effects.add(effect);
         return this;
      }

      public List<MobEffectInstance> getEffects() {
         return this.effects.stream().map(VaultEntitiesConfig.CustomEffect::makeEffect).collect(Collectors.toList());
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
