package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.block.TreasureDoorBlock;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class VaultTreasure extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("treasure");

   public VaultTreasure() {
      super(ID);
   }

   public VaultTreasure(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isDoorOpen(Level world) {
      return BlockPos.betweenClosedStream(this.getBoundingBox())
         .<BlockState>map(world::getBlockState)
         .filter(state -> state.getBlock() instanceof TreasureDoorBlock)
         .anyMatch(state -> (Boolean)state.getValue(TreasureDoorBlock.OPEN));
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
      AABB blind = AABB.of(this.boundingBox).inflate(-2.0, -2.0, -2.0);
      AABB inner = blind.inflate(-0.5, -0.5, -0.5);
      vault.getPlayers().forEach(vaultPlayer -> vaultPlayer.runIfPresent(world.getServer(), playerEntity -> {
         if (blind.intersects(playerEntity.getBoundingBox()) && !this.isDoorOpen(world)) {
            playerEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            if (!playerEntity.isSpectator() && inner.intersects(playerEntity.getBoundingBox())) {
               playerEntity.hurt(DamageSource.MAGIC, 1000000.0F);
               playerEntity.setHealth(0.0F);
            }
         }
      }));
   }
}
