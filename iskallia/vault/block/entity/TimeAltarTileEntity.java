package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TimeAltarTileEntity extends FillableAltarTileEntity {
   public TimeAltarTileEntity() {
      super(ModBlocks.TIME_ALTAR_TILE_ENTITY);
   }

   @Override
   public ITextComponent getRequirementName() {
      return new StringTextComponent("Vault Time");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.TIMEKEEPER;
   }

   @Override
   public ITextComponent getRequirementUnit() {
      return new StringTextComponent("minutes");
   }

   @Override
   public Color getFillColor() {
      return new Color(-14590);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(this::getMaxProgressMultiplier).orElse(1.0F);
         int progress = Math.min(1 + vaultLevel / 20, 3);
         return Math.round(progress * multiplier);
      });
   }
}
