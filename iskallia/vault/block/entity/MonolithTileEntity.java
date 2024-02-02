package iskallia.vault.block.entity;

import iskallia.vault.block.MonolithBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class MonolithTileEntity extends BlockEntity {
   private static final Random rand = new Random();
   private boolean generated;
   private boolean overStacking;
   private Map<ResourceLocation, Integer> modifiers = new HashMap<>();

   public MonolithTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.MONOLITH_TILE_ENTITY, pos, state);
   }

   public boolean isGenerated() {
      return this.generated;
   }

   public boolean isOverStacking() {
      return this.overStacking;
   }

   public Map<ResourceLocation, Integer> getModifiers() {
      return this.modifiers;
   }

   public void setGenerated(boolean generated) {
      this.generated = generated;
      this.sendUpdates();
   }

   public void setOverStacking(boolean overStacking) {
      this.overStacking = overStacking;
      this.sendUpdates();
   }

   public void addModifier(VaultModifier<?> modifier) {
      this.modifiers.put(modifier.getId(), this.modifiers.getOrDefault(modifier.getId(), 0) + 1);
      this.sendUpdates();
   }

   public void removeModifiers() {
      this.modifiers.clear();
      this.sendUpdates();
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.generated = nbt.getBoolean("Generated");
      this.overStacking = nbt.getBoolean("OverStacking");
      this.modifiers = new HashMap<>();
      CompoundTag modifiersNBT = nbt.getCompound("Modifiers");
      modifiersNBT.getAllKeys().forEach(key -> {
         ResourceLocation id = new ResourceLocation(key);
         int count = modifiersNBT.getInt(key);
         this.modifiers.put(id, count);
      });
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putBoolean("Generated", this.generated);
      nbt.putBoolean("OverStacking", this.overStacking);
      CompoundTag modifiersNBT = new CompoundTag();
      this.modifiers.forEach((id, count) -> modifiersNBT.putInt(id.toString(), count));
      nbt.put("Modifiers", modifiersNBT);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, MonolithTileEntity tile) {
      CommonEvents.MONOLITH_UPDATE.invoke(level, state, pos, tile);
      if (level.isClientSide()) {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (this.getLevel() != null) {
         BlockPos pos = this.getBlockPos();
         BlockState state = this.getBlockState();
         if (this.getLevel().getGameTime() % 1L == 0L) {
            ParticleEngine mgr = Minecraft.getInstance().particleEngine;
            if (state.getValue(MonolithBlock.STATE) == MonolithBlock.State.LIT) {
               Random random = this.getLevel().getRandom();
               if (random.nextInt(5) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + random.nextDouble() * 0.15F + 0.55F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 120.0,
                        random.nextDouble() * -0.005 + 0.075,
                        offset.z / 120.0
                     );
               }

               if (random.nextInt(2) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 9.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 9.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + 1.55F + random.nextDouble() * 0.15F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 120.0,
                        random.nextDouble() * -0.005 + 0.075,
                        offset.z / 120.0
                     );
               }

               if (random.nextInt(15) == 0) {
                  Vec3 offset = new Vec3(
                     random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
                  );
                  this.getLevel()
                     .addParticle(
                        ParticleTypes.LAVA,
                        true,
                        pos.getX() + 0.5 + offset.x,
                        pos.getY() + random.nextDouble() * 0.15F + 0.55F,
                        pos.getZ() + 0.5 + offset.z,
                        offset.x / 12.0,
                        random.nextDouble() * 0.1 + 0.1,
                        offset.z / 12.0
                     );
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnIgniteParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 50; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LAVA,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.45F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.LARGE_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.1,
               offset.z / 20.0
            );
         }
      }
   }
}
