package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.screen.player.ArchetypesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ArchetypeDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.ArchetypeNodeTextures;
import iskallia.vault.client.gui.screen.player.legacy.widget.ArchetypeWidgetSelectable;
import iskallia.vault.config.ArchetypeGUIConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import iskallia.vault.util.MiscUtils;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ArchetypePanRegion extends AbstractPanRegion<ArchetypesElementContainerScreen> {
   private final Map<ResourceLocation, ArchetypeWidgetSelectable> widgetMap = new HashMap<>();
   private final ArchetypeDialog dialog;
   private ArchetypeWidgetSelectable selectedWidget;

   public ArchetypePanRegion(ArchetypeDialog dialog, ArchetypesElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Archetypes Tab"));
      this.dialog = dialog;
   }

   @Override
   protected AbstractPanRegion.CenterButton createCenterButton() {
      return null;
   }

   @Override
   protected Collection<? extends AbstractWidget> getWidgets() {
      return this.widgetMap.values();
   }

   @Override
   public void update() {
      this.widgetMap.clear();
      ArchetypeContainer archetypeContainer = this.parentScreen.getArchetypeContainer();

      for (Entry<ResourceLocation, ArchetypeGUIConfig.ArchetypeStyle> entry : ModConfigs.ARCHETYPES_GUI.getStyles().entrySet()) {
         ArchetypeGUIConfig.ArchetypeStyle style = entry.getValue();
         ArchetypeWidgetSelectable.IIconRenderer iconRenderer;
         if (style.getIcon().getPath().equals("builtin/player_face")) {
            LocalPlayer player = Minecraft.getInstance().player;
            iconRenderer = ArchetypeWidgetSelectable.PlayerFaceIconRenderer.of(player == null ? null : player.getDisplayName().getString(), 48, 1, -16777216);
         } else {
            TextureAtlasRegion icon = TextureAtlasRegion.of(ModTextureAtlases.ARCHETYPES, style.getIcon());
            iconRenderer = ArchetypeWidgetSelectable.TextureAtlasRegionIconRenderer.of(icon);
         }

         this.widgetMap
            .put(
               entry.getKey(),
               new ArchetypeWidgetSelectable(
                  entry.getKey(),
                  () -> archetypeContainer.getCurrentArchetype().getRegistryName(),
                  style.getX(),
                  style.getY(),
                  ArchetypeNodeTextures.NODE,
                  iconRenderer
               )
            );
      }

      this.loadViewportTransforms(false);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         Float midpoint = MiscUtils.getMidpoint(this.getBounds());
         int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.x);
         int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.y);
         boolean abilityWidgetClicked = false;

         for (ArchetypeWidgetSelectable widget : this.widgetMap.values()) {
            if (widget.isMouseOver(containerMouseX, containerMouseY) && widget.mouseClicked(containerMouseX, containerMouseY, button)) {
               if (this.selectedWidget != null) {
                  this.selectedWidget.deselect();
               }

               this.selectedWidget = widget;
               this.selectedWidget.select();
               this.dialog.setWidget(this.selectedWidget.getArchetypeId());
               abilityWidgetClicked = true;
               break;
            }
         }

         return abilityWidgetClicked;
      }
   }

   @Override
   protected void calculateViewportTranslation(double mouseX, double mouseY) {
      int minX = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;

      for (AbstractWidget widget : this.getWidgets()) {
         if (widget.x < minX) {
            minX = widget.x;
         }

         if (widget.x > maxX) {
            maxX = widget.x;
         }
      }

      float dx = (float)(mouseX - this.grabbedPos.x) / this.viewportScale;
      this.viewportTranslation = new Vec2(Mth.clamp(this.viewportTranslation.x + dx, -maxX, -minX), this.viewportTranslation.y);
      this.saveViewportTransforms();
   }

   @Override
   protected float clampViewportScale(float viewportScale) {
      return Mth.clamp(viewportScale, 1.0F, 5.0F);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      int minX = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;

      for (AbstractWidget widget : this.getWidgets()) {
         if (widget.x < minX) {
            minX = widget.x;
         }

         if (widget.x > maxX) {
            maxX = widget.x;
         }
      }

      this.viewportTranslation = new Vec2((float)Mth.clamp(this.viewportTranslation.x + delta * 16.0, -maxX, -minX), this.viewportTranslation.y);
      this.saveViewportTransforms();
      return false;
   }

   @Override
   public void renderTabForeground(PoseStack renderStack, Rectangle containerBounds, int mouseX, int mouseY, float pTicks, List<Runnable> postContainerRender) {
      RenderSystem.enableBlend();
      Float midpoint = MiscUtils.getMidpoint(this.getBounds());
      renderStack.pushPose();
      renderStack.translate(Math.round(this.viewportTranslation.x + midpoint.x), Math.round(this.viewportTranslation.y + midpoint.y), 0.0);
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.x);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.y);

      for (ArchetypeWidgetSelectable widget : this.widgetMap.values()) {
         widget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.popPose();
   }
}
