package iskallia.vault.util.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class NBTHelper {
   public static IntArrayTag serializeBoundingBox(BoundingBox box) {
      return new IntArrayTag(new int[]{box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()});
   }

   public static BoundingBox deserializeBoundingBox(int[] v) {
      return new BoundingBox(v[0], v[1], v[2], v[3], v[4], v[5]);
   }

   public static CompoundTag serializeBlockPos(BlockPos pos) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("posX", pos.getX());
      tag.putInt("posY", pos.getY());
      tag.putInt("posZ", pos.getZ());
      return tag;
   }

   public static BlockPos deserializeBlockPos(CompoundTag tag) {
      int x = tag.getInt("posX");
      int y = tag.getInt("posY");
      int z = tag.getInt("posZ");
      return new BlockPos(x, y, z);
   }

   public static <T, N extends Tag> Map<UUID, T> readMap(CompoundTag nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
      Map<UUID, T> res = new HashMap<>();
      ListTag uuidList = nbt.getList(name + "Keys", 8);
      ListTag valuesList = (ListTag)nbt.get(name + "Values");
      if (uuidList.size() != valuesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < uuidList.size(); i++) {
            res.put(UUID.fromString(uuidList.get(i).getAsString()), mapper.apply((N)valuesList.get(i)));
         }

         return res;
      }
   }

   public static <T, N extends Tag> void writeMap(CompoundTag nbt, String name, Map<UUID, T> map, Class<N> nbtType, Function<T, N> mapper) {
      ListTag uuidList = new ListTag();
      ListTag valuesList = new ListTag();
      map.forEach((key, value) -> {
         uuidList.add(StringTag.valueOf(key.toString()));
         valuesList.add(mapper.apply((T)value));
      });
      nbt.put(name + "Keys", uuidList);
      nbt.put(name + "Values", valuesList);
   }

   public static <T, N extends Tag> List<T> readList(CompoundTag nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
      return readCollection(nbt, name, nbtType, mapper, new ArrayList<>());
   }

   public static <T, N extends Tag> Set<T> readSet(CompoundTag nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
      return readCollection(nbt, name, nbtType, mapper, new HashSet<>());
   }

   public static <T, C extends Collection<T>, N extends Tag> C readCollection(
      CompoundTag nbt, String name, Class<N> nbtType, Function<N, T> mapper, C collection
   ) {
      for (Tag inbt : (ListTag)nbt.get(name)) {
         collection.add(mapper.apply((N)inbt));
      }

      return collection;
   }

   public static <T, N extends Tag> void writeCollection(CompoundTag nbt, String name, Collection<T> list, Class<N> nbtType, Function<T, N> mapper) {
      ListTag listNBT = new ListTag();
      list.forEach(item -> listNBT.add(mapper.apply((T)item)));
      nbt.put(name, listNBT);
   }

   public static <T> void writeOptional(CompoundTag nbt, String key, @Nullable T object, BiConsumer<CompoundTag, T> writer) {
      nbt.putBoolean(key + "_present", object != null);
      if (object != null) {
         CompoundTag write = new CompoundTag();
         writer.accept(write, object);
         nbt.put(key, write);
      }
   }

   @Nullable
   public static <T> T readOptional(CompoundTag nbt, String key, Function<CompoundTag, T> reader) {
      return readOptional(nbt, key, reader, null);
   }

   @Nullable
   public static <T> T readOptional(CompoundTag nbt, String key, Function<CompoundTag, T> reader, T _default) {
      if (nbt.getBoolean(key + "_present")) {
         CompoundTag read = nbt.getCompound(key);
         return reader.apply(read);
      } else {
         return _default;
      }
   }

   public static ListTag serializeSimpleContainer(SimpleContainer ct) {
      ListTag list = new ListTag();

      for (int slot = 0; slot < ct.getContainerSize(); slot++) {
         ItemStack stack = ct.getItem(slot);
         if (!stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("slot", slot);
            tag.put("stack", stack.serializeNBT());
            list.add(tag);
         }
      }

      return list;
   }

   public static void deserializeSimpleContainer(SimpleContainer ct, ListTag list) {
      ct.clearContent();

      for (int i = 0; i < list.size(); i++) {
         CompoundTag tag = list.getCompound(i);
         int slot = tag.getInt("slot");
         ItemStack stack = ItemStack.of(tag.getCompound("stack"));
         if (!stack.isEmpty()) {
            ct.setItem(slot, stack);
         }
      }
   }
}
