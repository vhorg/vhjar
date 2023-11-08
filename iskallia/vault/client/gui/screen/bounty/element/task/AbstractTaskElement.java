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
import iskallia.vault.client.gui.framework.element.ClickableLabelElement;
import iskallia.vault.client.gui.framework.element.DynamicProgressElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.FakeOversizedItemSlotElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.client.gui.screen.bounty.element.BountyElement;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.TextUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
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
      LabelElement<?> descriptionElement = new LabelElement(
            Spatials.positionXY(2, descriptionLabel.bottom() + 1).width(this.getWorldSpatial().width() - 4),
            TextUtil.listToComponent((List<T>)this.getDescription()),
            LabelTextStyle.wrap()
         )
         .tooltip(Tooltips.shift(Tooltips.single(this::getTargetDisplayName), Tooltips.multi(this::getExtendedDisplay)));
      if (task instanceof KillEntityTask killEntityTask) {
         this.addElement(
               new ClickableLabelElement(
                  Spatials.positionXYZ(2, descriptionLabel.bottom() + 1, 1).width(this.getWorldSpatial().width() - 4).height(20),
                  Component.nullToEmpty(""),
                  LabelTextStyle.defaultStyle(),
                  () -> Minecraft.getInstance().setScreen(new BestiaryScreen(killEntityTask.getProperties().getFilter()))
               )
            )
            .layout((screen, gui, parent, world) -> {
               world.width(this.width() - 4);
               world.height(20);
            });
      }

      this.description = this.addElement(descriptionElement).layout((screen, gui, parent, world) -> world.width(this.width() - 4));
      int descriptionHeight = this.description.getTextStyle().getLabelHeight(this.description.getComponent(), this.description.width() - 2 / 2);
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
      String rewardPool = task.getProperties().getRewardPool();
      TextColor textColor = ModConfigs.COLORS.getColor(rewardPool);
      LabelElement<?> rewardText = new LabelElement(
         Spatials.positionXY(2, progressText.y() + progressText.height() + 7),
         new TextComponent("Rewards: (")
            .withStyle(ChatFormatting.BLACK)
            .append(new TextComponent(rewardPool).withStyle(Style.EMPTY.withColor(textColor)))
            .append(new TextComponent(")").withStyle(ChatFormatting.BLACK)),
         LabelTextStyle.defaultStyle()
      );
      int vaultExp = this.taskReward.getVaultExp();
      boolean hasVaultExp = vaultExp > 0;
      LabelElement<?> vaultExpLabel = null;
      if (hasVaultExp) {
         vaultExpLabel = new LabelElement(
            Spatials.positionXYZ(2, rewardText.bottom(), 1),
            new TextComponent("+" + vaultExp + " Vault XP").withStyle(ChatFormatting.YELLOW),
            LabelTextStyle.shadow()
         );
      }

      int stackX = 2;
      int stackY = hasVaultExp ? vaultExpLabel.y() + vaultExpLabel.height() + 1 : rewardText.bottom();

      for (OverSizedItemStack stack : this.taskReward.getRewardItems()) {
         this.addElement(
            new FakeOversizedItemSlotElement(Spatials.positionXY(stackX, stackY), () -> stack, () -> false)
               .setLabelStackCount()
               .tooltip(
                  Tooltips.shift(
                     Tooltips.multi(
                        () -> {
                           List<Component> tooltipLines = stack.stack().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);
                           if (stack.stack().getItem() instanceof VaultGearItem) {
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
                           List<Component> tooltipLines = stack.stack().getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED);
                           if (stack.stack().getItem() instanceof VaultGearItem) {
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
