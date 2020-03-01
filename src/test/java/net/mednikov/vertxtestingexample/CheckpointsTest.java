package net.mednikov.vertxtestingexample;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class CheckpointsTest {

    @Test
    void doTest(Vertx vertx, VertxTestContext context) throws Exception{
        Checkpoint serverCheckpoint = context.checkpoint();

        /* pass 5 as argument to flag checkpoints 5 times */
        Checkpoint requestCheckpoint = context.checkpoint(5);
        Checkpoint responseCheckpoint = context.checkpoint(5);

        //create server
        vertx.createHttpServer().requestHandler(req->{
            req.response().end("Hello server");
            //send response
            responseCheckpoint.flag();
        }).listen(4567, res->{
            if (res.succeeded()){
                //server created
                serverCheckpoint.flag();
            } else {
                //something wrong
                context.failNow(res.cause());
            }
        });

        //send request

        //send 5 times
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:4567/")).build();
        for (int i=0; i<5; i++){
            var res = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = res.body();
            if (body.equalsIgnoreCase("Hello server")){
                requestCheckpoint.flag();
            } else {
                context.failNow(new Exception());
            }
        }
    }
}