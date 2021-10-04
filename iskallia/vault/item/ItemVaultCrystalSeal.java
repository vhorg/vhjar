package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemVaultCrystalSeal extends Item {
   private final ResourceLocation objectiveId;

   public ItemVaultCrystalSeal(ResourceLocation id, ResourceLocation objectiveId) {
      super(new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.setRegistryName(id);
      this.objectiveId = objectiveId;
   }

   public ResourceLocation getObjectiveId() {
      return this.objectiveId;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      VaultObjective objective = VaultObjective.getObjective(this.objectiveId);
      if (objective != null) {
         tooltip.add(new StringTextComponent("Sets a vault crystal's objective").func_240699_a_(TextFormatting.GRAY));
         tooltip.add(new StringTextComponent("to: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(objective.getObjectiveDisplayName()));
      }
   }
}
