package iskallia.vault.init;

import com.mojang.serialization.Codec;
import iskallia.vault.client.particles.AlchemyTableParticle;
import iskallia.vault.client.particles.AltarFlameParticle;
import iskallia.vault.client.particles.ChainingParticle;
import iskallia.vault.client.particles.CloudEffectParticle;
import iskallia.vault.client.particles.ColoredParticleOptions;
import iskallia.vault.client.particles.DepthFireworkParticle;
import iskallia.vault.client.particles.DepthNightVisionParticle;
import iskallia.vault.client.particles.DiffuserCompleteParticle;
import iskallia.vault.client.particles.DiffuserParticle;
import iskallia.vault.client.particles.DiffuserUpgradedParticle;
import iskallia.vault.client.particles.EffectRangeParticle;
import iskallia.vault.client.particles.EnderAnchorParticle;
import iskallia.vault.client.particles.EntityLockedParticle;
import iskallia.vault.client.particles.FireballParticle;
import iskallia.vault.client.particles.FloatingAltarItemParticle;
import iskallia.vault.client.particles.GrowingSphereParticle;
import iskallia.vault.client.particles.HealParticle;
import iskallia.vault.client.particles.LuckyHitDrainParticle;
import iskallia.vault.client.particles.LuckyHitParticle;
import iskallia.vault.client.particles.LuckyHitSweepingParticle;
import iskallia.vault.client.particles.LuckyHitVortexParticle;
import iskallia.vault.client.particles.NovaDotParticle;
import iskallia.vault.client.particles.NovaExplosionCloudParticle;
import iskallia.vault.client.particles.NovaExplosionParticle;
import iskallia.vault.client.particles.NovaExplosionWaveParticle;
import iskallia.vault.client.particles.NovaSpeedParticle;
import iskallia.vault.client.particles.PylonConsumeParticle;
import iskallia.vault.client.particles.ReverseDiffuserParticle;
import iskallia.vault.client.particles.ReverseDiffuserUpgradedParticle;
import iskallia.vault.client.particles.ScavengerAltarConsumeParticle;
import iskallia.vault.client.particles.ScavengerAltarParticle;
import iskallia.vault.client.particles.ShockedParticle;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.client.particles.StabilizerCubeParticle;
import iskallia.vault.client.particles.StonefallFrostWaveParticle;
import iskallia.vault.client.particles.StormCloudParticle;
import iskallia.vault.client.particles.StunnedParticle;
import iskallia.vault.client.particles.TotemFountainParticle;
import iskallia.vault.client.particles.UberPylonFountainParticle;
import iskallia.vault.client.particles.UberPylonParticle;
import iskallia.vault.util.Tween;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "the_vault");
   public static final RegistryObject<SimpleParticleType> GREEN_FLAME = REGISTRY.register("green_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> BLUE_FLAME = REGISTRY.register("blue_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> RED_FLAME = REGISTRY.register("red_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> YELLOW_FLAME = REGISTRY.register("yellow_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> PURPLE_FLAME = REGISTRY.register("purple_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> DEPTH_FIREWORK = REGISTRY.register("depth_ignoring_firework", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STABILIZER_CUBE = REGISTRY.register("stabilizer_cube", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_SPEED = REGISTRY.register("nova_speed", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_DOT = REGISTRY.register("nova_dot", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA = REGISTRY.register("nova", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_WAVE = REGISTRY.register("nova_wave", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_CLOUD = REGISTRY.register("nova_cloud", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STONEFALL_FROST = REGISTRY.register("stonefall_frost", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> HEAL = REGISTRY.register("heal", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SCAVENGER_CORE = REGISTRY.register("scavenger_core", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SCAVENGER_CORE_CONSUME = REGISTRY.register(
      "scavenger_core_consume", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> DIFFUSER = REGISTRY.register("diffuser", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> DIFFUSER_COMPLETE = REGISTRY.register("diffuser_complete", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> DIFFUSER_UPGRADED = REGISTRY.register("diffuser_upgraded", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> REVERSE_DIFFUSER = REGISTRY.register("reverse_diffuser", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> REVERSE_DIFFUSER_UPGRADED = REGISTRY.register(
      "reverse_diffuser_upgraded", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> CLOUD_EFFECT = REGISTRY.register("cloud_effect", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> CHAINING = REGISTRY.register("chaining", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STUNNED = REGISTRY.register("stunned", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SHOCKED = REGISTRY.register("shocked", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> PYLON_CONSUME = REGISTRY.register("pylon_consume", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> FLOATING_ALTAR_ITEM = REGISTRY.register("floating_altar_item", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT_SWEEPING = REGISTRY.register("lucky_hit_sweeping", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT = REGISTRY.register("lucky_hit", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT_MANA = REGISTRY.register("lucky_hit_mana", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT_LEECH = REGISTRY.register("lucky_hit_leech", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT_DAMAGE = REGISTRY.register("lucky_hit_damage", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> LUCKY_HIT_VORTEX = REGISTRY.register("lucky_hit_vortex", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> ENDER_ANCHOR = REGISTRY.register("ender_anchor", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> DEPTH_NIGHT_VISION = REGISTRY.register("depth_night_vision", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> UBER_PYLON = REGISTRY.register("uber_pylon", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> UBER_PYLON_FOUNTAIN = REGISTRY.register("uber_pylon_fountain", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> FIREBALL_CLOUD = REGISTRY.register("fireball_cloud", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STORM_CLOUD = REGISTRY.register("storm_cloud", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> ENTITY_LOCKED = REGISTRY.register("entity_locked", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> BONK = REGISTRY.register("bonk", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> ALCHEMY_TABLE = REGISTRY.register("alchemy_table", () -> new SimpleParticleType(true));
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> TAUNT_CHARM_EFFECT_RANGE = register(
      REGISTRY, "taunt_charm_effect_range", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> HEAL_GROUP_EFFECT_RANGE = register(
      REGISTRY, "heal_group_effect_range", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> HEAL_GROUP_EFFECT_RING = register(
      REGISTRY, "heal_group_effect_ring", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> TOTEM_EFFECT_RANGE = register(
      REGISTRY, "totem_effect_range", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<ColoredParticleOptions>> TOTEM_FOUNTAIN = register(
      REGISTRY, "totem_fountain", ColoredParticleOptions.DESERIALIZER, ColoredParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> MANA_SHIELD_RETRIBUTION_EFFECT_RANGE = register(
      REGISTRY, "mana_shield_retribution_effect_range", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> SIGHT_JAVELIN_RANGE = register(
      REGISTRY, "sight_javelin_range", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );
   public static final RegistryObject<ParticleType<SphericalParticleOptions>> IMPLODE = register(
      REGISTRY, "implode", SphericalParticleOptions.DESERIALIZER, SphericalParticleOptions::codec, true
   );

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerParticles(ParticleFactoryRegisterEvent event) {
      ParticleEngine particleManager = Minecraft.getInstance().particleEngine;
      particleManager.register((ParticleType)SCAVENGER_CORE.get(), ScavengerAltarParticle.Provider::new);
      particleManager.register((ParticleType)SCAVENGER_CORE_CONSUME.get(), ScavengerAltarConsumeParticle.Factory::new);
      particleManager.register((ParticleType)GREEN_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.register((ParticleType)BLUE_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.register((ParticleType)RED_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.register((ParticleType)YELLOW_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.register((ParticleType)PURPLE_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.register((ParticleType)DEPTH_FIREWORK.get(), DepthFireworkParticle.Factory::new);
      particleManager.register((ParticleType)STABILIZER_CUBE.get(), StabilizerCubeParticle.Factory::new);
      particleManager.register((ParticleType)NOVA_SPEED.get(), NovaSpeedParticle.Provider::new);
      particleManager.register((ParticleType)NOVA_DOT.get(), NovaDotParticle.Provider::new);
      particleManager.register((ParticleType)NOVA.get(), NovaExplosionParticle.Provider::new);
      particleManager.register((ParticleType)NOVA_WAVE.get(), NovaExplosionWaveParticle.Provider::new);
      particleManager.register((ParticleType)NOVA_CLOUD.get(), NovaExplosionCloudParticle.Provider::new);
      particleManager.register((ParticleType)STONEFALL_FROST.get(), StonefallFrostWaveParticle.Provider::new);
      particleManager.register((ParticleType)HEAL.get(), HealParticle.Provider::new);
      particleManager.register((ParticleType)DIFFUSER.get(), DiffuserParticle.Provider::new);
      particleManager.register((ParticleType)DIFFUSER_UPGRADED.get(), DiffuserUpgradedParticle.Provider::new);
      particleManager.register((ParticleType)DIFFUSER_COMPLETE.get(), DiffuserCompleteParticle.Provider::new);
      particleManager.register((ParticleType)REVERSE_DIFFUSER.get(), ReverseDiffuserParticle.Provider::new);
      particleManager.register((ParticleType)REVERSE_DIFFUSER_UPGRADED.get(), ReverseDiffuserUpgradedParticle.Provider::new);
      particleManager.register((ParticleType)TOTEM_FOUNTAIN.get(), TotemFountainParticle.Provider::new);
      particleManager.register((ParticleType)CLOUD_EFFECT.get(), CloudEffectParticle.Provider::new);
      particleManager.register((ParticleType)CHAINING.get(), ChainingParticle.Provider::new);
      particleManager.register((ParticleType)STUNNED.get(), StunnedParticle.Provider::new);
      particleManager.register((ParticleType)SHOCKED.get(), ShockedParticle.Provider::new);
      particleManager.register((ParticleType)PYLON_CONSUME.get(), PylonConsumeParticle.Provider::new);
      particleManager.register((ParticleType)FLOATING_ALTAR_ITEM.get(), FloatingAltarItemParticle.Provider::new);
      particleManager.register((ParticleType)LUCKY_HIT.get(), LuckyHitParticle.Provider::new);
      particleManager.register((ParticleType)LUCKY_HIT_SWEEPING.get(), LuckyHitSweepingParticle.Provider::new);
      particleManager.register((ParticleType)LUCKY_HIT_MANA.get(), LuckyHitDrainParticle.Provider::new);
      particleManager.register((ParticleType)LUCKY_HIT_LEECH.get(), LuckyHitDrainParticle.Provider::new);
      particleManager.register((ParticleType)LUCKY_HIT_DAMAGE.get(), ShockedParticle.Provider::new);
      particleManager.register((ParticleType)ENDER_ANCHOR.get(), EnderAnchorParticle.Provider::new);
      particleManager.register((ParticleType)DEPTH_NIGHT_VISION.get(), DepthNightVisionParticle.Factory::new);
      particleManager.register((ParticleType)LUCKY_HIT_VORTEX.get(), LuckyHitVortexParticle.Provider::new);
      particleManager.register((ParticleType)UBER_PYLON.get(), UberPylonParticle.Provider::new);
      particleManager.register((ParticleType)UBER_PYLON_FOUNTAIN.get(), UberPylonFountainParticle.Provider::new);
      particleManager.register((ParticleType)FIREBALL_CLOUD.get(), FireballParticle.Provider::new);
      particleManager.register((ParticleType)STORM_CLOUD.get(), StormCloudParticle.Provider::new);
      particleManager.register((ParticleType)ENTITY_LOCKED.get(), EntityLockedParticle.Provider::new);
      particleManager.register((ParticleType)BONK.get(), LuckyHitDrainParticle.Provider::new);
      particleManager.register((ParticleType)ALCHEMY_TABLE.get(), AlchemyTableParticle.Provider::new);
      particleManager.register(
         (ParticleType)TOTEM_EFFECT_RANGE.get(), sprites -> new EffectRangeParticle.SphereProvider(sprites, 1.0F, 40, 0.5F, Tween.PARABOLIC)
      );
      particleManager.register(
         (ParticleType)TAUNT_CHARM_EFFECT_RANGE.get(),
         sprites -> new EffectRangeParticle.SphereProvider(sprites, 0.0F, 20, 2.0F, Tween.inOut(Tween.EASE_OUT_BOUNCE, Tween.EASE_IN_BOUNCE, 0.25F))
      );
      particleManager.register(
         (ParticleType)HEAL_GROUP_EFFECT_RANGE.get(),
         sprites -> new EffectRangeParticle.SphereProvider(sprites, 0.0F, 60, 2.0F, Tween.inOut(Tween.EASE_OUT_BOUNCE, Tween.LINEAR, 0.25F))
      );
      particleManager.register(
         (ParticleType)HEAL_GROUP_EFFECT_RING.get(),
         sprites -> new EffectRangeParticle.CircleProvider(sprites, 1.0F, 60, 1.0F, Tween.inOut(Tween.EASE_OUT_BOUNCE, Tween.LINEAR, 0.25F))
      );
      particleManager.register(
         (ParticleType)MANA_SHIELD_RETRIBUTION_EFFECT_RANGE.get(),
         sprites -> new EffectRangeParticle.SphereProvider(sprites, 0.0F, 60, 1.0F, Tween.inOut(Tween.LINEAR, Tween.EASE_OUT_CUBIC, 0.05F))
      );
      particleManager.register((ParticleType)SIGHT_JAVELIN_RANGE.get(), sprites -> new GrowingSphereParticle.SphereProvider(sprites, 2.0F, 60, 1.0F));
      particleManager.register((ParticleType)IMPLODE.get(), sprites -> new GrowingSphereParticle.SphereProvider(sprites, 0.0F, 10, 1.0F));
   }

   private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(
      DeferredRegister<ParticleType<?>> registry,
      String name,
      Deserializer<T> deserializer,
      Function<ParticleType<T>, Codec<T>> codecProvider,
      boolean overrideLimiter
   ) {
      return registry.register(name, () -> new ParticleType<T>(overrideLimiter, deserializer) {
         @Nonnull
         public Codec<T> codec() {
            return (Codec<T>)codecProvider.apply(this);
         }
      });
   }
}
