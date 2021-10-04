package iskallia.vault.block.base;

import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

public abstract class FillableAltarTileEntity extends TileEntity implements ITickableTileEntity {
   protected static final Random rand = new Random();
   private int currentProgress = 0;
   private int maxProgress = 0;

   public FillableAltarTileEntity(TileEntityType<?> tileEntityTypeIn) {
      super(tileEntityTypeIn);
   }

   public boolean initialized() {
      return this.getMaxProgress() > 0;
   }

   public int getCurrentProgress() {
      return this.currentProgress;
   }

   public int getMaxProgress() {
      return this.maxProgress;
   }

   public boolean isMaxedOut() {
      return this.currentProgress >= this.getMaxProgress();
   }

   public float progressPercentage() {
      return Math.min((float)this.getCurrentProgress() / this.getMaxProgress(), 1.0F);
   }

   public void makeProgress(ServerPlayerEntity sPlayer, int deltaProgress, Consumer<ServerPlayerEntity> onComplete) {
      if (this.initialized()) {
         this.currentProgress += deltaProgress;
         this.sendUpdates();
         if (this.isMaxedOut()) {
            onComplete.accept(sPlayer);
         }
      }
   }

   public void func_73660_a() {
      if (!this.initialized()) {
         if (this.func_145831_w() instanceof ServerWorld) {
            this.getCurrentVault().flatMap(this::calcMaxProgress).ifPresent(maxProgress -> {
               this.maxProgress = maxProgress;
               this.sendUpdates();
            });
         }
      }
   }

   private Optional<VaultRaid> getCurrentVault() {
      ServerWorld sWorld = (ServerWorld)this.func_145831_w();
      return Optional.ofNullable(VaultRaidData.get(sWorld).getAt(sWorld, this.func_174877_v()));
   }

   protected float getMaxProgressMultiplier(UUID playerUUID) {
      if (this.func_145831_w() instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)this.func_145831_w();
         int favour = PlayerFavourData.get(sWorld).getFavour(playerUUID, this.getAssociatedVaultGod());
         return favour < 0 ? 1.0F + 0.2F * (Math.abs(favour) / 6.0F) : 1.0F - 0.75F * (Math.min((float)favour, 8.0F) / 8.0F);
      } else {
         return 1.0F;
      }
   }

   public abstract ITextComponent getRequirementName();

   public abstract PlayerFavourData.VaultGodType getAssociatedVaultGod();

   public abstract ITextComponent getRequirementUnit();

   public abstract Color getFillColor();

   protected abstract Optional<Integer> calcMaxProgress(VaultRaid var1);

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      nbt.func_74768_a("CurrentProgress", this.currentProgress);
      nbt.func_74768_a("CalculatedMaxProgress", this.maxProgress);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      this.currentProgress = nbt.func_74762_e("CurrentProgress");
      this.maxProgress = nbt.func_74764_b("CalculatedMaxProgress") ? nbt.func_74762_e("CalculatedMaxProgress") : -1;
      super.func_230337_a_(state, nbt);
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

   public void sendUpdates() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
         this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
         this.func_70296_d();
      }
   }
}
