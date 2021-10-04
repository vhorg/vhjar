package iskallia.vault.block.render;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;

public class VaultChestRenderer<T extends VaultChestTileEntity> extends ChestTileEntityRenderer<T> {
   public static final RenderMaterial NORMAL = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/vault_chest"));
   public static final RenderMaterial TREASURE = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/vault_treasure_chest"));
   public static final RenderMaterial ALTAR = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/vault_altar_chest"));
   public static final RenderMaterial COOP = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/vault_coop_chest"));
   public static final RenderMaterial BONUS = new RenderMaterial(Atlases.field_228747_f_, Vault.id("entity/chest/vault_bonus_chest"));

   public VaultChestRenderer(TileEntityRendererDispatcher dispatcher) {
      super(dispatcher);
   }

   protected RenderMaterial getMaterial(T tileEntity, ChestType chestType) {
      BlockState state = tileEntity.func_195044_w();
      if (state.func_177230_c() == ModBlocks.VAULT_CHEST) {
         return NORMAL;
      } else if (state.func_177230_c() == ModBlocks.VAULT_TREASURE_CHEST) {
         return TREASURE;
      } else if (state.func_177230_c() == ModBlocks.VAULT_ALTAR_CHEST) {
         return ALTAR;
      } else if (state.func_177230_c() == ModBlocks.VAULT_COOP_CHEST) {
         return COOP;
      } else {
         return state.func_177230_c() == ModBlocks.VAULT_BONUS_CHEST ? BONUS : null;
      }
   }
}
