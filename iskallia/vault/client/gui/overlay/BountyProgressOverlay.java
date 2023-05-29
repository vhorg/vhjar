package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.bounty.client.ClientBountyData;
import iskallia.vault.bounty.task.CompletionTask;
import iskallia.vault.bounty.task.ItemDiscoveryTask;
import iskallia.vault.bounty.task.ItemSubmissionTask;
import iskallia.vault.bounty.task.KillEntityTask;
import iskallia.vault.bounty.task.MiningTask;
import iskallia.vault.bounty.task.Task;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.bounty.element.BountyElement;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.util.TextUtil;
import java.text.DecimalFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.registries.ForgeRegistries;

public class BountyProgressOverlay implements IIngameOverlay {
   protected static final ResourceLocation BOUNTY_PROGRESS_LOCATION = VaultMod.id("textures/gui/bounty_progress.png");

   public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
      Minecraft mc = Minecraft.getInstance();
      if (!mc.options.hideGui) {
         if (!ClientBountyData.INSTANCE.getBounties().isEmpty()) {
            if (ModKeybinds.bountyStatusKey.isDown()) {
               this.renderBounties(gui, poseStack, partialTick, width, height);
            }
         }
      }
   }

   private void renderBounties(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
      Minecraft mc = Minecraft.getInstance();
      ProfilerFiller profiler = mc.getProfiler();
      Player player = mc.player;
      if (player != null) {
         profiler.push("BountyProgressOverlay");
         int midX = mc.getWindow().getGuiScaledWidth() / 2;
         int midY = mc.getWindow().getGuiScaledHeight() / 2;
         int bountyHeight = 50;
         BountyList bounties = ClientBountyData.INSTANCE.getBounties();
         int startY = midY - bounties.size() * bountyHeight / 2;
         int y = startY;

         for (Bounty bounty : bounties) {
            RenderSystem.setShaderTexture(0, BOUNTY_PROGRESS_LOCATION);
            this.renderBounty(bounty, gui, poseStack, midX, y);
            y += bountyHeight + 5;
         }

         profiler.pop();
      }
   }

   private void renderBounty(Bounty bounty, ForgeIngameGui gui, PoseStack poseStack, int midX, int startY) {
      Task<?> task = bounty.getTask();
      int width = 110;
      int iconY = 62;
      int iconWidth = 14;
      int iconHeight = 16;
      BountyElement.Status status = task.getProperties().getRewardPool().equalsIgnoreCase("legendary")
         ? BountyElement.Status.LEGENDARY
         : BountyElement.Status.ACTIVE;
      ChatFormatting color = status == BountyElement.Status.LEGENDARY ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
      TextComponent target;
      int iconX;
      if (task instanceof KillEntityTask killEntityTask) {
         target = TextUtil.formatLocationPathAsProperNoun(killEntityTask.getProperties().getEntityId());
         iconX = 0;
      } else if (task instanceof CompletionTask completionTask) {
         target = BountyScreen.OBJECTIVE_NAME.getOrDefault(completionTask.getProperties().getId(), new TextComponent("Empty - Report to Dev"));
         iconX = 14;
      } else if (task instanceof ItemSubmissionTask itemSubmissionTask) {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(itemSubmissionTask.getProperties().getItemId());
         target = item == null
            ? TextUtil.formatLocationPathAsProperNoun(itemSubmissionTask.getProperties().getItemId())
            : new TextComponent(item.getDescription().getString());
         iconX = 28;
      } else if (task instanceof ItemDiscoveryTask itemDiscoveryTask) {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(itemDiscoveryTask.getProperties().getItemId());
         target = item == null
            ? TextUtil.formatLocationPathAsProperNoun(itemDiscoveryTask.getProperties().getItemId())
            : new TextComponent(item.getDescription().getString());
         iconX = 42;
      } else {
         if (!(task instanceof MiningTask miningTask)) {
            return;
         }

         Block block = (Block)ForgeRegistries.BLOCKS.getValue(miningTask.getProperties().getBlockId());
         target = block == null
            ? TextUtil.formatLocationPathAsProperNoun(miningTask.getProperties().getBlockId())
            : new TextComponent(block.getName().getString());
         iconX = 56;
      }

      target.getStyle().applyFormat(color);
      int headerX = 0;
      int headerY = 14;
      int headerHeight = 22;
      int x = 1;
      gui.blit(poseStack, x + 3, startY + 23, 20, 36, 104, 15);
      gui.blit(poseStack, x, startY + 2, headerX, headerY, width, headerHeight);
      int iconBoxX = 0;
      int iconBoxY = 36;
      int iconBoxWidth = 20;
      int iconBoxHeight = 26;
      gui.blit(poseStack, x + 4, startY, iconBoxX, iconBoxY, iconBoxWidth, iconBoxHeight);
      gui.blit(poseStack, x + 7, startY + 5, iconX, iconY, iconWidth, iconHeight);
      gui.blit(poseStack, x + 5, startY + 28, 0, 0, 100, 7);
      float progress = (float)(task.getAmountObtained() / task.getProperties().getAmount());
      gui.blit(poseStack, x + 5, startY + 28, 0, 7, (int)(100.0F * progress), 7);
      DecimalFormat df = new DecimalFormat("0");
      TextComponent progressComponent = new TextComponent(df.format(task.getAmountObtained()) + "/" + df.format(task.getProperties().getAmount()));
      int progressX = x + width / 2 - Minecraft.getInstance().font.width(progressComponent) / 2;
      GuiComponent.drawString(poseStack, Minecraft.getInstance().font, target, x + 25, startY + 9, color.getColor());
      GuiComponent.drawString(poseStack, Minecraft.getInstance().font, progressComponent, progressX, startY + 27, 16777215);
   }
}
