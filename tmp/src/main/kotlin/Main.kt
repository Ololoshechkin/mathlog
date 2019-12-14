
import java.io.File


fun main(args: Array<String>) {
    File("output.txt").printWriter().use { output ->
        File("input.txt").useLines { lines ->
            lines.filterNot(String::isEmpty).forEachIndexed { index, line ->
                if (index == 0)
                    processHeader(line.parser().header())
                else
                    output.println("($index) $line ${annotateLine(line, index)}")
            }
        }
    }
}
