package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
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

   @Nullable
   public static String getEventKey(ItemStack stack) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof ItemVaultCrystalSeal) {
         CompoundNBT tag = stack.func_196082_o();
         return !tag.func_150297_b("eventKey", 8) ? null : tag.func_74779_i("eventKey");
      } else {
         return null;
      }
   }

   public static void setEventKey(ItemStack stack, String eventKey) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof ItemVaultCrystalSeal) {
         stack.func_196082_o().func_74778_a("eventKey", eventKey);
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.func_201670_d()) {
         String eventKey = getEventKey(stack);
         if (eventKey != null) {
            boolean hasEvent = ModConfigs.ARCHITECT_EVENT.isEnabled() || ModConfigs.RAID_EVENT_CONFIG.isEnabled();
            if (!hasEvent) {
               stack.func_190920_e(0);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      VaultObjective objective = VaultObjective.getObjective(this.objectiveId);
      if (objective != null) {
         tooltip.add(new StringTextComponent("Sets a vault crystal's objective").func_240699_a_(TextFormatting.GRAY));
         tooltip.add(new StringTextComponent("to: ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(objective.getObjectiveDisplayName()));
      }

      String eventKey = getEventKey(stack);
      if (eventKey != null) {
         if (objective != null) {
            tooltip.add(StringTextComponent.field_240750_d_);
         }

         tooltip.add(new StringTextComponent("Event Item").func_240699_a_(TextFormatting.AQUA));
         tooltip.add(new StringTextComponent("Expires after the event finishes.").func_240699_a_(TextFormatting.GRAY));
      }
   }
}
