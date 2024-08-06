package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.util.EntityHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemRewardTask extends ConsumableTask<ItemRewardTask.Config> {
   public ItemRewardTask() {
      super(new ItemRewardTask.Config());
   }

   public ItemRewardTask(ItemRewardTask.Config config) {
      super(config);
   }

   @Override
   protected void onConsume(TaskContext context) {
      if (context.getSource() instanceof EntityTaskSource entityTaskSource) {
         for (ServerPlayer player : entityTaskSource.getEntities(ServerPlayer.class)) {
            for (ItemStack stack : this.getConfig().stacks) {
               ItemStack reward = stack.copy();
               EntityHelper.giveItem(player, reward);
            }
         }
      }
   }

   public static class Config extends ConfiguredTask.Config {
      public List<ItemStack> stacks = new ArrayList<>();
      private static final ArrayAdapter<ItemStack> ADAPTER = Adapters.ofArray(ItemStack[]::new, Adapters.ITEM_STACK);

      public Config() {
      }

      public Config(List<ItemStack> stacks) {
         this.stacks = stacks;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         ADAPTER.writeBits(this.stacks.toArray(ItemStack[]::new), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.stacks = Arrays.stream(ADAPTER.readBits(buffer).orElseThrow()).toList();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            ADAPTER.writeNbt(this.stacks.toArray(ItemStack[]::new)).ifPresent(value -> nbt.put("stacks", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.stacks = Arrays.stream(ADAPTER.readNbt(nbt.get("stacks")).orElse(new ItemStack[0])).toList();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            ADAPTER.writeJson(this.stacks.toArray(ItemStack[]::new)).ifPresent(value -> json.add("stacks", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.stacks = Arrays.stream(ADAPTER.readJson(json.get("stacks")).orElse(new ItemStack[0])).toList();
      }
   }
}
