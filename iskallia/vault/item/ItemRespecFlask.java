package iskallia.vault.item;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRespecFlask extends Item {
   public ItemRespecFlask(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(2));
      this.setRegistryName(id);
   }

   public void func_150895_a(ItemGroup group, NonNullList<ItemStack> items) {
      if (ModConfigs.ABILITIES != null) {
         if (this.func_194125_a(group)) {
            for (AbilityGroup<?, ?> abilityGroup : ModConfigs.ABILITIES.getAll()) {
               ItemStack stack = new ItemStack(this);
               setAbility(stack, abilityGroup.getParentName());
               items.add(stack);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      String abilityStr = getAbility(stack);
      if (abilityStr != null) {
         AbilityGroup<?, ?> grp = ModConfigs.ABILITIES.getAbilityGroupByName(abilityStr);
         ITextComponent ability = new StringTextComponent(grp.getParentName()).func_240699_a_(TextFormatting.GOLD);
         tooltip.add(StringTextComponent.field_240750_d_);
         tooltip.add(new StringTextComponent("Use to remove selected specialization"));
         tooltip.add(new StringTextComponent("of ability ").func_230529_a_(ability));
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (getAbility(stack) == null) {
         if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            if (stack.func_190916_E() > 1) {
               while (stack.func_190916_E() > 1) {
                  stack.func_190918_g(1);
                  ItemStack flask = new ItemStack(this);
                  MiscUtils.giveItem(player, flask);
               }
            }

            List<AbilityGroup<?, ?>> abilities = ModConfigs.ABILITIES.getAll();
            AbilityGroup<?, ?> group = abilities.get(field_77697_d.nextInt(abilities.size()));
            setAbility(stack, group.getParentName());
         }
      }
   }

   public Rarity func_77613_e(ItemStack stack) {
      return Rarity.UNCOMMON;
   }

   public static void setAbility(ItemStack stack, @Nullable String ability) {
      if (stack.func_77973_b() instanceof ItemRespecFlask) {
         stack.func_196082_o().func_74778_a("Ability", ability);
      }
   }

   @Nullable
   public static String getAbility(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof ItemRespecFlask)) {
         return null;
      } else {
         CompoundNBT tag = stack.func_196082_o();
         return tag.func_150297_b("Ability", 8) ? tag.func_74779_i("Ability") : null;
      }
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack held = player.func_184586_b(hand);
      String abilityStr = getAbility(held);
      if (abilityStr == null) {
         return ActionResult.func_226250_c_(held);
      } else {
         if (world.func_201670_d()) {
            if (!this.hasAbilityClient(abilityStr)) {
               return ActionResult.func_226250_c_(held);
            }
         } else {
            if (!(player instanceof ServerPlayerEntity)) {
               return ActionResult.func_226250_c_(held);
            }

            AbilityTree tree = PlayerAbilitiesData.get(((ServerPlayerEntity)player).func_71121_q()).getAbilities(player);
            AbilityNode<?, ?> node = tree.getNodeByName(abilityStr);
            if (!node.isLearned() || node.getSpecialization() == null) {
               return ActionResult.func_226250_c_(held);
            }
         }

         player.func_184598_c(hand);
         return ActionResult.func_226249_b_(held);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private boolean hasAbilityClient(String abilityStr) {
      AbilityNode<?, ?> node = ClientAbilityData.getLearnedAbilityNode(abilityStr);
      return node == null ? false : node.isLearned() && node.getSpecialization() != null;
   }

   public UseAction func_77661_b(ItemStack stack) {
      return UseAction.DRINK;
   }

   public int func_77626_a(ItemStack stack) {
      return 24;
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entityLiving) {
      if (world instanceof ServerWorld && entityLiving instanceof ServerPlayerEntity) {
         String abilityStr = getAbility(stack);
         if (abilityStr == null) {
            return stack;
         }

         ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
         ServerWorld sWorld = (ServerWorld)world;
         PlayerAbilitiesData data = PlayerAbilitiesData.get(sWorld);
         AbilityNode<?, ?> node = data.getAbilities(player).getNodeByName(abilityStr);
         if (node.isLearned() && node.getSpecialization() != null) {
            data.selectSpecialization(player, abilityStr, null);
            stack.func_190918_g(1);
            return stack;
         }
      }

      return stack;
   }
}
