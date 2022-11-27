package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FloatingTextTileEntity extends BlockEntity {
   @Nonnull
   protected List<String> lines = new LinkedList<>();

   public FloatingTextTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.FLOATING_TEXT_TILE_ENTITY, pos, state);
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

   public void loadFromNBT(CompoundTag nbt) {
      this.lines = NBTHelper.readList(nbt, "Lines", StringTag.class, StringTag::getAsString);
   }

   public void writeToEntityTag(CompoundTag nbt) {
      NBTHelper.writeCollection(nbt, "Lines", this.lines, StringTag.class, StringTag::valueOf);
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      this.writeToEntityTag(pTag);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.loadFromNBT(pTag);
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
