package iskallia.vault.item;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.item.Food.Builder;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VaultXPFoodItem extends Item {
   public static Food FOOD = new Builder().func_221454_a(0.0F).func_221456_a(0).func_221457_c().func_221455_b().func_221453_d();
   private final int levelLimit;

   public VaultXPFoodItem(ResourceLocation id, Properties properties) {
      this(id, properties, -1);
   }

   public VaultXPFoodItem(ResourceLocation id, Properties properties, int levelLimit) {
      super(properties.func_221540_a(FOOD));
      this.setRegistryName(id);
      this.levelLimit = levelLimit;
   }

   public UseAction func_77661_b(ItemStack stack) {
      return super.func_77661_b(stack);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (this.levelLimit > 0) {
         int vaultLevel;
         if (player instanceof ServerPlayerEntity) {
            vaultLevel = PlayerVaultStatsData.get(((ServerPlayerEntity)player).func_71121_q()).getVaultStats(player).getVaultLevel();
         } else {
            vaultLevel = this.getVaultLevel();
         }

         if (vaultLevel >= this.levelLimit) {
            return ActionResult.func_226250_c_(player.func_184586_b(hand));
         }
      }

      return super.func_77659_a(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   private int getVaultLevel() {
      return VaultBarOverlay.vaultLevel;
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entityLiving) {
      if (!world.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
         this.grantExp(player);
      }

      return super.func_77654_b(stack, world, entityLiving);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (this.levelLimit > 0) {
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(
            new StringTextComponent("Can't be conumed after Level: ")
               .func_240699_a_(TextFormatting.GRAY)
               .func_230529_a_(new StringTextComponent(String.valueOf(this.levelLimit)).func_240699_a_(TextFormatting.AQUA))
         );
      }
   }

   public abstract void grantExp(ServerPlayerEntity var1);

   public static class Flat extends VaultXPFoodItem {
      private final Supplier<Integer> min;
      private final Supplier<Integer> max;

      public Flat(ResourceLocation id, Supplier<Integer> min, Supplier<Integer> max, Properties properties) {
         this(id, min, max, properties, -1);
      }

      public Flat(ResourceLocation id, Supplier<Integer> min, Supplier<Integer> max, Properties properties, int levelRequirement) {
         super(id, properties, levelRequirement);
         this.min = min;
         this.max = max;
      }

      @Override
      public void grantExp(ServerPlayerEntity sPlayer) {
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get(sPlayer.func_71121_q());
         statsData.addVaultExp(sPlayer, MathUtilities.getRandomInt(this.min.get(), this.max.get()));
      }
   }

   public static class Percent extends VaultXPFoodItem {
      private final Supplier<Float> min;
      private final Supplier<Float> max;

      public Percent(ResourceLocation id, Supplier<Float> min, Supplier<Float> max, Properties properties) {
         this(id, min, max, properties, -1);
      }

      public Percent(ResourceLocation id, Supplier<Float> min, Supplier<Float> max, Properties properties, int levelRequirement) {
         super(id, properties, levelRequirement);
         this.min = min;
         this.max = max;
      }

      @Override
      public void grantExp(ServerPlayerEntity sPlayer) {
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get(sPlayer.func_71121_q());
         PlayerVaultStats stats = statsData.getVaultStats(sPlayer);
         float randomPercentage = MathUtilities.randomFloat(this.min.get(), this.max.get());
         statsData.addVaultExp(sPlayer, (int)(stats.getTnl() * randomPercentage));
      }
   }
}
