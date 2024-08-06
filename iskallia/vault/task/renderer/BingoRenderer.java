package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.BingoScrollMessage;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.counter.SlidingTimedTargetTaskCounter;
import iskallia.vault.task.renderer.context.BingoRendererContext;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BingoRenderer {
   public static class Leaf extends TaskRenderer<Task, BingoRendererContext> {
      public String name;
      public ResourceLocation icon;

      @OnlyIn(Dist.CLIENT)
      public void onRender(Task task, BingoRendererContext context) {
         List<Task> children = task.streamChildren().toList();
         if (task.isCompleted() && !children.isEmpty()) {
            for (Task child : children) {
               child.onRender(context);
            }
         } else {
            context.push();
            if (context.isExpandedView()) {
               context.scale(0.9F, 0.9F, 0.9F);
               addInProgressBackground(task, context);
            } else {
               context.scale(0.8F, 0.8F, 0.8F);
            }

            context.translate(-8.0, -8.0, 0.0);
            context.blit(this.icon, 0, 0, 0, 0, 16, 16, 16, 16);
            float textScale = 0.64F;
            context.scale(textScale, textScale, textScale);
            if (task instanceof IProgressTask progressTask) {
               TaskProgress progress = progressTask.getProgress();
               MutableComponent current = new TextComponent(String.valueOf(progress.getCurrent()));
               if (!task.isCompleted() && progress.getCurrent().longValue() > 0L) {
                  current.withStyle(Style.EMPTY.withColor(65280).withBold(true));
               }

               MutableComponent text = new TextComponent("").append(current).append("/" + progress.getTarget());
               context.renderText(
                  text, 8.5F / textScale, 18.0F / textScale, true, true, context.isCompleted() && !context.isExpandedView() ? '\uff00' : 16777215, false
               );
            }

            if (task instanceof ProgressConfiguredTask<?, ?> progressTask
               && progressTask.getCounter() instanceof SlidingTimedTargetTaskCounter<?, ?> counter
               && counter.getWindow() >= 20) {
               TextComponent text = new TextComponent(counter.getWindow() / 20 + "s");
               context.renderText(text, 1.5F / textScale, 2.0F / textScale, true, true, 16777215, false);
            }

            if (context.isCompleted()) {
               context.blit(VaultMod.id("textures/gui/bingo/checkmark.png"), (int)(10.0F / textScale), -((int)(3.0F / textScale)), 0, 0, 16, 16, 16, 16);
            }

            float descScale = 0.75F;
            context.scale(descScale, descScale, descScale);
            TextComponent description = new TextComponent(this.name);
            context.renderText(
               description,
               8.5F / textScale / descScale,
               25.5F / textScale / descScale,
               context.isExpandedView() ? 75 : 60,
               true,
               true,
               context.isCompleted() && !context.isExpandedView() ? 3912003 : -3355444,
               false
            );
            context.pop();
         }
      }

      private static void addInProgressBackground(Task task, BingoRendererContext context) {
         if (!task.isCompleted() && task instanceof IProgressTask progressTask && progressTask.getProgress().getCurrent().longValue() > 0L) {
            float percentage = (float)progressTask.getProgress().getCurrent().longValue() / (float)progressTask.getProgress().getTarget().longValue();
            int noProgressColor = 13434828;
            int doneColor = 65280;
            int red = (noProgressColor >> 16 & 0xFF) + (int)(((doneColor >> 16 & 0xFF) - (noProgressColor >> 16 & 0xFF)) * percentage);
            int green = (noProgressColor >> 8 & 0xFF) + (int)(((doneColor >> 8 & 0xFF) - (noProgressColor >> 8 & 0xFF)) * percentage);
            int blue = (noProgressColor & 0xFF) + (int)(((doneColor & 0xFF) - (noProgressColor & 0xFF)) * percentage);
            int color = red << 16 | green << 8 | blue;
            float cellSize = 42.0F;
            int padding = (int)(12.0F - percentage * 10.0F);
            context.drawColoredRect(-cellSize / 2.0F + padding, -9.0F, cellSize - padding * 2 + 1.0F, cellSize - 19.0F, color | 1140850688);
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.writeBits(this.name, buffer);
         Adapters.IDENTIFIER.writeBits(this.icon, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.name = Adapters.UTF_8.readBits(buffer).orElse(null);
         this.icon = Adapters.IDENTIFIER.readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.name).ifPresent(value -> nbt.put("name", value));
            Adapters.IDENTIFIER.writeNbt(this.icon).ifPresent(value -> nbt.put("icon", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
         this.icon = Adapters.IDENTIFIER.readNbt(nbt.get("icon")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.name).ifPresent(value -> json.add("name", value));
            Adapters.IDENTIFIER.writeJson(this.icon).ifPresent(value -> json.add("icon", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
         this.icon = Adapters.IDENTIFIER.readJson(json.get("icon")).orElse(null);
      }
   }

   public static class Root extends TaskRenderer<BingoTask, BingoRendererContext> {
      @OnlyIn(Dist.CLIENT)
      public void onRender(BingoTask root, BingoRendererContext context) {
         Minecraft minecraft = Minecraft.getInstance();
         context.setUuid(minecraft.player == null ? null : minecraft.player.getUUID());
         context.setExpandedView(minecraft.screen == null && ModKeybinds.openBingo.isDown());
         if (context.isExpandedView()) {
            this.renderExpanded(root, context);
         } else {
            this.renderCompact(root, context);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public void renderExpanded(BingoTask root, BingoRendererContext context) {
         Minecraft minecraft = Minecraft.getInstance();
         Window window = minecraft.getWindow();
         float cellSize = 42.0F;
         float gridWidth = cellSize * root.getWidth();
         float gridHeight = cellSize * root.getHeight();
         float maxGridWidth = 294.0F;
         float maxGridHeight = 210.0F;
         float scale = Math.min(maxGridWidth / gridWidth, maxGridHeight / gridHeight);
         context.push();
         context.translate(window.getGuiScaledWidth() / 2.0F - gridWidth * scale / 2.0F, 5.0, 0.0);
         context.scale(scale, scale, scale);
         context.drawColoredRect(-1.0F, -1.0F, gridWidth + 3.0F, gridHeight + 3.0F, Integer.MIN_VALUE);
         context.translate(cellSize / 2.0F, 13.0, 0.0);

         for (int column = 0; column < root.getWidth(); column++) {
            context.push();

            for (int row = 0; row < root.getHeight(); row++) {
               boolean selected = false;

               for (int index : root.getSelectedLine(context.getUuid())) {
                  if (root.getIndex(row, column) == index) {
                     selected = true;
                     break;
                  }
               }

               if (selected) {
                  context.drawColoredRect(-cellSize / 2.0F + 1.0F, -12.0F, cellSize - 1.0F, cellSize - 1.0F, 1694498560);
               }

               if (root.getState(row, column) != BingoTask.State.INCOMPLETE) {
                  context.drawColoredRect(-cellSize / 2.0F + 1.0F, -12.0F, cellSize - 1.0F, cellSize - 1.0F, 1677786880);
               }

               context.setCompleted(root.isCompleted(row, column));
               root.getChild(row, column).onRender(context);
               context.translate(0.0, cellSize, 0.0);
            }

            context.pop();
            context.translate(cellSize, 0.0, 0.0);
         }

         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      public void renderCompact(BingoTask root, BingoRendererContext context) {
         Minecraft minecraft = Minecraft.getInstance();
         Window window = minecraft.getWindow();
         Map<Integer, Task> children = new HashMap<>();

         for (int index : root.getSelectedLine(context.getUuid())) {
            children.put(index, root.getChild(index));
         }

         int sizeX = 30 * children.size();
         int spanX = 30 * (children.size() - 1);
         context.push();
         context.translate(window.getGuiScaledWidth() / 2.0F - spanX / 2.0F, 16.0, 0.0);
         children.forEach((indexx, child) -> {
            context.push();
            context.scale(1.2F, 1.2F, 1.2F);
            context.setCompleted(root.isCompleted(indexx));
            child.onRender(context);
            context.pop();
            context.translate(30.0, 0.0, 0.0);
         });
         context.pop();
         context.push();
         float gridHeight = 45.0F;
         float gridWidth = gridHeight / root.getHeight() * root.getWidth();
         float cellWidth = gridWidth / root.getWidth();
         float cellHeight = gridHeight / root.getHeight();
         context.translate(window.getGuiScaledWidth() / 2.0F - sizeX / 2.0F - 7.0F - gridWidth, 2.0, 0.0);
         context.drawColoredRect(-0.25F, -0.25F, gridWidth + 1.0F, gridHeight + 1.0F, 1677721600);

         for (int column = 0; column < root.getWidth(); column++) {
            context.push();

            for (int row = 0; row < root.getHeight(); row++) {
               context.drawColoredRect(
                  1.0F, 1.0F, cellWidth - 1.5F, cellHeight - 1.5F, root.getState(row, column) == BingoTask.State.INCOMPLETE ? 369098751 : 1677786880
               );

               for (int index : root.getSelectedLine(context.getUuid())) {
                  if (root.getIndex(row, column) == index) {
                     context.drawColoredRect(0.25F, 0.25F, cellWidth, cellHeight, 1090518784);
                     break;
                  }
               }

               context.translate(0.0, cellHeight, 0.0);
            }

            context.pop();
            context.translate(cellWidth, 0.0, 0.0);
         }

         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      public boolean onMouseScrolled(BingoTask task, double delta, BingoRendererContext context) {
         Minecraft minecraft = Minecraft.getInstance();
         context.setExpandedView(minecraft.screen == null && InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 258));
         if (context.isExpandedView()) {
            ModNetwork.CHANNEL.sendToServer(new BingoScrollMessage(delta));
            return true;
         } else {
            return super.onMouseScrolled(task, delta, context);
         }
      }
   }
}
