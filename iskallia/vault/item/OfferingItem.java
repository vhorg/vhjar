package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.IManualModelLoading;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

public class OfferingItem extends BasicItem implements IManualModelLoading {
   public OfferingItem(ResourceLocation id) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public static String getModifier(ItemStack stack) {
      if (stack.hasTag()) {
         CompoundTag tag = stack.getTag();
         if (tag != null) {
            return tag.getString("Modifier");
         }
      }

      return "";
   }

   public static List<ItemStack> getItems(ItemStack original) {
      ListTag itemsNbt = original.getOrCreateTag().getList("Items", 10);
      List<ItemStack> items = new ArrayList<>();

      for (Tag itemNbt : itemsNbt) {
         items.add(ItemStack.of((CompoundTag)itemNbt));
      }

      return items;
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (stack.hasTag()) {
         CompoundTag tag = stack.getTag();
         if (tag != null) {
            if (tag.contains("Items")) {
               for (Tag itemTag : tag.getList("Items", 10)) {
                  ItemStack itemStack = ItemStack.of((CompoundTag)itemTag);
                  tooltip.add(
                     new TextComponent("Guaranteed ")
                        .append(new TextComponent(itemStack.getHoverName().getString()).withStyle(ChatFormatting.GREEN))
                        .withStyle(ChatFormatting.GRAY)
                  );
               }
            }

            if (tag.contains("Modifier")) {
               tooltip.add(
                  new TextComponent("Adds \"")
                     .append(getModifierName(tag.getString("Modifier")).withStyle(ChatFormatting.RED))
                     .append("\" to boss")
                     .withStyle(ChatFormatting.GRAY)
               );
            }
         }
      }
   }

   public static MutableComponent getModifierName(String modifierId) {
      return new TranslatableComponent("the_vault.offering_effect." + modifierId);
   }

   public static void setModifier(ItemStack stack, String randomModifier) {
      stack.getOrCreateTag().putString("Modifier", randomModifier);
   }

   public static void setItems(ItemStack stack, List<ItemStack> randomLootItems) {
      ListTag items = new ListTag();
      randomLootItems.forEach(itemStack -> items.add(itemStack.save(new CompoundTag())));
      stack.getOrCreateTag().put("Items", items);
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return OfferingItemRenderer.INSTANCE;
         }
      });
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
   }
}
