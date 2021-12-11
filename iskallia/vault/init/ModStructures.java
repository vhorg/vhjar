package iskallia.vault.init;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import iskallia.vault.world.gen.structure.ArchitectEventStructure;
import iskallia.vault.world.gen.structure.RaidChallengeStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStructures {
   public static Structure<VaultStructure.Config> VAULT_STAR;
   public static Structure<ArchitectEventStructure.Config> ARCHITECT_EVENT;
   public static Structure<RaidChallengeStructure.Config> RAID_CHALLENGE;
   public static Structure<VaultTroveStructure.Config> VAULT_TROVE;

   public static void register(Register<Structure<?>> event) {
      VAULT_STAR = register(event.getRegistry(), "vault_star", new VaultStructure(VaultStructure.Config.CODEC));
      ARCHITECT_EVENT = register(event.getRegistry(), "architect_event", new ArchitectEventStructure(ArchitectEventStructure.Config.CODEC));
      RAID_CHALLENGE = register(event.getRegistry(), "raid_challenge", new RaidChallengeStructure(RaidChallengeStructure.Config.CODEC));
      VAULT_TROVE = register(event.getRegistry(), "trove", new VaultTroveStructure(VaultTroveStructure.Config.CODEC));
      ModStructures.PoolElements.register(event);
   }

   private static <T extends Structure<?>> T register(IForgeRegistry<Structure<?>> registry, String name, T structure) {
      Structure.field_236365_a_.put(name, structure);
      structure.setRegistryName(Vault.id(name));
      registry.register(structure);
      return structure;
   }

   public static class PoolElements {
      public static IJigsawDeserializer<PalettedSinglePoolElement> PALETTED_SINGLE_POOL_ELEMENT;
      public static IJigsawDeserializer<PalettedListPoolElement> PALETTED_LIST_POOL_ELEMENT;

      public static void register(Register<Structure<?>> event) {
         PALETTED_SINGLE_POOL_ELEMENT = register("paletted_single_pool_element", PalettedSinglePoolElement.CODEC);
         PALETTED_LIST_POOL_ELEMENT = register("paletted_list_pool_element", PalettedListPoolElement.CODEC);
      }

      static <P extends JigsawPiece> IJigsawDeserializer<P> register(String name, Codec<P> codec) {
         return (IJigsawDeserializer<P>)Registry.func_218322_a(Registry.field_218365_F, Vault.id(name), (IJigsawDeserializer)() -> codec);
      }
   }
}
