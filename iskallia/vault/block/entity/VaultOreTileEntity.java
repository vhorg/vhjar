package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.TemplateTagContainer;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultOreTileEntity extends BlockEntity implements TemplateTagContainer {
   private final List<String> templateTags = new ArrayList<>();

   public VaultOreTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_ORE_TILE_ENTITY, pos, state);
   }

   @Override
   public List<String> getTemplateTags() {
      return Collections.unmodifiableList(this.templateTags);
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.templateTags.addAll(this.loadTemplateTags(nbt));
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      this.saveTemplateTags(nbt);
   }
}
