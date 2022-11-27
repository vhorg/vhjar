package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.CompletionTask;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class CompletionTaskElement extends AbstractTaskElement<CompletionTask> {
   public static final TextComponent EMPTY_LINE = new TextComponent("");

   protected CompletionTaskElement(ISpatial spatial, CompletionTask task) {
      super(spatial, task);
   }

   @Override
   protected float getProgressPercentage() {
      return (float)(this.getAmountObtained() / this.getAmountRequired());
   }

   @Override
   protected double getAmountRequired() {
      return this.getTask().getProperties().getAmount();
   }

   @Override
   protected double getAmountObtained() {
      return this.getTask().getAmountObtained();
   }

   @Override
   public List<MutableComponent> getDescription() {
      List<MutableComponent> components = new ArrayList<>();
      components.add(new TextComponent("Complete Vaults!").withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)));
      components.add(EMPTY_LINE);
      components.add(new TextComponent("Objective:"));
      components.add(new TextComponent(" - ").append((Component)BountyScreen.OBJECTIVE_NAME.get(this.getTask().getProperties().getId())));
      return components;
   }

   @Override
   protected MutableComponent getTargetDisplayName() {
      return (MutableComponent)BountyScreen.OBJECTIVE_NAME.get(this.getTask().getProperties().getId());
   }

   @Override
   protected List<Component> getExtendedDisplay() {
      return List.of((Component)BountyScreen.OBJECTIVE_NAME.get(this.getTask().getProperties().getId()));
   }
}
