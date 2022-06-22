package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import iskallia.vault.util.flag.ExplosionImmune;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class FinalVaultFrameBlockItem extends BlockItem implements ExplosionImmune {
   public FinalVaultFrameBlockItem(Block blockIn) {
      super(blockIn, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_234689_a_().func_200917_a(1));
   }

   @Nonnull
   public Rarity func_77613_e(@Nonnull ItemStack stack) {
      return Rarity.EPIC;
   }

   public void func_77624_a(@Nonnull ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT blockEntityTag = stack.func_190925_c("BlockEntityTag");
      String stringUUID = blockEntityTag.func_74779_i("OwnerUUID");
      UUID ownerUUID = stringUUID.isEmpty() ? new UUID(0L, 0L) : UUID.fromString(stringUUID);
      String ownerNickname = blockEntityTag.func_74779_i("OwnerNickname");
      String displayNickname = McClientHelper.getOnlineProfile(ownerUUID).<String>map(GameProfile::getName).orElse(ownerNickname);
      IFormattableTextComponent ownerText = new StringTextComponent("Owner:").func_240699_a_(TextFormatting.GOLD);
      IFormattableTextComponent displayText = new StringTextComponent(displayNickname).func_240699_a_(TextFormatting.GOLD).func_240699_a_(TextFormatting.BOLD);
      tooltip.add(ownerText.func_230529_a_(displayText));
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public void func_77663_a(@Nonnull ItemStack itemStack, World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.field_72995_K) {
         if (entity instanceof ServerPlayerEntity) {
            CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
            if (!blockEntityTag.func_74764_b("OwnerUUID")) {
               ServerPlayerEntity player = (ServerPlayerEntity)entity;
               writeToItemStack(itemStack, player);
               super.func_77663_a(itemStack, world, entity, itemSlot, isSelected);
            }
         }
      }
   }

   public static void writeToItemStack(ItemStack itemStack, ServerPlayerEntity owner) {
      writeToItemStack(itemStack, owner.func_110124_au(), owner.func_200200_C_().getString());
   }

   public static void writeToItemStack(ItemStack itemStack, UUID ownerUUID, String ownerNickname) {
      CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
      blockEntityTag.func_74778_a("OwnerUUID", ownerUUID.toString());
      blockEntityTag.func_74778_a("OwnerNickname", ownerNickname);
   }
}
