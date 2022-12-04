package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Objective extends DataObject<Objective> implements ISupplierKey<Objective> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> ID = FieldKey.of("id", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Objective.ObjList> CHILDREN = FieldKey.of("children", Objective.ObjList.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), Objective.ObjList::new)
      .register(FIELDS);

   public Objective() {
      this.set(CHILDREN, new Objective.ObjList());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      this.get(CHILDREN).forEach(child -> child.initServer(world, vault));
   }

   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(CHILDREN).forEach(child -> child.tickServer(world, vault));
   }

   public void releaseServer() {
      CommonEvents.release(this);
      this.get(CHILDREN).forEach(Objective::releaseServer);
   }

   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      this.get(CHILDREN).forEach(child -> child.tickListener(world, vault, listener));
   }

   @OnlyIn(Dist.CLIENT)
   public abstract boolean render(PoseStack var1, Window var2, float var3, Player var4);

   public Objective add(Objective child) {
      this.get(CHILDREN).add(child);
      return this;
   }

   public abstract boolean isActive(Vault var1, Objective var2);

   public static class IdList extends DataList<Objective.IdList, Integer> {
      public IdList() {
         super(new ArrayList<>(), Adapter.ofSegmentedInt(3));
      }
   }

   public static class ObjList extends DataList<Objective.ObjList, Objective> {
      public ObjList() {
         super(new ArrayList<>(), Adapter.ofRegistryValue(() -> VaultRegistry.OBJECTIVE, ISupplierKey::getKey, Supplier::get));
      }
   }
}
