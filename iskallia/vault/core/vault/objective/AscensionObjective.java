package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.item.SoulPlaqueBlockItem;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.SoulFlameItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import iskallia.vault.item.crystal.modifiers.DefaultCrystalModifiers;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.UUID;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AscensionObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("ascension", Objective.class).with(Version.v1_21, AscensionObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> STACKS = FieldKey.of("stacks", Integer.class).with(Version.v1_21, Adapters.INT, DISK.all()).register(FIELDS);
   public static final FieldKey<String> PLAYER_NAME = FieldKey.of("player_name", String.class).with(Version.v1_21, Adapters.UTF_8, DISK.all()).register(FIELDS);
   public static final FieldKey<UUID> PLAYER_UUID = FieldKey.of("player_uuid", UUID.class).with(Version.v1_21, Adapters.UUID, DISK.all()).register(FIELDS);
   public static final FieldKey<Tag> MODIFIERS = FieldKey.of("modifiers", Tag.class).with(Version.v1_24, Adapters.GENERIC_NBT, DISK.all()).register(FIELDS);

   protected AscensionObjective() {
   }

   public static AscensionObjective create(int stacks, String playerName, UUID playerUuid, Tag modifiers) {
      AscensionObjective objective = (AscensionObjective)new AscensionObjective()
         .set(STACKS, Integer.valueOf(stacks))
         .set(PLAYER_NAME, playerName)
         .set(PLAYER_UUID, playerUuid);
      objective.set(MODIFIERS, modifiers.copy());
      return objective;
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_LEAVE
         .register(
            this,
            data -> {
               if (data.getVault() == vault) {
                  if (data.getListener().getId().equals(this.get(PLAYER_UUID))) {
                     vault.ifPresent(
                        Vault.STATS,
                        stats -> {
                           StatCollector stat = stats.get(data.getListener());
                           if (stat != null) {
                              if (stat.getCompletion() == Completion.COMPLETED) {
                                 PlayerVaultStats playerStats = PlayerVaultStatsData.get(world).getVaultStats(data.getListener().getId());
                                 int vaultLevel = vault.get(Vault.LEVEL).get();
                                 int playerLevel = playerStats.getVaultLevel();
                                 int stacks = this.getOr(STACKS, Integer.valueOf(0)) + (playerLevel <= vaultLevel ? 1 : 0);
                                 CrystalModifiers modifiers = this.getOptional(MODIFIERS)
                                    .flatMap(tag -> CrystalData.MODIFIERS.readNbt(tag))
                                    .orElseGet(DefaultCrystalModifiers::new);
                                 stat.get(StatCollector.REWARD).add(SoulFlameItem.create(stacks, this.get(PLAYER_NAME), this.get(PLAYER_UUID), modifiers));
                              } else {
                                 ItemStack stack = new ItemStack(ModItems.EMBER);
                                 stack.setCount(ModConfigs.ASCENSION.getEmberCount(this.get(STACKS)));
                                 ItemStack plaque = SoulPlaqueBlockItem.create(this.get(PLAYER_UUID), this.get(PLAYER_NAME), this.get(STACKS));
                                 if (!stack.isEmpty()) {
                                    stat.get(StatCollector.REWARD).add(stack);
                                 }

                                 if (!plaque.isEmpty()) {
                                    stat.get(StatCollector.REWARD).add(plaque);
                                 }
                              }
                           }
                        }
                     );
                  }
               }
            }
         );
   }

   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
