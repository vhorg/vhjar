package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemSkillOrb extends Item {
   public ItemSkillOrb(ItemGroup group) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(Vault.id("skill_orb"));
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack heldItemStack = player.func_184586_b(hand);
      world.func_184148_a(
         null,
         player.func_226277_ct_(),
         player.func_226278_cu_(),
         player.func_226281_cx_(),
         SoundEvents.field_187802_ec,
         SoundCategory.NEUTRAL,
         0.5F,
         0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F)
      );
      if (!world.field_72995_K) {
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)world);
         statsData.addSkillPoint((ServerPlayerEntity)player, 1);
      }

      player.func_71029_a(Stats.field_75929_E.func_199076_b(this));
      if (!player.field_71075_bZ.field_75098_d) {
         heldItemStack.func_190918_g(1);
      }

      return ActionResult.func_233538_a_(heldItemStack, world.func_201670_d());
   }
}
