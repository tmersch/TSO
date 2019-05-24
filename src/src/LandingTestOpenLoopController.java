

public class LandingTestFeedbackController {

    public static void main(String[] args) {
        LandingModuleOpenLoopController tester = new LandingModuleOpenLoopController(1000, new Vector2D(0, 10000), new Vector2D(0,0));
        tester.updateModule(1);
    }
}
