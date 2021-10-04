package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.time.extension.FruitExtension;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Food.Builder;
import net.minecraft.item.Item.Properties;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemVaultFruit extends Item {
   public static Food VAULT_FRUIT_FOOD = new Builder().func_221454_a(0.0F).func_221456_a(0).func_221457_c().func_221455_b().func_221453_d();
   protected int extraVaultTicks;

   public ItemVaultFruit(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
      super(new Properties().func_200916_a(group).func_221540_a(VAULT_FRUIT_FOOD).func_200917_a(64));
      this.setRegistryName(id);
      this.extraVaultTicks = extraVaultTicks;
   }

   public boolean addTime(World world, PlayerEntity player) {
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         VaultRaid vault = VaultRaidData.get((ServerWorld)world).getActiveFor(player.func_110124_au());
         if (vault == null) {
            return false;
         } else {
            for (VaultObjective objective : vault.getAllObjectives()) {
               if (objective.preventsEatingExtensionFruit(world.func_73046_m(), vault)) {
                  return false;
               }
            }

            vault.getPlayers().forEach(vPlayer -> vPlayer.getTimer().addTime(new FruitExtension(this), 0));
            return true;
         }
      } else {
         return false;
      }
   }

   public int getExtraVaultTicks() {
      return this.extraVaultTicks;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemStack = playerIn.func_184586_b(handIn);
      return playerIn.field_70170_p.func_234923_W_() != Vault.VAULT_KEY
         ? ActionResult.func_226251_d_(itemStack)
         : super.func_77659_a(worldIn, playerIn, handIn);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(new StringTextComponent(""));
      StringTextComponent comp = new StringTextComponent("[!] Only edible inside a Vault");
      comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)).func_240722_b_(true));
      tooltip.add(comp);
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      IFormattableTextComponent displayName = (IFormattableTextComponent)super.func_200295_i(stack);
      return displayName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16563456)));
   }

   public static class BitterLemon extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("bitter_lemon").func_76348_h();

      public BitterLemon(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack func_77654_b(ItemStack stack, World worldIn, LivingEntity entityLiving) {
         if (!worldIn.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
            if (!this.addTime(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -6);
            worldIn.func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_206933_aM,
               SoundCategory.MASTER,
               1.0F,
               1.0F
            );
         }

         return super.func_77654_b(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
         tooltip.add(new StringTextComponent(""));
         StringTextComponent comp = new StringTextComponent("A magical lemon with a bitter taste");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         tooltip.add(new StringTextComponent(""));
         comp = new StringTextComponent("- Wipes away 3 hearts");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
         tooltip.add(comp);
         comp = new StringTextComponent("- Adds 30 seconds to the Vault Timer");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(65280)));
         tooltip.add(comp);
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class MysticPear extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("mystic_pear").func_76348_h();

      public MysticPear(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack func_77654_b(ItemStack stack, World worldIn, LivingEntity entityLiving) {
         if (!worldIn.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
            if (!this.addTime(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -MathUtilities.getRandomInt(10, 20));
            if (MathUtilities.randomFloat(0.0F, 100.0F) <= 50.0F) {
               player.func_195064_c(new EffectInstance(Effects.field_76436_u, 600));
            } else {
               player.func_195064_c(new EffectInstance(Effects.field_82731_v, 600));
            }

            worldIn.func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_206933_aM,
               SoundCategory.MASTER,
               1.0F,
               1.0F
            );
         }

         return super.func_77654_b(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
         tooltip.add(new StringTextComponent(""));
         StringTextComponent comp = new StringTextComponent("A magical pear with a strange taste");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         tooltip.add(new StringTextComponent(""));
         comp = new StringTextComponent("- Wipes away 5 to 10 hearts");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
         tooltip.add(comp);
         comp = new StringTextComponent("- Inflicts with either Wither or Poison effect");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
         tooltip.add(comp);
         comp = new StringTextComponent("- Adds 5 minutes to the Vault Timer");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(65280)));
         tooltip.add(comp);
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class SourOrange extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("sour_orange").func_76348_h();

      public SourOrange(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack func_77654_b(ItemStack stack, World worldIn, LivingEntity entityLiving) {
         if (!worldIn.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
            if (!this.addTime(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -10);
            worldIn.func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_206933_aM,
               SoundCategory.MASTER,
               1.0F,
               1.0F
            );
         }

         return super.func_77654_b(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
         tooltip.add(new StringTextComponent(""));
         StringTextComponent comp = new StringTextComponent("A magical orange with a sour taste");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         comp = new StringTextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(12512238)).func_240722_b_(true));
         tooltip.add(comp);
         tooltip.add(new StringTextComponent(""));
         comp = new StringTextComponent("- Wipes away 5 hearts");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
         tooltip.add(comp);
         comp = new StringTextComponent("- Adds 60 seconds to the Vault Timer");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(65280)));
         tooltip.add(comp);
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class SweetKiwi extends ItemVaultFruit {
      public SweetKiwi(ItemGroup group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack func_77654_b(ItemStack stack, World worldIn, LivingEntity entityLiving) {
         if (!worldIn.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
            if (!this.addTime(worldIn, player)) {
               return stack;
            }

            worldIn.func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_206933_aM,
               SoundCategory.MASTER,
               1.0F,
               1.0F
            );
         }

         return super.func_77654_b(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
         tooltip.add(new StringTextComponent(""));
         StringTextComponent comp = new StringTextComponent("- Adds 5 seconds to the Vault Timer");
         comp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(65280)));
         tooltip.add(comp);
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      }
   }
}
