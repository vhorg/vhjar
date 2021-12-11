package iskallia.vault.item;

import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.init.ModSounds;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemUnidentifiedArtifact extends Item {
   public static int artifactOverride = -1;

   public ItemUnidentifiedArtifact(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (!world.field_72995_K) {
         ItemStack heldStack = player.func_184586_b(hand);
         Vector3d position = player.func_213303_ch();
         ((ServerWorld)world)
            .func_184148_a(
               null,
               position.field_72450_a,
               position.field_72448_b,
               position.field_72449_c,
               ModSounds.BOOSTER_PACK_SUCCESS_SFX,
               SoundCategory.PLAYERS,
               1.0F,
               1.0F
            );
         ((ServerWorld)world)
            .func_195598_a(ParticleTypes.field_197616_i, position.field_72450_a, position.field_72448_b, position.field_72449_c, 500, 1.0, 1.0, 1.0, 0.5);
         ItemStack artifactStack;
         if (artifactOverride != -1) {
            artifactStack = VaultArtifactBlock.createArtifact(artifactOverride);
            artifactOverride = -1;
         } else {
            artifactStack = VaultArtifactBlock.createRandomArtifact();
         }

         player.func_146097_a(artifactStack, false, false);
         heldStack.func_190918_g(1);
      }

      return super.func_77659_a(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      StringTextComponent text = new StringTextComponent("Right click to identify.");
      text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-9472)));
      tooltip.add(text);
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public boolean func_77636_d(ItemStack stack) {
      return true;
   }
}
