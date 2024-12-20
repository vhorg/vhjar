package iskallia.vault.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.integration.IntegrationCurios;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.server.ServerLifecycleHooks;

public class MiscUtils {
   private static final Random rand = new Random();

   public static <T> T eitherOf(Random r, T... selection) {
      return selection.length == 0 ? null : selection[r.nextInt(selection.length)];
   }

   public static <T> List<T> concat(List<T> list1, T... elements) {
      return Stream.concat(list1.stream(), Arrays.stream(elements)).collect(Collectors.toList());
   }

   public static <T> List<T> concat(List<T> list1, List<T> list2) {
      return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
   }

   public static <T> Set<T> intersection(Collection<T> list1, Collection<T> list2) {
      return Stream.concat(list1.stream(), list2.stream()).filter(list1::contains).filter(list2::contains).collect(Collectors.toSet());
   }

   public static java.awt.geom.Point2D.Float getMidpoint(Rectangle r) {
      return new java.awt.geom.Point2D.Float(r.x + r.width / 2.0F, r.y + r.height / 2.0F);
   }

   public static boolean hasEmptySlot(Container inventory) {
      return getRandomEmptySlot(inventory) != -1;
   }

   public static boolean hasEmptySlot(IItemHandler inventory) {
      return getRandomEmptySlot(inventory) != -1;
   }

   public static int getRandomEmptySlot(Container inventory) {
      return getRandomEmptySlot(new InvWrapper(inventory));
   }

   public static int getRandomEmptySlot(IItemHandler handler) {
      List<Integer> slots = new ArrayList<>();

      for (int slot = 0; slot < handler.getSlots(); slot++) {
         if (handler.getStackInSlot(slot).isEmpty()) {
            slots.add(slot);
         }
      }

      return slots.isEmpty() ? -1 : getRandomEntry(slots, rand);
   }

   public static int getRandomSlot(IItemHandler handler) {
      List<Integer> slots = new ArrayList<>();

      for (int slot = 0; slot < handler.getSlots(); slot++) {
         slots.add(slot);
      }

      return slots.isEmpty() ? -1 : getRandomEntry(slots, rand);
   }

   public static List<Integer> getEmptySlots(Container inventory) {
      List<Integer> list = Lists.newArrayList();

      for (int i = 0; i < inventory.getContainerSize(); i++) {
         if (inventory.getItem(i).isEmpty()) {
            list.add(i);
         }
      }

      return list;
   }

   public static List<Integer> getEmptySlots(IItemHandler inventory) {
      List<Integer> list = Lists.newArrayList();

      for (int i = 0; i < inventory.getSlots(); i++) {
         if (inventory.getStackInSlot(i).isEmpty()) {
            list.add(i);
         }
      }

      return list;
   }

   public static boolean inventoryContains(Container inventory, Predicate<ItemStack> filter) {
      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         if (filter.test(inventory.getItem(slot))) {
            return true;
         }
      }

