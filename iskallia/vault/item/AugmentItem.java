package iskallia.vault.item;

import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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

public class AugmentItem extends Item implements VaultLevelItem, DataTransferItem {
   public AugmentItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group));
      this.setRegistryName(id);
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
      super.appendHoverText(stack, world, tooltip, advanced);
      getTheme(stack)
         .ifPresent(key -> tooltip.add(new TextComponent("Theme: ").append(new TextComponent(key.getName()).withStyle(Style.EMPTY.withColor(key.getColor())))));
   }

   public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         for (ThemeKey key : VaultRegistry.THEME.getKeys()) {
            ItemStack stack = new ItemStack(ModItems.AUGMENT);
            stack.getOrCreateTag().putString("theme", key.getId().toString());
            items.add(stack);
         }
      }
   }

   public static int getColor(ItemStack stack) {
      return getTheme(stack).map(ThemeKey::getColor).orElse(16777215);
   }

   public static Optional<ThemeKey> getTheme(ItemStack stack) {
      if (stack.getTag() == null) {
         return Optional.empty();
      } else {
         CompoundTag nbt = stack.getOrCreateTag();
         if (!nbt.contains("theme", 8)) {
            return Optional.empty();
         } else {
            ResourceLocation theme = new ResourceLocation(nbt.getString("theme"));
            return Optional.ofNullable(VaultRegistry.THEME.getKey(theme));
         }
      }
   }

   public static ItemStack create(ResourceLocation theme) {
      ItemStack stack = new ItemStack(ModItems.AUGMENT);
      stack.getOrCreateTag().putString("theme", theme.toString());
      return stack;
   }

   @Override
   public void initializeVaultLoot(int vaultLevel, ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      Optional.ofNullable(vault)
         .flatMap(v -> v.getOptional(Vault.WORLD))
         .map(world -> world.get(WorldManager.THEME))
         .ifPresent(theme -> stack.getOrCreateTag().putString("theme", theme.toString()));
   }

   @Override
   public ItemStack convertStack(ItemStack stack, RandomSource random) {
      if (stack.getTag() == null) {
         return stack;
      } else {
         CompoundTag nbt = stack.getOrCreateTag();
         if (!nbt.contains("pool", 8)) {
            return stack;
         } else {
            ResourceLocation pool = new ResourceLocation(nbt.getString("pool"));
            return ModConfigs.AUGMENT.generate(pool, random).orElse(stack);
         }
      }
   }
}
