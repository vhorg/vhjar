package iskallia.vault.world.vault.event;

import iskallia.vault.world.vault.VaultRaid;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.Event;

public class VaultEvent<T extends Event> implements INBTSerializable<CompoundNBT> {
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

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Id", this.getId().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.id = new ResourceLocation(nbt.func_74779_i("Id"));
   }

   public static <T extends Event> VaultEvent<T> register(ResourceLocation id, Class<T> type, BiConsumer<VaultRaid, T> onEvent) {
      VaultEvent<T> listener = new VaultEvent<>(id, type, onEvent);
      REGISTRY.put(id, listener);
      return listener;
   }

   public static VaultEvent<?> fromNBT(CompoundNBT nbt) {
      return REGISTRY.get(new ResourceLocation(nbt.func_74779_i("Id")));
   }
}
