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

public class XpAltarTileEntity extends FillableAltarTileEntity {
   public XpAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.XP_ALTAR_TILE_ENTITY, pos, state);
   }

   @Override
   public Component getRequirementName() {
      return new TextComponent("EXP Levels");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.OMNISCIENT;
   }

   @Override
   public Component getRequirementUnit() {
      return new TextComponent("levels");
   }

   @Override
   public Color getFillColor() {
      return new Color(-13842220);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(x$0 -> this.getMaxProgressMultiplier(x$0)).orElse(1.0F);
         float progress = Math.max(10.0F, vaultLevel.intValue() * 2.0F);
         return Math.round(progress * multiplier);
      });
   }
}
