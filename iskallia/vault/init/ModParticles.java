package iskallia.vault.init;

import iskallia.vault.client.particles.AltarFlameParticle;
import iskallia.vault.client.particles.DepthFireworkParticle;
import iskallia.vault.client.particles.EyesoreAppearanceParticle;
import iskallia.vault.client.particles.RaidCubeParticle;
import iskallia.vault.client.particles.StabilizerCubeParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.MOD
)
public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "the_vault");
   public static final RegistryObject<BasicParticleType> GREEN_FLAME = REGISTRY.register("green_flame", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> BLUE_FLAME = REGISTRY.register("blue_flame", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> RED_FLAME = REGISTRY.register("red_flame", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> YELLOW_FLAME = REGISTRY.register("yellow_flame", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> DEPTH_FIREWORK = REGISTRY.register("depth_ignoring_firework", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> STABILIZER_CUBE = REGISTRY.register("stabilizer_cube", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> RAID_EFFECT_CUBE = REGISTRY.register("raid_cube", () -> new BasicParticleType(true));
   public static final RegistryObject<BasicParticleType> EYESORE_APPEARANCE = REGISTRY.register("eyesore_appearance", () -> new BasicParticleType(true));

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerParticles(ParticleFactoryRegisterEvent event) {
      ParticleManager particleManager = Minecraft.func_71410_x().field_71452_i;
      particleManager.func_215234_a((ParticleType)GREEN_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)BLUE_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)RED_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)YELLOW_FLAME.get(), AltarFlameParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)DEPTH_FIREWORK.get(), DepthFireworkParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)STABILIZER_CUBE.get(), StabilizerCubeParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)RAID_EFFECT_CUBE.get(), RaidCubeParticle.Factory::new);
      particleManager.func_215234_a((ParticleType)EYESORE_APPEARANCE.get(), EyesoreAppearanceParticle.Factory::new);
   }
}
