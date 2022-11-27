package iskallia.vault.item;

import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.init.ModSounds;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemUnidentifiedArtifact extends Item {
   public static int artifactOverride = -1;

   public ItemUnidentifiedArtifact(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.setRegistryName(id);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (!world.isClientSide) {
         ItemStack heldStack = player.getItemInHand(hand);
         Vec3 position = player.position();
         ((ServerLevel)world).playSound(null, position.x, position.y, position.z, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
         ((ServerLevel)world).sendParticles(ParticleTypes.DRAGON_BREATH, position.x, position.y, position.z, 500, 1.0, 1.0, 1.0, 0.5);
         ItemStack artifactStack;
         if (artifactOverride != -1) {
            artifactStack = VaultArtifactBlock.createArtifact(artifactOverride);
            artifactOverride = -1;
         } else {
            artifactStack = VaultArtifactBlock.createRandomArtifact();
         }

         player.drop(artifactStack, false, false);
         heldStack.shrink(1);
      }

      return super.use(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      TextComponent text = new TextComponent("Right click to identify.");
      text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(-9472)));
      tooltip.add(text);
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }
}
