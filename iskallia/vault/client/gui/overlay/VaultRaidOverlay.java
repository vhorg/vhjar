package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultRaidOverlay {
   public static final ResourceLocation RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");
   public static final ResourceLocation NORMAL_RARITY = new ResourceLocation("the_vault", "textures/gui/modifiers/normal.png");
   public static final ResourceLocation RARE_RARITY = new ResourceLocation("the_vault", "textures/gui/modifiers/rare.png");
   public static final ResourceLocation EPIC_RARITY = new ResourceLocation("the_vault", "textures/gui/modifiers/epic.png");
   public static final ResourceLocation OMEGA_RARITY = new ResourceLocation("the_vault", "textures/gui/modifiers/omega.png");
   public static int currentRarity;
   public static int remainingTicks;
   public static SimpleSound panicSound;
   public static SimpleSound ambientLoop;
   public static SimpleSound ambientSound;
   public static SimpleSound bossLoop;
   public static boolean bossSummoned;
   private static int ticksBeforeAmbientSound;

   public static void startBossLoop() {
      if (bossLoop != null) {
         stopBossLoop();
      }

      Minecraft minecraft = Minecraft.func_71410_x();
      bossLoop = SimpleSound.func_239532_b_(ModSounds.VAULT_BOSS_LOOP, 0.75F, 1.0F);
      minecraft.func_147118_V().func_147682_a(bossLoop);
   }

   public static void stopBossLoop() {
      if (bossLoop != null) {
         Minecraft minecraft = Minecraft.func_71410_x();
         minecraft.func_147118_V().func_147683_b(bossLoop);
         bossLoop = null;
      }
   }

   @SubscribeEvent
   public static void onPostRender(Post event) {
      if (event.getType() == ElementType.POTION_ICONS) {
         Minecraft minecraft = Minecraft.func_71410_x();
         boolean inVault = minecraft.field_71441_e.func_234923_W_() == Vault.VAULT_KEY;
         if (minecraft.field_71441_e != null && inVault) {
            if (remainingTicks != 0) {
               MatrixStack matrixStack = event.getMatrixStack();
               int bottom = minecraft.func_228018_at_().func_198087_p();
               int barWidth = 62;
               int barHeight = 22;
               int panicTicks = 600;
               if (!bossSummoned) {
                  if (inVault) {
                     stopBossLoop();
                  }
               } else if (!minecraft.func_147118_V().func_215294_c(bossLoop) && inVault) {
                  startBossLoop();
               }

               matrixStack.func_227860_a_();
               matrixStack.func_227861_a_(barWidth, bottom, 0.0);
               FontHelper.drawStringWithBorder(
                  matrixStack, formatTimeString(), 18.0F, -12.0F, remainingTicks < panicTicks && remainingTicks % 10 < 5 ? -65536 : -1, -16777216
               );
               matrixStack.func_227861_a_(30.0, -25.0, 0.0);
               if (remainingTicks < panicTicks) {
                  matrixStack.func_227863_a_(new Quaternion(0.0F, 0.0F, remainingTicks * 10.0F % 360.0F, true));
               } else {
                  matrixStack.func_227863_a_(new Quaternion(0.0F, 0.0F, remainingTicks % 360.0F, true));
               }

               minecraft.func_110434_K().func_110577_a(RESOURCE);
               RenderSystem.enableBlend();
               int hourglassWidth = 12;
               int hourglassHeight = 16;
               matrixStack.func_227861_a_(-hourglassWidth / 2.0F, -hourglassHeight / 2.0F, 0.0);
               minecraft.field_71456_v.func_238474_b_(matrixStack, 0, 0, 1, 36, hourglassWidth, hourglassHeight);
               matrixStack.func_227865_b_();
               if (inVault) {
                  if (bossSummoned && ambientLoop != null && minecraft.func_147118_V().func_215294_c(ambientLoop)) {
                     minecraft.func_147118_V().func_147683_b(ambientLoop);
                  }

                  if ((ambientLoop == null || !minecraft.func_147118_V().func_215294_c(ambientLoop)) && !bossSummoned) {
                     ambientLoop = SimpleSound.func_184370_a(ModSounds.VAULT_AMBIENT_LOOP);
                     minecraft.func_147118_V().func_147682_a(ambientLoop);
                  }

                  if (ticksBeforeAmbientSound < 0 && (ambientSound == null || !minecraft.func_147118_V().func_215294_c(ambientSound))) {
                     ambientSound = SimpleSound.func_239530_b_(ModSounds.VAULT_AMBIENT);
                     minecraft.func_147118_V().func_147682_a(ambientSound);
                     ticksBeforeAmbientSound = 3600;
                  }

                  ticksBeforeAmbientSound--;
               }

               renderVaultModifiers(event);
               if (remainingTicks < panicTicks && (panicSound == null || !minecraft.func_147118_V().func_215294_c(panicSound))) {
                  panicSound = SimpleSound.func_184371_a(ModSounds.TIMER_PANIC_TICK_SFX, 2.0F - (float)remainingTicks / panicTicks);
                  minecraft.func_147118_V().func_147682_a(panicSound);
               }
            }
         } else {
            if (inVault) {
               stopBossLoop();
            }

            bossSummoned = false;
         }
      }
   }

   public static void renderVaultModifiers(Post event) {
      if (VaultModifiers.CLIENT != null) {
         Minecraft minecraft = Minecraft.func_71410_x();
         MatrixStack matrixStack = event.getMatrixStack();
         int right = minecraft.func_228018_at_().func_198107_o();
         int bottom = minecraft.func_228018_at_().func_198087_p();
         int rightMargin = 28;
         int raritySize = 24;
         int modifierSize = 24;
         int modifierGap = 2;
         int xPosition = right - rightMargin;
         matrixStack.func_227860_a_();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(right - 1, bottom - 96, 0.0);
         minecraft.func_110434_K()
            .func_110577_a(
               currentRarity == 0
                  ? NORMAL_RARITY
                  : (currentRarity == 1 ? RARE_RARITY : (currentRarity == 2 ? EPIC_RARITY : (currentRarity == 3 ? OMEGA_RARITY : NORMAL_RARITY)))
            );
         AbstractGui.func_238463_a_(matrixStack, -raritySize, -raritySize - 3, 0.0F, 0.0F, raritySize, raritySize, raritySize, raritySize);
         matrixStack.func_227865_b_();
         VaultModifiers.CLIENT
            .forEach(
               (index, modifier) -> {
                  minecraft.func_110434_K().func_110577_a(modifier.getIcon());
                  AbstractGui.func_238463_a_(
                     matrixStack,
                     right - (rightMargin + modifierSize),
                     bottom - modifierSize - 2,
                     0.0F,
                     0.0F,
                     modifierSize,
                     modifierSize,
                     modifierSize,
                     modifierSize
                  );
                  matrixStack.func_227861_a_(-(modifierGap + modifierSize), 0.0, 0.0);
               }
            );
         matrixStack.func_227865_b_();
      }
   }

   public static String formatTimeString() {
      long seconds = remainingTicks / 20 % 60;
      long minutes = remainingTicks / 20 / 60 % 60;
      return String.format("%02d:%02d", minutes, seconds);
   }
}
