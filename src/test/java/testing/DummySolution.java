package testing;

import org.mschoe.aocutil.AocExecutor;
import org.mschoe.aocutil.Part;
import org.mschoe.aocutil.PartNumber;
import org.mschoe.aocutil.Solution;

import java.util.List;

@Solution(day = 1, year = 2024)
public class DummySolution {

    @Part(PartNumber.ONE)
    public int solveOne(List<String> lines) {
        return lines.size();
    }

    @Part(PartNumber.TWO)
    public int solveTwo(List<String> lines) {
        return lines.size() * 2;
    }

    public static void main(String[] args) {
        new AocExecutor().solve(1, 2024);
    }
}
