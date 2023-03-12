package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifierDiscoveryTileEntity extends BlockEntity {
   private Set<UUID> usedPlayers = new HashSet<>();
   private static final Random bookRand = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float rot;
   public float oRot;
   public float tRot;

   public ModifierDiscoveryTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.MODIFIER_DISCOVERY_ENTITY, pos, state);
   }

   public Set<UUID> getUsedPlayers() {
      return this.usedPlayers;
   }

   public boolean setUsedByPlayer(Player player) {
      if (this.usedPlayers.add(player.getUUID())) {
         this.setChanged();
         return true;
      } else {
         return false;
      }
   }

   public boolean canBeUsed(Player player) {
      return !this.getUsedPlayers().contains(player.getUUID());
   }

   @OnlyIn(Dist.CLIENT)
   public static void clientBookTick(Level level, BlockPos pos, BlockState state, ModifierDiscoveryTileEntity tile) {
      tile.oRot = tile.rot;
      Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0, false);
      if (player != null) {
         double d0 = player.getX() - (pos.getX() + 0.5);
         double d1 = player.getZ() - (pos.getZ() + 0.5);
         tile.tRot = (float)Mth.atan2(d1, d0);
         if (bookRand.nextInt(40) == 0) {
            float f1 = tile.flipT;

            do {
               tile.flipT = tile.flipT + (bookRand.nextInt(4) - bookRand.nextInt(4));
            } while (f1 == tile.flipT);
         }
      } else {
         tile.tRot += 0.02F;
      }

      while (tile.rot >= Math.PI) {
         tile.rot = (float)(tile.rot - (Math.PI * 2));
      }

      while (tile.rot < -Math.PI) {
         tile.rot = (float)(tile.rot + (Math.PI * 2));
      }

      while (tile.tRot >= Math.PI) {
         tile.tRot = (float)(tile.tRot - (Math.PI * 2));
      }

      while (tile.tRot < -Math.PI) {
         tile.tRot = (float)(tile.tRot + (Math.PI * 2));
      }

      float f2 = tile.tRot - tile.rot;

      while (f2 >= Math.PI) {
         f2 = (float)(f2 - (Math.PI * 2));
      }

      while (f2 < -Math.PI) {
         f2 = (float)(f2 + (Math.PI * 2));
      }

      tile.rot += f2 * 0.4F;
      tile.time++;
      tile.oFlip = tile.flip;
      float f = (tile.flipT - tile.flip) * 0.4F;
      f = Mth.clamp(f, -0.2F, 0.2F);
      tile.flipA = tile.flipA + (f - tile.flipA) * 0.9F;
      tile.flip = tile.flip + tile.flipA;
      if (bookRand.nextInt(5) == 0) {
         level.addParticle(
            ParticleTypes.ENCHANT,
            pos.getX() + 0.5,
            pos.getY() + 2,
            pos.getZ() + 0.5,
            -1.0F + bookRand.nextFloat() + 0.5,
            -1.0,
            -1.0F + bookRand.nextFloat() + 0.5
         );
      }
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.usedPlayers = NBTHelper.readSet(tag, "players", StringTag.class, strTag -> UUID.fromString(strTag.getAsString()));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      NBTHelper.writeCollection(tag, "players", this.usedPlayers, StringTag.class, uuid -> StringTag.valueOf(uuid.toString()));
   }
}
