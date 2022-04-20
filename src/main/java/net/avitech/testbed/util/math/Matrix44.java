package net.avitech.testbed.util.math;

import javax.annotation.Nonnull;

import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;

import net.avitech.testbed.feature.anaglyph.mixin.Matrix4fMixin;
import net.avitech.testbed.util.Option;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;

/**
 * A single-indirection 4x4 matrix.
 * 
 * The entries of this matrix can be accessed either through {@code aMN}
 * members, where M is the row and N is the column, or (less efficiently)
 * through {@link #getDynamic} and {@link #setDynamic}.
 * 
 * The behaviors of operations change depending on their suffixes. The meaning
 * of each suffix is listed below:
 * 
 * <ul>
 * <li>"Explicit" suffix - Takes in operation parameters and an explicit output
 * parameter, stores the result of the operation in the output parameter, and
 * returns the output parameter.</li>
 * 
 * <li>"Inplace" suffix - Takes in operation parameters, stores the result of
 * the operation in the documented operation parameter, and returns the modified
 * parameter.</li>
 * 
 * <li>no suffix - Takes in operation parameters, and stores the result of the
 * operation in a new matrix object, returning the newly-created result.</li>
 * </ul>
 * 
 */
public class Matrix44 implements Matrix {

    public float a00 = 0f, a01 = 0f, a02 = 0f, a03 = 0f;
    public float a10 = 0f, a11 = 0f, a12 = 0f, a13 = 0f;
    public float a20 = 0f, a21 = 0f, a22 = 0f, a23 = 0f;
    public float a30 = 0f, a31 = 0f, a32 = 0f, a33 = 0f;

    // Explicitly disabled. Matrices can be created with "Matrix44.make*" methods.
    private Matrix44() {
    }

    // #region Matrix constructors

    public static void makeZeroedExplicit(Matrix44 dst) {
        dst.a00 = 0f;
        dst.a01 = 0f;
        dst.a02 = 0f;
        dst.a03 = 0f;
        dst.a10 = 0f;
        dst.a11 = 0f;
        dst.a12 = 0f;
        dst.a13 = 0f;
        dst.a20 = 0f;
        dst.a21 = 0f;
        dst.a22 = 0f;
        dst.a23 = 0f;
        dst.a30 = 0f;
        dst.a31 = 0f;
        dst.a32 = 0f;
        dst.a33 = 0f;
    }

    public static @Nonnull Matrix44 makeZeroed() {
        return new Matrix44();
    }

    public static Matrix44 makeIdentityExplicit(@Nonnull Matrix44 dst) {
        dst.a00 = 1f;
        dst.a01 = 0f;
        dst.a02 = 0f;
        dst.a03 = 0f;
        dst.a10 = 0f;
        dst.a11 = 1f;
        dst.a12 = 0f;
        dst.a13 = 0f;
        dst.a20 = 0f;
        dst.a21 = 0f;
        dst.a22 = 1f;
        dst.a23 = 0f;
        dst.a30 = 0f;
        dst.a31 = 0f;
        dst.a32 = 0f;
        dst.a33 = 1f;
        return dst;
    }

    public static @Nonnull Matrix44 makeIdentity() {
        Matrix44 dst = new Matrix44();
        dst.a00 = 1f;
        dst.a11 = 1f;
        dst.a22 = 1f;
        dst.a33 = 1f;
        return dst;
    }

    public static @Nonnull Matrix44 makeTranslationExplicit(float x, float y, float z, @Nonnull Matrix44 dst) {
        makeIdentityExplicit(dst);
        dst.a03 = x;
        dst.a13 = y;
        dst.a23 = z;
        return dst;
    }

    public static @Nonnull Matrix44 makeTranslation(float x, float y, float z, @Nonnull Matrix44 mat) {
        return makeTranslationExplicit(x, y, z, new Matrix44());
    }

    public static @Nonnull Matrix44 makeScaleExplicit(float x, float y, float z, @Nonnull Matrix44 dst) {
        makeIdentityExplicit(dst);
        dst.a00 = x;
        dst.a11 = y;
        dst.a22 = z;
        dst.a33 = 1f;
        return dst;
    }

