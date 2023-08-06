package iskallia.vault.entity.champion;

import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.network.message.ClientboundChampionMessage;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;

@EventBusSubscriber
public class ChampionLogic {
   private static final String AFFIXES_TAG = "affixes";
   private final List<IChampionAffix> affixes = new ArrayList<>();
   public static final String CHAMPION_TAG = "vault_champion";

   public static boolean isChampion(Entity entity) {
      return entity instanceof LivingEntity livingEntity && isChampion(livingEntity);
   }

   public static boolean isChampion(LivingEntity entity) {
      return entity.getTags().contains("vault_champion");
   }

   public static ChampionLogic deserialize(CompoundTag tag) {
      ChampionLogic championLogic = new ChampionLogic();
      if (tag.contains("affixes")) {
         CompoundTag affixesNbt = tag.getCompound("affixes");
         affixesNbt.getAllKeys().forEach(key -> {
            CompoundTag affixCompound = affixesNbt.getCompound(key);
            ChampionAffixRegistry.deserialize(affixCompound).ifPresent(championLogic::addAffix);
         });
      }

      return championLogic;
   }

   @SubscribeEvent
   public static void onEntityAttack(LivingAttackEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity livingEntity && isChampion(livingEntity) && event.getEntityLiving() instanceof Player player) {
         runIfChampion(
            livingEntity,
            championLogic -> championLogic.runOnAffixesThatImplement(
               IChampionOnHitAffix.class, affix -> affix.onChampionHitPlayer(livingEntity, player, event.getAmount())
            )
         );
      }
   }

   @SubscribeEvent
   public static void onEntityDrops(LivingDropsEvent event) {
      if (isChampion(event.getEntityLiving()) && event.getSource().getEntity() instanceof ServerPlayer player) {
         ServerVaults.get(player.getLevel()).ifPresent(vault -> {
            int level = vault.get(Vault.LEVEL).get();
            LegacyLootTablesConfig.Level levelLootTables = ModConfigs.LOOT_TABLES.getForLevel(level);
            if (levelLootTables != null) {
               String lootTableId = levelLootTables.CHAMPION;
               LootTableGenerator generator = new LootTableGenerator(Version.latest(), VaultRegistry.LOOT_TABLE.getKey(lootTableId), 0.0F);
               generator.generate(JavaRandom.ofNanoTime());
               LivingEntity champion = event.getEntityLiving();
               generator.getItems().forEachRemaining(item -> {
                  VaultLevelItem.doInitializeVaultLoot(item, vault, event.getEntityLiving().blockPosition());
                  item = DataTransferItem.doConvertStack(item);
                  DataInitializationItem.doInitialize(item);
                  event.getDrops().add(new ItemEntity(champion.level, champion.getX(), champion.getY(), champion.getZ(), item));
               });
            }
         });
      }
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (isChampion(event.getTarget())
         && event.getTarget() instanceof ChampionLogic.IChampionLogicHolder championLogicHolder
         && event.getTarget() instanceof LivingEntity livingEntity
         && event.getPlayer() instanceof ServerPlayer player) {
         ChampionLogic championLogic = championLogicHolder.getChampionLogic();
         championLogic.syncClientData(livingEntity, PacketDistributor.PLAYER.with(() -> player));
      }
   }

   public Optional<CompoundTag> serialize() {
      if (this.affixes.isEmpty()) {
         return Optional.empty();
      } else {
         CompoundTag affixesNbt = new CompoundTag();
         this.affixes.forEach(affix -> affixesNbt.put(affix.getType(), affix.serialize()));
         CompoundTag ret = new CompoundTag();
         ret.put("affixes", affixesNbt);
         return Optional.of(ret);
      }
   }

   private static void runIfChampion(Entity entity, Consumer<ChampionLogic> consumer) {
      if (entity instanceof ChampionLogic.IChampionLogicHolder championLogicHolder) {
         consumer.accept(championLogicHolder.getChampionLogic());
      }
   }

   private <T extends IChampionAffix> void runOnAffixesThatImplement(Class<T> clazz, Consumer<T> consumer) {
      this.getAffixes().forEach(affix -> {
         if (clazz.isInstance(affix)) {
            consumer.accept((T)affix);
         }
      });
   }

   public void tick(Entity entity) {
      if (entity instanceof LivingEntity livingEntity && isChampion(livingEntity)) {
         runIfChampion(entity, championLogic -> championLogic.runOnAffixesThatImplement(IChampionTickableAffix.class, affix -> affix.tick(livingEntity)));
      }
   }

   public void addAffix(IChampionAffix affix) {
      this.affixes.add(affix);
   }

   public List<IChampionAffix> getAffixes() {
      return this.affixes;
   }

   public void setAffixes(List<IChampionAffix> affixes) {
      this.affixes.clear();
      this.affixes.addAll(affixes);
   }

   public void syncClientData(LivingEntity entity, PacketTarget target) {
      ModNetwork.CHANNEL.send(target, new ClientboundChampionMessage(entity.getId(), this.affixes));
   }

   public interface IChampionLogicHolder {
      ChampionLogic getChampionLogic();
   }
}