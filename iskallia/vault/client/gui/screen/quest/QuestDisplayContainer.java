package iskallia.vault.client.gui.screen.quest;

import iskallia.vault.client.gui.framework.element.CheckButtonElement;
import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.quest.QuestProgressMessage;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.quest.client.ClientQuestState;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

public class QuestDisplayContainer extends VerticalScrollClipContainer<QuestDisplayContainer> {
   private final QuestOverviewElementScreen parent;

   public QuestDisplayContainer(ISpatial spatial, QuestOverviewElementScreen parent) {
      super(spatial);
      this.parent = parent;
      Quest selectedQuest = this.parent.getSelectedQuest();
      if (selectedQuest != null) {
         int padding = 5;
         int marginX = 9;
         LabelElement<?> descriptionTitle = new LabelElement(
            Spatials.positionXYZ(marginX, padding, 5).width(this.innerWidth() - marginX).height(15),
            new TextComponent("Description:"),
            LabelTextStyle.wrap().shadow()
         );
         this.addElement(descriptionTitle);
         int y = padding + padding + descriptionTitle.height();
         int descriptionWidth = this.innerWidth() - 18;
         LabelElement<?> description = new LabelElement(
            Spatials.positionXY(marginX, y).width(descriptionWidth), selectedQuest.getDescription(), LabelTextStyle.wrap()
         );
         int descriptionHeight = description.getTextStyle().getLabelHeight(description.getComponent(), descriptionWidth);
         this.addElement(description).layout((screen, gui, parent1, world) -> world.width(descriptionWidth).height(descriptionHeight));
         y += padding + descriptionHeight;
         if (selectedQuest.getType().equals("checkmark")) {
            int buttonXY = 20;
            QuestState state = ClientQuestState.INSTANCE.getState();
            CheckButtonElement checkBox = new CheckButtonElement(
                  Spatials.positionXY(this.innerWidth() / 2 - buttonXY / 2, y).size(buttonXY, buttonXY),
                  () -> ModNetwork.CHANNEL.sendToServer(new QuestProgressMessage(selectedQuest.getId()))
               )
               .setDisabled(() -> !state.getInProgress().contains(selectedQuest.getId()))
               .setRenderButtonHeld(() -> state.getCompleted().contains(selectedQuest.getId()));
            this.addElement(checkBox);
            y += padding + checkBox.height();
         }

         LabelElement<?> rewardTitle = new LabelElement(
            Spatials.positionXYZ(marginX, y, 5).width(this.innerWidth() - marginX).height(15), new TextComponent("Reward:"), LabelTextStyle.wrap().shadow()
         );
         this.addElement(rewardTitle);
         y += padding + rewardTitle.height();
         Quest.QuestReward reward = selectedQuest.getReward();
         int vaultExp = reward.getVaultExp();
         if (vaultExp > 0) {
            LabelElement<?> vaultExpLabel = new LabelElement(
               Spatials.positionXYZ(marginX, y, 1), new TextComponent("+" + vaultExp + " Vault XP").withStyle(ChatFormatting.YELLOW), LabelTextStyle.shadow()
            );
            this.addElement(vaultExpLabel);
            y += padding + vaultExpLabel.height();
         }

         int skillPoints = reward.getSkillPoints();
         if (skillPoints > 0) {
            String text = skillPoints == 1 ? " Skill Point" : " Skill Points";
            LabelElement<?> skillPointLabel = new LabelElement(
               Spatials.positionXYZ(marginX, y, 1),
               new TextComponent("+" + skillPoints).withStyle(ChatFormatting.YELLOW).append(new TextComponent(text).withStyle(ChatFormatting.WHITE)),
               LabelTextStyle.shadow()
            );
            this.addElement(skillPointLabel);
            y += padding + skillPointLabel.height();
         }

         List<ItemStack> items = reward.getItems();
         if (!items.isEmpty()) {
            int stackX = marginX;
            int stackY = y;

            for (ItemStack stack : items) {
               FakeItemSlotElement<?> stackElement = new FakeItemSlotElement(Spatials.positionXY(stackX, stackY), () -> stack, () -> false)
                  .setLabelStackCount()
                  .tooltip(
                     Tooltips.shift(
                        Tooltips.multi(() -> stack.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL)),
                        Tooltips.multi(() -> stack.getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED))
                     )
                  );
               this.addElement(stackElement);
               stackX += 18;
               if (stackX + 18 > this.innerWidth() - 1) {
                  y += 18;
                  stackY += 18;
                  stackX = marginX;
               }
            }
         }

         y += padding + 18;
         this.addElement(new LabelElement(Spatials.positionXY(marginX, y), LabelTextStyle.defaultStyle()));
      }
   }
}