    public static @Nonnull Matrix44 makeScale(float x, float y, float z) {
        return makeScaleExplicit(x, y, z, new Matrix44());
    }

    // #endregion
    // #region Matrix copy/cast operations

    public static @Nonnull Matrix copyToDynamicExplicit(@Nonnull Matrix44 src, @Nonnull Matrix dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var dstDimensions = dst.dimensions();
        if (dstDimensions.rows() != 4 || dstDimensions.columns() != 4) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to dynamic destination matrix of size %dx%d from source matrix of size 4x4.",
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
        dst.setDynamic(3, 0, src.a30);
        dst.setDynamic(3, 1, src.a31);
        dst.setDynamic(3, 2, src.a32);
        dst.setDynamic(3, 3, src.a33);
        return dst;
    }

    public static @Nonnull Matrix44 copyFromDynamicExplicit(@Nonnull Matrix src, @Nonnull Matrix44 dst) {
        // Copies to self have no effect.
        if (src == dst) {
            return dst;
        }

        var srcDimensions = src.dimensions();
        if (srcDimensions.rows() != 4 || srcDimensions.columns() != 4) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to destination matrix of size 4x4 from dynamic source matrix of size %dx%d.",
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
        dst.a30 = src.getDynamic(3, 0);
        dst.a31 = src.getDynamic(3, 1);
        dst.a32 = src.getDynamic(3, 2);
        dst.a33 = src.getDynamic(3, 3);
        return dst;
    }

    public @Nonnull Matrix44 copyFromDynamicInplace(@Nonnull Matrix src) {
        return copyFromDynamicExplicit(src, this);
    }

