package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AscensionObjective;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import iskallia.vault.item.tool.ColorBlender;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AscensionCrystalObjective extends CrystalObjective {
   protected int stacks;
   protected String playerName;
   protected UUID playerUuid;
   protected Tag modifiers;

   public AscensionCrystalObjective() {
   }

   public AscensionCrystalObjective(int stacks, String playerName, UUID playerUuid, CrystalModifiers modifiers) {
      this.stacks = stacks;
      this.playerName = playerName;
      this.playerUuid = playerUuid;
      this.modifiers = CrystalData.MODIFIERS.writeNbt(modifiers).orElse(null);
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.add(AscensionObjective.create(this.stacks, this.playerName, this.playerUuid, this.modifiers)));
   }

   @Override
   public Optional<Integer> getColor(float time) {
      ColorBlender blender = new ColorBlender(1.0F).add(12713983, 60.0F).add(7535612, 60.0F).add(1699570, 60.0F);
      return Optional.of(blender.getColor(time));
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Ascension: "));
      if (this.playerUuid == null) {
         tooltip.add(
            new TextComponent("")
               .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent("Player: ???").withStyle(ChatFormatting.GRAY))
         );
      } else {
         tooltip.add(
            new TextComponent("")
               .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent("Player: "))
               .append(new TextComponent(this.playerName == null ? "Unknown" : this.playerName).withStyle(ChatFormatting.YELLOW))
         );
      }

      tooltip.add(
         new TextComponent("")
            .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("Stacks: "))
            .append(new TextComponent(String.valueOf(this.stacks)).withStyle(ChatFormatting.AQUA))
      );
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      if (entity instanceof ServerPlayer player) {
         this.playerName = player.getGameProfile().getName();
         this.playerUuid = player.getGameProfile().getId();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.INT.writeNbt(Integer.valueOf(this.stacks)).ifPresent(tag -> nbt.put("stack", tag));
      Adapters.UTF_8.writeNbt(this.playerName).ifPresent(tag -> nbt.put("player_name", tag));
      Adapters.UUID.writeNbt(this.playerUuid).ifPresent(tag -> nbt.put("player_uuid", tag));
      Adapters.GENERIC_NBT.writeNbt(this.modifiers).ifPresent(tag -> nbt.put("modifiers", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.stacks = Adapters.INT.readNbt(nbt.get("stack")).orElse(0);
      this.playerName = Adapters.UTF_8.readNbt(nbt.get("player_name")).orElse(null);
      this.playerUuid = Adapters.UUID.readNbt(nbt.get("player_uuid")).orElse(null);
      this.modifiers = Adapters.GENERIC_NBT.readNbt(nbt.get("modifiers")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.INT.writeJson(Integer.valueOf(this.stacks)).ifPresent(tag -> json.add("stack", tag));
      Adapters.UTF_8.writeJson(this.playerName).ifPresent(tag -> json.add("player_name", tag));
      Adapters.UUID.writeJson(this.playerUuid).ifPresent(tag -> json.add("player_uuid", tag));
      Adapters.GENERIC_NBT.writeJson(this.modifiers).ifPresent(tag -> json.add("modifiers", tag));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.stacks = Adapters.INT.readJson(json.get("stack")).orElse(0);
      this.playerName = Adapters.UTF_8.readJson(json.get("player_name")).orElse(null);
      this.playerUuid = Adapters.UUID.readJson(json.get("player_uuid")).orElse(null);
      this.modifiers = Adapters.GENERIC_NBT.readJson(json.get("modifiers")).orElse(null);
   }
}
