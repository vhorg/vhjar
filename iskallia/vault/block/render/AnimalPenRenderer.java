package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.BlackMarketBlock;
import iskallia.vault.block.entity.AnimalPenTileEntity;
import iskallia.vault.client.gui.framework.text.TextBorder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;

public class AnimalPenRenderer implements BlockEntityRenderer<AnimalPenTileEntity> {
   public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .optionalStart()
      .appendLiteral(':')
      .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
      .toFormatter();
   public static final float CORNERS = 0.0625F;
   public static final float MIN_Y = 0.25F;
   public static final float MAX_Y = 0.9375F;
   private final Minecraft mc = Minecraft.getInstance();
   private final BlockRenderDispatcher blockRenderer = this.mc.getBlockRenderer();

   public AnimalPenRenderer(Context context) {
   }

   public void render(
      AnimalPenTileEntity animalPenTile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = animalPenTile.getLevel();
      if (world != null) {
         Direction dir = (Direction)animalPenTile.getBlockState().getValue(BlackMarketBlock.FACING);
         if (animalPenTile.getInventory().getItem(1).getItem() instanceof BlockItem blockItem) {
            matrixStack.pushPose();
            float scale = 0.625F;
            matrixStack.scale(scale, 0.125F, scale);
            matrixStack.translate(0.1875F / scale, 0.015625, 0.1875F / scale);
            renderBlockState(
               blockItem.getBlock().defaultBlockState(), matrixStack, buffer, this.blockRenderer, animalPenTile.getLevel(), animalPenTile.getBlockPos()
            );
            matrixStack.popPose();
         }

         Animal animal = animalPenTile.getAnimalToReference();
         Animal dyingAnimal = animalPenTile.getDyingAnimalToReference();
         if (animal instanceof Bee bee) {
            int rotate = 0;
            if (dir == Direction.EAST) {
               rotate = 90;
            }

            if (dir == Direction.NORTH) {
               rotate = 180;
            }

            if (dir == Direction.WEST) {
               rotate = 270;
            }

            matrixStack.pushPose();
            CompoundTag tag = animalPenTile.getInventory().getItem(0).getOrCreateTag();
            int honeyLevel = 0;
            if (tag.contains("honeyLevel")) {
               honeyLevel = tag.getInt("honeyLevel");
            }

            float offsetx = (float)Math.sin(Math.toRadians(rotate)) / 3.0F;
            float offsety = 0.0F;
            float offsetz = (float)Math.cos(Math.toRadians(rotate)) / 3.0F;
            float scale = 0.5F;
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0.25F / scale - offsetx / scale, 0.28125, 0.25F / scale - offsetz / scale);
            renderBlockState(
               Blocks.SPRUCE_FENCE.defaultBlockState(), matrixStack, buffer, this.blockRenderer, animalPenTile.getLevel(), animalPenTile.getBlockPos()
            );
            matrixStack.translate(0.0, 0.75, 0.0);
            renderBlockState(
               (BlockState)((BlockState)Blocks.BEEHIVE.defaultBlockState().setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel)).setValue(BeehiveBlock.FACING, dir),
               matrixStack,
               buffer,
               this.blockRenderer,
               animalPenTile.getLevel(),
               animalPenTile.getBlockPos()
            );
            matrixStack.popPose();
         }

