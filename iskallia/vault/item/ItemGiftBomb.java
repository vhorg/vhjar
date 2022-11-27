package iskallia.vault.item;

import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.client.gui.overlay.GiftBombOverlay;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

public class ItemGiftBomb extends Item {
   protected ItemGiftBomb.Variant variant;

   public ItemGiftBomb(CreativeModeTab group, ItemGiftBomb.Variant variant, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.variant = variant;
      this.setRegistryName(id);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (heldStack.getItem() instanceof ItemGiftBomb giftBomb) {
         if (!world.isClientSide) {
            ItemStack randomLoot = ModConfigs.GIFT_BOMB.randomLoot(giftBomb.variant);

            while (randomLoot.getCount() > 0) {
               int amount = Math.min(randomLoot.getCount(), randomLoot.getMaxStackSize());
               ItemStack copy = randomLoot.copy();
               copy.setCount(amount);
               randomLoot.shrink(amount);
               player.drop(copy, false, false);
            }

            heldStack.shrink(1);
            if (this.variant.ordinal != -1) {
               CompoundTag nbt = Optional.ofNullable(heldStack.getTag()).orElse(new CompoundTag());
               String gifter = nbt.getString("Gifter");
               ItemStack gifterStatue = LootStatueBlockItem.getStatueBlockItem(gifter);
               player.drop(gifterStatue, false, false);
            }

            Vec3 position = player.position();
            world.playSound(null, position.x, position.y, position.z, ModSounds.GIFT_BOMB_SFX, SoundSource.PLAYERS, 0.55F, 1.0F);
            ((ServerLevel)world).sendParticles(ParticleTypes.EXPLOSION_EMITTER, position.x, position.y, position.z, 3, 1.0, 1.0, 1.0, 0.5);
         } else {
            GiftBombOverlay.pop();
         }
      }

      return InteractionResultHolder.sidedSuccess(heldStack, world.isClientSide());
   }

   public Component getName(ItemStack stack) {
      MutableComponent displayName = (MutableComponent)super.getName(stack);
      displayName.setStyle(Style.EMPTY.withColor(colorForVariant(this.variant)));
      return displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
      TextColor color = colorForVariant(this.variant);
      if (stack.hasTag()) {
         tooltip.add(new TextComponent(""));
         CompoundTag nbt = stack.getTag();
         String gifter = nbt.getString("Gifter");
         int giftedSubs = nbt.getInt("GiftedSubs");
         tooltip.add(this.getPropertyInfo("Gifter", gifter, color));
         tooltip.add(this.getPropertyInfo("Gifted", giftedSubs + " subscribers", color));
      }

      super.appendHoverText(stack, world, tooltip, flagIn);
   }

   private MutableComponent getPropertyInfo(String title, String value, TextColor color) {
      TextComponent titleComponent = new TextComponent(title + ": ");
      titleComponent.setStyle(Style.EMPTY.withColor(color));
      TextComponent valueComponent = new TextComponent(value);
      valueComponent.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
      return titleComponent.append(valueComponent);
   }

   private static TextColor colorForVariant(ItemGiftBomb.Variant variant) {
      if (variant == ItemGiftBomb.Variant.NORMAL) {
         return TextColor.fromRgb(12323595);
      } else if (variant == ItemGiftBomb.Variant.SUPER) {
         return TextColor.fromRgb(10423228);
      } else if (variant == ItemGiftBomb.Variant.MEGA) {
         return TextColor.fromRgb(757692);
      } else if (variant == ItemGiftBomb.Variant.OMEGA) {
         int color = (int)System.currentTimeMillis();
         return TextColor.fromRgb(color);
      } else {
         throw new InternalError("Unknown variant -> " + variant);
      }
   }

   public static ItemStack forGift(ItemGiftBomb.Variant variant, String gifter, int giftedSubs) {
      ItemStack giftBomb = new ItemStack(ofVariant(variant));
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Gifter", gifter);
      nbt.putInt("GiftedSubs", giftedSubs);
      giftBomb.setTag(nbt);
      return giftBomb;
   }

   public static Item ofVariant(ItemGiftBomb.Variant variant) {
      return switch (variant) {
         case NORMAL -> ModItems.NORMAL_GIFT_BOMB;
         case SUPER -> ModItems.SUPER_GIFT_BOMB;
         case MEGA -> ModItems.MEGA_GIFT_BOMB;
         case OMEGA -> ModItems.OMEGA_GIFT_BOMB;
      };
   }

   public static enum Variant {
      NORMAL(-1),
      SUPER(-1),
      MEGA(0),
      OMEGA(1);

      final int ordinal;

      private Variant(int ordinal) {
         this.ordinal = ordinal;
      }
   }
}
