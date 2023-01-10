package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;

public class XpAltarTileEntity extends FillableAltarTileEntity {
   public XpAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.XP_ALTAR_TILE_ENTITY, pos, state);
   }

   @Override
   public Component getRequirementName() {
      return new TextComponent("EXP Levels");
   }

   @Override
   public VaultGod getVaultGod() {
      return VaultGod.TENOS;
   }

   @Override
   public Component getRequirementUnit() {
      return new TextComponent("levels");
   }

   @Override
   public Color getFillColor() {
      return new Color(-13842220);
   }
}
