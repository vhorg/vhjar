package iskallia.vault.item;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRespecFlask extends Item {
   public ItemRespecFlask(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(2));
      this.setRegistryName(id);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (ModConfigs.ABILITIES != null) {
         if (this.allowdedIn(group)) {
            for (AbilityGroup<?, ?> abilityGroup : ModConfigs.ABILITIES.getAll()) {
               ItemStack stack = new ItemStack(this);
               setAbility(stack, abilityGroup.getParentName());
               items.add(stack);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      String abilityStr = getAbility(stack);
      if (abilityStr != null) {
         AbilityGroup<?, ?> grp = ModConfigs.ABILITIES.getAbilityGroupByName(abilityStr);
         Component ability = new TextComponent(grp.getParentName()).withStyle(ChatFormatting.GOLD);
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(new TextComponent("Use to remove selected specialization"));
         tooltip.add(new TextComponent("of ability ").append(ability));
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (getAbility(stack) == null) {
         if (world instanceof ServerLevel && entity instanceof ServerPlayer player) {
            if (stack.getCount() > 1) {
               while (stack.getCount() > 1) {
                  stack.shrink(1);
                  ItemStack flask = new ItemStack(this);
                  MiscUtils.giveItem(player, flask);
               }
            }

            List<AbilityGroup<?, ?>> abilities = ModConfigs.ABILITIES.getAll();
            AbilityGroup<?, ?> group = abilities.get(world.random.nextInt(abilities.size()));
            setAbility(stack, group.getParentName());
         }
      }
   }

   public Rarity getRarity(ItemStack stack) {
      return Rarity.UNCOMMON;
   }

   public static void setAbility(ItemStack stack, @Nullable String ability) {
      if (stack.getItem() instanceof ItemRespecFlask) {
         stack.getOrCreateTag().putString("Ability", ability);
      }
   }

   @Nullable
   public static String getAbility(ItemStack stack) {
      if (!(stack.getItem() instanceof ItemRespecFlask)) {
         return null;
      } else {
         CompoundTag tag = stack.getOrCreateTag();
         return tag.contains("Ability", 8) ? tag.getString("Ability") : null;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack held = player.getItemInHand(hand);
      String abilityStr = getAbility(held);
      if (abilityStr == null) {
         return InteractionResultHolder.pass(held);
      } else {
         if (world.isClientSide()) {
            if (!this.hasAbilityClient(abilityStr)) {
               return InteractionResultHolder.pass(held);
            }
         } else {
            if (!(player instanceof ServerPlayer)) {
               return InteractionResultHolder.pass(held);
            }

            AbilityTree tree = PlayerAbilitiesData.get(((ServerPlayer)player).getLevel()).getAbilities(player);
            AbilityNode<?, ?> node = tree.getNodeByName(abilityStr);
            if (!node.isLearned() || node.getSpecialization() == null) {
               return InteractionResultHolder.pass(held);
            }
         }

         player.startUsingItem(hand);
         return InteractionResultHolder.consume(held);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private boolean hasAbilityClient(String abilityStr) {
      AbilityNode<?, ?> node = ClientAbilityData.getLearnedAbilityNode(abilityStr);
      return node == null ? false : node.isLearned() && node.getSpecialization() != null;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public int getUseDuration(ItemStack stack) {
      return 24;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
      if (world instanceof ServerLevel sWorld && entityLiving instanceof ServerPlayer player) {
         String abilityStr = getAbility(stack);
         if (abilityStr == null) {
            return stack;
         }

         PlayerAbilitiesData data = PlayerAbilitiesData.get(sWorld);
         AbilityNode<?, ?> node = data.getAbilities(player).getNodeByName(abilityStr);
         if (node.isLearned() && node.getSpecialization() != null) {
            data.selectSpecialization(player, node, null);
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            return stack;
         }
      }

      return stack;
   }
}
