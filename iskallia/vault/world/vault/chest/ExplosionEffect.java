package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.DamageUtil;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.server.ServerWorld;

public class ExplosionEffect extends VaultChestEffect {
   @Expose
   private final float radius;
   @Expose
   private final double xOffset;
   @Expose
   private final double yOffset;
   @Expose
   private final double zOffset;
   @Expose
   private final boolean causesFire;
   @Expose
   private final float damage;
   @Expose
   private final String mode;

   public ExplosionEffect(String name, float radius, double xOffset, double yOffset, double zOffset, boolean causesFire, float damage, Mode mode) {
      super(name);
      this.radius = radius;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.zOffset = zOffset;
      this.causesFire = causesFire;
      this.damage = damage;
      this.mode = mode.name();
   }

   public float getRadius() {
      return this.radius;
   }

   public double getXOffset() {
      return this.xOffset;
   }

   public double getYOffset() {
      return this.yOffset;
   }

   public double getZOffset() {
      return this.zOffset;
   }

   public boolean causesFire() {
      return this.causesFire;
   }

   public float getDamage() {
      return this.damage;
   }

   public Mode getMode() {
      return Enum.valueOf(Mode.class, this.mode);
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      player.runIfPresent(
         world.func_73046_m(),
         playerEntity -> {
            world.func_217398_a(
               playerEntity,
               playerEntity.func_226277_ct_() + this.getXOffset(),
               playerEntity.func_226278_cu_() + this.getYOffset(),
               playerEntity.func_226281_cx_() + this.getZOffset(),
               this.getRadius(),
               this.causesFire(),
               this.getMode()
            );
            DamageUtil.shotgunAttack(playerEntity, entity -> entity.func_70097_a(new DamageSource("explosion").func_94540_d(), this.getDamage()));
         }
      );
   }
}
