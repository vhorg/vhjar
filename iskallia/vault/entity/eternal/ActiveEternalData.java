package iskallia.vault.entity.eternal;

import com.mojang.datafixers.util.Either;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ActiveEternalMessage;
import iskallia.vault.util.SkinProfile;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class ActiveEternalData {
   private static final Integer ETERNAL_TIMEOUT = 160;
   private static final ActiveEternalData INSTANCE = new ActiveEternalData();
   private final Map<UUID, Set<ActiveEternalData.ActiveEternal>> eternals = new HashMap<>();

   private ActiveEternalData() {
   }

   public static ActiveEternalData getInstance() {
      return INSTANCE;
   }

   public void updateEternal(EternalEntity eternal) {
      Either<UUID, ServerPlayerEntity> owner = eternal.getOwner();
      if (!owner.left().isPresent()) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)owner.right().get();
         if (sPlayer.func_130014_f_().func_234923_W_().equals(eternal.func_130014_f_().func_234923_W_())) {
            UUID ownerId = sPlayer.func_110124_au();
            boolean update = false;
            ActiveEternalData.ActiveEternal active = this.getActive(ownerId, eternal);
            if (active == null) {
               active = ActiveEternalData.ActiveEternal.create(eternal);
               this.eternals.computeIfAbsent(ownerId, id -> new LinkedHashSet<>()).add(active);
               update = true;
            }

            active.timeout = ETERNAL_TIMEOUT;
            float current = active.health;
            float healthToSet = eternal.func_110143_aJ();
            if (healthToSet <= 0.0F || Math.abs(current - healthToSet) >= 0.3F) {
               active.health = healthToSet;
               update = true;
            }

            if (!Objects.equals(active.abilityName, eternal.getProvidedAura())) {
               active.abilityName = eternal.getProvidedAura();
               update = true;
            }

            if (!Objects.equals(active.eternalName, eternal.getSkinName())) {
               active.eternalName = eternal.getSkinName();
               update = true;
            }

            if (update) {
               this.syncActives(ownerId, this.eternals.getOrDefault(ownerId, Collections.emptySet()));
            }
         }
      }
   }

   @Nullable
   private ActiveEternalData.ActiveEternal getActive(UUID ownerId, EternalEntity eternal) {
      UUID eternalId = eternal.getEternalId();

      for (ActiveEternalData.ActiveEternal activeEternal : this.eternals.computeIfAbsent(ownerId, id -> new LinkedHashSet<>())) {
         if (activeEternal.eternalId.equals(eternalId)) {
            return activeEternal;
         }
      }

      return null;
   }

   public boolean isEternalActive(UUID eternalId) {
      for (Set<ActiveEternalData.ActiveEternal> activeEternals : this.eternals.values()) {
         for (ActiveEternalData.ActiveEternal activeEternal : activeEternals) {
            if (activeEternal.eternalId.equals(eternalId)) {
               return true;
            }
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void onTick(ServerTickEvent event) {
      INSTANCE.eternals.forEach((playerId, activeEternals) -> {
         boolean removedAny = activeEternals.removeIf(activeEternal -> {
            activeEternal.timeout--;
            return activeEternal.timeout <= 0;
         });
         if (removedAny) {
            INSTANCE.syncActives(playerId, (Set<ActiveEternalData.ActiveEternal>)activeEternals);
         }
      });
   }

   @SubscribeEvent
   public static void onChangeDim(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity) {
         UUID playerId = event.getEntity().func_110124_au();
         if (INSTANCE.eternals.containsKey(playerId)) {
            Set<ActiveEternalData.ActiveEternal> eternals = INSTANCE.eternals.remove(playerId);
            if (eternals != null && !eternals.isEmpty()) {
               INSTANCE.syncActives((ServerPlayerEntity)event.getEntity(), Collections.emptySet());
            }
         }
      }
   }

   private void syncActives(UUID playerId, Set<ActiveEternalData.ActiveEternal> eternals) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      if (srv != null) {
         ServerPlayerEntity sPlayer = srv.func_184103_al().func_177451_a(playerId);
         if (sPlayer != null) {
            this.syncActives(sPlayer, eternals);
         }
      }
   }

   private void syncActives(ServerPlayerEntity sPlayer, Set<ActiveEternalData.ActiveEternal> eternals) {
      ModNetwork.CHANNEL.sendTo(new ActiveEternalMessage(eternals), sPlayer.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class ActiveEternal {
      private final UUID eternalId;
      private final boolean ancient;
      private String eternalName;
      private String abilityName;
      private float health;
      private int timeout = ActiveEternalData.ETERNAL_TIMEOUT;
      private SkinProfile skinUtil = null;

      private ActiveEternal(UUID eternalId, String eternalName, String abilityName, boolean ancient, float health) {
         this.eternalId = eternalId;
         this.eternalName = eternalName;
         this.abilityName = abilityName;
         this.ancient = ancient;
         this.health = health;
      }

      public static ActiveEternalData.ActiveEternal create(EternalEntity eternal) {
         return new ActiveEternalData.ActiveEternal(
            eternal.getEternalId(), eternal.getSkinName(), eternal.getProvidedAura(), eternal.isAncient(), eternal.func_110143_aJ()
         );
      }

      public static ActiveEternalData.ActiveEternal read(PacketBuffer buffer) {
         return new ActiveEternalData.ActiveEternal(
            buffer.func_179253_g(),
            buffer.func_150789_c(32767),
            buffer.readBoolean() ? buffer.func_150789_c(32767) : null,
            buffer.readBoolean(),
            buffer.readFloat()
         );
      }

      public void write(PacketBuffer buffer) {
         buffer.func_179252_a(this.eternalId);
         buffer.func_211400_a(this.eternalName, 32767);
         buffer.writeBoolean(this.abilityName != null);
         if (this.abilityName != null) {
            buffer.func_211400_a(this.abilityName, 32767);
         }

         buffer.writeBoolean(this.ancient);
         buffer.writeFloat(this.health);
      }

      public String getAbilityName() {
         return this.abilityName;
      }

      public Optional<EternalAuraConfig.AuraConfig> getAbilityConfig() {
         return this.getAbilityName() == null ? Optional.empty() : Optional.ofNullable(ModConfigs.ETERNAL_AURAS.getByName(this.getAbilityName()));
      }

      public boolean isAncient() {
         return this.ancient;
      }

      public float getHealth() {
         return this.health;
      }

      @OnlyIn(Dist.CLIENT)
      public void updateFrom(ActiveEternalData.ActiveEternal activeEternal) {
         this.health = activeEternal.health;
         this.abilityName = activeEternal.abilityName;
         if (!this.eternalName.equals(activeEternal.eternalName)) {
            this.eternalName = activeEternal.eternalName;
            if (this.skinUtil != null) {
               this.skinUtil.updateSkin(this.eternalName);
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public SkinProfile getSkin() {
         if (this.skinUtil == null) {
            this.skinUtil = new SkinProfile();
            this.skinUtil.updateSkin(this.eternalName);
         }

         return this.skinUtil;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ActiveEternalData.ActiveEternal that = (ActiveEternalData.ActiveEternal)o;
            return Objects.equals(this.eternalId, that.eternalId);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.eternalId);
      }
   }
}