package iskallia.vault.world.data;

import iskallia.vault.Vault;
import iskallia.vault.init.ModFeatures;
import iskallia.vault.init.ModStructures;
import iskallia.vault.item.CrystalData;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.world.raid.VaultRaid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultRaidData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_VaultRaid";
   private Map<UUID, VaultRaid> activeRaids = new HashMap<>();
   private int xOffset = 0;

   public VaultRaidData() {
      this("the_vault_VaultRaid");
   }

   public VaultRaidData(String name) {
      super(name);
   }

   public VaultRaid getAt(BlockPos pos) {
      return this.activeRaids.values().stream().filter(raid -> raid.box.func_175898_b(pos)).findFirst().orElse(null);
   }

   public void remove(ServerWorld server, UUID playerId) {
      VaultRaid v = this.activeRaids.remove(playerId);
      if (v != null) {
         v.ticksLeft = 0;
         v.finish(server, playerId);
      }
   }

   public VaultRaid getActiveFor(ServerPlayerEntity player) {
      return this.activeRaids.get(player.func_110124_au());
   }

   public VaultRaid startNew(ServerPlayerEntity player, ItemVaultCrystal crystal, boolean isFinalVault) {
      return this.startNew(player, crystal.getRarity().ordinal(), "", new CrystalData(null), isFinalVault);
   }

   public VaultRaid startNew(ServerPlayerEntity player, int rarity, String playerBossName, CrystalData data, boolean isFinalVault) {
      return this.startNew(Collections.singletonList(player), Collections.emptyList(), rarity, playerBossName, data, isFinalVault);
   }

   public VaultRaid startNew(
      List<ServerPlayerEntity> players, List<ServerPlayerEntity> spectators, int rarity, String playerBossName, CrystalData data, boolean isFinalVault
   ) {
      players.forEach(player -> player.func_146105_b(new StringTextComponent("Generating vault, please wait...").func_240699_a_(TextFormatting.GREEN), true));
      int level = players.stream()
         .map(player -> PlayerVaultStatsData.get(player.func_71121_q()).getVaultStats(player).getVaultLevel())
         .max(Integer::compareTo)
         .get();
      VaultRaid raid = new VaultRaid(
         players, spectators, new MutableBoundingBox(this.xOffset, 0, 0, this.xOffset += 2048, 256, 2048), level, rarity, playerBossName
      );
      raid.isFinalVault = isFinalVault;
      players.forEach(player -> {
         if (this.activeRaids.containsKey(player.func_110124_au())) {
            this.activeRaids.get(player.func_110124_au()).ticksLeft = 0;
         }
      });
      raid.getPlayerIds().forEach(uuid -> this.activeRaids.put(uuid, raid));
      this.func_76185_a();
      ServerWorld world = players.get(0).func_184102_h().func_71218_a(Vault.VAULT_KEY);
      players.get(0)
         .func_184102_h()
         .func_222817_e(
            () -> {
               try {
                  ChunkPos chunkPos = new ChunkPos(
                     raid.box.field_78897_a + raid.box.func_78883_b() / 2 >> 4, raid.box.field_78896_c + raid.box.func_78880_d() / 2 >> 4
                  );
                  StructureSeparationSettings settings = new StructureSeparationSettings(1, 0, -1);
                  StructureStart<?> start = (raid.isFinalVault ? ModFeatures.FINAL_VAULT_FEATURE : ModFeatures.VAULT_FEATURE)
                     .func_242771_a(
                        world.func_241828_r(),
                        world.func_72863_F().field_186029_c,
                        world.func_72863_F().field_186029_c.func_202090_b(),
                        world.func_184163_y(),
                        world.func_72905_C(),
                        chunkPos,
                        BiomeRegistry.field_244200_a,
                        0,
                        settings
                     );
                  int chunkRadius = 64;

                  for (int x = -chunkRadius; x <= chunkRadius; x += 17) {
                     for (int z = -chunkRadius; z <= chunkRadius; z += 17) {
                        world.func_217353_a(chunkPos.field_77276_a + x, chunkPos.field_77275_b + z, ChunkStatus.field_223226_a_, true)
                           .func_230344_a_(ModStructures.VAULT, start);
                     }
                  }

                  raid.start(world, chunkPos, data);
               } catch (Exception var9x) {
                  var9x.printStackTrace();
               }
            }
         );
      return raid;
   }

   public void tick(ServerWorld world) {
      this.activeRaids.values().forEach(vaultRaid -> vaultRaid.tick(world));
      boolean removed = false;
      List<Runnable> tasks = new ArrayList<>();

      for (VaultRaid raid : this.activeRaids.values()) {
         if (raid.isComplete()) {
            raid.syncTicksLeft(world.func_73046_m());
            tasks.add(() -> raid.playerIds.forEach(uuid -> this.remove(world, uuid)));
            removed = true;
         }
      }

      tasks.forEach(Runnable::run);
      if (removed || this.activeRaids.size() > 0) {
         this.func_76185_a();
      }
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (event.side == LogicalSide.SERVER && event.phase == Phase.START && event.world.func_234923_W_() == Vault.VAULT_KEY) {
         get((ServerWorld)event.world).tick((ServerWorld)event.world);
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.activeRaids.clear();
      nbt.func_150295_c("ActiveRaids", 10).forEach(raidNBT -> {
         VaultRaid raid = VaultRaid.fromNBT((CompoundNBT)raidNBT);
         raid.getPlayerIds().forEach(uuid -> {
            VaultRaid var10000 = this.activeRaids.put(uuid, raid);
         });
      });
      this.xOffset = nbt.func_74762_e("XOffset");
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      ListNBT raidsList = new ListNBT();
      this.activeRaids.values().forEach(raid -> raidsList.add(raid.serializeNBT()));
      nbt.func_218657_a("ActiveRaids", raidsList);
      nbt.func_74768_a("XOffset", this.xOffset);
      return nbt;
   }

   public static VaultRaidData get(ServerWorld world) {
      return (VaultRaidData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(VaultRaidData::new, "the_vault_VaultRaid");
   }
}
