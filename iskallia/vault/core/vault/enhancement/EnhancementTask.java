package iskallia.vault.core.vault.enhancement;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.util.TextComponentUtils;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public abstract class EnhancementTask<C extends EnhancementTask.Config<?>> implements ISerializable<CompoundTag, JsonObject> {
   protected C config;
   protected UUID vault;
   protected UUID player;
   protected UUID altar;

   public EnhancementTask() {
   }

   public EnhancementTask(C config, UUID vault, UUID player, UUID altar) {
      this.config = config;
      this.vault = vault;
      this.player = player;
      this.altar = altar;
   }

   public UUID getVault() {
      return this.vault;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public UUID getAltar() {
      return this.altar;
   }

   public abstract void initServer(MinecraftServer var1);

   public abstract void releaseServer();

   public abstract boolean isFinished();

   public abstract Component getProgressComponent();

   public Component getDisplay(CommandSourceStack stack) {
      return this.config.formatDisplay(stack, this.getProgressComponent());
   }

   public boolean belongsTo(Vault vault) {
      return vault != null && vault.has(Vault.ID) && vault.get(Vault.ID).equals(this.vault);
   }

   public boolean belongsTo(Entity player) {
      return player != null && player.getUUID().equals(this.player);
   }

   public boolean belongsTo(UUID player) {
      return player != null && player.equals(this.player);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UUID.writeNbt(this.vault).ifPresent(tag -> nbt.put("vault", tag));
      Adapters.UUID.writeNbt(this.player).ifPresent(tag -> nbt.put("player", tag));
      Adapters.UUID.writeNbt(this.altar).ifPresent(tag -> nbt.put("altar", tag));
      Adapters.ENHANCEMENT_CONFIG.writeNbt(this.config).ifPresent(tag -> nbt.put("config", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.vault = Adapters.UUID.readNbt(nbt.get("vault")).orElse(null);
      this.player = Adapters.UUID.readNbt(nbt.get("player")).orElse(null);
      this.altar = Adapters.UUID.readNbt(nbt.get("altar")).orElse(null);
      this.config = (C)Adapters.ENHANCEMENT_CONFIG.readNbt(nbt.getCompound("config")).orElse(null);
   }

   public abstract static class Config<T extends EnhancementTask<?>> implements ISerializable<CompoundTag, JsonObject> {
      private String display;

      public Config() {
      }

      public Config(String display) {
         this.display = display;
      }

      public abstract T create(Vault var1, Player var2, VaultEnhancementAltarTileEntity var3, RandomSource var4);

      public Component formatDisplay(CommandSourceStack stack, Component progressStr) {
         MutableComponent cmp = Serializer.fromJsonLenient(this.display);
         return (Component)(cmp == null ? new TextComponent("!ERROR!") : TextComponentUtils.replace(stack, cmp, "%s", progressStr));
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("display", this.display);
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.display = nbt.getString("display");
      }
   }
}
