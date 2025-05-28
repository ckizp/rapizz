package fr.rapizz.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T> {
    @Getter
    private final T data;
    private final List<String> errors;
    @Getter
    private final boolean success;

    private Result(T data, List<String> errors, boolean success) {
        this.data = data;
        this.errors = errors;
        this.success = success;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, Collections.emptyList(), true);
    }

    public static <T> Result<T> failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new Result<>(null, errors, false);
    }

    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(null, errors, false);
    }

    public boolean isFailure() {
        return !success;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(data);
        }
    }

    public void ifFailure(Consumer<List<String>> consumer) {
        if (isFailure()) {
            consumer.accept(errors);
        }
    }

    public <R> Result<R> map(Function<T, R> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(data));
        } else {
            return Result.failure(errors);
        }
    }
}
