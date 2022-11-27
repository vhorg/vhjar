package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

public class ItemRelicBoosterPack extends Item {
   public ItemRelicBoosterPack(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.setRegistryName(id);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (!world.isClientSide) {
         int rand = world.random.nextInt(100);
         ItemStack heldStack = player.getItemInHand(hand);
         ItemStack stackToDrop = ItemStack.EMPTY;
         if (rand >= 90) {
            stackToDrop = ModConfigs.UNIDENTIFIED_RELIC_FRAGMENTS.getRandomFragment(world.random);
            successEffects(world, player.position());
         } else {
            failureEffects(world, player.position());
         }

         if (!stackToDrop.isEmpty()) {
            player.drop(stackToDrop, false, false);
         }

         heldStack.shrink(1);
      }

      return super.use(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, world, tooltip, flagIn);
      if ("architect_event".equals(getKey(stack))) {
         tooltip.add(new TextComponent("Architect").withStyle(ChatFormatting.AQUA));
      }
   }

   public static ItemStack getArchitectBoosterPack() {
      ItemStack stack = new ItemStack(ModItems.RELIC_BOOSTER_PACK);
      stack.getOrCreateTag().putString("eventKey", "architect_event");
      return stack;
   }

   @Nullable
   public static String getKey(ItemStack stack) {
      return !stack.hasTag() ? null : stack.getOrCreateTag().getString("eventKey");
   }

   public static void successEffects(Level world, Vec3 pos) {
      world.playSound(null, pos.x, pos.y, pos.z, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
      ((ServerLevel)world).sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 500, 1.0, 1.0, 1.0, 0.5);
   }

   public static void failureEffects(Level world, Vec3 pos) {
      world.playSound(null, pos.x, pos.y, pos.z, ModSounds.BOOSTER_PACK_FAIL_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
      ((ServerLevel)world).sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 500, 1.0, 1.0, 1.0, 0.5);
   }
}
