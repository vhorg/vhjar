package iskallia.vault.core.vault.player;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.modifier.RelicExtension;
import iskallia.vault.core.vault.time.modifier.TrinketExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.event.event.VaultJoinForgeEvent;
import iskallia.vault.event.event.VaultLeaveForgeEvent;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.VaultTimeExtensionTrinket;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.world.data.VaultPartyData;
import iskallia.vault.world.data.VaultPlayerStats;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClassicListenersLogic extends ListenersLogic {
   public static final SupplierKey<ListenersLogic> KEY = SupplierKey.of("classic", ListenersLogic.class).with(Version.v1_0, ClassicListenersLogic::new);
   public static final FieldRegistry FIELDS = ListenersLogic.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> MAX_PLAYERS = FieldKey.of("max_players", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> NATURAL_REGEN = FieldKey.of("natural_regen", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> ADDED_BONUS_TIME = FieldKey.of("added_bonus_time", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<UUIDList> LEAVERS = FieldKey.of("leavers", UUIDList.class)
      .with(Version.v1_0, Adapter.ofCompound(UUIDList::create), DISK.all())
      .register(FIELDS);

   public ClassicListenersLogic() {
      this.set(LEAVERS, UUIDList.create());
   }

   @Override
   public SupplierKey<ListenersLogic> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.PLAYER_REGEN.register(this, data -> {
         Listener listener = vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID());
         if (listener instanceof Runner && !this.has(NATURAL_REGEN)) {
            data.setAmount(0.0F);
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> {
         ServerPlayer player = data.getListener().getPlayer().orElse(null);
         if (data.getVault() == vault && data.getListener() instanceof Runner && player != null) {
            StatCollector stats = vault.get(Vault.STATS).get(data.getListener().get(Listener.ID));
            if (stats != null) {
               Completion completion = stats.getCompletion();

               TextComponent prefix = new TextComponent(switch (completion) {
                  case COMPLETED -> " completed the Vault!";
                  case BAILED -> " survived.";
                  case FAILED -> " was defeated.";
               });
               prefix.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
               MutableComponent playerName = player.getDisplayName().copy();
               playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
               world.getServer().getPlayerList().broadcastMessage(playerName.append(prefix), ChatType.CHAT, player.getUUID());
            }
         }
      });
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault, Map<UUID, Listener> listeners) {
      List<Listener> runners = listeners.values().stream().filter(listenerx -> listenerx instanceof Runner).toList();
      this.keepInVault(world, vault, runners);
      if (runners.stream().noneMatch(Listener::isOnline)) {
         vault.ifPresent(Vault.CLOCK, clock -> clock.set(TickClock.PAUSED));
      } else {
         vault.ifPresent(Vault.CLOCK, clock -> clock.remove(TickClock.PAUSED));
      }

      if (runners.isEmpty()) {
         for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
            vault.get(Vault.LISTENERS).remove(world, vault, listener);
         }
      }

      if (vault.get(Vault.LISTENERS).getAll().isEmpty()) {
         world.markForDeletion();
         vault.set(Vault.FINISHED);
         vault.releaseServer();
      }
   }

   @Override
   public void releaseServer() {
      CommonEvents.release(this);
   }

   @Override
   public boolean onJoin(VirtualWorld world, Vault vault, Listener listener) {
      if (vault.get(Vault.LISTENERS).contains(listener.get(Listener.ID))) {
         return false;
      } else {
         if (this.has(MAX_PLAYERS)) {
            long active = vault.get(Vault.LISTENERS).getAll().stream().filter(l -> l instanceof Runner).count();
            if (active >= this.get(MAX_PLAYERS).intValue() && listener instanceof Runner) {
               listener.getPlayer()
                  .ifPresent(
                     player -> player.displayClientMessage(new TextComponent("This vault has reached max capacity.").withStyle(ChatFormatting.RED), true)
                  );
               return false;
            }
         }

         if (this.has(LEAVERS) && this.get(LEAVERS).contains(listener.get(Listener.ID))) {
            listener.getPlayer()
               .ifPresent(player -> player.displayClientMessage(new TextComponent("You cannot re-enter this vault.").withStyle(ChatFormatting.RED), true));
            return false;
         } else {
            if (vault.has(Vault.OWNER) && world.getGameRules().getBoolean(ModGameRules.VAULT_JOIN_REQUIRE_PARTY)) {
               VaultPartyData.Party party = VaultPartyData.get(world).getParty(vault.get(Vault.OWNER)).orElse(null);
               if (party == null || !party.hasMember(listener.get(Listener.ID))) {
                  listener.getPlayer()
                     .ifPresent(
                        player -> player.displayClientMessage(
                           new TextComponent("This vault is reserved for allowed party members.").withStyle(ChatFormatting.RED), true
                        )
                     );
                  return false;
               }
            }

            vault.set(Vault.OWNER, listener.get(Listener.ID));
            if (!this.has(ADDED_BONUS_TIME) && listener instanceof Runner) {
               vault.get(Vault.CLOCK).addModifier(new RelicExtension(listener.get(Listener.ID), 600));
               this.set(ADDED_BONUS_TIME);
            }

            listener.getPlayer().ifPresent(player -> {
               TrinketHelper.getTrinkets(player).forEach(trinket -> {
                  if (trinket.isUsable(player)) {
                     TrinketItem.addUsedVault(trinket.stack(), vault.get(Vault.ID));
                  }
               });
               TrinketHelper.getTrinkets(player, VaultTimeExtensionTrinket.class).forEach(timeTrinket -> {
                  if (timeTrinket.isUsable(player)) {
                     vault.get(Vault.CLOCK).addModifier(new TrinketExtension(player, timeTrinket.trinket().getConfig().getTimeAdded()));
                  }
               });
            });
            return true;
         }
      }
   }

   @Override
   protected void onTeleport(VirtualWorld world, Vault vault, ServerPlayer player) {
      MinecraftForge.EVENT_BUS.post(new VaultJoinForgeEvent(vault));
      TextComponent title = new TextComponent("The Vault");
      title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
      MutableComponent subtitle = new TextComponent("Good luck, ").append(player.getName()).append(new TextComponent("!"));
      subtitle.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
      ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
      ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
      player.connection.send(titlePacket);
      player.connection.send(subtitlePacket);
      this.printJoinMessage(world, vault, player);
   }

   private void printJoinMessage(VirtualWorld world, Vault vault, ServerPlayer player) {
      TextComponent text = new TextComponent("");
      AtomicBoolean startsWithVowel = new AtomicBoolean(false);
      ObjectIterator<Entry<VaultModifier<?>>> it = vault.get(Vault.MODIFIERS).getDisplayGroup().object2IntEntrySet().iterator();

      while (it.hasNext()) {
         Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
         text.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
         if (it.hasNext()) {
            text.append(new TextComponent(", "));
         } else {
            text.append(new TextComponent(" "));
         }
      }

      TextComponent prefix = new TextComponent(startsWithVowel.get() ? " entered an " : " entered a ");
      text.append("Vault").append("!");
      prefix.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
      text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
      MutableComponent playerName = player.getDisplayName().copy();
      playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
      world.getServer().getPlayerList().broadcastMessage(playerName.append(prefix).append(text), ChatType.CHAT, player.getUUID());
   }

   @Override
   public boolean onLeave(VirtualWorld world, Vault vault, Listener listener) {
      return listener.getPlayer().map(player -> {
         if (!player.isDeadOrDying()) {
            this.recallToJoinState(Stream.of(listener));
         }

         VaultPlayerStats.addStats(player.getUUID(), vault.get(Vault.ID));
         MinecraftForge.EVENT_BUS.post(new VaultLeaveForgeEvent(player, vault));
         this.get(LEAVERS).add(player.getUUID());
         return true;
      }).orElse(false);
   }
}
