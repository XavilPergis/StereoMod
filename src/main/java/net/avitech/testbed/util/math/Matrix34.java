package net.avitech.testbed.util.math;

import javax.annotation.Nonnull;

import org.lwjgl.openvr.HmdMatrix34;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A single-indirection 3x4 matrix.
 * 
 * For details on naming and code layout conventions, see the documentation on
 * {@link Matrix44}.
 * 
 * @see Matrix44
 */
public final class Matrix34 implements Matrix {

    public float a00 = 0f, a01 = 0f, a02 = 0f, a03 = 0f;
    public float a10 = 0f, a11 = 0f, a12 = 0f, a13 = 0f;
    public float a20 = 0f, a21 = 0f, a22 = 0f, a23 = 0f;

    public Matrix34() {
    }

    // #region Matrix copy/cast operations

    public static @Nonnull Matrix copyToDynamicExplicit(@Nonnull Matrix34 src, @Nonnull Matrix dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var dstDimensions = dst.dimensions();
        if (dstDimensions.rows() != 3 || dstDimensions.columns() != 4) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to dynamic destination matrix of size %dx%d from source matrix of size 3x4.",
                    dstDimensions.rows(), dstDimensions.columns()));
        }

        dst.setDynamic(0, 0, src.a00);
        dst.setDynamic(0, 1, src.a01);
        dst.setDynamic(0, 2, src.a02);
        dst.setDynamic(0, 3, src.a03);
        dst.setDynamic(1, 0, src.a10);
        dst.setDynamic(1, 1, src.a11);
        dst.setDynamic(1, 2, src.a12);
        dst.setDynamic(1, 3, src.a13);
        dst.setDynamic(2, 0, src.a20);
        dst.setDynamic(2, 1, src.a21);
        dst.setDynamic(2, 2, src.a22);
        dst.setDynamic(2, 3, src.a23);
        return dst;
    }

    public static @Nonnull Matrix34 copyFromDynamicExplicit(@Nonnull Matrix src, @Nonnull Matrix34 dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var srcDimensions = src.dimensions();
        if (srcDimensions.rows() != 3 || srcDimensions.columns() != 4) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to destination matrix of size 3x4 from dynamic source matrix of size %dx%d.",
                    srcDimensions.rows(), srcDimensions.columns()));
        }

        dst.a00 = src.getDynamic(0, 0);
        dst.a01 = src.getDynamic(0, 1);
        dst.a02 = src.getDynamic(0, 2);
        dst.a03 = src.getDynamic(0, 3);
        dst.a10 = src.getDynamic(1, 0);
        dst.a11 = src.getDynamic(1, 1);
        dst.a12 = src.getDynamic(1, 2);
        dst.a13 = src.getDynamic(1, 3);
        dst.a20 = src.getDynamic(2, 0);
        dst.a21 = src.getDynamic(2, 1);
        dst.a22 = src.getDynamic(2, 2);
        dst.a23 = src.getDynamic(2, 3);
        return dst;
    }

    public @Nonnull Matrix34 copyFromDynamicInplace(@Nonnull Matrix src) {
        return copyFromDynamicExplicit(src, this);
    }

    public @Nonnull Matrix copyToDynamic(@Nonnull Matrix dst) {
        return copyToDynamicExplicit(this, dst);
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix34 copyExplicit(@Nonnull HmdMatrix34 src, @Nonnull Matrix34 dst) {
        // The matrix we get from OpenVR seems to be stored in a row-major order.
        int i = 0;
        dst.a00 = src.m(i++);
        dst.a01 = src.m(i++);
        dst.a02 = src.m(i++);
        dst.a03 = src.m(i++);
        dst.a10 = src.m(i++);
        dst.a11 = src.m(i++);
        dst.a12 = src.m(i++);
        dst.a13 = src.m(i++);
        dst.a20 = src.m(i++);
        dst.a21 = src.m(i++);
        dst.a22 = src.m(i++);
        dst.a23 = src.m(i++);
        return dst;
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix34 copy(@Nonnull HmdMatrix34 src) {
        return copyExplicit(src, new Matrix34());
    }

    @Environment(EnvType.CLIENT)
    public @Nonnull Matrix34 copyFromInplace(@Nonnull HmdMatrix34 src) {
        return copyExplicit(src, this);
    }

    public static @Nonnull Matrix34 copyExplicit(@Nonnull Matrix34 src, @Nonnull Matrix34 dst) {
        dst.a00 = src.a00;
        dst.a01 = src.a01;
        dst.a02 = src.a02;
        dst.a03 = src.a03;
        dst.a10 = src.a10;
        dst.a11 = src.a11;
        dst.a12 = src.a12;
        dst.a13 = src.a13;
        dst.a20 = src.a20;
        dst.a21 = src.a21;
        dst.a22 = src.a22;
        dst.a23 = src.a23;
        return dst;
    }

    public static @Nonnull Matrix34 copy(@Nonnull Matrix34 src) {
        return copyExplicit(src, new Matrix34());
    }

    public @Nonnull Matrix34 copyFromInplace(@Nonnull Matrix34 src) {
        return copyExplicit(src, this);
    }

    // #endregion
    // #region Matrix interface implementation

    @Override
    public @Nonnull Dimensions dimensions() {
        return new Dimensions(3, 4);
    }

    @Override
    public float getDynamic(int row, int col) {
        // @formatter:off
        switch (row) {
            case 0: switch(col) {
                case 0: return this.a00;
                case 1: return this.a01;
                case 2: return this.a02;
                case 3: return this.a03;
                default: break;
            }
            case 1: switch(col) {
                case 0: return this.a10;
                case 1: return this.a11;
                case 2: return this.a12;
                case 3: return this.a13;
                default: break;
            }
            case 2: switch(col) {
                case 0: return this.a20;
                case 1: return this.a21;
                case 2: return this.a22;
                case 3: return this.a23;
                default: break;
            }
            default: break;
        }
        // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 3x4 matrix", row, col));
    }

    @Override
    public void setDynamic(int row, int col, float value) {
        // @formatter:off
        switch (row) {
            case 0: switch(col) {
                case 0: this.a00 = value; return;
                case 1: this.a01 = value; return;
                case 2: this.a02 = value; return;
                case 3: this.a03 = value; return;
                default: break;
            }
            case 1: switch(col) {
                case 0: this.a10 = value; return;
                case 1: this.a11 = value; return;
                case 2: this.a12 = value; return;
                case 3: this.a13 = value; return;
                default: break;
            }
            case 2: switch(col) {
                case 0: this.a20 = value; return;
                case 1: this.a21 = value; return;
                case 2: this.a22 = value; return;
                case 3: this.a23 = value; return;
                default: break;
            }
            default: break;
        }
        // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 3x4 matrix", row, col));
    }

    // #endregion

}
