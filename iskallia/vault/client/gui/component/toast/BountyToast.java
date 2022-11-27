package iskallia.vault.client.gui.component.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.bounty.TaskRegistry;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class BountyToast implements Toast {
   private static final ResourceLocation BOUNTY_TEXTURE = VaultMod.id("textures/gui/screen/bounty/toasts.png");
   private static final Map<ResourceLocation, int[]> uvCoordinates = Map.of(
      TaskRegistry.KILL_ENTITY,
      new int[]{0, 32},
      TaskRegistry.DAMAGE_ENTITY,
      new int[]{32, 32},
      TaskRegistry.COMPLETION,
      new int[]{64, 32},
      TaskRegistry.ITEM_DISCOVERY,
      new int[]{96, 32},
      TaskRegistry.ITEM_SUBMISSION,
      new int[]{128, 32},
      TaskRegistry.MINING,
      new int[]{160, 32},
      VaultMod.id("unidentified"),
      new int[]{192, 32}
   );
   private static final long DISPLAY_TIME = 3000L;
   private final Component title;
   private final List<FormattedCharSequence> message;
   private long deltaTime;
   private boolean changed;
   private ResourceLocation taskId;

   public BountyToast(Component title, Component message, ResourceLocation taskId) {
      this.title = title;
      this.message = Minecraft.getInstance().font.split(message, 122);
      this.taskId = taskId;
   }

   public static void add(Component title, Component message, ResourceLocation taskId) {
      Minecraft.getInstance().getToasts().addToast(new BountyToast(title, message, taskId));
   }

   @NotNull
   public Visibility render(@NotNull PoseStack poseStack, @NotNull ToastComponent toastComponent, long deltaTime) {
      if (this.changed) {
         this.deltaTime = deltaTime;
         this.changed = false;
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, BOUNTY_TEXTURE);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.message.size() <= 1) {
         toastComponent.blit(poseStack, 0, 0, 0, 0, this.width(), this.height());
      } else {
         int lineHeight = 12;
         int messageHeight = this.height() + Math.max(0, this.message.size() - 1) * lineHeight;
         int startY = 18;
         toastComponent.blit(poseStack, 0, 0, 0, 0, this.width(), startY);

         for (int y = 0; y < this.message.size(); y++) {
            toastComponent.blit(poseStack, 0, startY + y * lineHeight, 0, lineHeight, this.width(), lineHeight);
         }

         toastComponent.blit(poseStack, 0, messageHeight - 4, 0, 28, this.width(), 4);
      }

      toastComponent.blit(poseStack, 2, 6, uvCoordinates.get(this.taskId)[0], uvCoordinates.get(this.taskId)[1], 32, 32);
      toastComponent.getMinecraft().font.draw(poseStack, this.title, 36.0F, 7.0F, Color.WHITE.getRGB());
      int messageY = 20;

      for (FormattedCharSequence formattedCharSequence : this.message) {
         toastComponent.getMinecraft().font.draw(poseStack, formattedCharSequence, 36.0F, messageY, Color.LIGHT_GRAY.getRGB());
         messageY += 12;
      }

      return deltaTime - this.deltaTime >= 3000L ? Visibility.HIDE : Visibility.SHOW;
   }
}
