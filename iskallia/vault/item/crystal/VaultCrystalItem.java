package iskallia.vault.item.crystal;

import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.ParadoxObjective;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import iskallia.vault.item.crystal.layout.HeraldCrystalLayout;
import iskallia.vault.item.crystal.layout.ParadoxCrystalLayout;
import iskallia.vault.item.crystal.model.RawCrystalModel;
import iskallia.vault.item.crystal.modifiers.ParadoxCrystalModifiers;
import iskallia.vault.item.crystal.objective.EmptyCrystalObjective;
import iskallia.vault.item.crystal.objective.HeraldCrystalObjective;
import iskallia.vault.item.crystal.objective.ParadoxCrystalObjective;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import iskallia.vault.item.tool.IManualModelLoading;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

public class VaultCrystalItem extends Item implements IManualModelLoading {
   public static final String KEY_SCHEDULED_TASKS = "scheduledTasks";
   private static final Map<String, VaultCrystalItem.IScheduledTaskDeserializer> SCHEDULED_TASK_DESERIALIZER_MAP = new HashMap<String, VaultCrystalItem.IScheduledTaskDeserializer>() {
      {
         this.put("exhaust", VaultCrystalItem.ExhaustTask::deserializeNBT);
         this.put("removeRandomCurse", VaultCrystalItem.RemoveRandomCurseTask::deserializeNBT);
         this.put("addCurses", VaultCrystalItem.AddModifiersTask::deserializeNBT);
         this.put("addClarity", VaultCrystalItem.AddClarityTask::deserializeNBT);
         this.put("echo", VaultCrystalItem.EchoTask::deserializeNBT);
         this.put("clone", VaultCrystalItem.CloneTask::deserializeNBT);
         this.put("removeRandomNegativeModifier", VaultCrystalItem.RemoveRandomNegativeModifierTask::deserializeNBT);
         this.put("addRandomCurse", VaultCrystalItem.NoOpTask::deserializeNBT);
         this.put("addRandomCurses", VaultCrystalItem.NoOpTask::deserializeNBT);
      }
   };
   public static final ResourceLocation NEGATIVE_MODIFIER_POOL_NAME = VaultMod.id("random_negative");

