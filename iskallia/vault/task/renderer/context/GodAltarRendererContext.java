package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.influence.VaultGod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class GodAltarRendererContext extends RendererContext {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/god_altar_hud.png");
   private final VaultGod god;
   private final GodAltarRendererContext.Medium medium;
   private boolean completed;

   protected GodAltarRendererContext(
      PoseStack matrices, float tickDelta, BufferSource bufferSource, Font font, VaultGod god, GodAltarRendererContext.Medium medium
   ) {
      super(matrices, tickDelta, bufferSource, font);
      this.god = god;
      this.medium = medium;
   }

   public static GodAltarRendererContext forHud(PoseStack matrices, float tickDelta, Font font, VaultGod god) {
      return new GodAltarRendererContext(
         matrices, tickDelta, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), font, god, GodAltarRendererContext.Medium.HUD
      );
   }

   public static GodAltarRendererContext forWorld(PoseStack matrices, float tickDelta, Font font, VaultGod god) {
      return new GodAltarRendererContext(
         matrices, tickDelta, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), font, god, GodAltarRendererContext.Medium.WORLD
      );
   }

   public VaultGod getGod() {
      return this.god;
   }

   public boolean isHud() {
      return this.medium == GodAltarRendererContext.Medium.HUD;
   }

   public boolean isWorld() {
      return this.medium == GodAltarRendererContext.Medium.WORLD;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

   public int getTextColor() {
      return this.getGod().getColor();
   }

   public int getProgressBarColor() {
      int color = this.getGod().getColor();
      return (color & 16579836) >> 1;
   }

   public void renderHeader(String description, boolean centered) {
      int previous = this.setShaderTexture(TEXTURE);
      this.setShaderColor(16777215);
      this.push();
      if (this.isWorld()) {
         this.translate(0.0, 0.0, -0.01);
      }

      Component text = new TextComponent(" - ")
         .withStyle(Style.EMPTY.withColor(6316128))
         .append(new TextComponent(description).withStyle(Style.EMPTY.withColor(13421772)));
      Component shadow = new TextComponent(" - ")
         .withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK))
         .append(new TextComponent(description).withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)));
      this.renderText(shadow, centered ? 66.0F : 12.0F, 13.0F, centered, true);
      this.renderText(text, centered ? 65.0F : 11.0F, 12.0F, centered, true);
      this.pop();
      this.setShaderTexture(previous);
      this.translate(0.0, 0.0, 0.0);
   }

   public void renderProgressBar(String description, double progress, String hint) {
      int previous = this.setShaderTexture(TEXTURE);
      this.setShaderColor(16777215);
      this.push();
      this.setShaderColor(16777215);
      this.setShaderTexture(previous);
      if (this.isWorld()) {
         this.translate(0.0, 0.0, -0.01);
      }

      MutableComponent title = new TextComponent(" - ")
         .withStyle(Style.EMPTY.withColor(6316128))
         .append(new TextComponent(description).withStyle(Style.EMPTY.withColor(13421772)))
         .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(6316128)))
         .append(new TextComponent(hint).withStyle(ChatFormatting.WHITE));
      MutableComponent titleShadow = new TextComponent(" - " + description)
         .withStyle(ChatFormatting.BLACK)
         .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)))
         .append(new TextComponent(hint).withStyle(ChatFormatting.BLACK));
      if (this.isWorld()) {
         this.renderText(titleShadow, 66.0F, 13.0F, true, true);
         this.renderText(title, 65.0F, 12.0F, true, true);
      } else {
         this.renderText(titleShadow, 12.0F, 13.0F, false, true);
         this.renderText(title, 11.0F, 12.0F, false, true);
      }

      this.pop();
      this.translate(0.0, 0.0, -0.01);
   }

   public void renderTimerBar(double progress, String hint) {
      int previous = this.setShaderTexture(TEXTURE);
      this.setShaderColor(16777215);
      this.push();
      int width = Minecraft.getInstance().font.width(this.god.getName() + "'s Challenge | " + hint);
      if (this.isWorld()) {
         this.blit(65 - width / 2, 1, 0, 0, width, 2, 256, 256);
      } else {
         this.blit(11, 1, 0, 0, width, 2, 256, 256);
      }

      this.setShaderColor(16777215);
      this.setShaderTexture(previous);
      if (this.isWorld()) {
         this.renderText(
            new TextComponent(this.god.getName() + "'s Challenge")
               .withStyle(ChatFormatting.BLACK)
               .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)))
               .append(new TextComponent(hint).withStyle(ChatFormatting.BLACK)),
            66.0F,
            -4.0F,
            true,
            true
         );
         this.renderText(
            new TextComponent(this.god.getName() + "'s Challenge")
               .withStyle(Style.EMPTY.withColor(this.getTextColor()))
               .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(6316128)))
               .append(new TextComponent(hint).withStyle(ChatFormatting.WHITE)),
            65.0F,
            -5.0F,
            true,
            true
         );
      } else {
         this.renderText(
            new TextComponent(this.god.getName() + "'s Challenge")
               .withStyle(ChatFormatting.BLACK)
               .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)))
               .append(new TextComponent(hint).withStyle(ChatFormatting.BLACK)),
            12.0F,
            -4.0F,
            false,
            true
         );
         this.renderText(
            new TextComponent(this.god.getName() + "'s Challenge")
               .withStyle(Style.EMPTY.withColor(this.getTextColor()))
               .append(new TextComponent(" | ").withStyle(Style.EMPTY.withColor(6316128)))
               .append(new TextComponent(hint).withStyle(ChatFormatting.WHITE)),
            11.0F,
            -5.0F,
            false,
            true
         );
      }

      this.pop();
      this.translate(0.0, 0.0, -0.01);
   }

   protected static enum Medium {
      HUD,
      WORLD;
   }
}
