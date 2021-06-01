package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.SkinProfile;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class PlayerStatueTileEntity extends TileEntity {
   protected SkinProfile skin = new SkinProfile();
   protected boolean hasCrown;

   public PlayerStatueTileEntity() {
      super(ModBlocks.PLAYER_STATUE_TILE_ENTITY);
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   public boolean hasCrown() {
      return this.hasCrown;
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      String nickname = this.skin.getLatestNickname();
      nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
      nbt.func_74757_a("HasCrown", this.hasCrown);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      String nickname = nbt.func_74779_i("PlayerNickname");
      this.skin.updateSkin(nickname);
      this.hasCrown = nbt.func_74767_n("HasCrown");
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      String nickname = this.skin.getLatestNickname();
      nbt.func_74778_a("PlayerNickname", nickname == null ? "" : nickname);
      nbt.func_74757_a("HasCrown", this.hasCrown);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
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
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }
}
