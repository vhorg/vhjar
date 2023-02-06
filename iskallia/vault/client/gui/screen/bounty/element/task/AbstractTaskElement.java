package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.CompletionTask;
import iskallia.vault.bounty.task.DamageTask;
import iskallia.vault.bounty.task.ItemDiscoveryTask;
import iskallia.vault.bounty.task.ItemSubmissionTask;
import iskallia.vault.bounty.task.KillEntityTask;
import iskallia.vault.bounty.task.MiningTask;
import iskallia.vault.bounty.task.Task;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.DynamicProgressElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bounty.element.BountyElement;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.util.TextUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

public abstract class AbstractTaskElement<T extends Task<?>> extends ElasticContainerElement<AbstractTaskElement<T>> {
   private T task;
   private TaskReward taskReward;
   final int marginY = 7;
   final int marginX = 2;
   final DynamicProgressElement<?> progressBar;
   private final LabelElement<?> description;

   protected AbstractTaskElement(ISpatial spatial, T task, BountyElement.Status status) {
      super(spatial);
      this.task = task;
      this.taskReward = task.getTaskReward();
      LabelElement<?> statusLabel = new LabelElement(
         Spatials.positionXY(2, 5), new TextComponent("Status: ").withStyle(ChatFormatting.BLACK).append(status.getDisplay()), LabelTextStyle.defaultStyle()
      );
      LabelElement<?> descriptionLabel = new LabelElement(
         Spatials.positionXY(2, 20), new TextComponent("Description:").withStyle(ChatFormatting.BLACK), LabelTextStyle.defaultStyle()
      );
      this.description = this.addElement(
            new LabelElement<LabelElement<LabelElement<?>>>(
                  Spatials.positionXY(2, descriptionLabel.bottom() + 1).width(this.getWorldSpatial().width() - 4),
                  TextUtil.listToComponent((List<T>)this.getDescription()),
                  LabelTextStyle.wrap()
               )
               .tooltip(Tooltips.shift(Tooltips.single(this::getTargetDisplayName), Tooltips.multi(this::getExtendedDisplay)))
         )
         .layout((screen, gui, parent, world) -> world.width(this.width() - 4));
      int descriptionHeight = this.getLabelHeight(this.description);
      LabelElement<?> progressLabel = new LabelElement(
         Spatials.positionXY(2, descriptionHeight + 18 + 15), new TextComponent("Progress:").withStyle(ChatFormatting.BLACK), LabelTextStyle.defaultStyle()
      );
      int barWidth = this.getWorldSpatial().width() - 4;
      this.progressBar = new DynamicProgressElement(
         Spatials.positionXY(2, progressLabel.bottom()), Spatials.size(barWidth, 8), ScreenTextures.BOUNTY_PROGRESS_BAR, this::getProgressPercentage
      );
      DecimalFormat df = new DecimalFormat("0");
      TextComponent progressComponent = new TextComponent(df.format(this.getAmountObtained()) + "/" + df.format(this.getAmountRequired()));
      LabelElement<?> progressText = new LabelElement(
            Spatials.positionXYZ(2, this.progressBar.y(), 5).width(this.getWorldSpatial().width() - 4),
            progressComponent.withStyle(ChatFormatting.WHITE),
            LabelTextStyle.center().shadow()
         )
         .layout((screen, gui, parent, world) -> world.width(this.getWorldSpatial().width() - 2));
      LabelElement<?> rewardText = new LabelElement(
         Spatials.positionXY(2, progressText.y() + progressText.height() + 7),
         new TextComponent("Rewards:").withStyle(ChatFormatting.BLACK),
         LabelTextStyle.defaultStyle()
      );
      LabelElement<?> vaultExpLabel = new LabelElement(
         Spatials.positionXYZ(2, rewardText.bottom(), 1),
         new TextComponent("+" + this.taskReward.getVaultExp() + " Vault XP").withStyle(ChatFormatting.YELLOW),
         LabelTextStyle.shadow()
      );
      int stackX = 2;
      int stackY = vaultExpLabel.y() + vaultExpLabel.height() + 1;

      for (ItemStack stack : this.taskReward.getRewardItems()) {
         this.addElement(
            (FakeItemSlotElement)new FakeItemSlotElement(Spatials.positionXY(stackX, stackY), () -> stack, () -> false)
               .setLabelStackCount()
               .tooltip(
                  Tooltips.shift(
                     Tooltips.multi(
                        () -> {
                           List<Component> tooltipLines = stack.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);
                           if (stack.getItem() instanceof VaultGearItem) {
                              tooltipLines.add(new TextComponent(" "));
                              tooltipLines.add(
                                 new TextComponent("Vault Gear level is locked to your Vault Level at the time of Bounty generation.")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                              );
                           }

                           return tooltipLines;
                        }
                     ),
                     Tooltips.multi(
                        () -> {
                           List<Component> tooltipLines = stack.getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED);
                           if (stack.getItem() instanceof VaultGearItem) {
                              tooltipLines.add(new TextComponent(" "));
                              tooltipLines.add(
                                 new TextComponent("Vault Gear level is locked to your Vault Level at the time of Bounty generation.")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                              );
                           }

                           return tooltipLines;
                        }
                     )
                  )
               )
         );
         stackX += 18;
         if (stackX + 18 > this.getWorldSpatial().width() - 1) {
            stackY += 18;
            stackX = 2;
         }
      }

      new LabelElement(
         Spatials.positionXYZ(2, rewardText.bottom(), 1),
         new TextComponent("+" + this.taskReward.getVaultExp() + " Vault XP").withStyle(ChatFormatting.YELLOW),
         LabelTextStyle.shadow()
      );
      this.addElements(
         statusLabel, new IElement[]{descriptionLabel, this.description, progressLabel, this.progressBar, progressText, rewardText, vaultExpLabel}
      );
   }

   private int getLabelHeight(LabelElement<?> label) {
      return label.getTextStyle().calculateLines(label.getComponent(), label.width() - 1) * 9;
   }

   private int getCenteredTextStartX(int width, TextComponent component) {
      return width / 2 - Minecraft.getInstance().font.width(component) / 2;
   }

   protected abstract float getProgressPercentage();

   protected abstract double getAmountRequired();

   protected abstract double getAmountObtained();

   protected abstract List<MutableComponent> getDescription();

   protected abstract MutableComponent getTargetDisplayName();

   protected abstract List<Component> getExtendedDisplay();

   protected List<MutableComponent> getDimensionsForDescription() {
      List<MutableComponent> dimensionComponents = new ArrayList<>();
      List<TextComponent> names = this.task.getProperties().getValidDimensions().stream().map(TextUtil::formatLocationPathAsProperNoun).toList();
      if (this.task.getProperties().isVaultOnly()) {
         dimensionComponents.add(new TextComponent(" - The Vault"));
      } else if (names.isEmpty()) {
         dimensionComponents.add(new TextComponent(" - Any Dimension"));
      } else {
         names.forEach(name -> dimensionComponents.add(new TextComponent(" - ").append(name)));
      }

      return dimensionComponents;
   }

   public T getTask() {
      return this.task;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AbstractTaskElement<?> that = (AbstractTaskElement<?>)o;
         return this.task != null ? this.task.getBountyId().equals(that.task.getBountyId()) : that.task == null;
      } else {
         return false;
      }
   }

   public static <E extends AbstractTaskElement<T>, T extends Task<?>> E create(ResourceLocation taskId, ISpatial spatial, T task, BountyElement.Status status) {
      if (taskId.equals(TaskRegistry.KILL_ENTITY)) {
         return (E)(new KillEntityTaskElement(spatial, (KillEntityTask)task, status));
      } else if (taskId.equals(TaskRegistry.DAMAGE_ENTITY)) {
         return (E)(new DamageEntityTaskElement(spatial, (DamageTask)task, status));
      } else if (taskId.equals(TaskRegistry.COMPLETION)) {
         return (E)(new CompletionTaskElement(spatial, (CompletionTask)task, status));
      } else if (taskId.equals(TaskRegistry.ITEM_SUBMISSION)) {
         return (E)(new ItemSubmissionTaskElement(spatial, (ItemSubmissionTask)task, status));
      } else if (taskId.equals(TaskRegistry.ITEM_DISCOVERY)) {
         return (E)(new ItemDiscoveryTaskElement(spatial, (ItemDiscoveryTask)task, status));
      } else if (taskId.equals(TaskRegistry.MINING)) {
         return (E)(new MiningTaskElement(spatial, (MiningTask)task, status));
      } else {
         throw new IllegalArgumentException("No Task Element defined for " + taskId);
      }
   }
}
