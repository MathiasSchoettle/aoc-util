package org.mschoe.aocutil;

import org.mschoe.aocutil.lib.container.ClassContainer;
import org.mschoe.aocutil.lib.ClassExecutor;
import org.mschoe.aocutil.lib.exception.AocUtilException;
import org.mschoe.aocutil.lib.util.Result;

import java.util.Map;

import static org.mschoe.aocutil.lib.util.TimeUtils.currentDay;
import static org.mschoe.aocutil.lib.util.TimeUtils.currentYear;

public class AocExecutor {

    private final ClassExecutor executor;

    private static final ClassContainer CLASS_CONTAINER;

    static {
        CLASS_CONTAINER = new ClassContainer();
    }

    public AocExecutor() {
        this.executor = new ClassExecutor();
    }

    public void solve() {
        solve(currentDay());
    }

    public void solve(int day) {
        solve(day, currentYear());
    }

    public void solve(int day, int year) {
        var result = getResult(day, year);

        try {
            handleResult(result, day, year);
        } catch (AocUtilException e) {
            // TODO handle internal exceptions
        }
    }

    @SuppressWarnings("unchecked")
    private void handleResult(Result result, int day, int year) {
        switch (result) {
            case Result.Success<?> success -> printSolution((Map<Integer,Object>) success.object(), day, year);
            case Result.Error error -> printError(error.errorMessage());
        }
    }

    private void printSolution(Map<Integer,Object> solutions, int day, int year) {

        for (var entry : solutions.entrySet()) {
            var solution = entry.getValue();
            System.out.printf("""
                Solution for %d.12.%d, Part %d: %s
                """, day, year, entry.getKey(), solution);
        }
    }

    private void printError(String errorMessage) {
        System.err.printf("""
                Caught error: %s
                """, errorMessage);
    }

    private Result getResult(int day, int year) {
        var entry = CLASS_CONTAINER.getClassForEntry(day, year);

        if (entry.isPresent()) {
            return executor.execute(entry.get(), day, year);
        }

        return new Result.Error("No solution found found for %d.12.%d".formatted(day, year));
    }
}
