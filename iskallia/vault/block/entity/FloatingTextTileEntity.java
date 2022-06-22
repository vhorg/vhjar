package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class FloatingTextTileEntity extends TileEntity {
   @Nonnull
   protected List<String> lines = new LinkedList<>();

   public FloatingTextTileEntity() {
      super(ModBlocks.FLOATING_TEXT_TILE_ENTITY);
      this.lines
         .add(
            "[\"\",{\"text\":\"A sample \",\"bold\":true},{\"text\":\"floating\",\"bold\":true,\"color\":\"light_purple\"},{\"text\":\" text\",\"bold\":true}]"
         );
      this.lines.add("");
      this.lines.add("{\"text\":\"Edit the content by using\",\"bold\":true}");
      this.lines.add("{\"text\":\"/data modify block <x> <y> <z> Lines append value 'JSON Here'\",\"bold\":true,\"color\":\"aqua\"}");
   }

   @Nonnull
   public List<String> getLines() {
      return this.lines;
   }

   public void loadFromNBT(CompoundNBT nbt) {
      this.lines = NBTHelper.readList(nbt, "Lines", StringNBT.class, StringNBT::func_150285_a_);
   }

   public void writeToEntityTag(CompoundNBT nbt) {
      NBTHelper.writeList(nbt, "Lines", this.lines, StringNBT.class, StringNBT::func_229705_a_);
   }

   @Nonnull
   public CompoundNBT func_189515_b(@Nonnull CompoundNBT nbt) {
      this.writeToEntityTag(nbt);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
      this.loadFromNBT(nbt);
      super.func_230337_a_(state, nbt);
   }

   @Nonnull
   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      this.writeToEntityTag(nbt);
      return nbt;
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }
}
