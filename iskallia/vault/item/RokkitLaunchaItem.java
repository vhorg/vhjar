package iskallia.vault.item;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RokkitLaunchaItem extends ProjectileWeaponItem {
   public static final Predicate<ItemStack> FIREWORKS_ONLY = itemStack -> itemStack.is(Items.FIREWORK_ROCKET);

   public RokkitLaunchaItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   @Nonnull
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return FIREWORKS_ONLY;
   }

   public int getDefaultProjectileRange() {
      return 8;
   }

   @Nonnull
   public Rarity getRarity(@Nonnull ItemStack stack) {
      return Rarity.EPIC;
   }

   @Nonnull
   public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack launcherStack = player.getItemInHand(hand);
      ItemStack ammoStack = player.getProjectile(launcherStack);
      if (!ammoStack.isEmpty()) {
         player.getCooldowns().addCooldown(this, 100);
         if (!world.isClientSide()) {
            performShooting(world, player, hand, launcherStack, 1.0F, 1.0F);
            return InteractionResultHolder.consume(launcherStack);
         } else {
            return super.use(world, player, hand);
         }
      } else {
         world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.MASTER, 1.0F, 1.2F, false);
         Vec3 upVector = player.getUpVector(1.0F);
         Vector3f viewVector = new Vector3f(player.getViewVector(1.0F));
         viewVector.add(new Vector3f(upVector));

         for (int i = 0; i < 5; i++) {
            world.addParticle(
               ParticleTypes.SMOKE,
               player.getX() + viewVector.x() + world.random.nextFloat() * 0.5F,
               player.getY() + viewVector.y() + world.random.nextFloat() * 0.5F,
               player.getZ() + viewVector.z() + world.random.nextFloat() * 0.5F,
               0.0,
               0.1F,
               0.0
            );
         }

         player.displayClientMessage(new TextComponent("Rokkit Launcha requires Fireworks as ammo.").withStyle(ChatFormatting.RED), true);
         return InteractionResultHolder.fail(launcherStack);
      }
   }

   public static void performShooting(Level world, Player player, InteractionHand hand, ItemStack launcherStack, float velocity, float inaccuracy) {
      ItemStack ammoStack = player.getProjectile(launcherStack);
      float[] pitches = getShotPitches(player.getRandom());
      if (!ammoStack.isEmpty()) {
         shootProjectile(world, player, hand, launcherStack, ammoStack, pitches[0], velocity, inaccuracy, -10.0F);
         shootProjectile(world, player, hand, launcherStack, ammoStack, pitches[1], velocity, inaccuracy, 0.0F);
         shootProjectile(world, player, hand, launcherStack, ammoStack, pitches[2], velocity, inaccuracy, 10.0F);
         if (!player.isCreative()) {
            ammoStack.shrink(1);
         }
      }
   }

   private static void shootProjectile(
      Level world,
      Player player,
      InteractionHand hand,
      ItemStack shooterStack,
      ItemStack ammoStack,
      float pSoundPitch,
      float velocity,
      float inaccuracy,
      float angle
   ) {
      if (!world.isClientSide) {
         Projectile projectile = new FireworkRocketEntity(world, ammoStack, player, player.getX(), player.getEyeY() - 0.15F, player.getZ(), true);
         Vec3 upVector = player.getUpVector(1.0F);
         Quaternion quaternion = new Quaternion(new Vector3f(upVector), angle, true);
         Vector3f viewVector = new Vector3f(player.getViewVector(1.0F));
         viewVector.transform(quaternion);
         projectile.shoot(viewVector.x(), viewVector.y(), viewVector.z(), velocity, inaccuracy);
         shooterStack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(hand));
         world.addFreshEntity(projectile);
         world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, pSoundPitch);
      }
   }

   private static float[] getShotPitches(Random pRandom) {
      boolean highPitches = pRandom.nextBoolean();
      return new float[]{1.0F, getRandomShotPitch(highPitches, pRandom), getRandomShotPitch(!highPitches, pRandom)};
   }

   private static float getRandomShotPitch(boolean pIsHighPitched, Random pRandom) {
      float f = pIsHighPitched ? 0.63F : 0.43F;
      return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
   }
}
