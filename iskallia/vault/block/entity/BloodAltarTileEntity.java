package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BloodAltarTileEntity extends FillableAltarTileEntity {
   public BloodAltarTileEntity() {
      super(ModBlocks.BLOOD_ALTAR_TILE_ENTITY);
   }

   @Override
   public ITextComponent getRequirementName() {
      return new StringTextComponent("Health Points");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.BENEVOLENT;
   }

   @Override
   public ITextComponent getRequirementUnit() {
      return new StringTextComponent("hearts");
   }

   @Override
   public Color getFillColor() {
      return new Color(-5570816);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(this::getMaxProgressMultiplier).orElse(1.0F);
         int progress = 3 + vaultLevel / 5;
         return Math.round(progress * multiplier);
      });
   }
}
