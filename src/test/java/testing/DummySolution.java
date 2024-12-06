package testing;

import org.mschoe.aocutil.AocExecutor;
import org.mschoe.aocutil.Solution;

import java.util.List;

@Solution(day = 1, year = 2024)
public class DummySolution {

    public int solve(List<String> lines) {
        return lines.size();
    }

    public static void main(String[] args) {
        new AocExecutor().solve(1, 2024);
    }
}
