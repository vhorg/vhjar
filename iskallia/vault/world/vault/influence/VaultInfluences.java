package iskallia.vault.world.vault.influence;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultInfluences implements INBTSerializable<CompoundNBT>, Iterable<VaultInfluence> {
   private final List<VaultInfluence> influences = new ArrayList<>();
   protected boolean initialized = false;

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized() {
      this.initialized = true;
   }

   public void addInfluence(VaultInfluence influence, VaultRaid vault, ServerWorld world) {
      this.influences.add(influence);
      Random rand = world.func_201674_k();
      vault.getPlayers().forEach(vPlayer -> influence.apply(vault, vPlayer, world, rand));
   }

   public void tick(VaultRaid vault, VaultPlayer vPlayer, ServerWorld world) {
      this.forEach(influence -> influence.tick(vault, vPlayer, world));
   }

   public <T extends VaultInfluence> List<T> getInfluences(Class<T> influenceClass) {
      return this.influences
         .stream()
         .filter(influence -> influenceClass.isAssignableFrom(influence.getClass()))
         .map(influence -> (VaultInfluence)influence)
         .collect(Collectors.toList());
   }

   @Override
   public Iterator<VaultInfluence> iterator() {
      return this.influences.iterator();
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT tag = new CompoundNBT();
      tag.func_74757_a("initialized", this.initialized);
      ListNBT influenceList = new ListNBT();

      for (VaultInfluence influence : this.influences) {
         CompoundNBT ct = new CompoundNBT();
         ct.func_74778_a("id", influence.getKey().toString());
         ct.func_218657_a("data", influence.serializeNBT());
      }

      tag.func_218657_a("influences", influenceList);
      return tag;
   }

   public void deserializeNBT(CompoundNBT tag) {
      this.initialized = tag.func_74767_n("initialized");
      ListNBT influenceList = tag.func_150295_c("influences", 10);

      for (int i = 0; i < influenceList.size(); i++) {
         CompoundNBT ct = influenceList.func_150305_b(i);
         VaultInfluenceRegistry.getInfluence(new ResourceLocation(ct.func_74779_i("id"))).ifPresent(influence -> {
            influence.deserializeNBT(ct.func_74775_l("data"));
            this.influences.add(influence);
         });
      }
   }
}
