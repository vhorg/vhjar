package iskallia.vault.tags;

import iskallia.vault.VaultMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
   public static TagKey<Block> VOIDMINE_EXCLUSIONS = BlockTags.create(VaultMod.id("voidmine_exclusions"));
   public static TagKey<Block> FOLIAGE = BlockTags.create(VaultMod.id("foliage"));
}
