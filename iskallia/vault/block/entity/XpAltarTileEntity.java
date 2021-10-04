package iskallia.vault.block.entity;

import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class XpAltarTileEntity extends FillableAltarTileEntity {
   public XpAltarTileEntity() {
      super(ModBlocks.XP_ALTAR_TILE_ENTITY);
   }

   @Override
   public ITextComponent getRequirementName() {
      return new StringTextComponent("EXP Levels");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.OMNISCIENT;
   }

   @Override
   public ITextComponent getRequirementUnit() {
      return new StringTextComponent("levels");
   }

   @Override
   public Color getFillColor() {
      return new Color(-13842220);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(this::getMaxProgressMultiplier).orElse(1.0F);
         float progress = Math.max(10.0F, vaultLevel.intValue() * 2.0F);
         return Math.round(progress * multiplier);
      });
   }
}
