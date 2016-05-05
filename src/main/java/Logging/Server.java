package Logging;


import Configuration.LogServerSettings;
import Utilities.*;
import Configuration.Config;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Robin on 2016-04-07.
 */
public class Server implements Verticle {
    private LogServerSettings settings;
    private TokenFactory tokenFactory;
    private Vertx vertx;
    private Logger logger;

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.settings = Config.instance().getLogSettings();
        this.logger = new DefaultLogger(vertx, settings.getLogserver());
        this.tokenFactory = new TokenFactory(settings.getSecret());
    }

    @Override
    public void start(Future<Void> start) throws Exception {
        createIndex();

        vertx.createHttpServer().websocketHandler(connection -> {

            connection.handler(data -> {
                Token token = (Token) Serializer.unpack(data.toJsonObject().getJsonObject("token"), Token.class);

                if (tokenFactory.verifyToken(token)) {
                    JsonObject logdata = data.toJsonObject();
                    logdata.remove("token");

                    vertx.createHttpClient().post(
                            settings.getElastic().getPort(),
                            settings.getElastic().getRemote(),
                            settings.getElastic().getIndex() + "/all/", response -> {

                                response.handler(event -> {
                                    System.out.println(event.toString());
                                });

                            }).end(logdata.encode());

                    System.out.println(data.toString());
                }

            });
        }).listen(settings.getPort());

        logger.onServerStarted();
        start.complete();
    }

    private void createIndex() {
        vertx.createHttpClient().put(
                settings.getElastic().getPort(),
                settings.getElastic().getRemote(),
                settings.getElastic().getIndex(), connection -> {

                    connection.handler(data -> {
                        System.out.println(data.toString());
                    });
                })
                .end(settings.getElastic().getTemplate().toString());
    }

    @Override
    public void stop(Future<Void> stop) throws Exception {
        logger.onServerStopped();
        stop.complete();
    }
}
