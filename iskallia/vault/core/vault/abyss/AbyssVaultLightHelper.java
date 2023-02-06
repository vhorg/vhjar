package iskallia.vault.core.vault.abyss;

import com.mojang.blaze3d.platform.NativeImage;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.mixin.AccessorLightTexture;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public class AbyssVaultLightHelper {
   private static boolean isCleanupCall = false;
   private static final Random rand = new Random();
   private static final AbyssVaultLightHelper INSTANCE = new AbyssVaultLightHelper();

   private AbyssVaultLightHelper() {
   }

   public static void init() {
      ClientEvents.AMBIENT_PARTICLE.register(INSTANCE, event -> {
         if (AbyssHelper.hasAbyssEffectClient()) {
            Player player = Minecraft.getInstance().player;
            float chance = AbyssHelper.getAbyssEffect(player) * AbyssHelper.getAbyssDistanceModifier(player);
            if (rand.nextFloat() < chance) {
               float particleChance = 0.02F * chance;
               event.setSettings(new AmbientParticleSettings(ParticleTypes.LARGE_SMOKE, particleChance));
            }
         }
      }, -1);
   }

   @SubscribeEvent
   public static void onFogColor(FogColors event) {
      if (AbyssHelper.hasAbyssEffectClient()) {
         float distanceEffect = AbyssHelper.getAbyssDistanceModifier(Minecraft.getInstance().player);
         float effect = distanceEffect * AbyssHelper.getAbyssEffect(Minecraft.getInstance().player);
         effect = Math.max(1.0F - effect, 0.05F);
         event.setRed(event.getRed() * effect);
         event.setGreen(event.getGreen() * effect);
         event.setBlue(event.getBlue() * effect);
      }
   }

   @SubscribeEvent
   public static void onRenderLast(RenderLevelLastEvent event) {
      if (AbyssHelper.hasAbyssEffectClient()) {
         LightTexture lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
         AccessorLightTexture accessLightTexture = (AccessorLightTexture)lightTexture;
         isCleanupCall = true;

         try {
            accessLightTexture.setUpdateLightTexture(true);
            lightTexture.updateLightTexture(event.getPartialTick());
         } finally {
            isCleanupCall = false;
         }
      }
   }

   public static void onSetupLight() {
      if (AbyssHelper.hasAbyssEffectClient()) {
         if (!isCleanupCall) {
            float distanceEffect = AbyssHelper.getAbyssDistanceModifier(Minecraft.getInstance().player);
            float effect = distanceEffect * AbyssHelper.getAbyssEffect(Minecraft.getInstance().player);
            effect = Math.max(1.0F - effect, 0.05F);
            effect *= effect;
            adjustLightmap(effect);
         }
      }
   }

   private static void adjustLightmap(float brightness) {
      AccessorLightTexture accessLightTexture = (AccessorLightTexture)Minecraft.getInstance().gameRenderer.lightTexture();
      NativeImage lightPixels = accessLightTexture.getLightPixels();
      DynamicTexture lightTexture = accessLightTexture.getLightTexture();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int px = lightPixels.getPixelRGBA(j, i);
            int a = px & 0xFF000000;
            int b = (px & 0xFF0000) >> 16;
            int g = (px & 0xFF00) >> 8;
            int r = px & 0xFF;
            b = (int)(b * brightness);
            g = (int)(g * brightness);
            r = (int)(r * brightness);
            lightPixels.setPixelRGBA(j, i, a | b << 16 | g << 8 | r);
         }
      }

      lightTexture.upload();
   }
}
