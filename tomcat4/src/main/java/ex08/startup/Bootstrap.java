package ex08.startup;

import com.brainysoftware.pyrmont.util.Constant;
import ex08.core.SimpleContextConfig;
import ex08.core.SimpleWrapper;
import org.apache.catalina.*;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;

public final class Bootstrap {
    /**
     * http://localhost:8080/Modern?test=123
     * http://localhost:8080/Primitive
     */
    public static void main(String[] args) {
        System.setProperty("catalina.base", Constant.CATALINA_BASE);

        Connector connector = new HttpConnector();
        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        Context context = new StandardContext();
        // StandardContext's start method adds a default mapper
        context.setPath("/myApp");
        context.setDocBase("webapps/context");

        context.addChild(wrapper1);
        context.addChild(wrapper2);

        // context.addServletMapping(pattern, name);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");
        // add ContextConfig. This listener is important because it configures
        // StandardContext (sets configured to true), otherwise StandardContext
        // won't start
        LifecycleListener listener = new SimpleContextConfig();
        ((Lifecycle) context).addLifecycleListener(listener);

        // here is our loader
        Loader loader = new WebappLoader();
        // associate the loader with the Context
        context.setLoader(loader);

        connector.setContainer(context);

        try {
            connector.initialize();
            ((Lifecycle) connector).start();
            ((Lifecycle) context).start();
            // now we want to know some details about WebappLoader
            WebappClassLoader classLoader = (WebappClassLoader) loader.getClassLoader();
            //System.out.println("Resources' docBase: " + ((ProxyDirContext)classLoader.getResources()).getDocBase());
            String[] repositories = classLoader.findRepositories();
            for (int i = 0; i < repositories.length; i++) {
                System.out.println("  repository: " + repositories[i]);
            }

            // HttpConnector，Tomcat 4里面具体怎么实现的，不知道，在这个应用感觉，好像只监听了一次Http请求，不过，我们不关心细节，只要了解原理就好了
            // 在第三章我们已经了解了HttpConnector原理，以下是逐渐了解各个组件的原理

            // make the application wait until we press a key.
            System.in.read();
            ((Lifecycle) context).stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}