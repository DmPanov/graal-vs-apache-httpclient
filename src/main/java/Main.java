import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

// see https://github.com/oracle/graal/issues/715
@SuppressWarnings("unused")
@TargetClass(LogFactory.class)
final class LogFactorySubstituted {
    @Substitute
    protected static LogFactory newFactory(final String factoryClass,
                                           final ClassLoader classLoader,
                                           final ClassLoader contextClassLoader) {
        return new LogFactoryImpl();
    }
}

@SuppressWarnings("unused")
@TargetClass(LogFactoryImpl.class)
final class LogFactoryImplSubstituted {
    @Substitute
    private Log discoverLogImplementation(String logCategory) {
        return new SimpleLog(logCategory);
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            client.execute(new HttpHead("https://www.google.com"), response -> {
                System.out.println(response.getStatusLine().getStatusCode());
                return null;
            });
        }
    }
}