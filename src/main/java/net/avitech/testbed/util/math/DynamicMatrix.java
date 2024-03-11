package net.avitech.testbed.util.math;

import org.jetbrains.annotations.NotNull;

public class DynamicMatrix implements Matrix {

    private float[] values;
    private Matrix.Dimensions dimensions;

    // Explicitly disabled.
    private DynamicMatrix() {
    }

    private static float[] makeStorageForDimensions(@NotNull Matrix.Dimensions dimensions) {
        float[] values = new float[dimensions.rows() * dimensions.columns()];
        return values;
    }

    public static DynamicMatrix makeZeroedExplicit(@NotNull Matrix.Dimensions dimensions, DynamicMatrix dst) {
        dst.dimensions = dimensions;
        dst.values = makeStorageForDimensions(dimensions);
        return dst;
    }

    public static @NotNull DynamicMatrix makeZeroed(@NotNull Matrix.Dimensions dimensions) {
        return makeZeroedExplicit(dimensions, new DynamicMatrix());
    }

    public static DynamicMatrix makeIdentityExplicit(int size, DynamicMatrix dst) {
        dst.dimensions = new Matrix.Dimensions(size, size);
        dst.values = makeStorageForDimensions(dst.dimensions);
        for (int i = 0; i < size; ++i) {
            dst.values[dst.dimensions.getIndexRowMajor(i, i)] = 1;
        }
        return dst;
    }

    public static @NotNull DynamicMatrix makeIdentity(int size) {
        return makeIdentityExplicit(size, new DynamicMatrix());
    }

    /**
     * Resize a dynamic matrix to {@code dimensions}. The contents of the resized
     * matrix are unspecified, and may be anything.
     * 
     * @param dimensions
     * @return
     */
    public @NotNull DynamicMatrix resizeUndefined(@NotNull Matrix.Dimensions dimensions) {
        if (!dimensions.equals(this.dimensions)) {
            this.dimensions = dimensions;
            this.values = makeStorageForDimensions(dimensions);
        }

        return this;
    }

    public static @NotNull DynamicMatrix copyExplicit(@NotNull DynamicMatrix src, @NotNull DynamicMatrix dst) {
        if (src == dst) {
            return dst;
        }

        // resize dst if needed
        if (!dst.dimensions.equals(src.dimensions)) {
            dst.values = makeStorageForDimensions(src.dimensions);
        }

        for (int i = 0; i < src.values.length; ++i) {
            dst.values[i] = src.values[i];
        }

        return dst;
    }

    public static @NotNull DynamicMatrix copy(@NotNull DynamicMatrix src) {
        return copyExplicit(src, new DynamicMatrix());
    }

    public @NotNull DynamicMatrix copyFromInplace(@NotNull DynamicMatrix src) {
        return copyExplicit(src, this);
    }

    public @NotNull DynamicMatrix copyToInplace(@NotNull DynamicMatrix dst) {
        return copyExplicit(this, dst);
    }

    public static @NotNull DynamicMatrix multiplyExplicit(@NotNull DynamicMatrix lhs, @NotNull DynamicMatrix rhs,
            @NotNull DynamicMatrix dst) {

        // matrix multiplication only works if the number of columns in the lhs is the
        // same as the number of rows in the rhs. This is because you take the dot
        // product of each row in the lhs with each columns in the rhs, which requires
        // the vectors to be the same size.
        assert lhs.dimensions().columns() == rhs.dimensions().rows();

        var newDimensions = new Matrix.Dimensions(lhs.dimensions().rows(), rhs.dimensions().columns());
        var dotSize = lhs.dimensions().columns();

        // TODO: what do we do here? should we silently resize, or should we require
        // this as a precondition?
        assert dst.dimensions().equals(newDimensions);

        // Make sure we're not overwriting values as we go!! There's not really any good
        // way to resize on-the-go here, so we just assert that it doesn't happen!
        assert dst != lhs & dst != rhs;

        for (int dr = 0; dr < newDimensions.rows(); ++dr) {
            for (int dc = 0; dc < newDimensions.columns(); ++dr) {
                // dot(a,b,N) = sum(n in 0..N, a[n] * b[n])
                var dotProduct = 0f;
                for (int i = 0; i < dotSize; ++i) {
                    dotProduct += lhs.get(dr, i) * rhs.get(i, dc);
                }

                // foreach((r, c) in (0..N) * (0..N)) res[r, c] = dot(lhs.rows[r], rhs.cols[c])
                dst.set(dr, dc, dotProduct);
            }
        }

        return dst;
    }

    public static @NotNull DynamicMatrix multiply(@NotNull DynamicMatrix lhs, @NotNull DynamicMatrix rhs) {
        return multiplyExplicit(lhs, rhs, new DynamicMatrix());
    }

    public float get(int row, int column) {
        return this.values[this.dimensions.getIndexRowMajor(row, column)];
    }

    public void set(int row, int column, float value) {
        this.values[this.dimensions.getIndexRowMajor(row, column)] = value;
    }

    @Override
    public Dimensions dimensions() {
        return this.dimensions;
    }

    @Override
    public float getDynamic(int row, int column) {
        return get(row, column);
    }

    @Override
    public void setDynamic(int row, int column, float value) {
        set(row, column, value);
    }

}
