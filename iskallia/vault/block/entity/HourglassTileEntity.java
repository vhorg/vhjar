package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class HourglassTileEntity extends TileEntity {
   private UUID ownerUUID;
   private String ownerPlayerName = "Unknown";
   private int currentSand = 0;
   private int totalSand = -1;

   public HourglassTileEntity() {
      super(ModBlocks.HOURGLASS_TILE_ENTITY);
   }

   public void setOwner(@Nonnull UUID ownerUUID, @Nonnull String playerName) {
      this.ownerUUID = ownerUUID;
      this.ownerPlayerName = playerName;
   }

   @Nonnull
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   @Nonnull
   public String getOwnerPlayerName() {
      return this.ownerPlayerName;
   }

   public void setTotalSand(int totalSand) {
      if (this.totalSand != (this.totalSand = totalSand)) {
         this.markForUpdate();
      }
   }

   public boolean addSand(PlayerEntity player, int amount) {
      int total = this.totalSand <= 0 ? ModConfigs.SAND_EVENT.getTotalSandRequired(player) : this.totalSand;
      if (this.currentSand >= total) {
         return false;
      } else if (this.ownerUUID != null && !player.func_110124_au().equals(this.getOwnerUUID())) {
         return false;
      } else {
         this.currentSand += amount;
         VaultRaid vault = VaultRaidData.get((ServerWorld)player.field_70170_p).getActiveFor(player.func_110124_au());
         if (vault != null) {
            vault.getActiveObjective(TreasureHuntObjective.class)
               .ifPresent(treasureHunt -> treasureHunt.depositSand(vault, (ServerPlayerEntity)player, amount));
         }

         this.markForUpdate();
         return true;
      }
   }

   public float getFilledPercentage() {
      return MathHelper.func_76131_a((float)this.currentSand / this.totalSand, 0.0F, 1.0F);
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      super.func_189515_b(nbt);
      if (this.ownerUUID != null) {
         nbt.func_186854_a("ownerUUID", this.ownerUUID);
      }

      nbt.func_74778_a("ownerPlayerName", this.ownerPlayerName);
      nbt.func_74768_a("currentSand", this.currentSand);
      nbt.func_74768_a("totalSand", this.totalSand);
      return nbt;
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      this.ownerUUID = nbt.func_150297_b("ownerUUID", 11) ? nbt.func_186857_a("ownerUUID") : null;
      this.ownerPlayerName = nbt.func_74779_i("ownerPlayerName");
      this.currentSand = nbt.func_74762_e("currentSand");
      this.totalSand = nbt.func_74762_e("totalSand");
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      this.func_189515_b(nbt);
      return nbt;
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }

   public void markForUpdate() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
         this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
         this.func_70296_d();
      }
   }
}
