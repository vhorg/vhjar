package iskallia.vault.block.entity;

import com.google.common.collect.Iterables;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.EternalsData;
import javax.annotation.Nonnull;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AncientCryoChamberTileEntity extends CryoChamberTileEntity {
   public AncientCryoChamberTileEntity() {
      super(ModBlocks.ANCIENT_CRYO_CHAMBER_TILE_ENTITY);
      this.setMaxCores(1);
   }

   public void setEternalName(String coreName) {
      this.coreNames.clear();
      this.coreNames.add(coreName);
      this.func_70296_d();
   }

   @Nonnull
   public String getEternalName() {
      return (String)Iterables.getFirst(this.coreNames, "Unknown");
   }

   @Override
   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K && this.getOwner() != null) {
         if (this.getEternalId() != null && this.field_145850_b.func_82737_E() % 40L == 0L) {
            this.field_145850_b
               .func_184148_a(
                  null,
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  SoundEvents.field_206934_aN,
                  SoundCategory.PLAYERS,
                  0.25F,
                  1.0F
               );
         }

         if (this.getEternalId() == null && !this.coreNames.isEmpty()) {
            this.createAncient();
            this.sendUpdates();
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
      this.eternalId = EternalsData.get((ServerWorld)this.func_145831_w()).add(this.getOwner(), name, true);
   }
}
