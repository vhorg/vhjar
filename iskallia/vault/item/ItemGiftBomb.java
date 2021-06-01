package iskallia.vault.item;

import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.client.gui.overlay.GiftBombOverlay;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemGiftBomb extends Item {
   protected ItemGiftBomb.Variant variant;

   public ItemGiftBomb(ItemGroup group, ItemGiftBomb.Variant variant, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.variant = variant;
      this.setRegistryName(id);
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack heldStack = player.func_184586_b(hand);
      Item heldItem = heldStack.func_77973_b();
      if (heldItem instanceof ItemGiftBomb) {
         ItemGiftBomb giftBomb = (ItemGiftBomb)heldItem;
         if (!world.field_72995_K) {
            ItemStack randomLoot = ModConfigs.GIFT_BOMB.randomLoot(giftBomb.variant);

            while (randomLoot.func_190916_E() > 0) {
               int amount = Math.min(randomLoot.func_190916_E(), randomLoot.func_77976_d());
               ItemStack copy = randomLoot.func_77946_l();
               copy.func_190920_e(amount);
               randomLoot.func_190918_g(amount);
               player.func_146097_a(copy, false, false);
            }

            heldStack.func_190918_g(1);
            if (this.variant.ordinal != -1) {
               CompoundNBT nbt = Optional.ofNullable(heldStack.func_77978_p()).orElse(new CompoundNBT());
               String gifter = nbt.func_74779_i("Gifter");
               ItemStack gifterStatue = LootStatueBlockItem.forGift(gifter, this.variant.ordinal, false);
               player.func_146097_a(gifterStatue, false, false);
            }

            Vector3d position = player.func_213303_ch();
            world.func_184148_a(
               null, position.field_72450_a, position.field_72448_b, position.field_72449_c, ModSounds.GIFT_BOMB_SFX, SoundCategory.PLAYERS, 0.55F, 1.0F
            );
            ((ServerWorld)world)
               .func_195598_a(ParticleTypes.field_197626_s, position.field_72450_a, position.field_72448_b, position.field_72449_c, 3, 1.0, 1.0, 1.0, 0.5);
         } else {
            GiftBombOverlay.pop();
         }
      }

      return ActionResult.func_233538_a_(heldStack, world.func_201670_d());
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      IFormattableTextComponent displayName = (IFormattableTextComponent)super.func_200295_i(stack);
      displayName.func_230530_a_(Style.field_240709_b_.func_240718_a_(colorForVariant(this.variant)));
      return displayName;
   }

   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      Color color = colorForVariant(this.variant);
      if (stack.func_77942_o()) {
         tooltip.add(new StringTextComponent(""));
         CompoundNBT nbt = stack.func_77978_p();
         String gifter = nbt.func_74779_i("Gifter");
         int giftedSubs = nbt.func_74762_e("GiftedSubs");
         tooltip.add(this.getPropertyInfo("Gifter", gifter, color));
         tooltip.add(this.getPropertyInfo("Gifted", giftedSubs + " subscribers", color));
      }

      super.func_77624_a(stack, world, tooltip, flagIn);
   }

   private IFormattableTextComponent getPropertyInfo(String title, String value, Color color) {
      StringTextComponent titleComponent = new StringTextComponent(title + ": ");
      titleComponent.func_230530_a_(Style.field_240709_b_.func_240718_a_(color));
      StringTextComponent valueComponent = new StringTextComponent(value);
      valueComponent.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
      return titleComponent.func_230529_a_(valueComponent);
   }

   private static Color colorForVariant(ItemGiftBomb.Variant variant) {
      if (variant == ItemGiftBomb.Variant.NORMAL) {
         return Color.func_240743_a_(12323595);
      } else if (variant == ItemGiftBomb.Variant.SUPER) {
         return Color.func_240743_a_(10423228);
      } else if (variant == ItemGiftBomb.Variant.MEGA) {
         return Color.func_240743_a_(757692);
      } else if (variant == ItemGiftBomb.Variant.OMEGA) {
         int color = (int)System.currentTimeMillis();
         return Color.func_240743_a_(color);
      } else {
         throw new InternalError("Unknown variant -> " + variant);
      }
   }

   public static ItemStack forGift(ItemGiftBomb.Variant variant, String gifter, int giftedSubs) {
      ItemStack giftBomb = new ItemStack(ofVariant(variant));
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Gifter", gifter);
      nbt.func_74768_a("GiftedSubs", giftedSubs);
      giftBomb.func_77982_d(nbt);
      return giftBomb;
   }

   public static Item ofVariant(ItemGiftBomb.Variant variant) {
      switch (variant) {
         case NORMAL:
            return ModItems.NORMAL_GIFT_BOMB;
         case SUPER:
            return ModItems.SUPER_GIFT_BOMB;
         case MEGA:
            return ModItems.MEGA_GIFT_BOMB;
         case OMEGA:
            return ModItems.OMEGA_GIFT_BOMB;
         default:
            throw new InternalError("Unknown Gift Bomb variant: " + variant);
      }
   }

   public static enum Variant {
      NORMAL(-1),
      SUPER(-1),
      MEGA(0),
      OMEGA(1);

      int ordinal;

      private Variant(int ordinal) {
         this.ordinal = ordinal;
      }
   }
}
