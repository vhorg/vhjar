package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.DiscoveryGoalStatesData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModelDebugCommand extends Command {
   @Override
   public String getName() {
      return "model_debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("discover_all").executes(this::discoverAll));
      builder.then(Commands.literal("undiscover_all").executes(this::undiscoverAll));
      ModDynamicModels.Armor.MODEL_REGISTRY
         .forEach(
            (setId, armorModel) -> {
               builder.then(Commands.literal("all_armor_pieces").then(Commands.literal(setId.toString()).executes(ctx -> this.giveAllPieces(ctx, armorModel))));
               armorModel.getPiece(EquipmentSlot.HEAD)
                  .ifPresent(
                     piece -> builder.then(
                        Commands.literal("helmet")
                           .then(Commands.literal(piece.getId().toString()).executes(ctx -> this.givePiece(ctx, ModItems.HELMET, piece.getId())))
                     )
                  );
               armorModel.getPiece(EquipmentSlot.CHEST)
                  .ifPresent(
                     piece -> builder.then(
                        Commands.literal("chestplate")
                           .then(Commands.literal(piece.getId().toString()).executes(ctx -> this.givePiece(ctx, ModItems.CHESTPLATE, piece.getId())))
                     )
                  );
               armorModel.getPiece(EquipmentSlot.LEGS)
                  .ifPresent(
                     piece -> builder.then(
                        Commands.literal("leggings")
                           .then(Commands.literal(piece.getId().toString()).executes(ctx -> this.givePiece(ctx, ModItems.LEGGINGS, piece.getId())))
                     )
                  );
               armorModel.getPiece(EquipmentSlot.FEET)
                  .ifPresent(
                     piece -> builder.then(
                        Commands.literal("boots")
                           .then(Commands.literal(piece.getId().toString()).executes(ctx -> this.givePiece(ctx, ModItems.BOOTS, piece.getId())))
                     )
                  );
            }
         );
   }

   private int discoverAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      DiscoveredModelsData modelsData = DiscoveredModelsData.get(player.server);
      ModDynamicModels.REGISTRIES
         .getUniqueRegistries()
         .forEach(registry -> registry.forEach((modelId, model) -> modelsData.discoverModel(player.getUUID(), modelId)));
      return 0;
   }

   private int undiscoverAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      DiscoveredModelsData modelsData = DiscoveredModelsData.get(player.server);
      modelsData.reset(player.getUUID());
      modelsData.setDirty();
      DiscoveryGoalStatesData goalStatesData = DiscoveryGoalStatesData.get(player.getLevel());
      goalStatesData.getState(player).deleteCompletions();
      goalStatesData.setDirty();
      return 0;
   }

   private int giveAllPieces(CommandContext<CommandSourceStack> context, ArmorModel armorModel) throws CommandSyntaxException {
      armorModel.getPiece(EquipmentSlot.HEAD).ifPresent(armorModelPiece -> this.givePiece(context, ModItems.HELMET, armorModelPiece.getId()));
      armorModel.getPiece(EquipmentSlot.CHEST).ifPresent(armorModelPiece -> this.givePiece(context, ModItems.CHESTPLATE, armorModelPiece.getId()));
      armorModel.getPiece(EquipmentSlot.LEGS).ifPresent(armorModelPiece -> this.givePiece(context, ModItems.LEGGINGS, armorModelPiece.getId()));
      armorModel.getPiece(EquipmentSlot.FEET).ifPresent(armorModelPiece -> this.givePiece(context, ModItems.BOOTS, armorModelPiece.getId()));
      return 0;
   }

   private int givePiece(CommandContext<CommandSourceStack> context, Item gear, ResourceLocation modelId) {
      try {
         ItemStack helmetStack = new ItemStack(gear);
         if (FMLEnvironment.production && !ModConfigs.GEAR_MODEL_ROLL_RARITIES.canAppearNormally(helmetStack, modelId)) {
            return 0;
         } else {
            this.configureGear(helmetStack, modelId, VaultGearRarity.COMMON);
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            EntityHelper.giveItem(player, helmetStack);
            return 0;
         }
      } catch (CommandSyntaxException var6) {
         return 0;
      }
   }

   private void configureGear(ItemStack gearStack, ResourceLocation modelId, VaultGearRarity rarity) {
      VaultGearData gearData = VaultGearData.read(gearStack);
      gearData.setState(VaultGearState.IDENTIFIED);
      gearData.setRarity(rarity);
      gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_MODEL, modelId);
      gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_COLOR, Integer.valueOf(-1));
      gearData.write(gearStack);
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
