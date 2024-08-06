package iskallia.vault.task.renderer;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.task.DiscoverTransmogTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TransmogRewardRenderer {
   public static class Achievement extends TaskRenderer<DiscoverTransmogTask, AchievementRendererContext> {
      @OnlyIn(Dist.CLIENT)
      public void onRender(DiscoverTransmogTask task, AchievementRendererContext context) {
         if (context.isDetailsPane()) {
            MutableComponent label = new TextComponent("Transmogs").withStyle(ChatFormatting.BLACK).withStyle(ChatFormatting.UNDERLINE);
            context.renderText(label, (int)(context.getSize().getX() / 2.0), 0.0F, true, false);
            context.translate(0.0, 9 + 2, 0.0);
            context.push();
            List<ResourceLocation> transmogs = task.getConfig().transmogs;
            int width = transmogs.size() * 18;
            context.translate(context.getSize().getX() / 2.0 - width / 2.0, 0.0, 0.0);

            for (ResourceLocation transmog : transmogs) {
               Optional<Pair<? extends DynamicModel<?>, Item>> modelAndAssociatedItem = ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(transmog);
               if (!modelAndAssociatedItem.isEmpty()) {
                  Pair<? extends DynamicModel<?>, Item> pair = modelAndAssociatedItem.get();
                  Item associatedItem = (Item)pair.getSecond();
                  if (associatedItem instanceof VaultGearItem) {
                     VaultGearItem gearItem = (VaultGearItem)associatedItem;
                     ItemStack stack = gearItem.defaultItem();
                     VaultGearData gearData = VaultGearData.read(stack);
                     gearData.setState(VaultGearState.IDENTIFIED);
                     gearData.updateAttribute(ModGearAttributes.GEAR_MODEL, transmog);
                     gearData.write(stack);
                     context.renderStack(stack, 0, 0, 1.0F, true, false);
                     if (this.isMouseOver(task, context)) {
                        this.renderModel(stack, (DynamicModel<?>)pair.getFirst(), context);
                     }

                     context.translate(18.0, 0.0, 0.0);
                  }
               }
            }

            context.pop();
            context.translate(0.0, 20.0, 0.0);
         }

         super.onRender(task, context);
      }

      @OnlyIn(Dist.CLIENT)
      private void renderModel(ItemStack stack, DynamicModel<?> model, AchievementRendererContext context) {
         context.push();
         double realMouseY = Minecraft.getInstance().mouseHandler.ypos();
         int height = Minecraft.getInstance().getWindow().getScreenHeight();
         boolean flip = realMouseY > height / 2.0;
         int y = flip ? 0 : 110;
         context.translate(0.0, y, 300.0);
         MutableComponent display = new TextComponent(model.getDisplayName()).withStyle(ChatFormatting.BLACK);
         int width = Minecraft.getInstance().font.width(display) + 14;
         width = Math.max(width, 74);
         Vec2d mouse = context.getMouse();
         int mouseX = (int)Math.round(mouse.getX());
         int mouseY = (int)Math.round(mouse.getY());
         int center = width / 2;
         context.drawNineSlice(ScreenTextures.DEFAULT_WINDOW_BACKGROUND, mouseX - center, mouseY - 96 + y, width, 92);
         context.renderArmorModel(stack, mouseX, mouseY - 12 + y);
         context.drawNineSlice(ScreenTextures.DEFAULT_WINDOW_BACKGROUND, mouseX - center - 4, mouseY - 96 + y, width + 8, 20);
         context.renderText(display, mouseX, mouseY - 90 + y, true, false);
         float scale = 0.85F;
         context.translate(0.0, y, 300.0);
         context.scale(scale, scale, 1.0);
         context.renderText(new TextComponent("Transmog Only").withStyle(ChatFormatting.DARK_GRAY), mouseX / scale, (mouseY - 16) / scale, true, false);
         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isMouseOver(DiscoverTransmogTask task, AchievementRendererContext context) {
         if (!context.isDetailsPane()) {
            return super.isMouseOver(task, context);
         } else {
            Vec2d mouse = context.getMouse();
            return mouse.getX() >= 0.0 && mouse.getX() < 18.0 && mouse.getY() >= 0.0 && mouse.getY() < 18.0;
         }
      }
   }
}
