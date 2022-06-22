package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.entity.EyesoreEntity;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EyesoreBossOverlay {
   public static final ResourceLocation LASER_VIGNETTE = new ResourceLocation("the_vault", "textures/gui/overlay/vignette_red.png");

   @SubscribeEvent
   public static void onPreRender(Pre event) {
      if (event.getType() == ElementType.ALL) {
         Minecraft minecraft = Minecraft.func_71410_x();
         MatrixStack matrixStack = event.getMatrixStack();
         if (minecraft.field_71439_g != null) {
            if (minecraft.field_71441_e != null) {
               minecraft.field_71441_e
                  .func_217416_b()
                  .iterator()
                  .forEachRemaining(
                     entity -> {
                        if (entity instanceof EyesoreEntity) {
                           EyesoreEntity eyesore = (EyesoreEntity)entity;
                           PlayerEntity target = ((Optional)eyesore.func_184212_Q().func_187225_a(EyesoreEntity.LASER_TARGET))
                              .<PlayerEntity>map(id -> entity.func_130014_f_().func_217371_b(id))
                              .orElse(null);
                           if (target != null) {
                              if (target == minecraft.field_71439_g) {
                                 minecraft.field_71446_o.func_110577_a(LASER_VIGNETTE);
                                 RenderSystem.enableBlend();
                                 AbstractGui.func_238463_a_(
                                    matrixStack,
                                    0,
                                    0,
                                    0.0F,
                                    0.0F,
                                    minecraft.func_228018_at_().func_198105_m(),
                                    minecraft.func_228018_at_().func_198083_n(),
                                    minecraft.func_228018_at_().func_198107_o(),
                                    minecraft.func_228018_at_().func_198087_p()
                                 );
                              }
                           }
                        }
                     }
                  );
            }
         }
      }
   }
}
