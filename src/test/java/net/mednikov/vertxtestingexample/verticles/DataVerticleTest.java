package net.mednikov.vertxtestingexample.verticles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.mednikov.vertxtestingexample.models.Person;
import net.mednikov.vertxtestingexample.repositories.PersonRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataVerticleTest {

    @Mock private PersonRepository repository;
    @InjectMocks private DataVerticle verticle;

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context){
        vertx.deployVerticle(verticle, result -> {
            if (result.succeeded()){
                System.out.println("Verticle deployed");
                context.completeNow();
            } else {
                context.failNow(result.cause());
            }
        });
    }

    @Test
    void addPersonTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        final Person mockPerson = new Person("John", "Doe", 28);
        when(repository.add(any(Person.class))).thenReturn(mockPerson);
        JsonObject payload = JsonObject.mapFrom(mockPerson);
        eventBus.request(Routes.DATA_ADD_NEW_PERSON, payload, result->{
            if (result.succeeded()){
                JsonObject reply = JsonObject.mapFrom(result.result().body());
                context.verify(() -> {
                    assertAll(
                        () -> assertEquals("John", reply.getString("firstName")),
                        () -> assertEquals("Doe", reply.getString("lastName")),
                        () -> assertEquals(28, reply.getInteger("age"))
                    );
                });
                context.completeNow();
            } else {
                context.failNow(result.cause());
            }
        });
    }

    @Test
    void findOnePersonSuccessTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        final Person mockPerson = new Person("John", "Doe", 28);
        final String id = "id";
        when(repository.findById(id)).thenReturn(Optional.of(mockPerson));
        JsonObject payload = new JsonObject().put("id", id);
        eventBus.request(Routes.DATA_FIND_ONE, payload, result ->{
            if (result.succeeded()){
                JsonObject reply = JsonObject.mapFrom(result.result().body());
                context.verify(() -> {
                    assertAll(
                        () -> assertEquals("John", reply.getString("firstName")),
                        () -> assertEquals("Doe", reply.getString("lastName")),
                        () -> assertEquals(28, reply.getInteger("age"))
                    );
                });
                context.completeNow();
            } else {
                context.failNow(result.cause());
            }
        });
    }

    @Test
    void findOnePersonFailureTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        final String id = "id";
        when(repository.findById(id)).thenReturn(Optional.empty());
        JsonObject payload = new JsonObject().put("id", id);
        eventBus.request(Routes.DATA_FIND_ONE, payload, result ->{
            if (result.succeeded()){
                context.failNow(new RuntimeException());
            } else {
                context.completeNow();
            }
        });
    }
}