package net.avitech.testbed.feature.anaglyph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.math.Matrix4f;

@Mixin(Matrix4f.class)
public interface Matrix4fMixin {

    // @formatter:off
    @Accessor public void setA00(float value);
    @Accessor public void setA01(float value);
    @Accessor public void setA02(float value);
    @Accessor public void setA03(float value);
    @Accessor public void setA10(float value);
    @Accessor public void setA11(float value);
    @Accessor public void setA12(float value);
    @Accessor public void setA13(float value);
    @Accessor public void setA20(float value);
    @Accessor public void setA21(float value);
    @Accessor public void setA22(float value);
    @Accessor public void setA23(float value);
    @Accessor public void setA30(float value);
    @Accessor public void setA31(float value);
    @Accessor public void setA32(float value);
    @Accessor public void setA33(float value);

    @Accessor public float getA00();
    @Accessor public float getA01();
    @Accessor public float getA02();
    @Accessor public float getA03();
    @Accessor public float getA10();
    @Accessor public float getA11();
    @Accessor public float getA12();
    @Accessor public float getA13();
    @Accessor public float getA20();
    @Accessor public float getA21();
    @Accessor public float getA22();
    @Accessor public float getA23();
    @Accessor public float getA30();
    @Accessor public float getA31();
    @Accessor public float getA32();
    @Accessor public float getA33();
    // @formatter:on

}
