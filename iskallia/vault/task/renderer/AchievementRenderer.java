package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.AchievementTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TimedTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import iskallia.vault.util.ResourceBoundary;
import iskallia.vault.util.TextUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class AchievementRenderer {
   public static class Base<T extends Task, C extends AchievementRendererContext> extends TaskRenderer<T, C> {
      protected String name;
      protected String description;
      protected ResourceLocation icon;
      protected Vec2d position;
      protected boolean hidden;

      public Base() {
      }

      public Base(String name, String description, ResourceLocation icon, Vec2d position, boolean hidden) {
         this.name = name;
         this.description = description;
         this.icon = icon;
         this.position = position;
         this.hidden = hidden;
      }

      @OnlyIn(Dist.CLIENT)
      public void onRender(T task, C context) {
         if (context.isOverview()) {
            if (!this.hidden) {
               this.onRenderOverview(task, context);
            }
         } else if (context.isDetailsPane()) {
            this.onRenderDetails(task, context);
         }

         for (Task child : task.getChildren()) {
            child.onRender(context);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public void onRenderOverview(T task, C context) {
         if (this.icon != null) {
            boolean completed = task.isCompleted();
            boolean mouseOver = this.isMouseOver(task, context);
            ResourceBoundary icon = SkillFrame.STAR.getResourceBoundary();
            int vOffset = 0;
            if (context.isSelected() || mouseOver) {
               vOffset = -31;
            } else if (completed) {
               vOffset = 31;
            }

            int previous = context.setShaderTexture(icon.getResource());
            context.blit(
               (int)Math.round(this.position.getX()),
               (int)Math.round(this.position.getY()),
               icon.getU(),
               icon.getV() + vOffset,
               icon.getWidth(),
               icon.getHeight(),
               256,
               256
            );
            context.setShaderTexture(previous);
            if (this.icon != null && !this.icon.equals(VaultMod.id("empty"))) {
               context.renderIcon(this.icon, this.position.add(7.0, 7.0));
            }

            if (mouseOver) {
               context.renderTooltip(List.of(new TextComponent(this.name)));
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public void onRenderDetails(T task, C context) {
         int lineHeight = 12;
         int width = (int)context.getSize().getX() - 4;
         if (!StringUtils.isBlank(this.name)) {
            context.translate(0.0, 4.0, 0.0);
            MutableComponent name = new TextComponent(this.name).withStyle(ChatFormatting.BLACK);
            context.renderText(name, 0.0F, 0.0F, false, false);
            context.translate(0.0, lineHeight + 4, 0.0);
         }

         if (!StringUtils.isBlank(this.description)) {
            MutableComponent description = new TextComponent(this.description).withStyle(ChatFormatting.DARK_GRAY);
            context.renderText(description, 0.0F, 0.0F, width, false, false, -1, false);
            int descriptionHeight = TextUtil.getLineHeight(description, width) * lineHeight;
            context.translate(0.0, descriptionHeight, 0.0);
         }

         if (task instanceof IProgressTask progressTask) {
            TaskProgress taskProgress = progressTask.getProgress();
            Number current = taskProgress.getCurrent();
            Number max = taskProgress.getTarget();
            double progress = current.doubleValue() / max.doubleValue();
            context.renderProgressBar(String.format("%s/%s", current, max), progress, 0, 0, width + 1, 10, false);
            context.translate(0.0, lineHeight, 0.0);
         }

         if (context.getTimedTask() != null) {
            TimedTask timedTask = context.getTimedTask();
            long elapsed = timedTask.getElapsed();
            long duration = timedTask.getDuration();
            double progress = (double)elapsed / duration;
            long durationMillis = (duration / 20L - elapsed / 20L) * 1000L;
            String timeRemaining = DurationFormatUtils.formatDuration(durationMillis, "mm:ss");
            context.renderProgressBar(timeRemaining, progress, 0, 0, width + 1, 10, true);
            context.translate(0.0, lineHeight, 0.0);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public boolean onMouseClicked(T task, int button, C context) {
         if (this.isMouseOver(task, context)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.75F));
            return true;
         } else {
            return super.onMouseClicked(task, button, context);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isMouseOver(T task, C context) {
         if (context.isOverview()) {
            if (this.position == null) {
               return super.isMouseOver(task, context);
            } else {
               Vec2d mouse = context.getMouse();
               return mouse.getX() >= this.position.getX()
                  && mouse.getX() < this.position.getX() + 30.0
                  && mouse.getY() >= this.position.getY()
                  && mouse.getY() < this.position.getY() + 30.0;
            }
         } else {
            return false;
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.asNullable().writeBits(this.name, buffer);
         Adapters.UTF_8.asNullable().writeBits(this.description, buffer);
         Adapters.IDENTIFIER.asNullable().writeBits(this.icon, buffer);
         Adapters.VEC_2D.writeBits(this.position, buffer);
         Adapters.BOOLEAN.writeBits(this.hidden, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.name = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
         this.description = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
         this.icon = Adapters.IDENTIFIER.asNullable().readBits(buffer).orElse(null);
         this.position = Adapters.VEC_2D.readBits(buffer).orElseThrow();
         this.hidden = Adapters.BOOLEAN.readBits(buffer).orElse(false);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.name).ifPresent(value -> nbt.put("name", value));
            Adapters.UTF_8.writeNbt(this.description).ifPresent(value -> nbt.put("description", value));
            Adapters.IDENTIFIER.writeNbt(this.icon).ifPresent(value -> nbt.put("icon", value));
            Adapters.VEC_2D.writeNbt(this.position).ifPresent(value -> nbt.put("position", value));
            Adapters.BOOLEAN.writeNbt(this.hidden).ifPresent(value -> nbt.put("hidden", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
         this.description = Adapters.UTF_8.readNbt(nbt.get("description")).orElse(null);
         this.icon = Adapters.IDENTIFIER.readNbt(nbt.get("icon")).orElse(null);
         this.position = Adapters.VEC_2D.readNbt(nbt.get("position")).orElse(new Vec2d());
         this.hidden = Adapters.BOOLEAN.readNbt(nbt.get("hidden")).orElse(false);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.name).ifPresent(value -> json.add("name", value));
            Adapters.UTF_8.writeJson(this.description).ifPresent(value -> json.add("description", value));
            Adapters.IDENTIFIER.writeJson(this.icon).ifPresent(value -> json.add("icon", value));
            Adapters.VEC_2D.writeJson(this.position).ifPresent(value -> json.add("position", value));
            Adapters.BOOLEAN.writeJson(this.hidden).ifPresent(value -> json.add("hidden", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
         this.description = Adapters.UTF_8.readJson(json.get("description")).orElse(null);
         this.icon = Adapters.IDENTIFIER.readJson(json.get("icon")).orElse(null);
         this.position = Adapters.VEC_2D.readJson(json.get("position")).orElse(new Vec2d());
         this.hidden = Adapters.BOOLEAN.readJson(json.get("hidden")).orElse(false);
      }
   }

   public static class DetailsRoot extends PanRegionRenderer<AchievementTask, AchievementRendererContext> {
      private final AchievementRenderer.Root root;

      public DetailsRoot(AchievementRenderer.Root root) {
         this.root = root;
      }

      protected Vec2d getSize(AchievementTask task, AchievementRendererContext context) {
         return context.getSize();
      }

      protected PanRegionRenderer.ScrollType getScrollType(AchievementTask task, AchievementRendererContext context) {
         return PanRegionRenderer.ScrollType.VERTICAL;
      }

      @OnlyIn(Dist.CLIENT)
      protected void onRenderForeground(AchievementTask task, AchievementRendererContext context) {
         context.push();
         context.translate(2.0, 2.0, 0.0);
         context.drawNineSlice(ScreenTextures.DEFAULT_WINDOW_BACKGROUND, -20, -20, (int)context.getSize().getX() + 40, (int)context.getSize().getY() + 120);
         if (this.root.selected != null) {
            task.streamChildren().toList().get(this.root.selected).onRender(context);
         }

         context.pop();
      }
   }

   public static class ModifierListRoot extends TaskRenderer<AchievementTask, AchievementRendererContext> {
      private final AchievementRenderer.Root root;

      public ModifierListRoot(AchievementRenderer.Root root) {
         this.root = root;
      }

      @OnlyIn(Dist.CLIENT)
      public void onRender(AchievementTask task, AchievementRendererContext context) {
         context.push();
         context.translate(0.0, 0.0, 0.0);
         context.drawNineSlice(ScreenTextures.INSET_GREY_BACKGROUND, 0, -1, (int)context.getSize().getX(), (int)context.getSize().getY() + 2);
         if (this.root.selected != null) {
            task.streamChildren().toList().get(this.root.selected).onRender(context);
            context.renderModifiers();
         }

         context.pop();
      }
   }

   public static class OverviewRoot extends PanRegionRenderer<AchievementTask, AchievementRendererContext> {
      private final AchievementRenderer.Root root;

      public OverviewRoot(AchievementRenderer.Root root) {
         this.root = root;
      }

      protected Vec2d getSize(AchievementTask task, AchievementRendererContext context) {
         return context.getSize();
      }

      protected PanRegionRenderer.ScrollType getScrollType(AchievementTask task, AchievementRendererContext context) {
         return PanRegionRenderer.ScrollType.ZOOM;
      }

      @OnlyIn(Dist.CLIENT)
      public void onRender(AchievementTask task, AchievementRendererContext context) {
         super.onRender(task, context);
         Vec2d centerButton = this.getCenterButtonPos(task, context);
         NineSlice.TextureRegion buttonTexture = this.isMouseOverCenterButton(task, context) ? ScreenTextures.BUTTON_EMPTY_HOVER : ScreenTextures.BUTTON_EMPTY;
         context.drawNineSlice(buttonTexture, (int)centerButton.getX(), (int)centerButton.getY(), 16, 16);
         int previous = context.setShaderTexture(ScreenTextures.UI_RESOURCE);
         context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         context.blit((int)centerButton.getX(), (int)centerButton.getY() - 1, 192, 0, 16, 16, 256, 256);
         context.setShaderTexture(previous);
      }

      @OnlyIn(Dist.CLIENT)
      protected void onRenderForeground(AchievementTask task, AchievementRendererContext context) {
         List<Task> children = task.streamChildren().toList();

         for (int i = 0; i < children.size(); i++) {
            Task child = children.get(i);
            context.setSelected(Objects.equals(i, this.root.selected));
            child.onRender(context);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public boolean onMouseClicked(AchievementTask task, int button, AchievementRendererContext context) {
         if (this.isMouseOverCenterButton(task, context)) {
            this.resetTranslation();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.75F));
            return true;
         } else if (!this.isInBounds(context.getMouse(), task, context)) {
            return false;
         } else {
            this.clickedPos = context.getMouse();
            context.pushMouse(context.getMouse().subtract(this.getTranslation(task, context)));
            this.dragging = true;
            int index = -1;
            List<Task> children = task.streamChildren().toList();

            for (int i = 0; i < children.size(); i++) {
               if (children.get(i).onMouseClicked(button, context)) {
                  this.dragging = false;
                  index = i;
                  break;
               }
            }

            if (index >= 0) {
               this.root.selected = index;
            }

            context.popMouse();
            return true;
         }
      }

      protected Vec2d getCenterButtonPos(AchievementTask task, AchievementRendererContext context) {
         return new Vec2d((int)this.getSize(task, context).getX() - 18, (int)this.getSize(task, context).getY() - 18);
      }

      protected boolean isMouseOverCenterButton(AchievementTask task, AchievementRendererContext context) {
         Vec2d centerButton = this.getCenterButtonPos(task, context);
         return context.getMouse().getX() >= centerButton.getX()
            && context.getMouse().getX() <= centerButton.getX() + 16.0
            && context.getMouse().getY() >= centerButton.getY()
            && context.getMouse().getY() <= centerButton.getY() + 16.0;
      }
   }

   public static class Root extends DelegatedTaskRenderer<AchievementTask, AchievementRendererContext> {
      private final AchievementRenderer.OverviewRoot overview = new AchievementRenderer.OverviewRoot(this);
      private final AchievementRenderer.DetailsRoot details = new AchievementRenderer.DetailsRoot(this);
      private final AchievementRenderer.ModifierListRoot modifiers = new AchievementRenderer.ModifierListRoot(this);
      private Integer selected;

      @OnlyIn(Dist.CLIENT)
      public TaskRenderer<AchievementTask, AchievementRendererContext> getDelegate(AchievementTask task, AchievementRendererContext context) {
         return (TaskRenderer<AchievementTask, AchievementRendererContext>)(switch (context.getMedium()) {
            case OVERVIEW -> this.overview;
            case DETAILS -> this.details;
            case MODIFIERS -> this.modifiers;
            case STATS -> null;
         });
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         this.overview.writeBits(buffer);
         this.details.writeBits(buffer);
         this.modifiers.writeBits(buffer);
         Adapters.INT.asNullable().writeBits(this.selected, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.overview.readBits(buffer);
         this.details.readBits(buffer);
         this.modifiers.readBits(buffer);
         this.selected = Adapters.INT.asNullable().readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            this.overview.writeNbt().ifPresent(tag -> nbt.put("overview", tag));
            this.details.writeNbt().ifPresent(tag -> nbt.put("details", tag));
            this.modifiers.writeNbt().ifPresent(tag -> nbt.put("modifiers", tag));
            Adapters.INT.asNullable().writeNbt(this.selected).ifPresent(tag -> nbt.put("selected", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.overview.readNbt(nbt.getCompound("overview"));
         this.details.readNbt(nbt.getCompound("details"));
         this.modifiers.readNbt(nbt.getCompound("modifiers"));
         this.selected = Adapters.INT.asNullable().readNbt(nbt.get("selected")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            this.overview.writeJson().ifPresent(tag -> json.add("overview", tag));
            this.details.writeJson().ifPresent(tag -> json.add("details", tag));
            this.modifiers.writeJson().ifPresent(tag -> json.add("modifiers", tag));
            Adapters.INT.asNullable().writeJson(this.selected).ifPresent(value -> json.add("selected", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.overview.readJson(json.getAsJsonObject("overview"));
         this.details.readJson(json.getAsJsonObject("details"));
         this.modifiers.readJson(json.getAsJsonObject("modifiers"));
         this.selected = Adapters.INT.asNullable().readJson(json.getAsJsonObject("selected")).orElse(null);
      }
   }
}
