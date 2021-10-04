package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.VaultRaid;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRelicBoosterPack extends Item {
   public ItemRelicBoosterPack(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         int rand = world.field_73012_v.nextInt(100);
         ItemStack heldStack = player.func_184586_b(hand);
         ItemStack stackToDrop = ItemStack.field_190927_a;
         if (rand == 99) {
            RelicPartItem randomPart = ModConfigs.VAULT_RELICS.getRandomPart();
            stackToDrop = new ItemStack(randomPart);
            successEffects(world, player.func_213303_ch());
         } else if (rand == 98) {
            stackToDrop = new ItemStack(ModItems.MYSTERY_BOX);
            successEffects(world, player.func_213303_ch());
         } else if (rand == 97 && "architect_event".equals(getKey(heldStack))) {
            stackToDrop = VaultCrystalItem.getCrystalWithObjective(VaultRaid.ARCHITECT_EVENT.get().getId());
            successEffects(world, player.func_213303_ch());
         } else {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            ServerWorld serverWorld = serverPlayer.func_71121_q();
            float coef = MathUtilities.randomFloat(0.1F, 0.25F);
            PlayerVaultStatsData.get(serverWorld).addVaultExp(serverPlayer, (int)(90.0F * coef));
            failureEffects(world, player.func_213303_ch());
         }

         if (!stackToDrop.func_190926_b()) {
            player.func_146097_a(stackToDrop, false, false);
         }

         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, world, tooltip, flagIn);
      if ("architect_event".equals(getKey(stack))) {
         tooltip.add(new StringTextComponent("Architect").func_240699_a_(TextFormatting.AQUA));
      }
   }

   public static ItemStack getArchitectBoosterPack() {
      ItemStack stack = new ItemStack(ModItems.RELIC_BOOSTER_PACK);
      stack.func_196082_o().func_74778_a("eventKey", "architect_event");
      return stack;
   }

   @Nullable
   public static String getKey(ItemStack stack) {
      return !stack.func_77942_o() ? null : stack.func_196082_o().func_74779_i("eventKey");
   }

   public static void successEffects(World world, Vector3d pos) {
      world.func_184148_a(null, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundCategory.PLAYERS, 1.0F, 1.0F);
      ((ServerWorld)world).func_195598_a(ParticleTypes.field_197616_i, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 500, 1.0, 1.0, 1.0, 0.5);
   }

   public static void failureEffects(World world, Vector3d pos) {
      world.func_184148_a(null, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, ModSounds.BOOSTER_PACK_FAIL_SFX, SoundCategory.PLAYERS, 1.0F, 1.0F);
      ((ServerWorld)world).func_195598_a(ParticleTypes.field_197601_L, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 500, 1.0, 1.0, 1.0, 0.5);
   }
}
