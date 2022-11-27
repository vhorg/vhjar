package iskallia.vault.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultRoomNames;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
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
import net.minecraftforge.server.command.EnumArgument;

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
      builder.then(Commands.literal("preventRandomModifiers").then(Commands.argument("random", BoolArgumentType.bool()).executes(this::setRollsRandom)));
      builder.then(Commands.literal("canTriggerInfluences").then(Commands.argument("trigger", BoolArgumentType.bool()).executes(this::setCanTriggerInfluences)));
      builder.then(
         Commands.literal("canGenerateTreasureRooms").then(Commands.argument("generate", BoolArgumentType.bool()).executes(this::canGenerateTreasureRooms))
      );
      builder.then(Commands.literal("setModifiable").then(Commands.argument("modifiable", BoolArgumentType.bool()).executes(this::setModifiable)));
      builder.then(
         Commands.literal("addModifier")
            .then(
               Commands.argument("modifier", ResourceLocationArgument.id())
                  .suggests(SUGGEST_MODIFIER)
                  .then(Commands.argument("stackSize", IntegerArgumentType.integer(1, 100)).executes(this::addModifier))
            )
      );
      builder.then(
         Commands.literal("addRoom")
            .then(
               Commands.argument("roomKey", StringArgumentType.string())
                  .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100)).executes(this::addRoom))
            )
      );
      builder.then(Commands.literal("objectiveCount").then(Commands.argument("count", IntegerArgumentType.integer(1)).executes(this::setObjectiveCount)));
      builder.then(Commands.literal("objective").then(Commands.argument("crystalObjective", StringArgumentType.string()).executes(this::setObjective)));
      builder.then(Commands.literal("clearObjective").executes(this::clearObjective));
      builder.then(Commands.literal("type").then(Commands.argument("crystalType", EnumArgument.enumArgument(CrystalData.Type.class)).executes(this::setType)));
      builder.then(
         Commands.literal("setInstabilityCounter").then(Commands.argument("instabilityCounter", IntegerArgumentType.integer(0)).executes(this::setInstability))
      );
      builder.then(Commands.literal("setLevel").then(Commands.argument("level", IntegerArgumentType.integer(0)).executes(this::setLevel)));
   }

   private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(context);
      int level = IntegerArgumentType.getInteger(context, "level");
      VaultCrystalItem.getData(crystal).setLevel(level);
      return 0;
   }

   private int canGenerateTreasureRooms(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean generateTreasureRooms = BoolArgumentType.getBool(ctx, "generate");
      data.setCanGenerateTreasureRooms(generateTreasureRooms);
      return 0;
   }

   private int setCanTriggerInfluences(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean triggerInfluences = BoolArgumentType.getBool(ctx, "trigger");
      data.setCanTriggerInfluences(triggerInfluences);
      return 0;
   }

   private int setRollsRandom(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean randomModifiers = BoolArgumentType.getBool(ctx, "random");
      data.setPreventsRandomModifiers(randomModifiers);
      return 0;
   }

   private int setModifiable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean modifiable = BoolArgumentType.getBool(ctx, "modifiable");
      data.setModifiable(modifiable);
      return 0;
   }

   private int addRoom(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      String roomKey = StringArgumentType.getString(ctx, "roomKey");
      if (VaultRoomNames.getName(roomKey) == null) {
         ((CommandSourceStack)ctx.getSource()).getPlayerOrException().sendMessage(new TextComponent("Unknown Room: " + roomKey), Util.NIL_UUID);
         return 0;
      } else {
         int amount = IntegerArgumentType.getInteger(ctx, "amount");

         for (int i = 0; i < amount; i++) {
            data.addGuaranteedRoom(roomKey);
         }

         return 0;
      }
   }

   private int addModifier(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      ResourceLocation id = ResourceLocationArgument.getId(ctx, "modifier");
      VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(id, null);
      if (modifier == null) {
         ((CommandSourceStack)ctx.getSource()).getPlayerOrException().sendMessage(new TextComponent("Unknown Modifier: " + id), Util.NIL_UUID);
         return 0;
      } else {
         int stackSize = IntegerArgumentType.getInteger(ctx, "stackSize");
         data.addModifier(VaultModifierStack.of(modifier, stackSize));
         return 0;
      }
   }

   private int setObjectiveCount(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      int count = IntegerArgumentType.getInteger(ctx, "count");
      return 0;
   }

   private int clearObjective(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      return 0;
   }

   private int setObjective(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      String objectiveStr = StringArgumentType.getString(ctx, "crystalObjective");
      VaultRaid.ARCHITECT_EVENT.get();
      VaultObjective objective = VaultObjective.getObjective(new ResourceLocation(objectiveStr));
      if (objective == null) {
         ((CommandSourceStack)ctx.getSource()).getPlayerOrException().sendMessage(new TextComponent("Unknown Objective: " + objectiveStr), Util.NIL_UUID);
         return 0;
      } else {
         return 0;
      }
   }

   private int setType(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      CrystalData.Type type = (CrystalData.Type)ctx.getArgument("crystalType", CrystalData.Type.class);
      if (type != CrystalData.Type.RAFFLE) {
         data.setType(type);
      }

      return 0;
   }

   private int setInstability(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      int instabilityCounter = IntegerArgumentType.getInteger(ctx, "instabilityCounter");
      data.setInstabilityCounter(instabilityCounter);
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
