package iskallia.vault.util;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class EntityHelper {
   private static final AABB BOX = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   public static int CHAMPION_COUNT = 30;
   public static int DUNGEON_COUNT = 10;
   public static int GUARDIAN_COUNT = 7;
   public static int TANK_COUNT = 10;
   public static int ASSASSIN_COUNT = 3;
   public static int HORDE_COUNT = 1;
   public static final Predicate<Entity> VAULT_TARGET_SELECTOR = entity -> !(entity instanceof Player)
      && entity instanceof LivingEntity livingEntity
      && livingEntity.isAlive()
      && !livingEntity.isInvulnerable();

   public static void changeHealth(LivingEntity entity, int healthChange) {
      float health = entity.getHealth();
      entity.setHealth(health + healthChange);
      if (entity.isDeadOrDying()) {
         entity.die(entity.getLastDamageSource() != null ? entity.getLastDamageSource() : DamageSource.GENERIC);
      }
   }

   public static void knockback(LivingEntity target, LivingEntity source) {
      double xDiff = source.getX() - target.getX();

      double zDiff;
      for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
         xDiff = (Math.random() - Math.random()) * 0.01;
      }

      target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
      target.knockback(1.0, xDiff, zDiff);
   }

   public static void knockbackIgnoreResist(LivingEntity target, LivingEntity source, float strength) {
      if (target != null && source != null) {
         double xDiff = source.getX() - target.getX();

         double zDiff;
         for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
            xDiff = (Math.random() - Math.random()) * 0.01;
         }

         target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
         LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(target, strength, xDiff, zDiff);
         if (!event.isCanceled()) {
            strength = event.getStrength();
            xDiff = event.getRatioX();
            zDiff = event.getRatioZ();
            target.hasImpulse = true;
            Vec3 vec3 = target.getDeltaMovement();
            Vec3 vec31 = new Vec3(xDiff, 0.0, zDiff).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0 - vec31.x, target.isOnGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y, vec3.z / 2.0 - vec31.z);
         }
      }
   }

   public static void knockbackWithStrength(LivingEntity target, LivingEntity source, float strength) {
      double xDiff = source.getX() - target.getX();

      double zDiff;
      for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
         xDiff = (Math.random() - Math.random()) * 0.01;
      }

      target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
      target.knockback(strength, xDiff, zDiff);
   }

   public static <T extends Entity> T changeSize(T entity, float size, Runnable callback) {
      changeSize(entity, size);
      callback.run();
      return entity;
   }

   public static <T extends Entity> T changeSize(T entity, float size) {
      entity.dimensions = entity.getDimensions(Pose.STANDING).scale(size);
      entity.refreshDimensions();
      return entity;
   }

   public static void giveItem(Player player, ItemStack itemStack) {
      boolean added = player.getInventory().add(itemStack);
      if (!added) {
         player.drop(itemStack, false, false);
      }
   }

   public static <T extends Entity> List<T> getNearby(LevelAccessor world, Vec3i pos, float radius, Class<T> entityClass) {
      AABB selectBox = BOX.move(pos.getX(), pos.getY(), pos.getZ()).inflate(radius);
      return world.getEntitiesOfClass(entityClass, selectBox, entity -> entity.isAlive() && !entity.isSpectator());
   }

   public static boolean isColliding(Entity entity) {
      return isColliding(entity.getLevel(), entity.getBoundingBox());
   }

   public static boolean isColliding(Level level, AABB boundingBox) {
      return Streams.stream(level.getCollisions(null, boundingBox)).findAny().isPresent();
   }

   public static void getEntitiesInRange(LevelAccessor levelAccessor, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result) {
      getEntitiesInRange(levelAccessor, AABBHelper.create(center, range + 4.0F), center, range, filter, result);
   }

   public static void getEntitiesInRange(LevelAccessor levelAccessor, AABB area, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result) {
      if (levelAccessor != null) {
         for (Entity entity : levelAccessor.getEntities((Entity)null, area, filter)) {
            if (MathUtilities.isAABBIntersectingOrInsideSphere(entity.getBoundingBox(), center, range)) {
               result.add((LivingEntity)entity);
            }
         }
      }
   }

   public static int getEntityValue(LivingEntity livingEntity) {
      if (ChampionLogic.isChampion(livingEntity)) {
         return CHAMPION_COUNT;
      } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("dungeon"), livingEntity)) {
         return DUNGEON_COUNT;
      } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("guardian"), livingEntity)) {
         return GUARDIAN_COUNT;
      } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("tank"), livingEntity)) {
         return TANK_COUNT;
      } else if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("assassin"), livingEntity)) {
         return ASSASSIN_COUNT;
      } else {
         return ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("horde"), livingEntity) ? HORDE_COUNT : 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderEntityInUI(LivingEntity entity, PoseStack matrices, BufferSource bufferSource, int x, int y, float scale, int mouseX, int mouseY) {
      matrices.pushPose();
      float lookX = (float)(-Math.atan((mouseX - x) / 40.0F));
      float lookY = (float)(-Math.atan((mouseY - y) / 40.0F));
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(x, y + entity.getEyeHeight() * scale, 250.0);
      posestack.scale(1.0F, 1.0F, -1.0F);
      RenderSystem.applyModelViewMatrix();
      matrices.translate(0.0, 0.0, 100.0);
      matrices.scale(scale, scale, scale);
      Quaternion entityRotation = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion lookRotation = Vector3f.XP.rotationDegrees(lookY * 20.0F);
      entityRotation.mul(lookRotation);
      matrices.mulPose(entityRotation);
      float yBodyRot = entity.yBodyRot;
      float yRot = entity.getYRot();
      float xRot = entity.getXRot();
      float yHeadRotO = entity.yHeadRotO;
      float yHeadRot = entity.yHeadRot;
      entity.yBodyRot = 180.0F + lookX * 20.0F;
      entity.setYRot(180.0F + lookX * 40.0F);
      entity.setXRot(-lookY * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      lookRotation.conj();
      entityrenderdispatcher.overrideCameraOrientation(lookRotation);
      entityrenderdispatcher.setRenderShadow(false);
      entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrices, bufferSource, 15728880);
      bufferSource.endBatch();
      entityrenderdispatcher.setRenderShadow(true);
      entity.yBodyRot = yBodyRot;
      entity.setYRot(yRot);
      entity.setXRot(xRot);
      entity.yHeadRotO = yHeadRotO;
      entity.yHeadRot = yHeadRot;
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
      matrices.popPose();
   }
}
