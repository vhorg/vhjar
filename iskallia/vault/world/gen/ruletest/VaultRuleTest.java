package iskallia.vault.world.gen.ruletest;

import com.mojang.serialization.Codec;
import iskallia.vault.VaultMod;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.material.Material;

public class VaultRuleTest extends RuleTest {
   public static final VaultRuleTest INSTANCE = new VaultRuleTest();
   public static final Codec<VaultRuleTest> CODEC = Codec.unit(() -> INSTANCE);
   public static final RuleTestType<VaultRuleTest> TYPE = register("vault_stone_match", CODEC);

   public boolean test(BlockState state, Random random) {
      return state.canOcclude() && state.getMaterial() == Material.STONE;
   }

   protected RuleTestType<?> getType() {
      return TYPE;
   }

   static <P extends RuleTest> RuleTestType<P> register(String name, Codec<P> codec) {
      return (RuleTestType<P>)Registry.register(Registry.RULE_TEST, VaultMod.id(name), (RuleTestType)() -> codec);
   }
}
