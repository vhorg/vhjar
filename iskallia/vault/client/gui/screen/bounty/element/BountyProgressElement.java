package iskallia.vault.client.gui.screen.bounty.element;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.bounty.element.task.TaskScrollContainerElement;
import iskallia.vault.util.TextUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class BountyProgressElement extends ContainerElement<BountyProgressElement> {
   public BountyProgressElement(ISpatial spatial, Bounty active) {
      super(spatial);
      BountyElement.Status status = active.getTask().getProperties().getRewardPool().equalsIgnoreCase("legendary")
         ? BountyElement.Status.LEGENDARY
         : BountyElement.Status.ACTIVE;
      MutableComponent bountyTitle = TextUtil.formatLocationPathAsProperNoun(active.getTask().getTaskType()).withStyle(status.getDisplay().getStyle());
      HeaderElement headerElement = this.addElement(
         new HeaderElement(
            Spatials.positionXY(1, 0).width(this.getWorldSpatial().width() - 5).height(20),
            (TextComponent)bountyTitle,
            BountyScreen.TASK_ICON_MAP.get(active.getTask().getTaskType()),
            false
         )
      );
      headerElement.tooltip(Tooltips.single(status::getDisplay));
      TaskScrollContainerElement taskScrollContainerElement = this.addElement(
         new TaskScrollContainerElement(Spatials.positionXY(4, 20).width(this.width() - 11).height(this.height() - 53))
      );
      taskScrollContainerElement.setTaskElement(active.getTask(), status);
   }
}
