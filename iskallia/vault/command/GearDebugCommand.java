package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class GearDebugCommand extends Command {
   private static final Random rand = new Random();

   @Override
   public String getName() {
      return "gear_debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("setLevel").then(Commands.argument("level", IntegerArgumentType.integer(0, 100)).executes(this::setLevel)));
      builder.then(Commands.literal("rollType").then(Commands.argument("rollType", StringArgumentType.string()).executes(this::setRollType)));
      builder.then(Commands.literal("addModifier").executes(this::addModifier));
      builder.then(Commands.literal("removeModifier").executes(this::removeModifier));
      builder.then(Commands.literal("rerollAll").executes(this::rerollAllModifiers));
      builder.then(Commands.literal("rerollWithTag").then(Commands.argument("tag", StringArgumentType.string()).executes(this::rerollAllModifiersWithTag)));
      builder.then(Commands.literal("unpack").executes(this::unpackGearData));
      builder.then(Commands.literal("pack").executes(this::packGearData));
   }

   private int packGearData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = player.getMainHandItem();
      if (!gear.isEmpty() && AttributeGearData.hasData(gear)) {
         CompoundTag tag = gear.getOrCreateTag().getCompound("nbtGearData");
         AttributeGearData.<AttributeGearData>fromNbt(gear, tag).write(gear);
         gear.getOrCreateTag().remove("nbtGearData");
         return 0;
      } else {
         player.sendMessage(new TextComponent("No gear data found on item."), Util.NIL_UUID);
         return 0;
      }
   }

   private int unpackGearData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = player.getMainHandItem();
      if (!gear.isEmpty() && AttributeGearData.hasData(gear)) {
         CompoundTag data = AttributeGearData.<AttributeGearData>read(gear).toNbt();
         gear.getOrCreateTag().put("nbtGearData", data);
         return 0;
      } else {
         player.sendMessage(new TextComponent("No gear data found on item."), Util.NIL_UUID);
         return 0;
      }
   }

   private int rerollAllModifiersWithTag(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = this.getHeldGear(player);
      String tag = StringArgumentType.getString(ctx, "tag");
      VaultGearTagConfig.ModTagGroup groupTag = ModConfigs.VAULT_GEAR_TAG_CONFIG.getGroupTag(tag);
      if (groupTag == null) {
         player.sendMessage(new TextComponent("Could not add modifier of tag " + tag), Util.NIL_UUID);
         return 0;
      } else {
         GearModification.Result result = VaultGearModifierHelper.reForgeAllWithTag(groupTag, gear, rand);
         if (!result.success()) {
            player.sendMessage(new TextComponent("Could not add modifier of tag " + tag), Util.NIL_UUID);
         }

         return 0;
      }
   }

   private int rerollAllModifiers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = this.getHeldGear(player);
      VaultGearModifierHelper.reForgeAllModifiers(gear, rand);
      return 0;
   }

   private int removeModifier(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = this.getHeldGear(player);
      if (!VaultGearModifierHelper.removeRandomModifier(gear, rand).success()) {
         player.sendMessage(new TextComponent("No modifiers remaining to remove."), Util.NIL_UUID);
      }

      return 0;
   }

   private int addModifier(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      long gameTime = player.getCommandSenderWorld().getGameTime();
      ItemStack gear = this.getHeldGear(player);
      if (!VaultGearModifierHelper.addNewModifier(gear, gameTime, rand).success()) {
         player.sendMessage(new TextComponent("No empty modifier slots remaining."), Util.NIL_UUID);
      }

      return 0;
   }

   private int setRollType(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = this.getHeldGear(player);
      String rollType = StringArgumentType.getString(ctx, "rollType");
      if (!ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPoolNames().contains(rollType)) {
         player.sendMessage(new TextComponent("Unknown roll type " + rollType), Util.NIL_UUID);
         player.sendMessage(new TextComponent("Known roll types: " + ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPoolNames().toString()), Util.NIL_UUID);
         return 0;
      } else {
         VaultGearData data = VaultGearData.read(gear);
         data.updateAttribute(ModGearAttributes.GEAR_ROLL_TYPE, rollType);
         data.write(gear);
         return 0;
      }
   }

   private int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack gear = this.getHeldGear(player);
      int level = IntegerArgumentType.getInteger(ctx, "level");
      VaultGearData data = VaultGearData.read(gear);
      data.setItemLevel(level);
      data.write(gear);
      return 0;
   }

   private ItemStack getHeldGear(ServerPlayer player) {
      ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (!(held.getItem() instanceof VaultGearItem)) {
         player.sendMessage(new TextComponent("No vaultgear held in hand"), Util.NIL_UUID);
         throw new IllegalArgumentException("Not vaultgear in hand");
      } else {
         return held;
      }
   }
}
