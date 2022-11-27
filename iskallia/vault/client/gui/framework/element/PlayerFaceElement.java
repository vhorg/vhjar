package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFaceElement<E extends PlayerFaceElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   public static final int DEFAULT_WIDTH = 12;
   public static final int DEFAULT_HEIGHT = 12;
   private final ObservableSupplier<String> playerNameObserver;
   private final SkinProfile skinProfile;
   private boolean visible;

   public PlayerFaceElement(IPosition position, Supplier<String> playerName) {
      this(position, Spatials.size(12, 12), playerName);
   }

   public PlayerFaceElement(IPosition position, ISize size, Supplier<String> playerName) {
      super(Spatials.positionXYZ(position).size(size));
      this.setVisible(true);
      this.skinProfile = new SkinProfile();
      this.playerNameObserver = ObservableSupplier.of(playerName, Objects::equals);
   }

   @Nullable
   public static String getLocalPlayerName() {
      LocalPlayer player = Minecraft.getInstance().player;
      return player != null ? player.getDisplayName().getString() : null;
   }

   public Optional<String> getPlayerName() {
      return Optional.ofNullable(this.skinProfile.getLatestNickname());
   }

   protected void onPlayerNameChanged(@Nullable String playerName) {
      this.skinProfile.updateSkin(playerName);
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.playerNameObserver.ifChanged(this::onPlayerNameChanged);
      RenderSystem.setShaderTexture(0, this.skinProfile.getLocationSkin());
      poseStack.pushPose();
      poseStack.translate(0.0, 0.0, this.worldSpatial.z());
      GuiComponent.blit(
         poseStack, this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.width(), this.worldSpatial.height(), 8.0F, 8.0F, 8, 8, 64, 64
      );
      GuiComponent.blit(
         poseStack, this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.width(), this.worldSpatial.height(), 40.0F, 8.0F, 8, 8, 64, 64
      );
      poseStack.popPose();
   }
}
