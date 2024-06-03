package iskallia.vault.item.crystal.properties;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.item.crystal.CrystalEntry;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class CrystalProperties extends CrystalEntry implements ISerializable<CompoundTag, JsonObject> {
   protected UUID vaultId = null;
   protected Integer level = null;
   protected boolean unmodifiable = false;

   public UUID getVaultId() {
      return this.vaultId;
   }

   public Optional<Integer> getLevel() {
      return Optional.ofNullable(this.level);
   }

   public boolean isUnmodifiable() {
      return this.unmodifiable;
   }

   public CrystalProperties setVaultId(UUID vaultId) {
      this.vaultId = vaultId;
      return this;
   }

   public CrystalProperties setLevel(Integer level) {
      this.level = level;
      return this;
   }

   public CrystalProperties setUnmodifiable(boolean unmodifiable) {
      this.unmodifiable = unmodifiable;
      return this;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = Math.max(this.getLevel().orElse(0), 0);
      vault.set(Vault.LEVEL, new VaultLevel().set(VaultLevel.VALUE, Integer.valueOf(level)));
      super.configure(vault, random);
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      if (this.level == null && entity instanceof ServerPlayer serverPlayer && entity.getServer() != null) {
         this.level = PlayerVaultStatsData.get(entity.getServer()).getVaultStats(serverPlayer).getVaultLevel();
      }

      super.onInventoryTick(world, entity, slot, selected);
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      if (this.level == null) {
         tooltip.add(minIndex, new TextComponent("Level: ???").withStyle(ChatFormatting.GRAY));
      } else {
         tooltip.add(minIndex, new TextComponent("Level: ").append(new TextComponent(this.level + "").setStyle(Style.EMPTY.withColor(11583738))));
      }

      super.addText(tooltip, minIndex, flag, time);
      if (this.unmodifiable) {
         tooltip.add(new TextComponent("Unmodifiable").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(11027010))));
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UUID.writeNbt(this.vaultId).ifPresent(vaultId -> nbt.put("vault_id", vaultId));
      Adapters.INT.writeNbt(this.level).ifPresent(level -> nbt.put("level", level));
      Adapters.BOOLEAN.writeNbt(this.unmodifiable).ifPresent(unmodifiable -> nbt.put("exhausted", unmodifiable));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.vaultId = Adapters.UUID.readNbt(nbt.get("vault_id")).orElse(null);
      this.level = Adapters.INT.readNbt(nbt.get("level")).orElse(null);
      this.unmodifiable = Adapters.BOOLEAN.readNbt(nbt.get("exhausted")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.UUID.writeJson(this.vaultId).ifPresent(vaultId -> json.add("vault_id", vaultId));
      Adapters.INT.writeJson(this.level).ifPresent(level -> json.add("level", level));
      Adapters.BOOLEAN.writeJson(this.unmodifiable).ifPresent(unmodifiable -> json.add("exhausted", unmodifiable));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.vaultId = Adapters.UUID.readJson(json.get("vault_id")).orElse(null);
      this.level = Adapters.INT.readJson(json.get("level")).orElse(null);
      this.unmodifiable = Adapters.BOOLEAN.readJson(json.get("exhausted")).orElse(false);
   }
}
