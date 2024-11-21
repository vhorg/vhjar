package iskallia.vault.client.gui.screen.achievements;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.screen.BaseScreen;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.TaskWidget;
import iskallia.vault.task.renderer.Vec2d;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.world.data.AchievementData;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class AchievementScreen extends BaseScreen {
   private final Task clientAchievements;

   public AchievementScreen() {
      super(new TranslatableComponent("screen.the_vault.achievements.title"), 410, 230);
      Task task = AchievementData.CLIENT;
      this.clientAchievements = task != null ? task.copy() : null;
   }

   protected void init() {
      if (this.clientAchievements != null) {
         int titleHeight = 12;
         int x = this.getGuiLeft() + this.getPadding();
         int y = this.getGuiTop() + titleHeight + this.getPadding();
         int width = this.guiWidth / 2 - 14;
         int height = this.guiHeight - titleHeight - this.getPadding() * 2;
         int rightX = this.getGuiLeft() + this.guiWidth / 2 + this.getPadding() - 1;
         int modifierListWidth = 20;
         int detailsPaneWidth = width - modifierListWidth;
         Vec2d leftOrigin = new Vec2d(x, y);
         Vec2d rightOrigin = new Vec2d(rightX, y);
         Vec2d modifiersOrigin = new Vec2d(rightX + detailsPaneWidth + 1, y);
         this.addRenderableWidget(
            new TaskWidget(
               rightOrigin,
               this.clientAchievements,
               () -> AchievementRendererContext.forDetailsPane(rightOrigin, new Vec2d(detailsPaneWidth, height), null, 0.0F)
            )
         );
         this.addRenderableWidget(
            new TaskWidget(leftOrigin, this.clientAchievements, () -> AchievementRendererContext.forOverview(leftOrigin, new Vec2d(width, height), null, 0.0F))
         );
         this.addRenderableWidget(
            new TaskWidget(
               modifiersOrigin,
               this.clientAchievements,
               () -> AchievementRendererContext.forModifierList(modifiersOrigin, new Vec2d(modifierListWidth, height), null, 0.0F)
            )
         );
      }
   }

   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      ScreenTextures.DEFAULT_WINDOW_BACKGROUND.blit(poseStack, this.getGuiLeft(), this.getGuiTop(), 0, this.guiWidth, this.guiHeight);
      ScreenTextures.INSET_VERTICAL_SEPARATOR.blit(poseStack, this.getCenterX() - 2, this.getGuiTop() + 1, 0, 3, this.guiHeight - 2);
      this.font.draw(poseStack, this.title, this.getGuiLeft() + 8, this.getGuiTop() + 6, -16777216);
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
