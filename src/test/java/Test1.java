import org.junit.jupiter.api.Test;

public class Test1 {
    @Test
    public void test1(){
        String filename="晴天.jpg";
        // public String substring(int beginIndex) []包含beginIndex
        String suffix = filename.substring(filename.lastIndexOf("."));
        System.out.println(suffix);

    }
}
