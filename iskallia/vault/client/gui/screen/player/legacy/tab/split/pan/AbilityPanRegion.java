package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.screen.player.AbilitiesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.AbilityDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityNodeTextures;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityWidgetSelectable;
import iskallia.vault.config.AbilitiesGUIConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.util.MiscUtils;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class AbilityPanRegion extends AbstractPanRegion<AbilitiesElementContainerScreen> {
   private final Map<String, AbilityWidgetSelectable> abilityWidgets = new HashMap<>();
   private final AbilityDialog abilityDialog;
   private AbilityWidgetSelectable selectedWidget;

   public AbilityPanRegion(AbilityDialog abilityDialog, AbilitiesElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Abilities Tab"));
      this.abilityDialog = abilityDialog;
   }

   @Override
   protected AbstractPanRegion.CenterButton createCenterButton() {
      return null;
   }

   @Override
   protected Collection<? extends AbstractWidget> getWidgets() {
      return this.abilityWidgets.values();
   }

   @Override
   public void update() {
      this.abilityWidgets.clear();
      AbilityTree abilityTree = this.parentScreen.getAbilityTree();

      for (Entry<String, AbilitiesGUIConfig.AbilityStyle> abilityEntry : ModConfigs.ABILITIES_GUI.getStyles().entrySet()) {
         String abilityName = abilityEntry.getKey();
         AbilitiesGUIConfig.AbilityStyle abilityStyle = abilityEntry.getValue();
         this.abilityWidgets
            .put(
               abilityName,
               new AbilityWidgetSelectable(
                  abilityName,
                  abilityTree,
                  abilityStyle.getX(),
                  abilityStyle.getY(),
                  AbilityNodeTextures.PRIMARY_NODE,
                  TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, abilityStyle.getIcon())
               )
            );
         int index = 1;

         for (Entry<String, AbilitiesGUIConfig.SpecializationStyle> specializationEntry : abilityStyle.getSpecializationStyles().entrySet()) {
            String specializationName = specializationEntry.getKey();
            this.abilityWidgets
               .put(
                  specializationName,
                  new AbilityWidgetSelectable(
                     specializationName,
                     abilityTree,
                     abilityStyle.getX(),
                     abilityStyle.getY() + 2 + 23 * index,
                     AbilityNodeTextures.SECONDARY_NODE,
                     TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, specializationEntry.getValue().getIcon())
                  )
               );
            index++;
         }
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

         for (AbilityWidgetSelectable abilityWidget : this.abilityWidgets.values()) {
            if (abilityWidget.isMouseOver(containerMouseX, containerMouseY) && abilityWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
               if (this.selectedWidget != null) {
                  this.selectedWidget.deselect();
               }

               this.selectedWidget = abilityWidget;
               this.selectedWidget.select();
               this.abilityDialog.setAbilityWidget(this.selectedWidget.getAbilityName());
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

      for (AbilityWidgetSelectable abilityWidget : this.abilityWidgets.values()) {
         abilityWidget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.popPose();
   }
}
