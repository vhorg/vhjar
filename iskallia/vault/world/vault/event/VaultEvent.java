package iskallia.vault.world.vault.event;

import iskallia.vault.world.vault.VaultRaid;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.Event;

public class VaultEvent<T extends Event> implements INBTSerializable<CompoundTag> {
   public static final Map<ResourceLocation, VaultEvent<?>> REGISTRY = new HashMap<>();
   private ResourceLocation id;
   private Class<T> type;
   private BiConsumer<VaultRaid, T> onEvent;

   protected VaultEvent() {
   }

   protected VaultEvent(ResourceLocation id, Class<T> type, BiConsumer<VaultRaid, T> onEvent) {
      this.id = id;
      this.type = type;
      this.onEvent = onEvent;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Class<T> getType() {
      return this.type;
   }

   protected void accept(VaultRaid vault, Event event) {
      this.onEvent.accept(vault, (T)event);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("Id"));
   }

   public static <T extends Event> VaultEvent<T> register(ResourceLocation id, Class<T> type, BiConsumer<VaultRaid, T> onEvent) {
      VaultEvent<T> listener = new VaultEvent<>(id, type, onEvent);
      REGISTRY.put(id, listener);
      return listener;
   }

   public static VaultEvent<?> fromNBT(CompoundTag nbt) {
      return REGISTRY.get(new ResourceLocation(nbt.getString("Id")));
   }
}
