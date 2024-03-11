package net.avitech.testbed.util.math;

import net.minecraft.util.math.MathHelper;

public class Quaternion {

    public float cdiv2;
    public float xsdiv2;
    public float ysdiv2;
    public float zsdiv2;

    private Quaternion() {
    }

    public static Quaternion makeAxisAngleExplicit(float x, float y, float z, float theta, Quaternion dst) {
        dst.cdiv2 = MathHelper.cos(theta / 2f);
        float sdiv2 = MathHelper.sin(theta / 2f);
        dst.xsdiv2 = x * sdiv2;
        dst.ysdiv2 = y * sdiv2;
        dst.zsdiv2 = z * sdiv2;
        return dst;
    }

    public static Quaternion makeAxisAngle(float x, float y, float z, float theta) {
        return makeAxisAngleExplicit(x, y, z, theta, new Quaternion());
    }

}
