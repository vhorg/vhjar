package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemVaultGem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.OreBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.MathHelper;

public class VaultOreBlock extends OreBlock {
   public static final List<VaultOreBlock> ORES = new ArrayList<>();
   public ItemVaultGem associatedGem;

   public VaultOreBlock(@Nonnull ItemVaultGem associatedGem) {
      super(
         Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G)
            .func_235861_h_()
            .func_235838_a_(state -> 9)
            .func_200948_a(3.0F, 3.0F)
            .func_200947_a(ModSounds.VAULT_GEM)
      );
      this.associatedGem = associatedGem;
      ORES.add(this);
   }

   @Nonnull
   public ItemVaultGem getAssociatedGem() {
      return this.associatedGem;
   }

   protected int func_220281_a(@Nonnull Random random) {
      return MathHelper.func_76136_a(random, 3, 7);
   }
}
