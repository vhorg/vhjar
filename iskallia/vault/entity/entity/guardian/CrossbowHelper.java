package iskallia.vault.entity.entity.guardian;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CrossbowHelper {
   public static void performShooting(
      Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack pCrossbowStack, float pVelocity, float pInaccuracy
   ) {
      List<ItemStack> list = getChargedProjectiles(pCrossbowStack);
      float[] afloat = getShotPitches(pShooter.getRandom());

      for (int i = 0; i < list.size(); i++) {
         ItemStack itemstack = list.get(i);
         boolean flag = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
         if (!itemstack.isEmpty()) {
            if (i == 0) {
               shootProjectile(pLevel, pShooter, pUsedHand, pCrossbowStack, itemstack, afloat[i], flag, pVelocity, pInaccuracy, 0.0F);
            } else if (i == 1) {
               shootProjectile(pLevel, pShooter, pUsedHand, pCrossbowStack, itemstack, afloat[i], flag, pVelocity, pInaccuracy, -10.0F);
            } else if (i == 2) {
               shootProjectile(pLevel, pShooter, pUsedHand, pCrossbowStack, itemstack, afloat[i], flag, pVelocity, pInaccuracy, 10.0F);
            }
         }
      }

      onCrossbowShot(pLevel, pShooter, pCrossbowStack);
   }

   private static void onCrossbowShot(Level pLevel, LivingEntity pShooter, ItemStack pCrossbowStack) {
      if (pShooter instanceof ServerPlayer serverplayer) {
         if (!pLevel.isClientSide) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, pCrossbowStack);
         }

         serverplayer.awardStat(Stats.ITEM_USED.get(pCrossbowStack.getItem()));
      }

      clearChargedProjectiles(pCrossbowStack);
   }

   private static void clearChargedProjectiles(ItemStack pCrossbowStack) {
      CompoundTag compoundtag = pCrossbowStack.getTag();
      if (compoundtag != null) {
         ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
         listtag.clear();
         compoundtag.put("ChargedProjectiles", listtag);
      }
   }

   private static List<ItemStack> getChargedProjectiles(ItemStack pCrossbowStack) {
      List<ItemStack> list = Lists.newArrayList();
      CompoundTag compoundtag = pCrossbowStack.getTag();
      if (compoundtag != null && compoundtag.contains("ChargedProjectiles", 9)) {
         ListTag listtag = compoundtag.getList("ChargedProjectiles", 10);

         for (int i = 0; i < listtag.size(); i++) {
            CompoundTag compoundtag1 = listtag.getCompound(i);
            list.add(ItemStack.of(compoundtag1));
         }
      }

      return list;
   }

   private static float[] getShotPitches(Random pRandom) {
      boolean flag = pRandom.nextBoolean();
      return new float[]{1.0F, getRandomShotPitch(flag, pRandom), getRandomShotPitch(!flag, pRandom)};
   }

   private static float getRandomShotPitch(boolean pIsHighPitched, Random pRandom) {
      float f = pIsHighPitched ? 0.63F : 0.43F;
      return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
   }

   private static void shootProjectile(
      Level pLevel,
      LivingEntity pShooter,
      InteractionHand pHand,
      ItemStack pCrossbowStack,
      ItemStack pAmmoStack,
      float pSoundPitch,
      boolean pIsCreativeMode,
      float pVelocity,
      float pInaccuracy,
      float pProjectileAngle
   ) {
      if (!pLevel.isClientSide) {
         boolean flag = pAmmoStack.is(Items.FIREWORK_ROCKET);
         Projectile projectile;
         if (flag) {
            projectile = new FireworkRocketEntity(pLevel, pAmmoStack, pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15F, pShooter.getZ(), true);
         } else {
            projectile = getArrow(pLevel, pShooter, pCrossbowStack, pAmmoStack);
            if (pIsCreativeMode || pProjectileAngle != 0.0F) {
               ((AbstractArrow)projectile).pickup = Pickup.CREATIVE_ONLY;
            }
         }

         if (pShooter instanceof CrossbowAttackMob crossbowattackmob) {
            crossbowattackmob.shootCrossbowProjectile(crossbowattackmob.getTarget(), pCrossbowStack, projectile, pProjectileAngle);
         } else {
            Vec3 vec31 = pShooter.getUpVector(1.0F);
            Quaternion quaternion = new Quaternion(new Vector3f(vec31), pProjectileAngle, true);
            Vec3 vec3 = pShooter.getViewVector(1.0F);
            Vector3f vector3f = new Vector3f(vec3);
            vector3f.transform(quaternion);
            projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), pVelocity, pInaccuracy);
         }

         pCrossbowStack.hurtAndBreak(flag ? 3 : 1, pShooter, p_40858_ -> p_40858_.broadcastBreakEvent(pHand));
         pLevel.addFreshEntity(projectile);
         pLevel.playSound((Player)null, pShooter.getX(), pShooter.getY(), pShooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pSoundPitch);
      }
   }

   private static AbstractArrow getArrow(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack) {
      FixedArrowEntity abstractarrow = new FixedArrowEntity(pLevel, pLivingEntity, pCrossbowStack);
      if (pLivingEntity instanceof Player) {
         abstractarrow.setCritArrow(true);
      }

      abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      abstractarrow.setShotFromCrossbow(true);
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, pCrossbowStack);
      if (i > 0) {
         abstractarrow.setPierceLevel((byte)i);
      }

      return abstractarrow;
   }
}
