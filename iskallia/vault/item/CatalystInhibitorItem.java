package iskallia.vault.item;

import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.boss.BossProtectionCatalystEntity;
import iskallia.vault.entity.boss.CatalystInhibitorEntity;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CatalystInhibitorItem extends Item {
   private final BossProtectionCatalystEntity.CatalystType catalystType;

   public CatalystInhibitorItem(ResourceLocation registryName, BossProtectionCatalystEntity.CatalystType catalystType) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP));
      this.catalystType = catalystType;
      this.setRegistryName(registryName);
   }

   public BossProtectionCatalystEntity.CatalystType getCatalystType() {
      return this.catalystType;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (this.isInvalid(player, this.getVaultId(stack))) {
         return InteractionResultHolder.fail(stack);
      } else {
         level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.SNOWBALL_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
         );
         if (!level.isClientSide) {
            CatalystInhibitorEntity inhibitor = new CatalystInhibitorEntity(level, player, stack);
            inhibitor.setItem(stack);
            inhibitor.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(inhibitor);
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
      }
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      if (nbt == null) {
         return super.getName(stack);
      } else {
         MutableComponent name = new TranslatableComponent(stack.getItem().getDescriptionId());
         return stack.hasTag() && stack.getOrCreateTag().contains("rotten")
            ? new TextComponent("")
               .append(new TextComponent("Rotten ").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00680A"))))
               .append(name.withStyle(ChatFormatting.WHITE))
            : name;
      }
   }

   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (stack.hasTag() && stack.getOrCreateTag().contains("rotten")) {
         tooltip.add(tooltip.size(), new TextComponent("Rotten items can not be used in the vault").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
      }
   }

   @javax.annotation.Nullable
   private UUID getVaultId(ItemStack inhibitor) {
      CompoundTag tag = inhibitor.getTag();
      return tag != null && tag.contains("VaultId") ? tag.getUUID("VaultId") : null;
   }

   public void setVaultId(ItemStack inhibitor, UUID vaultId) {
      inhibitor.getOrCreateTag().putUUID("VaultId", vaultId);
   }

   public boolean isInvalid(Player player, @javax.annotation.Nullable UUID vaultId) {
      return player.getLevel().isClientSide()
         ? ClientVaults.getActive().map(v -> !v.get(Vault.ID).equals(vaultId)).orElse(vaultId != null)
         : ServerVaults.get(player.getLevel()).map(v -> !v.get(Vault.ID).equals(vaultId)).orElse(vaultId != null);
   }
}
