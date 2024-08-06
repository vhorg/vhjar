package iskallia.vault.item;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.item.tool.CatalystItemRenderer;
import iskallia.vault.item.tool.IManualModelLoading;
import iskallia.vault.util.CodecUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

public class InfusedCatalystItem extends Item implements VaultLevelItem, IManualModelLoading, DataInitializationItem {
   public static int MODELS = 14;

   public InfusedCatalystItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   public static ItemStack create(int size, List<ResourceLocation> modifiers) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CATALYST_INFUSED);
      setSize(stack, size);
      setModifiers(stack, modifiers);
      return stack;
   }

   public static ItemStack createDisplay(ResourceLocation pool, int minSize, int maxSize, List<ResourceLocation> modifiers) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CATALYST_INFUSED);
      stack.getOrCreateTag().putString("pool", pool.toString());
      if (minSize == maxSize) {
         setSize(stack, minSize);
      } else {
         ListTag size = new ListTag();
         size.add(IntTag.valueOf(minSize));
         size.add(IntTag.valueOf(maxSize));
         stack.getOrCreateTag().put("size", size);
      }

      setModifiers(stack, modifiers);
      return stack;
   }

   public static Optional<Integer> getSize(ItemStack stack) {
      return stack.getTag() != null && stack.getTag().contains("size", 99) ? Optional.of(stack.getTag().getInt("size")) : Optional.of(10);
   }

   public static List<ResourceLocation> getModifiers(ItemStack stack) {
      List<ResourceLocation> modifiers = new ArrayList<>();
      if (stack.getTag() == null) {
         return modifiers;
      } else {
         for (Tag child : stack.getTag().getList("modifiers", 8)) {
            if (child instanceof StringTag string) {
               modifiers.add(new ResourceLocation(string.getAsString()));
            }
         }

         return modifiers;
      }
   }

   public static void setSize(ItemStack stack, int size) {
      stack.getOrCreateTag().putInt("size", size);
   }

   private static void setModifiers(ItemStack stack, List<ResourceLocation> modifiers) {
      ListTag list = new ListTag();
      modifiers.forEach(modifier -> list.add(StringTag.valueOf(modifier.toString())));
      stack.getOrCreateTag().put("modifiers", list);
   }

   @OnlyIn(Dist.CLIENT)
   @ParametersAreNonnullByDefault
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      List<ResourceLocation> modifiers = getModifiers(stack);
      getSize(stack)
         .ifPresentOrElse(
            size -> tooltip.add(new TextComponent("Size: ").append(new TextComponent(String.valueOf(size)).withStyle(Style.EMPTY.withColor(14540253)))),
            () -> tooltip.add(new TextComponent("Size: ???").withStyle(ChatFormatting.GRAY))
         );
      if (!modifiers.isEmpty()) {
         for (ResourceLocation modifierId : modifiers) {
            VaultModifierRegistry.getOpt(modifierId).ifPresent(vaultModifier -> {
               tooltip.add(new TextComponent(" • " + vaultModifier.getDisplayName()).withStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor())));
               if (Screen.hasShiftDown()) {
                  tooltip.add(new TextComponent("    " + vaultModifier.getDisplayDescription()).withStyle(ChatFormatting.DARK_GRAY));
               }
            });
         }
      }
   }

   public static void setJeiModifiers(ItemStack itemStack, List<ResourceLocation> result) {
      if (itemStack.getItem() == ModItems.VAULT_CATALYST_INFUSED) {
         CompoundTag tag = itemStack.getOrCreateTag();
         CodecUtils.writeNBT(ResourceLocation.CODEC.listOf(), result, nbt -> tag.put("modifiers", nbt));
      }
   }

   @Override
   public void initializeVaultLoot(int vaultLevel, ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      stack.getOrCreateTag().putInt("level", vaultLevel);
   }

   @Override
   public void initialize(ItemStack stack, RandomSource rand) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
         if (nbt.contains("pool", 8)) {
            ResourceLocation pool = new ResourceLocation(nbt.getString("pool"));
            int level = nbt.getInt("level");
            ModConfigs.CATALYST.generate(pool, level, rand).ifPresent(result -> stack.getOrCreateTag().merge(result));
            nbt.remove("pool");
            nbt.remove("level");
         }
      }
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      for (int i = 0; i < MODELS; i++) {
         consumer.accept(new ModelResourceLocation("the_vault:catalyst/%d#inventory".formatted(i)));
      }
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return CatalystItemRenderer.INSTANCE;
         }
      });
   }
}
