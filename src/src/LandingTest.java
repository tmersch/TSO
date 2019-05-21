

public class LandingTest {

    public static void main(String[] args) {
        LandingModule tester = new LandingModule(1000, new Vector2D(0, 10000), new Vector2D(0,0));
        ((LandingModule) tester).updateModule(1);
    }
}
