package iskallia.vault.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.item.crystal.data.serializable.IJsonSerializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantmentCost implements IJsonSerializable<JsonObject> {
   public static EnchantmentCost EMPTY = new EnchantmentCost(new ArrayList<>(), 0);
   private List<ItemStack> items = new LinkedList<>();
   private int levels;
   public static SerializableAdapter<EnchantmentCost, CompoundTag, JsonObject> ADAPTER = new SerializableAdapter<>(EnchantmentCost::new, false);

   private EnchantmentCost() {
   }

   public EnchantmentCost(List<ItemStack> items, int levels) {
      this.items = items;
      this.levels = levels;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public int getLevels() {
      return this.levels;
   }

   public boolean tryConsume(ServerPlayer player) {
      if (player.isCreative()) {
         return true;
      } else if (player.experienceLevel < this.levels) {
         return false;
      } else {
         List<ItemStack> missing = InventoryUtil.getMissingInputs(this.items, player.getInventory());
         if (!missing.isEmpty()) {
            return false;
         } else if (!InventoryUtil.consumeInputs(this.items, player.getInventory(), true)) {
            return false;
         } else {
            InventoryUtil.consumeInputs(this.items, player.getInventory(), false);
            player.setExperienceLevels(player.experienceLevel - this.levels);
            return true;
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.items.forEach(stack -> Adapters.ITEM_STACK.writeJson(stack).ifPresent(array::add));
      json.add("items", array);
      json.add("levels", new JsonPrimitive(this.levels));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.items.clear();
      JsonArray array = json.getAsJsonArray("items");
      array.forEach(element -> Adapters.ITEM_STACK.readJson(element).ifPresent(this.items::add));
      this.levels = Adapters.INT.readJson(json.get("levels")).orElse(0);
   }
}
