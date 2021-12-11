package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.VaultGearHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.command.EnumArgument;

public class GearCommand extends Command {
   @Override
   public String getName() {
      return "gear";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("add").then(Commands.func_197056_a("modifier", StringArgumentType.string()).executes(this::addModifier)));
      builder.then(Commands.func_197057_a("remove").then(Commands.func_197056_a("modifier", StringArgumentType.string()).executes(this::removeModifier)));
      builder.then(
         Commands.func_197057_a("rarity").then(Commands.func_197056_a("rarity", EnumArgument.enumArgument(VaultGear.Rarity.class)).executes(this::setRarity))
      );
      builder.then(Commands.func_197057_a("tier").then(Commands.func_197056_a("tier", IntegerArgumentType.integer(0, 2)).executes(this::setTier)));
   }

   private int addModifier(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity player = ((CommandSource)context.getSource()).func_197035_h();
      String modifierName = StringArgumentType.getString(context, "modifier");
      VAttribute<?, ?> attribute = ModAttributes.REGISTRY.get(new ResourceLocation(modifierName));
      if (attribute == null) {
         player.func_145747_a(new StringTextComponent("No modifier with name " + modifierName), Util.field_240973_b_);
         return 0;
      } else {
         ItemStack held = player.func_184614_ca();
         if (!held.func_190926_b() && held.func_77973_b() instanceof VaultGear) {
            VaultGear.Rarity gearRarity = ModAttributes.GEAR_RARITY.getBase(held).orElse(VaultGear.Rarity.COMMON);
            int tier = ModAttributes.GEAR_TIER.getBase(held).orElse(0);
            VaultGearHelper.applyGearModifier(held, gearRarity, tier, attribute);
            return 0;
         } else {
            player.func_145747_a(new StringTextComponent("No vault gear in hand!"), Util.field_240973_b_);
            return 0;
         }
      }
   }

   private int removeModifier(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity player = ((CommandSource)context.getSource()).func_197035_h();
      String modifierName = StringArgumentType.getString(context, "modifier");
      VAttribute<?, ?> attribute = ModAttributes.REGISTRY.get(new ResourceLocation(modifierName));
      if (attribute == null) {
         player.func_145747_a(new StringTextComponent("No modifier with name " + modifierName), Util.field_240973_b_);
         return 0;
      } else {
         ItemStack held = player.func_184614_ca();
         if (!held.func_190926_b() && held.func_77973_b() instanceof VaultGear) {
            VaultGearHelper.removeAttribute(held, attribute);
            return 0;
         } else {
            player.func_145747_a(new StringTextComponent("No vault gear in hand!"), Util.field_240973_b_);
            return 0;
         }
      }
   }

   private int setRarity(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity player = ((CommandSource)context.getSource()).func_197035_h();
      VaultGear.Rarity rarity = (VaultGear.Rarity)context.getArgument("rarity", VaultGear.Rarity.class);
      ItemStack held = player.func_184614_ca();
      if (!held.func_190926_b() && held.func_77973_b() instanceof VaultGear) {
         ModAttributes.GEAR_RARITY.create(held, rarity);
         return 0;
      } else {
         player.func_145747_a(new StringTextComponent("No vault gear in hand!"), Util.field_240973_b_);
         return 0;
      }
   }

   private int setTier(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity player = ((CommandSource)context.getSource()).func_197035_h();
      int tier = IntegerArgumentType.getInteger(context, "tier");
      ItemStack held = player.func_184614_ca();
      if (!held.func_190926_b() && held.func_77973_b() instanceof VaultGear) {
         ModAttributes.GEAR_TIER.create(held, tier);
         return 0;
      } else {
         player.func_145747_a(new StringTextComponent("No vault gear in hand!"), Util.field_240973_b_);
         return 0;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
