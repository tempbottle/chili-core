package Meta.Transport;

import Configuration.FileConfiguration;
import Configuration.Webserver.MetaServerSettings;
import Meta.ClientRequest;
import Meta.MetaProvider;
import Protocols.PacketHandler;
import Protocols.Protocol;
import Utilities.DefaultLogger;
import Utilities.Logger;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author Robin Duda
 */
public class ClientServer implements Verticle {
    private Protocol<PacketHandler<ClientRequest>> protocol;
    private Vertx vertx;
    private Logger logger;
    private MetaServerSettings settings;

    public ClientServer(MetaProvider provider) {
        this.protocol = provider.protocol();
        this.logger = provider.getLogger();
        this.settings = provider.getSettings();
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.settings = FileConfiguration.instance().getMetaServerSettings();
        this.vertx = vertx;
        this.logger = new DefaultLogger(vertx, settings.getLogserver());
    }

    @Override
    public void start(Future<Void> start) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        setLogging(router);
        setResources(router);
        setCatchAll(router);

        vertx.createHttpServer(new HttpServerOptions()
                .setCompressionSupported(settings.getCompress()))
                .requestHandler(router::accept).listen(settings.getPort());

        logger.onServerStarted();
        start.complete();
    }

    private void setLogging(Router router) {
        router.route("/").handler(context -> {
            logger.onPageLoaded(context.request());
            context.next();
        });
    }

    private void setResources(Router router) {
        router.route("/resources/*").handler(StaticHandler.create("resources/")
                .setCachingEnabled(settings.getCache()));

        router.route("/*").handler(StaticHandler.create("website/")
                .setCachingEnabled(settings.getCache()));
    }

    private void setCatchAll(Router router) {
        router.route().handler(context -> {
            context.response()
                    .setStatusCode(404)
                    .putHeader("content-type", "application/json")
                    .end("{\"page\" : 404}");
        });
    }

    @Override
    public void stop(Future<Void> stop) throws Exception {
        logger.onServerStopped();
        stop.complete();
    }
}