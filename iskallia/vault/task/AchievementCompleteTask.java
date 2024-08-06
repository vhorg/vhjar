package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundToastMessage;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class AchievementCompleteTask extends ConsumableTask<AchievementCompleteTask.Config> {
   public AchievementCompleteTask() {
      this(new AchievementCompleteTask.Config());
   }

   public AchievementCompleteTask(AchievementCompleteTask.Config config) {
      super(config);
   }

   @Override
   protected void onConsume(TaskContext context) {
      if (context.getSource() instanceof EntityTaskSource entityTaskSource) {
         for (ServerPlayer player : entityTaskSource.getEntities(ServerPlayer.class)) {
            ModNetwork.CHANNEL
               .sendTo(
                  new ClientboundToastMessage(this.getConfig().title, "Achievement Complete!", this.getConfig().icon),
                  player.connection.getConnection(),
                  NetworkDirection.PLAY_TO_CLIENT
               );
            FireworkRocketEntity firework = new FireworkRocketEntity(
               player.level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.FIREWORK_ROCKET)
            );
            player.level.addFreshEntity(firework);
            String message = this.getConfig().message.replace("${player}", player.getDisplayName().getString()).replace("${title}", this.getConfig().title);
            ServerLifecycleHooks.getCurrentServer()
               .getPlayerList()
               .broadcastMessage(new TextComponent(message).withStyle(ChatFormatting.GREEN), ChatType.CHAT, Util.NIL_UUID);
         }
      }
   }

   public static class Config extends ConfiguredTask.Config {
      private String title;
      private String message;
      private ResourceLocation icon;

      public Config() {
      }

      public Config(String title, String message, ResourceLocation icon) {
         this.title = title;
         this.message = message;
         this.icon = icon;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.UTF_8.writeBits(this.title, buffer);
         Adapters.UTF_8.writeBits(this.message, buffer);
         Adapters.IDENTIFIER.writeBits(this.icon, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.title = Adapters.UTF_8.readBits(buffer).orElseThrow();
         this.message = Adapters.UTF_8.readBits(buffer).orElseThrow();
         this.icon = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.UTF_8.writeNbt(this.title).ifPresent(value -> nbt.put("title", value));
            Adapters.UTF_8.writeNbt(this.message).ifPresent(value -> nbt.put("message", value));
            Adapters.IDENTIFIER.writeNbt(this.icon).ifPresent(value -> nbt.put("icon", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.title = Adapters.UTF_8.readNbt(nbt.get("title")).orElse("");
         this.message = Adapters.UTF_8.readNbt(nbt.get("message")).orElse("");
         this.icon = Adapters.IDENTIFIER.readNbt(nbt.get("icon")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.UTF_8.writeJson(this.title).ifPresent(value -> json.add("title", value));
            Adapters.UTF_8.writeJson(this.message).ifPresent(value -> json.add("message", value));
            Adapters.IDENTIFIER.writeJson(this.icon).ifPresent(value -> json.add("icon", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.title = Adapters.UTF_8.readJson(json.get("title")).orElse("");
         this.message = Adapters.UTF_8.readJson(json.get("message")).orElse("");
         this.icon = Adapters.IDENTIFIER.readJson(json.get("icon")).orElse(VaultMod.id("empty"));
      }
   }
}
