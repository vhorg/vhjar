package iskallia.vault.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.ints.IntIterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ToolItem extends TieredItem implements VaultGearItem, Vanishable, IManualModelLoading {
   public static final ResourceLocation SPAWNER_ID = new ResourceLocation("ispawner", "spawner");

   public ToolItem(ResourceLocation id, Properties properties) {
      super(Tiers.NETHERITE, properties);
      this.setRegistryName(id);
   }

   @Override
   public boolean shouldPreventAnvilCombination(ItemStack other) {
      return !other.is(ModItems.JEWEL);
   }

   public boolean isDamageable(ItemStack stack) {
      return true;
   }

   public int getMaxDamage(ItemStack stack) {
      return VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum());
   }

   public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack material) {
      return false;
   }

   @NotNull
   public Component getName(@Nonnull ItemStack stack) {
      ToolType type = ToolType.of(stack);
      ToolMaterial material = getMaterial(stack);
      return (Component)(material == null
         ? new TextComponent("")
         : new TranslatableComponent(material.getDescription())
            .append(" ")
            .append((Component)(type == null ? new TextComponent("") : new TranslatableComponent(type.getDescription()))));
   }

   public static ToolMaterial getMaterial(@NotNull ItemStack stack) {
      return VaultGearData.read(stack).get(ModGearAttributes.TOOL_MATERIAL, VaultGearAttributeTypeMerger.of(() -> null, (a, b) -> b));
   }

   public static boolean canUse(ItemStack stack, Entity entity) {
      if (entity instanceof Player player) {
         int playerLevel = SidedHelper.getVaultLevel(player);
         return playerLevel >= VaultGearData.read(stack).getItemLevel();
      } else {
         return true;
      }
   }

   @SubscribeEvent
   public static void onBlockClick(LeftClickBlock event) {
      if (event.getItemStack().getItem() == ModItems.TOOL && !canUse(event.getItemStack(), event.getEntity())) {
         event.setCanceled(true);
      }
   }

   public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
      return this.hasAffinity(stack, state) ? VaultGearData.read(stack).get(ModGearAttributes.MINING_SPEED, VaultGearAttributeTypeMerger.floatSum()) : 1.0F;
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return this.hasAffinity(stack, state);
   }

   public boolean hasAffinity(@Nonnull ItemStack stack, @Nonnull BlockState state) {
      VaultGearData data = VaultGearData.read(stack);
      if (data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue()) && state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
         return true;
      } else if (data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue()) && state.is(BlockTags.MINEABLE_WITH_AXE)) {
         return true;
      } else if (data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue()) && state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
         return true;
      } else if (!data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())
         || !state.is(BlockTags.MINEABLE_WITH_HOE)
            && !(Items.NETHERITE_SWORD.getDestroySpeed(stack, state) > 1.0F)
            && !Items.NETHERITE_SWORD.isCorrectToolForDrops(stack, state)
            && !(Items.SHEARS.getDestroySpeed(stack, state) > 1.0F)
            && !Items.SHEARS.isCorrectToolForDrops(stack, state)) {
         if (data.get(ModGearAttributes.WOODEN_AFFINITY, VaultGearAttributeTypeMerger.anyTrue()) && state.is(ModBlocks.WOODEN_CHEST)) {
            return true;
         } else if (data.get(ModGearAttributes.ORNATE_AFFINITY, VaultGearAttributeTypeMerger.anyTrue()) && state.is(ModBlocks.ORNATE_CHEST)) {
            return true;
         } else if (data.get(ModGearAttributes.GILDED_AFFINITY, VaultGearAttributeTypeMerger.anyTrue()) && state.is(ModBlocks.GILDED_CHEST)) {
            return true;
         } else if (data.get(ModGearAttributes.LIVING_AFFINITY, VaultGearAttributeTypeMerger.anyTrue()) && state.is(ModBlocks.LIVING_CHEST)) {
            return true;
         } else {
            return data.get(ModGearAttributes.COIN_AFFINITY, VaultGearAttributeTypeMerger.anyTrue()) && state.is(ModBlocks.COIN_PILE)
               ? true
               : SPAWNER_ID.equals(state.getBlock().getRegistryName());
         }
      } else {
         return true;
      }
   }

   @NotNull
   public InteractionResult useOn(@Nonnull UseOnContext context) {
      InteractionResult result = InteractionResult.PASS;
      if (canUse(context.getItemInHand(), context.getPlayer())) {
         VaultGearData data = VaultGearData.read(context.getItemInHand());
         if (data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_PICKAXE.useOn(context), result);
         }

         if (data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_AXE.useOn(context), result);
         }

         if (data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_SHOVEL.useOn(context), result);
         }

         if (data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_HOE.useOn(context), result);
            result = this.getResultFit(Items.NETHERITE_SWORD.useOn(context), result);
            result = this.getResultFit(Items.SHEARS.useOn(context), result);
         }
      }

      return result;
   }

   @NotNull
   public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
      InteractionResult result = InteractionResult.PASS;
      if (canUse(stack, target)) {
         VaultGearData data = VaultGearData.read(stack);
         if (data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_PICKAXE.interactLivingEntity(stack, player, target, hand), result);
         }

         if (data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_AXE.interactLivingEntity(stack, player, target, hand), result);
         }

         if (data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_SHOVEL.interactLivingEntity(stack, player, target, hand), result);
         }

         if (data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
            result = this.getResultFit(Items.NETHERITE_HOE.interactLivingEntity(stack, player, target, hand), result);
            result = this.getResultFit(Items.SHEARS.interactLivingEntity(stack, player, target, hand), result);
            result = this.getResultFit(Items.NETHERITE_SWORD.interactLivingEntity(stack, player, target, hand), result);
         }
      }

      return result;
   }

   public InteractionResult getResultFit(InteractionResult... results) {
      InteractionResult min = InteractionResult.FAIL;

      for (InteractionResult result : results) {
         if (result.ordinal() < min.ordinal()) {
            min = result;
         }
      }

      return min;
   }

   public boolean canPerformAction(ItemStack stack, ToolAction action) {
      VaultGearData data = VaultGearData.read(stack);
      if (data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue()) && ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(action)) {
         return true;
      } else if (data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue()) && ToolActions.DEFAULT_AXE_ACTIONS.contains(action)) {
         return true;
      } else {
         return data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue()) && ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(action)
            ? true
            : data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())
               && (
                  ToolActions.DEFAULT_HOE_ACTIONS.contains(action)
                     || ToolActions.DEFAULT_SHEARS_ACTIONS.contains(action)
                     || ToolActions.DEFAULT_SWORD_ACTIONS.contains(action)
               );
      }
   }

   public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity owner) {
      if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
         double damage = 1.0;
         if (owner instanceof ServerPlayer player && ((IHammer)player.gameMode).getHammer().contains(pos)) {
            damage = 0.0;
         } else if (this.hasAffinity(stack, state)
            && state.getBlock() instanceof VaultChestBlock block
            && block.hasStepBreaking()
            && world.getBlockEntity(pos) instanceof VaultChestTileEntity chest) {
            if (block == ModBlocks.WOODEN_CHEST) {
               damage = 3.0;
            } else if (block == ModBlocks.GILDED_CHEST || block == ModBlocks.LIVING_CHEST || block == ModBlocks.ORNATE_CHEST) {
               damage = 0.25;
            }
         } else if (SPAWNER_ID.equals(state.getBlock().getRegistryName())) {
            damage = 10.0;
         }

         this.hurt(stack, world, owner, damage);
         return true;
      } else {
         return true;
      }
   }

   public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
      this.hurt(stack, target.level, attacker, 1.0);
      return true;
   }

   public boolean hurt(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull LivingEntity owner, double damage) {
      int result = (int)damage + (world.getRandom().nextFloat() < damage - (int)damage ? 1 : 0);
      if (result <= 0) {
         return false;
      } else {
         stack.hurtAndBreak(result, owner, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
         return true;
      }
   }

   public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
      Level world = entity.level;
      VaultGearData data = VaultGearData.read(stack);
      return ServerVaults.get(world).isEmpty()
            && world.getRandom().nextFloat() < data.get(ModGearAttributes.IMMMORTALITY, VaultGearAttributeTypeMerger.floatSum())
         ? 0
         : amount;
   }

   @Override
   public boolean isImmuneToDamage(ItemStack stack, @Nullable Player player) {
      return false;
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
      return (Multimap<Attribute, AttributeModifier>)(slot == EquipmentSlot.MAINHAND
         ? VaultGearHelper.getModifiers(VaultGearData.read(stack))
         : ImmutableMultimap.of());
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      if (enchantment instanceof MendingEnchantment) {
         return false;
      } else {
         return enchantment.category == EnchantmentCategory.DIGGER ? true : enchantment.category.canEnchant(stack.getItem());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      tooltip.addAll(this.createTooltip(stack, GearTooltip.toolTooltip()));
   }

   @Override
   public void addTooltipRarity(VaultGearData data, ItemStack stack, List<Component> tooltip, VaultGearState state) {
   }

   public void fillItemCategory(@Nonnull CreativeModeTab category, @Nonnull NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         for (ToolMaterial material : ToolMaterial.values()) {
            for (ToolType type : ToolType.values()) {
               items.add(create(material, type));
            }
         }
      }
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return ToolItemRenderer.INSTANCE;
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      for (ToolType type : ToolType.values()) {
         consumer.accept(new ModelResourceLocation("the_vault:tool/%s/handle#inventory".formatted(type.getId())));

         for (ToolMaterial material : ToolMaterial.values()) {
            consumer.accept(new ModelResourceLocation("the_vault:tool/%s/head/%s#inventory".formatted(type.getId(), material.getId())));
         }
      }
   }

   public static ItemStack create(ToolMaterial material, ToolType type) {
      ItemStack stack = new ItemStack(ModItems.TOOL);
      VaultGearData data = VaultGearData.read(stack);
      data.setState(VaultGearState.IDENTIFIED);
      data.setItemLevel(material.getLevel());
      data.setRepairSlots(material.getRepairs());
      data.updateAttribute(ModGearAttributes.TOOL_CAPACITY, Integer.valueOf(material.getCapacity()));
      data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.MINING_SPEED, material.getMiningSpeed()));
      data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.DURABILITY, material.getDurability()));
      if (type.has(ToolType.HAMMER)) {
         data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.HAMMER_SIZE, 1));
      }

      data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.TOOL_MATERIAL, material));

      for (VaultGearAttribute<Boolean> attribute : type.getAttributes()) {
         data.addModifier(VaultGearModifier.AffixType.PREFIX, new VaultGearModifier<>(attribute, true));
      }

      data.write(stack);
      stack.getOrCreateTag().putLong("offset", 0L);
      return stack;
   }

   public static void offset(ItemStack stack, int right, int up) {
      VaultGearData data = VaultGearData.read(stack);
      int size = data.get(ModGearAttributes.HAMMER_SIZE, VaultGearAttributeTypeMerger.intSum());
      long offset = stack.getOrCreateTag().getLong("offset");
      int offsetRight = Mth.clamp((int)(offset >>> 32) + right, -size, size);
      int offsetUp = Mth.clamp((int)offset + up, -size, size);
      stack.getOrCreateTag().putLong("offset", (long)offsetRight << 32 | Integer.toUnsignedLong(offsetUp));
   }

   public static Iterator<BlockPos> getHammerPositions(ItemStack stack, BlockPos origin, Direction face, Entity entity) {
      if (!canUse(stack, entity)) {
         return Collections.emptyIterator();
      } else {
         VaultGearData data = VaultGearData.read(stack);
         int size = data.get(ModGearAttributes.HAMMER_SIZE, VaultGearAttributeTypeMerger.intSum());
         long offset = stack.getOrCreateTag().getLong("offset");
         int offsetRight = (int)(offset >>> 32);
         int offsetUp = (int)offset;

         Direction right = switch (face) {
            case DOWN, UP -> entity.getDirection().getClockWise();
            case NORTH, SOUTH, WEST, EAST -> face.getCounterClockWise();
            default -> throw new IncompatibleClassChangeError();
         };

         Direction up;
         Direction var12 = up = switch (face) {
            case DOWN -> entity.getDirection().getOpposite();
            case UP -> entity.getDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
            default -> throw new IncompatibleClassChangeError();
         };
         return Iterators.concat(
            new MappingIterator<>(
               IntIterators.fromTo(-size, size + 1),
               r -> new MappingIterator<>(IntIterators.fromTo(-size, size + 1), u -> origin.relative(right, r + offsetRight).relative(up, u + offsetUp))
            )
         );
      }
   }

   public static boolean applyJewel(ItemStack tool, ItemStack jewel) {
      VaultGearData toolData = VaultGearData.read(tool);
      VaultGearData jewelData = VaultGearData.read(jewel);
      int capacity = toolData.getFirstValue(ModGearAttributes.TOOL_CAPACITY).orElse(0);
      int size = jewelData.getFirstValue(ModGearAttributes.JEWEL_SIZE).orElse(0);
      if (capacity - size < 0) {
         return false;
      } else {
         toolData.updateAttribute(ModGearAttributes.TOOL_CAPACITY, Integer.valueOf(capacity - size));

         for (VaultGearModifier.AffixType affix : VaultGearModifier.AffixType.explicits()) {
            for (VaultGearModifier<?> jewelModifier : jewelData.getModifiers(affix)) {
               if (jewelModifier.getAttribute() != ModGearAttributes.HAMMER_SIZE
                  || toolData.get(ModGearAttributes.HAMMERING, VaultGearAttributeTypeMerger.anyTrue())) {
                  mergeModifier(affix, toolData, jewelModifier);
               }
            }
         }

         for (VaultGearAttributeInstance<Integer> hammerSizeModifier : toolData.getModifiers(ModGearAttributes.HAMMER_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
            hammerSizeModifier.setValue(Math.min(hammerSizeModifier.getValue(), 7));
         }

         toolData.setItemLevel(Math.max(toolData.getItemLevel(), jewelData.getItemLevel()));
         toolData.write(tool);
         return true;
      }
   }

   private static <T> void mergeModifier(VaultGearModifier.AffixType affix, VaultGearData targetData, VaultGearModifier<T> toAdd) {
      List<VaultGearAttributeInstance<T>> matching = targetData.getModifiers(toAdd.getAttribute(), VaultGearData.Type.EXPLICIT_MODIFIERS);
      if (matching.isEmpty()) {
         targetData.addModifier(affix, new VaultGearModifier<>(toAdd.getAttribute(), toAdd.getValue()));
      } else {
         matching.stream().findFirst().ifPresent(current -> current.setValue(merge((VaultGearAttributeInstance<T>)current, toAdd.getValue())));
      }
   }

   private static <T> T merge(VaultGearAttributeInstance<T> attributeInstance, T toAdd) {
      VaultGearAttribute<T> attribute = attributeInstance.getAttribute();
      if (attribute.getAttributeComparator() != null) {
         return attribute.getAttributeComparator().merge(attributeInstance.getValue(), toAdd);
      } else {
         VaultMod.LOGGER.error("Unsupported merging on attribute " + attribute.getRegistryName(), new UnsupportedOperationException());
         return attributeInstance.getValue();
      }
   }

   @Nonnull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.TOOL;
   }

   @Nonnull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.UNKNOWN;
   }

   @Nullable
   @Override
   public EquipmentSlot getIntendedSlot(ItemStack stack) {
      return EquipmentSlot.MAINHAND;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return null;
   }

   public static boolean handleLoot(LootContext context, List<ItemStack> loot) {
      ItemStack stack = (ItemStack)context.getParamOrNull(LootContextParams.TOOL);
      Entity entity = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
      return handleLoot(context.getLevel(), stack, entity, loot);
   }

   public static boolean handleLoot(Builder context, List<ItemStack> loot) {
      ItemStack stack = (ItemStack)context.getOptionalParameter(LootContextParams.TOOL);
      Entity entity = (Entity)context.getOptionalParameter(LootContextParams.THIS_ENTITY);
      return handleLoot(context.getLevel(), stack, entity, loot);
   }

   public static boolean handleLoot(ServerLevel world, ItemStack stack, Entity entity, List<ItemStack> loot) {
      if (stack != null && stack.getItem() == ModItems.TOOL) {
         VaultGearData data = VaultGearData.read(stack);
         if (entity != null && entity.isShiftKeyDown()) {
            if (data.get(ModGearAttributes.SMELTING, VaultGearAttributeTypeMerger.anyTrue())) {
               handleSmelting(world, loot);
            }

            if (data.get(ModGearAttributes.PULVERIZING, VaultGearAttributeTypeMerger.anyTrue())) {
               handlePulverizing(loot);
            }
         } else {
            if (data.get(ModGearAttributes.PULVERIZING, VaultGearAttributeTypeMerger.anyTrue())) {
               handlePulverizing(loot);
            }

            if (data.get(ModGearAttributes.SMELTING, VaultGearAttributeTypeMerger.anyTrue())) {
               handleSmelting(world, loot);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void handleSmelting(ServerLevel world, List<ItemStack> loot) {
      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         Optional<SmeltingRecipe> opt = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{raw}), world.getLevel());
         if (opt.isPresent()) {
            ItemStack smelted = opt.get().getResultItem().copy();
            smelted.setCount(raw.getCount() * smelted.getCount());
            loot.set(i, smelted);
         }
      }
   }

   public static void handlePulverizing(List<ItemStack> loot) {
      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         LootTable table = ModConfigs.TOOL_PULVERIZING.get(raw.getItem());
         if (table != null) {
            loot.remove(i);
            LootTableGenerator generator = new LootTableGenerator(Version.latest(), table, 0.0F);
            generator.generate(JavaRandom.ofNanoTime());
            generator.getItems().forEachRemaining(pulverized -> {
               pulverized.setCount(raw.getCount() * pulverized.getCount());
               loot.add(pulverized);
            });
         }
      }
   }
}
