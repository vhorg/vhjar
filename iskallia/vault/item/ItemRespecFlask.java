package iskallia.vault.item;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.LegacyAbilityMapper;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.tree.AbilityTree;
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
            ModConfigs.ABILITIES.get().ifPresent(tree -> tree.iterate(SpecializedSkill.class, skill -> {
               ItemStack stack = new ItemStack(this);
               setAbility(stack, skill.getId());
               items.add(stack);
            }));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      String abilityStr = getAbility(stack);
      if (abilityStr != null) {
         ModConfigs.ABILITIES.getAbilityById(abilityStr).ifPresent(grp -> {
            Component ability = new TextComponent(grp.getName()).withStyle(ChatFormatting.GOLD);
            tooltip.add(TextComponent.EMPTY);
            tooltip.add(new TextComponent("Use to remove selected specialization"));
            tooltip.add(new TextComponent("of ability ").append(ability));
         });
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
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
         return tag.contains("Ability", 8) ? LegacyAbilityMapper.mapAbilityName(tag.getString("Ability")) : null;
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
            Skill skill = tree.getForId(abilityStr).orElse(null);
            if (skill == null || !skill.isUnlocked() || !(skill instanceof SpecializedSkill specialized) || specialized.getIndex() == 0) {
               return InteractionResultHolder.pass(held);
            }
         }

         player.startUsingItem(hand);
         return InteractionResultHolder.consume(held);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private boolean hasAbilityClient(String abilityStr) {
      return ClientAbilityData.getTree().getForId(abilityStr).map(Skill::isUnlocked).orElse(false);
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
         data.getAbilities(player).getForId(abilityStr).ifPresent(skill -> {
            if (skill.isUnlocked() && skill instanceof SpecializedSkill specialized) {
               specialized.resetSpecialization(SkillContext.of(player));
               if (!player.isCreative()) {
                  stack.shrink(1);
               }
            }
         });
      }

      return stack;
   }
}
