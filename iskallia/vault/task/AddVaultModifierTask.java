package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.init.ModConfigs;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class AddVaultModifierTask extends ConsumableTask<AddVaultModifierTask.Config> {
   public AddVaultModifierTask() {
      super(new AddVaultModifierTask.Config());
   }

   public AddVaultModifierTask(AddVaultModifierTask.Config config) {
      super(config);
   }

   @Override
   protected void onConsume(TaskContext context) {
      if (context.getVault() != null) {
         List<VaultModifier<?>> modifiers = new ArrayList<>();
         this.getConfig().modifiers.forEach((id, count) -> {
            if (id.charAt(0) == '@') {
               ResourceLocation pool = ResourceLocation.tryParse(id.substring(1));

               for (int i = 0; i < count; i++) {
                  modifiers.addAll(ModConfigs.VAULT_MODIFIER_POOLS.getRandom(pool, context.getLevel(), context.getSource().getRandom()));
               }
            } else {
               for (int i = 0; i < count; i++) {
                  VaultModifierRegistry.getOpt(ResourceLocation.tryParse(id)).ifPresent(modifiers::add);
               }
            }
         });
         Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
         modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
         ObjectIterator<Entry<VaultModifier<?>>> it = groups.object2IntEntrySet().iterator();
         groups.forEach((modifier, count) -> context.getVault().get(Vault.MODIFIERS).addModifier(modifier, count, true, context.getSource().getRandom()));
         if (this.getConfig().message != null && !groups.isEmpty()) {
            TextComponent modifierNames = new TextComponent("");

            while (it.hasNext()) {
               Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
               modifierNames.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
               if (it.hasNext()) {
                  modifierNames.append(new TextComponent(", "));
               }
            }

            Component message = this.replace(this.getConfig().message, "${modifiers}", modifierNames);

            for (Listener listener : context.getVault().get(Vault.LISTENERS).getAll()) {
               listener.getPlayer()
                  .ifPresent(
                     player -> {
                        player.displayClientMessage(message, false);
                        player.level
                           .playSound(
                              null, player, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.MASTER, 1.0F, 0.75F + player.level.getRandom().nextFloat() * 0.25F
                           );
                     }
                  );
            }
         }
      }
   }

   public Component replace(Component component, String target, Component replacement) {
      if (!(component instanceof TextComponent base)) {
         return component;
      } else {
         List<Component> siblings = base.getSiblings();
         siblings.add(0, base.plainCopy().setStyle(base.getStyle()));

         for (int result = 0; result < siblings.size(); result++) {
            Component sibling = siblings.get(result);
            if (sibling instanceof TextComponent) {
               String text = ((TextComponent)sibling).getText();
               if (!text.isEmpty()) {
                  List<Component> parts = new ArrayList<>();
                  if (text.equals(target)) {
                     parts.add(replacement);
                  } else {
                     for (String raw : text.split(Pattern.quote(target))) {
                        parts.add(new TextComponent(raw).setStyle(sibling.getStyle()));
                        parts.add(replacement);
                     }

                     parts.remove(parts.size() - 1);
                  }

                  siblings.remove(result);

                  for (int j = 0; j < parts.size(); j++) {
                     siblings.add(result, parts.get(parts.size() - j - 1));
                  }
               }
            }
         }

         TextComponent resultx = new TextComponent("");
         resultx.setStyle(base.getStyle());

         for (Component sibling : siblings) {
            resultx.append(sibling);
         }

         return resultx;
      }
   }

   public static class Config extends ConfiguredTask.Config {
      public Map<String, Integer> modifiers;
      public Component message;

      public Config() {
         this.modifiers = new LinkedHashMap<>();
      }

      public Config(Map<String, Integer> modifiers) {
         this.modifiers = modifiers;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.modifiers.size()), buffer);
         this.modifiers.forEach((id, count) -> {
            Adapters.UTF_8.writeBits(id, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(count, buffer);
         });
         Adapters.COMPONENT.writeBits(this.message, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.modifiers.clear();

         for (int i = 0; i < size; i++) {
            this.modifiers.put(Adapters.UTF_8.readBits(buffer).orElseThrow(), Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());
         }

         this.message = Adapters.COMPONENT.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            CompoundTag modifiers = new CompoundTag();
            this.modifiers.forEach((id, count) -> Adapters.INT.writeNbt(count).ifPresent(tag -> modifiers.put("id", tag)));
            nbt.put("modifiers", modifiers);
            Adapters.COMPONENT.writeNbt(this.message).ifPresent(tag -> nbt.put("message", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.modifiers.clear();
         if (nbt.get("modifiers") instanceof CompoundTag compound) {
            for (String key : compound.getAllKeys()) {
               Adapters.INT.readNbt(compound.get(key)).ifPresent(count -> this.modifiers.put(key, count));
            }
         }

         this.message = Adapters.COMPONENT.readNbt(nbt.get("message")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            JsonObject modifiers = new JsonObject();
            this.modifiers.forEach((id, count) -> Adapters.INT.writeJson(count).ifPresent(tag -> modifiers.add("id", tag)));
            modifiers.add("modifiers", modifiers);
            Adapters.COMPONENT.writeJson(this.message).ifPresent(tag -> json.add("message", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.modifiers.clear();
         if (json.get("modifiers") instanceof JsonObject object) {
            for (String key : object.keySet()) {
               Adapters.INT.readJson(object.get(key)).ifPresent(count -> this.modifiers.put(key, count));
            }
         }

         this.message = Adapters.COMPONENT.readJson(json.get("message")).orElse(null);
      }
   }
}
