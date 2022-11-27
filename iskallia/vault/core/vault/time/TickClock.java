package iskallia.vault.core.vault.time;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TickClock extends DataObject<TickClock> implements ISupplierKey<TickClock> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> GLOBAL_TIME = FieldKey.of("global_time", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> LOGICAL_TIME = FieldKey.of("logical_time", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> DISPLAY_TIME = FieldKey.of("display_time", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> PAUSED = FieldKey.of("paused", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> VISIBLE = FieldKey.of("visible", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TEXT_COLOR = FieldKey.of("text_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> ROTATION_TIME = FieldKey.of("rotation_time", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ClockModifier.List> MODIFIERS = FieldKey.of("modifiers", ClockModifier.List.class)
      .with(Version.v1_0, Adapter.ofCompound(ClockModifier.List::new), DISK.all())
      .register(FIELDS);

   public TickClock() {
      this.set(GLOBAL_TIME, Integer.valueOf(0));
      this.set(LOGICAL_TIME, Integer.valueOf(0));
      this.set(DISPLAY_TIME, Integer.valueOf(0));
      this.set(VISIBLE);
      this.set(TEXT_COLOR, Integer.valueOf(-1));
      this.set(ROTATION_TIME, Integer.valueOf(360));
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public TickClock addModifier(ClockModifier modifier) {
      this.get(MODIFIERS).add(modifier);
      return this;
   }

   protected abstract void tickTime();

   public final void tickServer(ServerLevel world) {
      this.set(GLOBAL_TIME, Integer.valueOf(this.get(GLOBAL_TIME) + 1));
      this.get(MODIFIERS).forEach(modifier -> modifier.tick(world, this));
      if (!this.has(PAUSED)) {
         this.tickTime();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void tickClient() {
   }

   protected int getTextColor(int time) {
      return this.get(TEXT_COLOR);
   }

   protected float getRotationTime(int time) {
      return this.get(ROTATION_TIME).intValue();
   }

   @OnlyIn(Dist.CLIENT)
   public void render(PoseStack matrixStack) {
      if (this.has(VISIBLE)) {
         int hourglassWidth = 12;
         int hourglassHeight = 16;
         int color = this.getTextColor(this.get(DISPLAY_TIME));
         String text = UIHelper.formatTimeString(Math.abs(this.get(DISPLAY_TIME)));
         FontHelper.drawStringWithBorder(matrixStack, text, -12.0F, 13.0F, color, -16777216);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, VaultOverlay.VAULT_HUD);
         float rotationTime = this.getRotationTime(this.get(DISPLAY_TIME));
         float degrees = this.get(DISPLAY_TIME).intValue() % rotationTime * 360.0F / rotationTime;
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(degrees));
         matrixStack.translate(-hourglassWidth / 2.0F, -hourglassHeight / 2.0F, 0.0);
         ScreenDrawHelper.drawTexturedQuads(
            buf -> ScreenDrawHelper.rect(buf, matrixStack).dim(hourglassWidth, hourglassHeight).texVanilla(1.0F, 36.0F, hourglassWidth, hourglassHeight).draw()
         );
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      }
   }
}
