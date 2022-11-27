package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
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
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.BENEVOLENT;
   }

   @Override
   public Component getRequirementUnit() {
      return new TextComponent("hearts");
   }

   @Override
   public Color getFillColor() {
      return new Color(-5570816);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(x$0 -> this.getMaxProgressMultiplier(x$0)).orElse(1.0F);
         int progress = 3 + vaultLevel / 5;
         return Math.round(progress * multiplier);
      });
   }
}
