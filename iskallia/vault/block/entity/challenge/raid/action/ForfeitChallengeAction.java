package iskallia.vault.block.entity.challenge.raid.action;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.block.entity.challenge.raid.RaidChallengeManager;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;

public class ForfeitChallengeAction extends ChallengeAction<ForfeitChallengeAction.Config> {
   public ForfeitChallengeAction() {
      super(new ForfeitChallengeAction.Config());
   }

   public ForfeitChallengeAction(ForfeitChallengeAction.Config config) {
      super(config);
   }

   @Override
   public boolean onMerge(ChallengeAction<?> action) {
      return action instanceof ForfeitChallengeAction;
   }

   @Override
   public void onActivate(ServerLevel world, ChallengeManager manager, RandomSource random) {
      super.onActivate(world, manager, random);
      if (manager instanceof RaidChallengeManager raid) {
         if (this.getConfig().loot) {
            raid.setPhase(RaidChallengeManager.Phase.COMPLETED);
         } else {
            raid.setPhase(RaidChallengeManager.Phase.FORFEITED);
         }
      }
   }

   @Override
   public Component getText() {
      return !this.getConfig().loot
         ? new TextComponent("Forfeit").setStyle(Style.EMPTY.withColor(this.getConfig().textColor))
         : new TextComponent("Forfeit & Loot").setStyle(Style.EMPTY.withColor(this.getConfig().textColor));
   }

   public static class Config extends ChallengeAction.Config {
      private boolean loot;

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.BOOLEAN.writeBits(this.loot, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.loot = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.BOOLEAN.writeNbt(this.loot).ifPresent(tag -> nbt.put("loot", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.loot = Adapters.BOOLEAN.readNbt(nbt.get("loot")).orElse(false);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.BOOLEAN.writeJson(this.loot).ifPresent(tag -> json.add("loot", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.loot = Adapters.BOOLEAN.readJson(json.get("loot")).orElse(false);
      }
   }
}
