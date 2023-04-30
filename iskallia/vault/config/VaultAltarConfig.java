package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VaultAltarConfig extends Config {
   @Expose
   public float PULL_SPEED;
   @Expose
   public double PLAYER_RANGE_CHECK;
   @Expose
   public double ITEM_RANGE_CHECK;
   @Expose
   public int INFUSION_TIME;
   @Expose
   public int GROUP_DISPLAY_TICKS;

   @Override
   public String getName() {
      return "vault_altar";
   }

   @Override
   protected void reset() {
      this.PULL_SPEED = 1.0F;
      this.PLAYER_RANGE_CHECK = 32.0;
      this.ITEM_RANGE_CHECK = 8.0;
      this.INFUSION_TIME = 5;
      this.GROUP_DISPLAY_TICKS = 20;
   }

   private void spawnLuckyEffects(Level world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vec3 offset = MiscUtils.getRandomOffset(pos, rand, 2.0F);
         ((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, offset.x, offset.y, offset.z, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public class AltarConfigItem {
      @Expose
      public String ITEM_ID;
      @Expose
      public int MIN;
      @Expose
      public int MAX;

      public AltarConfigItem(String item, int min, int max) {
         this.ITEM_ID = item;
         this.MIN = min;
         this.MAX = max;
      }
   }
}
