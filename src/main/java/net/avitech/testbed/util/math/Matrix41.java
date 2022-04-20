package net.avitech.testbed.util.math;

import javax.annotation.Nonnull;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class Matrix41 implements Matrix {
    public float a00 = 0f;
    public float a10 = 0f;
    public float a20 = 0f;
    public float a30 = 0f;

    private Matrix41() {
    }

    // @formatter:off
    public float getX() { return this.a00; }
    public float getY() { return this.a10; }
    public float getZ() { return this.a20; }
    public float getW() { return this.a30; }
    public void setX(float value) { this.a00 = value; }
    public void setY(float value) { this.a10 = value; }
    public void setZ(float value) { this.a20 = value; }
    public void setW(float value) { this.a30 = value; }
    // @formatter:on

    // #region matrix constructors

    public static void makeZeroedExplicit(Matrix41 dst) {
        dst.a00 = 0f;
        dst.a10 = 0f;
        dst.a20 = 0f;
        dst.a30 = 0f;
    }

    public static @Nonnull Matrix41 makeZeroed() {
        return new Matrix41();
    }

    public static Matrix41 makeXyzwExplicit(float x, float y, float z, float w, Matrix41 dst) {
        dst.a00 = x;
        dst.a10 = y;
        dst.a20 = z;
        dst.a30 = w;
        return dst;
    }

    public static @Nonnull Matrix41 makeXyzw(float x, float y, float z, float w) {
        return makeXyzwExplicit(x, y, z, w, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz1(float x, float y, float z) {
        return makeXyzwExplicit(x, y, z, 1f, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz0(float x, float y, float z) {
        return makeXyzwExplicit(x, y, z, 0f, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyzw(Vec3f vec, float w) {
        return makeXyzwExplicit(vec.getX(), vec.getY(), vec.getZ(), w, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz1(Vec3f vec) {
        return makeXyzwExplicit(vec.getX(), vec.getY(), vec.getZ(), 1f, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz0(Vec3f vec) {
        return makeXyzwExplicit(vec.getX(), vec.getY(), vec.getZ(), 0f, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyzw(Vec3d vec, float w) {
        return makeXyzwExplicit((float) vec.getX(), (float) vec.getY(), (float) vec.getZ(), w, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz1(Vec3d vec) {
        return makeXyzwExplicit((float) vec.getX(), (float) vec.getY(), (float) vec.getZ(), 1f, new Matrix41());
    }

    public static @Nonnull Matrix41 makeXyz0(Vec3d vec) {
        return makeXyzwExplicit((float) vec.getX(), (float) vec.getY(), (float) vec.getZ(), 0f, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosPositiveX() {
        return makeXyzwExplicit(1, 0, 0, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosPositiveY() {
        return makeXyzwExplicit(0, 1, 0, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosPositiveZ() {
        return makeXyzwExplicit(0, 0, 1, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosNegativeX() {
        return makeXyzwExplicit(-1, 0, 0, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosNegativeY() {
        return makeXyzwExplicit(0, -1, 0, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makePosNegativeZ() {
        return makeXyzwExplicit(0, 0, -1, 1, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirPositiveX() {
        return makeXyzwExplicit(1, 0, 0, 0, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirPositiveY() {
        return makeXyzwExplicit(0, 1, 0, 0, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirPositiveZ() {
        return makeXyzwExplicit(0, 0, 1, 0, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirNegativeX() {
        return makeXyzwExplicit(-1, 0, 0, 0, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirNegativeY() {
        return makeXyzwExplicit(0, -1, 0, 0, new Matrix41());
    }

    public static @Nonnull Matrix41 makeDirNegativeZ() {
        return makeXyzwExplicit(0, 0, -1, 0, new Matrix41());
    }

    // #endregion
    // #region Matrix copy/cast operations

    public static @Nonnull Matrix copyToDynamicExplicit(@Nonnull Matrix41 src, @Nonnull Matrix dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var dstDimensions = dst.dimensions();
        if (dstDimensions.rows() != 4 || dstDimensions.columns() != 1) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to dynamic destination matrix of size %dx%d from source matrix of size 4x1.",
                    dstDimensions.rows(), dstDimensions.columns()));
        }

        dst.setDynamic(0, 0, src.a00);
        dst.setDynamic(1, 0, src.a10);
        dst.setDynamic(2, 0, src.a20);
        dst.setDynamic(3, 0, src.a30);
        return dst;
    }

    public static @Nonnull Matrix41 copyFromDynamicExplicit(@Nonnull Matrix src, @Nonnull Matrix41 dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var srcDimensions = src.dimensions();
        if (srcDimensions.rows() != 4 || srcDimensions.columns() != 1) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to destination matrix of size 4x1 from dynamic source matrix of size %dx%d.",
                    srcDimensions.rows(), srcDimensions.columns()));
        }

        dst.a00 = src.getDynamic(0, 0);
        dst.a10 = src.getDynamic(1, 0);
        dst.a20 = src.getDynamic(2, 0);
        dst.a30 = src.getDynamic(3, 0);
        return dst;
    }

    public @Nonnull Matrix41 copyFromDynamicInplace(@Nonnull Matrix src) {
        return copyFromDynamicExplicit(src, this);
    }

    public @Nonnull Matrix copyToDynamic(@Nonnull Matrix dst) {
        return copyToDynamicExplicit(this, dst);
    }

    public static @Nonnull Matrix41 copyExplicit(@Nonnull Matrix41 src, @Nonnull Matrix41 dst) {
        dst.a00 = src.a00;
        dst.a10 = src.a10;
        dst.a20 = src.a20;
        dst.a30 = src.a30;
        return dst;
    }

    public static @Nonnull Matrix41 copy(@Nonnull Matrix41 src) {
        return copyExplicit(src, new Matrix41());
    }

    public @Nonnull Matrix41 copyFromInplace(@Nonnull Matrix41 src) {
        return copyExplicit(src, this);
    }

    public static @Nonnull Matrix41 copyExplicit(@Nonnull Vector4f src, @Nonnull Matrix41 dst) {
        dst.a00 = src.getX();
        dst.a10 = src.getY();
        dst.a20 = src.getZ();
        dst.a30 = src.getW();
        return dst;
    }

    public static @Nonnull Vector4f copyExplicit(@Nonnull Matrix41 src, @Nonnull Vector4f dst) {
        dst.set(src.a00, src.a10, src.a20, src.a30);
        return dst;
    }

    public static @Nonnull Matrix41 copy(@Nonnull Vector4f src) {
        return copyExplicit(src, new Matrix41());
    }

    public @Nonnull Matrix41 copyFromInplace(@Nonnull Vector4f src) {
        return copyExplicit(src, this);
    }

    public @Nonnull Vector4f copyToInplace(@Nonnull Vector4f dst) {
        return copyExplicit(this, dst);
    }

    // #endregion
    // #region Matrix interface implementation

    @Override
    public @Nonnull Dimensions dimensions() {
        return new Dimensions(4, 1);
    }

    @Override
    public float getDynamic(int row, int col) {
        // @formatter:off
            switch (row) {
                case 0: switch(col) {
                    case 0: return this.a00;
                    default: break;
                }
                case 1: switch(col) {
                    case 0: return this.a10;
                    default: break;
                }
                case 2: switch(col) {
                    case 0: return this.a20;
                    default: break;
                }
                case 3: switch(col) {
                    case 0: return this.a20;
                    default: break;
                }
                default: break;
            }
            // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 4x1 matrix", row, col));
    }

    @Override
    public void setDynamic(int row, int col, float value) {
        // @formatter:off
            switch (row) {
                case 0: switch(col) {
                    case 0: this.a00 = value; return;
                    default: break;
                }
                case 1: switch(col) {
                    case 0: this.a10 = value; return;
                    default: break;
                }
                case 2: switch(col) {
                    case 0: this.a20 = value; return;
                    default: break;
                }
                case 3: switch(col) {
                    case 0: this.a30 = value; return;
                    default: break;
                }
                default: break;
            }
            // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 4x1 matrix", row, col));
    }

    // #endregion

}
