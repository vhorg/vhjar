package iskallia.vault.item.crystal;

import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.RenameType;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class VaultCrystalItem extends Item {
   private static final Random rand = new Random();

   public VaultCrystalItem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
   }

   public static CrystalData getData(ItemStack stack) {
      return new CrystalData(stack);
   }

   public static ItemStack getCrystalWithBoss(String playerBossName) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData data = new CrystalData(stack);
      data.setPlayerBossName(playerBossName);
      data.setType(CrystalData.Type.RAFFLE);
      return stack;
   }

   public static ItemStack getCrystalWithObjective(ResourceLocation objectiveKey) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData data = new CrystalData(stack);
      data.setSelectedObjective(objectiveKey);
      if (rand.nextBoolean()) {
         data.setType(CrystalData.Type.COOP);
      }

      return stack;
   }

   public void func_150895_a(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.func_194125_a(group)) {
         for (CrystalData.Type crystalType : CrystalData.Type.values()) {
            if (crystalType.visibleInCreative()) {
               ItemStack crystal = new ItemStack(this);
               getData(crystal).setType(crystalType);
               items.add(crystal);
            }
         }
      }
   }

   public ActionResultType func_195939_a(ItemUseContext context) {
      if (!context.func_195991_k().field_72995_K && context.func_195999_j() != null) {
         ItemStack stack = context.func_195999_j().func_184586_b(context.func_221531_n());
         CrystalData data = new CrystalData(stack);
         BlockPos pos = context.func_195995_a();
         if (this.tryCreatePortal(context.func_195991_k(), pos, context.func_196000_l(), data)) {
            context.func_195991_k()
               .func_184148_a(
                  null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), ModSounds.VAULT_PORTAL_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F
               );
            IFormattableTextComponent playerName = context.func_195999_j().func_145748_c_().func_230532_e_();
            playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
            StringTextComponent suffix = new StringTextComponent(" opened a Vault!");
            context.func_195991_k()
               .func_73046_m()
               .func_184103_al()
               .func_232641_a_(
                  new StringTextComponent("").func_230529_a_(playerName).func_230529_a_(suffix), ChatType.CHAT, context.func_195999_j().func_110124_au()
               );
            context.func_195996_i().func_190918_g(1);
            return ActionResultType.SUCCESS;
         } else {
            return super.func_195939_a(context);
         }
      } else {
         return super.func_195939_a(context);
      }
   }

   private boolean tryCreatePortal(World world, BlockPos pos, Direction facing, CrystalData data) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(world, pos.func_177972_a(facing), Axis.X);
      if (optional.isPresent()) {
         optional.get().placePortalBlocks(data);
         return true;
      } else {
         return false;
      }
   }

   public static long getSeed(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof VaultCrystalItem)) {
         return 0L;
      } else {
         CompoundNBT nbt = stack.func_196082_o();
         if (!nbt.func_150297_b("Seed", 4)) {
            setRandomSeed(stack);
         }

         return nbt.func_74763_f("Seed");
      }
   }

   public static void setRandomSeed(ItemStack stack) {
      if (stack.func_77973_b() instanceof VaultCrystalItem) {
         stack.func_196082_o().func_74772_a("Seed", rand.nextLong());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      getData(stack).addInformation(world, tooltip, flag);
      super.func_77624_a(stack, world, tooltip, flag);
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, PlayerEntity player, Hand handIn) {
      if (worldIn.field_72995_K) {
         return super.func_77659_a(worldIn, player, handIn);
      } else if (handIn == Hand.OFF_HAND) {
         return super.func_77659_a(worldIn, player, handIn);
      } else {
         ItemStack stack = player.func_184614_ca();
         CrystalData data = getData(stack);
         if (!data.getPlayerBossName().isEmpty() && player.func_225608_bj_()) {
            final CompoundNBT nbt = new CompoundNBT();
            nbt.func_74768_a("RenameType", RenameType.VAULT_CRYSTAL.ordinal());
            nbt.func_218657_a("Data", stack.serializeNBT());
            NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
               public ITextComponent func_145748_c_() {
                  return new StringTextComponent("Rename Raffle Boss");
               }

               @Nullable
               public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  return new RenamingContainer(windowId, nbt);
               }
            }, buffer -> buffer.func_150786_a(nbt));
         }

         return super.func_77659_a(worldIn, player, handIn);
      }
   }
}