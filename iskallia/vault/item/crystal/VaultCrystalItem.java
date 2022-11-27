package iskallia.vault.item.crystal;

import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.config.VaultCrystalCatalystConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultCrystalItem extends Item {
   private static final Random RANDOM = new Random();
   public static final String KEY_SCHEDULED_TASKS = "scheduledTasks";
   public static final String CLONED = "Cloned";
   private static final Map<String, VaultCrystalItem.IScheduledTaskDeserializer> SCHEDULED_TASK_DESERIALIZER_MAP = new HashMap<String, VaultCrystalItem.IScheduledTaskDeserializer>() {
      {
         this.put("exhaust", VaultCrystalItem.ExhaustTask::deserializeNBT);
         this.put("addRandomCurse", VaultCrystalItem.AddRandomCurseTask::deserializeNBT);
         this.put("addRandomCurses", VaultCrystalItem.AddRandomCursesTask::deserializeNBT);
         this.put("removeRandomCurse", VaultCrystalItem.RemoveRandomCurseTask::deserializeNBT);
         this.put("addClarity", VaultCrystalItem.AddClarityTask::deserializeNBT);
         this.put("echo", VaultCrystalItem.EchoTask::deserializeNBT);
         this.put("clone", VaultCrystalItem.CloneTask::deserializeNBT);
      }
   };

   public VaultCrystalItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(1));
      this.setRegistryName(id);
   }

   public static CrystalData getData(ItemStack stack) {
      return new CrystalData(stack);
   }

   public static ItemStack getCrystalWithBoss(String playerBossName) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData data = new CrystalData(stack);
      data.setType(CrystalData.Type.RAFFLE);
      return stack;
   }

   public static ItemStack getCrystalWithObjective(ResourceLocation objectiveKey) {
      ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData data = new CrystalData(stack);
      if (RANDOM.nextBoolean()) {
         data.setType(CrystalData.Type.COOP);
      }

      return stack;
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         this.addEmptyCrystal(items);
         this.addDebugCrystal(items);
      }
   }

   private void addEmptyCrystal(NonNullList<ItemStack> items) {
      ItemStack stack = new ItemStack(this);
      CrystalData crystal = new CrystalData(stack);
      crystal.setTheme(new PoolCrystalTheme(VaultMod.id("default")));
      items.add(stack);
   }

   private void addDebugCrystal(NonNullList<ItemStack> items) {
      ItemStack stack = new ItemStack(this);
      CrystalData crystal = new CrystalData(stack);
      crystal.setTheme(new PoolCrystalTheme(VaultMod.id("default")));
      crystal.setLayout(new ClassicInfiniteCrystalLayout(1));
      items.add(stack);
   }

   @Nonnull
   public Component getName(@Nonnull ItemStack stack) {
      CrystalData data = getData(stack);
      return (Component)(data.getEchoData().getEchoCount() > 0
         ? new TextComponent("Echoed Vault Crystal").withStyle(ChatFormatting.DARK_PURPLE)
         : super.getName(stack));
   }

   @Nonnull
   public InteractionResult useOn(UseOnContext context) {
      if (!context.getLevel().isClientSide && context.getPlayer() != null) {
         ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
         CrystalData data = new CrystalData(stack);
         if (data.getEchoData().getEchoCount() > 0) {
            return InteractionResult.SUCCESS;
         } else {
            BlockPos pos = context.getClickedPos();
            if (this.tryCreatePortal(context.getLevel(), pos, context.getClickedFace(), data)) {
               context.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.VAULT_PORTAL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
               MutableComponent playerName = context.getPlayer().getDisplayName().copy();
               playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
               String suffix = data.getType() == CrystalData.Type.FINAL_LOBBY ? " opened the Final Vault!" : " opened a Vault!";
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
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   private boolean tryCreatePortal(Level world, BlockPos pos, Direction facing, CrystalData data) {
      Optional<VaultPortalSize> optional = VaultPortalSize.getPortalSize(world, pos.relative(facing), Axis.X, VaultPortalBlock.FRAME);
      if (!optional.isPresent()) {
         return false;
      } else {
         VaultPortalSize portal = optional.get();
         BlockState state = (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, portal.getAxis());
         data.frameData = new FrameData();

         for (int i = -1; i <= portal.getWidth(); i++) {
            for (int j = -1; j <= portal.getHeight(); j++) {
               if (i == -1 || j == -1 || i == portal.getWidth() || j == portal.getHeight()) {
                  BlockPos p = portal.getBottomLeft().relative(portal.getRightDir(), i).above(j);
                  BlockEntity te = world.getBlockEntity(p);
                  data.frameData.tiles.add(new FrameData.Tile(world.getBlockState(p).getBlock(), te == null ? new CompoundTag() : te.serializeNBT(), p));
               }
            }
         }

         data.updateDelegate();
         portal.placePortalBlocks(blockPos -> {
            world.setBlock(blockPos, state, 3);
            if (world.getBlockEntity(blockPos) instanceof VaultPortalTileEntity portalTE) {
               portalTE.setCrystalData(data.copy());
            }
         });
         return true;
      }
   }

   public static long getSeed(ItemStack stack) {
      if (!(stack.getItem() instanceof VaultCrystalItem)) {
         return 0L;
      } else {
         CompoundTag nbt = stack.getOrCreateTag();
         if (!nbt.contains("Seed", 4)) {
            setRandomSeed(stack);
         }

         return nbt.getLong("Seed");
      }
   }

   public static void setRandomSeed(ItemStack stack) {
      if (stack.getItem() instanceof VaultCrystalItem) {
         stack.getOrCreateTag().putLong("Seed", RANDOM.nextLong());
      }
   }

   @OnlyIn(Dist.CLIENT)
   @ParametersAreNonnullByDefault
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      getData(stack).addInformation(world, tooltip, flag);
      super.appendHoverText(stack, world, tooltip, flag);
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (!world.isClientSide && hand != InteractionHand.OFF_HAND) {
         ItemStack stack = player.getMainHandItem();
         CrystalData data = getData(stack);
         return super.use(world, player, hand);
      } else {
         return super.use(world, player, hand);
      }
   }

   @ParametersAreNonnullByDefault
   public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (entity instanceof ServerPlayer serverPlayer && this.hasScheduledTasks(itemStack)) {
         this.executeScheduledTasks(serverPlayer, itemStack);
         this.clearScheduledTasks(itemStack);
      }

      super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
   }

   public static void scheduleTask(VaultCrystalItem.IScheduledTask task, ItemStack itemStack) {
      CompoundTag nbt = itemStack.getOrCreateTag();
      CompoundTag scheduledTasks = nbt.getCompound("scheduledTasks");
      scheduledTasks.put(task.getId(), task.serializeNBT());
      nbt.put("scheduledTasks", scheduledTasks);
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

         deserializer.deserialize(scheduledTasks.getCompound(key)).execute(serverPlayer, itemStack, getData(itemStack));
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
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         data.setClarity(true);
      }

      public static VaultCrystalItem.AddClarityTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public static class AddRandomCurseTask implements VaultCrystalItem.IScheduledTask {
      public static final VaultCrystalItem.AddRandomCurseTask INSTANCE = new VaultCrystalItem.AddRandomCurseTask();
      public static final String ID = "addRandomCurse";

      private AddRandomCurseTask() {
      }

      @Override
      public String getId() {
         return "addRandomCurse";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         if (Math.random() < ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.calculateCurseChance(data.getInstability())) {
            VaultCrystalCatalystConfig.ModifierPool pool = ModConfigs.VAULT_CRYSTAL_CATALYST.getModifierPoolById("CURSE");
            if (pool != null) {
               ResourceLocation curseModifier = pool.getRandomModifier(VaultCrystalItem.RANDOM);
               VaultModifierRegistry.getOpt(curseModifier).ifPresent(modifier -> data.addModifier(VaultModifierStack.of((VaultModifier<?>)modifier)));
            }
         }
      }

      public static VaultCrystalItem.AddRandomCurseTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }

   public record AddRandomCursesTask(int min, int max) implements VaultCrystalItem.IScheduledTask {
      public static final String ID = "addRandomCurses";
      private static final String KEY_MIN = "min";
      private static final String KEY_MAX = "max";

      @Override
      public String getId() {
         return "addRandomCurses";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         int curses = VaultCrystalItem.RANDOM.nextInt(this.max - this.min + 1) + this.min;

         for (int i = 0; i < curses; i++) {
            VaultCrystalCatalystConfig.ModifierPool pool = ModConfigs.VAULT_CRYSTAL_CATALYST.getModifierPoolById("CURSE");
            if (pool != null) {
               ResourceLocation curseModifier = pool.getRandomModifier(VaultCrystalItem.RANDOM);
               VaultModifierRegistry.getOpt(curseModifier).ifPresent(modifier -> data.addModifier(VaultModifierStack.of((VaultModifier<?>)modifier)));
            }
         }
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag nbt = VaultCrystalItem.IScheduledTask.super.serializeNBT();
         nbt.putInt("min", this.min);
         nbt.putInt("max", this.max);
         return nbt;
      }

      public static VaultCrystalItem.AddRandomCursesTask deserializeNBT(CompoundTag nbt) {
         return new VaultCrystalItem.AddRandomCursesTask(nbt.getInt("min"), nbt.getInt("max"));
      }
   }

   public record CloneTask(boolean success) implements VaultCrystalItem.IScheduledTask {
      public static final String ID = "clone";
      private static final String KEY_SUCCESS = "success";

      @Override
      public String getId() {
         return "clone";
      }

      @Override
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         data.setModifiable(false);
         if (this.success) {
            itemStack.getOrCreateTag().putBoolean("Cloned", true);
            EntityHelper.giveItem(player, itemStack.copy());
            player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.5F);
         } else {
            player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
         }
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
         int remainder = data.addEchoGems(this.amount);
         if (remainder > 0) {
            EntityHelper.giveItem(player, new ItemStack(ModItems.ECHO_GEM, remainder));
         }
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
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         if (data.canBeModified() && Math.random() * 100.0 < data.getInstability()) {
            data.setModifiable(false);
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
      public void execute(ServerPlayer player, ItemStack itemStack, CrystalData data) {
         data.removeRandomCurse(VaultCrystalItem.RANDOM);
      }

      public static VaultCrystalItem.RemoveRandomCurseTask deserializeNBT(CompoundTag nbt) {
         return INSTANCE;
      }
   }
}
