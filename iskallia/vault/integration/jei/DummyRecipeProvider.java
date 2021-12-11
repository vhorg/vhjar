package iskallia.vault.integration.jei;

import com.google.common.collect.Lists;
import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

public class DummyRecipeProvider {
   public static List<Object> getAnvilRecipes(IVanillaRecipeFactory factory) {
      return Collections.singletonList(
         factory.createAnvilRecipe(
            new ItemStack(ModItems.PERFECT_ECHO_GEM),
            Collections.singletonList(new ItemStack(ModItems.VAULT_CATALYST)),
            Collections.singletonList(new ItemStack(ModItems.VAULT_INHIBITOR))
         )
      );
   }

   public static List<Object> getCustomCraftingRecipes(IVanillaRecipeFactory factory) {
      Ingredient spawnEggs = Ingredient.func_199804_a(
         new IItemProvider[]{
            Items.field_196157_cs,
            Items.field_226634_mw_,
            Items.field_196159_ct,
            Items.field_222090_mx,
            Items.field_196161_cu,
            Items.field_196163_cv,
            Items.field_203798_cB,
            Items.field_196165_cw,
            Items.field_196167_cx,
            Items.field_205156_cF,
            Items.field_196169_cy,
            Items.field_222077_mF,
            Items.field_196171_cz,
            Items.field_196101_cA,
            Items.field_196103_cB,
            Items.field_196105_cC,
            Items.field_222080_mK,
            Items.field_196107_cD,
            Items.field_196109_cE,
            Items.field_234769_nY_,
            Items.field_196111_cF,
            Items.field_196113_cG,
            Items.field_196115_cH,
            Items.field_196117_cI,
            Items.field_196119_cJ,
            Items.field_196121_cK,
            Items.field_196123_cL,
            Items.field_222082_mU,
            Items.field_196125_cM,
            Items.field_203181_cQ,
            Items.field_196127_cN,
            Items.field_234773_ok_,
            Items.field_242399_ol,
            Items.field_222084_mY,
            Items.field_196129_cO,
            Items.field_203799_cW,
            Items.field_196131_cP,
            Items.field_222091_nc,
            Items.field_203800_cY,
            Items.field_196133_cQ,
            Items.field_196135_cR,
            Items.field_196137_cS,
            Items.field_196138_cT,
            Items.field_196139_cU,
            Items.field_196141_cV,
            Items.field_196143_cW,
            Items.field_196145_cX,
            Items.field_196147_cY,
            Items.field_234770_oA_,
            Items.field_222092_nn,
            Items.field_204273_dj,
            Items.field_203182_dc,
            Items.field_196149_cZ,
            Items.field_196172_da,
            Items.field_196173_db,
            Items.field_222093_nt,
            Items.field_196174_dc,
            Items.field_196175_dd,
            Items.field_196176_de,
            Items.field_234771_oL_,
            Items.field_196177_df,
            Items.field_196178_dg,
            Items.field_196181_di,
            Items.field_234772_oO_
         }
      );
      NonNullList<Ingredient> mysteryEggInputs = NonNullList.func_191196_a();
      mysteryEggInputs.add(spawnEggs);
      mysteryEggInputs.add(spawnEggs);
      mysteryEggInputs.add(spawnEggs);
      mysteryEggInputs.add(spawnEggs);
      mysteryEggInputs.add(Ingredient.func_199804_a(new IItemProvider[]{ModItems.ALEXANDRITE_GEM}));
      return Lists.newArrayList(new Object[]{new ShapelessRecipe(Vault.id("mystery_egg_recipe"), "", new ItemStack(ModItems.MYSTERY_EGG, 4), mysteryEggInputs)});
   }
}
