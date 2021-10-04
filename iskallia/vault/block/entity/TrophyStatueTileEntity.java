package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrophyStatueTileEntity extends LootStatueTileEntity {
   private static final Random rand = new Random();
   private WeekKey week = null;
   private PlayerVaultStatsData.PlayerRecordEntry recordEntry = null;

   public TrophyStatueTileEntity() {
      super(ModBlocks.TROPHY_STATUE_TILE_ENTITY);
   }

   public WeekKey getWeek() {
      return this.week;
   }

   public void setWeek(WeekKey week) {
      this.week = week;
   }

   public PlayerVaultStatsData.PlayerRecordEntry getRecordEntry() {
      return this.recordEntry;
   }

   public void setRecordEntry(PlayerVaultStatsData.PlayerRecordEntry recordEntry) {
      this.recordEntry = recordEntry;
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      if (this.field_145850_b.func_201670_d()) {
         this.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (rand.nextInt(4) == 0) {
         ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
         BlockPos pos = this.func_174877_v();
         Vector3d rPos = new Vector3d(
            pos.func_177958_n() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * (0.1 + rand.nextFloat() * 0.6),
            pos.func_177956_o() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 0.2,
            pos.func_177952_p() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * (0.1 + rand.nextFloat() * 0.6)
         );
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
            ParticleTypes.field_197629_v, rPos.field_72450_a, rPos.field_72448_b, rPos.field_72449_c, 0.0, 0.0, 0.0
         );
         if (p != null) {
            p.field_187149_H = 0.0F;
            p.func_187146_c(-3229440);
         }
      }
   }

   @Override
   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.week != null) {
         nbt.func_218657_a("trophyWeek", this.week.serialize());
      }

      if (this.recordEntry != null) {
         nbt.func_218657_a("recordEntry", this.recordEntry.serialize());
      }

      return super.func_189515_b(nbt);
   }

   @Override
   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      if (nbt.func_150297_b("trophyWeek", 10)) {
         this.week = WeekKey.deserialize(nbt.func_74775_l("trophyWeek"));
      } else {
         this.week = null;
      }

      if (nbt.func_150297_b("recordEntry", 10)) {
         this.recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(nbt.func_74775_l("recordEntry"));
      } else {
         this.recordEntry = null;
      }

      super.func_230337_a_(state, nbt);
   }

   @Override
   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      if (this.week != null) {
         nbt.func_218657_a("trophyWeek", this.week.serialize());
      }

      if (this.recordEntry != null) {
         nbt.func_218657_a("recordEntry", this.recordEntry.serialize());
      }

      return nbt;
   }
}
