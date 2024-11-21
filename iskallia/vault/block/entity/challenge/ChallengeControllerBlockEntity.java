package iskallia.vault.block.entity.challenge;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import iskallia.vault.block.ChallengeControllerBlock;
import iskallia.vault.client.particles.ColoredParticleOptions;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.init.ModParticles;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.world.data.ChallengeData;
import java.awt.Color;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class ChallengeControllerBlockEntity<T extends ChallengeManager> extends BlockEntity {
   protected UUID uuid;
   private ChallengeControllerBlockEntity.Renderer renderer = new ChallengeControllerBlockEntity.Renderer(16777215, 16777215, 16777215);
   private ChallengeControllerBlockEntity.State state = ChallengeControllerBlockEntity.State.IDLE;
   private int ticker = 0;
   public int animationTick;

   public ChallengeControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public ChallengeControllerBlockEntity.Renderer getRenderer() {
      return this.renderer;
   }

   public void setRenderer(int coreColor, int glyphColor, int particleColor) {
      this.setRenderer(new ChallengeControllerBlockEntity.Renderer(coreColor, glyphColor, particleColor));
   }

   public void setRenderer(ChallengeControllerBlockEntity.Renderer renderer) {
      this.renderer = renderer;
      this.sendUpdates();
   }

   public ChallengeControllerBlockEntity.State getState() {
      return this.state;
   }

   public void setState(ChallengeControllerBlockEntity.State state) {
      this.state = state;
      this.ticker = 0;
      this.sendUpdates();
   }

   public int getTicker() {
      return this.ticker;
   }

   public void setTicker(int ticker) {
      this.ticker = ticker;
      this.sendUpdates();
   }

   public abstract T createManager();

   public void onTick(Level world, BlockPos pos, BlockState state) {
      if (world.isClientSide()) {
         if (this.state != ChallengeControllerBlockEntity.State.IDLE) {
            this.animationTick++;
         }
      } else if (world instanceof ServerLevel serverWorld) {
         ChallengeData data = ChallengeData.get(serverWorld.getServer());
         if (this.state != ChallengeControllerBlockEntity.State.DESTROYED && !data.contains(this.uuid)) {
            data.add(serverWorld, this.createManager());
         }

         Color color = new Color(this.getRenderer().getParticleColor());
         if ((this.getState() == ChallengeControllerBlockEntity.State.GENERATING || this.getState() == ChallengeControllerBlockEntity.State.ACTIVE)
            && serverWorld.getGameTime() % 3L == 0L) {
            serverWorld.sendParticles(
               new ColoredParticleOptions(
                  (ParticleType<ColoredParticleOptions>)ModParticles.TOTEM_FOUNTAIN.get(), new Vector3f(color.getColorComponents(new float[3]))
               ),
               pos.getX() + 0.5,
               pos.getY() + 1.5,
               pos.getZ() + 0.5,
               5,
               0.075,
               0.0,
               0.075,
               0.0
            );
         }

         if (this.getState() == ChallengeControllerBlockEntity.State.GENERATING && this.getTicker() >= 50) {
            this.setState(ChallengeControllerBlockEntity.State.ACTIVE);
         }

         if (this.getState() == ChallengeControllerBlockEntity.State.ACTIVE && this.getTicker() % 160 == 0) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.5F);
         }

         this.setTicker(this.getTicker() + 1);
      }
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.getBlockPos()).inflate(2.0);
   }

   public static void tick(Level world, BlockPos pos, BlockState state, ChallengeControllerBlockEntity<?> entity) {
      entity.onTick(world, pos, state);
   }

   protected Rotation getRotation() {
      return switch ((Direction)this.getBlockState().getValue(ChallengeControllerBlock.FACING)) {
         case NORTH -> Rotation.COUNTERCLOCKWISE_90;
         case SOUTH -> Rotation.CLOCKWISE_90;
         case WEST -> Rotation.CLOCKWISE_180;
         case EAST -> Rotation.NONE;
         default -> throw new UnsupportedOperationException();
      };
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
      this.renderer.writeNbt().ifPresent(tag -> nbt.put("renderer", tag));
      Adapters.ofEnum(ChallengeControllerBlockEntity.State.class, EnumAdapter.Mode.NAME).writeNbt(this.state).ifPresent(tag -> nbt.put("state", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.ticker)).ifPresent(tag -> nbt.put("ticker", tag));
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseGet(Mth::createInsecureUUID);
      this.renderer.readNbt(nbt.getCompound("renderer"));
      this.state = Adapters.ofEnum(ChallengeControllerBlockEntity.State.class, EnumAdapter.Mode.NAME)
         .readNbt(nbt.get("state"))
         .orElse(ChallengeControllerBlockEntity.State.IDLE);
      this.ticker = Adapters.INT.readNbt(nbt.get("ticker")).orElse(0);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

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

   public static class Renderer implements ISerializable<CompoundTag, JsonObject> {
      private int coreColor;
      private int glyphColor;
      private int particleColor;

      public Renderer(int coreColor, int glyphColor, int particleColor) {
         this.coreColor = coreColor;
         this.glyphColor = glyphColor;
         this.particleColor = particleColor;
      }

      public int getCoreColor() {
         return this.coreColor;
      }

      public int getGlyphColor() {
         return this.glyphColor;
      }

      public int getParticleColor() {
         return this.particleColor;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.INT.writeNbt(Integer.valueOf(this.coreColor)).ifPresent(tag -> nbt.put("coreColor", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.glyphColor)).ifPresent(tag -> nbt.put("glyphColor", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.particleColor)).ifPresent(tag -> nbt.put("particleColor", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.coreColor = Adapters.INT.readNbt(nbt.get("coreColor")).orElse(16777215);
         this.glyphColor = Adapters.INT.readNbt(nbt.get("glyphColor")).orElse(16777215);
         this.particleColor = Adapters.INT.readNbt(nbt.get("particleColor")).orElse(16777215);
      }
   }

   public static enum State {
      IDLE,
      GENERATING,
      ACTIVE,
      DESTROYED;
   }
}
