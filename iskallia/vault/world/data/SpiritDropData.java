package iskallia.vault.world.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

public class SpiritDropData extends SavedData {
   private static final String DATA_NAME = "the_vault_SpiritDrops";
   private static final String PLAYER_DROPS_TAG = "playerDrops";
   private final Map<UUID, List<ItemStack>> playerDrops = new HashMap<>();

   public static SpiritDropData get(ServerLevel level) {
      return (SpiritDropData)level.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(SpiritDropData::create, SpiritDropData::new, "the_vault_SpiritDrops");
   }

   public void addDrop(UUID playerId, ItemStack dropStack) {
      this.playerDrops.computeIfAbsent(playerId, id -> new ArrayList<>()).add(dropStack);
      this.setDirty();
   }

   public void removeDrops(UUID playerId) {
      this.playerDrops.remove(playerId);
      this.setDirty();
   }

   public static SpiritDropData create(CompoundTag tag) {
      SpiritDropData data = new SpiritDropData();
      data.load(tag);
      return data;
   }

   @Nonnull
   public CompoundTag save(CompoundTag tag) {
      if (!this.playerDrops.isEmpty()) {
         tag.put("playerDrops", this.serializePlayerDrops());
      }

      return tag;
   }

   public void load(CompoundTag tag) {
      this.playerDrops.clear();
      if (tag.contains("playerDrops", 10)) {
         CompoundTag playerDropsTag = tag.getCompound("playerDrops");
         playerDropsTag.getAllKeys().forEach(playerId -> {
            ListTag dropsListTag = playerDropsTag.getList(playerId, 10);
            List<ItemStack> drops = new ArrayList<>();
            dropsListTag.forEach(dropTag -> drops.add(ItemStack.of((CompoundTag)dropTag)));
            this.playerDrops.put(UUID.fromString(playerId), drops);
         });
      }
   }

   private CompoundTag serializePlayerDrops() {
      CompoundTag tag = new CompoundTag();
      this.playerDrops.forEach((playerId, drops) -> tag.put(playerId.toString(), this.serializeStacks((List<ItemStack>)drops)));
      return tag;
   }

   private ListTag serializeStacks(List<ItemStack> lootStacks) {
      ListTag lootStacksNbt = new ListTag();
      lootStacks.forEach(stack -> lootStacksNbt.add(stack.save(new CompoundTag())));
      return lootStacksNbt;
   }

   public List<ItemStack> getDrops(UUID playerId) {
      return this.playerDrops.getOrDefault(playerId, Collections.emptyList());
   }
}
