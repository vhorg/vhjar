package iskallia.vault.item;

import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.VaultRarity;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemVaultCrystal extends Item {
   private VaultRarity vaultRarity;

   public ItemVaultCrystal(ItemGroup group, ResourceLocation id, VaultRarity vaultRarity) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
      this.vaultRarity = vaultRarity;
   }

   public static ItemStack getRandomCrystal() {
      return getCrystal(VaultRarity.getWeightedRandom());
   }

   public static ItemStack getCrystal(VaultRarity rarity) {
      switch (rarity) {
         case COMMON:
            return new ItemStack(ModItems.VAULT_CRYSTAL_NORMAL);
         case RARE:
            return new ItemStack(ModItems.VAULT_CRYSTAL_RARE);
         case EPIC:
            return new ItemStack(ModItems.VAULT_CRYSTAL_EPIC);
         case OMEGA:
            return new ItemStack(ModItems.VAULT_CRYSTAL_OMEGA);
         default:
            return new ItemStack(ModItems.VAULT_CRYSTAL_NORMAL);
      }
   }

   public static ItemStack getCrystalWithBoss(String playerBossName) {
      ItemStack stack = getRandomCrystal();
      stack.func_196082_o().func_74778_a("playerBossName", playerBossName);
      return stack;
   }

   public static ItemStack getCrystalWithBoss(VaultRarity rarity, String playerBossName) {
      ItemStack stack = getCrystal(rarity);
      stack.func_196082_o().func_74778_a("playerBossName", playerBossName);
      return stack;
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      if (context.func_195991_k().field_72995_K) {
         return super.func_195939_a(context);
      } else {
         ItemStack stack = context.func_195999_j().func_184614_ca();
         Item item = stack.func_77973_b();
         if (item instanceof ItemVaultCrystal) {
            ItemVaultCrystal crystal = (ItemVaultCrystal)item;
            String playerBossName = "";
            CompoundNBT tag = stack.func_196082_o();
            if (tag.func_150296_c().contains("playerBossName")) {
               playerBossName = tag.func_74779_i("playerBossName");
            }

            BlockPos pos = context.func_195995_a();
            if (this.tryCreatePortal(crystal, context.func_195991_k(), pos, context.func_196000_l(), playerBossName, getData(stack))) {
               context.func_195991_k()
                  .func_184148_a(
                     null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), ModSounds.VAULT_PORTAL_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F
                  );
               context.func_195996_i().func_190918_g(1);
               IFormattableTextComponent text = new StringTextComponent("");
               text.func_230529_a_(new StringTextComponent(context.func_195999_j().func_200200_C_().getString()).func_240699_a_(TextFormatting.GREEN));
               text.func_230529_a_(new StringTextComponent(" has created a "));
               String rarityName = crystal.getRarity().name().toLowerCase();
               rarityName = rarityName.substring(0, 1).toUpperCase() + rarityName.substring(1);
               text.func_230529_a_(new StringTextComponent(rarityName).func_240699_a_(crystal.getRarity().color));
               text.func_230529_a_(new StringTextComponent(" Vault!"));
               context.func_195991_k().func_73046_m().func_184103_al().func_232641_a_(text, ChatType.CHAT, context.func_195999_j().func_110124_au());
               return ActionResultType.SUCCESS;
            }
         }

         return super.func_195939_a(context);
      }
   }

   private boolean tryCreatePortal(ItemVaultCrystal crystal, World world, BlockPos pos, Direction facing, String playerBossName, CrystalData data) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(world, pos.func_177972_a(facing), Axis.X);
      if (optional.isPresent()) {
         optional.get().placePortalBlocks(crystal, playerBossName, data);
         return true;
      } else {
         return false;
      }
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      if (stack.func_77973_b() instanceof ItemVaultCrystal) {
         ItemVaultCrystal item = (ItemVaultCrystal)stack.func_77973_b();
         CompoundNBT tag = stack.func_196082_o();
         if (tag.func_150296_c().contains("playerBossName")) {
            return new StringTextComponent(item.getRarity().color + "Vault Crystal (" + tag.func_74779_i("playerBossName") + ")");
         }

         switch (item.getRarity()) {
            case COMMON:
               return new StringTextComponent(item.getRarity().color + "Vault Crystal (common)");
            case RARE:
               return new StringTextComponent(item.getRarity().color + "Vault Crystal (rare)");
            case EPIC:
               return new StringTextComponent(item.getRarity().color + "Vault Crystal (epic)");
            case OMEGA:
               return new StringTextComponent(item.getRarity().color + "Vault crystal (omega)");
         }
      }

      return super.func_200295_i(stack);
   }

   public VaultRarity getRarity() {
      return this.vaultRarity;
   }

   public static CrystalData getData(ItemStack stack) {
      return new CrystalData(stack);
   }

   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      getData(stack).addInformation(world, tooltip, flag);
      super.func_77624_a(stack, world, tooltip, flag);
   }
}
