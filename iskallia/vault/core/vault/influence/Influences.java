package iskallia.vault.core.vault.influence;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.PlayerInfluences;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;

public class Influences extends DataObject<Influences> {
   private static final Map<VaultGod, List<String>> MESSAGES = new HashMap<>();
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Void> INITIALIZED = FieldKey.of("initialized", Void.class).with(Version.v1_5, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<VaultGod> CURRENT = FieldKey.of("current", VaultGod.class)
      .with(Version.v1_5, Adapters.ofEnum(VaultGod.class, EnumAdapter.Mode.ORDINAL), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Favours> FAVOURS = FieldKey.of("favours", Favours.class)
      .with(Version.v1_5, CompoundAdapter.of(Favours::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault, Runner runner) {
      this.ifPresent(FAVOURS, favours -> favours.initServer(world, vault));
      CommonEvents.ALTAR_PROGRESS.in(world).register(this, data -> {
         if (data.isConsuming() && data.getPlayer().getUUID().equals(runner.getId())) {
            ChunkRandom random = ChunkRandom.ofInternal(vault.get(Vault.SEED));
            long a = random.nextLong() | 1L;
            long b = random.nextLong() | 1L;
            long c = random.nextLong() | 1L;
            int x = data.getPos().getX();
            int y = data.getPos().getY();
            int z = data.getPos().getZ();
            random.setSeed(a * x + b * y + c * z ^ vault.get(Vault.SEED));
            VaultGod god = data.getBlockEntity().getVaultGod();
            int playerLevel = PlayerVaultStatsData.get(world).getVaultStats(data.getPlayer().getUUID()).getVaultLevel();
            int diff = playerLevel - vault.get(Vault.LEVEL).get();
            if (diff <= 5) {
               PlayerInfluences.attemptFavour(data.getPlayer(), data.getBlockEntity().getVaultGod(), random);
            }

            data.getBlockEntity().placeReward(data.getWorld(), data.getPos().above(), god.getColor(), random);
         }
      });
   }

   public void tickServer(VirtualWorld world, Vault vault, Runner runner) {
      this.ifPresent(FAVOURS, favours -> favours.tickServer(world, vault));
      if (!this.has(INITIALIZED) && vault.get(Vault.CLOCK).get(TickClock.GLOBAL_TIME) > 200) {
         this.initialize(world, vault, runner);
         this.set(INITIALIZED);
      }
   }

   public void releaseServer() {
      this.ifPresent(FAVOURS, Modifiers::releaseServer);
   }

   public void onLeave(VirtualWorld world, Vault vault, Runner runner) {
      if (this.has(CURRENT)) {
         vault.getOptional(Vault.STATS).map(stats -> stats.get(runner)).ifPresent(stats -> {
            if (stats.getCompletion() == Completion.COMPLETED) {
               PlayerInfluences.addReputation(runner.getId(), this.get(CURRENT), 1);
            }
         });
      }
   }

   public void initialize(VirtualWorld world, Vault vault, Runner runner) {
      PlayerInfluences.consumeFavour(runner.getId()).ifPresent(god -> this.set(CURRENT, god));
      if (this.has(CURRENT)) {
         int reputation = PlayerInfluences.getReputation(runner.getId(), this.get(CURRENT));
         this.set(FAVOURS, new Favours(runner.getId(), reputation));
         this.get(FAVOURS).initServer(world, vault);
         RandomSource random = JavaRandom.ofInternal(vault.get(Vault.SEED) ^ runner.getId().getLeastSignificantBits());
         ResourceLocation id = VaultMod.id(this.get(CURRENT).getName().toLowerCase() + "_favours");
         Object2IntMap<VaultModifier<?>> modifiers = new Object2IntOpenHashMap();
         ModConfigs.VAULT_MODIFIER_POOLS
            .getRandom(id, reputation, random)
            .forEach(modifier -> modifiers.put(modifier, modifiers.getOrDefault(modifier, 0) + 1));
         modifiers.forEach((modifier, count) -> this.get(FAVOURS).addPermanentModifier(modifier, count, true, random));
         this.printGodMessage(runner, modifiers, random);
      }
   }

   private void printGodMessage(Runner runner, Object2IntMap<VaultModifier<?>> modifiers, RandomSource random) {
      if (!modifiers.isEmpty()) {
         VaultGod god = this.get(CURRENT);
         String message = MESSAGES.get(god).get(random.nextInt(MESSAGES.get(god).size()));
         MutableComponent vgName = new TextComponent(god.getName()).withStyle(god.getChatColor());
         vgName.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, god.getHoverChatComponent())));
         MutableComponent txt = new TextComponent("");
         txt.append(new TextComponent("[VG] ").withStyle(ChatFormatting.DARK_PURPLE))
            .append(vgName)
            .append(new TextComponent(": ").withStyle(ChatFormatting.WHITE))
            .append(new TextComponent(message));
         runner.getPlayer().ifPresent(player -> {
            player.sendMessage(txt, Util.NIL_UUID);
            modifiers.forEach((modifier, count) -> {
               MutableComponent info = new TextComponent(modifier.getDisplayDescriptionFormatted(count)).withStyle(ChatFormatting.DARK_GRAY);
               player.sendMessage(info, Util.NIL_UUID);
            });
         });
      }
   }

   static {
      List<String> velara = new ArrayList<>();
      velara.add("Our domain's ground will carve a path.");
      velara.add("Tread upon our domain with care and it will respond in kind.");
      velara.add("May your desire blossom into a wildfire.");
      velara.add("Creation bends to our will.");
      MESSAGES.put(VaultGod.VELARA, velara);
      List<String> tenos = new ArrayList<>();
      tenos.add("May foresight guide your step.");
      tenos.add("Careful planning and strategy may lead you.");
      tenos.add("A set choice; followed through and flawlessly executed.");
      tenos.add("Chance's hand may favour your goals.");
      MESSAGES.put(VaultGod.TENOS, tenos);
      List<String> wendarr = new ArrayList<>();
      wendarr.add("Seize the opportunity.");
      wendarr.add("A single instant, stretched to infinity.");
      wendarr.add("Your future glows golden with possibility.");
      wendarr.add("Hasten and value every passing moment.");
      MESSAGES.put(VaultGod.WENDARR, wendarr);
      List<String> idona = new ArrayList<>();
      idona.add("Enforce your path through obstacles.");
      idona.add("Our vigor may aid your conquest.");
      idona.add("Cherish this mote of my might.");
      idona.add("A tempest incarnate.");
      MESSAGES.put(VaultGod.IDONA, idona);
   }
}
