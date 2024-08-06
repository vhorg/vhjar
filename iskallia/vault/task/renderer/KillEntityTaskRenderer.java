package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.entity.PartialEntityGroup;
import iskallia.vault.task.KillEntityTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import iskallia.vault.util.GroupUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class KillEntityTaskRenderer {
   public static class Achievement extends AchievementRenderer.Base<KillEntityTask, AchievementRendererContext> {
      private long startTime;
      private int currentIndex;
      private final List<EntityType<?>> types = new ArrayList<>();

      public Achievement() {
      }

      public Achievement(String name, String description, ResourceLocation icon, Vec2d position, boolean hidden) {
         super(name, description, icon, position, hidden);
      }

      @OnlyIn(Dist.CLIENT)
      public void onRenderDetails(KillEntityTask task, AchievementRendererContext context) {
         super.onRenderDetails(task, context);
         this.updateEntityCycle();
         if (this.types.isEmpty()) {
            this.loadTypes(task);
         }

         if (!this.types.isEmpty()) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && this.types.get(this.currentIndex).create(level) instanceof LivingEntity livingEntity) {
               context.drawNineSlice(ScreenTextures.INSET_BLACK_BACKGROUND, 0, 0, (int)context.getSize().getX() - 3, 80);
               context.renderEntity(livingEntity, (int)(context.getSize().getX() / 2.0), 18, 30.0F);
               context.translate(0.0, 82.0, 0.0);
            }
         }
      }

      private void loadTypes(KillEntityTask task) {
         EntityPredicate filter = task.getConfig().filter;
         if (filter instanceof PartialEntityGroup group) {
            this.types.addAll(GroupUtils.getEntityTypes(group.getId()));
         } else if (filter instanceof PartialEntity entity) {
            Optional<CompoundTag> whole = entity.getNbt().asWhole();
            if (whole.isPresent()) {
               String id = whole.get().getString("id");
               EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
               this.types.add(type);
            }
         }
      }

      private void updateEntityCycle() {
         if (this.startTime == 0L) {
            this.startTime = System.currentTimeMillis();
         }

         long currentTime = System.currentTimeMillis() - this.startTime;
         if (currentTime >= 1500L) {
            this.startTime = System.currentTimeMillis();
            this.currentIndex++;
            if (this.currentIndex >= this.types.size()) {
               this.currentIndex = 0;
            }
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt();
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson();
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
      }
   }
}
