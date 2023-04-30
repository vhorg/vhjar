package iskallia.vault.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.objective.NullCrystalObjective;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class CrystalCommand extends Command {
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_MODIFIER = (context, builder) -> SharedSuggestionProvider.suggestResource(
      VaultModifierRegistry.getAll().map(VaultModifier::getId).collect(Collectors.toList()), builder
   );

   @Override
   public String getName() {
      return "crystal";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("setRandomModifiers").then(Commands.argument("random", BoolArgumentType.bool()).executes(this::setRandomModifiers)));
      builder.then(Commands.literal("setExhausted").then(Commands.argument("unmodifiable", BoolArgumentType.bool()).executes(this::setUnmodifiable)));
      builder.then(Commands.literal("setUnmodifiable").then(Commands.argument("unmodifiable", BoolArgumentType.bool()).executes(this::setUnmodifiable)));
      builder.then(
         Commands.literal("addModifier")
            .then(
               Commands.argument("modifier", ResourceLocationArgument.id())
                  .suggests(SUGGEST_MODIFIER)
                  .then(Commands.argument("stackSize", IntegerArgumentType.integer(1, 100)).executes(this::addModifier))
            )
      );
      builder.then(Commands.literal("clearObjective").executes(this::clearObjective));
      builder.then(Commands.literal("setInstability").then(Commands.argument("instability", FloatArgumentType.floatArg(0.0F)).executes(this::setInstability)));
      builder.then(Commands.literal("setLevel").then(Commands.argument("level", IntegerArgumentType.integer(0)).executes(this::setLevel)));
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(context);
      CrystalData data = CrystalData.read(crystal);
      int level = IntegerArgumentType.getInteger(context, "level");
      data.setLevel(level);
      data.write(crystal);
      return 0;
   }

   private int setRandomModifiers(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = CrystalData.read(crystal);
      boolean randomModifiers = BoolArgumentType.getBool(ctx, "random");
      data.getModifiers().setRandomModifiers(randomModifiers);
      data.write(crystal);
      return 0;
   }

   private int setUnmodifiable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = CrystalData.read(crystal);
      data.setUnmodifiable(BoolArgumentType.getBool(ctx, "unmodifiable"));
      data.write(crystal);
      return 0;
   }

   private int addModifier(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = CrystalData.read(crystal);
      ResourceLocation id = ResourceLocationArgument.getId(ctx, "modifier");
      VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(id, null);
      if (modifier == null) {
         ((CommandSourceStack)ctx.getSource()).getPlayerOrException().sendMessage(new TextComponent("Unknown Modifier: " + id), Util.NIL_UUID);
         return 0;
      } else {
         int stackSize = IntegerArgumentType.getInteger(ctx, "stackSize");
         data.getModifiers().add(VaultModifierStack.of(modifier, stackSize));
         data.write(crystal);
         return 0;
      }
   }

   private int clearObjective(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = CrystalData.read(crystal);
      data.setObjective(NullCrystalObjective.INSTANCE);
      data.write(crystal);
      return 0;
   }

   private int setInstability(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = CrystalData.read(crystal);
      float instability = FloatArgumentType.getFloat(ctx, "instability");
      data.setInstability(instability);
      data.write(crystal);
      return 0;
   }

   private ItemStack getCrystal(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (!held.isEmpty() && held.getItem() instanceof VaultCrystalItem) {
         return held;
      } else {
         player.sendMessage(new TextComponent("Not holding crystal!"), Util.NIL_UUID);
         throw new RuntimeException();
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
