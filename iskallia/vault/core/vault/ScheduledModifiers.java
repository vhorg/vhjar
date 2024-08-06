package iskallia.vault.core.vault;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.attribute.talent.RandomVaultModifierAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ScheduledModifiers implements ISerializable<CompoundTag, JsonObject> {
   private final List<ScheduledModifiers.Entry> cache = new ArrayList<>();

   public void onJoin(Vault vault, ServerPlayer player) {
      RandomSource random = JavaRandom.ofScrambled(player.getUUID().getLeastSignificantBits() ^ vault.get(Vault.ID).getMostSignificantBits());
      int logicalTime = vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      int displayTime = vault.get(Vault.CLOCK).get(TickClock.DISPLAY_TIME);
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);

      for (RandomVaultModifierAttribute attribute : snapshot.getAttributeValue(ModGearAttributes.RANDOM_VAULT_MODIFIER, VaultGearAttributeTypeMerger.asList())) {
         int activationTime = random.nextInt(logicalTime + displayTime);
         this.cache.add(new ScheduledModifiers.Entry(attribute.getModifier(), attribute.getCount(), attribute.getTime(), activationTime));
      }
   }

   public void onTick(VirtualWorld world, Vault vault, ServerPlayer player) {
      int logicalTime = vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      RandomSource random = JavaRandom.ofScrambled(player.getUUID().getLeastSignificantBits() ^ vault.get(Vault.ID).getMostSignificantBits() ^ logicalTime);
      random.nextLong();
      this.cache
         .removeIf(
            entry -> {
               if (entry.activationTime == logicalTime) {
                  VaultModifierRegistry.getOpt(entry.modifier)
                     .ifPresent(
                        modifier -> {
                           vault.get(Vault.MODIFIERS).addModifier((VaultModifier<?>)modifier, entry.count, true, random, context -> {
                              if (entry.timeLeft != null) {
                                 context.set(ModifierContext.TICKS_LEFT, entry.timeLeft);
                              }
                           });
                           Component text;
                           if (entry.timeLeft != null) {
                              text = new TextComponent("")
                                 .append(player.getDisplayName())
                                 .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
                                 .append(modifier.getChatDisplayNameComponent(entry.count))
                                 .append(new TextComponent(" for ").withStyle(ChatFormatting.GRAY))
                                 .append(new TextComponent(entry.timeLeft / 20 + " seconds"))
                                 .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
                           } else {
                              text = new TextComponent("")
                                 .append(player.getDisplayName())
                                 .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
                                 .append(modifier.getChatDisplayNameComponent(entry.count))
                                 .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
                           }

                           for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                              listener.getPlayer().ifPresent(other -> {
                                 world.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                                 other.displayClientMessage(text, false);
                              });
                           }
                        }
                     );
                  return true;
               } else {
                  return false;
               }
            }
         );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.cache.size()), buffer);

      for (ScheduledModifiers.Entry entry : this.cache) {
         entry.writeBits(buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.cache.clear();

      for (int i = 0; i < size; i++) {
         ScheduledModifiers.Entry entry = new ScheduledModifiers.Entry();
         entry.readBits(buffer);
         this.cache.add(entry);
      }
   }

   public static class Entry implements ISerializable<CompoundTag, JsonObject> {
      private ResourceLocation modifier;
      private int count;
      private Integer timeLeft;
      private int activationTime;

      public Entry() {
      }

      public Entry(ResourceLocation modifier, int count, Integer timeLeft, int activationTime) {
         this.modifier = modifier;
         this.count = count;
         this.timeLeft = timeLeft;
         this.activationTime = activationTime;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.IDENTIFIER.writeBits(this.modifier, buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.count), buffer);
         Adapters.INT_SEGMENTED_7.asNullable().writeBits(this.timeLeft, buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.activationTime), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.modifier = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
         this.count = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.timeLeft = Adapters.INT_SEGMENTED_7.asNullable().readBits(buffer).orElseThrow();
         this.activationTime = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      }
   }
}
