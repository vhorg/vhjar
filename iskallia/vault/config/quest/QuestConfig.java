package iskallia.vault.config.quest;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.quest.base.InVaultQuest;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.quest.type.AnvilQuest;
import iskallia.vault.quest.type.BlockInteractionQuest;
import iskallia.vault.quest.type.BountyCompleteQuest;
import iskallia.vault.quest.type.CollectionQuest;
import iskallia.vault.quest.type.CraftingQuest;
import iskallia.vault.quest.type.EnterVaultQuest;
import iskallia.vault.quest.type.LevelUpQuest;
import iskallia.vault.quest.type.MiningQuest;
import iskallia.vault.quest.type.SurviveQuest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class QuestConfig extends Config {
   @Expose
   private List<Quest> quests = new ArrayList<>();

   public Collection<Quest> getQuests() {
      return ImmutableList.copyOf(this.quests);
   }

   @Override
   public String getName() {
      return "quest/quests";
   }

   protected void collection(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      CollectionQuest quest = new CollectionQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void crafting(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      CraftingQuest quest = new CraftingQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void survive(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      SurviveQuest quest = new SurviveQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void enterVault(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      EnterVaultQuest quest = new EnterVaultQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void anvil(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      AnvilQuest quest = new AnvilQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void mining(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      MiningQuest quest = new MiningQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void bounty(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      BountyCompleteQuest quest = new BountyCompleteQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void levelUp(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      LevelUpQuest quest = new LevelUpQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   protected void blockInteract(String id, String name, ResourceLocation targetId, float targetProgress, String unlockedBy, Quest.QuestReward questReward) {
      BlockInteractionQuest quest = new BlockInteractionQuest(
         id, name, Quest.DescriptionData.getDefault(id), VaultMod.id("gui/quests/" + id), targetId, targetProgress, unlockedBy, questReward
      );
      this.quests.add(quest);
   }

   @Override
   protected void reset() {
      this.collection(
         "find_chromatic_iron",
         "Find Chromatic Iron",
         ModItems.RAW_CHROMATIC_IRON.getRegistryName(),
         16.0F,
         "",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.CHROMATIC_IRON_INGOT, 16)), 0)
      );
      this.mining(
         "mine_vault_stone",
         "Mine Vault Stone",
         ModBlocks.VAULT_STONE.getRegistryName(),
         8.0F,
         "find_chromatic_iron",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.CHIPPED_VAULT_ROCK, 8)), 0)
      );
      this.crafting(
         "craft_vault_altar",
         "Craft a Vault Altar",
         ModBlocks.VAULT_ALTAR.getRegistryName(),
         1.0F,
         "mine_vault_stone",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.VAULT_ROCK, 1)), 0)
      );
      this.crafting(
         "craft_vault_rock",
         "Craft a Vault Rock",
         ModItems.VAULT_ROCK.getRegistryName(),
         1.0F,
         "craft_vault_altar",
         new Quest.QuestReward(this.defaultReward(), 0)
      );
      this.collection(
         "complete_vault_crystal",
         "Complete a Vault Crystal",
         ModItems.VAULT_CRYSTAL.getRegistryName(),
         1.0F,
         "craft_vault_rock",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.SKILL_ORB, 1)), 0)
      );
      this.enterVault(
         "enter_vault", "Enter a Vault", anyId(), 1.0F, "complete_vault_crystal", new Quest.QuestReward(List.of(new ItemStack(ModBlocks.BOUNTY_BLOCK, 1)), 0)
      );
      this.bounty(
         "complete_bounty",
         "Complete a Bounty",
         anyId(),
         1.0F,
         "enter_vault",
         new Quest.QuestReward(
            List.of(
               new ItemStack(ModItems.HELMET, 1), new ItemStack(ModItems.CHESTPLATE, 1), new ItemStack(ModItems.LEGGINGS, 1), new ItemStack(ModItems.BOOTS, 1)
            ),
            0
         )
      );
      this.crafting(
         "craft_tool_station",
         "Craft a Tool Station",
         ModBlocks.TOOL_STATION.getRegistryName(),
         1.0F,
         "complete_bounty",
         new Quest.QuestReward(List.of(ModItems.JEWEL.defaultItem(), new ItemStack(ModItems.GEMSTONE, 1)), 0)
      );
      this.anvil(
         "apply_jewel",
         "Apply a Jewel",
         ModItems.JEWEL.getRegistryName(),
         1.0F,
         "craft_tool_station",
         new Quest.QuestReward(List.of(ModItems.JEWEL.defaultItem()), 0)
      );
      this.levelUp("level_up", "Gain a Vault Level", anyId(), 1.0F, "apply_jewel", new Quest.QuestReward(this.defaultReward(), 0));
      this.crafting(
         "craft_forge",
         "Craft a Vault Forge",
         ModBlocks.VAULT_FORGE.getRegistryName(),
         1.0F,
         "level_up",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.VAULT_ALLOY, 4)), 0)
      );
      this.crafting(
         "craft_artisan",
         "Craft an Artisan Station",
         ModBlocks.VAULT_ARTISAN_STATION.getRegistryName(),
         1.0F,
         "craft_forge",
         new Quest.QuestReward(List.of(new ItemStack(ModBlocks.VAULT_BRONZE, 16), new ItemStack(ModItems.VAULT_PLATING, 4)), 0)
      );
      this.crafting(
         "craft_recycler",
         "Craft a Vault Recycler",
         ModBlocks.VAULT_RECYCLER.getRegistryName(),
         1.0F,
         "craft_artisan",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.VAULT_SCRAP, 8)), 0)
      );
      this.blockInteract(
         "complete_god_altar", "Complete a God Altar", VaultMod.id("god_altar"), 1.0F, "craft_recycler", new Quest.QuestReward(this.defaultReward(), 0)
      );
      this.crafting(
         "craft_black_market",
         "Craft a Black Market",
         ModBlocks.BLACK_MARKET.getRegistryName(),
         1.0F,
         "complete_god_altar",
         new Quest.QuestReward(List.of(new ItemStack(ModItems.SOUL_SHARD, 32)), 0)
      );
      this.crafting(
         "craft_knowledge_star",
         "Craft a Knowledge Star",
         ModItems.KNOWLEDGE_STAR.getRegistryName(),
         1.0F,
         "craft_black_market",
         new Quest.QuestReward(this.defaultReward(), 0)
      );
      this.levelUp(
         "reach_level_20",
         "Reach Level 20",
         anyId(),
         1.0F,
         "craft_knowledge_star",
         new Quest.QuestReward(
            List.of(
               ModItems.SKILL_ORB.getDefaultInstance(),
               ModItems.KNOWLEDGE_STAR.getDefaultInstance(),
               new ItemStack(ModItems.KEY_PIECE, 8),
               ModItems.KEY_MOULD.getDefaultInstance()
            ),
            0
         )
      );
      this.onLoad(null);
   }

   @NotNull
   protected List<ItemStack> defaultReward() {
      return List.of(Items.DIAMOND.getDefaultInstance());
   }

   @NotNull
   protected static ResourceLocation anyId() {
      return VaultMod.id("any");
   }

   @Override
   public <T extends Config> T readConfig() {
      QuestConfig config = super.readConfig();
      config.getQuests().stream().map(Quest::getDescriptionElement).forEach(jsonElement -> ModConfigs.COLORS.replaceColorStrings(jsonElement));
      return (T)config;
   }

   @Override
   protected void onLoad(Config oldConfigInstance) {
      Collections.sort(this.quests);
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         ServerLevel overworld = server.getLevel(Level.OVERWORLD);
         if (overworld != null) {
            IEventBus eventBus = MinecraftForge.EVENT_BUS;
            boolean isSkyVault = SkyVaultsChunkGenerator.matches(overworld);
            if (isSkyVault && this instanceof SkyVaultQuestConfig) {
               this.quests.stream().filter(quest -> !(quest instanceof InVaultQuest)).forEach(eventBus::register);
            } else if (!isSkyVault && !(this instanceof SkyVaultQuestConfig)) {
               this.quests.stream().filter(quest -> !(quest instanceof InVaultQuest)).forEach(eventBus::register);
            }
         }
      }
   }

   @Override
   public void onUnload() {
      IEventBus eventBus = MinecraftForge.EVENT_BUS;
      this.quests.forEach(eventBus::unregister);
   }

   public <G extends Quest> G getQuestById(String id) {
      return (G)this.getQuests()
         .stream()
         .filter(goal -> goal.getId().equals(id))
         .findFirst()
         .orElseThrow(() -> new IllegalArgumentException("No quest found with the ID: " + id));
   }

   public <G extends Quest> G getNextQuest(Quest current) {
      return this.getNextQuest(current.getId());
   }

   public <G extends Quest> G getNextQuest(String currentId) {
      return (G)this.getQuests().stream().filter(quest -> quest.getUnlockedBy().equals(currentId)).findFirst().orElse(null);
   }
}
