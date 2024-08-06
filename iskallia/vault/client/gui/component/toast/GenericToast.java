package iskallia.vault.client.gui.component.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GenericToast implements Toast {
   private static final ResourceLocation TOAST_BACKGROUND = VaultMod.id("textures/gui/screen/toast_background.png");
   protected final Component title;
   protected final List<FormattedCharSequence> message;
   protected final ResourceLocation texture;

   public GenericToast(Component title, Component message, ResourceLocation texture) {
      this.title = title;
      this.message = Minecraft.getInstance().font.split(message, 120);
      this.texture = this.parseTexture(texture);
   }

   private ResourceLocation parseTexture(ResourceLocation texture) {
      String path = texture.getPath();
      if (path.startsWith("textures")) {
         return texture;
      } else {
         path = "textures/" + path + ".png";
         return new ResourceLocation(texture.getNamespace(), path);
      }
   }

   protected long getDisplayTime() {
      return 3000L;
   }

   @OnlyIn(Dist.CLIENT)
   public static void add(String title, String message, ResourceLocation icon) {
      Minecraft.getInstance().getToasts().addToast(new GenericToast(new TextComponent(title), new TextComponent(message), icon));
   }

   @NotNull
   public Visibility render(@NotNull PoseStack poseStack, @NotNull ToastComponent toastComponent, long deltaTime) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TOAST_BACKGROUND);
      int startY = 4;
      toastComponent.blit(poseStack, 0, 0, 0, 0, this.width(), startY);

      for (int y = 0; y < this.height() - 8; y++) {
         toastComponent.blit(poseStack, 0, startY + y, 0, 4, this.width(), 1);
      }

      toastComponent.blit(poseStack, 0, this.height() - 4, 0, 30, this.width(), 4);
      RenderSystem.setShaderTexture(0, this.texture);
      GuiComponent.blit(poseStack, 10, 8, 0.0F, 0.0F, 16, 16, 16, 16);
      toastComponent.getMinecraft().font.draw(poseStack, this.title, 36.0F, 7.0F, Color.WHITE.getRGB());
      int messageY = 20;

      for (FormattedCharSequence formattedCharSequence : this.message) {
         toastComponent.getMinecraft().font.draw(poseStack, formattedCharSequence, 36.0F, messageY, Color.LIGHT_GRAY.getRGB());
         messageY += 9;
      }

      return deltaTime >= this.getDisplayTime() ? Visibility.HIDE : Visibility.SHOW;
   }

   public int height() {
      return 34 + this.message.size() * 9;
   }
}
