package iskallia.vault.item.gear;

import iskallia.vault.Vault;
import iskallia.vault.attribute.EnumAttribute;
import iskallia.vault.config.EtchingConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BasicItem;
import iskallia.vault.util.MiscUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EtchingItem extends BasicItem {
   public EtchingItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public static ItemStack createEtchingStack(VaultGear.Set set) {
      ItemStack etchingStack = new ItemStack(ModItems.ETCHING);
      ModAttributes.GEAR_SET.create(etchingStack, set);
      ModAttributes.GEAR_STATE.create(etchingStack, VaultGear.State.IDENTIFIED);
      return etchingStack;
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      ITextComponent name = super.func_200295_i(stack);
      ModAttributes.GEAR_SET.getValue(stack).ifPresent(set -> {
         EtchingConfig.Etching etching = ModConfigs.ETCHING.getFor(set);
         Style style = name.func_150256_b().func_240718_a_(Color.func_240743_a_(etching.color));
         ((IFormattableTextComponent)name).func_230530_a_(style);
      });
      return name;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ActionResult<ItemStack> result = super.func_77659_a(world, player, hand);
      ItemStack stack = player.func_184586_b(hand);
      if (world.func_201670_d()) {
         return result;
      } else {
         if (world.func_234923_W_() != Vault.VAULT_KEY) {
            Optional<EnumAttribute<VaultGear.State>> attribute = ModAttributes.GEAR_STATE.get(stack);
            if (attribute.isPresent() && attribute.get().getValue(stack) == VaultGear.State.UNIDENTIFIED) {
               attribute.get().setBaseValue(VaultGear.State.ROLLING);
               return ActionResult.func_226251_d_(stack);
            }
         }

         return result;
      }
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         if (world instanceof ServerWorld && stack.func_190916_E() > 1) {
            while (stack.func_190916_E() > 1) {
               stack.func_190918_g(1);
               ItemStack flask = new ItemStack(this);
               MiscUtils.giveItem(player, flask);
            }
         }

         if (ModAttributes.GEAR_STATE.getOrCreate(stack, VaultGear.State.UNIDENTIFIED).getValue(stack) == VaultGear.State.ROLLING) {
            this.tickRoll(stack, world, player, itemSlot, isSelected);
         }
      }
   }

   public void tickRoll(ItemStack stack, World world, ServerPlayerEntity player, int itemSlot, boolean isSelected) {
      int rollTicks = stack.func_196082_o().func_74762_e("RollTicks");
      int lastModelHit = stack.func_196082_o().func_74762_e("LastModelHit");
      double displacement = VaultGear.getDisplacement(rollTicks);
      if (rollTicks >= 120) {
         ModAttributes.GEAR_STATE.create(stack, VaultGear.State.IDENTIFIED);
         stack.func_196082_o().func_82580_o("RollTicks");
         stack.func_196082_o().func_82580_o("LastModelHit");
         world.func_184133_a(null, player.func_233580_cy_(), ModSounds.CONFETTI_SFX, SoundCategory.PLAYERS, 0.5F, 1.0F);
      } else {
         if ((int)displacement != lastModelHit) {
            VaultGear.Set set = ModConfigs.ETCHING.getRandomSet();
            ModAttributes.GEAR_SET.create(stack, set);
            stack.func_196082_o().func_74768_a("LastModelHit", (int)displacement);
            world.func_184133_a(null, player.func_233580_cy_(), ModSounds.RAFFLE_SFX, SoundCategory.PLAYERS, 0.4F, 1.0F);
         }

         stack.func_196082_o().func_74768_a("RollTicks", rollTicks + 1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void func_77624_a(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      super.func_77624_a(stack, world, tooltip, flag);
      ModAttributes.GEAR_SET
         .get(stack)
         .map(attribute -> attribute.getValue(stack))
         .ifPresent(
            value -> {
               if (value != VaultGear.Set.NONE) {
                  EtchingConfig.Etching etching = ModConfigs.ETCHING.getFor(value);
                  tooltip.add(
                     new StringTextComponent("Etching: ")
                        .func_230529_a_(
                           new StringTextComponent(value.name()).func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(etching.color)))
                        )
                  );
                  tooltip.add(StringTextComponent.field_240750_d_);

                  for (TextComponent descriptionLine : this.split(etching.effectText)) {
                     tooltip.add(descriptionLine.func_240699_a_(TextFormatting.GRAY));
                  }
               }
            }
         );
   }

   private List<TextComponent> split(String text) {
      LinkedList<TextComponent> tooltip = new LinkedList<>();
      StringBuilder sb = new StringBuilder();

      for (String word : text.split("\\s+")) {
         sb.append(word).append(" ");
         if (sb.length() >= 30) {
            tooltip.add(new StringTextComponent(sb.toString().trim()));
            sb = new StringBuilder();
         }
      }

      if (sb.length() > 0) {
         tooltip.add(new StringTextComponent(sb.toString().trim()));
      }

      return tooltip;
   }
}
