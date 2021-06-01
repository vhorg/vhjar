package iskallia.vault.world.gen.ruletest;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.RuleTest;

public class VaultRuleTest extends RuleTest {
   public static final VaultRuleTest INSTANCE = new VaultRuleTest();
   public static final Codec<VaultRuleTest> CODEC = Codec.unit(() -> INSTANCE);
   public static final IRuleTestType<VaultRuleTest> TYPE = register("vault_stone_match", CODEC);

   public boolean func_215181_a(BlockState state, Random random) {
      return state.func_200132_m() && state.func_185904_a() == Material.field_151576_e;
   }

   protected IRuleTestType<?> func_215180_a() {
      return TYPE;
   }

   static <P extends RuleTest> IRuleTestType<P> register(String name, Codec<P> codec) {
      return (IRuleTestType<P>)Registry.func_218322_a(Registry.field_218363_D, Vault.id(name), (IRuleTestType)() -> codec);
   }
}
