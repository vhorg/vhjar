package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.damage.DamageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion.BlockInteraction;

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

   public ExplosionEffect(String name, float radius, double xOffset, double yOffset, double zOffset, boolean causesFire, float damage, BlockInteraction mode) {
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

   public BlockInteraction getMode() {
      return Enum.valueOf(BlockInteraction.class, this.mode);
   }

   @Override
   public void apply(VirtualWorld world, Vault vault, ServerPlayer player) {
      world.explode(
         player,
         player.getX() + this.getXOffset(),
         player.getY() + this.getYOffset(),
         player.getZ() + this.getZOffset(),
         this.getRadius(),
         this.causesFire(),
         this.getMode()
      );
      DamageUtil.shotgunAttack(player, entity -> entity.hurt(new DamageSource("explosion").setExplosion(), this.getDamage()));
   }
}
