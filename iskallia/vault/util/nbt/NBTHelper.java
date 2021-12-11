package iskallia.vault.util.nbt;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;

public class NBTHelper {
   public static CompoundNBT serializeBlockPos(BlockPos pos) {
      CompoundNBT tag = new CompoundNBT();
      tag.func_74768_a("posX", pos.func_177958_n());
      tag.func_74768_a("posY", pos.func_177956_o());
      tag.func_74768_a("posZ", pos.func_177952_p());
      return tag;
   }

   public static BlockPos deserializeBlockPos(CompoundNBT tag) {
      int x = tag.func_74762_e("posX");
      int y = tag.func_74762_e("posY");
      int z = tag.func_74762_e("posZ");
      return new BlockPos(x, y, z);
   }

   public static <T, N extends INBT> Map<UUID, T> readMap(CompoundNBT nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
      Map<UUID, T> res = new HashMap<>();
      ListNBT uuidList = nbt.func_150295_c(name + "Keys", 8);
      ListNBT valuesList = (ListNBT)nbt.func_74781_a(name + "Values");
      if (uuidList.size() != valuesList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < uuidList.size(); i++) {
            res.put(UUID.fromString(uuidList.get(i).func_150285_a_()), mapper.apply((N)valuesList.get(i)));
         }

         return res;
      }
   }

   public static <T, N extends INBT> void writeMap(CompoundNBT nbt, String name, Map<UUID, T> map, Class<N> nbtType, Function<T, N> mapper) {
      ListNBT uuidList = new ListNBT();
      ListNBT valuesList = new ListNBT();
      map.forEach((key, value) -> {
         uuidList.add(StringNBT.func_229705_a_(key.toString()));
         valuesList.add(mapper.apply((T)value));
      });
      nbt.func_218657_a(name + "Keys", uuidList);
      nbt.func_218657_a(name + "Values", valuesList);
   }

   public static <T, N extends INBT> List<T> readList(CompoundNBT nbt, String name, Class<N> nbtType, Function<N, T> mapper) {
      List<T> res = new LinkedList<>();

      for (INBT inbt : (ListNBT)nbt.func_74781_a(name)) {
         res.add(mapper.apply((N)inbt));
      }

      return res;
   }

   public static <T, N extends INBT> void writeList(CompoundNBT nbt, String name, Collection<T> list, Class<N> nbtType, Function<T, N> mapper) {
      ListNBT listNBT = new ListNBT();
      list.forEach(item -> listNBT.add(mapper.apply((T)item)));
      nbt.func_218657_a(name, listNBT);
   }

   public static <T> void writeOptional(CompoundNBT nbt, String key, @Nullable T object, BiConsumer<CompoundNBT, T> writer) {
      nbt.func_74757_a(key + "_present", object != null);
      if (object != null) {
         CompoundNBT write = new CompoundNBT();
         writer.accept(write, object);
         nbt.func_218657_a(key, write);
      }
   }

   @Nullable
   public static <T> T readOptional(CompoundNBT nbt, String key, Function<CompoundNBT, T> reader) {
      return readOptional(nbt, key, reader, null);
   }

   @Nullable
   public static <T> T readOptional(CompoundNBT nbt, String key, Function<CompoundNBT, T> reader, T _default) {
      if (nbt.func_74767_n(key + "_present")) {
         CompoundNBT read = nbt.func_74775_l(key);
         return reader.apply(read);
      } else {
         return _default;
      }
   }
}
