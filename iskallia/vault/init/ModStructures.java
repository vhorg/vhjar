package iskallia.vault.init;

import com.mojang.serialization.Codec;
import iskallia.vault.VaultMod;
import iskallia.vault.world.gen.structure.ArchitectEventStructure;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.gen.structure.FinalVaultLobbyStructure;
import iskallia.vault.world.gen.structure.RaidChallengeStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import iskallia.vault.world.gen.structure.pool.PalettedListPoolElement;
import iskallia.vault.world.gen.structure.pool.PalettedSinglePoolElement;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStructures {
   public static final TagKey<Biome> EMPTY = TagKey.create(Registry.BIOME_REGISTRY, VaultMod.id("empty"));
   public static VaultStructure VAULT_STAR;
   public static ArenaStructure ARENA;
   public static ArchitectEventStructure ARCHITECT_EVENT;
   public static RaidChallengeStructure RAID_CHALLENGE;
   public static VaultTroveStructure VAULT_TROVE;
   public static FinalVaultLobbyStructure FINAL_VAULT_LOBBY;

   public static void register(Register<StructureFeature<?>> event) {
      VAULT_STAR = register(event.getRegistry(), "vault_star", new VaultStructure());
      ARENA = register(event.getRegistry(), "arena", new ArenaStructure());
      ARCHITECT_EVENT = register(event.getRegistry(), "architect_event", new ArchitectEventStructure());
      RAID_CHALLENGE = register(event.getRegistry(), "raid_challenge", new RaidChallengeStructure());
      VAULT_TROVE = register(event.getRegistry(), "trove", new VaultTroveStructure());
      FINAL_VAULT_LOBBY = register(event.getRegistry(), "final_vault_lobby", new FinalVaultLobbyStructure());
      ModStructures.PoolElements.register(event);
   }

   private static <T extends StructureFeature<?>> T register(IForgeRegistry<StructureFeature<?>> registry, String name, T structure) {
      structure.setRegistryName(VaultMod.id(name));
      registry.register(structure);
      return structure;
   }

   public static class PoolElements {
      public static StructurePoolElementType<PalettedSinglePoolElement> PALETTED_SINGLE_POOL_ELEMENT;
      public static StructurePoolElementType<PalettedListPoolElement> PALETTED_LIST_POOL_ELEMENT;

      public static void register(Register<StructureFeature<?>> event) {
         PALETTED_SINGLE_POOL_ELEMENT = register("paletted_single_pool_element", PalettedSinglePoolElement.CODEC);
         PALETTED_LIST_POOL_ELEMENT = register("paletted_list_pool_element", PalettedListPoolElement.CODEC);
      }

      static <P extends StructurePoolElement> StructurePoolElementType<P> register(String name, Codec<P> codec) {
         return (StructurePoolElementType<P>)Registry.register(Registry.STRUCTURE_POOL_ELEMENT, VaultMod.id(name), (StructurePoolElementType)() -> codec);
      }
   }
}
