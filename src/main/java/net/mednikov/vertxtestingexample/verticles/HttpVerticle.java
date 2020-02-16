package net.mednikov.vertxtestingexample.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static net.mednikov.vertxtestingexample.verticles.Routes.*;


public class HttpVerticle extends AbstractVerticle {

    private EventBus eventbus;
    
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.eventbus =  vertx.eventBus();

        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create());
        router.get("/person/:id").handler(this::getOnePerson);
        router.post("/person").handler(this::postPerson);

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router);
        server.listen(4567, res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    private void getOnePerson(RoutingContext context){
        String id = context.pathParam("id");
        JsonObject payload = new JsonObject().put("id", id);
        eventbus.request(DATA_FIND_ONE, payload, result ->{
            if (result.succeeded()) {
                JsonObject data = JsonObject.mapFrom(result.result().body());
                context.response().setStatusCode(200).end(data.encode());
            } else {
                context.fail(404);
            }
        });
    }

    private void postPerson(RoutingContext context){
        JsonObject payload = context.getBodyAsJson();
        eventbus.request(DATA_ADD_NEW_PERSON, payload, result -> {
            if (result.succeeded()) {
                JsonObject data = JsonObject.mapFrom(result.result().body());
                context.response().setStatusCode(200).end(data.encode());
            } else {
                context.fail(500);
            }
        });
    }

}