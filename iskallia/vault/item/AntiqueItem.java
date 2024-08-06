package iskallia.vault.item;

import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.config.AntiquesConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.TextComponentUtils;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AntiqueItem extends Item implements DynamicModelItem {
   private static final RandomSource rewardRandom = JavaRandom.ofNanoTime();

   public AntiqueItem(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP));
      this.setRegistryName(id);
   }

   public Rarity getRarity(ItemStack pStack) {
      return Rarity.RARE;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (this.allowdedIn(tab)) {
         AntiqueRegistry.sorted().forEach(antique -> items.add(createStack(antique)));
      }
   }

   public Component getName(ItemStack pStack) {
      return Optional.ofNullable(getAntique(pStack))
         .map(Antique::getConfig)
         .map(cfg -> cfg.getInfo().getName())
         .<TextComponent>map(TextComponent::new)
         .map(cmp -> (Component)cmp)
         .orElseGet(() -> super.getName(pStack));
   }

   public int getItemStackLimit(ItemStack stack) {
      return Optional.ofNullable(getAntique(stack))
         .map(Antique::getConfig)
         .map(AntiquesConfig.Entry::getInfo)
         .map(AntiquesConfig.Info::getRequiredCount)
         .orElse(super.getItemStackLimit(stack));
   }

   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
      Optional.ofNullable(getAntique(stack))
         .map(Antique::getConfig)
         .map(AntiquesConfig.Entry::getInfo)
         .filter(info -> info.getRewardDescription() != null && info.getSubtext() != null)
         .ifPresent(
            info -> {
               tooltip.add(TextComponent.EMPTY);
               tooltip.add(new TextComponent(info.getRewardDescription()).withStyle(ChatFormatting.GOLD));
               tooltip.add(TextComponent.EMPTY);
               Arrays.stream(info.getSubtext().split("\n"))
                  .<TextComponent>map(TextComponent::new)
                  .map(cmp -> TextComponentUtils.applyStyle(cmp, Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)))
                  .forEach(tooltip::add);
            }
         );
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      Antique antique = getAntique(stack);
      if (antique != null) {
         AntiquesConfig.Entry cfg = antique.getConfig();
         if (cfg != null) {
            int requiredCount = cfg.getInfo().getRequiredCount();
            if (stack.getCount() >= requiredCount && level instanceof ServerLevel sLevel && player instanceof ServerPlayer sPlayer) {
               int playerLevel = PlayerVaultStatsData.get(sLevel).getVaultStats(sPlayer).getVaultLevel();
               int attempts = 200;

               while (attempts-- > 0) {
                  List<ItemStack> out = cfg.getReward().generateReward(rewardRandom, sPlayer, playerLevel);
                  out.removeIf(ItemStack::isEmpty);
                  if (!out.isEmpty()) {
                     stack.shrink(requiredCount);
                     Vec3 pos = player.position();
                     level.playSound(null, pos.x, pos.y, pos.z, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
                     out.forEach(reward -> {
                        for (int stackCount = reward.getCount(); stackCount > 0; stackCount = reward.getCount()) {
                           ItemStack split = reward.copy();
                           split.setCount(Math.min(stackCount, reward.getMaxStackSize()));
                           reward.setCount(stackCount - split.getCount());
                           player.drop(split, false, true);
                        }
                     });
                     return InteractionResultHolder.success(stack);
                  }
               }

               return InteractionResultHolder.fail(stack);
            }
         }
      }

      return InteractionResultHolder.pass(stack);
   }

   public static ItemStack createStack(Antique antique) {
      return createStack(antique, 1);
   }

   public static ItemStack createStack(Antique antique, int count) {
      ItemStack stack = new ItemStack(ModItems.ANTIQUE, count);
      setAntique(stack, antique);
      return stack;
   }

   @Nullable
   public static Antique getAntique(ItemStack stack) {
      if (stack.hasTag() && stack.is(ModItems.ANTIQUE)) {
         CompoundTag tag = stack.getOrCreateTag();
         ResourceLocation key = ResourceLocation.tryParse(tag.getString("antique"));
         return key == null ? null : (Antique)AntiqueRegistry.getRegistry().getValue(key);
      } else {
         return null;
      }
   }

   public static void setAntique(ItemStack stack, @Nullable Antique antique) {
      CompoundTag tag = stack.getOrCreateTag();
      if (antique == null) {
         tag.remove("antique");
      } else {
         tag.putString("antique", antique.getRegistryName().toString());
      }
   }

   public static String buildAntiqueCountText(@Nullable String text, ItemStack stack) {
      int requiredCount = Optional.ofNullable(getAntique(stack))
         .map(Antique::getConfig)
         .map(AntiquesConfig.Entry::getInfo)
         .map(AntiquesConfig.Info::getRequiredCount)
         .orElse(1);
      if (text != null && !text.isEmpty()) {
         String colorlessText = ChatFormatting.stripFormatting(text);

         try {
            int parsedCount = Integer.parseInt(colorlessText);
            if (parsedCount >= requiredCount) {
               text = ChatFormatting.GOLD + colorlessText + "/" + requiredCount;
            } else {
               text = text + "/" + requiredCount;
            }
         } catch (NumberFormatException var5) {
         }
      } else if (stack.getCount() >= requiredCount) {
         text = ChatFormatting.GOLD.toString() + stack.getCount() + "/" + requiredCount;
      } else {
         text = stack.getCount() + "/" + requiredCount;
      }

      return text;
   }

   @Override
   public Optional<ResourceLocation> getDynamicModelId(ItemStack itemStack) {
      return Optional.ofNullable(getAntique(itemStack)).map(ModDynamicModels.Antiques::getModel).map(DynamicModel::getId);
   }
}
