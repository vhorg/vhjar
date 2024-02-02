package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.DirectAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class Modifiers extends DataObject<Modifiers> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   protected static final FieldKey<Modifiers.Entry.List> ENTRIES = FieldKey.of("entries", Modifiers.Entry.List.class)
      .with(Version.v1_0, CompoundAdapter.of(Modifiers.Entry.List::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public Modifiers() {
      this.set(ENTRIES, new Modifiers.Entry.List());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public Modifiers addModifier(VaultModifier<?> modifier, int amount, boolean display, RandomSource random) {
      return this.addModifier(modifier, amount, display, random, context -> {});
   }

   public Modifiers addModifier(VaultModifier<?> modifier, int amount, boolean display, RandomSource random, Consumer<ModifierContext> configurator) {
      for (int i = 0; i < amount; i++) {
         modifier.flatten(display, random).forEach(entry -> {
            configurator.accept(entry.getContext());
            this.get(ENTRIES).add(entry);
         });
      }

      return this;
   }

   public ModifierContext getContext(Modifiers.Entry entry) {
      return entry.get(Modifiers.Entry.CONTEXT).copy();
   }

   public Stream<ModifierContext> getContexts(VaultModifier<?> modifier) {
      return this.get(ENTRIES).stream().filter(entry -> entry.getModifier().orElse(null) == modifier).map(Modifiers.Entry::getContext);
   }

   public java.util.List<VaultModifier<?>> getModifiers() {
      java.util.List<VaultModifier<?>> modifiers = new ArrayList<>();

      for (Modifiers.Entry entry : this.get(ENTRIES)) {
         entry.getModifier().ifPresent(modifiers::add);
      }

      return modifiers;
   }

   public java.util.List<Modifiers.Entry> getEntries() {
      return this.get(ENTRIES);
   }

   public void onListenerAdd(VirtualWorld world, Vault vault, Listener listener) {
      for (Modifiers.Entry entry : this.get(ENTRIES)) {
         entry.getModifier().ifPresent(modifier -> modifier.onListenerAdd(world, vault, this.getContext(entry), listener));
      }
   }

   public void onListenerRemove(VirtualWorld world, Vault vault, Listener listener) {
      for (Modifiers.Entry entry : this.get(ENTRIES)) {
         entry.getModifier().ifPresent(modifier -> modifier.onListenerRemove(world, vault, this.getContext(entry), listener));
      }
   }

   public Object2IntMap<VaultModifier<?>> getDisplayGroup() {
      Object2IntMap<VaultModifier<?>> map = new Object2IntOpenHashMap();

      for (Modifiers.Entry entry : this.get(ENTRIES)) {
         if (entry.has(Modifiers.Entry.DISPLAY)) {
            entry.getModifier().ifPresent(modifier -> map.put(modifier, map.getOrDefault(modifier, 0) + 1));
         }
      }

      return map;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      for (Modifiers.Entry entry : this.get(ENTRIES)) {
         if (entry.has(Modifiers.Entry.CONSUMED)) {
            entry.getModifier().ifPresent(modifier -> modifier.initServer(world, vault, this.getContext(entry)));
         } else {
            entry.getModifier().ifPresent(modifier -> {
               modifier.onVaultAdd(world, vault, this.getContext(entry));
               vault.ifPresent(Vault.LISTENERS, listeners -> {
                  for (Listener listener : listeners.getAll()) {
                     modifier.onListenerAdd(world, vault, this.getContext(entry), listener);
                  }
               });
            });
            entry.set(Modifiers.Entry.CONSUMED);
         }
      }
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(ENTRIES).removeIf(entry -> {
         if (!entry.hasExpired()) {
            return false;
         } else if (!entry.has(Modifiers.Entry.CONSUMED)) {
            return true;
         } else {
            entry.getModifier().ifPresent(modifier -> {
               modifier.onVaultRemove(world, vault, this.getContext(entry));
               vault.ifPresent(Vault.LISTENERS, listeners -> {
                  for (Listener listener : listeners.getAll()) {
                     modifier.onListenerRemove(world, vault, this.getContext(entry), listener);
                  }
               });
            });
            return true;
         }
      });
      this.get(ENTRIES).forEach(entry -> {
         if (!entry.has(Modifiers.Entry.CONSUMED)) {
            entry.getModifier().ifPresent(modifier -> {
               modifier.onVaultAdd(world, vault, this.getContext(entry));
               vault.ifPresent(Vault.LISTENERS, listeners -> {
                  for (Listener listener : listeners.getAll()) {
                     modifier.onListenerAdd(world, vault, this.getContext(entry), listener);
                  }
               });
            });
            entry.set(Modifiers.Entry.CONSUMED);
         }
      });
      this.get(ENTRIES).forEach(Modifiers.Entry::tick);
   }

   public void releaseServer() {
      this.get(ENTRIES).forEach(entry -> entry.getModifier().ifPresent(modifier -> modifier.releaseServer(this.getContext(entry))));
   }

   public static class Entry extends DataObject<Modifiers.Entry> {
      public static final FieldRegistry FIELDS = new FieldRegistry();
      public static final FieldKey<ResourceLocation> MODIFIER = FieldKey.of("modifier", ResourceLocation.class)
         .with(
            Version.v1_0,
            new DirectAdapter<>((value, buffer, context) -> buffer.writeIdentifier(value), (buffer, context) -> Optional.of(buffer.readIdentifier())),
            DISK.all().or(CLIENT.all())
         )
         .register(FIELDS);
      public static final FieldKey<ModifierContext> CONTEXT = FieldKey.of("context", ModifierContext.class)
         .with(Version.v1_0, CompoundAdapter.of(ModifierContext::new), DISK.all().or(CLIENT.all()))
         .register(FIELDS);
      public static final FieldKey<Void> DISPLAY = FieldKey.of("display", Void.class)
         .with(Version.v1_0, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
         .register(FIELDS);
      public static final FieldKey<Void> CONSUMED = FieldKey.of("consumed", Void.class)
         .with(Version.v1_0, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
         .register(FIELDS);

      private Entry() {
      }

      public Entry(VaultModifier<?> modifier, boolean display) {
         this.set(MODIFIER, modifier.getId());
         this.set(CONTEXT, new ModifierContext().set(ModifierContext.UUID, UUID.randomUUID()));
         this.setIf(DISPLAY, () -> display);
      }

      public Entry(VaultModifier<?> modifier, int timeLeft, boolean display) {
         this.set(MODIFIER, modifier.getId());
         this.set(CONTEXT, new ModifierContext().set(ModifierContext.UUID, UUID.randomUUID()).set(ModifierContext.TICKS_LEFT, Integer.valueOf(timeLeft)));
         this.setIf(DISPLAY, () -> display);
      }

      @Override
      public FieldRegistry getFields() {
         return FIELDS;
      }

      public ModifierContext getContext() {
         return this.get(CONTEXT);
      }

      public Optional<VaultModifier<?>> getModifier() {
         return VaultModifierRegistry.getOpt(this.get(MODIFIER));
      }

      public boolean hasExpired() {
         return this.get(CONTEXT).hasExpired();
      }

      public void tick() {
         if (this.get(CONTEXT).has(ModifierContext.TICKS_LEFT)) {
            this.get(CONTEXT).modify(ModifierContext.TICKS_LEFT, i -> i - 1);
         }
      }

      public static class List extends DataList<Modifiers.Entry.List, Modifiers.Entry> {
         public List() {
            super(new ArrayList<>(), CompoundAdapter.of(Modifiers.Entry::new));
         }
      }
   }
}
