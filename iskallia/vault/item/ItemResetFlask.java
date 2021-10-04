package iskallia.vault.item;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.ClientTalentData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

public class ItemResetFlask extends Item {
   public ItemResetFlask(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(3));
      this.setRegistryName(id);
   }

   public void func_150895_a(ItemGroup group, NonNullList<ItemStack> items) {
      if (ModConfigs.ABILITIES != null && ModConfigs.TALENTS != null) {
         if (this.func_194125_a(group)) {
            for (AbilityGroup<?, ?> abilityGroup : ModConfigs.ABILITIES.getAll()) {
               ItemStack stack = new ItemStack(this);
               setSkillable(stack, abilityGroup.getParentName());
               items.add(stack);
            }

            for (TalentGroup<?> talentGroup : ModConfigs.TALENTS.getAll()) {
               String talentName = talentGroup.getParentName();
               if (!talentName.equals("Trader")
                  && !talentName.equals("Looter")
                  && !talentName.equals("Artisan")
                  && !talentName.equals("Treasure Hunter")
                  && !talentName.equals("Lucky Altar")) {
                  ItemStack stack = new ItemStack(this);
                  setSkillable(stack, talentGroup.getParentName());
                  items.add(stack);
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      String skillableStr = getSkillable(stack);
      if (skillableStr != null) {
         ModConfigs.ABILITIES.getAbility(skillableStr).ifPresent(abilityGrp -> {
            ITextComponent ability = new StringTextComponent(abilityGrp.getParentName()).func_240699_a_(TextFormatting.GOLD);
            tooltip.add(StringTextComponent.field_240750_d_);
            tooltip.add(new StringTextComponent("Remove one level of Ability ").func_230529_a_(ability));
            tooltip.add(new StringTextComponent("and regain the Skillpoints spent."));
         });
         ModConfigs.TALENTS.getTalent(skillableStr).ifPresent(talentGrp -> {
            ITextComponent talent = new StringTextComponent(talentGrp.getParentName()).func_240699_a_(TextFormatting.AQUA);
            tooltip.add(StringTextComponent.field_240750_d_);
            tooltip.add(new StringTextComponent("Remove one level of Talent ").func_230529_a_(talent));
            tooltip.add(new StringTextComponent("and regain the Skillpoints spent."));
         });
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (getSkillable(stack) == null) {
         if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            if (stack.func_190916_E() > 1) {
               while (stack.func_190916_E() > 1) {
                  stack.func_190918_g(1);
                  ItemStack flask = new ItemStack(this);
                  MiscUtils.giveItem(player, flask);
               }
            }

            List<String> skillables = new ArrayList<>();
            ModConfigs.ABILITIES.getAll().forEach(ability -> skillables.add(ability.getParentName()));
            ModConfigs.TALENTS.getAll().forEach(talent -> skillables.add(talent.getParentName()));
            skillables.remove("Trader");
            skillables.remove("Looter");
            skillables.remove("Artisan");
            skillables.remove("Treasure Hunter");
            skillables.remove("Lucky Altar");
            setSkillable(stack, MiscUtils.getRandomEntry(skillables, field_77697_d));
         }
      }
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack held = player.func_184586_b(hand);
      String skillableStr = getSkillable(held);
      if (skillableStr == null) {
         return ActionResult.func_226250_c_(held);
      } else {
         if (world.func_201670_d()) {
            if (!this.canRevertSkillableClient(skillableStr)) {
               return ActionResult.func_226250_c_(held);
            }
         } else {
            if (!(player instanceof ServerPlayerEntity)) {
               return ActionResult.func_226250_c_(held);
            }

            Optional<AbilityGroup<?, ?>> abilityOpt = ModConfigs.ABILITIES.getAbility(skillableStr);
            if (abilityOpt.isPresent()) {
               AbilityTree abilityTree = PlayerAbilitiesData.get(((ServerPlayerEntity)player).func_71121_q()).getAbilities(player);
               AbilityNode<?, ?> node = abilityTree.getNodeOf(abilityOpt.get());
               if (!node.isLearned()) {
                  return ActionResult.func_226250_c_(held);
               }

               if (node.getLevel() == 1) {
                  for (AbilityGroup<?, ?> dependent : ModConfigs.SKILL_GATES.getGates().getAbilitiesDependingOn(node.getGroup().getParentName())) {
                     if (abilityTree.getNodeOf(dependent).isLearned()) {
                        return ActionResult.func_226250_c_(held);
                     }
                  }
               }
            }

            Optional<TalentGroup<?>> talentOpt = ModConfigs.TALENTS.getTalent(skillableStr);
            if (talentOpt.isPresent()) {
               TalentTree talentTree = PlayerTalentsData.get(((ServerPlayerEntity)player).func_71121_q()).getTalents(player);
               TalentNode<?> nodex = talentTree.getNodeOf(talentOpt.get());
               if (!nodex.isLearned()) {
                  return ActionResult.func_226250_c_(held);
               }

               if (nodex.getLevel() == 1) {
                  for (TalentGroup<?> dependentx : ModConfigs.SKILL_GATES.getGates().getTalentsDependingOn(nodex.getGroup().getParentName())) {
                     if (talentTree.getNodeOf(dependentx).isLearned()) {
                        return ActionResult.func_226250_c_(held);
                     }
                  }
               }
            }
         }

         player.func_184598_c(hand);
         return ActionResult.func_226249_b_(held);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private boolean canRevertSkillableClient(String skillableStr) {
      Optional<AbilityGroup<?, ?>> abilityOpt = ModConfigs.ABILITIES.getAbility(skillableStr);
      if (abilityOpt.isPresent()) {
         AbilityNode<?, ?> node = ClientAbilityData.getLearnedAbilityNode(abilityOpt.get());
         if (node != null && node.isLearned()) {
            if (node.getLevel() == 1) {
               for (AbilityGroup<?, ?> dependent : ModConfigs.SKILL_GATES.getGates().getAbilitiesDependingOn(node.getGroup().getParentName())) {
                  if (ClientAbilityData.getLearnedAbilityNode(dependent) != null) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      Optional<TalentGroup<?>> talentOpt = ModConfigs.TALENTS.getTalent(skillableStr);
      if (talentOpt.isPresent()) {
         TalentNode<?> node = ClientTalentData.getLearnedTalentNode(talentOpt.get());
         if (node != null && node.isLearned()) {
            if (node.getLevel() == 1) {
               for (TalentGroup<?> dependentx : ModConfigs.SKILL_GATES.getGates().getTalentsDependingOn(node.getGroup().getParentName())) {
                  if (ClientTalentData.getLearnedTalentNode(dependentx) != null) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   public UseAction func_77661_b(ItemStack stack) {
      return UseAction.DRINK;
   }

   public int func_77626_a(ItemStack stack) {
      return 24;
   }

   public Rarity func_77613_e(ItemStack stack) {
      return Rarity.RARE;
   }

   public static void setSkillable(ItemStack stack, @Nullable String ability) {
      if (stack.func_77973_b() instanceof ItemResetFlask) {
         stack.func_196082_o().func_74778_a("Skillable", ability);
      }
   }

   @Nullable
   public static String getSkillable(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof ItemResetFlask)) {
         return null;
      } else {
         CompoundNBT tag = stack.func_196082_o();
         return tag.func_150297_b("Skillable", 8) ? tag.func_74779_i("Skillable") : null;
      }
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entityLiving) {
      if (world instanceof ServerWorld && entityLiving instanceof ServerPlayerEntity) {
         String skillableStr = getSkillable(stack);
         if (skillableStr == null) {
            return stack;
         }

         ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
         ServerWorld sWorld = (ServerWorld)world;
         ModConfigs.ABILITIES.getAbility(skillableStr).ifPresent(ability -> {
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(sWorld);
            AbilityTree abilityTree = abilitiesData.getAbilities(player);
            AbilityNode<?, ?> node = abilityTree.getNodeOf((AbilityGroup<?, ?>)ability);
            if (node.isLearned()) {
               if (node.getLevel() == 1) {
                  for (AbilityGroup<?, ?> dependent : ModConfigs.SKILL_GATES.getGates().getAbilitiesDependingOn(node.getGroup().getParentName())) {
                     if (abilityTree.getNodeOf(dependent).isLearned()) {
                        return;
                     }
                  }
               }

               PlayerVaultStatsData.get(sWorld).spendSkillPts(player, -node.getAbilityConfig().getLearningCost());
               abilitiesData.downgradeAbility(player, node);
               stack.func_190918_g(1);
            }
         });
         ModConfigs.TALENTS.getTalent(skillableStr).ifPresent(talent -> {
            PlayerTalentsData talentsData = PlayerTalentsData.get(sWorld);
            TalentTree talentTree = talentsData.getTalents(player);
            TalentNode<?> node = talentTree.getNodeOf((TalentGroup<?>)talent);
            if (node.isLearned()) {
               if (node.getLevel() == 1) {
                  for (TalentGroup<?> dependent : ModConfigs.SKILL_GATES.getGates().getTalentsDependingOn(node.getGroup().getParentName())) {
                     if (talentTree.getNodeOf(dependent).isLearned()) {
                        return;
                     }
                  }
               }

               PlayerVaultStatsData.get(sWorld).spendSkillPts(player, -node.getTalent().getCost());
               talentsData.downgradeTalent(player, node);
               stack.func_190918_g(1);
            }
         });
      }

      return stack;
   }
}