    public @Nonnull Matrix copyToDynamic(@Nonnull Matrix dst) {
        return copyToDynamicExplicit(this, dst);
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix44 copyExplicit(@Nonnull HmdMatrix44 src, @Nonnull Matrix44 dst) {
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
        dst.a30 = src.m(i++);
        dst.a31 = src.m(i++);
        dst.a32 = src.m(i++);
        dst.a33 = src.m(i++);
        return dst;
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix44 copy(@Nonnull HmdMatrix44 src) {
        return copyExplicit(src, new Matrix44());
    }

    @Environment(EnvType.CLIENT)
    public @Nonnull Matrix44 copyFromInplace(@Nonnull HmdMatrix44 src) {
        return copyExplicit(src, this);
    }

    public static @Nonnull Matrix44 copyExplicit(@Nonnull Matrix44 src, @Nonnull Matrix44 dst) {
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
        dst.a30 = src.a30;
        dst.a31 = src.a31;
        dst.a32 = src.a32;
        dst.a33 = src.a33;
        return dst;
    }

    public static @Nonnull Matrix44 copy(@Nonnull Matrix44 src) {
        return copyExplicit(src, new Matrix44());
    }

    public @Nonnull Matrix44 copyFromInplace(@Nonnull Matrix44 src) {
        return copyExplicit(src, this);
    }

    public @Nonnull Matrix44 copyToInplace(@Nonnull Matrix44 dst) {
        return copyExplicit(this, dst);
    }

    public @Nonnull Matrix44 copy() {
        return copyExplicit(this, new Matrix44());
    }

    public static @Nonnull Matrix44 copyExplicit(@Nonnull Matrix4f src, @Nonnull Matrix44 dst) {
        var srcAccess = (Matrix4fMixin) (Object) src;
        dst.a00 = srcAccess.getA00();
        dst.a01 = srcAccess.getA01();
        dst.a02 = srcAccess.getA02();
        dst.a03 = srcAccess.getA03();
        dst.a10 = srcAccess.getA10();
        dst.a11 = srcAccess.getA11();
        dst.a12 = srcAccess.getA12();
        dst.a13 = srcAccess.getA13();
        dst.a20 = srcAccess.getA20();
        dst.a21 = srcAccess.getA21();
        dst.a22 = srcAccess.getA22();
        dst.a23 = srcAccess.getA23();
        dst.a30 = srcAccess.getA30();
        dst.a31 = srcAccess.getA31();
        dst.a32 = srcAccess.getA32();
        dst.a33 = srcAccess.getA33();
        return dst;
    }

    public static @Nonnull Matrix4f copyExplicit(@Nonnull Matrix44 src, @Nonnull Matrix4f dst) {
        var dstAccess = (Matrix4fMixin) (Object) dst;
        dstAccess.setA00(src.a00);
        dstAccess.setA01(src.a01);
        dstAccess.setA02(src.a02);
        dstAccess.setA03(src.a03);
        dstAccess.setA10(src.a10);
        dstAccess.setA11(src.a11);
        dstAccess.setA12(src.a12);
        dstAccess.setA13(src.a13);
        dstAccess.setA20(src.a20);
        dstAccess.setA21(src.a21);
        dstAccess.setA22(src.a22);
        dstAccess.setA23(src.a23);
        dstAccess.setA30(src.a30);
        dstAccess.setA31(src.a31);
        dstAccess.setA32(src.a32);
        dstAccess.setA33(src.a33);
        return dst;
    }

    public static @Nonnull Matrix44 copy(@Nonnull Matrix4f src) {
        return copyExplicit(src, new Matrix44());
    }

    public @Nonnull Matrix44 copyFromInplace(@Nonnull Matrix4f src) {
        return copyExplicit(src, this);
    }

    public @Nonnull Matrix4f copyToInplace(@Nonnull Matrix4f dst) {
        return copyExplicit(this, dst);
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix44 copyExplicit(@Nonnull HmdMatrix34 src, @Nonnull Matrix44 dst) {
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
        dst.a30 = 0f;
        dst.a31 = 0f;
        dst.a32 = 0f;
        dst.a33 = 1f;
        return dst;
    }

    @Environment(EnvType.CLIENT)
    public static @Nonnull Matrix44 copy(@Nonnull HmdMatrix34 src) {
        return copyExplicit(src, new Matrix44());
    }

    @Environment(EnvType.CLIENT)
    public @Nonnull Matrix44 copyFromInplace(@Nonnull HmdMatrix34 src) {
        return copyExplicit(src, this);
    }

    public static @Nonnull Matrix44 copyExplicit(@Nonnull Matrix34 src, @Nonnull Matrix44 dst) {
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
        dst.a30 = 0f;
        dst.a31 = 0f;
        dst.a32 = 0f;
        dst.a33 = 1f;
        return dst;
    }

    public static @Nonnull Matrix44 copy(@Nonnull Matrix34 src) {
        return copyExplicit(src, new Matrix44());
    }

    public @Nonnull Matrix44 copyFromInplace(@Nonnull Matrix34 src) {
        return copyExplicit(src, this);
    }

    // #endregion
    // #region Matrix multiplication operations

    public static @Nonnull Matrix44 multiplyExplicit(@Nonnull Matrix44 lhs, @Nonnull Matrix44 rhs,
            @Nonnull Matrix44 dst) {
        // each line follows this formula, where N represents the size of the matrix:
        // foreach((r, c) in (0..N) * (0..N)) res[r, c] = dot(lhs.rows[r], rhs.cols[c])

        // each assignment rhs follows this formula
        // dot(a,b,N) = sum(n in 0..N, a[n] * b[n])

        // res[0,0] = (lhs[0,0] * rhs[0,0]) + (lhs[0,1] * rhs[1,0]) + ...
        float res00 = (lhs.a00 * rhs.a00) + (lhs.a01 * rhs.a10) + (lhs.a02 * rhs.a20) + (lhs.a03 * rhs.a30);
        float res01 = (lhs.a00 * rhs.a01) + (lhs.a01 * rhs.a11) + (lhs.a02 * rhs.a21) + (lhs.a03 * rhs.a31);
        float res02 = (lhs.a00 * rhs.a02) + (lhs.a01 * rhs.a12) + (lhs.a02 * rhs.a22) + (lhs.a03 * rhs.a32);
        float res03 = (lhs.a00 * rhs.a03) + (lhs.a01 * rhs.a13) + (lhs.a02 * rhs.a23) + (lhs.a03 * rhs.a33);
        float res10 = (lhs.a10 * rhs.a00) + (lhs.a11 * rhs.a10) + (lhs.a12 * rhs.a20) + (lhs.a13 * rhs.a30);
        float res11 = (lhs.a10 * rhs.a01) + (lhs.a11 * rhs.a11) + (lhs.a12 * rhs.a21) + (lhs.a13 * rhs.a31);
        float res12 = (lhs.a10 * rhs.a02) + (lhs.a11 * rhs.a12) + (lhs.a12 * rhs.a22) + (lhs.a13 * rhs.a32);
        float res13 = (lhs.a10 * rhs.a03) + (lhs.a11 * rhs.a13) + (lhs.a12 * rhs.a23) + (lhs.a13 * rhs.a33);
        float res20 = (lhs.a20 * rhs.a00) + (lhs.a21 * rhs.a10) + (lhs.a22 * rhs.a20) + (lhs.a23 * rhs.a30);
        float res21 = (lhs.a20 * rhs.a01) + (lhs.a21 * rhs.a11) + (lhs.a22 * rhs.a21) + (lhs.a23 * rhs.a31);
        float res22 = (lhs.a20 * rhs.a02) + (lhs.a21 * rhs.a12) + (lhs.a22 * rhs.a22) + (lhs.a23 * rhs.a32);
        float res23 = (lhs.a20 * rhs.a03) + (lhs.a21 * rhs.a13) + (lhs.a22 * rhs.a23) + (lhs.a23 * rhs.a33);
        float res30 = (lhs.a30 * rhs.a00) + (lhs.a31 * rhs.a10) + (lhs.a32 * rhs.a20) + (lhs.a33 * rhs.a30);
        float res31 = (lhs.a30 * rhs.a01) + (lhs.a31 * rhs.a11) + (lhs.a32 * rhs.a21) + (lhs.a33 * rhs.a31);
        float res32 = (lhs.a30 * rhs.a02) + (lhs.a31 * rhs.a12) + (lhs.a32 * rhs.a22) + (lhs.a33 * rhs.a32);
        float res33 = (lhs.a30 * rhs.a03) + (lhs.a31 * rhs.a13) + (lhs.a32 * rhs.a23) + (lhs.a33 * rhs.a33);

        dst.a00 = res00;
        dst.a01 = res01;
        dst.a02 = res02;
        dst.a03 = res03;
        dst.a10 = res10;
        dst.a11 = res11;
        dst.a12 = res12;
        dst.a13 = res13;
        dst.a20 = res20;
        dst.a21 = res21;
        dst.a22 = res22;
        dst.a23 = res23;
        dst.a30 = res30;
        dst.a31 = res31;
        dst.a32 = res32;
        dst.a33 = res33;

        return dst;
    }

    public static @Nonnull Matrix44 multiply(@Nonnull Matrix44 lhs, @Nonnull Matrix44 rhs) {
        return multiplyExplicit(lhs, rhs, new Matrix44());
    }

    public @Nonnull Matrix44 multiplyWithRhsInplace(@Nonnull Matrix44 rhs) {
        return multiplyExplicit(this, rhs, this);
    }

    public @Nonnull Matrix44 multiplyWithLhsInplace(@Nonnull Matrix44 lhs) {
        return multiplyExplicit(lhs, this, this);
    }

    public @Nonnull Matrix44 multiplyWithRhs(@Nonnull Matrix44 rhs) {
        return multiplyExplicit(this, rhs, new Matrix44());
    }

    /**
     * Equivalent to {@code Matrix44.multiply(lhs, this)}.
     * 
     * This method allows for natural composition of transforms by method chaining.
     * If {@code lhs} was a transformation from space B to space C, and {@code this}
     * was a transformation from space A -> B, the result of this method would be a
     * matrix that transforms space A -> C. In other words, this applies
     * {@code lhs}'s transformation <i>after</i> {@code this}'s transformation.
     * 
     * Note that this method produces the correct order of transformation when the
     * final matrix is used to transform <i>column</i> vectors, and if you need to
     * transform <i>row</i> vectors, the roles of this method and
     * {@link #multiplyWithRhs(Matrix44)} are reversed.
     * 
     * To apply {@code lhs} before {@code this}, see
     * {@link #multiplyWithRhs(Matrix44)}.
     */
    public @Nonnull Matrix44 multiplyWithLhs(@Nonnull Matrix44 lhs) {
        return multiplyExplicit(lhs, this, new Matrix44());
    }

    public @Nonnull Matrix44 applyAfter(@Nonnull Matrix44 lhs) {
        return multiplyExplicit(lhs, this, new Matrix44());
    }

    public @Nonnull Matrix44 applyBefore(@Nonnull Matrix44 rhs) {
        return multiplyExplicit(this, rhs, new Matrix44());
    }

    public static @Nonnull Matrix41 multiplyExplicit(@Nonnull Matrix44 lhs, @Nonnull Matrix41 rhs,
            @Nonnull Matrix41 dst) {
        // each line follows this formula, where N represents the size of the matrix:
        // foreach((r, c) in (0..N) * (0..N)) res[r, c] = dot(lhs.rows[r], rhs.cols[c])

        // each assignment rhs follows this formula
        // dot(a,b,N) = sum(n in 0..N, a[n] * b[n])

        // res[0,0] = (lhs[0,0] * rhs[0,0]) + (lhs[0,1] * rhs[1,0]) + ...
        float res00 = (lhs.a00 * rhs.a00) + (lhs.a01 * rhs.a10) + (lhs.a02 * rhs.a20) + (lhs.a03 * rhs.a30);
        float res10 = (lhs.a10 * rhs.a00) + (lhs.a11 * rhs.a10) + (lhs.a12 * rhs.a20) + (lhs.a13 * rhs.a30);
        float res20 = (lhs.a20 * rhs.a00) + (lhs.a21 * rhs.a10) + (lhs.a22 * rhs.a20) + (lhs.a23 * rhs.a30);
        float res30 = (lhs.a30 * rhs.a00) + (lhs.a31 * rhs.a10) + (lhs.a32 * rhs.a20) + (lhs.a33 * rhs.a30);

        dst.a00 = res00;
        dst.a10 = res10;
        dst.a20 = res20;
        dst.a30 = res30;

        return dst;
    }

    public static @Nonnull Matrix41 multiply(@Nonnull Matrix44 lhs, @Nonnull Matrix41 rhs) {
        return multiplyExplicit(lhs, rhs, Matrix41.makeZeroed());
    }

    public @Nonnull Matrix41 multiplyWithRhs(@Nonnull Matrix41 rhs) {
        return multiplyExplicit(this, rhs, Matrix41.makeZeroed());
    }

    public static @Nonnull Matrix44 multiplyExplicit(float lhs, @Nonnull Matrix44 rhs, @Nonnull Matrix44 dst) {
        dst.a00 = lhs * rhs.a00;
        dst.a01 = lhs * rhs.a01;
        dst.a02 = lhs * rhs.a02;
        dst.a03 = lhs * rhs.a03;
        dst.a10 = lhs * rhs.a10;
        dst.a11 = lhs * rhs.a11;
        dst.a12 = lhs * rhs.a12;
        dst.a13 = lhs * rhs.a13;
        dst.a20 = lhs * rhs.a20;
        dst.a21 = lhs * rhs.a21;
        dst.a22 = lhs * rhs.a22;
        dst.a23 = lhs * rhs.a23;
        dst.a30 = lhs * rhs.a30;
        dst.a31 = lhs * rhs.a31;
        dst.a32 = lhs * rhs.a32;
        dst.a33 = lhs * rhs.a33;
        return dst;
    }

    public static @Nonnull Matrix44 multiply(float lhs, @Nonnull Matrix44 rhs) {
        return multiplyExplicit(lhs, rhs, new Matrix44());
    }

    public @Nonnull Matrix44 multiplyInplace(float lhs) {
        return multiplyExplicit(lhs, this, this);
    }

    public @Nonnull Matrix44 multiply(float lhs) {
        return multiplyExplicit(lhs, this, new Matrix44());
    }

    // #endregion
    // #region Other operations

    public static float determinant(@Nonnull Matrix44 mat) {
        // A minor for an NxM matrix m is an (N-1)x(M-1) matrix, where m.rows[R] and
        // m.cols[C] are removed. Think of placing a bomb at m[R,C] that explodes
        // infinitely far in each cardinal direction, removing all elements it touches.
        // minor(m, R, C) = ...

        // each det2 calculation dmAmB is grouped into threes, and collectively
        // represent what is needed to calculate det3 stored in d3mA

        // Determinant of a 2x2 matrix:
        // det2(m) = m[0,0] * m[1,1] - m[1,0] * m[0,1]

        // The notation for the following variables is as such:
        // d2mAmB = det2(minor(minor(mat, 0, A), 0, B))

        // d3m0 has columns: 1, 2, 3
        float d2m0m0 = mat.a22 * mat.a33 - mat.a32 * mat.a23; // c: 2, 3
        float d2m0m1 = mat.a21 * mat.a33 - mat.a31 * mat.a23; // c: 1, 3
        float d2m0m2 = mat.a21 * mat.a32 - mat.a31 * mat.a22; // c: 1, 2
        // d3m1 has columns: 0, 2, 3
        float d2m1m0 = mat.a22 * mat.a33 - mat.a32 * mat.a23; // c: 2, 3
        float d2m1m1 = mat.a20 * mat.a33 - mat.a30 * mat.a23; // c: 0, 3
        float d2m1m2 = mat.a20 * mat.a32 - mat.a30 * mat.a22; // c: 0, 2
        // d3m2 has columns: 0, 1, 3
        float d2m2m0 = mat.a21 * mat.a33 - mat.a31 * mat.a23; // c: 1, 3
        float d2m2m1 = mat.a20 * mat.a33 - mat.a30 * mat.a23; // c: 0, 3
        float d2m2m2 = mat.a20 * mat.a31 - mat.a30 * mat.a21; // c: 0, 1
        // d3m3 has columns: 0, 1, 2
        float d2m3m0 = mat.a21 * mat.a32 - mat.a31 * mat.a22; // c: 1, 2
        float d2m3m1 = mat.a20 * mat.a32 - mat.a30 * mat.a22; // c: 0, 2
        float d2m3m2 = mat.a20 * mat.a31 - mat.a30 * mat.a21; // c: 0, 1

        // Determinant of a 3x3 matrix:
        // det3(m) =
        // + m[0,0] * det2(minor(m, 0, 0))
        // - m[0,1] * det2(minor(m, 0, 1))
        // + m[0,2] * det2(minor(m, 0, 2))

        // The notation for the following variables is as such:
        // d3mA = det3(minor(mat, 0, A))

        float d3m0 = (mat.a01 * d2m0m0) - (mat.a02 * d2m0m1) + (mat.a03 * d2m0m2);
        float d3m1 = (mat.a00 * d2m1m0) - (mat.a02 * d2m1m1) + (mat.a03 * d2m1m2);
        float d3m2 = (mat.a00 * d2m2m0) - (mat.a01 * d2m2m1) + (mat.a03 * d2m2m2);
        float d3m3 = (mat.a00 * d2m3m0) - (mat.a01 * d2m3m1) + (mat.a02 * d2m3m2);

        // Determinant of a 4x4 matrix:
        // det4(m) =
        // + m[0,0] * det3(minor(m, 0, 0))
        // - m[0,1] * det3(minor(m, 0, 1))
        // + m[0,2] * det3(minor(m, 0, 2))
        // - m[0,3] * det3(minor(m, 0, 3))

        return (mat.a00 * d3m0) - (mat.a01 * d3m1) + (mat.a02 * d3m2) - (mat.a03 * d3m3);
    }

    public float determinant() {
        return determinant(this);
    }

    public static @Nonnull Matrix44 transposeExplicit(@Nonnull Matrix44 mat, @Nonnull Matrix44 dst) {
        float res00 = mat.a00;
        float res01 = mat.a10;
        float res02 = mat.a20;
        float res03 = mat.a30;
        float res10 = mat.a01;
        float res11 = mat.a11;
        float res12 = mat.a21;
        float res13 = mat.a31;
        float res20 = mat.a02;
        float res21 = mat.a12;
        float res22 = mat.a22;
        float res23 = mat.a32;
        float res30 = mat.a03;
        float res31 = mat.a13;
        float res32 = mat.a23;
        float res33 = mat.a33;

        dst.a00 = res00;
        dst.a01 = res01;
        dst.a02 = res02;
        dst.a03 = res03;
        dst.a10 = res10;
        dst.a11 = res11;
        dst.a12 = res12;
        dst.a13 = res13;
        dst.a20 = res20;
        dst.a21 = res21;
        dst.a22 = res22;
        dst.a23 = res23;
        dst.a30 = res30;
        dst.a31 = res31;
        dst.a32 = res32;
        dst.a33 = res33;
        return dst;
    }

    public static @Nonnull Matrix44 transpose(@Nonnull Matrix44 mat) {
        return transposeExplicit(mat, new Matrix44());
    }

    public @Nonnull Matrix44 transposeInplace() {
        return transposeExplicit(this, this);
    }

    public @Nonnull Matrix44 transpose() {
        return transposeExplicit(this, new Matrix44());
    }

    public static @Nonnull Matrix44 adjugateExplicit(@Nonnull Matrix44 mat, @Nonnull Matrix44 dst) {
        transposeExplicit(mat, dst);
        dst.a01 *= -1f;
        dst.a03 *= -1f;
        dst.a10 *= -1f;
        dst.a12 *= -1f;
        dst.a21 *= -1f;
        dst.a23 *= -1f;
        dst.a30 *= -1f;
        dst.a32 *= -1f;
        return dst;
    }

    public static @Nonnull Matrix44 adjugate(@Nonnull Matrix44 mat) {
        return adjugateExplicit(mat, new Matrix44());
    }

    public @Nonnull Matrix44 adjugateInplace() {
        return adjugateExplicit(this, this);
    }

    public @Nonnull Matrix44 adjugate() {
        return adjugateExplicit(this, new Matrix44());
    }

    public static @Nonnull Option<net.avitech.testbed.util.math.Matrix44> inverseExplicit(@Nonnull Matrix44 mat,
            @Nonnull Matrix44 dst) {
        float det = determinant(mat);

        // Matrix inversion is undefined if the determinant is 0
        if (Math.abs(det) < 1.0e-6f) {
            return Option.none();
        }

        adjugateExplicit(mat, dst);
        multiplyExplicit(1f / det, dst, dst);
        return Option.wrap(dst);
    }

    public static @Nonnull Option<net.avitech.testbed.util.math.Matrix44> inverse(@Nonnull Matrix44 mat) {
        return inverseExplicit(mat, new Matrix44());
    }

    public @Nonnull Option<net.avitech.testbed.util.math.Matrix44> inverseInplace() {
        return inverseExplicit(this, this);
    }

    public @Nonnull Option<net.avitech.testbed.util.math.Matrix44> inverse() {
        return inverseExplicit(this, new Matrix44());
    }

    // #endregion
    // #region Matrix interface implementation

    @Override
    public @Nonnull Dimensions dimensions() {
        return new Dimensions(4, 4);
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
            case 3: switch(col) {
                case 0: return this.a30;
                case 1: return this.a31;
                case 2: return this.a32;
                case 3: return this.a33;
                default: break;
            }
            default: break;
        }
        // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 4x4 matrix", row, col));
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
            case 3: switch(col) {
                case 0: this.a30 = value; return;
                case 1: this.a31 = value; return;
                case 2: this.a32 = value; return;
                case 3: this.a33 = value; return;
                default: break;
            }
            default: break;
        }
        // @formatter:on

        throw new IndexOutOfBoundsException(String.format("%d,%d out of bounds for 4x4 matrix", row, col));
    }
    // #endregion
}
