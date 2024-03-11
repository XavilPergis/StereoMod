package net.avitech.testbed.util.math;

public class EulerAngles {

    public float angleX = 0f;
    public float angleY = 0f;
    public float angleZ = 0f;

    private EulerAngles() {
    }

    public static EulerAngles makeXyzRotationExplicit(float angleX, float angleY, float angleZ, EulerAngles dst) {
        dst.angleX = angleX;
        dst.angleY = angleY;
        dst.angleZ = angleZ;
        return dst;
    }

    public static EulerAngles makeXyzRotation(float angleX, float angleY, float angleZ) {
        return makeXyzRotationExplicit(angleX, angleY, angleZ, new EulerAngles());
    }

    public static EulerAngles makeIdentityExplicit(EulerAngles dst) {
        dst.angleX = 0f;
        dst.angleY = 0f;
        dst.angleZ = 0f;
        return dst;
    }

    public static EulerAngles makeIdentity(EulerAngles dst) {
        return new EulerAngles();
    }

}
