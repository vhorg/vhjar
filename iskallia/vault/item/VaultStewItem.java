package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SoupItem;
import net.minecraft.item.Food.Builder;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultStewItem extends SoupItem {
   public static Food FOOD = new Builder().func_221454_a(0.0F).func_221456_a(0).func_221457_c().func_221455_b().func_221453_d();
   private final VaultStewItem.Rarity rarity;

   public VaultStewItem(ResourceLocation id, VaultStewItem.Rarity rarity, Properties builder) {
      super(builder);
      this.setRegistryName(id);
      this.rarity = rarity;
   }

   public VaultStewItem.Rarity getRarity() {
      return this.rarity;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K && this.getRarity() == VaultStewItem.Rarity.MYSTERY) {
         ItemStack heldStack = player.func_184586_b(hand);
         String randomPart = ModConfigs.VAULT_STEW.STEW_POOL.getRandom(world.field_73012_v);
         ItemStack stackToDrop = new ItemStack(
            (IItemProvider)Registry.field_212630_s.func_241873_b(new ResourceLocation(randomPart)).orElse(Items.field_190931_a)
         );
         ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
         player.func_146097_a(stackToDrop, false, false);
         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entity) {
      if (this.getRarity() != VaultStewItem.Rarity.MYSTERY && entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)world);
         PlayerVaultStats stats = statsData.getVaultStats(player);
         statsData.addVaultExp(player, (int)(stats.getTnl() * this.getRarity().tnlProgress));
      }

      return super.func_77654_b(stack, world, entity);
   }

   public static enum Rarity {
      MYSTERY(0.0F),
      NORMAL(0.2F),
      RARE(0.4F),
      EPIC(0.65F),
      OMEGA(0.99F);

      public final float tnlProgress;

      private Rarity(float tnlProgress) {
         this.tnlProgress = tnlProgress;
      }
   }
}