   public VaultCrystalItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   public void onWorldTick(Level world, ItemEntity entity) {
      ItemStack stack = entity.getItem().copy();
      CrystalData data = CrystalData.read(stack);
      data.onWorldTick(world, entity);
      data.write(stack);
      entity.setItem(stack);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         items.add(create(crystal -> crystal.setProperties(new CapacityCrystalProperties())));
         items.add(create(crystal -> {
            crystal.setLayout(new ClassicInfiniteCrystalLayout(1));
            crystal.setProperties(new CapacityCrystalProperties());
         }));
         items.add(create(crystal -> {
            crystal.setModel(new RawCrystalModel());
            crystal.setObjective(new EmptyCrystalObjective());
            crystal.setTheme(new PoolCrystalTheme(VaultMod.id("raw")));
            crystal.setProperties(new CapacityCrystalProperties());
         }));
         items.add(create(crystal -> {
            crystal.setObjective(new ParadoxCrystalObjective(ParadoxObjective.Type.BUILD));
            crystal.setLayout(new ParadoxCrystalLayout());
            crystal.setModifiers(new ParadoxCrystalModifiers());
            crystal.setProperties(new CapacityCrystalProperties().setVolume(0).setUnmodifiable(true));
         }));
         items.add(create(crystal -> {
            crystal.setObjective(new ParadoxCrystalObjective(ParadoxObjective.Type.RUN));
            crystal.setLayout(new ParadoxCrystalLayout());
            crystal.setModifiers(new ParadoxCrystalModifiers());
            crystal.setProperties(new CapacityCrystalProperties().setVolume(0).setUnmodifiable(true));
         }));
         items.add(create(crystal -> {
            crystal.setObjective(new HeraldCrystalObjective());
            crystal.setTheme(new ValueCrystalTheme(VaultMod.id("classic_vault_herald")));
            crystal.setLayout(new HeraldCrystalLayout());
            crystal.getModifiers().setRandomModifiers(false);
            crystal.setProperties(new CapacityCrystalProperties().setVolume(0).setLevel(Integer.valueOf(100)).setUnmodifiable(true));
         }));
      }
   }

   public static ItemStack create(Consumer<CrystalData> action) {
      return create(new ItemStack(ModItems.VAULT_CRYSTAL), action);
   }

   public static ItemStack create(ItemStack base, Consumer<CrystalData> action) {
      ItemStack stack = base.copy();
      CrystalData crystal = CrystalData.read(stack);
      action.accept(crystal);
      crystal.write(stack);
      return stack;
   }

   public CompoundTag getShareTag(ItemStack stack) {
      CompoundTag nbt = super.getShareTag(stack);
      if (nbt == null) {
         return null;
      } else {
         nbt = nbt.copy();
         nbt.remove("scheduledTasks");
         return nbt;
      }
   }

   @Nonnull
   public InteractionResult useOn(UseOnContext context) {
      if (!context.getLevel().isClientSide && context.getPlayer() != null) {
         CrystalData data = CrystalData.read(context.getItemInHand());
         BlockPos pos = context.getClickedPos();
         if (this.tryCreatePortal(context.getLevel(), pos, context.getClickedFace(), data) && data.onPlaced(context)) {
            context.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.VAULT_PORTAL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            MutableComponent playerName = context.getPlayer().getDisplayName().copy();
            playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
            String suffix = " opened a Vault!";
            TextComponent suffixComponent = new TextComponent(suffix);
            context.getLevel()
               .getServer()
               .getPlayerList()
               .broadcastMessage(new TextComponent("").append(playerName).append(suffixComponent), ChatType.CHAT, context.getPlayer().getUUID());
            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
         } else {
            return super.useOn(context);
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   private boolean tryCreatePortal(Level world, BlockPos pos, Direction facing, CrystalData data) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(world, pos.relative(facing), Axis.X, VaultPortalBlock.FRAME);
      if (optional.isPresent()) {
         VaultPortalSize portal = optional.get();
         BlockState state = (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, portal.getAxis());
         portal.placePortalBlocks(blockPos -> {
            world.setBlock(blockPos, state, 3);
            if (world.getBlockEntity(blockPos) instanceof VaultPortalTileEntity portalTE) {
               portalTE.setCrystalData(data.copy());
            }
         });
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   @ParametersAreNonnullByDefault
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      if (ModConfigs.isInitialized()) {
         CrystalData.run(stack, data -> data.addText(tooltip, tooltip.size(), flag, (float)ClientScheduler.INSTANCE.getTickCount()));
      }

      super.appendHoverText(stack, world, tooltip, flag);
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      consumer.accept(new ModelResourceLocation("the_vault:crystal/core/rainbow#inventory"));
      consumer.accept(new ModelResourceLocation("the_vault:crystal/core/raw#inventory"));
      consumer.accept(new ModelResourceLocation("the_vault:crystal/core/chaos#inventory"));
      consumer.accept(new ModelResourceLocation("the_vault:crystal/core/grayscale#inventory"));
      consumer.accept(new ModelResourceLocation("the_vault:crystal/augment/core#inventory"));
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return CrystalRenderer.INSTANCE;
         }
      });
   }

   @ParametersAreNonnullByDefault
   public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (entity instanceof ServerPlayer serverPlayer && this.hasScheduledTasks(itemStack)) {
         this.executeScheduledTasks(serverPlayer, itemStack);
         this.clearScheduledTasks(itemStack);
      }

      CrystalData.run(itemStack, data -> data.onInventoryTick(world, entity, itemSlot, isSelected));
      super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
   }

   public static void scheduleTask(VaultCrystalItem.IScheduledTask task, ItemStack itemStack) {
      if (!(task instanceof VaultCrystalItem.NoOpTask)) {
         CompoundTag nbt = itemStack.getOrCreateTag();
         CompoundTag scheduledTasks = nbt.getCompound("scheduledTasks");
         scheduledTasks.put(task.getId(), task.serializeNBT());
         nbt.put("scheduledTasks", scheduledTasks);
      }
   }

   private boolean hasScheduledTasks(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getOrCreateTag();
      return nbt.contains("scheduledTasks") && nbt.getCompound("scheduledTasks").size() > 0;
   }

   private void clearScheduledTasks(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getOrCreateTag();
      nbt.remove("scheduledTasks");
   }

   private void executeScheduledTasks(ServerPlayer serverPlayer, ItemStack itemStack) {
      CompoundTag nbt = itemStack.getOrCreateTag();
      CompoundTag scheduledTasks = nbt.getCompound("scheduledTasks");

      for (String key : scheduledTasks.getAllKeys()) {
         VaultCrystalItem.IScheduledTaskDeserializer deserializer = SCHEDULED_TASK_DESERIALIZER_MAP.get(key);
         if (deserializer == null) {
            throw new IllegalStateException("Missing scheduled task deserializer registration for key [%s]".formatted(key));
         }

         deserializer.deserialize(scheduledTasks.getCompound(key)).execute(serverPlayer, itemStack, CrystalData.read(itemStack));
      }

      nbt.remove("scheduledTasks");
   }

   public static class AddClarityTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.AddClarityTask INSTANCE = new VaultCrystalItem.AddClarityTask();
      public static final String ID = "addClarity";

      private AddClarityTask() {
      }

      @Override
      public String getId() {
         return "addClarity";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack stack, CrystalData data) {
         if (!data.getModifiers().hasClarity()) {
            data.getModifiers().setClarity(true);
            data.write(stack);
         }
      }

      public static VaultCrystalItem.AddClarityTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public record AddModifiersTask(ResourceLocation pool, int count) implements VaultCrystalItem.IScheduledTask {
      public static final String ID = "addCurses";

      @Override
      public String getId() {
         return "addCurses";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack stack, CrystalData data) {
         Integer level = data.getProperties().getLevel().orElse(null);
         if (level != null) {
            ModConfigs.VAULT_MODIFIER_POOLS
               .getRandom(this.pool, level, JavaRandom.ofNanoTime())
               .forEach(modifier -> data.getModifiers().add(VaultModifierStack.of((VaultModifier<?>)modifier)));
            data.write(stack);
         }
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag nbt = VaultCrystalItem.IScheduledTask.super.serializeNBT();
         nbt.putString("pool", this.pool.toString());
         nbt.putInt("count", this.count);
         return nbt;
      }

      public static VaultCrystalItem.AddModifiersTask deserializeNBT(CompoundTag nbt) {
         return new VaultCrystalItem.AddModifiersTask(new ResourceLocation(nbt.getString("pool")), nbt.contains("count") ? nbt.getInt("count") : 1);
      }
   }

   public record CloneTask(boolean success) implements VaultCrystalItem.IScheduledTask {
      public static final String ID = "clone";
      private static final String KEY_SUCCESS = "success";
      public static final String CLONED = "Cloned";

      @Override
      public String getId() {
         return "clone";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         throw new UnsupportedOperationException();
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag nbt = VaultCrystalItem.IScheduledTask.super.serializeNBT();
         nbt.putBoolean("success", this.success);
         return nbt;
      }

      public static VaultCrystalItem.CloneTask deserializeNBT(CompoundTag nbt) {
         return new VaultCrystalItem.CloneTask(nbt.getBoolean("success"));
      }
   }

   public record EchoTask(int amount) implements VaultCrystalItem.IScheduledTask {
      public static final String ID = "echo";
      private static final String KEY_AMOUNT = "amount";

      @Override
      public String getId() {
         return "echo";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         throw new UnsupportedOperationException();
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag nbt = VaultCrystalItem.IScheduledTask.super.serializeNBT();
         nbt.putInt("amount", this.amount);
         return nbt;
      }

      public static VaultCrystalItem.EchoTask deserializeNBT(CompoundTag nbt) {
         return new VaultCrystalItem.EchoTask(nbt.getInt("amount"));
      }
   }

   public static class ExhaustTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.ExhaustTask INSTANCE = new VaultCrystalItem.ExhaustTask();
      public static final String ID = "exhaust";

      private ExhaustTask() {
      }

      @Override
      public String getId() {
         return "exhaust";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack stack, CrystalData data) {
         if (!data.getProperties().isUnmodifiable()) {
            data.getProperties().setUnmodifiable(true);
            data.write(stack);
         }
      }

      public static VaultCrystalItem.ExhaustTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public interface IScheduledTask {
      CompoundTag EMPTY_COMPOUND_TAG = new CompoundTag();

      String getId();

      void execute(ServerPlayer var1, ItemStack var2, CrystalData var3);

      default CompoundTag serializeNBT() {
         return EMPTY_COMPOUND_TAG;
      }
   }

   @FunctionalInterface
   public interface IScheduledTaskDeserializer {
      VaultCrystalItem.IScheduledTask deserialize(CompoundTag var1);
   }

   private static class NoOpTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.NoOpTask INSTANCE = new VaultCrystalItem.NoOpTask();

      @Override
      public String getId() {
         return "noop";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
      }

      @Override
      public CompoundTag serializeNBT() {
         return VaultCrystalItem.IScheduledTask.super.serializeNBT();
      }

      public static VaultCrystalItem.NoOpTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public static class RemoveRandomCurseTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.RemoveRandomCurseTask INSTANCE = new VaultCrystalItem.RemoveRandomCurseTask();
      public static final String ID = "removeRandomCurse";

      private RemoveRandomCurseTask() {
      }

      @Override
      public String getId() {
         return "removeRandomCurse";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack stack, CrystalData data) {
         data.getModifiers().removeRandomCurse();
         data.write(stack);
      }

      public static VaultCrystalItem.RemoveRandomCurseTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public static class RemoveRandomNegativeModifierTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.RemoveRandomNegativeModifierTask INSTANCE = new VaultCrystalItem.RemoveRandomNegativeModifierTask();
      public static final String ID = "removeRandomNegativeModifier";

      private RemoveRandomNegativeModifierTask() {
      }

      @Override
      public String getId() {
         return "removeRandomNegativeModifier";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack stack, CrystalData data) {
         Iterator<VaultModifierStack> it = data.getModifiers().iterator();

         while (it.hasNext()) {
            VaultModifierStack modifier = it.next();
            if (modifier.getModifier().getId().equals(VaultCrystalItem.NEGATIVE_MODIFIER_POOL_NAME)) {
               modifier.shrink(1);
               if (modifier.isEmpty()) {
                  it.remove();
               }

               data.write(stack);
               return;
            }
         }
      }

      public static VaultCrystalItem.RemoveRandomNegativeModifierTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }
}
