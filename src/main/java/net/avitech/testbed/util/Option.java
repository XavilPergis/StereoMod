package net.avitech.testbed.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

/**
 * @implNote "emptyness" is encoded in the reference, so null is a perfectly
 *           valid value to contain.
 */
public class Option<T> {
    private static final Option<?> NONE = new Option<Object>(new Object());
    private @Nullable T value;

    private Option(T value) {
        this.value = value;
    }

    public boolean isSome() {
        return this != NONE;
    }

    public boolean isNone() {
        return this == NONE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return (Option<T>) NONE;
    }

    public static <T> Option<T> wrap(T value) {
        return new Option<>(value);
    }

    public static <T> Option<T> wrapNullable(T value) {
        return value == null ? none() : new Option<>(value);
    }

    public T unwrap() {
        assert isSome();
        return value;
    }

    public T unwrapOr(T fallback) {
        return isNone() ? fallback : this.value;
    }

    public T unwrapOrElse(Supplier<T> fallback) {
        return isNone() ? fallback.get() : this.value;
    }

    public void ifSome(Consumer<T> consumer) {
        if (isSome())
            consumer.accept(this.value);
    }

    public <U> Option<U> map(Function<T, U> mapper) {
        return isNone() ? none() : new Option<>(mapper.apply(this.value));
    }

    public <U> Option<U> andThen(Function<T, Option<U>> mapper) {
        return isNone() ? none() : mapper.apply(this.value);
    }

    public Option<T> or(Option<T> other) {
        return isNone() ? other : this;
    }

    public Option<T> orElse(Supplier<Option<T>> supplier) {
        return isNone() ? supplier.get() : this;
    }

    public Option<T> filter(Predicate<T> predicate) {
        return !contains(predicate) ? none() : this;
    }

    public boolean contains(Predicate<T> predicate) {
        return !isNone() && predicate.test(this.value);
    }

}
