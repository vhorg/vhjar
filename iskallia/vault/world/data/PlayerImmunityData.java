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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class PlayerImmunityData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_PlayerImmunity";
   protected Map<UUID, List<String>> effects = new HashMap<>();

   public PlayerImmunityData() {
      this("the_vault_PlayerImmunity");
   }

   public PlayerImmunityData(String name) {
      super(name);
   }

   public void addEffect(PlayerEntity player, EffectInstance effectInstance) {
      this.addEffect(player.func_110124_au(), effectInstance);
   }

   private void addEffect(UUID playerUUID, EffectInstance effectInstance) {
      this.effects.put(playerUUID, Collections.singletonList(effectInstance.func_188419_a().getRegistryName().toString()));
      this.func_76185_a();
   }

   public void addEffects(PlayerEntity player, List<EffectInstance> effects) {
      this.addEffects(player.func_110124_au(), effects);
   }

   public void addEffects(UUID playerUUID, List<EffectInstance> effects) {
      this.effects
         .put(
            playerUUID,
            effects.stream()
               .map(EffectInstance::func_188419_a)
               .<ResourceLocation>map(ForgeRegistryEntry::getRegistryName)
               .filter(Objects::nonNull)
               .<String>map(ResourceLocation::toString)
               .collect(Collectors.toList())
         );
      this.func_76185_a();
   }

   public List<Effect> getEffects(UUID playerUUID) {
      return Registry.field_212631_t
         .func_201756_e()
         .filter(effect -> this.effects.get(playerUUID).contains(effect.getRegistryName().toString()))
         .collect(Collectors.toList());
   }

   public void removeEffects(PlayerEntity player) {
      this.removeEffects(player.func_110124_au());
   }

   public void removeEffects(UUID playerUUID) {
      this.effects.remove(playerUUID);
      this.func_76185_a();
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.effects.clear();

      for (String key : nbt.func_150296_c()) {
         UUID uuid;
         try {
            uuid = UUID.fromString(key);
         } catch (IllegalArgumentException var7) {
            continue;
         }

         List<String> effects = new ArrayList<>();
         CompoundNBT effectTag = nbt.func_74775_l(key);
         effectTag.func_150296_c().forEach(effectKey -> effects.add(effectTag.func_74779_i(effectKey)));
         this.effects.put(uuid, effects);
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      this.effects.forEach((uuid, effects) -> {
         CompoundNBT effectTag = new CompoundNBT();
         AtomicInteger index = new AtomicInteger();
         effects.forEach(effectId -> effectTag.func_74778_a("Effect" + index.getAndIncrement(), effectId));
         compound.func_218657_a(uuid.toString(), effectTag);
      });
      return compound;
   }

   public static PlayerImmunityData get(ServerWorld world) {
      return (PlayerImmunityData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(PlayerImmunityData::new, "the_vault_PlayerImmunity");
   }
}
