package iskallia.vault.quest.base;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.type.AnvilQuest;
import iskallia.vault.quest.type.BlockInteractionQuest;
import iskallia.vault.quest.type.BountyCompleteQuest;
import iskallia.vault.quest.type.CheckmarkQuest;
import iskallia.vault.quest.type.CollectionQuest;
import iskallia.vault.quest.type.CraftCrystalQuest;
import iskallia.vault.quest.type.CraftingQuest;
import iskallia.vault.quest.type.EnterVaultQuest;
import iskallia.vault.quest.type.ForgeGearQuest;
import iskallia.vault.quest.type.LevelUpQuest;
import iskallia.vault.quest.type.MiningQuest;
import iskallia.vault.quest.type.ModifyGearQuest;
import iskallia.vault.quest.type.SurviveQuest;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.QuestStatesData;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Quest implements Comparable<Quest> {
   @Expose
   protected final String type;
   @Expose
   protected final String id;
   @Expose
   protected final String name;
   @Expose
   protected final DescriptionData descriptionData;
   @Expose
   protected final ResourceLocation icon;
   @Expose
   protected final ResourceLocation targetId;
   @Expose
   protected final float targetProgress;
   @Expose
   protected final String unlockedBy;
   @Expose
   protected final Quest.QuestReward reward;

   protected Quest(
      String type,
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      this.type = type;
      this.id = id;
      this.name = name;
      this.descriptionData = descriptionData;
      this.icon = icon;
      this.targetId = targetId;
      this.targetProgress = targetProgress;
      this.unlockedBy = unlockedBy;
      this.reward = reward;
   }

   public String getType() {
      return this.type;
   }

   public abstract MutableComponent getTypeDescription();

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public MutableComponent getDescription() {
      return this.descriptionData == null
         ? Serializer.fromJsonLenient(
            "[{text:'No description for ', color:'#192022'},{text: '" + this.id + "', color: '#fcf5c5'},{text: ', yet', color: '#192022'}]"
         )
         : Serializer.fromJson(this.descriptionData.getDescription());
   }

   public JsonElement getDescriptionElement() {
      return this.descriptionData.getDescription();
   }

   public ResourceLocation getIcon() {
      return this.icon;
   }

   public ResourceLocation getTargetId() {
      return this.targetId;
   }

   public float getTargetProgress() {
      return this.targetProgress;
   }

   public String getUnlockedBy() {
      return this.unlockedBy;
   }

   public Quest.QuestReward getReward() {
      return this.reward;
   }

   public void progress(ServerPlayer player, float amount) {
      QuestStatesData data = QuestStatesData.get();
      QuestState state = data.getState(player);
      state.addProgress(this, amount);
      data.setDirty();
   }

   public void onComplete(ServerPlayer player) {
      if (this.reward != null) {
         this.reward.apply(player);
      }
   }

   public int compareTo(@NotNull Quest other) {
      if (this.id.equals(other.id)) {
         return 0;
      } else if (this.unlockedBy.isBlank()) {
         return -1;
      } else if (this.unlockedBy.equals(other.id)) {
         return 1;
      } else {
         return this.id.equals(other.unlockedBy) ? -1 : 0;
      }
   }

   @Override
   public String toString() {
      return "Quest{type='" + this.type + "', id='" + this.id + "', unlockedBy='" + this.unlockedBy + "'}";
   }

   public static class Adapter implements JsonDeserializer<Quest> {
      public static final Quest.Adapter INSTANCE = new Quest.Adapter();

      public Quest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String type = object.get("type").getAsString();
         String id = object.get("id").getAsString();
         String name = object.get("name").getAsString();
         DescriptionData descriptionData = (DescriptionData)context.deserialize(object.get("descriptionData"), DescriptionData.class);
         ResourceLocation icon = new ResourceLocation(object.get("icon").getAsString());
         ResourceLocation targetId = new ResourceLocation(object.get("targetId").getAsString());
         float targetProgress = object.get("targetProgress").getAsFloat();
         String unlockedBy = object.has("unlockedBy") ? object.get("unlockedBy").getAsString() : "";
         Quest.QuestReward questReward = (Quest.QuestReward)context.deserialize(object.get("reward"), Quest.QuestReward.class);
         Quest.Builder builder = new Quest.Builder(type)
            .id(id)
            .name(name)
            .descriptionData(descriptionData)
            .icon(icon)
            .targetId(targetId)
            .targetProgress(targetProgress)
            .unlockedBy(unlockedBy)
            .reward(questReward);
         return builder.build();
      }
   }

   public static class Builder {
      private String type;
      private String id;
      private String name;
      private DescriptionData descriptionData;
      private ResourceLocation icon;
      private ResourceLocation targetId;
      private float targetProgress;
      private String unlockedBy;
      private Quest.QuestReward reward;

      public Builder(String type) {
         this.type = type;
      }

      public Quest.Builder id(String id) {
         this.id = id;
         return this;
      }

      public Quest.Builder setType(String type) {
         this.type = type;
         return this;
      }

      public Quest.Builder name(String name) {
         this.name = name;
         return this;
      }

      public Quest.Builder descriptionData(DescriptionData descriptionData) {
         this.descriptionData = descriptionData;
         return this;
      }

      public Quest.Builder icon(ResourceLocation icon) {
         this.icon = icon;
         return this;
      }

      public Quest.Builder targetId(ResourceLocation targetId) {
         this.targetId = targetId;
         return this;
      }

      public Quest.Builder targetProgress(float targetProgress) {
         this.targetProgress = targetProgress;
         return this;
      }

      public Quest.Builder unlockedBy(String unlockedBy) {
         this.unlockedBy = unlockedBy;
         return this;
      }

      public Quest.Builder reward(Quest.QuestReward reward) {
         this.reward = reward;
         return this;
      }

      public Quest build() {
         if (this.id == null || this.id.isBlank()) {
            throw new IllegalStateException("Attempted to create a Quest with invalid ID. ID must not be null or empty.");
         } else if (this.name == null || this.name.isBlank()) {
            throw new IllegalStateException("Attempted to create a Quest with invalid NAME. NAME must not be null or empty.");
         } else if (this.descriptionData == null) {
            throw new IllegalStateException("Attempted to create a Quest with invalid DESCRIPTION. DESCRIPTION must not be null or empty.");
         } else if (this.icon == null) {
            throw new IllegalStateException("Attempted to create a Quest with invalid ICON. ICON must not be null.");
         } else if (this.targetId == null) {
            throw new IllegalStateException("Attempted to create a Quest with invalid TARGET. TARGET must not be null.");
         } else if (this.targetProgress < 1.0F) {
            throw new IllegalStateException("Attempted to create a Quest with invalid TARGET PROGRESS. Must be greater than 0.");
         } else if (this.reward == null) {
            throw new IllegalStateException("Attempted to create a Quest with invalid REWARD. REWARD must not be null.");
         } else {
            String var1 = this.type;
            switch (var1) {
               case "anvil":
                  return new AnvilQuest(this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward);
               case "block_interact":
                  return new BlockInteractionQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "bounty_complete":
                  return new BountyCompleteQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "checkmark":
                  return new CheckmarkQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "collection":
                  return new CollectionQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "craft_crystal":
                  return new CraftCrystalQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "crafting":
                  return new CraftingQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "enter_vault":
                  return new EnterVaultQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "forge_gear":
                  return new ForgeGearQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "level_up":
                  return new LevelUpQuest(this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward);
               case "mining":
                  return new MiningQuest(this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward);
               case "modify_gear":
                  return new ModifyGearQuest(
                     this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward
                  );
               case "survive":
                  return new SurviveQuest(this.id, this.name, this.descriptionData, this.icon, this.targetId, this.targetProgress, this.unlockedBy, this.reward);
               default:
                  return null;
            }
         }
      }
   }

   public static class QuestReward {
      @Expose
      List<ItemStack> items;
      @Expose
      int vaultExp;
      @Expose
      int skillPoints;

      public QuestReward(List<ItemStack> items, int vaultExp) {
         this.items = items;
         this.vaultExp = vaultExp;
         this.skillPoints = 0;
      }

      public QuestReward(List<ItemStack> items, int vaultExp, int skillPoints) {
         this.items = items;
         this.vaultExp = vaultExp;
         this.skillPoints = skillPoints;
      }

      public void apply(ServerPlayer player) {
         PlayerVaultStatsData data = PlayerVaultStatsData.get(player.getLevel());
         int vaultLevel = data.getVaultStats(player).getVaultLevel();
         this.getItems().forEach(stack -> {
            stack = LootInitialization.initializeVaultLoot(stack, vaultLevel);
            EntityHelper.giveItem(player, stack);
         });
         data.addVaultExp(player, this.vaultExp);
         data.addSkillPoints(player, this.skillPoints);
      }

      public int getVaultExp() {
         return this.vaultExp;
      }

      public List<ItemStack> getItems() {
         return this.items.stream().<ItemStack>map(ItemStack::copy).toList();
      }

      public int getSkillPoints() {
         return this.skillPoints;
      }
   }
}
