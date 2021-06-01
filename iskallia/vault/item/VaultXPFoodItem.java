package iskallia.vault.item;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Food.Builder;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultXPFoodItem extends Item {
   public static Food FOOD = new Builder().func_221454_a(0.0F).func_221456_a(0).func_221457_c().func_221455_b().func_221453_d();
   private final Supplier<Float> min;
   private final Supplier<Float> max;

   public VaultXPFoodItem(ResourceLocation id, Supplier<Float> min, Supplier<Float> max, Properties properties) {
      super(properties.func_221540_a(FOOD));
      this.min = min;
      this.max = max;
      this.setRegistryName(id);
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entityLiving) {
      if (!world.field_72995_K && entityLiving instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)world);
         PlayerVaultStats stats = statsData.getVaultStats(player);
         float randomPercentage = MathUtilities.randomFloat(this.min.get(), this.max.get());
         statsData.addVaultExp(player, (int)(stats.getTnl() * randomPercentage));
      }

      return super.func_77654_b(stack, world, entityLiving);
   }
}
