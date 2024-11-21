package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.init.ModConfigs;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ReferenceChallengeAction extends ChallengeAction<ReferenceChallengeAction.Config> {
   protected ReferenceChallengeAction() {
      super(new ReferenceChallengeAction.Config());
   }

   protected ReferenceChallengeAction(ReferenceChallengeAction.Config config) {
      super(config);
   }

   @Override
   public boolean onMerge(ChallengeAction<?> action) {
      return false;
   }

   @Override
   public Component getText() {
      return new TextComponent("REFERENCE");
   }

   @Override
   public Stream<ChallengeAction<?>> flatten(RandomSource random) {
      return ModConfigs.RAID_ACTIONS.getRandom(this.getConfig().path, random).map(action -> {
         action = action.copy();
         action.onPopulate(random);
         return action.flatten(random);
      }).orElse(Stream.empty());
   }

   public static class Config extends ChallengeAction.Config {
      private String path;

      public Config() {
      }

      public Config(String path) {
         this.path = path;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.writeBits(this.path, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.path = Adapters.UTF_8.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.UTF_8.writeNbt(this.path).ifPresent(tag -> nbt.put("path", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         this.path = Adapters.UTF_8.readNbt(nbt.get("path")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.path).ifPresent(tag -> json.add("path", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.path = Adapters.UTF_8.readJson(json.get("path")).orElseThrow();
      }
   }
}
