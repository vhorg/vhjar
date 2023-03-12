package iskallia.vault.item;

import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AugmentItem extends Item implements VaultLevelItem, DataTransferItem {
   public AugmentItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group));
      this.setRegistryName(id);
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
      super.appendHoverText(stack, world, tooltip, advanced);
      getTheme(stack)
         .ifPresent(key -> tooltip.add(new TextComponent("Theme: ").append(new TextComponent(key.getName()).withStyle(Style.EMPTY.withColor(key.getColor())))));
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

   @Override
   public void initializeVaultLoot(Vault vault, ItemStack stack, @Nullable BlockPos pos) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.THEME)).ifPresent(theme -> stack.getOrCreateTag().putString("pool", theme.toString()));
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
