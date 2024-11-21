package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.TooltipFlag;

public class CardDeck implements ISerializable<CompoundTag, JsonObject> {
   private UUID uuid;
   private final Map<CardPos, Card> cards;

   public CardDeck() {
      this.cards = new LinkedHashMap<>();
   }

   public CardDeck(Map<CardPos, Card> cards) {
      this.cards = new LinkedHashMap<>(cards);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setUuid(UUID uuid) {
      this.uuid = uuid;
   }

   public Set<CardPos> getSlots() {
      return this.cards.keySet();
   }

   public Map<CardPos, Card> getCards() {
      return Collections.unmodifiableMap(this.cards);
   }

   public Optional<Card> getCard(CardPos pos) {
      return Optional.ofNullable(this.cards.get(pos));
   }

   public void setCard(CardPos pos, Card card) {
      this.cards.put(pos, card);
   }

   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      this.getSnapshotAttributes().forEach(instance -> {
         MutableComponent text = instance.getAttribute().getReader().getDisplay((VaultGearAttributeInstance<?>)instance, VaultGearModifier.AffixType.PREFIX);
         tooltip.add(text);
      });
      this.cards.forEach((pos, card) -> {
         if (card != null) {
            for (CardEntry entry : card.getEntries()) {
               if (entry.getModifier() instanceof TaskLootCardModifier modifier) {
                  modifier.addText(tooltip, minIndex, flag, time, card.getTier());
               }
            }
         }
      });
   }

   public List<VaultGearAttributeInstance<?>> getSnapshotAttributes() {
      List<VaultGearAttributeInstance<?>> attributes = new ArrayList<>();
      Map<VaultGearAttribute<?>, List<Object>> merged = new LinkedHashMap<>();
      this.getCards().forEach((pos, card) -> {
         if (card != null) {
            for (CardEntry entry : card.getEntries()) {
               entry.getSnapshotAttributes(card.getTier(), pos, this).forEach(instance -> {
                  List<Object> values = merged.computeIfAbsent(instance.getAttribute(), k -> new ArrayList<>());
                  VaultGearAttributeComparator comparator = instance.getAttribute().getAttributeComparator();
                  if (comparator != null && !values.isEmpty()) {
                     for (int i = 0; i < values.size(); i++) {
                        Object result = comparator.merge(values.get(i), instance.getValue()).orElse(null);
                        if (result != null) {
                           values.set(i, result);
                           return;
                        }
                     }

                     values.add(instance.getValue());
                  } else {
                     values.add(instance.getValue());
                  }
               });
            }
         }
      });
      merged.forEach((attribute, values) -> {
         for (Object value : values) {
            attributes.add(new VaultGearAttributeInstance<>((VaultGearAttribute<?>)attribute, value));
         }
      });
      return attributes;
   }

   public CardPos getMinSlot() {
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;

      for (CardPos pos : this.cards.keySet()) {
         if (pos.x < minX) {
            minX = pos.x;
         }

         if (pos.y < minY) {
            minY = pos.y;
         }
      }

      return new CardPos(minX, minY);
   }

   public CardPos getMaxSlot() {
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (CardPos pos : this.cards.keySet()) {
         if (pos.x > maxX) {
            maxX = pos.x;
         }

         if (pos.y > maxY) {
            maxY = pos.y;
         }
      }

      return new CardPos(maxX, maxY);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.UUID.writeBits(this.uuid, buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.cards.size()), buffer);

      for (int i = 0; i < this.cards.size(); i++) {
         this.cards.forEach((pos, card) -> {
            CardPos.ADAPTER.writeBits(pos, buffer);
            Card.ADAPTER.writeBits(card, buffer);
         });
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.cards.clear();

      for (int i = 0; i < size; i++) {
         this.cards.put(CardPos.ADAPTER.readBits(buffer).orElseThrow(), Card.ADAPTER.readBits(buffer).orElse(null));
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
         ListTag cards = new ListTag();
         this.cards.forEach((pos, card) -> {
            CompoundTag entry = new CompoundTag();
            CardPos.ADAPTER.writeNbt(pos).ifPresent(tag -> entry.put("pos", tag));
            Card.ADAPTER.writeNbt(card).ifPresent(tag -> entry.put("card", tag));
            cards.add(entry);
         });
         nbt.put("cards", cards);
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseGet(Mth::createInsecureUUID);
      ListTag cards = nbt.getList("cards", 10);

      for (int i = 0; i < cards.size(); i++) {
         CompoundTag entry = cards.getCompound(i);
         this.cards.put(CardPos.ADAPTER.readNbt(entry.get("pos")).orElse(null), Card.ADAPTER.readNbt(entry.getCompound("card")).orElseThrow());
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(nbt -> {
         Adapters.UUID.writeJson(this.uuid).ifPresent(tag -> nbt.add("uuid", tag));
         JsonArray cards = new JsonArray();
         this.cards.forEach((pos, card) -> {
            JsonObject entry = new JsonObject();
            CardPos.ADAPTER.writeJson(pos).ifPresent(tag -> entry.add("pos", tag));
            Card.ADAPTER.writeJson(card).ifPresent(tag -> entry.add("card", tag));
            cards.add(entry);
         });
         nbt.add("cards", cards);
         return (JsonObject)nbt;
      });
   }

   public void readJson(JsonObject json) {
      this.uuid = Adapters.UUID.readJson(json.get("uuid")).orElseGet(Mth::createInsecureUUID);
      JsonArray cards = json.getAsJsonArray("cards");

      for (int i = 0; i < cards.size(); i++) {
         JsonObject entry = cards.get(i).getAsJsonObject();
         this.cards.put(CardPos.ADAPTER.readJson(entry.get("pos")).orElse(null), Card.ADAPTER.readJson(entry.getAsJsonObject("card")).orElseThrow());
      }
   }
}
