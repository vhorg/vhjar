package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.KillEntityTask;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.bounty.element.BountyElement;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.util.GroupUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class KillEntityTaskElement extends AbstractTaskElement<KillEntityTask> {
   private List<Component> extendedDisplay = new ArrayList<>();

   protected KillEntityTaskElement(ISpatial spatial, KillEntityTask task, BountyElement.Status status) {
      super(spatial, task, status);
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
      List<MutableComponent> description = new ArrayList<>();
      EntityPredicate filter = this.getTask().getProperties().getFilter();
      Component entityText = GroupUtils.getEntityName(filter);
      description.add(new TextComponent("Kill ").append(entityText).append(" Mobs in:").withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK)));
      description.addAll(this.getDimensionsForDescription());
      return description;
   }

   @Override
   protected MutableComponent getTargetDisplayName() {
      return new TextComponent("Click to view in Bestiary.");
   }

   @Override
   protected List<Component> getExtendedDisplay() {
      return List.of(this.getTargetDisplayName());
   }
}
