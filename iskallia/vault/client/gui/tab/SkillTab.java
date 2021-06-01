package iskallia.vault.client.gui.tab;

import iskallia.vault.client.gui.screen.SkillTreeScreen;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;

public abstract class SkillTab extends Screen {
   protected SkillTreeScreen parentScreen;
   protected static Map<Class<? extends SkillTab>, Vector2f> persistedTranslations = new HashMap<>();
   protected static Map<Class<? extends SkillTab>, Float> persistedScales = new HashMap<>();
   protected Vector2f viewportTranslation;
   protected float viewportScale;
   protected boolean dragging;
   protected Vector2f grabbedPos;

   protected SkillTab(SkillTreeScreen parentScreen, ITextComponent title) {
      super(title);
      this.parentScreen = parentScreen;
      this.viewportTranslation = persistedTranslations.computeIfAbsent((Class<? extends SkillTab>)this.getClass(), clazz -> new Vector2f(0.0F, 0.0F));
      this.viewportScale = persistedScales.computeIfAbsent((Class<? extends SkillTab>)this.getClass(), clazz -> 1.0F);
      this.dragging = false;
      this.grabbedPos = new Vector2f(0.0F, 0.0F);
   }

   public abstract void refresh();

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      this.dragging = true;
      this.grabbedPos = new Vector2f((float)mouseX, (float)mouseY);
      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.dragging = false;
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public void func_212927_b(double mouseX, double mouseY) {
      if (this.dragging) {
         float dx = (float)(mouseX - this.grabbedPos.field_189982_i) / this.viewportScale;
         float dy = (float)(mouseY - this.grabbedPos.field_189983_j) / this.viewportScale;
         this.viewportTranslation = new Vector2f(this.viewportTranslation.field_189982_i + dx, this.viewportTranslation.field_189983_j + dy);
         this.grabbedPos = new Vector2f((float)mouseX, (float)mouseY);
      }
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      Vector2f midpoint = this.parentScreen.getContainerBounds().midpoint();
      boolean mouseScrolled = super.func_231043_a_(mouseX, mouseY, delta);
      double zoomingX = (mouseX - midpoint.field_189982_i) / this.viewportScale + this.viewportTranslation.field_189982_i;
      double zoomingY = (mouseY - midpoint.field_189983_j) / this.viewportScale + this.viewportTranslation.field_189983_j;
      int wheel = delta < 0.0 ? -1 : 1;
      double zoomTargetX = (zoomingX - this.viewportTranslation.field_189982_i) / this.viewportScale;
      double zoomTargetY = (zoomingY - this.viewportTranslation.field_189983_j) / this.viewportScale;
      this.viewportScale = (float)(this.viewportScale + 0.25 * wheel * this.viewportScale);
      this.viewportScale = (float)MathHelper.func_151237_a(this.viewportScale, 0.5, 5.0);
      this.viewportTranslation = new Vector2f((float)(-zoomTargetX * this.viewportScale + zoomingX), (float)(-zoomTargetY * this.viewportScale + zoomingY));
      return mouseScrolled;
   }

   public void func_231164_f_() {
      System.out.println(this.getClass().getSimpleName() + " closed.");
      persistedTranslations.put((Class<? extends SkillTab>)this.getClass(), this.viewportTranslation);
      persistedScales.put((Class<? extends SkillTab>)this.getClass(), this.viewportScale);
   }
}
