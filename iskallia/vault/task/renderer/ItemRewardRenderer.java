package iskallia.vault.task.renderer;

import iskallia.vault.task.ItemRewardTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRewardRenderer {
   public static class Achievement extends TaskRenderer<ItemRewardTask, AchievementRendererContext> {
      @OnlyIn(Dist.CLIENT)
      public void onRender(ItemRewardTask task, AchievementRendererContext context) {
         if (context.isDetailsPane()) {
            MutableComponent label = new TextComponent("Items").withStyle(ChatFormatting.BLACK).withStyle(ChatFormatting.UNDERLINE);
            context.renderText(label, (int)(context.getSize().getX() / 2.0), 0.0F, true, false);
            context.translate(0.0, 9 + 2, 0.0);
            List<ItemStack> stacks = task.getConfig().stacks;
            int width = stacks.size() * 18;
            context.translate(context.getSize().getX() / 2.0 - width / 2.0, 0.0, 0.0);
            context.push();

            for (ItemStack stack : stacks) {
               context.renderStack(stack, 0, 0, 1.0F, true, false);
               if (this.isMouseOver(task, context)) {
                  context.renderTooltip(stack.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL));
               }

               context.translate(18.0, 0.0, 0.0);
            }

            context.pop();
            context.translate(0.0, 20.0, 0.0);
         }

         super.onRender(task, context);
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isMouseOver(ItemRewardTask task, AchievementRendererContext context) {
         if (!context.isDetailsPane()) {
            return super.isMouseOver(task, context);
         } else {
            Vec2d mouse = context.getMouse();
            return mouse.getX() >= 0.0 && mouse.getX() < 18.0 && mouse.getY() >= 0.0 && mouse.getY() < 18.0;
         }
      }
   }
}
