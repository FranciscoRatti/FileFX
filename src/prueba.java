import java.nio.file.Path;

public class prueba {
    public static void main(String[] args) {
        Path file = Path.of(System.getenv("HOME"));
        Path parent = file.getParent();

        System.out.println(file.toString());
        System.out.println(parent.toString());
    }
}
