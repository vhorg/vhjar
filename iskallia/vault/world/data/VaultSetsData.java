package iskallia.vault.world.data;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.RelicPartItem;
import iskallia.vault.util.RelicSet;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultSetsData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_VaultSets";
   private Map<UUID, Set<String>> playerData = new HashMap<>();

   public VaultSetsData() {
      super("the_vault_VaultSets");
   }

   public VaultSetsData(String name) {
      super(name);
   }

   public Set<String> getCraftedSets(UUID playerId) {
      return this.playerData.computeIfAbsent(playerId, uuid -> new HashSet<>());
   }

   public int getExtraTime(UUID playerId) {
      return this.getCraftedSets(playerId).size() * ModConfigs.VAULT_RELICS.getExtraTickPerSet();
   }

   public boolean markSetAsCrafted(UUID playerId, RelicSet relicSet) {
      Set<String> craftedSets = this.getCraftedSets(playerId);
      this.func_76185_a();
      return craftedSets.add(relicSet.getId().toString());
   }

   @SubscribeEvent
   public static void onCrafted(ItemCraftedEvent event) {
      PlayerEntity player = event.getPlayer();
      if (!player.field_70170_p.field_72995_K) {
         IInventory craftingMatrix = event.getInventory();
         ItemStack craftedItemstack = event.getCrafting();
         if (craftedItemstack.func_77973_b() == ModBlocks.RELIC_STATUE_BLOCK_ITEM) {
            for (int i = 0; i < craftingMatrix.func_70302_i_(); i++) {
               ItemStack stackInSlot = craftingMatrix.func_70301_a(i);
               if (stackInSlot != ItemStack.field_190927_a) {
                  Item item = stackInSlot.func_77973_b();
                  if (item instanceof RelicPartItem) {
                     RelicPartItem relicPart = (RelicPartItem)item;
                     VaultSetsData vaultSetsData = get((ServerWorld)player.field_70170_p);
                     vaultSetsData.markSetAsCrafted(player.func_110124_au(), relicPart.getRelicSet());
                     break;
                  }
               }
            }
         }
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      this.playerData = NBTHelper.readMap(
         nbt, "Sets", ListNBT.class, list -> IntStream.range(0, list.size()).mapToObj(list::func_150307_f).collect(Collectors.toSet())
      );
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      NBTHelper.writeMap(compound, "Sets", this.playerData, ListNBT.class, strings -> {
         ListNBT list = new ListNBT();
         strings.forEach(s -> list.add(StringNBT.func_229705_a_(s)));
         return list;
      });
      return compound;
   }

   public static VaultSetsData get(ServerWorld world) {
      return (VaultSetsData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(VaultSetsData::new, "the_vault_VaultSets");
   }
}
