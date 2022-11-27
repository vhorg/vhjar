package iskallia.vault.block.entity;

import com.google.common.collect.Iterables;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.EternalsData;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AncientCryoChamberTileEntity extends CryoChamberTileEntity {
   public AncientCryoChamberTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ANCIENT_CRYO_CHAMBER_TILE_ENTITY, pos, state);
      this.setMaxCores(1);
   }

   public void setEternalName(String coreName) {
      this.coreNames.clear();
      this.coreNames.add(coreName);
      this.setChanged();
   }

   @Nonnull
   public String getEternalName() {
      return (String)Iterables.getFirst(this.coreNames, "Unknown");
   }

   public static void tick(Level level, BlockPos pos, BlockState state, AncientCryoChamberTileEntity tile) {
      if (level != null && !level.isClientSide && tile.getOwner() != null) {
         if (tile.getEternalId() != null && level.getGameTime() % 40L == 0L) {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CONDUIT_AMBIENT, SoundSource.PLAYERS, 0.25F, 1.0F);
         }

         if (tile.getEternalId() == null && !tile.coreNames.isEmpty()) {
            tile.createAncient();
            tile.sendUpdates();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void updateSkin() {
      if (this.eternalId == null && !this.coreNames.isEmpty()) {
         this.skin.updateSkin(this.getEternalName());
      } else {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(this.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            this.skin.updateSkin(snapshot.getName());
         }
      }
   }

   private void createAncient() {
      String name = (String)Iterables.getFirst(this.coreNames, "Unknown");
      EternalsData.EternalVariant variant = EternalsData.EternalVariant.byId(new Random().nextInt(EternalsData.EternalVariant.values().length));
      this.variant = variant;
      this.eternalId = EternalsData.get((ServerLevel)this.getLevel()).add(this.getOwner(), name, true, variant, false);
   }
}
