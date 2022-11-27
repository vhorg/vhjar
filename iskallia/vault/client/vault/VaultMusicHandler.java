package iskallia.vault.client.vault;

import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.world.data.ServerVaults;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class VaultMusicHandler {
   public static final int PANIC_TICKS_THRESHOLD = 600;
   public static SimpleSoundInstance panicSound;
   public static SimpleSoundInstance ambientLoop;
   public static SimpleSoundInstance ambientSound;
   public static SimpleSoundInstance bossLoop;
   public static boolean playBossMusic;
   private static int ticksBeforeAmbientSound;

   public static void startBossLoop() {
      if (bossLoop != null) {
         stopBossLoop();
      }

      bossLoop = SimpleSoundInstance.forMusic(ModSounds.VAULT_BOSS_LOOP);
      Minecraft.getInstance().getSoundManager().play(bossLoop);
   }

   public static void stopBossLoop() {
      if (bossLoop != null) {
         Minecraft.getInstance().getSoundManager().stop(bossLoop);
         bossLoop = null;
      }

      playBossMusic = false;
   }

   @SubscribeEvent
   public static void onTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         Minecraft mc = Minecraft.getInstance();
         SoundManager sh = mc.getSoundManager();
         if (mc.level == null) {
            stopBossLoop();
         } else {
            boolean inVault = ServerVaults.isVaultWorld(mc.level);
            boolean inArena = mc.level.dimension() == VaultMod.ARENA_KEY;
            if (!inVault && !inArena) {
               stopBossLoop();
            } else {
               VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
               if (type == VaultOverlayMessage.OverlayType.VAULT && ClientVaultRaidData.showTimer()) {
                  int remainingTicks = ClientVaultRaidData.getRemainingTicks();
                  if (remainingTicks < 600) {
                     panicSound = playNotActive(panicSound, () -> SimpleSoundInstance.forUI(ModSounds.TIMER_PANIC_TICK_SFX, 2.0F - remainingTicks / 600.0F));
                  }

                  if (!ClientVaultRaidData.isInBossFight()) {
                     stopBossLoop();
                  } else if (!sh.isActive(bossLoop)) {
                     startBossLoop();
                  }

                  if (ClientVaultRaidData.isInBossFight()) {
                     stopSound(ambientLoop);
                  } else {
                     ambientLoop = playNotActive(ambientLoop, () -> SimpleSoundInstance.forMusic(ModSounds.VAULT_AMBIENT_LOOP));
                  }

                  if (ticksBeforeAmbientSound < 0) {
                     ambientSound = playNotActive(ambientSound, () -> {
                        ticksBeforeAmbientSound = 3600;
                        return SimpleSoundInstance.forAmbientAddition(ModSounds.VAULT_AMBIENT);
                     });
                  }

                  ticksBeforeAmbientSound--;
               }
            }
         }
      }
   }

   private static void stopSound(SimpleSoundInstance sound) {
      SoundManager sh = Minecraft.getInstance().getSoundManager();
      if (sound != null && sh.isActive(sound)) {
         sh.stop(sound);
      }
   }

   private static SimpleSoundInstance playNotActive(@Nullable SimpleSoundInstance existing, Supplier<SimpleSoundInstance> playSound) {
      Minecraft mc = Minecraft.getInstance();
      if (existing == null || !mc.getSoundManager().isActive(existing)) {
         existing = playSound.get();
         mc.getSoundManager().play(existing);
      }

      return existing;
   }
}
