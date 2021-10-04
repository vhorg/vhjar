package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.block.BlockState;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultTreasure extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("treasure");

   public VaultTreasure() {
      super(ID);
   }

   public VaultTreasure(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isDoorOpen(World world) {
      return BlockPos.func_229383_a_(this.getBoundingBox())
         .<BlockState>map(world::func_180495_p)
         .filter(state -> state.func_177230_c() instanceof VaultDoorBlock)
         .anyMatch(state -> (Boolean)state.func_177229_b(VaultDoorBlock.field_176519_b));
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
      AxisAlignedBB blind = AxisAlignedBB.func_216363_a(this.boundingBox).func_72314_b(-2.0, -2.0, -2.0);
      AxisAlignedBB inner = blind.func_72314_b(-0.5, -0.5, -0.5);
      vault.getPlayers().forEach(vaultPlayer -> vaultPlayer.runIfPresent(world.func_73046_m(), playerEntity -> {
         if (blind.func_72326_a(playerEntity.func_174813_aQ()) && !this.isDoorOpen(world)) {
            playerEntity.func_195064_c(new EffectInstance(Effects.field_76440_q, 40));
            if (!playerEntity.func_175149_v() && inner.func_72326_a(playerEntity.func_174813_aQ())) {
               playerEntity.func_70097_a(DamageSource.field_76376_m, 1000000.0F);
               playerEntity.func_70606_j(0.0F);
            }
         }
      }));
   }
}
