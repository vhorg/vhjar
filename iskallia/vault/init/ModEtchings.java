package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.etching.set.AssassinSet;
import iskallia.vault.etching.set.BloodSet;
import iskallia.vault.etching.set.DragonSet;
import iskallia.vault.etching.set.DreamSet;
import iskallia.vault.etching.set.DryadSet;
import iskallia.vault.etching.set.GolemSet;
import iskallia.vault.etching.set.RiftSet;
import iskallia.vault.etching.set.TreasureSet;
import iskallia.vault.etching.set.VampireSet;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEtchings {
   public static final AssassinSet ASSASSIN = new AssassinSet(VaultMod.id("assassin"));
   public static final BloodSet BLOOD = new BloodSet(VaultMod.id("blood"));
   public static final DragonSet DRAGON = new DragonSet(VaultMod.id("dragon"));
   public static final DreamSet DREAM = new DreamSet(VaultMod.id("dream"));
   public static final DryadSet DRYAD = new DryadSet(VaultMod.id("dryad"));
   public static final GolemSet GOLEM = new GolemSet(VaultMod.id("golem"));
   public static final RiftSet RIFT = new RiftSet(VaultMod.id("rift"));
   public static final TreasureSet TREASURE = new TreasureSet(VaultMod.id("treasure"));
   public static final VampireSet VAMPIRE = new VampireSet(VaultMod.id("vampire"));
   public static final EtchingSet.Simple DIVINITY = new EtchingSet.Simple(VaultMod.id("divinity"));
   public static final EtchingSet.Simple PHOENIX = new EtchingSet.Simple(VaultMod.id("phoenix"));
   public static final EtchingSet.Simple ZOD = new EtchingSet.Simple(VaultMod.id("zod"));

   public static void init(Register<EtchingSet<?>> event) {
      IForgeRegistry<EtchingSet<?>> registry = event.getRegistry();
      registry.register(ASSASSIN);
      registry.register(BLOOD);
      registry.register(DRAGON);
      registry.register(DREAM);
      registry.register(DRYAD);
      registry.register(GOLEM);
      registry.register(RIFT);
      registry.register(TREASURE);
      registry.register(VAMPIRE);
      registry.register(DIVINITY);
      registry.register(PHOENIX);
      registry.register(ZOD);
   }
}
