package iskallia.vault.item.paxel;

import com.google.common.collect.Multimap;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.paxel.enhancement.DurabilityEnhancement;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancement;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class VaultPaxelItem extends ToolItem {
   public static final ToolType PAXEL_TOOL_TYPE = ToolType.get("paxel");

   public VaultPaxelItem(ResourceLocation id) {
      super(
         3.0F,
         -3.0F,
         PaxelItemTier.INSTANCE,
         Collections.emptySet(),
         new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).addToolType(PAXEL_TOOL_TYPE, PaxelItemTier.INSTANCE.func_200925_d()).func_200917_a(1)
      );
      this.setRegistryName(id);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
      return super.getAttributeModifiers(slot, stack);
   }

   public int getMaxDamage(ItemStack itemStack) {
      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(itemStack);
      return enhancement instanceof DurabilityEnhancement
         ? super.getMaxDamage(itemStack) + ((DurabilityEnhancement)enhancement).getExtraDurability()
         : super.getMaxDamage(itemStack);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment == Enchantments.field_185296_A
         ? false
         : enchantment.func_92089_a(new ItemStack(Items.field_151048_u))
            || enchantment.func_92089_a(new ItemStack(Items.field_151056_x))
            || enchantment.func_92089_a(new ItemStack(Items.field_151047_v))
            || enchantment.func_92089_a(new ItemStack(Items.field_151012_L));
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      if (EnchantmentHelper.func_82781_a(book).containsKey(Enchantments.field_185296_A)) {
         return false;
      } else {
         for (Enchantment e : EnchantmentHelper.func_82781_a(book).keySet()) {
            if (e.func_92089_a(new ItemStack(Items.field_151048_u))
               || e.func_92089_a(new ItemStack(Items.field_151056_x))
               || e.func_92089_a(new ItemStack(Items.field_151047_v))
               || e.func_92089_a(new ItemStack(Items.field_151012_L))) {
               return true;
            }
         }

         return false;
      }
   }

   public int getHarvestLevel(ItemStack stack, ToolType tool, PlayerEntity player, BlockState blockState) {
      return this.func_200891_e().func_200925_d();
   }

   public boolean func_150897_b(BlockState state) {
      ToolType harvestTool = state.getHarvestTool();
      if ((harvestTool == ToolType.AXE || harvestTool == ToolType.PICKAXE || harvestTool == ToolType.SHOVEL)
         && this.func_200891_e().func_200925_d() >= state.getHarvestLevel()) {
         return true;
      } else if (!state.func_203425_a(Blocks.field_150433_aE) && !state.func_203425_a(Blocks.field_196604_cC)) {
         Material material = state.func_185904_a();
         return material == Material.field_151576_e || material == Material.field_151573_f || material == Material.field_151574_g;
      } else {
         return true;
      }
   }

   public float func_150893_a(@Nonnull ItemStack stack, BlockState state) {
      return this.func_200891_e().func_200928_b();
   }

   public boolean func_82789_a(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   @Nonnull
   public ActionResultType func_195939_a(ItemUseContext context) {
      World world = context.func_195991_k();
      BlockPos blockpos = context.func_195995_a();
      PlayerEntity player = context.func_195999_j();
      ItemStack stack = context.func_195996_i();
      BlockState blockstate = world.func_180495_p(blockpos);
      BlockState resultToSet = blockstate.getToolModifiedState(world, blockpos, player, stack, ToolType.AXE);
      if (resultToSet != null) {
         world.func_184133_a(player, blockpos, SoundEvents.field_203255_y, SoundCategory.BLOCKS, 1.0F, 1.0F);
      } else {
         if (context.func_196000_l() == Direction.DOWN) {
            return ActionResultType.PASS;
         }

         BlockState foundResult = blockstate.getToolModifiedState(world, blockpos, player, stack, ToolType.SHOVEL);
         if (foundResult != null && world.func_175623_d(blockpos.func_177984_a())) {
            world.func_184133_a(player, blockpos, SoundEvents.field_187771_eN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            resultToSet = foundResult;
         } else if (blockstate.func_177230_c() instanceof CampfireBlock && (Boolean)blockstate.func_177229_b(CampfireBlock.field_220101_b)) {
            if (!world.field_72995_K) {
               world.func_217378_a(null, 1009, blockpos, 0);
            }

            CampfireBlock.func_235475_c_(world, blockpos, blockstate);
            resultToSet = (BlockState)blockstate.func_206870_a(CampfireBlock.field_220101_b, false);
         }
      }

      if (resultToSet == null) {
         return ActionResultType.PASS;
      } else {
         if (!world.field_72995_K) {
            world.func_180501_a(blockpos, resultToSet, 11);
            if (player != null) {
               stack.func_222118_a(1, player, onBroken -> onBroken.func_213334_d(context.func_221531_n()));
            }
         }

         return ActionResultType.func_233537_a_(world.field_72995_K);
      }
   }

   public void func_77663_a(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(itemStack, world, entity, itemSlot, isSelected);
      if (!world.field_72995_K && PaxelEnhancements.shouldEnhance(itemStack)) {
         PaxelEnhancement randomEnhancement = ModConfigs.PAXEL_ENHANCEMENTS.getRandomEnhancement(world.func_201674_k());
         if (randomEnhancement != null) {
            PaxelEnhancements.enhance(itemStack, randomEnhancement);
         }
      }

      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(itemStack);
      if (enhancement != null) {
         enhancement.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
      }
   }

   public void func_77624_a(@Nonnull ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(itemStack, world, tooltip, flag);
      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(itemStack);
      if (enhancement != null) {
         IFormattableTextComponent label = new TranslationTextComponent("paxel_enhancement.name").func_240702_b_(": ");
         tooltip.add(
            label.func_230529_a_(enhancement.getName().func_230530_a_(Style.field_240709_b_.func_240718_a_(enhancement.getColor()).func_240713_a_(true)))
         );
         tooltip.add(enhancement.getDescription().func_230530_a_(Style.field_240709_b_.func_240718_a_(enhancement.getColor())));
      }

      if (PaxelEnhancements.shouldEnhance(itemStack)) {
         IFormattableTextComponent label = new TranslationTextComponent("paxel_enhancement.name").func_240702_b_(": ");
         tooltip.add(label.func_230529_a_(new StringTextComponent("???").func_240699_a_(TextFormatting.GRAY)));
      }

      Map<Enchantment, Integer> enchantments = OverlevelEnchantHelper.getEnchantments(itemStack);
      if (enchantments.size() > 0) {
         tooltip.add(new StringTextComponent(""));
      }
   }
}
