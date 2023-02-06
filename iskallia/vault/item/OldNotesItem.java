package iskallia.vault.item;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class OldNotesItem extends Item {
   public static final String NBTKEY_HINT = "HintId";

   public OldNotesItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, @Nonnull Level world, @Nonnull Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(itemStack, world, entity, slotId, isSelected);
      if (!world.isClientSide()) {
         CompoundTag nbt = itemStack.getOrCreateTag();
         String hintId = nbt.getString("HintId");
         if (hintId.isEmpty()) {
            ResourceLocation randomHintId = ModConfigs.HINT_PAPER.getRandomHint(JavaRandom.ofNanoTime());
            nbt.putString("HintId", randomHintId.toString());
         }
      }
   }

   public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag pIsAdvanced) {
      super.appendHoverText(itemStack, world, tooltip, pIsAdvanced);
      CompoundTag nbt = itemStack.getOrCreateTag();
      String hintId = nbt.getString("HintId");
      if (!hintId.isEmpty()) {
         String localeCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
         ResourceLocation id = ResourceLocation.tryParse(hintId);
         tooltip.add(new TextComponent(""));
         tooltip.add(ModConfigs.HINT_PAPER.getHint(localeCode, id));
      }
   }
}
