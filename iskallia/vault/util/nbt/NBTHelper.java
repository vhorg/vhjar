package iskallia.vault.util.nbt;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

public class NBTHelper {
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
      ListNBT listNBT = (ListNBT)nbt.func_74781_a(name);

      for (int i = 0; i < listNBT.size(); i++) {
         res.add(mapper.apply((N)listNBT.get(i)));
      }

      return res;
   }

   public static <T, N extends INBT> void writeList(CompoundNBT nbt, String name, Collection<T> list, Class<N> nbtType, Function<T, N> mapper) {
      ListNBT listNBT = new ListNBT();
      list.forEach(item -> listNBT.add(mapper.apply((T)item)));
      nbt.func_218657_a(name, listNBT);
   }
}
