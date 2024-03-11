package net.avitech.testbed.util.math;

import org.jetbrains.annotations.NotNull;

/**
 * Tools for working with matrices of unknown sizes. As such, the
 * implementations of any method with "Dynamic" in the name will likely be
 * substantially slower than operations without. If your code is relatively
 * performance-sensitive, it is recommended to use the non-dynamic methods on
 * the {@code MatrixNM} types, as the storage of the matrix data only suffers a
 * single indirection, and the non-dynamic methods don't have to go through a
 * double switch chain to access their values dynamically.
 */
public interface Matrix {

    @NotNull
    Dimensions dimensions();

    float getDynamic(int row, int column);

    void setDynamic(int row, int column, float value);

    public static record Dimensions(int rows, int columns) {
        public boolean isSquare() {
            return this.rows() == this.columns();
        }

        public int getIndexRowMajor(int row, int column) {
            return row * this.columns + column;
        }

        public int getIndexColumnMajor(int row, int column) {
            return column * this.rows + row;
        }
    }

    /**
     * Copies all entries from {@code src} to {@code dst}.
     * 
     * @param src The matrix this operation will load from.
     * @param dst The matrix this operation will store to.
     * @throws IllegalArgumentException Thrown if {@code src} and {@code dst} vary
     *                                  in sizes.
     * @return The {@code dst} parameter.
     */
    public static @NotNull Matrix copyDynamicExplicit(@NotNull Matrix src, @NotNull Matrix dst) {
        var srcDimensions = src.dimensions();
        var dstDimensions = dst.dimensions();
        if (srcDimensions.rows() != dstDimensions.rows() || srcDimensions.columns() != dstDimensions.columns()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot copy to dynamic destination matrix of size %dx%d from dynamic source matrix of size %dx%d.",
                    dstDimensions.rows(), dstDimensions.columns(), srcDimensions.rows(), srcDimensions.columns()));
        }

        for (int i = 0; i < srcDimensions.rows(); ++i) {
            for (int j = 0; j < srcDimensions.rows(); ++j) {
                dst.setDynamic(i, j, src.getDynamic(i, j));
            }
        }

        return dst;
    }

    /**
     * Copies {@code src[i,j]} to {@code dst[i,j]} for each (i,j) index that exists
     * in both matrices. This function will never throw if {@code dst.setDynamic}
     * and {@code src.getDynamic} don't throw for in-bounds lookups.
     * 
     * @param src The matrix this operation will load from.
     * @param dst The matrix this operation will store to.
     * @return The {@code dst} parameter.
     */
    public static @NotNull Matrix copyOverlappingDynamicExplicit(@NotNull Matrix src, @NotNull Matrix dst) {
        var srcDimensions = src.dimensions();
        var dstDimensions = dst.dimensions();
        var rowsToCopy = Math.min(srcDimensions.rows(), dstDimensions.rows());
        var columnsToCopy = Math.min(srcDimensions.columns(), dstDimensions.columns());
        for (int i = 0; i < rowsToCopy; ++i) {
            for (int j = 0; j < columnsToCopy; ++j) {
                dst.setDynamic(i, j, src.getDynamic(i, j));
            }
        }

        return dst;
    }

    public static @NotNull Matrix multiplyDynamicExplicit(@NotNull Matrix lhs, @NotNull Matrix rhs,
            @NotNull Matrix dst) {
        var rhsDimensions = rhs.dimensions();
        var lhsDimensions = lhs.dimensions();

        if (rhsDimensions.rows() != lhsDimensions.columns() || rhsDimensions.columns() != lhsDimensions.rows()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot multiply dynamic lhs matrix of size %dx%d with dynamic rhs matrix of size %dx%d.",
                    lhsDimensions.rows(), lhsDimensions.columns(), rhsDimensions.rows(), rhsDimensions.columns()));
        }

        var dstDimensions = dst.dimensions();
        if (dstDimensions.rows() != lhsDimensions.rows() || dstDimensions.columns() != rhsDimensions.columns()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot multiply dynamic lhs matrix of size %dx%d with dynamic rhs matrix of size %dx%d. " +
                            "The expected size of dst was %dx%d, but the actual size of dst was %dx%d. ",
                    lhsDimensions.rows(), lhsDimensions.columns(),
                    rhsDimensions.rows(), rhsDimensions.columns(),
                    lhsDimensions.rows(), rhsDimensions.columns(),
                    dstDimensions.rows(), dstDimensions.columns()));
        }

        for (int dstRow = 0; dstRow < dstDimensions.rows(); ++dstRow) {
            for (int dstColumn = 0; dstColumn < dstDimensions.rows(); ++dstColumn) {
                var value = 0f;
                for (int n = 0; n < lhsDimensions.rows(); ++n) {
                    value += lhs.getDynamic(dstRow, n) * rhs.getDynamic(n, dstColumn);
                }
                dst.setDynamic(dstRow, dstColumn, value);
            }
        }

        return dst;
    }

}
