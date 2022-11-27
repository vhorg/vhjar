package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

@Deprecated
public class PointLabelContainerElement<E extends PointLabelContainerElement<E>> extends ElasticContainerElement<E> {
   public PointLabelContainerElement(IPosition position, Supplier<Integer> skillPointValueSupplier, Supplier<Integer> knowledgePointValueSupplier) {
      super(Spatials.zero());
      PointLabelContainerElement.SkillPointLabelElement skillPointLabelElement = this.addElement(
         (PointLabelContainerElement.SkillPointLabelElement)new PointLabelContainerElement.SkillPointLabelElement(position, skillPointValueSupplier)
            .layout((screen, gui, parent, world) -> world.translateX(parent.right() - world.width()))
      );
      this.addElement(
         (PointLabelContainerElement.KnowledgePointLabelElement)new PointLabelContainerElement.KnowledgePointLabelElement(position, knowledgePointValueSupplier)
            .layout((screen, gui, parent, world) -> world.translateX(parent.right() - world.width()).translateY(skillPointLabelElement.isEnabled() ? 12 : 0))
      );
   }

   private abstract static class AbstractPointLabelElement<E extends PointLabelContainerElement.AbstractPointLabelElement<E>>
      extends DynamicLabelElement<Integer, E> {
      private static final TextColor SHADOW_COLOR = TextColor.parseColor("#000000");
      private final String type;
      private final ChatFormatting valueColor;

      private AbstractPointLabelElement(IPosition position, Supplier<Integer> valueSupplier, String type, ChatFormatting valueColor) {
         super(position, valueSupplier, LabelTextStyle.shadow(SHADOW_COLOR).right());
         this.type = type;
         this.valueColor = valueColor;
         this.setAutoResize(LabelAutoResize.NONE);
      }

      @Override
      public boolean isEnabled() {
         return this.valueSupplier.get() > 0;
      }

      @Override
      public boolean isVisible() {
         return this.isEnabled();
      }

      protected void onValueChanged(Integer value) {
         this.set(
            new TextComponent("")
               .append(new TextComponent(String.valueOf(value)).withStyle(this.valueColor))
               .append(new TextComponent(" unspent " + this.type + " point" + (value == 1 ? "" : "s")).withStyle(ChatFormatting.WHITE))
         );
      }
   }

   private static class KnowledgePointLabelElement
      extends PointLabelContainerElement.AbstractPointLabelElement<PointLabelContainerElement.KnowledgePointLabelElement> {
      private KnowledgePointLabelElement(IPosition position, Supplier<Integer> valueSupplier) {
         super(position, valueSupplier, "knowledge", ChatFormatting.AQUA);
      }
   }

   private static class SkillPointLabelElement extends PointLabelContainerElement.AbstractPointLabelElement<PointLabelContainerElement.SkillPointLabelElement> {
      private SkillPointLabelElement(IPosition position, Supplier<Integer> valueSupplier) {
         super(position, valueSupplier, "skill", ChatFormatting.YELLOW);
      }
   }
}
