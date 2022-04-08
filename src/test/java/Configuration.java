import java.nio.file.Path;

public class Configuration {
    public static String url = "http://dasec-jenkins.dastc.stee.com:5003/api/2.0/mlflow/experiments/";
    public static String workingDir = Path.of("").toAbsolutePath().toString();
}