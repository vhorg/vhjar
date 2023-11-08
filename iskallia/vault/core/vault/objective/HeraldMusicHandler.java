package iskallia.vault.core.vault.objective;

import iskallia.vault.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class HeraldMusicHandler {
   private static boolean TICKED = false;
   private static boolean SHOULD_PLAY = false;
   private static int TRACK_ID = -1;
   public static SimpleSoundInstance TRACK;

   @SubscribeEvent
   public static void onTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         SoundManager manager = Minecraft.getInstance().getSoundManager();
         if (!TICKED) {
            if (manager.isActive(TRACK)) {
               manager.stop(TRACK);
            }

            TRACK_ID = -1;
         } else {
            if (!manager.isActive(TRACK) && SHOULD_PLAY) {
               TRACK = new SimpleSoundInstance(
                  getTrack(++TRACK_ID).getLocation(), SoundSource.MASTER, 0.3F, 1.0F, false, 0, Attenuation.LINEAR, 0.0, 0.0, 0.0, true
               );
               manager.play(TRACK);
            }

            TICKED = false;
         }
      }
   }

   private static SoundEvent getTrack(int index) {
      return new SoundEvent[]{ModSounds.BOSS_FIGHT_1, ModSounds.BOSS_FIGHT_2, ModSounds.BOSS_FIGHT_3, ModSounds.BOSS_FIGHT_4}[(index >> 1) % 4];
   }

   public static void tick(boolean shouldPlay) {
      TICKED = true;
      SHOULD_PLAY = shouldPlay;
   }
}
