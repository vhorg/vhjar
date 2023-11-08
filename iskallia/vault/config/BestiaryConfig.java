package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.core.vault.objective.scavenger.MobScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.calc.SoulChanceHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class BestiaryConfig extends Config {
   @Expose
   private HashMap<ResourceLocation, DescriptionData> groupDescriptions;
   @Expose
   private List<BestiaryConfig.EntityEntry> entities;
   @Expose
   private List<ResourceLocation> hiddenGroups;

   @Override
   public String getName() {
      return "bestiary";
   }

   @Override
   protected void reset() {
      this.groupDescriptions = new HashMap<>();
      this.groupDescriptions.put(VaultMod.id("horde"), DescriptionData.getDefault("horde"));
      this.groupDescriptions.put(VaultMod.id("assassin"), DescriptionData.getDefault("assassin"));
      this.entities = new ArrayList<>();
      this.entities
         .add(
            new BestiaryConfig.EntityEntry(
               new ResourceLocation("zombie"),
               DescriptionData.getDefault("zombie"),
               100.0F,
               List.of("Theme 1", "Theme 2"),
               0,
               List.of(
                  new BestiaryConfig.EntityDrop(Items.IRON_INGOT.getDefaultInstance(), new IntRangeEntry(0, 1), new IntRangeEntry(0, -1)),
                  new BestiaryConfig.EntityDrop(Items.CARROT.getDefaultInstance(), new IntRangeEntry(0, 1), new IntRangeEntry(0, -1))
               )
            )
         );
      ItemStack bow = Items.BOW.getDefaultInstance();
      bow = EnchantmentHelper.enchantItem(new Random(), bow, 30, true);
      this.entities
         .add(
            new BestiaryConfig.EntityEntry(
               new ResourceLocation("skeleton"),
               DescriptionData.getDefault("skeleton"),
               200.0F,
               List.of("Theme 3", "Theme 4"),
               0,
               List.of(
                  new BestiaryConfig.EntityDrop(Items.BONE.getDefaultInstance(), new IntRangeEntry(1, 2), new IntRangeEntry(0, 10)),
                  new BestiaryConfig.EntityDrop(Items.ARROW.getDefaultInstance(), new IntRangeEntry(0, 1), new IntRangeEntry(0, -1)),
                  new BestiaryConfig.EntityDrop(bow, new IntRangeEntry(0, 1), new IntRangeEntry(0, -1))
               )
            )
         );
      this.hiddenGroups = List.of(VaultMod.id("skeleton"), VaultMod.id("zombie"));
   }

   public List<BestiaryConfig.EntityEntry> getEntities() {
      return this.entities;
   }

   public List<ResourceLocation> getHiddenGroups() {
      return this.hiddenGroups;
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<BestiaryConfig.EntityEntry> getEntityEntry(EntityType<?> entityType) {
      for (BestiaryConfig.EntityEntry entry : this.getEntities()) {
         ResourceLocation entityId = entityType.getRegistryName();
         if (entityId != null && entityId.equals(entry.getEntityId())) {
            return Optional.of(updateWithExistingValues(entry, entityId));
         }
      }

      return Optional.empty();
   }

   @OnlyIn(Dist.CLIENT)
   private static BestiaryConfig.EntityEntry updateWithExistingValues(BestiaryConfig.EntityEntry entry, ResourceLocation entityId) {
      BestiaryConfig.EntityEntry copy = new BestiaryConfig.EntityEntry(
         entityId, entry.descriptionData, entry.vaultExp, entry.themes, entry.minLevel, entry.drops
      );
      copy.setVaultExp(
         ModConfigs.VAULT_STATS.getMobsKilled().getOrDefault(entityId, ModConfigs.VAULT_STATS.getMobsKilled().get(new ResourceLocation("default")))
      );
      List<BestiaryConfig.EntityDrop> drops = new ArrayList<>(copy.getDrops());
      drops.addAll(getSoulShardDrops(entityId));
      drops.addAll(getScavDrops(entityId));
      copy.setDrops(drops);
      return copy;
   }

   @OnlyIn(Dist.CLIENT)
   private static Optional<Player> getLocalPlayer() {
      return Optional.ofNullable(Minecraft.getInstance().player);
   }

   @OnlyIn(Dist.CLIENT)
   private static List<BestiaryConfig.EntityDrop> getSoulShardDrops(ResourceLocation entityId) {
      List<BestiaryConfig.EntityDrop> defaultList = List.of(
         new BestiaryConfig.EntityDrop(ModItems.SOUL_SHARD.getDefaultInstance(), new IntRangeEntry(0, 1), new IntRangeEntry(-1, -1))
      );
      Optional<Player> playerOptional = getLocalPlayer();
      if (playerOptional.isEmpty()) {
         return defaultList;
      } else {
         EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(entityId);
         Level level = Minecraft.getInstance().level;
         if (type != null && level != null) {
            Entity entity = type.create(level);
            if (entity == null) {
               return defaultList;
            } else {
               Player player = playerOptional.get();
               float chanceMultiplier = SoulChanceHelper.getSoulChance(player);
               float chance = 1.0F + (chanceMultiplier - -0.3F);
               SoulShardConfig.DropRange dropRange = ModConfigs.SOUL_SHARD.getDropRange(entity);
               int amount = (int)Math.ceil(chance * dropRange.getMax());
               BestiaryConfig.EntityDrop shardDrop = new BestiaryConfig.EntityDrop(
                  ModItems.SOUL_SHARD.getDefaultInstance(), new IntRangeEntry(dropRange.getMin(), amount), new IntRangeEntry(-1, -1)
               );
               return List.of(shardDrop);
            }
         } else {
            return defaultList;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<BestiaryConfig.EntityDrop> getScavDrops(ResourceLocation entityId) {
      for (ScavengeTask task : ModConfigs.SCAVENGER.getTasks()) {
         if (task instanceof MobScavengerTask) {
            MobScavengerTask mobScavengerTask = (MobScavengerTask)task;

            for (MobScavengerTask.Entry entry : mobScavengerTask.entries) {
               if (entry.group.contains(entityId)) {
                  return List.of(
                     new BestiaryConfig.EntityDrop(entry.item, new IntRangeEntry(0, 1), new IntRangeEntry(0, (int)(mobScavengerTask.probability * 100.0)))
                  );
               }
            }
         }
      }

      return List.of();
   }

   public DescriptionData getGroupDescription(ResourceLocation groupId) {
      return this.groupDescriptions.getOrDefault(groupId, DescriptionData.getDefault(groupId.getPath()));
   }

   public static final class EntityDrop {
      @Expose
      private ItemStack stack;
      @Expose
      private IntRangeEntry amount;
      @Expose
      private IntRangeEntry probability;

      public EntityDrop(ItemStack item, IntRangeEntry amount, IntRangeEntry probability) {
         this.stack = item;
         this.amount = amount;
         this.probability = probability;
      }

      public ItemStack getStack() {
         return this.stack;
      }

      public IntRangeEntry getAmount() {
         return this.amount;
      }

      public IntRangeEntry getProbability() {
         return this.probability;
      }
   }

   public static final class EntityEntry {
      @Expose
      private ResourceLocation entityId;
      @Expose
      private DescriptionData descriptionData;
      private float vaultExp;
      @Expose
      private List<String> themes;
      @Expose
      private int minLevel;
      @Expose
      List<BestiaryConfig.EntityDrop> drops;
      public static final BestiaryConfig.EntityEntry MISSING = new BestiaryConfig.EntityEntry(
         new ResourceLocation("missing"), DescriptionData.getDefault("missing"), 9000.0F, List.of("Missing 1", "Missing 2"), 666, List.of()
      );

      public EntityEntry(
         ResourceLocation entityId, DescriptionData descriptionData, float vaultExp, List<String> themes, int minLevel, List<BestiaryConfig.EntityDrop> drops
      ) {
         this.entityId = entityId;
         this.descriptionData = descriptionData;
         this.vaultExp = vaultExp;
         this.themes = themes;
         this.minLevel = minLevel;
         this.drops = drops;
      }

      public static BestiaryConfig.EntityEntry getDefault(ResourceLocation entityId) {
         BestiaryConfig.EntityEntry entry = new BestiaryConfig.EntityEntry(
            entityId, MISSING.descriptionData, MISSING.vaultExp, MISSING.themes, MISSING.minLevel, MISSING.drops
         );
         entry.setEntityId(entityId);
         return BestiaryConfig.updateWithExistingValues(entry, entityId);
      }

      public BestiaryConfig.EntityEntry setEntityId(ResourceLocation entityId) {
         this.entityId = entityId;
         return this;
      }

      public BestiaryConfig.EntityEntry setVaultExp(float vaultExp) {
         this.vaultExp = vaultExp;
         return this;
      }

      public BestiaryConfig.EntityEntry setDrops(List<BestiaryConfig.EntityDrop> drops) {
         this.drops = drops;
         return this;
      }

      public ResourceLocation getEntityId() {
         return this.entityId;
      }

      public MutableComponent getDescriptionData() {
         return this.descriptionData.getComponent();
      }

      public float getVaultExp() {
         return this.vaultExp;
      }

      public List<String> getThemes() {
         return this.themes;
      }

      public int getMinLevel() {
         return this.minLevel;
      }

      public List<BestiaryConfig.EntityDrop> getDrops() {
         return this.drops;
      }
   }
}
