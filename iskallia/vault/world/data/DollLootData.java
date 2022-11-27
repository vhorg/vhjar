package iskallia.vault.world.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

public class DollLootData extends SavedData {
   private static final String DATA_NAME_PREFIX = "vaultdolls/";
   private static final String LOOT_STACKS_TAG = "lootStacks";
   private final List<ItemStack> lootStacks = new ArrayList<>();

   public static DollLootData get(ServerLevel level, UUID dollId) {
      return (DollLootData)level.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(DollLootData::create, DollLootData::new, "vaultdolls/" + dollId.toString());
   }

   public void addLoot(ItemStack lootStack) {
      this.lootStacks.add(lootStack);
      this.setDirty();
   }

   public static DollLootData create(CompoundTag tag) {
      DollLootData data = new DollLootData();
      data.load(tag);
      return data;
   }

   @Nonnull
   public CompoundTag save(CompoundTag tag) {
      if (!this.lootStacks.isEmpty()) {
         tag.put("lootStacks", this.serializeStacks(this.lootStacks));
      }

      return tag;
   }

   public void save(File file) {
      file.getParentFile().mkdirs();
      super.save(file);
   }

   public void load(CompoundTag tag) {
      this.lootStacks.clear();
      if (tag.contains("lootStacks")) {
         this.deserializeStacks(tag.getList("lootStacks", 10));
      }
   }

   private ListTag serializeStacks(List<ItemStack> lootStacks) {
      ListTag lootStacksNbt = new ListTag();
      lootStacks.forEach(stack -> lootStacksNbt.add(stack.save(new CompoundTag())));
      return lootStacksNbt;
   }

   private void deserializeStacks(ListTag itemsNbt) {
      this.lootStacks.clear();
      new ArrayList();
      itemsNbt.forEach(nbt -> {
         CompoundTag itemNbt = (CompoundTag)nbt;
         this.lootStacks.add(ItemStack.of(itemNbt));
      });
   }

   public List<ItemStack> getLoot() {
      return this.lootStacks;
   }

   public void clearLoot() {
      this.lootStacks.clear();
      this.setDirty();
   }
}
