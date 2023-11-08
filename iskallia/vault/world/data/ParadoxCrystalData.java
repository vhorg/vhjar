package iskallia.vault.world.data;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import iskallia.vault.network.message.UpdateParadoxDataMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ParadoxCrystalData extends SavedData {
   protected static final String DATA_NAME = "the_vault_CrystalParadox";
   public static ParadoxCrystalData CLIENT = new ParadoxCrystalData();
   private final Map<UUID, ParadoxCrystalData.Entry> entries = new HashMap<>();

   private ParadoxCrystalData() {
   }

   private ParadoxCrystalData(CompoundTag tag) {
      this.load(tag);
   }

   public boolean isDirty() {
      return true;
   }

   public ParadoxCrystalData.Entry getOrCreate(UUID uuid) {
      return this.entries.computeIfAbsent(uuid, uuid1 -> new ParadoxCrystalData.Entry());
   }

   public void putAll(Map<UUID, ParadoxCrystalData.Entry> entries) {
      this.entries.putAll(entries);
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ModNetwork.CHANNEL
            .sendTo(
               new UpdateParadoxDataMessage(get(ServerLifecycleHooks.getCurrentServer()).entries),
               player.connection.connection,
               NetworkDirection.PLAY_TO_CLIENT
            );
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         Map<UUID, ParadoxCrystalData.Entry> changed = new HashMap<>();
         get(ServerLifecycleHooks.getCurrentServer()).entries.forEach((uuid, entry) -> {
            if (entry.changed) {
               changed.put(uuid, entry);
               entry.changed = false;
            }
         });
         if (!changed.isEmpty()) {
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateParadoxDataMessage(changed));
         }
      }
   }

   public CompoundTag save(CompoundTag nbt) {
      this.entries.forEach((id, entry) -> entry.writeNbt().ifPresent(tag -> nbt.put(id.toString(), tag)));
      return nbt;
   }

   public void load(CompoundTag nbt) {
      this.entries.clear();
      nbt.getAllKeys().forEach(id -> {
         CompoundTag tag = nbt.getCompound(id);
         ParadoxCrystalData.Entry entry = new ParadoxCrystalData.Entry();
         entry.readNbt(tag);
         this.entries.put(UUID.fromString(id), entry);
      });
   }

   public static ParadoxCrystalData.Entry getEntry(UUID uuid) {
      return EffectiveSide.get() == LogicalSide.SERVER ? get(ServerLifecycleHooks.getCurrentServer()).getOrCreate(uuid) : CLIENT.getOrCreate(uuid);
   }

   public static ParadoxCrystalData get(ServerLevel level) {
      return get(level.getServer());
   }

   public static ParadoxCrystalData get(MinecraftServer server) {
      return (ParadoxCrystalData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(ParadoxCrystalData::new, ParadoxCrystalData::new, "the_vault_CrystalParadox");
   }

   public static class Entry implements ISerializable<CompoundTag, JsonObject> {
      public long seed;
      public long unlockTime;
      public StructurePreset preset;
      public List<VaultModifierStack> modifiers = new ArrayList<>();
      public boolean changed;

      public Entry() {
         this.reset();
      }

      public void reset() {
         this.seed = new Random().nextLong();
         this.unlockTime = 0L;
         this.preset = null;
         this.modifiers.clear();
         this.changed = true;
      }

      public void mergeModifiers(List<VaultModifierStack> modifiers) {
         Map<VaultModifier<?>, Integer> map = new LinkedHashMap<>();

         for (VaultModifierStack modifier : this.modifiers) {
            map.put(modifier.getModifier(), map.getOrDefault(modifier.getModifier(), 0) + modifier.getSize());
         }

         for (VaultModifierStack modifier : modifiers) {
            map.put(modifier.getModifier(), map.getOrDefault(modifier.getModifier(), 0) + modifier.getSize());
         }

         this.modifiers.clear();

         for (Map.Entry<VaultModifier<?>, Integer> entry : map.entrySet()) {
            this.modifiers.add(VaultModifierStack.of(entry.getKey(), entry.getValue()));
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.LONG.writeNbt(Long.valueOf(this.seed)).ifPresent(tag -> nbt.put("seed", tag));
         Adapters.LONG.writeNbt(Long.valueOf(this.unlockTime)).ifPresent(tag -> nbt.put("unlock_time", tag));
         Adapters.STRUCTURE_PRESET.writeNbt(this.preset).ifPresent(tag -> nbt.put("preset", tag));
         ListTag list = new ListTag();

         for (VaultModifierStack modifier : this.modifiers) {
            list.add(modifier.serializeNBT());
         }

         nbt.put("modifiers", list);
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.seed = Adapters.LONG.readNbt(nbt.get("seed")).orElse(0L);
         this.unlockTime = Adapters.LONG.readNbt(nbt.get("unlock_time")).orElse(0L);
         this.preset = Adapters.STRUCTURE_PRESET.readNbt((CompoundTag)nbt.get("preset")).orElse(null);
         this.modifiers = new ArrayList<>();
         ListTag list = nbt.getList("modifiers", 10);

         for (int i = 0; i < list.size(); i++) {
            this.modifiers.add(VaultModifierStack.of(list.getCompound(i)));
         }
      }
   }
}
