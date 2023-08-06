package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundSyncSkillAltarDataMessage;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SkillAltarData extends SavedData {
   public static final Comparator<? super TieredSkill> TIERED_SKILL_HIGHEST_LEVEL_COMPARATOR = Comparator.comparingInt(TieredSkill::getUnmodifiedTier)
      .reversed()
      .thenComparing(Skill::getName);
   protected static final String DATA_NAME = "the_vault_SkillAltar";
   public static final Comparator<SpecializedSkill> SPECIALIZED_SKILL_HIGHEST_LEVEL_COMPARATOR = Comparator.<SpecializedSkill>comparingInt(
         skill -> ((TieredSkill)skill.getSpecialization()).getUnmodifiedTier()
      )
      .reversed()
      .thenComparing(Skill::getName);
   private Map<UUID, Map<Integer, SkillAltarData.SkillTemplate>> playerSkillTemplates = new HashMap<>();
   private final Set<UUID> scheduledMerge = new HashSet<>();
   private AbilityTree previousAbilities;
   private TalentTree previousTalents;

   public boolean isDirty() {
      return true;
   }

   public static SkillAltarData create(CompoundTag tag) {
      SkillAltarData data = new SkillAltarData();
      data.load(tag);
      return data;
   }

   public void updateTemplateIcon(UUID playerId, int templateIndex, SkillAltarData.SkillIcon icon) {
      Map<Integer, SkillAltarData.SkillTemplate> templates = this.playerSkillTemplates.get(playerId);
      if (templates != null) {
         SkillAltarData.SkillTemplate template = templates.get(templateIndex);
         if (template != null) {
            template.setIcon(icon);
            this.syncPlayerIconKeys(playerId);
            this.setDirty();
         }
      }
   }

   public void saveSkillTemplate(UUID playerId, AbilityTree abilityTree, TalentTree talentTree, int templateIndex, SkillAltarData.SkillIcon icon) {
      AbilityTree abilities = abilityTree.copy();
      TalentTree talents = talentTree.copy();
      this.playerSkillTemplates
         .computeIfAbsent(playerId, uuid -> new HashMap<>())
         .put(templateIndex, new SkillAltarData.SkillTemplate(abilities, talents, icon));
      this.syncPlayerIconKeys(playerId);
      this.setDirty();
   }

   public void saveSkillTemplate(UUID playerId, int templateIndex, SkillAltarData.SkillTemplate template) {
      this.playerSkillTemplates.computeIfAbsent(playerId, uuid -> new HashMap<>()).put(templateIndex, template);
      this.syncPlayerIconKeys(playerId);
      this.setDirty();
   }

   private void syncPlayerIconKeys(UUID playerId) {
      Map<Integer, SkillAltarData.SkillTemplate> templates = this.playerSkillTemplates.get(playerId);
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundSyncSkillAltarDataMessage(playerId, getOrderedAbilityIconKeys(templates)));
   }

   public Map<Integer, SkillAltarData.SkillTemplate> getSkillTemplates(UUID playerId) {
      return this.playerSkillTemplates.getOrDefault(playerId, new HashMap<>());
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer()) {
            SkillAltarData data = get((ServerLevel)event.world);
            AbilityTree currentAbilities = ModConfigs.ABILITIES.get().orElse(null);
            TalentTree currentTalents = ModConfigs.TALENTS.get().orElse(null);
            if (data.previousAbilities != currentAbilities && currentAbilities != null) {
               data.previousAbilities = currentAbilities;
               data.scheduledMerge.addAll(data.playerSkillTemplates.keySet());
            }

            if (data.previousTalents != currentTalents && currentTalents != null) {
               data.previousTalents = currentTalents;
               data.scheduledMerge.addAll(data.playerSkillTemplates.keySet());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side.isServer() && event.player instanceof ServerPlayer player) {
            SkillAltarData data = get(player.getLevel());
            if (data.scheduledMerge.remove(player.getUUID())) {
               for (SkillAltarData.SkillTemplate template : data.playerSkillTemplates.get(player.getUUID()).values()) {
                  ModConfigs.ABILITIES.get().ifPresent(tree -> template.abilities.mergeFrom(tree.copy(), SkillContext.empty(0, 0, 0)));
                  ModConfigs.TALENTS.get().ifPresent(tree -> template.talents.mergeFrom(tree.copy(), SkillContext.empty(0, 0, 0)));
               }
            }
         }
      }
   }

   public void load(CompoundTag nbt) {
      this.playerSkillTemplates = NBTHelper.<UUID, Map<Integer, SkillAltarData.SkillTemplate>>readMap(
            nbt,
            "playerSkillTemplates",
            UUID::fromString,
            (uuid, skillTemplatesNbt) -> Optional.of(
               NBTHelper.deserializeMap(
                  (CompoundTag)skillTemplatesNbt,
                  Integer::valueOf,
                  (tagName, tag) -> Optional.of(SkillAltarData.SkillTemplate.deserializeNBT((CompoundTag)tag))
               )
            )
         )
         .orElseGet(HashMap::new);
   }

   public CompoundTag save(CompoundTag nbt) {
      NBTHelper.writeMap(
         nbt,
         "playerSkillTemplates",
         this.playerSkillTemplates,
         UUID::toString,
         skillTemplates -> NBTHelper.serializeMap(skillTemplates, String::valueOf, SkillAltarData.SkillTemplate::serializeNBT)
      );
      return nbt;
   }

   public static SkillAltarData get(ServerLevel world) {
      return (SkillAltarData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(SkillAltarData::create, SkillAltarData::new, "the_vault_SkillAltar");
   }

   @Nullable
   public SkillAltarData.SkillTemplate getSkillTemplate(UUID uuid, int templateIndex) {
      return !this.playerSkillTemplates.containsKey(uuid) ? null : this.playerSkillTemplates.get(uuid).get(templateIndex);
   }

   public void syncTo(ServerPlayer player) {
      Map<UUID, List<SkillAltarData.SkillIcon>> playerAbilityIconKeys = new HashMap<>();
      this.playerSkillTemplates
         .forEach(
            (uuid, skillTemplates) -> playerAbilityIconKeys.put(uuid, getOrderedAbilityIconKeys((Map<Integer, SkillAltarData.SkillTemplate>)skillTemplates))
         );
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncSkillAltarDataMessage(playerAbilityIconKeys));
   }

   private static List<SkillAltarData.SkillIcon> getOrderedAbilityIconKeys(Map<Integer, SkillAltarData.SkillTemplate> skillTemplates) {
      List<SkillAltarData.SkillIcon> list = new ArrayList<>();

      for (int i = 0; i < skillTemplates.size(); i++) {
         if (skillTemplates.containsKey(i)) {
            list.add(skillTemplates.get(i).getIcon());
         }
      }

      return list;
   }

   public record DeserializationResult<T>(boolean valid, String message, T deserializedValue) {
      public static <T> SkillAltarData.DeserializationResult<T> valid(T value) {
         return new SkillAltarData.DeserializationResult<>(true, "", value);
      }

      public static <T> SkillAltarData.DeserializationResult<T> invalid(String message) {
         return new SkillAltarData.DeserializationResult<>(false, message, null);
      }
   }

   public record SkillIcon(String key, boolean isTalent) {
      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("key", this.key);
         nbt.putBoolean("isTalent", this.isTalent);
         return nbt;
      }

      public static SkillAltarData.SkillIcon deserializeNBT(CompoundTag nbt) {
         return new SkillAltarData.SkillIcon(nbt.getString("key"), nbt.getBoolean("isTalent"));
      }

      public void writeTo(FriendlyByteBuf buffer) {
         buffer.writeUtf(this.key);
         buffer.writeBoolean(this.isTalent);
      }

      public static SkillAltarData.SkillIcon readFrom(FriendlyByteBuf buffer) {
         return new SkillAltarData.SkillIcon(buffer.readUtf(), buffer.readBoolean());
      }
   }

   public static class SkillTemplate {
      private final AbilityTree abilities;
      private final TalentTree talents;
      private SkillAltarData.SkillIcon icon;

      public SkillTemplate(AbilityTree abilities, TalentTree talents, SkillAltarData.SkillIcon icon) {
         this.abilities = abilities;
         this.talents = talents;
         this.icon = icon;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         this.abilities.writeNbt().ifPresent(abilitiesNbt -> nbt.put("abilities", abilitiesNbt));
         this.talents.writeNbt().ifPresent(talentsNbt -> nbt.put("talents", talentsNbt));
         nbt.put("icon", this.icon.serializeNBT());
         return nbt;
      }

      public static SkillAltarData.SkillTemplate deserializeNBT(CompoundTag nbt) {
         AbilityTree abilities = new AbilityTree();
         abilities.readNbt(nbt.getCompound("abilities"));
         TalentTree talents = new TalentTree();
         talents.readNbt(nbt.getCompound("talents"));
         return nbt.contains("topAbilityKey")
            ? new SkillAltarData.SkillTemplate(abilities, talents, new SkillAltarData.SkillIcon(nbt.getString("topAbilityKey"), false))
            : new SkillAltarData.SkillTemplate(abilities, talents, SkillAltarData.SkillIcon.deserializeNBT(nbt.getCompound("icon")));
      }

      public SkillAltarData.SkillIcon getIcon() {
         return this.icon;
      }

      public TalentTree getTalents() {
         return this.talents;
      }

      public AbilityTree getAbilities() {
         return this.abilities;
      }

      public static void writeTo(@Nullable SkillAltarData.SkillTemplate template, FriendlyByteBuf buffer) {
         if (template == null) {
            buffer.writeBoolean(false);
         } else {
            buffer.writeBoolean(true);
            ArrayBitBuffer bitBuffer = ArrayBitBuffer.empty();
            Adapters.SKILL.writeBits(template.abilities, bitBuffer);
            Adapters.SKILL.writeBits(template.talents, bitBuffer);
            buffer.writeLongArray(bitBuffer.toLongArray());
            template.icon.writeTo(buffer);
         }
      }

      @Nullable
      public static SkillAltarData.SkillTemplate readFrom(FriendlyByteBuf buffer) {
         if (!buffer.readBoolean()) {
            return null;
         } else {
            ArrayBitBuffer bitBuffer = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
            AbilityTree abilities = (AbilityTree)Adapters.SKILL.readBits(bitBuffer).orElse(null);
            TalentTree talents = (TalentTree)Adapters.SKILL.readBits(bitBuffer).orElse(null);
            return new SkillAltarData.SkillTemplate(abilities, talents, SkillAltarData.SkillIcon.readFrom(buffer));
         }
      }

      public void setIcon(SkillAltarData.SkillIcon icon) {
         this.icon = icon;
      }

      public String exportToString() {
         StringJoiner abilitiesJoiner = new StringJoiner("|");
         this.abilities.iterate(SpecializedSkill.class, skill -> addSkillIfSpeccedInto(abilitiesJoiner, skill.getSpecialization()));
         StringJoiner talentsJoiner = new StringJoiner("|");
         this.talents.iterate(LearnableSkill.class, learnableSkill -> {
            if (!(learnableSkill instanceof GroupedSkill)) {
               addSkillIfSpeccedInto(talentsJoiner, learnableSkill);
            }
         });
         return abilitiesJoiner + ";" + talentsJoiner + ";" + this.icon.key() + "|" + (this.icon.isTalent() ? 1 : 0);
      }

      private static void addSkillIfSpeccedInto(StringJoiner abilitiesJoiner, LearnableSkill learnableSkill) {
         if (learnableSkill instanceof TieredSkill tieredSkill) {
            int tier = tieredSkill.getUnmodifiedTier();
            if (tier > 0) {
               abilitiesJoiner.add(learnableSkill.getId() + ":" + tier);
            }
         }
      }

      public static SkillAltarData.DeserializationResult<SkillAltarData.SkillTemplate> fromString(String data) {
         String[] split = data.trim().split(";");
         if (split.length != 3) {
            return SkillAltarData.DeserializationResult.invalid("Template data must include abilities, talents and icon divided with ';'");
         } else {
            String[] abilitiesString = split[0].split("\\|");
            String[] talentsString = split[1].split("\\|");
            String[] iconString = split[2].split("\\|");
            SkillContext context = new SkillContext(100, 1000, 1000, SkillSource.empty());
            AbilityTree abilities = new AbilityTree();
            abilities.mergeFrom(ModConfigs.ABILITIES.get().orElse(null), context);

            for (String abilityString : abilitiesString) {
               String[] abilitySplit = abilityString.split(":");
               if (abilitySplit.length != 2) {
                  return SkillAltarData.DeserializationResult.invalid("Invalid ability '" + abilityString + "' must include name and tier divided with ':'");
               }

               if (ModConfigs.ABILITIES.get().flatMap(allAbilities -> allAbilities.getForId(abilitySplit[0])).isEmpty()) {
                  return SkillAltarData.DeserializationResult.invalid("Invalid ability '" + abilitySplit[0] + "' no such ability exists");
               }

               String specializationId = abilitySplit[0];
               int tier = Integer.parseInt(abilitySplit[1]);

               for (int i = 0; i < tier; i++) {
                  abilities.learn(specializationId, context);
               }

               abilities.specialize(specializationId, context);
            }

            TalentTree talents = new TalentTree();
            talents.mergeFrom(ModConfigs.TALENTS.get().orElse(null), context);

            for (String talentString : talentsString) {
               String[] talentSplit = talentString.split(":");
               if (talentSplit.length != 2) {
                  return SkillAltarData.DeserializationResult.invalid("Invalid talent '" + talentString + "' must include name and tier divided with ':'");
               }

               if (ModConfigs.TALENTS.get().flatMap(allTalents -> allTalents.getForId(talentSplit[0])).isEmpty()) {
                  return SkillAltarData.DeserializationResult.invalid("Invalid talent '" + talentSplit[0] + "' no such talent exists");
               }

               String talentId = talentSplit[0];
               int tier = Integer.parseInt(talentSplit[1]);

               for (int i = 0; i < tier; i++) {
                  talents.learn(talentId, context);
               }
            }

            SkillAltarData.SkillIcon icon;
            if (iconString.length == 2) {
               icon = new SkillAltarData.SkillIcon(iconString[0], iconString[1].equals("1"));
            } else {
               icon = new SkillAltarData.SkillIcon("", false);
            }

            return SkillAltarData.DeserializationResult.valid(new SkillAltarData.SkillTemplate(abilities, talents, icon));
         }
      }
   }
}
