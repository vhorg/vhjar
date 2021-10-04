package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultGeneralConfig extends Config {
   @Expose
   private int TICK_COUNTER;
   @Expose
   private int NO_EXIT_CHANCE;
   @Expose
   private int OBELISK_DROP_CHANCE;
   @Expose
   private List<String> ITEM_BLACKLIST;
   @Expose
   private List<String> BLOCK_BLACKLIST;
   @Expose
   public float VAULT_EXIT_TNL_MIN;
   @Expose
   public float VAULT_EXIT_TNL_MAX;
   @Expose
   public boolean SAVE_PLAYER_SNAPSHOTS;
   @Expose
   private final WeightedList<String> VAULT_OBJECTIVES = new WeightedList<>();
   @Expose
   private final WeightedList<String> VAULT_COOP_OBJECTIVES = new WeightedList<>();

   @Override
   public String getName() {
      return "vault_general";
   }

   public int getTickCounter() {
      return this.TICK_COUNTER;
   }

   public int getNoExitChance() {
      return this.NO_EXIT_CHANCE;
   }

   public int getObeliskDropChance() {
      return this.OBELISK_DROP_CHANCE;
   }

   public VaultObjective generateObjective() {
      return this.selectObjective(this.VAULT_OBJECTIVES);
   }

   public VaultObjective generateCoopObjective() {
      return this.selectObjective(this.VAULT_COOP_OBJECTIVES);
   }

   private VaultObjective selectObjective(WeightedList<String> objectiveList) {
      return Optional.ofNullable(objectiveList.getRandom(rand))
         .<ResourceLocation>map(ResourceLocation::new)
         .map(VaultObjective::getObjective)
         .orElse(VaultRaid.SUMMON_AND_KILL_BOSS.get());
   }

   @Override
   protected void reset() {
      this.TICK_COUNTER = 30000;
      this.NO_EXIT_CHANCE = 10;
      this.ITEM_BLACKLIST = new ArrayList<>();
      this.ITEM_BLACKLIST.add(Items.field_221735_dD.getRegistryName().toString());
      this.BLOCK_BLACKLIST = new ArrayList<>();
      this.BLOCK_BLACKLIST.add(Blocks.field_150477_bB.getRegistryName().toString());
      this.OBELISK_DROP_CHANCE = 2;
      this.VAULT_EXIT_TNL_MIN = 0.0F;
      this.VAULT_EXIT_TNL_MAX = 0.0F;
      this.SAVE_PLAYER_SNAPSHOTS = false;
      this.VAULT_OBJECTIVES.clear();
      this.VAULT_OBJECTIVES.add(Vault.id("summon_and_kill_boss").toString(), 1);
      this.VAULT_OBJECTIVES.add(Vault.id("scavenger_hunt").toString(), 1);
      this.VAULT_COOP_OBJECTIVES.clear();
      this.VAULT_COOP_OBJECTIVES.add(Vault.id("summon_and_kill_boss").toString(), 1);
      this.VAULT_COOP_OBJECTIVES.add(Vault.id("scavenger_hunt").toString(), 1);
   }

   @SubscribeEvent
   public static void cancelItemInteraction(PlayerInteractEvent event) {
      if (event.getPlayer().field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
         if (ModConfigs.VAULT_GENERAL.ITEM_BLACKLIST.contains(event.getItemStack().func_77973_b().getRegistryName().toString()) && event.isCancelable()) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void cancelBlockInteraction(PlayerInteractEvent event) {
      if (event.getPlayer().field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
         BlockState state = event.getWorld().func_180495_p(event.getPos());
         if (ModConfigs.VAULT_GENERAL.BLOCK_BLACKLIST.contains(state.func_177230_c().getRegistryName().toString()) && event.isCancelable()) {
            event.setCanceled(true);
         }
      }
   }
}
