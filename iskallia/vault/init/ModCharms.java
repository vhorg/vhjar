package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.config.CharmConfig;
import iskallia.vault.gear.charm.AttributeCharm;
import iskallia.vault.gear.charm.CharmEffect;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModCharms {
   public static final AttributeCharm<Float> SMALL_IDONA = new AttributeCharm<>(
      CharmConfig.Size.SMALL, VaultMod.id("small_idona"), ModGearAttributes.IDONA_AFFINITY
   );
   public static final AttributeCharm<Float> LARGE_IDONA = new AttributeCharm<>(
      CharmConfig.Size.LARGE, VaultMod.id("large_idona"), ModGearAttributes.IDONA_AFFINITY
   );
   public static final AttributeCharm<Float> GRAND_IDONA = new AttributeCharm<>(
      CharmConfig.Size.GRAND, VaultMod.id("grand_idona"), ModGearAttributes.IDONA_AFFINITY
   );
   public static final AttributeCharm<Float> MAJESTIC_IDONA = new AttributeCharm<>(
      CharmConfig.Size.MAJESTIC, VaultMod.id("majestic_idona"), ModGearAttributes.IDONA_AFFINITY
   );
   public static final AttributeCharm<Float> SMALL_VELARA = new AttributeCharm<>(
      CharmConfig.Size.SMALL, VaultMod.id("small_velara"), ModGearAttributes.VELARA_AFFINITY
   );
   public static final AttributeCharm<Float> LARGE_VELARA = new AttributeCharm<>(
      CharmConfig.Size.LARGE, VaultMod.id("large_velara"), ModGearAttributes.VELARA_AFFINITY
   );
   public static final AttributeCharm<Float> GRAND_VELARA = new AttributeCharm<>(
      CharmConfig.Size.GRAND, VaultMod.id("grand_velara"), ModGearAttributes.VELARA_AFFINITY
   );
   public static final AttributeCharm<Float> MAJESTIC_VELARA = new AttributeCharm<>(
      CharmConfig.Size.MAJESTIC, VaultMod.id("majestic_velara"), ModGearAttributes.VELARA_AFFINITY
   );
   public static final AttributeCharm<Float> SMALL_TENOS = new AttributeCharm<>(
      CharmConfig.Size.SMALL, VaultMod.id("small_tenos"), ModGearAttributes.TENOS_AFFINITY
   );
   public static final AttributeCharm<Float> LARGE_TENOS = new AttributeCharm<>(
      CharmConfig.Size.LARGE, VaultMod.id("large_tenos"), ModGearAttributes.TENOS_AFFINITY
   );
   public static final AttributeCharm<Float> GRAND_TENOS = new AttributeCharm<>(
      CharmConfig.Size.GRAND, VaultMod.id("grand_tenos"), ModGearAttributes.TENOS_AFFINITY
   );
   public static final AttributeCharm<Float> MAJESTIC_TENOS = new AttributeCharm<>(
      CharmConfig.Size.MAJESTIC, VaultMod.id("majestic_tenos"), ModGearAttributes.TENOS_AFFINITY
   );
   public static final AttributeCharm<Float> SMALL_WENDARR = new AttributeCharm<>(
      CharmConfig.Size.SMALL, VaultMod.id("small_wendarr"), ModGearAttributes.WENDARR_AFFINITY
   );
   public static final AttributeCharm<Float> LARGE_WENDARR = new AttributeCharm<>(
      CharmConfig.Size.LARGE, VaultMod.id("large_wendarr"), ModGearAttributes.WENDARR_AFFINITY
   );
   public static final AttributeCharm<Float> GRAND_WENDARR = new AttributeCharm<>(
      CharmConfig.Size.GRAND, VaultMod.id("grand_wendarr"), ModGearAttributes.WENDARR_AFFINITY
   );
   public static final AttributeCharm<Float> MAJESTIC_WENDARR = new AttributeCharm<>(
      CharmConfig.Size.MAJESTIC, VaultMod.id("majestic_wendarr"), ModGearAttributes.WENDARR_AFFINITY
   );

   public static void init(Register<CharmEffect<?>> event) {
      IForgeRegistry<CharmEffect<?>> registry = event.getRegistry();
      registry.register(SMALL_IDONA);
      registry.register(LARGE_IDONA);
      registry.register(GRAND_IDONA);
      registry.register(MAJESTIC_IDONA);
      registry.register(SMALL_VELARA);
      registry.register(LARGE_VELARA);
      registry.register(GRAND_VELARA);
      registry.register(MAJESTIC_VELARA);
      registry.register(SMALL_TENOS);
      registry.register(LARGE_TENOS);
      registry.register(GRAND_TENOS);
      registry.register(MAJESTIC_TENOS);
      registry.register(SMALL_WENDARR);
      registry.register(LARGE_WENDARR);
      registry.register(GRAND_WENDARR);
      registry.register(MAJESTIC_WENDARR);
   }
}
