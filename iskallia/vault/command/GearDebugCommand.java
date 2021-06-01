package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.EntityHelper;
import java.util.Random;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;

public class GearDebugCommand extends Command {
   private static final Random COLOR_RAND = new Random();

   @Override
   public String getName() {
      return "gear_debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(
                     Commands.func_197057_a("helmet")
                        .then(
                           Commands.func_197056_a("model", IntegerArgumentType.integer(0, 11))
                              .executes(ctx -> this.giveHelmet(ctx, IntegerArgumentType.getInteger(ctx, "model")))
                        )
                  ))
                  .then(
                     Commands.func_197057_a("chestplate")
                        .then(
                           Commands.func_197056_a("model", IntegerArgumentType.integer(0, 11))
                              .executes(ctx -> this.giveChestplate(ctx, IntegerArgumentType.getInteger(ctx, "model")))
                        )
                  ))
               .then(
                  Commands.func_197057_a("leggings")
                     .then(
                        Commands.func_197056_a("model", IntegerArgumentType.integer(0, 11))
                           .executes(ctx -> this.giveLeggings(ctx, IntegerArgumentType.getInteger(ctx, "model")))
                     )
               ))
            .then(
               Commands.func_197057_a("boots")
                  .then(
                     Commands.func_197056_a("model", IntegerArgumentType.integer(0, 11))
                        .executes(ctx -> this.giveBoots(ctx, IntegerArgumentType.getInteger(ctx, "model")))
                  )
            ))
         .build();
   }

   private int giveHelmet(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
      ItemStack helmetStack = new ItemStack(ModItems.HELMET);
      this.configureGear(helmetStack, model);
      EntityHelper.giveItem(((CommandSource)context.getSource()).func_197035_h(), helmetStack);
      return 0;
   }

   private int giveChestplate(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
      ItemStack chestStack = new ItemStack(ModItems.CHESTPLATE);
      this.configureGear(chestStack, model);
      EntityHelper.giveItem(((CommandSource)context.getSource()).func_197035_h(), chestStack);
      return 0;
   }

   private int giveLeggings(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
      ItemStack leggingsStack = new ItemStack(ModItems.LEGGINGS);
      this.configureGear(leggingsStack, model);
      EntityHelper.giveItem(((CommandSource)context.getSource()).func_197035_h(), leggingsStack);
      return 0;
   }

   private int giveBoots(CommandContext<CommandSource> context, int model) throws CommandSyntaxException {
      ItemStack bootsStack = new ItemStack(ModItems.BOOTS);
      this.configureGear(bootsStack, model);
      EntityHelper.giveItem(((CommandSource)context.getSource()).func_197035_h(), bootsStack);
      return 0;
   }

   private void configureGear(ItemStack gearStack, int model) {
      ModAttributes.GEAR_STATE.create(gearStack, VaultGear.State.IDENTIFIED);
      gearStack.func_196082_o().func_82580_o("RollTicks");
      gearStack.func_196082_o().func_82580_o("LastModelHit");
      ModAttributes.GEAR_RARITY.create(gearStack, VaultGear.Rarity.OMEGA);
      ModAttributes.GEAR_SET.create(gearStack, VaultGear.Set.NONE);
      ModAttributes.GEAR_MODEL.create(gearStack, model);
      ModAttributes.GEAR_COLOR.create(gearStack, -1);
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
