package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.EternalsData;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.world.server.ServerWorld;

public class GlobalTraderTileEntity extends SkinnableTileEntity implements ITickableTileEntity {
   private String lastName = "Player1";

   public GlobalTraderTileEntity() {
      super(ModBlocks.GLOBAL_TRADER_TILE_ENTITY);
      this.updateLastName();
      this.updateSkin();
   }

   @Override
   protected void updateSkin() {
      if (this.func_145831_w() != null) {
         if (this.func_145831_w().field_72995_K) {
            this.skin.updateSkin(this.lastName);
         }
      }
   }

   private void updateLastName() {
      if (this.func_145831_w() != null && !this.func_145831_w().field_72995_K) {
         List<String> names = EternalsData.get((ServerWorld)this.func_145831_w()).getAllEternalNamesExcept(this.lastName);
         if (names.isEmpty()) {
            this.lastName = "Player1";
         }

         Collections.shuffle(names);
         this.lastName = names.stream().findFirst().orElse("Player1");
         this.sendUpdates();
      }
   }

   public void func_73660_a() {
      if (this.func_145831_w() != null) {
         if (this.func_145831_w().field_72995_K) {
            if (this.skin.getLatestNickname() == null) {
               this.skin.updateSkin("Player1");
            }

            if (this.skin.getLatestNickname().equalsIgnoreCase(this.lastName)) {
               return;
            }

            this.updateSkin();
         } else {
            if (this.func_145831_w().func_82737_E() % 20L != 0L) {
               return;
            }

            long time = Instant.now().getEpochSecond();
            if (time % ModConfigs.GLOBAL_TRADER.SKIN_UPDATE_RATE_SECONDS == 0L) {
               this.updateLastName();
            }
         }
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      nbt.func_74778_a("Name", this.lastName);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      this.lastName = nbt.func_74779_i("Name");
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      nbt.func_74778_a("Name", this.lastName);
      return nbt;
   }
}
