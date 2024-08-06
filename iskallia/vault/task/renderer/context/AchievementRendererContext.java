package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.Vec2d;
import iskallia.vault.task.util.ITaskModifier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AchievementRendererContext extends RendererContext {
   protected final Vec2d origin;
   protected final Vec2d size;
   protected final AchievementRendererContext.Medium medium;
   protected boolean selected;
   protected List<ITaskModifier> modifiers;
   protected TimedTask timedTask;

   protected AchievementRendererContext(Vec2d origin, Vec2d size, PoseStack matrices, float tickDelta, AchievementRendererContext.Medium medium) {
      super(matrices, tickDelta, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), Minecraft.getInstance().font);
      this.origin = origin;
      this.size = size;
      this.medium = medium;
      this.modifiers = new ArrayList<>();
   }

   public static AchievementRendererContext forOverview(Vec2d origin, Vec2d size, PoseStack poseStack, float tickDelta) {
      return new AchievementRendererContext(origin, size, poseStack, tickDelta, AchievementRendererContext.Medium.OVERVIEW);
   }

   public static AchievementRendererContext forDetailsPane(Vec2d origin, Vec2d size, PoseStack poseStack, float tickDelta) {
      return new AchievementRendererContext(origin, size, poseStack, tickDelta, AchievementRendererContext.Medium.DETAILS);
   }

   public static AchievementRendererContext forModifierList(Vec2d origin, Vec2d size, PoseStack poseStack, float tickDelta) {
      return new AchievementRendererContext(origin, size, poseStack, tickDelta, AchievementRendererContext.Medium.MODIFIERS);
   }

   public static AchievementRendererContext forStatsPane(Vec2d origin, Vec2d size, PoseStack poseStack, float tickDelta) {
      return new AchievementRendererContext(origin, size, poseStack, tickDelta, AchievementRendererContext.Medium.STATS);
   }

   public void addModifier(ITaskModifier modifier) {
      this.modifiers.add(modifier);
   }

   public List<ITaskModifier> getModifiers() {
      return this.modifiers;
   }

   public void renderIcon(ResourceLocation id, Vec2d position) {
      this.renderIcon(id, (int)position.getX(), (int)position.getY());
   }

   public void renderIcon(ResourceLocation id, int x, int y) {
      int previous = this.setShaderTexture(id);
      this.push();
      this.translate(x, y, 0.0);
      this.blit(0, 0, 0, 0, 16, 16, 16, 16);
      this.pop();
      this.setShaderTexture(previous);
   }

   public void renderProgressBar(String labelText, double progress, int x, int y, int width, int height, boolean drain) {
      this.push();
      this.translate(x, y, 0.0);
      MutableComponent label = new TextComponent(labelText).withStyle(ChatFormatting.WHITE);
      ScreenTextures.BOUNTY_PROGRESS_BAR.background().blit(this.getMatrices(), 0, 0, 0, width, height);
      ScreenTextures.BOUNTY_PROGRESS_BAR
         .foreground()
         .blit(this.getMatrices(), 0, 0, 0, drain ? (int)(width - width * progress) : (int)(width * progress), height);
      this.renderText(label, width / 2.0F, height / 2.0F, true, true, 16777215, true);
      this.pop();
   }

   public void renderModifiers() {
      int x = 2;
      int y = 1;

      for (ITaskModifier modifier : this.getModifiers()) {
         this.renderIcon(modifier.getRenderIcon(), x, y);
         if (this.isModifierHovered(x, y)) {
            this.renderTooltip(modifier.getTooltips());
         }

         y += 18;
      }
   }

   private boolean isModifierHovered(int x, int y) {
      Vec2d mouse = this.getMouse();
      return mouse.getX() >= x && mouse.getX() < x + 18 && mouse.getY() >= y && mouse.getY() < y + 18;
   }

   public Vec2d getOrigin() {
      return this.origin;
   }

   public Vec2d getSize() {
      return this.size;
   }

   public AchievementRendererContext.Medium getMedium() {
      return this.medium;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isOverview() {
      return this.medium == AchievementRendererContext.Medium.OVERVIEW;
   }

   public boolean isDetailsPane() {
      return this.medium == AchievementRendererContext.Medium.DETAILS;
   }

   public boolean isModifierList() {
      return this.medium == AchievementRendererContext.Medium.DETAILS;
   }

   public boolean isStatsPane() {
      return this.medium == AchievementRendererContext.Medium.DETAILS;
   }

   public void drawArrow(Vec2d position, Vec2d position1) {
   }

   public void addTimedTask(TimedTask task) {
      this.timedTask = task;
   }

   public TimedTask getTimedTask() {
      return this.timedTask;
   }

   public static enum Medium {
      OVERVIEW,
      DETAILS,
      MODIFIERS,
      STATS;
   }
}
