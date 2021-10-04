package iskallia.vault.client.vault;

import iskallia.vault.Vault;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.VaultOverlayMessage;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class VaultMusicHandler {
   public static SimpleSound panicSound;
   public static SimpleSound ambientLoop;
   public static SimpleSound ambientSound;
   public static SimpleSound bossLoop;
   public static boolean playBossMusic;
   private static int ticksBeforeAmbientSound;

   public static void startBossLoop() {
      if (bossLoop != null) {
         stopBossLoop();
      }

      bossLoop = SimpleSound.func_184370_a(ModSounds.VAULT_BOSS_LOOP);
      Minecraft.func_71410_x().func_147118_V().func_147682_a(bossLoop);
   }

   public static void stopBossLoop() {
      if (bossLoop != null) {
         Minecraft.func_71410_x().func_147118_V().func_147683_b(bossLoop);
         bossLoop = null;
      }

      playBossMusic = false;
   }

   @SubscribeEvent
   public static void onTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         Minecraft mc = Minecraft.func_71410_x();
         SoundHandler sh = mc.func_147118_V();
         if (mc.field_71441_e == null) {
            stopBossLoop();
         } else {
            boolean inVault = mc.field_71441_e.func_234923_W_() == Vault.VAULT_KEY;
            if (!inVault) {
               stopBossLoop();
            } else {
               VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
               if (type == VaultOverlayMessage.OverlayType.VAULT) {
                  int remainingTicks = ClientVaultRaidData.getRemainingTicks();
                  int panicTicks = 600;
                  if (remainingTicks < 600) {
                     panicSound = playNotActive(
                        panicSound, () -> SimpleSound.func_184371_a(ModSounds.TIMER_PANIC_TICK_SFX, 2.0F - (float)remainingTicks / panicTicks)
                     );
                  }

                  if (!ClientVaultRaidData.isInBossFight()) {
                     stopBossLoop();
                  } else if (!sh.func_215294_c(bossLoop)) {
                     startBossLoop();
                  }

                  if (ClientVaultRaidData.isInBossFight()) {
                     stopSound(ambientLoop);
                  } else {
                     ambientLoop = playNotActive(ambientLoop, () -> SimpleSound.func_184370_a(ModSounds.VAULT_AMBIENT_LOOP));
                  }

                  if (ticksBeforeAmbientSound < 0) {
                     ambientSound = playNotActive(ambientSound, () -> {
                        ticksBeforeAmbientSound = 3600;
                        return SimpleSound.func_239530_b_(ModSounds.VAULT_AMBIENT);
                     });
                  }

                  ticksBeforeAmbientSound--;
               }
            }
         }
      }
   }

   private static void stopSound(SimpleSound sound) {
      SoundHandler sh = Minecraft.func_71410_x().func_147118_V();
      if (sound != null && sh.func_215294_c(sound)) {
         sh.func_147683_b(sound);
      }
   }

   private static SimpleSound playNotActive(@Nullable SimpleSound existing, Supplier<SimpleSound> playSound) {
      Minecraft mc = Minecraft.func_71410_x();
      if (existing == null || !mc.func_147118_V().func_215294_c(existing)) {
         existing = playSound.get();
         mc.func_147118_V().func_147682_a(existing);
      }

      return existing;
   }
}
