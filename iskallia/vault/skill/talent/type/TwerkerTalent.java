package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TwerkerTalent extends PlayerTalent {
   @Expose
   private final int tickDelay = 5;
   @Expose
   private final int xRange = 2;
   @Expose
   private final int yRange = 1;
   @Expose
   private final int zRange = 2;

   public TwerkerTalent(int cost) {
      super(cost);
   }

   public int getTickDelay() {
      return 5;
   }

   public int getXRange() {
      return 2;
   }

   public int getYRange() {
      return 1;
   }

   public int getZRange() {
      return 2;
   }

   @Override
   public void tick(PlayerEntity player) {
      if (player.func_213453_ef()) {
         BlockPos playerPos = player.func_233580_cy_();
         BlockPos pos = new BlockPos(
            playerPos.func_177958_n() + player.func_70681_au().nextInt(this.getXRange() * 2 + 1) - this.getXRange(),
            playerPos.func_177956_o() - player.func_70681_au().nextInt(this.getYRange() * 2 + 1) + this.getYRange(),
            playerPos.func_177952_p() + player.func_70681_au().nextInt(this.getZRange() * 2 + 1) - this.getZRange()
         );
         Block block = player.field_70170_p.func_180495_p(pos).func_177230_c();
         if (block instanceof CropsBlock || block instanceof SaplingBlock) {
            BoneMealItem.applyBonemeal(new ItemStack(Items.field_196106_bc), player.field_70170_p, pos, player);
            ((ServerWorld)player.field_70170_p)
               .func_195598_a(ParticleTypes.field_197632_y, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), 100, 1.0, 0.5, 1.0, 0.0);
         }
      }
   }
}
