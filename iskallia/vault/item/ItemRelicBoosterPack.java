package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemRelicBoosterPack extends Item {
   public ItemRelicBoosterPack(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         float rand = world.field_73012_v.nextFloat() * 100.0F;
         ItemStack heldStack = player.func_184586_b(hand);
         ItemStack stackToDrop = null;
         if (rand >= 99.0F) {
            RelicPartItem randomPart = ModConfigs.VAULT_RELICS.getRandomPart();
            stackToDrop = new ItemStack(randomPart);
            successEffects(world, player.func_213303_ch());
         } else if (rand >= 98.0F) {
            stackToDrop = new ItemStack(ModItems.PANDORAS_BOX);
            successEffects(world, player.func_213303_ch());
         } else {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            ServerWorld serverWorld = serverPlayer.func_71121_q();
            int exp = ModConfigs.PLAYER_EXP.getRelicBoosterPackExp();
            float coef = MathUtilities.randomFloat(0.1F, 0.5F);
            PlayerVaultStatsData.get(serverWorld).addVaultExp(serverPlayer, (int)(exp * coef));
            failureEffects(world, player.func_213303_ch());
         }

         if (stackToDrop != null) {
            player.func_146097_a(stackToDrop, false, false);
         }

         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }

   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, world, tooltip, flagIn);
   }

   public static void successEffects(World world, Vector3d position) {
      world.func_184148_a(
         null, position.field_72450_a, position.field_72448_b, position.field_72449_c, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundCategory.PLAYERS, 1.0F, 1.0F
      );
      ((ServerWorld)world)
         .func_195598_a(ParticleTypes.field_197616_i, position.field_72450_a, position.field_72448_b, position.field_72449_c, 500, 1.0, 1.0, 1.0, 0.5);
   }

   public static void failureEffects(World world, Vector3d position) {
      world.func_184148_a(
         null, position.field_72450_a, position.field_72448_b, position.field_72449_c, ModSounds.BOOSTER_PACK_FAIL_SFX, SoundCategory.PLAYERS, 1.0F, 1.0F
      );
      ((ServerWorld)world)
         .func_195598_a(ParticleTypes.field_197601_L, position.field_72450_a, position.field_72448_b, position.field_72449_c, 500, 1.0, 1.0, 1.0, 0.5);
   }
}
