package iskallia.vault.core.vault.influence;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;

public class LegacyInfluences extends DataObject<LegacyInfluences> {
   private static final Map<VaultGod, LegacyInfluences.InfluenceMessages> MESSAGES = new HashMap<>();
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Void> INITIALIZED = FieldKey.of("initialized", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class).with(Version.v1_0, Adapters.UUID, DISK.all()).register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_JOIN.register(this, data -> {
         if (!this.has(PLAYER)) {
            if (data.getListener() instanceof Runner) {
               this.set(PLAYER, data.getListener().get(Listener.ID));
            }
         }
      });
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      if (!this.has(INITIALIZED) && vault.get(Vault.CLOCK).get(TickClock.GLOBAL_TIME) > 200) {
         this.generateInfluences(world, vault, this.get(PLAYER));
         this.set(INITIALIZED);
      }
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }

   private void generateInfluences(VirtualWorld world, Vault vault, UUID uuid) {
   }

   public Object2IntMap<VaultModifier<?>> generateModifiers(int level, VaultGod godType, int favours, RandomSource random) {
      Object2IntMap<VaultModifier<?>> modifiers = new Object2IntOpenHashMap();
      boolean positive = favours >= 0;
      int rolls = Math.abs(favours) / 4;
      ResourceLocation id = VaultMod.id("influences_" + godType.getSerializedName() + "_" + (positive ? "positive" : "negative"));

      for (int i = 0; i < rolls; i++) {
         for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(id, level, random)) {
            modifiers.put(modifier, modifiers.getOrDefault(modifier, 0) + 1);
         }
      }

      return modifiers;
   }

   private void printGodMessage(Vault vault, VaultGod godType, int favours, Object2IntMap<VaultModifier<?>> modifiers, RandomSource random) {
      if (!modifiers.isEmpty()) {
         String message = favours >= 0 ? MESSAGES.get(godType).getPositiveMessage(random) : MESSAGES.get(godType).getNegativeMessage(random);
         MutableComponent vgName = new TextComponent(godType.getName()).withStyle(godType.getChatColor());
         vgName.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, godType.getHoverChatComponent())));
         MutableComponent txt = new TextComponent("");
         txt.append(new TextComponent("[VG] ").withStyle(ChatFormatting.DARK_PURPLE))
            .append(vgName)
            .append(new TextComponent(": ").withStyle(ChatFormatting.WHITE))
            .append(new TextComponent(message));

         for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
            listener.getPlayer().ifPresent(player -> {
               player.sendMessage(txt, Util.NIL_UUID);
               modifiers.forEach((modifier, count) -> {
                  MutableComponent info = new TextComponent(modifier.getDisplayDescriptionFormatted(count)).withStyle(ChatFormatting.DARK_GRAY);
                  player.sendMessage(info, Util.NIL_UUID);
               });
            });
         }
      }
   }

   static {
      LegacyInfluences.InfluenceMessages benevolent = new LegacyInfluences.InfluenceMessages();
      benevolent.positiveMessages.add("Our domain's ground will carve a path.");
      benevolent.positiveMessages.add("Tread upon our domain with care and it will respond in kind.");
      benevolent.positiveMessages.add("May your desire blossom into a wildfire.");
      benevolent.positiveMessages.add("Creation bends to our will.");
      benevolent.negativeMessages.add("Nature rises against you.");
      benevolent.negativeMessages.add("Prosperity withers at your touch.");
      benevolent.negativeMessages.add("Defile, rot, decay and fester.");
      benevolent.negativeMessages.add("The flower of your aspirations will waste away.");
      MESSAGES.put(VaultGod.VELARA, benevolent);
      LegacyInfluences.InfluenceMessages omniscient = new LegacyInfluences.InfluenceMessages();
      omniscient.positiveMessages.add("May foresight guide your step.");
      omniscient.positiveMessages.add("Careful planning and strategy may lead you.");
      omniscient.positiveMessages.add("A set choice; followed through and flawlessly executed.");
      omniscient.positiveMessages.add("Chance's hand may favour your goals.");
      omniscient.negativeMessages.add("A choice; leading one to disfavour.");
      omniscient.negativeMessages.add("Riches, Wealth, Prosperity. An illusion.");
      omniscient.negativeMessages.add("Cascading eventuality. Solidified in ruin.");
      omniscient.negativeMessages.add("Diminishing reality.");
      MESSAGES.put(VaultGod.TENOS, omniscient);
      LegacyInfluences.InfluenceMessages timekeeper = new LegacyInfluences.InfluenceMessages();
      timekeeper.positiveMessages.add("Seize the opportunity.");
      timekeeper.positiveMessages.add("A single instant, stretched to infinity.");
      timekeeper.positiveMessages.add("Your future glows golden with possibility.");
      timekeeper.positiveMessages.add("Hasten and value every passing moment.");
      timekeeper.negativeMessages.add("Eternity in the moment of standstill.");
      timekeeper.negativeMessages.add("Drown in the flow of time.");
      timekeeper.negativeMessages.add("Transience manifested.");
      timekeeper.negativeMessages.add("Immutable emptiness.");
      MESSAGES.put(VaultGod.WENDARR, timekeeper);
      LegacyInfluences.InfluenceMessages malevolence = new LegacyInfluences.InfluenceMessages();
      malevolence.positiveMessages.add("Enforce your path through obstacles.");
      malevolence.positiveMessages.add("Our vigor may aid your conquest.");
      malevolence.positiveMessages.add("Cherish this mote of my might.");
      malevolence.positiveMessages.add("A tempest incarnate.");
      malevolence.negativeMessages.add("Feel our domain's wrath.");
      malevolence.negativeMessages.add("Malice and spite given form.");
      malevolence.negativeMessages.add("Flee before the growing horde.");
      malevolence.negativeMessages.add("Perish from your own ambition.");
      MESSAGES.put(VaultGod.IDONA, malevolence);
   }

   private static class InfluenceMessages {
      private final List<String> positiveMessages = new ArrayList<>();
      private final List<String> negativeMessages = new ArrayList<>();

      private String getNegativeMessage(RandomSource random) {
         return this.negativeMessages.get(random.nextInt(this.negativeMessages.size()));
      }

      private String getPositiveMessage(RandomSource random) {
         return this.positiveMessages.get(random.nextInt(this.positiveMessages.size()));
      }
   }
}
