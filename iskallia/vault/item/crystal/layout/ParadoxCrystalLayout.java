package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPresetLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.template.data.DirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.item.crystal.layout.preset.ParadoxTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import iskallia.vault.world.data.ParadoxCrystalData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ParadoxCrystalLayout extends ClassicInfiniteCrystalLayout {
   private UUID playerUuid;

   public ParadoxCrystalLayout() {
      super(1);
   }

   public void setPlayerUuid(UUID playerUuid) {
      this.playerUuid = playerUuid;
   }

   public StructurePreset getPreset() {
      StructurePreset preset = null;
      if (this.playerUuid != null) {
         preset = ParadoxCrystalData.getEntry(this.playerUuid).preset;
      }

      if (preset == null) {
         preset = new StructurePreset()
            .put(
               RegionPos.ORIGIN,
               new PoolTemplatePreset(
                  VaultLayout.PieceType.ROOM,
                  new TemplatePool()
                     .addLeaf(
                        new DirectTemplateEntry(
                           VaultMod.id("vault/starts/personal_vault_start1"),
                           Arrays.asList(VaultMod.id("generic/gate_placeholder"), VaultMod.id("gate_lock/base"))
                        ),
                        1.0
                     )
               )
            );
      }

      return preset;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      if (vault.has(Vault.WORLD)) {
         vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
            if (generator instanceof GridGenerator grid) {
               grid.set(GridGenerator.LAYOUT, new ClassicPresetLayout(this.tunnelSpan, this.getPreset()));
            }
         });
      }
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Preset").withStyle(Style.EMPTY.withColor(13882323))));
      Map<VaultGod, Integer> godCounts = new HashMap<>();
      this.getPreset().getAll().forEach((regionPos, preset) -> {
         if (preset instanceof ParadoxTemplatePreset paradoxTemplatePreset) {
            VaultGod godx = paradoxTemplatePreset.getGod();
            if (godx != null) {
               godCounts.put(godx, godCounts.getOrDefault(godx, 0) + 1);
            }
         }
      });

      for (VaultGod god : VaultGod.values()) {
         int count = godCounts.getOrDefault(god, 0);
         String roomStr = count == 1 ? "Room" : "Rooms";
         Component txt = new TextComponent(" • ")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(this.playerUuid == null ? "?" : String.valueOf(count)).withStyle(ChatFormatting.GRAY))
            .append(" ")
            .append(new TextComponent(god.getName()).withStyle(god.getChatColor()))
            .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
         tooltip.add(txt);
      }
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      if (entity instanceof ServerPlayer player) {
         this.playerUuid = player.getGameProfile().getId();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UUID.writeNbt(this.playerUuid).ifPresent(uuid -> nbt.put("player_uuid", uuid));
      return Optional.of(nbt);
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      this.playerUuid = Adapters.UUID.readNbt(nbt.get("player_uuid")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.UUID.writeJson(this.playerUuid).ifPresent(uuid -> json.add("player_uuid", uuid));
      return Optional.of(json);
   }

   @Override
   public void readJson(JsonObject json) {
      this.playerUuid = Adapters.UUID.readJson(json.get("player_uuid")).orElse(null);
   }
}
