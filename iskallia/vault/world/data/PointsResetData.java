package iskallia.vault.world.data;

import iskallia.vault.util.nbt.NBTHelper;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class PointsResetData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PointsReset";
   private final Set<UUID> skillPointsCurrentlyReset = new HashSet<>();
   private final Set<UUID> archetypePointsCurrentlyReset = new HashSet<>();
   private final Set<UUID> knowledgePointsCurrentlyReset = new HashSet<>();

   public void onResetSkillPoints() {
      this.skillPointsCurrentlyReset.clear();
      this.setDirty();
   }

   public void onResetKnowledgePoints() {
      this.knowledgePointsCurrentlyReset.clear();
      this.setDirty();
   }

   public void onResetArchetypePoints() {
      this.archetypePointsCurrentlyReset.clear();
      this.setDirty();
   }

   public void addToSkillPoinsList(UUID uuid) {
      this.skillPointsCurrentlyReset.add(uuid);
      this.setDirty();
   }

   public void addToKnowledgePoinsList(UUID uuid) {
      this.knowledgePointsCurrentlyReset.add(uuid);
      this.setDirty();
   }

   public void addToArchetypePoinsList(UUID uuid) {
      this.archetypePointsCurrentlyReset.add(uuid);
      this.setDirty();
   }

   public static PointsResetData create(CompoundTag nbt) {
      PointsResetData data = new PointsResetData();
      data.load(nbt);
      return data;
   }

   public void load(CompoundTag tag) {
      this.skillPointsCurrentlyReset.clear();
      this.skillPointsCurrentlyReset
         .addAll(NBTHelper.readSet(tag, "skillPointsCurrentlyReset", StringTag.class, stringTag -> UUID.fromString(stringTag.getAsString())));
      this.knowledgePointsCurrentlyReset.clear();
      this.knowledgePointsCurrentlyReset
         .addAll(NBTHelper.readSet(tag, "knowledgePointsCurrentlyReset", StringTag.class, stringTag -> UUID.fromString(stringTag.getAsString())));
      this.archetypePointsCurrentlyReset.clear();
      this.archetypePointsCurrentlyReset
         .addAll(NBTHelper.readSet(tag, "archetypePointsCurrentlyReset", StringTag.class, stringTag -> UUID.fromString(stringTag.getAsString())));
   }

   @NotNull
   public CompoundTag save(CompoundTag nbt) {
      NBTHelper.writeCollection(nbt, "skillPointsCurrentlyReset", this.skillPointsCurrentlyReset, StringTag.class, id -> StringTag.valueOf(id.toString()));
      NBTHelper.writeCollection(
         nbt, "knowledgePointsCurrentlyReset", this.knowledgePointsCurrentlyReset, StringTag.class, id -> StringTag.valueOf(id.toString())
      );
      NBTHelper.writeCollection(
         nbt, "archetypePointsCurrentlyReset", this.archetypePointsCurrentlyReset, StringTag.class, id -> StringTag.valueOf(id.toString())
      );
      return nbt;
   }

   public static PointsResetData get() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return (PointsResetData)server.overworld().getDataStorage().computeIfAbsent(PointsResetData::create, PointsResetData::new, "the_vault_PointsReset");
   }

   public boolean hasSkillPointsReset(UUID uuid) {
      return this.skillPointsCurrentlyReset.contains(uuid);
   }

   public boolean hasKnowledgePointsReset(UUID uuid) {
      return this.knowledgePointsCurrentlyReset.contains(uuid);
   }

   public boolean hasArchetypePointsReset(UUID uuid) {
      return this.archetypePointsCurrentlyReset.contains(uuid);
   }
}
