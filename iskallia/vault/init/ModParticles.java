package iskallia.vault.init;

import iskallia.vault.client.particles.AltarFlameParticle;
import iskallia.vault.client.particles.DepthFireworkParticle;
import iskallia.vault.client.particles.HealParticle;
import iskallia.vault.client.particles.NovaDotParticle;
import iskallia.vault.client.particles.NovaSpeedParticle;
import iskallia.vault.client.particles.RaidCubeParticle;
import iskallia.vault.client.particles.ScavengerAltarConsumeParticle;
import iskallia.vault.client.particles.ScavengerAltarParticle;
import iskallia.vault.client.particles.StabilizerCubeParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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
   bus = Bus.MOD
)
public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "the_vault");
   public static final RegistryObject<SimpleParticleType> GREEN_FLAME = REGISTRY.register("green_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> BLUE_FLAME = REGISTRY.register("blue_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> RED_FLAME = REGISTRY.register("red_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> YELLOW_FLAME = REGISTRY.register("yellow_flame", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> DEPTH_FIREWORK = REGISTRY.register("depth_ignoring_firework", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STABILIZER_CUBE = REGISTRY.register("stabilizer_cube", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> RAID_EFFECT_CUBE = REGISTRY.register("raid_cube", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_SPEED = REGISTRY.register("nova_speed", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NOVA_DOT = REGISTRY.register("nova_dot", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> HEAL = REGISTRY.register("heal", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SCAVENGER_CORE = REGISTRY.register("scavenger_core", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SCAVENGER_CORE_CONSUME = REGISTRY.register(
      "scavenger_core_consume", () -> new SimpleParticleType(true)
   );

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
      particleManager.register((ParticleType)DEPTH_FIREWORK.get(), DepthFireworkParticle.Factory::new);
      particleManager.register((ParticleType)STABILIZER_CUBE.get(), StabilizerCubeParticle.Factory::new);
      particleManager.register((ParticleType)RAID_EFFECT_CUBE.get(), RaidCubeParticle.Factory::new);
      particleManager.register((ParticleType)NOVA_SPEED.get(), NovaSpeedParticle.Provider::new);
      particleManager.register((ParticleType)NOVA_DOT.get(), NovaDotParticle.Provider::new);
      particleManager.register((ParticleType)HEAL.get(), HealParticle.Provider::new);
   }
}
