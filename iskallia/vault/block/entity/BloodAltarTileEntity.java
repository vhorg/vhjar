package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;

public class BloodAltarTileEntity extends FillableAltarTileEntity {
   public BloodAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.BLOOD_ALTAR_TILE_ENTITY, pos, state);
   }

   @Override
   public Component getRequirementName() {
      return new TextComponent("Health Points");
   }

   @Override
   public VaultGod getVaultGod() {
      return VaultGod.VELARA;
   }

   @Override
   public Component getRequirementUnit() {
      return new TextComponent("hearts");
   }

   @Override
   public Color getFillColor() {
      return new Color(-5570816);
   }
}
