package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientProficiencyData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.gear.crafting.ProficiencyType;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProficiencyDisplayElement<E extends ProficiencyDisplayElement<E>> extends FakeItemSlotElement<E> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
   private final ProficiencyType proficiencyType;

   public ProficiencyDisplayElement(ISpatial spatial, ProficiencyType proficiencyType) {
      super(spatial, () -> ItemStack.EMPTY, () -> false, ScreenTextures.EMPTY, ScreenTextures.EMPTY, 18, 18);
      this.proficiencyType = proficiencyType;
      this.tooltip(
         () -> {
            MutableComponent display = this.proficiencyType.getDisplayName().copy().append(" Proficiency: ");
            display.append(
               new TextComponent(FORMAT.format(this.getProficiencyPercent() * 100.0F) + "%").withStyle(Style.EMPTY.withColor(this.getProficiencyBarColor()))
            );
            return display;
         }
      );
   }

   @Override
   public ItemStack getDisplayStack() {
      return this.proficiencyType.getDisplayStack().get();
   }

   public float getProficiencyPercent() {
      return ClientProficiencyData.getProficiency(this.proficiencyType);
   }

   public int getProficiencyBarColor() {
      float hue = 120.0F * this.getProficiencyPercent();
      return Color.getHSBColor(hue / 360.0F, 1.0F, 0.85F).getRGB();
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      IMutableSpatial frameOffset = this.worldSpatial.copy().translateXY(0, 19);
      renderer.render(ScreenTextures.PROFICIENCY_DISPLAY_FRAME, poseStack, frameOffset);
      int width = Math.round(16.0F * this.getProficiencyPercent());
      IMutableSpatial frameContent = frameOffset.copy().translateXY(1, 1).size(width, 4);
      renderer.renderColoredQuad(poseStack, this.getProficiencyBarColor(), frameContent);
   }
}
