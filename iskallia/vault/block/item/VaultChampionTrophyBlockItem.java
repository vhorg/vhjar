package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultChampionTrophyBlockItem extends BlockItem {
   public static final String NBT_OWNER_UUID = "OwnerUUID";
   public static final String NBT_OWNER_NICK = "OwnerNickname";
   public static final String NBT_VARIANT = "Variant";
   public static final String NBT_SCORE = "Score";

   public VaultChampionTrophyBlockItem(Block block) {
      super(block, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
   }

   public void func_150895_a(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
      if (this.func_194125_a(group)) {
         items.add(create(null, VaultChampionTrophy.Variant.GOLDEN));
         items.add(create(null, VaultChampionTrophy.Variant.PLATINUM));
         items.add(create(null, VaultChampionTrophy.Variant.BLUE_SILVER));
         items.add(create(null, VaultChampionTrophy.Variant.SILVER));
      }
   }

   public void func_77663_a(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.field_72995_K) {
         if (entity instanceof ServerPlayerEntity) {
            CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
            if (!blockEntityTag.func_74764_b("OwnerUUID")) {
               ServerPlayerEntity player = (ServerPlayerEntity)entity;
               blockEntityTag.func_74778_a("OwnerUUID", player.func_110124_au().toString());
               blockEntityTag.func_74778_a("OwnerNickname", player.func_200200_C_().getString());
               super.func_77663_a(itemStack, world, entity, itemSlot, isSelected);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
      super.func_77624_a(stack, worldIn, tooltip, flag);
      CompoundNBT blockEntityTag = stack.func_190925_c("BlockEntityTag");
      String uuidString = blockEntityTag.func_74779_i("OwnerUUID");
      UUID ownerUUID = uuidString.isEmpty() ? null : UUID.fromString(uuidString);
      String ownerNickname = McClientHelper.getOnlineProfile(ownerUUID).<String>map(GameProfile::getName).orElse(blockEntityTag.func_74779_i("OwnerNickname"));
      int score = blockEntityTag.func_74762_e("Score");
      IFormattableTextComponent titleText = new StringTextComponent("Vault Champion").func_240699_a_(TextFormatting.GOLD);
      IFormattableTextComponent championText = new StringTextComponent("Mighty " + ownerNickname)
         .func_240699_a_(TextFormatting.GOLD)
         .func_240699_a_(TextFormatting.BOLD);
      IFormattableTextComponent scoreText = new StringTextComponent("Score: ")
         .func_240699_a_(TextFormatting.GOLD)
         .func_230529_a_(new StringTextComponent(String.valueOf(score)).func_240699_a_(TextFormatting.AQUA));
      tooltip.add(new StringTextComponent(""));
      tooltip.add(titleText);
      tooltip.add(championText);
      tooltip.add(scoreText);
   }

   public static void setScore(ItemStack itemStack, int score) {
      CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
      blockEntityTag.func_74768_a("Score", score);
   }

   public static ItemStack create(ServerPlayerEntity owner, VaultChampionTrophy.Variant variant) {
      return create(owner == null ? null : owner.func_110124_au(), owner == null ? null : owner.func_200200_C_().getString(), variant);
   }

   public static ItemStack create(UUID ownerUUID, String ownerNickname, VaultChampionTrophy.Variant variant) {
      ItemStack itemStack = new ItemStack(ModBlocks.VAULT_CHAMPION_TROPHY_BLOCK_ITEM);
      CompoundNBT nbt = itemStack.func_196082_o();
      CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
      if (ownerUUID != null) {
         blockEntityTag.func_74778_a("OwnerUUID", ownerUUID.toString());
      }

      if (ownerNickname != null) {
         blockEntityTag.func_74778_a("OwnerNickname", ownerNickname);
      }

      blockEntityTag.func_74778_a("Variant", variant.func_176610_l());
      nbt.func_74768_a("CustomModelData", variant.ordinal());
      return itemStack;
   }
}
