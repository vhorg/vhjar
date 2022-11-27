package iskallia.vault.world.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Deprecated(
   forRemoval = true
)
public class PlayerImmunityData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerImmunity";
   protected Map<UUID, List<String>> effects = new HashMap<>();

   public void addEffect(Player player, MobEffectInstance effectInstance) {
      this.addEffect(player.getUUID(), effectInstance);
   }

   private void addEffect(UUID playerUUID, MobEffectInstance effectInstance) {
      this.effects.put(playerUUID, Collections.singletonList(effectInstance.getEffect().getRegistryName().toString()));
      this.setDirty();
   }

   public void addEffects(Player player, List<MobEffectInstance> effects) {
      this.addEffects(player.getUUID(), effects);
   }

   public void addEffects(UUID playerUUID, List<MobEffectInstance> effects) {
      this.effects
         .put(
            playerUUID,
            effects.stream()
               .map(MobEffectInstance::getEffect)
               .<ResourceLocation>map(ForgeRegistryEntry::getRegistryName)
               .filter(Objects::nonNull)
               .<String>map(ResourceLocation::toString)
               .collect(Collectors.toList())
         );
      this.setDirty();
   }

   public List<MobEffect> getEffects(UUID playerUUID) {
      return Registry.MOB_EFFECT
         .stream()
         .filter(effect -> this.effects.get(playerUUID).contains(effect.getRegistryName().toString()))
         .collect(Collectors.toList());
   }

   public void removeEffects(Player player) {
      this.removeEffects(player.getUUID());
   }

   public void removeEffects(UUID playerUUID) {
      this.effects.remove(playerUUID);
      this.setDirty();
   }

   private static PlayerImmunityData create(CompoundTag tag) {
      PlayerImmunityData data = new PlayerImmunityData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      this.effects.clear();

      for (String key : nbt.getAllKeys()) {
         UUID uuid;
         try {
            uuid = UUID.fromString(key);
         } catch (IllegalArgumentException var7) {
            continue;
         }

         List<String> effects = new ArrayList<>();
         CompoundTag effectTag = nbt.getCompound(key);
         effectTag.getAllKeys().forEach(effectKey -> effects.add(effectTag.getString(effectKey)));
         this.effects.put(uuid, effects);
      }
   }

   public CompoundTag save(CompoundTag compound) {
      this.effects.forEach((uuid, effects) -> {
         CompoundTag effectTag = new CompoundTag();
         AtomicInteger index = new AtomicInteger();
         effects.forEach(effectId -> effectTag.putString("Effect" + index.getAndIncrement(), effectId));
         compound.put(uuid.toString(), effectTag);
      });
      return compound;
   }

   public static PlayerImmunityData get(ServerLevel world) {
      return (PlayerImmunityData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerImmunityData::create, PlayerImmunityData::new, "the_vault_PlayerImmunity");
   }
}
