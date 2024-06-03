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
import iskallia.vault.item.crystal.objective.ParadoxCrystalObjective;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.properties.InstabilityCrystalProperties;
import iskallia.vault.world.data.ParadoxCrystalData;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
      builder.then(
         Commands.literal("setParadoxCooldown").then(Commands.argument("timeoutSeconds", IntegerArgumentType.integer(0)).executes(this::setParadoxCooldown))
      );
      builder.then(Commands.literal("setVolume").then(Commands.argument("volume", IntegerArgumentType.integer(0)).executes(this::setVolume)));
   }

   private int setParadoxCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(context);
      CrystalData data = CrystalData.read(crystal);
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      if (!(data.getObjective() instanceof ParadoxCrystalObjective)) {
         player.sendMessage(new TextComponent("Only works on divine paradox crystals"), Util.NIL_UUID);
         return 0;
      } else {
         ParadoxCrystalData.Entry entry = ParadoxCrystalData.get(player.getServer()).getOrCreate(player.getUUID());
         entry.unlockTime = ZonedDateTime.now()
            .plusSeconds(IntegerArgumentType.getInteger(context, "timeoutSeconds"))
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli();
         entry.changed = true;
         return 0;
      }
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(context);
      CrystalData data = CrystalData.read(crystal);
      int level = IntegerArgumentType.getInteger(context, "level");
      data.getProperties().setLevel(level);
      data.write(crystal);
      return 0;
   }

   private int setVolume(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(context);
      CrystalData data = CrystalData.read(crystal);
      int volume = IntegerArgumentType.getInteger(context, "volume");
      if (data.getProperties() instanceof CapacityCrystalProperties capacityCrystalProperties) {
         capacityCrystalProperties.setVolume(volume);
      }

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
      data.getProperties().setUnmodifiable(BoolArgumentType.getBool(ctx, "unmodifiable"));
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
      if (data.getProperties() instanceof InstabilityCrystalProperties properties) {
         properties.setInstability(instability);
      }

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
