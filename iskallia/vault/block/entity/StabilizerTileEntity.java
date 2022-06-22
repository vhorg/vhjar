package iskallia.vault.block.entity;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.StabilizerCompassBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectSummonAndKillBossesObjective;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StabilizerTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Random rand = new Random();
   private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1.0, -1.0, -1.0, 1.0, 2.0, 1.0);
   private boolean active = false;
   private int timeout = 20;
   private final Set<Direction> highlightDirections = new HashSet<>();
   private final List<Object> particleReferences = new ArrayList<>();

   public StabilizerTileEntity() {
      super(ModBlocks.STABILIZER_TILE_ENTITY);
   }

   public void func_73660_a() {
      World world = this.func_145831_w();
      if (world instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)world;
         BlockState up = world.func_180495_p(this.func_174877_v().func_177984_a());
         if (!(up.func_177230_c() instanceof StabilizerBlock)) {
            world.func_175656_a(
               this.func_174877_v().func_177984_a(),
               (BlockState)ModBlocks.STABILIZER.func_176223_P().func_206870_a(StabilizerBlock.HALF, DoubleBlockHalf.UPPER)
            );
         }

         VaultRaid raid = VaultRaidData.get(sWorld).getAt(sWorld, this.func_174877_v());
         if (raid != null) {
            raid.getActiveObjective(ArchitectSummonAndKillBossesObjective.class).ifPresent(objective -> Plane.HORIZONTAL.func_239636_a_().forEach(dir -> {
               BlockPos compassPos = this.func_174877_v().func_177977_b().func_177972_a(dir);
               sWorld.func_175656_a(compassPos, (BlockState)ModBlocks.STABILIZER_COMPASS.func_176223_P().func_206870_a(StabilizerCompassBlock.DIRECTION, dir));
            }));
         }

         if (this.active && this.timeout > 0) {
            this.timeout--;
            if (this.timeout <= 0) {
               this.active = false;
               this.markForUpdate();
            }
         }
      } else {
         this.setupParticle();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void setupParticle() {
      if (this.particleReferences.size() < 3) {
         int toAdd = 3 - this.particleReferences.size();

         for (int i = 0; i < toAdd; i++) {
            ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
            Particle p = mgr.func_199280_a(
               (IParticleData)ModParticles.STABILIZER_CUBE.get(),
               this.field_174879_c.func_177958_n() + 0.5,
               this.field_174879_c.func_177956_o() + 0.5,
               this.field_174879_c.func_177952_p() + 0.5,
               0.0,
               0.0,
               0.0
            );
            this.particleReferences.add(p);
         }
      }

      this.particleReferences.removeIf(ref -> !((Particle)ref).func_187113_k());
      if (this.isActive()) {
         Vector3d particlePos = new Vector3d(
            this.field_174879_c.func_177958_n() + rand.nextFloat(),
            this.field_174879_c.func_177956_o() + rand.nextFloat() * 2.0F,
            this.field_174879_c.func_177952_p() + rand.nextFloat()
         );
         ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
         SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
            ParticleTypes.field_197629_v, particlePos.field_72450_a, particlePos.field_72448_b, particlePos.field_72449_c, 0.0, 0.0, 0.0
         );
         p.field_187149_H = 0.0F;
         p.func_187146_c(301982);

         for (Direction voteDirection : this.highlightDirections) {
            Vector3d dirPos = new Vector3d(
                  this.field_174879_c.func_177958_n() + rand.nextFloat(),
                  this.field_174879_c.func_177956_o() + rand.nextFloat() * 0.1,
                  this.field_174879_c.func_177952_p() + rand.nextFloat()
               )
               .func_72441_c(voteDirection.func_82601_c(), voteDirection.func_96559_d(), voteDirection.func_82599_e());
            p = (SimpleAnimatedParticle)mgr.func_199280_a(
               ParticleTypes.field_197629_v,
               dirPos.field_72450_a,
               dirPos.field_72448_b,
               dirPos.field_72449_c,
               voteDirection.func_82601_c() * rand.nextFloat() * 0.18,
               voteDirection.func_96559_d() * rand.nextFloat() * 0.18,
               voteDirection.func_82599_e() * rand.nextFloat() * 0.18
            );
            p.field_187149_H = 4.0E-4F;
            p.func_187146_c(this.getDirectionColor(voteDirection));
         }
      }
   }

   private int getDirectionColor(Direction direction) {
      switch (direction) {
         case NORTH:
            return 13375004;
         case SOUTH:
            return 1636588;
         case WEST:
            return 14985778;
         case EAST:
            return 2805028;
         default:
            return 16777215;
      }
   }

   public void setActive() {
      this.active = true;
      this.timeout = 20;
      this.markForUpdate();
   }

   public void setHighlightDirections(Collection<Direction> directions) {
      this.highlightDirections.clear();
      this.highlightDirections.addAll(directions);
      this.markForUpdate();
   }

   public boolean isActive() {
      return this.active;
   }

   private void markForUpdate() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
         this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
         this.func_70296_d();
      }
   }

   public void func_230337_a_(BlockState state, CompoundNBT tag) {
      super.func_230337_a_(state, tag);
      this.active = tag.func_74767_n("active");
      this.highlightDirections.clear();
      if (tag.func_150297_b("directions", 9)) {
         this.highlightDirections.addAll(NBTHelper.readList(tag, "directions", IntNBT.class, nbt -> Direction.values()[nbt.func_150287_d()]));
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT tag) {
      tag.func_74757_a("active", this.active);
      NBTHelper.writeList(tag, "directions", this.highlightDirections, IntNBT.class, dir -> IntNBT.func_229692_a_(dir.ordinal()));
      return super.func_189515_b(tag);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      this.func_189515_b(nbt);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
      this.func_230337_a_(state, nbt);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return RENDER_BOX.func_186670_a(this.func_174877_v());
   }
}
