package iskallia.vault.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.VaultRoomNames;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.modifier.VaultModifier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.command.EnumArgument;

public class CrystalCommand extends Command {
   @Override
   public String getName() {
      return "crystal";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(
         Commands.func_197057_a("preventRandomModifiers").then(Commands.func_197056_a("random", BoolArgumentType.bool()).executes(this::setRollsRandom))
      );
      builder.then(Commands.func_197057_a("setModifiable").then(Commands.func_197056_a("modifiable", BoolArgumentType.bool()).executes(this::setModifiable)));
      builder.then(Commands.func_197057_a("addModifier").then(Commands.func_197056_a("modifier", StringArgumentType.string()).executes(this::addModifier)));
      builder.then(Commands.func_197057_a("addRoom").then(Commands.func_197056_a("roomKey", StringArgumentType.string()).executes(this::addRoom)));
      builder.then(
         Commands.func_197057_a("objectiveCount").then(Commands.func_197056_a("count", IntegerArgumentType.integer(1)).executes(this::setObjectiveCount))
      );
      builder.then(
         Commands.func_197057_a("objective").then(Commands.func_197056_a("crystalObjective", StringArgumentType.string()).executes(this::setObjective))
      );
      builder.then(Commands.func_197057_a("clearObjective").executes(this::clearObjective));
      builder.then(
         Commands.func_197057_a("type").then(Commands.func_197056_a("crystalType", EnumArgument.enumArgument(CrystalData.Type.class)).executes(this::setType))
      );
   }

   private int setRollsRandom(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean randomModifiers = BoolArgumentType.getBool(ctx, "random");
      data.setPreventsRandomModifiers(randomModifiers);
      return 0;
   }

   private int setModifiable(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      boolean modifiable = BoolArgumentType.getBool(ctx, "modifiable");
      data.setModifiable(modifiable);
      return 0;
   }

   private int addRoom(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      String roomKey = StringArgumentType.getString(ctx, "roomKey");
      if (VaultRoomNames.getName(roomKey) == null) {
         ((CommandSource)ctx.getSource()).func_197035_h().func_145747_a(new StringTextComponent("Unknown Room: " + roomKey), Util.field_240973_b_);
         return 0;
      } else {
         data.addGuaranteedRoom(roomKey);
         return 0;
      }
   }

   private int addModifier(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      String modifierStr = StringArgumentType.getString(ctx, "modifier");
      VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(modifierStr);
      if (modifier == null) {
         ((CommandSource)ctx.getSource()).func_197035_h().func_145747_a(new StringTextComponent("Unknown Modifier: " + modifierStr), Util.field_240973_b_);
         return 0;
      } else {
         data.addModifier(modifierStr);
         return 0;
      }
   }

   private int setObjectiveCount(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      int count = IntegerArgumentType.getInteger(ctx, "count");
      data.setTargetObjectiveCount(count);
      return 0;
   }

   private int clearObjective(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      data.setSelectedObjective(null);
      return 0;
   }

   private int setObjective(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      String objectiveStr = StringArgumentType.getString(ctx, "crystalObjective");
      VaultRaid.ARCHITECT_EVENT.get();
      VaultObjective objective = VaultObjective.getObjective(new ResourceLocation(objectiveStr));
      if (objective == null) {
         ((CommandSource)ctx.getSource()).func_197035_h().func_145747_a(new StringTextComponent("Unknown Objective: " + objectiveStr), Util.field_240973_b_);
         return 0;
      } else {
         data.setSelectedObjective(objective.getId());
         return 0;
      }
   }

   private int setType(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ItemStack crystal = this.getCrystal(ctx);
      CrystalData data = VaultCrystalItem.getData(crystal);
      CrystalData.Type type = (CrystalData.Type)ctx.getArgument("crystalType", CrystalData.Type.class);
      if (type == CrystalData.Type.RAFFLE) {
         data.setPlayerBossName(((CommandSource)ctx.getSource()).func_197035_h().func_200200_C_().getString());
      } else {
         if (data.getPlayerBossName() != null) {
            data.setPlayerBossName("");
         }

         data.setType(type);
      }

      return 0;
   }

   private ItemStack getCrystal(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      ItemStack held = player.func_184586_b(Hand.MAIN_HAND);
      if (!held.func_190926_b() && held.func_77973_b() instanceof VaultCrystalItem) {
         return held;
      } else {
         player.func_145747_a(new StringTextComponent("Not holding crystal!"), Util.field_240973_b_);
         throw new RuntimeException();
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