      return false;
   }

   public static boolean inventoryContains(IItemHandler handler, Predicate<ItemStack> filter) {
      for (int slot = 0; slot < handler.getSlots(); slot++) {
         if (filter.test(handler.getStackInSlot(slot))) {
            return true;
         }
      }

      return false;
   }

   public static List<ItemStack> mergeItemStacks(List<ItemStack> stacks) {
      List<ItemStack> out = new ArrayList<>();

      label29:
      for (ItemStack stack : stacks) {
         if (!stack.isEmpty()) {
            for (ItemStack existing : out) {
               if (canMerge(existing, stack)) {
                  existing.setCount(existing.getCount() + stack.getCount());
                  continue label29;
               }
            }

            out.add(stack);
         }
      }

      return out;
   }

   public static void mergeItemStack(List<ItemStack> stacks, ItemStack stackToMerge) {
      if (!stackToMerge.isEmpty()) {
         for (ItemStack existing : stacks) {
            if (existing.getCount() < existing.getMaxStackSize() && ItemStack.isSameItemSameTags(existing, stackToMerge)) {
               int toInsert = Math.min(existing.getMaxStackSize() - existing.getCount(), stackToMerge.getCount());
               existing.setCount(existing.getCount() + toInsert);
               if (toInsert == stackToMerge.getCount()) {
                  return;
               }

               stackToMerge = ItemHandlerHelper.copyStackWithSize(stackToMerge, stackToMerge.getCount() - toInsert);
            }
         }

         stacks.add(stackToMerge);
      }
   }

   public static List<ItemStack> splitAndLimitStackSize(List<ItemStack> stacks) {
      List<ItemStack> out = new ArrayList<>();

      for (ItemStack stack : stacks) {
         if (!stack.isEmpty()) {
            int i = stack.getCount();

            while (i > 0) {
               int newCount = Math.min(i, stack.getMaxStackSize());
               i -= newCount;
               ItemStack copy = stack.copy();
               copy.setCount(newCount);
               out.add(copy);
            }
         }
      }

      return out;
   }

   public static boolean canMerge(ItemStack stack, ItemStack other) {
      return stack.getItem() == other.getItem() && ItemStack.tagMatches(stack, other);
   }

   public static boolean canFullyMergeIntoSlot(Container inventory, int slot, ItemStack stack) {
      if (stack.isEmpty()) {
         return true;
      } else {
         ItemStack existing = inventory.getItem(slot);
         if (existing.isEmpty()) {
            return inventory.getMaxStackSize() >= stack.getCount();
         } else {
            return !canMerge(existing, stack) ? false : inventory.getMaxStackSize() >= existing.getCount() + stack.getCount();
         }
      }
   }

   public static ItemStack mergeIntoInventory(IItemHandler inventory, ItemStack toAdd, boolean simulate) {
      if (toAdd.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (!inSlot.isEmpty()) {
               toAdd = inventory.insertItem(slot, toAdd, simulate);
               if (toAdd.isEmpty()) {
                  return ItemStack.EMPTY;
               }
            }
         }

         for (int emptySlotId : getEmptySlots(inventory)) {
            toAdd = inventory.insertItem(emptySlotId, toAdd, simulate);
            if (toAdd.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         return toAdd;
      }
   }

   public static void addStackToSlot(Container inventory, int slot, ItemStack toAdd) {
      if (!toAdd.isEmpty()) {
         ItemStack stack = inventory.getItem(slot);
         if (stack.isEmpty()) {
            inventory.setItem(slot, toAdd.copy());
         } else {
            if (canMerge(stack, toAdd)) {
               stack.grow(toAdd.getCount());
            }
         }
      }
   }

   public static boolean addItemStack(Container inventory, ItemStack stack) {
      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         ItemStack contained = inventory.getItem(slot);
         if (contained.isEmpty()) {
            inventory.setItem(slot, stack);
            return true;
         }
      }

      return false;
   }

   public static <T> T getListEntrySafe(List<T> list, int index) {
      return list.get(Mth.clamp(index, 0, list.size() - 1));
   }

   public static <T extends Enum<T>> T getEnumEntry(Class<T> enumClass, int index) {
      T[] constants = (T[])enumClass.getEnumConstants();
      return constants[Mth.clamp(index, 0, constants.length - 1)];
   }

   public static Optional<BlockPos> getEmptyNearby(LevelReader world, BlockPos pos) {
      return BlockPos.findClosestMatch(pos, 8, 8, world::isEmptyBlock);
   }

   public static BlockPos getRandomPos(BoundingBox box, Random r) {
      return getRandomPos(AABB.of(box), r);
   }

   public static BlockPos getRandomPos(AABB box, Random r) {
      int sizeX = Math.max(1, Mth.floor(box.getXsize()));
      int sizeY = Math.max(1, Mth.floor(box.getYsize()));
      int sizeZ = Math.max(1, Mth.floor(box.getZsize()));
      return new BlockPos(box.minX + r.nextInt(sizeX), box.minY + r.nextInt(sizeY), box.minZ + r.nextInt(sizeZ));
   }

   public static Vec3 getRandomOffset(AABB box, Random r) {
      return new Vec3(
         box.minX + r.nextFloat() * (box.maxX - box.minX), box.minY + r.nextFloat() * (box.maxY - box.minY), box.minZ + r.nextFloat() * (box.maxZ - box.minZ)
      );
   }

   public static Vec3 getRandomOffset(BlockPos pos, Random r) {
      return new Vec3(pos.getX() + r.nextFloat(), pos.getY() + r.nextFloat(), pos.getZ() + r.nextFloat());
   }

   public static Vec3 getRandomOffset(BlockPos pos, Random r, float scale) {
      float x = pos.getX() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      float y = pos.getY() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      float z = pos.getZ() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      return new Vec3(x, y, z);
   }

   public static Collection<ChunkPos> getChunksContaining(AABB box) {
      return getChunksContaining(new Vec3i(box.minX, box.minY, box.minZ), new Vec3i(box.maxX, box.maxY, box.maxZ));
   }

   public static Collection<ChunkPos> getChunksContaining(Vec3i min, Vec3i max) {
      List<ChunkPos> affected = Lists.newArrayList();
      int maxX = max.getX() >> 4;
      int maxZ = max.getZ() >> 4;

      for (int chX = min.getX() >> 4; chX <= maxX; chX++) {
         for (int chZ = min.getZ() >> 4; chZ <= maxZ; chZ++) {
            affected.add(new ChunkPos(chX, chZ));
         }
      }

      return affected;
   }

   @Nullable
   public static <T> T getRandomEntry(T... entries) {
      return getRandomEntry(Lists.newArrayList(entries), rand);
   }

   public static <K, V> V getRandomValueFromMap(Map<K, V> map) {
      if (map.isEmpty()) {
         return null;
      } else {
         int randomIndex = new Random().nextInt(map.size());
         int currentIndex = 0;

         for (V value : map.values()) {
            if (currentIndex == randomIndex) {
               return value;
            }

            currentIndex++;
         }

         return null;
      }
   }

   public static <K, V> Entry<K, V> getRandomMapEntry(Map<K, V> map) {
      if (map.isEmpty()) {
         return null;
      } else {
         int randomIndex = new Random().nextInt(map.size());
         int currentIndex = 0;

         for (Entry<K, V> entry : map.entrySet()) {
            if (currentIndex == randomIndex) {
               return entry;
            }

            currentIndex++;
         }

         return null;
      }
   }

   @Nullable
   public static <T> T getRandomEntry(Collection<T> collection) {
      return getRandomEntry(collection, rand);
   }

   @Nullable
   public static <T> T getRandomEntry(Collection<T> collection, Random rand) {
      if (collection.isEmpty()) {
         return null;
      } else {
         int randomPick = rand.nextInt(collection.size());
         return (T)Iterables.get(collection, randomPick, null);
      }
   }

   public static void broadcast(Component message) {
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().broadcastMessage(message, ChatType.CHAT, Util.NIL_UUID);
      }
   }

   public static <T> Optional<Float> getIntValueRange(int value, Optional<T> min, Optional<T> max, Function<T, Integer> valueFn) {
      return min.isPresent() && max.isPresent() ? Optional.of(getValueRange(value, valueFn.apply(min.get()), valueFn.apply(max.get()))) : Optional.empty();
   }

   public static float getValueRange(int value, int min, int max) {
      int range = max - min;
      int valueRange = value - min;
      return Mth.clamp((float)valueRange / range, 0.0F, 1.0F);
   }

   public static <T> Optional<Float> getFloatValueRange(float value, Optional<T> min, Optional<T> max, Function<T, Float> valueFn) {
      return min.isPresent() && max.isPresent() ? Optional.of(getValueRange(value, valueFn.apply(min.get()), valueFn.apply(max.get()))) : Optional.empty();
   }

   public static float getValueRange(float value, float min, float max) {
      float range = max - min;
      float valueRange = value - min;
      return Mth.clamp(valueRange / range, 0.0F, 1.0F);
   }

   @Nullable
   public static Player findPlayerUsingAnvil(ItemStack left, ItemStack right) {
      for (Player player : SidedHelper.getSidedPlayers()) {
         if (player.containerMenu instanceof AnvilMenu) {
            NonNullList<ItemStack> contents = player.containerMenu.getItems();
            if (contents.get(0) == left && contents.get(1) == right) {
               return player;
            }
         }
      }

      return null;
   }

   public static JavaRandom getVaultPositionRandom(Vault vault, BlockPos at) {
      JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED));
      long a = random.nextLong() | 1L;
      long b = random.nextLong() | 1L;
      long c = random.nextLong() | 1L;
      random.setSeed(a * at.getX() + b * at.getY() + c * at.getZ() ^ vault.get(Vault.SEED));
      return random;
   }

   public static void fillContainer(AbstractContainerMenu ct, NonNullList<ItemStack> items) {
      for (int slot = 0; slot < items.size(); slot++) {
         ct.setItem(slot, ct.getStateId(), (ItemStack)items.get(slot));
      }
   }

   public static void clearPlayerInventory(Player player) {
      player.getInventory().clearContent();
      IntegrationCurios.clearCurios(player);
   }

   public static void giveItem(ServerPlayer player, ItemStack stack) {
      stack = stack.copy();
      if (player.getInventory().add(stack) && stack.isEmpty()) {
         stack.setCount(1);
         ItemEntity dropped = player.drop(stack, false);
         if (dropped != null) {
            dropped.makeFakeItem();
         }

         player.level
            .playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.ITEM_PICKUP,
               SoundSource.PLAYERS,
               0.2F,
               ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
            );
         player.inventoryMenu.broadcastChanges();
      } else {
         ItemEntity dropped = player.drop(stack, false);
         if (dropped != null) {
            dropped.setNoPickUpDelay();
            dropped.setOwner(player.getUUID());
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static double getClientDistance(Vec3i pos) {
      Vec3 src = Vec3.ZERO;
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         src = player.position();
      }

      return src.distanceTo(Vec3.atCenterOf(pos));
   }

   public static Vector3f getRandomCirclePosition(Vector3f centerOffset, Vector3f axis, float radius) {
      return getCirclePosition(centerOffset, axis, radius, (float)(Math.random() * 360.0));
   }

   public static Vector3f getCirclePosition(Vector3f centerOffset, Vector3f axis, float radius, float degree) {
      Vector3f circleVec = normalize(perpendicular(axis));
      circleVec = new Vector3f(circleVec.x() * radius, circleVec.y() * radius, circleVec.z() * radius);
      Quaternion rotQuat = new Quaternion(axis, degree, true);
      circleVec.transform(rotQuat);
      return new Vector3f(circleVec.x() + centerOffset.x(), circleVec.y() + centerOffset.y(), circleVec.z() + centerOffset.z());
   }

   public static Vector3f normalize(Vector3f vec) {
      float lengthSq = vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z();
      float length = (float)Math.sqrt(lengthSq);
      return new Vector3f(vec.x() / length, vec.y() / length, vec.z() / length);
   }

   public static Vector3f perpendicular(Vector3f vec) {
      return vec.z() == 0.0 ? new Vector3f(vec.y(), -vec.x(), 0.0F) : new Vector3f(0.0F, vec.z(), -vec.y());
   }

   public static boolean isPlayerFakeMP(ServerPlayer player) {
      if (player instanceof FakePlayer) {
         return true;
      } else {
         try {
            player.getIpAddress().length();
            player.connection.connection.getRemoteAddress().toString();
            return !player.connection.connection.channel().isOpen();
         } catch (Exception var2) {
            return true;
         }
      }
   }

   public static List<TextComponent> splitDescriptionText(String text) {
      List<TextComponent> tooltip = new ArrayList<>();
      StringBuilder sb = new StringBuilder();

      for (String word : text.split("\\s+")) {
         sb.append(word).append(" ");
         if (sb.length() >= 30) {
            tooltip.add(new TextComponent(sb.toString().trim()));
            sb = new StringBuilder();
         }
      }

      if (sb.length() > 0) {
         tooltip.add(new TextComponent(sb.toString().trim()));
      }

      return tooltip;
   }

   public static <T> Class<T> cast(Class<?> cls) {
      return (Class<T>)cls;
   }
}