         this.mc.getEntityRenderDispatcher().setRenderShadow(false);
         if (this.mc.player != null && this.mc.player.isCrouching()) {
            matrixStack.pushPose();
            Vec3 lookAngle = this.mc.player.getLookAngle();
            if (this.mc.cameraEntity != null) {
               lookAngle = this.mc.cameraEntity.getLookAngle();
            }

            dir = Direction.getNearest(lookAngle.x, 0.0, lookAngle.z);
            int rot = 0;
            if (dir == Direction.WEST) {
               rot = 90;
            }

            if (dir == Direction.SOUTH) {
               rot = 180;
            }

            if (dir == Direction.EAST) {
               rot = 270;
            }

            List<Component> components = new ArrayList<>();
            if (animal != null) {
               components.add(animal.getType().getDescription());
               ItemStack invItem = animalPenTile.getInventory().getItem(0);
               if (invItem.hasTag()) {
                  CompoundTag tag = invItem.getOrCreateTag();
                  if (tag.contains("count")) {
                     components.add(new TextComponent("count: " + tag.getInt("count")));
                  }

                  if (tag.contains("breedTimer")) {
                     int time = tag.getInt("breedTimer");
                     components.add(new TextComponent("breed in: " + LocalTime.of(0, 0, 0).plusSeconds(time / 20).format(FORMATTER)));
                  }

                  if (tag.contains("shearTimer")) {
                     int time = tag.getInt("shearTimer");
                     components.add(new TextComponent("wool in: " + LocalTime.of(0, 0, 0).plusSeconds(time / 20).format(FORMATTER)));
                  }

                  if (tag.contains("eggTimer")) {
                     int time = tag.getInt("eggTimer");
                     components.add(new TextComponent("eggs in: " + LocalTime.of(0, 0, 0).plusSeconds(time / 20).format(FORMATTER)));
                  } else if (animal instanceof Chicken) {
                     components.add(new TextComponent("Eggs ready!"));
                     components.add(new TextComponent("(grab a bucket)"));
                  }

                  if (tag.contains("honeyLevel")) {
                     int level = tag.getInt("honeyLevel");
                     components.add(new TextComponent("honey level: " + level));
                  }

                  if (tag.contains("honeyReady") && tag.getBoolean("honeyReady")) {
                     components.add(new TextComponent("Honey ready!"));
                  } else if (tag.contains("pollenTimer")) {
                     int time = tag.getInt("pollenTimer");
                     components.add(new TextComponent("gathering pollen: " + LocalTime.of(0, 0, 0).plusSeconds(time / 20).format(FORMATTER)));
                  }
               }
            } else {
               components.add(new TextComponent("No Animal"));
            }

            float offset = components.size() * 0.1F - 0.1F;
            int animalCount = 0;
            if (animalPenTile.getInventory().getItem(0).hasTag() && animalPenTile.getInventory().getItem(0).getOrCreateTag().contains("count")) {
               animalCount = animalPenTile.getInventory().getItem(0).getOrCreateTag().getInt("count");
            }

            matrixStack.translate(0.5, 0.65 + 0.65 * (animalCount / 1000.0F), 0.5);
            matrixStack.translate(0.0, 0.33333334F + offset, 0.046666667F);
            matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));

            for (int i = 0; i < components.size(); i++) {
               FormattedCharSequence $$25 = components.get(i).getVisualOrderText();
               float $$26 = -TextBorder.DEFAULT_FONT.get().width($$25) / 2;
               TextBorder.DEFAULT_FONT
                  .get()
                  .drawInBatch8xOutline(
                     $$25,
                     $$26,
                     i * 10 - 20,
                     TextBorder.DEFAULT_COLOR.getValue(),
                     TextBorder.DEFAULT_BORDER_COLOR.getValue(),
                     matrixStack.last().pose(),
                     buffer,
                     15728880
                  );
            }

            matrixStack.popPose();
         }

         if (dyingAnimal != null) {
            if (dyingAnimal instanceof Sheep sheep) {
               ItemStack invItem = animalPenTile.getInventory().getItem(0);
               sheep.setSheared(invItem.hasTag() && invItem.getTag().contains("shearTimer"));
            }

            List<Integer> deathTime = animalPenTile.getDeathTime();
            if (!deathTime.isEmpty()) {
               dir = (Direction)animalPenTile.getBlockState().getValue(BlackMarketBlock.FACING);
               int rotx = 0;
               if (dir == Direction.WEST) {
                  rotx = 90;
               }

               if (dir == Direction.NORTH) {
                  rotx = 180;
               }

               if (dir == Direction.EAST) {
                  rotx = 270;
               }

               float yBodyRot = dyingAnimal.yBodyRot;
               float yBodyRotO = dyingAnimal.yBodyRotO;
               float yRot = dyingAnimal.getYRot();
               float xRot = dyingAnimal.getXRot();
               float yHeadRotO = dyingAnimal.yHeadRotO;
               float yHeadRot = dyingAnimal.yHeadRot;
               int count = 0;
               if (animalPenTile.getInventory().getItem(0).hasTag() && animalPenTile.getInventory().getItem(0).getOrCreateTag().contains("count")) {
                  count = animalPenTile.getInventory().getItem(0).getOrCreateTag().getInt("count");
               }

               float scale = 0.55F / dyingAnimal.getBbWidth();
               scale = 0.55F + (scale - 0.55F) / 2.0F;
               scale += scale * (count / 1000.0F);
               float offsetx = 0.0F;
               float offsety = 0.0F;
               float offsetz = 0.0F;
               if (animal instanceof Bee bee) {
                  int rotatex = 0;
                  if (dir == Direction.EAST) {
                     rotatex = 90;
                  }

                  if (dir == Direction.NORTH) {
                     rotatex = 180;
                  }

                  if (dir == Direction.WEST) {
                     rotatex = 270;
                  }

                  offsetx = (float)Math.sin(Math.toRadians(rotatex)) / 2.5F;
                  offsetz = (float)Math.cos(Math.toRadians(rotatex)) / 2.5F;
                  offsety = 0.25F;
               }

               dyingAnimal.yBodyRot = rotx;
               dyingAnimal.yBodyRotO = rotx;
               dyingAnimal.setYRot(rotx);
               dyingAnimal.setXRot(0.0F);
               dyingAnimal.xRotO = 0.0F;
               dyingAnimal.yHeadRot = dyingAnimal.getYRot();
               dyingAnimal.yHeadRotO = dyingAnimal.getYRot();
               dyingAnimal.animationSpeedOld = 0.0F;
               dyingAnimal.animationSpeed = 0.0F;
               dyingAnimal.animationPosition = 0.0F;
               dyingAnimal.tickCount = animalPenTile.getTickCount();
               matrixStack.pushPose();
               matrixStack.scale(scale, scale, scale);
               float finalOffsetx = offsetx;
               float finalOffsetz = offsetz;
               float finalOffsety = offsety;
               deathTime.forEach(
                  integer -> {
                     dyingAnimal.deathTime = integer;
                     Minecraft.getInstance()
                        .getEntityRenderDispatcher()
                        .render(
                           dyingAnimal,
                           0.5F / scale + finalOffsetx / scale,
                           0.125F / scale + finalOffsety / scale,
                           0.5F / scale + finalOffsetz / scale,
                           0.0F,
                           Minecraft.getInstance().getFrameTime(),
                           matrixStack,
                           buffer,
                           combinedLight
                        );
                  }
               );
               matrixStack.popPose();
               dyingAnimal.yBodyRot = yBodyRot;
               dyingAnimal.yBodyRotO = yBodyRotO;
               dyingAnimal.setYRot(yRot);
               dyingAnimal.setXRot(xRot);
               dyingAnimal.yHeadRotO = yHeadRotO;
               dyingAnimal.yHeadRot = yHeadRot;
            }
         }

         if (animal != null) {
            if (animal instanceof Sheep sheep) {
               ItemStack invItem = animalPenTile.getInventory().getItem(0);
               sheep.setSheared(invItem.hasTag() && invItem.getTag().contains("shearTimer"));
            }

            dir = (Direction)animalPenTile.getBlockState().getValue(BlackMarketBlock.FACING);
            int rotxx = 0;
            if (dir == Direction.WEST) {
               rotxx = 90;
            }

            if (dir == Direction.NORTH) {
               rotxx = 180;
            }

            if (dir == Direction.EAST) {
               rotxx = 270;
            }

            float yBodyRotx = animal.yBodyRot;
            float yBodyRotOx = animal.yBodyRotO;
            float yRotx = animal.getYRot();
            float xRotx = animal.getXRot();
            float yHeadRotOx = animal.yHeadRotO;
            float yHeadRotx = animal.yHeadRot;
            int countx = 0;
            if (animalPenTile.getInventory().getItem(0).hasTag() && animalPenTile.getInventory().getItem(0).getOrCreateTag().contains("count")) {
               countx = animalPenTile.getInventory().getItem(0).getOrCreateTag().getInt("count");
            }

            float scale = 0.55F / animal.getBbWidth();
            scale = 0.55F + (scale - 0.55F) / 2.0F;
            scale += scale * (countx / 1000.0F);
            float offsetx = 0.0F;
            float offsety = 0.0F;
            float offsetz = 0.0F;
            if (animal instanceof Bee bee) {
               int rotatexx = 0;
               if (dir == Direction.EAST) {
                  rotatexx = 90;
               }

               if (dir == Direction.NORTH) {
                  rotatexx = 180;
               }

               if (dir == Direction.WEST) {
                  rotatexx = 270;
               }

               offsetx = (float)Math.sin(Math.toRadians(rotatexx)) / 2.5F;
               offsetz = (float)Math.cos(Math.toRadians(rotatexx)) / 2.5F;
               offsety = 0.25F;
            }

            animal.yBodyRot = rotxx;
            animal.yBodyRotO = rotxx;
            animal.setYRot(rotxx);
            animal.setXRot(0.0F);
            animal.xRotO = 0.0F;
            animal.yHeadRot = animal.getYRot();
            animal.yHeadRotO = animal.getYRot();
            matrixStack.pushPose();
            matrixStack.scale(scale, scale, scale);
            animal.deathTime = 0;
            animal.animationSpeedOld = 0.0F;
            animal.animationSpeed = 0.0F;
            animal.animationPosition = 0.0F;
            animal.tickCount = animalPenTile.getTickCount();
            this.mc
               .getEntityRenderDispatcher()
               .render(
                  animal,
                  0.5F / scale + offsetx / scale,
                  0.125F / scale + offsety / scale,
                  0.5F / scale + offsetz / scale,
                  0.0F,
                  this.mc.getFrameTime(),
                  matrixStack,
                  buffer,
                  combinedLight
               );
            matrixStack.popPose();
            animal.yBodyRot = yBodyRotx;
            animal.yBodyRotO = yBodyRotOx;
            animal.setYRot(yRotx);
            animal.setXRot(xRotx);
            animal.yHeadRotO = yHeadRotOx;
            animal.yHeadRot = yHeadRotx;
            this.mc.getEntityRenderDispatcher().setRenderShadow(true);
         }
      }
   }

   private static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos
   ) {
      try {
         for (RenderType type : RenderType.chunkBufferLayers()) {
            if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
               renderBlockState(state, matrixStack, buffer, blockRenderer, world, pos, type);
            }
         }
      } catch (Exception var8) {
      }
   }

   public static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos, RenderType type
   ) {
      ForgeHooksClient.setRenderType(type);
      blockRenderer.getModelRenderer()
         .tesselateBlock(
            world, blockRenderer.getBlockModel(state), state, pos, matrixStack, buffer.getBuffer(type), false, world.random, 0L, OverlayTexture.NO_OVERLAY
         );
      ForgeHooksClient.setRenderType(null);
   }
}
