package Game.Controller;

import Configuration.Gameserver.GameServerSettings;
import Configuration.Gameserver.InstanceSettings;
import Configuration.Gameserver.RealmSettings;
import Logging.Model.DefaultLogger;
import Logging.Model.Logger;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

/**
 * @author Robin Duda
 *         Handles players in a map.
 */
public class Instance implements Verticle {
    private Vertx vertx;
    private InstanceSettings settings;
    private RealmSettings realm;
    private GameServerSettings game;
    private Logger logger;

    public Instance(GameServerSettings game, RealmSettings realm, InstanceSettings settings) {
        this.game = game;
        this.realm = realm;
        this.settings = settings;
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.logger = new DefaultLogger(vertx, game.getLogserver());
    }

    @Override
    public void start(Future<Void> start) throws Exception {
        logger.onInstanceStarted(realm, settings);
        start.complete();
    }

    @Override
    public void stop(Future<Void> stop) throws Exception {
        stop.complete();
    }
}
